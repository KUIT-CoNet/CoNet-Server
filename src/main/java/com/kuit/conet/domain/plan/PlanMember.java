package com.kuit.conet.domain.plan;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanMember {
    private Long id;
    private String name;
    private String image;
}