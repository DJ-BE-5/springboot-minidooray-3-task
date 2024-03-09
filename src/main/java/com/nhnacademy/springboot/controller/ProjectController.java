package com.nhnacademy.springboot.controller;

import com.nhnacademy.springboot.domain.Project;
import com.nhnacademy.springboot.domain.ProjectMember;
import com.nhnacademy.springboot.domain.ProjectMemberRequest;
import com.nhnacademy.springboot.domain.ProjectStateRequest;
import com.nhnacademy.springboot.repository.ProjectMemberRepository;
import com.nhnacademy.springboot.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@RestController
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectController(ProjectRepository projectRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @GetMapping(value = "/projects")
    public ResponseEntity<List<Project>> getProjects(@RequestHeader(name = "X-USER-ID") String xUserId) {
        List<Project> projectList = projectRepository.findProjectsByAccountId(xUserId);
        if (Objects.isNull(projectList)) {
            projectList = new ArrayList<>();
            return ResponseEntity.ok(projectList);
        }
        return ResponseEntity.ok(projectList);
    }


    @GetMapping(value = "/projects/{projectId}")
    public ResponseEntity<Project> getProject(@PathVariable Long projectId,
                                              @RequestHeader(name = "X-USER-ID") String xUserId) {
        Project project = projectRepository.getProjectById(projectId);
        if (!projectRepository.existsById(projectId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project Not Found : " + projectId);
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(projectId, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        return ResponseEntity.ok(project);
    }

    @PostMapping(value = "/projects")
    public ResponseEntity<Project> createProject(@RequestBody @Valid Project project,
                                                 @RequestHeader(name = "X-USER-ID") String xUserId,
                                                 BindingResult bindingResult) {
        project.setAccountId(xUserId);
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed.");
        }
        ProjectMember.Pk pk = new ProjectMember.Pk();
        pk.setProjectId(project.getId());
        pk.setAccountId(xUserId);
        ProjectMember projectMember = new ProjectMember();
        projectMember.setProject(project);
        projectMember.setPk(pk);
        projectRepository.save(project);
        projectMemberRepository.save(projectMember);

        return ResponseEntity.created(URI.create("/projects")).build();
    }

    @PostMapping(value = "/projects/{projectId}/members")
    public ResponseEntity<ProjectMember> registerProjectMember(@PathVariable Long projectId,
                                                               @RequestBody@Valid ProjectMemberRequest projectMemberRequest,
                                                               @RequestHeader(name = "X-USER-ID") String xUserId,
                                                               BindingResult bindingResult) {
        Project project = projectRepository.getProjectById(projectId);
        if (!projectRepository.existsProjectByAccountId(xUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        ProjectMember.Pk pk = new ProjectMember.Pk();
        pk.setProjectId(projectId);
        pk.setAccountId(projectMemberRequest.getMemberId());
        ProjectMember projectMembers = new ProjectMember();
        projectMembers.setPk(pk);
        projectMembers.setProject(project);
        projectMemberRepository.save(projectMembers);

        return ResponseEntity.ok(projectMembers);
    }

    @PutMapping(value = "/projects/{projectId}")
    public ResponseEntity<Project> modifyProjectState(@PathVariable Long projectId,
                                                      @RequestBody @Valid ProjectStateRequest projectStateRequest,
                                                      @RequestHeader(name = "X-USER-ID") String xUserId,
                                                      BindingResult bindingResult) {
        Project project = projectRepository.getProjectById(projectId);
        if (Objects.isNull(project)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Project Not Found : " + projectId);
        }
        if (Objects.isNull(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(projectId, xUserId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        if (bindingResult.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        project.setState(projectStateRequest.getState());
        return ResponseEntity.ok(projectRepository.save(project));
    }
}
