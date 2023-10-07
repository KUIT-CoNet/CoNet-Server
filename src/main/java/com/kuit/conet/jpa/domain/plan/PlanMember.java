package com.kuit.conet.jpa.domain.plan;

import com.kuit.conet.jpa.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PlanMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_member_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "plan_id") // 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계의 주인
    private Plan plan;

    @ManyToOne
    @JoinColumn(name = "member_id") // 다대다(다대일, 일대다) 양방향 연관 관계 / 연관 관계의 주인
    private Member member;

}
