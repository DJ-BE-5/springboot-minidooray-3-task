package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.*;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

public class TaskRepositoryImpl extends QuerydslRepositorySupport implements TaskRepositoryCustom {

    public TaskRepositoryImpl() {
        super(Task.class);
    }


    @Override
    public ProjectMember findProjectMemberByTask_ProjectIdAndAccountId(Task task, String accountId) {
        QTask qTask = QTask.task;
        QProject project = QProject.project;
        QProjectMember projectMember = QProjectMember.projectMember;

        return from(qTask)
                .innerJoin(qTask.project, project)
                .innerJoin(project, projectMember.project)
                .where(projectMember.pk.projectId.eq(project.id), projectMember.pk.accountId.eq(accountId))
                .select(projectMember)
                .fetchOne();
    }
}
