package com.nhnacademy.springboot.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@Getter @Setter
@Entity
@Table(name = "project_member")
public class ProjectMember {
    @EmbeddedId
    private Pk pk;

    @MapsId("projectId")
    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @Setter
    @Embeddable
    public static class Pk implements Serializable {
        @Column(name = "account_id")
        private String accountId;

        @Column(name = "project_id")
        private Long projectId;
    }

}
