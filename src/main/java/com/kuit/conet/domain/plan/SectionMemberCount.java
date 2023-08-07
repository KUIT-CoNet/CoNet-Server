package com.kuit.conet.domain.plan;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class SectionMemberCount {
    private Integer section;
    private List<Integer> memberCount;
}
