package com.kuit.conet.dao;

import com.kuit.conet.domain.FixedPlan;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class HomeDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public HomeDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<String> getPlanOnMonth(Long userId, String searchDate) {
        // 해당 년, 월에 유저가 포함된 모든 모임의 모든 약속 -> fixed_date 만 distinct 로 검색
        // team_member(userId, status) -> plan(teamId, fixed_date, status)

        String sql = "select distinct(p.fixed_date) " +
                "from team_member tm, plan p " +
                "where tm.team_id = p.team_id " +
                "and tm.user_id=:user_id and tm.status=1 " +
                "and p.status=2 and date_format(p.fixed_date,'%Y-%m')=:search_date"; // plan status 확정 : 2
        Map<String, Object> param = Map.of("user_id", userId,
                "search_date", searchDate);

        RowMapper<String> mapper = new SingleColumnRowMapper<>(String.class);

        return jdbcTemplate.query(sql, param, mapper);
    }

    public List<FixedPlan> getPlanOnDay(Long userId, String searchDate) {
        String sql = "select p.fixed_date as fixed_date, p.fixed_time as fixed_time, p.plan_name as plan_name, t.team_name as team_name " +
                "from team_member tm, plan p, team t " +
                "where tm.team_id = p.team_id and p.team_id = t.team_id " +
                "and tm.user_id=:user_id and tm.status=1 and t.status=1 " +
                "and p.status=2 and date_format(p.fixed_date,'%Y-%m-%d')=:search_date"; // plan status 대기 : 1
        Map<String, Object> param = Map.of("user_id", userId,
                "search_date", searchDate);

        RowMapper<FixedPlan> mapper = (rs, rowNum) -> {
            FixedPlan plan = new FixedPlan();
            plan.setDate(rs.getString("fixed_date"));
            String fixedTime = rs.getString("fixed_time");
            int timeEndIndex = fixedTime.length()-3;
            plan.setTime(fixedTime.substring(0, timeEndIndex));
            plan.setPlanName(rs.getString("plan_name"));
            plan.setTeamName(rs.getString("team_name"));
            return plan;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }
}