package com.kuit.conet.jpa.domain.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
