package com.uttkarsh.blogpost.service;

import com.uttkarsh.blogpost.dto.BlogPostDto;
import com.uttkarsh.blogpost.exception.ResourceNotFoundException;
import com.uttkarsh.blogpost.model.BlogPost;
import com.uttkarsh.blogpost.model.User;
import com.uttkarsh.blogpost.repository.BlogPostRepository;
import com.uttkarsh.blogpost.repository.UserRepository;
import com.uttkarsh.blogpost.util.BlogPostMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final BlogPostMapper blogPostMapper;
    private final ImageService imageService;

    @Transactional
    public Page<BlogPostDto> getAllBlogPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable).map(blogPostMapper::toDto);
    }

    @Transactional
    public BlogPostDto getBlogPostById(Long id) {
        return blogPostRepository.findById(id)
                .map(blogPostMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));
    }

    @Transactional
    public BlogPostDto createBlogPost(BlogPostDto blogPostDto, Long authorId) throws IOException {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

        BlogPost blogPost = blogPostMapper.toEntity(blogPostDto);
        blogPost.setAuthor(author);

        if (blogPostDto.getNewImages() != null && !blogPostDto.getNewImages().isEmpty()) {
            List<String> imageUrls = imageService.uploadImages(blogPostDto.getNewImages());
            blogPost.setImages(imageUrls);
        }

        BlogPost savedBlogPost = blogPostRepository.save(blogPost);
        return blogPostMapper.toDto(savedBlogPost);
    }


    @Transactional
    public BlogPostDto updateBlogPost(Long id, BlogPostDto blogPostDto) throws IOException {
        BlogPost existingBlogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        existingBlogPost.setTitle(blogPostDto.getTitle());
        existingBlogPost.setContent(blogPostDto.getContent());

        List<String> updatedImages = new ArrayList<>(blogPostDto.getExistingImages());

        // Delete removed images
        for (String imageUrl : existingBlogPost.getImages()) {
            if (!updatedImages.contains(imageUrl)) {
                imageService.deleteImage(imageUrl);
            }
        }

        // Add new images
        if (blogPostDto.getNewImages() != null && !blogPostDto.getNewImages().isEmpty()) {
            List<String> newImageUrls = imageService.uploadImages(blogPostDto.getNewImages());
            updatedImages.addAll(newImageUrls);
        }

        existingBlogPost.setImages(updatedImages);

        BlogPost updatedBlogPost = blogPostRepository.save(existingBlogPost);
        return blogPostMapper.toDto(updatedBlogPost);
    }

    @Transactional
    public void deleteBlogPost(Long id) throws IOException {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Blog post not found with id: " + id));

        // Delete associated images
        for (String imageUrl : blogPost.getImages()) {
            imageService.deleteImage(imageUrl);
        }

        blogPostRepository.deleteById(id);
    }

    @Transactional
    public Page<BlogPostDto> searchBlogPosts(String title, Long authorId, Pageable pageable) {
        if (title != null && authorId != null) {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));
            return blogPostRepository.findByTitleContainingIgnoreCaseAndAuthor(title, author, pageable)
                    .map(blogPostMapper::toDto);
        } else if (title != null) {
            return blogPostRepository.findByTitleContainingIgnoreCase(title, pageable)
                    .map(blogPostMapper::toDto);
        } else if (authorId != null) {
            User author = userRepository.findById(authorId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));
            return blogPostRepository.findByAuthor(author, pageable)
                    .map(blogPostMapper::toDto);
        } else {
            return blogPostRepository.findAll(pageable)
                    .map(blogPostMapper::toDto);
        }
    }

    @Transactional
    public Page<BlogPostDto> filterByTitle(String title, Pageable pageable) {
        return blogPostRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(blogPostMapper::toDto);
    }

    @Transactional
    public Page<BlogPostDto> filterByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));
        return blogPostRepository.findByAuthor(author, pageable)
                .map(blogPostMapper::toDto);
    }
}