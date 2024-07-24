package com.uttkarsh.blogpost.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uttkarsh.blogpost.dto.BlogPostDto;
import com.uttkarsh.blogpost.model.User;
import com.uttkarsh.blogpost.service.BlogPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Blog Posts", description = "Blog post management APIs")
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;
    private final ObjectMapper objectMapper;

    @Operation(summary = "Get all blog posts", description = "Retrieve a paginated list of all blog posts")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of blog posts")
    @GetMapping
    public ResponseEntity<Page<BlogPostDto>> getAllBlogPosts(
            @Parameter(description = "Pagination information") Pageable pageable) {
        return ResponseEntity.ok(blogPostService.getAllBlogPosts(pageable));
    }

    @Operation(summary = "Get a blog post by ID", description = "Retrieve a specific blog post by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the blog post")
    @ApiResponse(responseCode = "404", description = "Blog post not found")
    @GetMapping("/{id}")
    public ResponseEntity<BlogPostDto> getBlogPostById(
            @Parameter(description = "ID of the blog post to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.getBlogPostById(id));
    }


    @Operation(summary = "Create a new blog post", description = "Create a new blog post with optional images")
    @ApiResponse(responseCode = "201", description = "Blog post created successfully")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BlogPostDto> createBlogPost(
            @Parameter(description = "Blog post data in JSON format") @RequestPart(value = "blogPost", required = false) String blogPostJson,
            @Parameter(description = "Images to upload") @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        BlogPostDto blogPostDto;

        if (blogPostJson != null) {
            blogPostDto = objectMapper.readValue(blogPostJson, BlogPostDto.class);
        } else {
            blogPostDto = new BlogPostDto();
        }

        blogPostDto.setNewImages(images);
        BlogPostDto createdPost = blogPostService.createBlogPost(blogPostDto, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @Operation(summary = "Update a blog post", description = "Update an existing blog post by ID")
    @ApiResponse(responseCode = "200", description = "Blog post updated successfully")
    @ApiResponse(responseCode = "404", description = "Blog post not found")
    @PutMapping(value = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<BlogPostDto> updateBlogPost(
            @Parameter(description = "ID of the blog post to update") @PathVariable Long id,
            @Parameter(description = "Updated blog post data in JSON format") @RequestPart(value = "blogPost", required = false) String blogPostJson,
            @Parameter(description = "New images to upload") @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @Parameter(hidden = true) @AuthenticationPrincipal User currentUser) throws IOException {

        BlogPostDto blogPostDto;
        if (blogPostJson != null) {
            blogPostDto = objectMapper.readValue(blogPostJson, BlogPostDto.class);
        } else {
            blogPostDto = new BlogPostDto();
        }

        blogPostDto.setNewImages(images);
        return ResponseEntity.ok(blogPostService.updateBlogPost(id, blogPostDto, currentUser.getId()));
    }

    @Operation(summary = "Delete a blog post", description = "Delete a blog post by its ID")
    @ApiResponse(responseCode = "204", description = "Blog post deleted successfully")
    @ApiResponse(responseCode = "404", description = "Blog post not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlogPost(
            @Parameter(description = "ID of the blog post to delete") @PathVariable Long id) throws IOException {
        blogPostService.deleteBlogPost(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search blog posts", description = "Search blog posts by title and/or author")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of blog posts")
    @GetMapping("/search")
    public ResponseEntity<Page<BlogPostDto>> searchBlogPosts(
            @Parameter(description = "Title to search for") @RequestParam(required = false) String title,
            @Parameter(description = "Author ID to filter by") @RequestParam(required = false) Long authorId,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return ResponseEntity.ok(blogPostService.searchBlogPosts(title, authorId, pageable));
    }

    @Operation(summary = "Filter blog posts by title", description = "Get a list of blog posts filtered by title")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered list of blog posts")
    @GetMapping("/filter/title")
    public ResponseEntity<Page<BlogPostDto>> filterByTitle(
            @Parameter(description = "Title to filter by") @RequestParam String title,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return ResponseEntity.ok(blogPostService.filterByTitle(title, pageable));
    }

    @Operation(summary = "Filter blog posts by author", description = "Get a list of blog posts filtered by author ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved filtered list of blog posts")
    @GetMapping("/filter/author")
    public ResponseEntity<Page<BlogPostDto>> filterByAuthor(
            @Parameter(description = "Author ID to filter by") @RequestParam Long authorId,
            @Parameter(description = "Pagination information") Pageable pageable) {
        return ResponseEntity.ok(blogPostService.filterByAuthor(authorId, pageable));
    }
}

