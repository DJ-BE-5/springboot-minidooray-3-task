package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.ProjectMember;
import com.nhnacademy.springboot.domain.Task;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface TaskRepositoryCustom {
    ProjectMember findProjectMemberByTask_ProjectIdAndAccountId(Task task, String accountId);
    
}
