package com.uttkarsh.blogpost.repository;

import com.uttkarsh.blogpost.model.BlogPost;
import com.uttkarsh.blogpost.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    Page<BlogPost> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<BlogPost> findByAuthor(User author, Pageable pageable);
    Page<BlogPost> findByTitleContainingIgnoreCaseAndAuthor(String title, User author, Pageable pageable);
}