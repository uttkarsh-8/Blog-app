package com.uttkarsh.blogpost.util;

import com.uttkarsh.blogpost.dto.BlogPostDto;
import com.uttkarsh.blogpost.model.BlogPost;
import org.springframework.stereotype.Component;

@Component
public class BlogPostMapperImpl implements BlogPostMapper {
    @Override
    public BlogPostDto toDto(BlogPost blogPost) {
        if (blogPost == null) {
            return null;
        }

        BlogPostDto blogPostDto = new BlogPostDto();
        blogPostDto.setId(blogPost.getId());
        blogPostDto.setTitle(blogPost.getTitle());
        blogPostDto.setContent(blogPost.getContent());
        blogPostDto.setExistingImages(blogPost.getImages());
        blogPostDto.setAuthorId(blogPost.getAuthor() != null ? blogPost.getAuthor().getId() : null);
        return blogPostDto;
    }

    @Override
    public BlogPost toEntity(BlogPostDto blogPostDto) {
        if (blogPostDto == null) {
            return null;
        }

        BlogPost blogPost = new BlogPost();
        blogPost.setId(blogPostDto.getId());
        blogPost.setTitle(blogPostDto.getTitle());
        blogPost.setContent(blogPostDto.getContent());
        blogPost.setImages(blogPostDto.getExistingImages());
        return blogPost;
    }
}
