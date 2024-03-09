package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.Project;
import com.nhnacademy.springboot.domain.QProject;
import com.nhnacademy.springboot.domain.QProjectMember;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class ProjectRepositoryImpl extends QuerydslRepositorySupport implements ProjectRepositoryCustom {
    public ProjectRepositoryImpl() {
        super(Project.class);
    }

    @Override
    public List<Project> findProjectsByAccountId(String accountId) {
        QProject project = QProject.project;
        QProjectMember projectMember = QProjectMember.projectMember;

        return from(projectMember)
                .innerJoin(projectMember.project, project)
                .where(projectMember.pk.accountId.eq(accountId))
                .select(project)
                .fetch();
    }


}
