package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends ProjectRepositoryCustom, JpaRepository<Project, Long> {
    Project getProjectByProjectId(Long projectId);
    boolean existsProjectByAdminId(String accountId);
}
