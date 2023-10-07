package com.kuit.conet.jpa.domain.member;

import com.kuit.conet.jpa.domain.plan.PlanMember;
import com.kuit.conet.jpa.domain.team.TeamMember;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String name;
    @Column(length = 100)
    private String email;
    @Column(length = 20)
    private String platform;
    @Column(length = 500)
    private String platformId;
    @Column(length = 500)
    private String imgUrl;
    private Integer serviceTerm; // 필수 약관
    private Integer optionTerm;
    private Integer status;

    @OneToMany(mappedBy = "member")// 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계 주인의 반대편
    private List<TeamMember> teams = new ArrayList<>();

    @OneToMany(mappedBy = "member")// 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계 주인의 반대편
    private List<PlanMember> plans = new ArrayList<>();

}
