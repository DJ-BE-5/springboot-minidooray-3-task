package com.nhnacademy.springboot.controller;

import com.nhnacademy.springboot.domain.Comment;
import com.nhnacademy.springboot.domain.Task;
import com.nhnacademy.springboot.repository.CommentRepository;
import com.nhnacademy.springboot.repository.ProjectMemberRepository;
import com.nhnacademy.springboot.repository.ProjectRepository;
import com.nhnacademy.springboot.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
public class CommentController {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;

    public CommentController(ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, TaskRepository taskRepository, CommentRepository commentRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping(value = "/comments/{commentId}")
    public ResponseEntity<Comment> getComment(@PathVariable Long commentId,
                                              @RequestHeader(name = "X-USER-ID") String xUserId) {
        Comment comment = commentRepository.getReferenceById(commentId);
        Task task = taskRepository.getReferenceById(comment.getTask().getId());
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found : " + task.getId());
        }
        if (Objects.isNull(comment)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment Not Found : " + commentId);
        }
        if (Objects.isNull(taskRepository.findProjectMemberByTask_ProjectIdAndAccountId(task, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(commentRepository.getReferenceById(commentId));
    }

    @GetMapping(value = "/tasks/{taskId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long taskId,
                                                     @RequestHeader(name = "X-USER-ID") String xUserId) {
        Task task = taskRepository.getReferenceById(taskId);
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found : " + taskId);
        } else if (Objects.isNull(taskRepository.findProjectMemberByTask_ProjectIdAndAccountId(task, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(commentRepository.findAllByTask(task));
    }

    @PostMapping(value = "/tasks/{taskId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long taskId,
                                                 @RequestHeader(name = "X-USER-ID") String xUserId,
                                                 @RequestBody @Valid Comment comment,
                                                 BindingResult bindingResult) {
        Task task = taskRepository.getReferenceById(taskId);
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found : " + task.getId());
        }
        if (Objects.isNull(taskRepository.findProjectMemberByTask_ProjectIdAndAccountId(task, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(commentRepository.save(comment));
    }

    @PutMapping(value = "/comments/{commentId}")
    public ResponseEntity<Comment> modifyComment(@PathVariable Long commentId,
                                                 @RequestHeader(name = "X-USER-ID") String xUserId,
                                                 @RequestBody @Valid Comment comment,
                                                 BindingResult bindingResult) {
        Comment comments = commentRepository.getReferenceById(commentId);
        if (Objects.isNull(comments)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment Not Found : " + commentId);
        }
        Task task = taskRepository.getReferenceById(comments.getTask().getId());
        if (Objects.isNull(taskRepository.findProjectMemberByTask_ProjectIdAndAccountId(task, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(commentRepository.save(comment));
    }

    @DeleteMapping(value = "/comments/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long commentId,
                                                 @RequestHeader(name = "X-USER-ID")String xUserId) {
        Comment comment = commentRepository.getReferenceById(commentId);
        if (Objects.isNull(comment)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment Not Found : " + commentId);
        }
        Task task = taskRepository.getReferenceById(comment.getTask().getId());
        if (Objects.isNull(taskRepository.findProjectMemberByTask_ProjectIdAndAccountId(task, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }
}
