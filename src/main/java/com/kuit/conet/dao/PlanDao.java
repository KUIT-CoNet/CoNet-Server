package com.kuit.conet.dao;

import com.kuit.conet.domain.Plan;
import com.kuit.conet.domain.PlanMemberTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
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

}
