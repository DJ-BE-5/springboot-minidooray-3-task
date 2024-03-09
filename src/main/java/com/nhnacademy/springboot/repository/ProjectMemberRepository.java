package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.Project;
import com.nhnacademy.springboot.domain.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMember.Pk> {
    ProjectMember findProjectMemberByPk_ProjectIdAndPk_AccountId(Long projectId, String accountId);
    List<ProjectMember> findAllByPk_AccountId(String accountId);
}
