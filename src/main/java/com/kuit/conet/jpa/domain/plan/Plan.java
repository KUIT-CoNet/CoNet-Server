package com.kuit.conet.jpa.domain.plan;

import com.kuit.conet.jpa.domain.team.Team;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Plan {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne // 다대일 양방향 연관 관계 / 연관 관계의 주인
    @JoinColumn(name = "team_id")
    private Team team;

    @Column(name = "plan_name")
    private String name;

    @Temporal(TemporalType.DATE)
    @Column(name = "plan_start_period")
    private Date startPeriod;

    @Temporal(TemporalType.DATE)
    @Column(name = "plan_end_period")
    private Date endPeriod;

    @Temporal(TemporalType.DATE)
    private Date fixedDate;

    @Temporal(TemporalType.TIME)
    private Date fixedTime;

    private Integer status;

    private Integer history;

    @OneToMany(mappedBy = "plan")// 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계 주인의 반대편
    private List<PlanMember> plans = new ArrayList<>();

    @OneToMany(mappedBy = "plan")// 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계 주인의 반대편
    private List<History> histories = new ArrayList<>();
}
