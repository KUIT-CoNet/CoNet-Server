package com.kuit.conet.dao;

import com.kuit.conet.domain.Plan;
import com.kuit.conet.domain.PlanMemberTime;
import com.kuit.conet.dto.response.plan.PossibleTimeResponse;
import com.kuit.conet.dto.response.plan.UserTimeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Transactional
public class PlanDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PlanDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long savePlan(Plan plan) {
        String sql = "insert into plan (team_id, plan_name, plan_start_period, plan_end_period) " +
                "values (:team_id, :plan_name, :plan_start_period, :plan_end_period)";
        Map<String, Object> param = Map.of("team_id", plan.getTeamId(),
                "plan_name", plan.getPlanName(),
                "plan_start_period", plan.getPlanStartPeriod(),
                "plan_end_period", plan.getPlanEndPeriod());

        jdbcTemplate.update(sql, param);

        String returnSql = "select last_insert_id()";
        Map<String, Object> returnParam = Map.of();

        return jdbcTemplate.queryForObject(returnSql, returnParam, Long.class);
    }

    public void saveTime(PlanMemberTime planMemberTime) {
        String sql = "insert into plan_member_time (plan_id, user_id, possible_date, possible_time) " +
                "values (:plan_id, :user_id, :possible_date, :possible_time)";
        Map<String, Object> param = Map.of("plan_id", planMemberTime.getPlanId(),
                "user_id", planMemberTime.getUserId(),
                "possible_date", planMemberTime.getPossibleDate(),
                "possible_time", planMemberTime.getPossibleTime());

        jdbcTemplate.update(sql, param);
    }

    public Boolean isExistingUserTime(Long planId, Long userId) {
        String isExistingSql = "select exists(select * from plan_member_time where plan_id=:plan_id and user_id=:user_id)";
        Map<String, Object> isExistingParam = Map.of("plan_id", planId,
                "user_id", userId);

        return jdbcTemplate.queryForObject(isExistingSql, isExistingParam, Boolean.class);
    }

    public UserTimeResponse getUserTime(Long planId, Long userId) {
        String sql = "select * from plan_member_time where plan_id=:plan_id and user_id=:user_id";
        Map<String, Object> param = Map.of("plan_id", planId,
                "user_id", userId);

        RowMapper<PossibleTimeResponse> mapper = ((rs, rowNum) -> {
            PossibleTimeResponse possibleTime = new PossibleTimeResponse();
            possibleTime.setDate(rs.getDate("possible_date"));
            possibleTime.setTime(rs.getString("possible_time"));
            return possibleTime;
        });

        List<PossibleTimeResponse> response = jdbcTemplate.query(sql, param, mapper);

        return new UserTimeResponse(planId, userId, response);
    }

}
