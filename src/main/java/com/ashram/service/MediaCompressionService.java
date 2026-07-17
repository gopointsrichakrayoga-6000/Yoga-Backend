package com.ashram.service;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MediaCompressionService {

    private static final Logger logger = LoggerFactory.getLogger(MediaCompressionService.class);

    private static final long MAX_VIDEO_SIZE_BYTES = 300L * 1024 * 1024; // 300 MB hard cap
    private static final long MAX_PHOTO_SIZE_BYTES = 20L * 1024 * 1024;  // 20 MB hard cap

    public Map<String, Object> processAndCompressFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Cannot process empty media file.");
        }

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "media.bin";
        String lowerName = originalFilename.toLowerCase();

        boolean isPhoto = lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") ||
                lowerName.endsWith(".png") || lowerName.endsWith(".webp") ||
                lowerName.endsWith(".bmp") || lowerName.endsWith(".gif") ||
                (file.getContentType() != null && file.getContentType().startsWith("image/"));

        boolean isVideo = lowerName.endsWith(".mp4") || lowerName.endsWith(".mov") ||
                lowerName.endsWith(".webm") || lowerName.endsWith(".avi") ||
                lowerName.endsWith(".mkv") ||
                (file.getContentType() != null && file.getContentType().startsWith("video/"));

        long fileSize = file.getSize();

        // 1. Hard Cap Validation Before Processing
        if (isVideo && fileSize > MAX_VIDEO_SIZE_BYTES) {
            throw new IllegalArgumentException(String.format(
                    "Video file size (%.2f MB) exceeds the maximum allowed cap of 300 MB before compression.",
                    fileSize / (1024.0 * 1024.0)
            ));
        }
        if (isPhoto && fileSize > MAX_PHOTO_SIZE_BYTES) {
            throw new IllegalArgumentException(String.format(
                    "Photo file size (%.2f MB) exceeds the maximum allowed cap of 20 MB before compression.",
                    fileSize / (1024.0 * 1024.0)
            ));
        }

        // 2. Ensure Storage Directory Structure (/uploads/photos/, /uploads/videos/, /uploads/thumbnails/, /uploads/temp/)
        File uploadsRoot = Paths.get(System.getProperty("user.dir"), "uploads").toFile();
        File photosDir = new File(uploadsRoot, "photos");
        File videosDir = new File(uploadsRoot, "videos");
        File thumbnailsDir = new File(uploadsRoot, "thumbnails");
        File tempDir = new File(uploadsRoot, "temp");

        if (!photosDir.exists()) photosDir.mkdirs();
        if (!videosDir.exists()) videosDir.mkdirs();
        if (!thumbnailsDir.exists()) thumbnailsDir.mkdirs();
        if (!tempDir.exists()) tempDir.mkdirs();

        String cleanBaseName = originalFilename.replaceAll("[^a-zA-Z0-9.-]", "_");
        File tempFile = new File(tempDir, UUID.randomUUID().toString() + "_" + cleanBaseName);
        file.transferTo(tempFile);

        Map<String, Object> result = new HashMap<>();
        result.put("originalSizeBytes", fileSize);
        result.put("originalFilename", originalFilename);

        try {
            if (isPhoto) {
                // HD PHOTO COMPRESSION — Two-Tier Output:
                //   1. Main web image (photo.url): 1920px, 0.82 quality — for lightbox/full HD viewing
                //   2. Grid thumbnail (photo.thumbnailUrl): 1000px, 0.78 quality — for sharp gallery cards
                String baseNoExt = cleanBaseName.contains(".")
                        ? cleanBaseName.substring(0, cleanBaseName.lastIndexOf('.'))
                        : cleanBaseName;
                String targetPhotoName = UUID.randomUUID().toString() + "_" + baseNoExt + ".jpg";
                File targetPhoto = new File(photosDir, targetPhotoName);
                File targetPhotoNoExt = new File(photosDir, targetPhotoName.substring(0, targetPhotoName.lastIndexOf('.')));

                logger.info("Starting HD photo compression for: {} (original size: {} KB)", originalFilename, fileSize / 1024);
                
                // Main image: 1920px longest side at 0.82 JPEG quality — HD for lightbox
                Thumbnails.of(tempFile)
                        .size(1920, 1920)
                        .outputQuality(0.82f)
                        .outputFormat("jpg")
                        .toFile(targetPhotoNoExt);

                // Adaptive fallback ONLY for extreme cases (30MB+ raw files producing huge output)
                if (targetPhoto.length() > 1200 * 1024) {
                    logger.info("Pass 1 output ({} KB) exceeds 1.2 MB. Running mild adaptive pass (1920px, 0.70f)...", targetPhoto.length() / 1024);
                    Thumbnails.of(tempFile)
                            .size(1920, 1920)
                            .outputQuality(0.70f)
                            .outputFormat("jpg")
                            .toFile(targetPhotoNoExt);
                }

                // Final safety net — only for extreme synthetic/noise images (30MB+ RAW producing 1.5MB+ JPEG)
                if (targetPhoto.length() > 1500 * 1024) {
                    logger.info("Pass 2 output ({} KB) still large. Running final pass (1600px, 0.60f)...", targetPhoto.length() / 1024);
                    Thumbnails.of(tempFile)
                            .size(1600, 1600)
                            .outputQuality(0.60f)
                            .outputFormat("jpg")
                            .toFile(targetPhotoNoExt);
                }

                // Generate grid thumbnail: 1000px at 0.78 quality — sharp enough for HD gallery cards
                String targetThumbName = "thumb_" + UUID.randomUUID().toString() + "_" + baseNoExt + ".jpg";
                File targetThumb = new File(thumbnailsDir, targetThumbName);
                File targetThumbNoExt = new File(thumbnailsDir, targetThumbName.substring(0, targetThumbName.lastIndexOf('.')));
                Thumbnails.of(tempFile)
                        .size(1000, 1000)
                        .outputQuality(0.78f)
                        .outputFormat("jpg")
                        .toFile(targetThumbNoExt);

                long compressedSize = targetPhoto.length();
                logger.info("Photo HD compression complete: {} KB -> {} KB (Grid thumbnail: {} KB)",
                        fileSize / 1024, compressedSize / 1024, targetThumb.length() / 1024);

                result.put("compressedSizeBytes", compressedSize);
                result.put("url", "http://localhost:8081/uploads/photos/" + targetPhotoName);
                result.put("thumbnailUrl", "http://localhost:8081/uploads/thumbnails/" + targetThumbName);
                result.put("filename", targetPhotoName);
                result.put("mediaType", "PHOTO");

            } else {
                // VIDEO COMPRESSION (via JAVE FFmpeg Wrapper with Size Guard)
                String baseNoExt = cleanBaseName.contains(".")
                        ? cleanBaseName.substring(0, cleanBaseName.lastIndexOf('.'))
                        : cleanBaseName;
                String targetVideoName = UUID.randomUUID().toString() + "_" + baseNoExt + ".mp4";
                File targetVideo = new File(videosDir, targetVideoName);

                logger.info("Starting server-side FFmpeg video compression for: {} (original size: {} MB)",
                        originalFilename, fileSize / (1024 * 1024));

                try {
                    AudioAttributes audio = new AudioAttributes();
                    audio.setCodec("aac");
                    audio.setBitRate(128000); // 128 kbps audio
                    audio.setChannels(2);
                    audio.setSamplingRate(44100);

                    VideoAttributes video = new VideoAttributes();
                    video.setCodec("h264");
                    video.setBitRate(1200000); // 1.2 Mbps (~18 MB per 2 min video, well below 50-80 MB target)
                    video.setFrameRate(30);

                    EncodingAttributes attrs = new EncodingAttributes();
                    attrs.setOutputFormat("mp4");
                    attrs.setAudioAttributes(audio);
                    attrs.setVideoAttributes(video);

                    Encoder encoder = new Encoder();
                    encoder.encode(new MultimediaObject(tempFile), targetVideo, attrs);

                    // Size Guard: If re-encoding inflated an already small/low-bitrate video, keep original
                    if (targetVideo.length() >= fileSize) {
                        logger.info("Re-encoded video size ({} MB) is larger than or equal to original input ({} MB). Using original optimized stream.",
                                targetVideo.length() / (1024 * 1024), fileSize / (1024 * 1024));
                        Files.copy(tempFile.toPath(), targetVideo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } else {
                        logger.info("Video successfully compressed: {} MB -> {} MB",
                                fileSize / (1024 * 1024), targetVideo.length() / (1024 * 1024));
                    }
                    result.put("compressedSizeBytes", targetVideo.length());

                } catch (Exception e) {
                    logger.warn("FFmpeg compression encountered issue or codec unsupported for {}: {}. Falling back to clean copy.",
                            originalFilename, e.getMessage());
                    Files.copy(tempFile.toPath(), targetVideo.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    result.put("compressedSizeBytes", targetVideo.length());
                }

                result.put("url", "http://localhost:8081/uploads/videos/" + targetVideoName);
                result.put("filename", targetVideoName);
                result.put("mediaType", "VIDEO");
            }
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }

        return result;
    }
}
