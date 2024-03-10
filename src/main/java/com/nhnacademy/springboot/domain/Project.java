package com.nhnacademy.springboot.domain;

import com.nhnacademy.springboot.validator.ProjectState;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "account_id", nullable = false)
    private String adminId;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @NotNull
    @ProjectState
    @Column(name = "state", nullable = false, columnDefinition = "VARCHAR (10) DEFAULT 'active' ")
    private String state;
}
