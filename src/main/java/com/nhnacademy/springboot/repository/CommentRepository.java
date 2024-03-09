package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.Comment;
import com.nhnacademy.springboot.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTask(Task task);
}
