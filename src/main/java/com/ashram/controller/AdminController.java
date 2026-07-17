package com.ashram.controller;

import com.ashram.dto.CategoryDto;
import com.ashram.dto.MediaItemDto;
import com.ashram.dto.MediaUploadRequestDto;
import com.ashram.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final com.ashram.service.MediaCompressionService mediaCompressionService;

    public AdminController(AdminService adminService, com.ashram.service.MediaCompressionService mediaCompressionService) {
        this.adminService = adminService;
        this.mediaCompressionService = mediaCompressionService;
    }

    @PostMapping("/media")
    public ResponseEntity<MediaItemDto> createMedia(@Valid @RequestBody MediaUploadRequestDto request, Principal principal) {
        String uploaderEmail = principal != null ? principal.getName() : "admin@srichakrayoga.org";
        MediaItemDto created = adminService.createMedia(request, uploaderEmail);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/media/{id}")
    public ResponseEntity<MediaItemDto> updateMedia(@PathVariable Long id, @Valid @RequestBody MediaUploadRequestDto request) {
        MediaItemDto updated = adminService.updateMedia(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/media/{id}")
    public ResponseEntity<Void> deleteMedia(@PathVariable Long id) {
        adminService.deleteMedia(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/media/upload-file")
    public ResponseEntity<?> uploadFile(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            java.util.Map<String, Object> result = mediaCompressionService.processAndCompressFile(file);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            java.util.Map<String, String> error = new java.util.HashMap<>();
            error.put("message", "Failed to compress & store uploaded file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto request) {
        CategoryDto created = adminService.createCategory(request.getName(), request.getDescription());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto request) {
        CategoryDto updated = adminService.updateCategory(id, request.getName(), request.getDescription());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
