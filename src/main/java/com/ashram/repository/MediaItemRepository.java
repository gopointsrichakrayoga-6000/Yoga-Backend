package com.ashram.repository;

import com.ashram.entity.MediaItem;
import com.ashram.entity.MediaItemType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MediaItemRepository extends JpaRepository<MediaItem, Long> {
    Page<MediaItem> findByTypeOrderByIdDesc(MediaItemType type, Pageable pageable);
    Page<MediaItem> findByCategoryIdAndTypeOrderByIdDesc(Long categoryId, MediaItemType type, Pageable pageable);
    Page<MediaItem> findByCategoryIdOrderByIdDesc(Long categoryId, Pageable pageable);
    Page<MediaItem> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT COUNT(m) FROM MediaItem m WHERE m.category.id = :categoryId")
    long countByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT COUNT(m) FROM MediaItem m WHERE m.category.id = :categoryId AND m.type = :type")
    long countByCategoryIdAndType(@Param("categoryId") Long categoryId, @Param("type") MediaItemType type);
}
