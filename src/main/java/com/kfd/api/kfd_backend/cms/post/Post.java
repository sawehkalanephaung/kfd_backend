package com.kfd.api.kfd_backend.cms.post;

import com.kfd.api.kfd_backend.cms.category.PostCategory;
import com.kfd.api.kfd_backend.cms.tag.Tag;
import com.kfd.api.kfd_backend.department.Department;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(length = 1024)
    private String title;

    @Column(unique = true, length = 255)
    private String slug;

    @Column(length = 2048)
    private String excerpt;

    @Column
    private String content;

    @Column(name = "featured_image_url", length = 1024)
    private String featuredImageUrl;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "slider_image_ids", columnDefinition = "jsonb")
    private java.util.List<UUID> sliderImageIds;

    // author_id — plain UUID; no @ManyToOne needed until User entity is created
    @Column(name = "author_id")
    private UUID authorId;

    // Many posts belong to one category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private PostCategory category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Many posts can have many tags via the post_tags junction table
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "post_tags",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PostStatus status = PostStatus.DRAFT;

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "last_updated_by")
    private UUID lastUpdatedBy;

    @org.hibernate.annotations.JdbcTypeCode(org.hibernate.type.SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private java.util.Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;
}
