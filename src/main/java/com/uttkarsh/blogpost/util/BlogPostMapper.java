package com.uttkarsh.blogpost.util;

import com.uttkarsh.blogpost.dto.BlogPostDto;
import com.uttkarsh.blogpost.model.BlogPost;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlogPostMapper {

    @Mapping(target = "authorId", source = "author.id")
    BlogPostDto toDto(BlogPost blogPost);

    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    BlogPost toEntity(BlogPostDto blogPostDto);
}

