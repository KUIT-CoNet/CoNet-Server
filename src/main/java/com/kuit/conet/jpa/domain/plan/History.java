package com.kuit.conet.jpa.domain.plan;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class History {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "history_image_url", length = 500)
    private String imgUrl;

    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JoinColumn(name = "plan_id") // 다대일 양방향 연관 관계 / 연관 관계의 주인
    private Plan plan;
}
