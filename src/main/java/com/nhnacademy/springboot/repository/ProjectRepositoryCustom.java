package com.nhnacademy.springboot.repository;

import com.nhnacademy.springboot.domain.Project;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface ProjectRepositoryCustom {
    List<Project> findProjectsByAccountId(String accountId);
}
