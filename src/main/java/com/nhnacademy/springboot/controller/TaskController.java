package com.nhnacademy.springboot.controller;

import com.nhnacademy.springboot.domain.Project;
import com.nhnacademy.springboot.domain.Task;
import com.nhnacademy.springboot.domain.TaskRequest;
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
public class TaskController {
    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public TaskController(ProjectMemberRepository projectMemberRepository, ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    @GetMapping(value = "/tasks/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable Long taskId,
                                        @RequestHeader(name = "X-USER-ID") String xUserId) {
        if (Objects.isNull(taskRepository.getTaskByTaskId(taskId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found");
        }
        Task task = taskRepository.getTaskByTaskId(taskId);
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping(value = "/projects/{projectId}/tasks")
    public ResponseEntity<List<Task>> getTasks(@PathVariable Long projectId,
                                               @RequestHeader(name = "X-USER-ID") String xUserId) {
        Project project = projectRepository.getProjectByProjectId(projectId);
        if (Objects.isNull(project)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project Not Found");
        }
        if (Objects.isNull(taskRepository.findAllByProject(project))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tasks Not Found");
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(projectId, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        List<Task> tasks = taskRepository.findAllByProject(project);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping(value = "/projects/{projectId}/tasks")
    public ResponseEntity<Task> createTask(@PathVariable Long projectId,
                                           @RequestHeader(name = "X-USER-ID") String xUserId,
                                           @RequestBody @Valid Task task,
                                           BindingResult bindingResult) {
        Project project = projectRepository.getProjectByProjectId(projectId);
        if (Objects.isNull(project)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project Not Found");
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(projectId, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        task.setProject(project);
        task.setMemberId(xUserId);
        return ResponseEntity.ok(taskRepository.save(task));
    }

    @PutMapping(value = "/tasks/{taskId}")
    public ResponseEntity<Task> modifyTask(@PathVariable Long taskId,
                                           @RequestBody TaskRequest taskRequest,
                                           @RequestHeader(name = "X-USER-ID") String xUserId,
                                           BindingResult bindingResult) {
        Task task = taskRepository.getTaskByTaskId(taskId);
        if (Objects.isNull(taskRepository.getTaskByTaskId(taskId))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found");
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        task.setTitle(taskRequest.getTitle());
        task.setContent(taskRequest.getContent());
        task = taskRepository.save(task);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping(value = "/tasks/{taskId}")
    public ResponseEntity<Task> deleteTask(@PathVariable Long taskId,
                                           @RequestHeader(name = "X-USER-ID") String xUserId) {
        Task task = taskRepository.getTaskByTaskId(taskId);
        if (Objects.isNull(task)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task Not Found");
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(task.getProject().getProjectId(), xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        taskRepository.delete(task);
        return ResponseEntity.noContent().build();
    }
}
