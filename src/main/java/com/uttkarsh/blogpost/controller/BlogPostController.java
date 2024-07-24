package com.uttkarsh.blogpost.controller;

import com.uttkarsh.blogpost.dto.BlogPostDto;
import com.uttkarsh.blogpost.model.User;
import com.uttkarsh.blogpost.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;

    @GetMapping
    public ResponseEntity<Page<BlogPostDto>> getAllBlogPosts(Pageable pageable) {
        return ResponseEntity.ok(blogPostService.getAllBlogPosts(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDto> getBlogPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.getBlogPostById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogPostDto> createBlogPost(
            @Valid @RequestPart("blogPost") BlogPostDto blogPostDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal User currentUser) throws IOException {
        blogPostDto.setNewImages(images);
        BlogPostDto createdPost = blogPostService.createBlogPost(blogPostDto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BlogPostDto> updateBlogPost(
            @PathVariable Long id,
            @Valid @RequestPart("blogPost") BlogPostDto blogPostDto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {
        blogPostDto.setNewImages(images);
        return ResponseEntity.ok(blogPostService.updateBlogPost(id, blogPostDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable Long id) throws IOException {
        blogPostService.deleteBlogPost(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<BlogPostDto>> searchBlogPosts(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long authorId,
            Pageable pageable) {
        return ResponseEntity.ok(blogPostService.searchBlogPosts(title, authorId, pageable));
    }

    @GetMapping("/filter/title")
    public ResponseEntity<Page<BlogPostDto>> filterByTitle(
            @RequestParam String title,
            Pageable pageable) {
        return ResponseEntity.ok(blogPostService.filterByTitle(title, pageable));
    }

    @GetMapping("/filter/author")
    public ResponseEntity<Page<BlogPostDto>> filterByAuthor(
            @RequestParam Long authorId,
            Pageable pageable) {
        return ResponseEntity.ok(blogPostService.filterByAuthor(authorId, pageable));
    }
}

