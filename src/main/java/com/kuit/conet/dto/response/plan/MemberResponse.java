package com.kuit.conet.dto.response.plan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberResponse {
    private int time;
    private int section;
    private List<String> memberNames;
    private List<Long> memberIds;
}