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
        Comment comment = commentRepository.getCommentByCommentId(commentId);
        Task task = taskRepository.getTaskByTaskId(comment.getTask().getTaskId());
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found");
        }
        if (Objects.isNull(commentRepository.getCommentByCommentId(commentId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment Not Found");
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(commentRepository.getCommentByCommentId(commentId));
    }

    @GetMapping(value = "/tasks/{taskId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long taskId,
                                                     @RequestHeader(name = "X-USER-ID") String xUserId) {
        Task task = taskRepository.getTaskByTaskId(taskId);
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found");
        } else if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(commentRepository.findAllByTask(task));
    }

    @PostMapping(value = "/tasks/{taskId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long taskId,
                                                 @RequestHeader(name = "X-USER-ID") String xUserId,
                                                 @RequestBody @Valid Comment comment,
                                                 BindingResult bindingResult) {
        Task task = taskRepository.getTaskByTaskId(taskId);
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found");
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        comment.setTask(task);
        return ResponseEntity.ok(commentRepository.save(comment));
    }

    @PutMapping(value = "/comments/{commentId}")
    public ResponseEntity<Comment> modifyComment(@PathVariable Long commentId,
                                                 @RequestHeader(name = "X-USER-ID") String xUserId,
                                                 @RequestBody @Valid Comment comment,
                                                 BindingResult bindingResult) {
        Comment comments = commentRepository.getCommentByCommentId(commentId);
        if (Objects.isNull(comments)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment Not Found");
        }
        Task task = taskRepository.getTaskByTaskId(comments.getTask().getTaskId());
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        comment.setTask(comments.getTask());
        return ResponseEntity.ok(commentRepository.save(comment));
    }

    @DeleteMapping(value = "/comments/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long commentId,
                                                 @RequestHeader(name = "X-USER-ID")String xUserId) {
        Comment comment = commentRepository.getCommentByCommentId(commentId);
        if (Objects.isNull(comment)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment Not Found");
        }
        Task task = taskRepository.getTaskByTaskId(comment.getTask().getTaskId());
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        commentRepository.delete(comment);
        return ResponseEntity.noContent().build();
    }
}
