package com.nhnacademy.springboot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.springboot.domain.Project;
import com.nhnacademy.springboot.domain.ProjectMember;
import com.nhnacademy.springboot.repository.ProjectMemberRepository;
import com.nhnacademy.springboot.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@WebAppConfiguration
public class ProjectControllerTest {
    private MockMvc mockMvc;
    private ProjectRepository projectRepository;
    private ProjectMemberRepository projectMemberRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        projectMemberRepository = mock(ProjectMemberRepository.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProjectController(projectRepository, projectMemberRepository)).build();
    }

    @Test
    @Order(1)
    @DisplayName("프로젝트 생성")
    void createProjectTest() {
        Project project = new Project();
        project.setId(1L);
        project.setTitle("test");
        project.setContent("test");
        project.setAccountId("test");
        project.setState("ACTIVE");
        when(projectRepository.save(project)).thenReturn(project);
        ProjectMember projectMember = new ProjectMember();
        when(projectMemberRepository.save(projectMember)).thenReturn(projectMember);

        try {
            mockMvc.perform(post("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(project))
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isCreated());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("프로젝트 생성 - Bad Request")
    void createProjectTest_BadRequest() {
        Project project = new Project();
        project.setId(1L);
        project.setTitle("test");
        project.setContent("test");
        project.setAccountId("test");
        project.setState("ACTIVE");
        when(projectRepository.save(project)).thenReturn(project);

        try {
            mockMvc.perform(post("/projects")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(project)))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(3)
    @DisplayName("프로젝트 목록 조회")
    void getProjectsTest() {
        List<Project> projectList = new ArrayList<>();
        when(projectRepository.findProjectsByAccountId(anyString())).thenReturn(projectList);

        try {
            mockMvc.perform(get("/projects")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("X-USER-ID", "1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(4)
    @DisplayName("프로젝트 개별 조회")
    void getProjectTest() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(anyLong(), anyString())).thenReturn(projectMember);

        try {
            mockMvc.perform(get("/projects/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(5)
    @DisplayName("프로젝트 개별 조회 - Not Found")
    void getProjectTest_NotFound() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectRepository.existsById(anyLong())).thenReturn(false);
        when(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(anyLong(), anyString())).thenReturn(projectMember);

        try {
            mockMvc.perform(get("/projects/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(6)
    @DisplayName("프로젝트 개별 조회 - Forbidden")
    void getProjectTest_Forbidden() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(null);
        when(projectRepository.existsById(anyLong())).thenReturn(true);
        when(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(anyLong(), anyString())).thenReturn(null);

        try {
            mockMvc.perform(get("/projects/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(7)
    @DisplayName("프로젝트 멤버 등록")
    void registerProjectMemberTest() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectRepository.existsProjectByAccountId(anyString())).thenReturn(true);
        when(projectMemberRepository.save(projectMember)).thenReturn(projectMember);

        try {
            mockMvc.perform(post("/projects/1/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString("1"))
                    .header("X-USER-ID", "1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(8)
    @DisplayName("프로젝트 멤버 등록 - Forbidden")
    void registerProjectMemberTest_Forbidden() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectRepository.existsProjectByAccountId(anyString())).thenReturn(false);
        when(projectMemberRepository.save(projectMember)).thenReturn(projectMember);

        try {
            mockMvc.perform(post("/projects/1/members")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString("1"))
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(9)
    @DisplayName("프로젝트 멤버 등록 - BadRequest")
    void registerProjectMemberTest_BadRequest() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectRepository.existsProjectByAccountId(anyString())).thenReturn(false);
        when(projectMemberRepository.save(projectMember)).thenReturn(projectMember);

        try {
            mockMvc.perform(post("/projects/1/members")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(projectMember))
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(10)
    @DisplayName("프로젝트 상태 수정")
    void modifyProjectStateTest() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(anyLong(), anyString())).thenReturn(projectMember);
        when(projectRepository.save(project)).thenReturn(project);

        try {
            mockMvc.perform(put("/projects/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString("SLEEP"))
                    .header("X-USER-ID", "1"))
                    .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(11)
    @DisplayName("프로젝트 상태 수정 - Not Found")
    void modifyProjectStateTest_NotFound() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(null);
        when(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(anyLong(), anyString())).thenReturn(projectMember);
        when(projectRepository.save(project)).thenReturn(project);

        try {
            mockMvc.perform(put("/projects/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString("SLEEP"))
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isNotFound());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(12)
    @DisplayName("프로젝트 상태 수정 - Forbidden")
    void modifyProjectStateTest_Forbidden() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(anyLong(), anyString())).thenReturn(null);
        when(projectRepository.save(project)).thenReturn(project);

        try {
            mockMvc.perform(put("/projects/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString("SLEEP"))
                            .header("X-USER-ID", "1"))
                    .andExpect(status().isForbidden());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(12)
    @DisplayName("프로젝트 상태 수정 - BadRequest")
    void modifyProjectStateTest_BadRequest() {
        Project project = new Project();
        ProjectMember projectMember = new ProjectMember();
        when(projectRepository.getProjectById(anyLong())).thenReturn(project);
        when(projectMemberRepository.findProjectMemberByPk_ProjectIdAndPk_AccountId(anyLong(), anyString())).thenReturn(null);
        when(projectRepository.save(project)).thenReturn(project);

        try {
            mockMvc.perform(put("/projects/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString("SLEEP")))
                    .andExpect(status().isBadRequest());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
