package com.kuit.conet.domain.plan;

import lombok.*;

import java.sql.Date;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
public class PossibleDateTime {
    private Date date;
    private List<Integer> time;
}
