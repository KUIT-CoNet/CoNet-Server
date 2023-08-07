package com.kuit.conet.dao;

import com.kuit.conet.domain.plan.*;
import com.kuit.conet.domain.plan.PastPlan;
import com.kuit.conet.dto.request.plan.UpdatePlanRequest;
import com.kuit.conet.dto.response.plan.UserPossibleTimeResponse;
import com.kuit.conet.dto.response.plan.UserTimeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
        String sql = "select * from plan_member_time where plan_id=:plan_id and user_id=:user_id order by possible_date";
        Map<String, Object> param = Map.of("plan_id", planId,
                "user_id", userId);

        RowMapper<UserPossibleTimeResponse> mapper = ((rs, rowNum) -> {
            UserPossibleTimeResponse possibleTime = new UserPossibleTimeResponse();
            possibleTime.setDate(rs.getDate("possible_date"));

            String time = rs.getString("possible_time");

            List<Integer> timeIntList = new ArrayList<>();

            if (time.isEmpty()) {
                possibleTime.setTime(timeIntList);
                return possibleTime;
            }

            String[] timeStrList = time.split(",");
            for(String str : timeStrList) {
                timeIntList.add(Integer.parseInt(str.trim()));
            }

            possibleTime.setTime(timeIntList);
            return possibleTime;
        });

        List<UserPossibleTimeResponse> response = jdbcTemplate.query(sql, param, mapper);

        Boolean hasPossibleTime = false;
        for (UserPossibleTimeResponse possibleTime : response) {
            if (!possibleTime.getTime().isEmpty()) {
                hasPossibleTime = true;
            }
        }
        return new UserTimeResponse(planId, userId, true, hasPossibleTime, response);
    }

    public List<MemberPossibleTime> getMemberTime(Long planId, Date planStartPeriod) {
        String sql = "select user_id, possible_time from plan_member_time where plan_id=:plan_id and possible_date=:possible_date order by user_id";
        Map<String, Object> param = Map.of("plan_id", planId,
                "possible_date", planStartPeriod);

        RowMapper<MemberPossibleTime> mapper = ((rs, rowNum) -> {
            MemberPossibleTime possibleTime = new MemberPossibleTime();
            possibleTime.setUserId(rs.getLong("user_id"));
            possibleTime.setPossibleTime(rs.getString("possible_time"));
            return possibleTime;
        });

        return jdbcTemplate.query(sql, param, mapper);
    }

    public Plan getWaitingPlan(Long planId) {
        String sql = "select * from plan where plan_id=:plan_id and status=1";
        Map<String, Object> param = Map.of("plan_id", planId);

        RowMapper<Plan> mapper = ((rs, rowNum) -> {
            Plan plan = new Plan();
            plan.setPlanId(rs.getLong("plan_id"));
            plan.setTeamId(rs.getLong("team_id"));
            plan.setPlanName(rs.getString("plan_name"));
            plan.setPlanStartPeriod(rs.getDate("plan_start_period"));
            plan.setPlanEndPeriod(rs.getDate("plan_end_period"));
            plan.setFixedDate(rs.getDate("fixed_date"));
            plan.setFixedTime(rs.getTime("fixed_time"));
            plan.setStatus(rs.getBoolean("status"));
            return plan;
        });

        return jdbcTemplate.queryForObject(sql, param, mapper);
    }

    public Long getTeamId(Long planId) {
        String sql = "select team_id from plan where plan_id=:plan_id";
        Map<String, Object> param = Map.of("plan_id", planId);

        return jdbcTemplate.queryForObject(sql, param, Long.class);
    }

    public void fixPlan(Long planId, Date fixed_date, Time fixed_time, List<Long> userId) {
        String planSql = "update plan set fixed_date=:fixed_date, fixed_time=:fixed_time, status=2 where plan_id=:plan_id and status=1";
        Map<String, Object> planParam = Map.of("plan_id", planId,
                "fixed_date", fixed_date,
                "fixed_time", fixed_time);

        jdbcTemplate.update(planSql, planParam);

        for(Long userid : userId) {
            String planMemberSql = "insert into plan_member (plan_id, user_id) " +
                    "values (:plan_id, :user_id)";
            Map<String, Object> planMemberParam = Map.of("plan_id", planId,
                    "user_id", userid);

            jdbcTemplate.update(planMemberSql, planMemberParam);
        }
    }

    public Boolean isFixedPlan(Long planId) {
        String sql = "select exists(select * from plan where plan_id=:plan_id and status=2)";
        Map<String, Object> param = Map.of("plan_id", planId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Boolean isWaitingPlan(Long planId) {
        String sql = "select exists(select * from plan where plan_id=:plan_id and status=1)";
        Map<String, Object> param = Map.of("plan_id", planId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public List<String> getPlanInMonth(Long teamId, String searchDate) {
        // 해당 년, 월에 해당 모임의 모든 약속 -> fixed_date 만 distinct 로 검색
        // team_member(userId, status) -> plan(teamId, fixed_date, status)

        String sql = "select distinct(fixed_date) " +
                "from plan " +
                "where team_id=:team_id and status=2 " +
                "and date_format(fixed_date,'%Y-%m')=:search_date"; // plan status 확정 : 2
        Map<String, Object> param = Map.of("team_id", teamId,
                "search_date", searchDate);

        RowMapper<String> mapper = new SingleColumnRowMapper<>(String.class);

        return jdbcTemplate.query(sql, param, mapper);
    }

    /**
     * 모임 내 특정 날짜 확정 약속 조회
     * */
    public List<TeamFixedPlanOnDay> getPlanOnDay(Long teamId, String searchDate) {
        String sql = "select p.plan_id as plan_id, p.fixed_time as fixed_time, p.plan_name as plan_name " +
                "from plan p, team t " +
                "where p.team_id = t.team_id " +
                "and p.team_id=:team_id " +
                "and p.status=2 and date_format(p.fixed_date,'%Y-%m-%d')=:search_date " + // plan status 확정 : 2
                "order by p.fixed_time";
        Map<String, Object> param = Map.of("team_id", teamId,
                "search_date", searchDate);

        RowMapper<TeamFixedPlanOnDay> mapper = (rs, rowNum) -> {
            TeamFixedPlanOnDay plan = new TeamFixedPlanOnDay();
            plan.setPlanId(rs.getLong("plan_id"));
            String fixedTime = rs.getString("fixed_time");
            int timeEndIndex = fixedTime.length()-3;
            plan.setTime(fixedTime.substring(0, timeEndIndex));
            plan.setPlanName(rs.getString("plan_name"));
            return plan;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public List<WaitingPlan> getWaitingPlanInTeam(Long teamId) {
        // 해당 모임의 p.team_id = ?
        // 모든 대기 중인 약속 중에서 p.status=1
        // 시작 날짜가 오늘 이후 plan_start_period >= current_date();

        String sql = "select p.plan_id as plan_id, p.plan_start_period as start_date, p.plan_end_period as end_date, p.plan_name as plan_name " +
                        "from plan p, team t " +
                        "where p.team_id = t.team_id " +
                        "and p.team_id=:team_id and p.status=1 " + // plan status 대기 : 1
                        "  and p.plan_start_period >= current_date() " +
                        "order by p.plan_start_period";
        Map<String, Object> param = Map.of("team_id", teamId);

        RowMapper<WaitingPlan> mapper = (rs, rowNum) -> {
            WaitingPlan plan = new WaitingPlan();
            String startDate = rs.getString("start_date").replace("-", ". ");
            String endDate = rs.getString("end_date").replace("-", ". ");

            plan.setPlanId(rs.getLong("plan_id"));
            plan.setStartDate(startDate);
            plan.setEndDate(endDate);
            plan.setPlanName(rs.getString("plan_name"));
            return plan;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public PlanDetail getPlanDetail(Long planId, Boolean isRegisteredToHistory) {
        String sql = null;
        if (!isRegisteredToHistory) {
            sql = "select plan_id, plan_name, fixed_date as date, fixed_time as time " +
                    "from plan " +
                    "where plan_id=:plan_id and status=2 and history=0";
        } else {
            sql = "select p.plan_id as plan_id, p.plan_name as plan_name, p.fixed_date as date, p.fixed_time as time, h.history_image_url as history_image_url, h.description as history_description " +
                    "from plan p, history h " +
                    "where p.plan_id=h.plan_id " +
                    "  and p.plan_id=:plan_id and p.status=2 and p.history=1;";
        }

        Map<String, Object> param = Map.of("plan_id", planId);

        RowMapper<PlanDetail> mapper = (rs, rowNum) -> {
            PlanDetail detail = new PlanDetail();
            detail.setPlanId(rs.getLong("plan_id"));
            detail.setPlanName(rs.getString("plan_name"));
            String date = rs.getString("date").replace("-", ". ");
            detail.setDate(date);
            String fixedTime = rs.getString("time");
            int timeEndIndex = fixedTime.length()-3;
            detail.setTime(fixedTime.substring(0, timeEndIndex));
            if (isRegisteredToHistory) {
                detail.setIsRegisteredToHistory(true);

                String imgUrl = rs.getString("history_image_url");
                if (imgUrl.equals("")) imgUrl = null;
                detail.setHistoryImgUrl(imgUrl);

                String description = rs.getString("history_description");
                if (description.equals("")) description = null;
                detail.setHistoryDescription(description);
            } else {
                detail.setIsRegisteredToHistory(false);
                detail.setHistoryImgUrl(null);
                detail.setHistoryDescription(null);
            }
            return detail;
        };

        PlanDetail detail = jdbcTemplate.queryForObject(sql, param, mapper);

        detail.setMembers(getPlanMember(planId));

        return detail;
    }

    // 약속 상세 정보 조회의 참여자 정
    private List<PlanMember> getPlanMember(Long planId) {
        String sql = "select u.user_id as id, u.name as name, u.img_url as img_url " +
                "from plan_member pm, user u " +
                "where pm.user_id=u.user_id " +
                "  and pm.plan_id=:plan_id"; // user status 필터링 안 하는 이유: 탈퇴한 유저도 이름이 서치되어야 함
        Map<String, Object> param = Map.of("plan_id", planId);

        RowMapper<PlanMember> mapper = (rs, rowNum) -> {
            PlanMember member = new PlanMember();
            member.setId(rs.getLong("id"));
            member.setName(rs.getString("name"));
            member.setImage(rs.getString("img_url"));
            return member;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public Boolean isRegisteredToHistory(Long planId) {
        String sqlPlan = "select exists(select * from plan where plan_id=:plan_id and status=2 and history=1)";
        String sqlHistory = "select exists(select * from history where plan_id=:plan_id)";
        Map<String, Object> param = Map.of("plan_id", planId);

        Boolean isPlanHistoryTrue = jdbcTemplate.queryForObject(sqlPlan, param, Boolean.class);
        Boolean historyHasPlanId = jdbcTemplate.queryForObject(sqlHistory, param, Boolean.class);

        return (Boolean.TRUE.equals(isPlanHistoryTrue) | Boolean.TRUE.equals(historyHasPlanId));
    }

    public Boolean isPastPlan(Long planId) {
        String sql = "select if( " +
                "    (select exists(select * " +
                "                    from plan " +
                "                    where plan_id=:plan_id and (fixed_date < current_date() or (fixed_date = current_date() and fixed_time < current_time())))) = 1 " +
                "           , 1, 0)";
        Map<String, Object> param = Map.of("plan_id", planId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public void deletePlan(Long planId, Boolean isFixedPlan) {
        Map<String, Object> param = Map.of("plan_id", planId);

        if (isFixedPlan) {
            // plan_member 삭제
            String planMemberSql = "delete from plan_member where plan_id=:plan_id";
            jdbcTemplate.update(planMemberSql, param);
        }

        if (!isFixedPlan) {
            // plan_member_time 삭제
            String planMemberTimeSql = "delete from plan_member_time where plan_id=:plan_id";
            jdbcTemplate.update(planMemberTimeSql, param);
        }

        // plan 삭제
        String planSql = "delete from plan where plan_id=:plan_id";
        jdbcTemplate.update(planSql, param);
    }

    public void updateWaitingPlan(Long planId, String planName) {
        String sql = "update plan set plan_name=:plan_name where plan_id=:plan_id and status=1";
        Map<String, Object> param = Map.of("plan_id", planId,
                "plan_name", planName);

        jdbcTemplate.update(sql, param);
    }

    public void updateFixedPlan(UpdatePlanRequest request) {
        Long planId = request.getPlanId();

        // 약속 이름, 날짜/시간 update
        String sql = "update plan set plan_name=:plan_name, fixed_date=:fixed_date, fixed_time=:fixed_time where plan_id=:plan_id and status=2";
        Map<String, Object> param = Map.of("plan_id", planId,
                "plan_name", request.getPlanName(),
                "fixed_date", request.getDate(),
                "fixed_time", request.getTime());

        jdbcTemplate.update(sql, param);

        // 구성원 update
        deleteMember(planId);

        if (request.getMembers() != null) {
            updateMember(planId, request.getMembers());
        }
    }

    private void updateMember(Long planId, List<Long> members) {
        for (Long memberId : members) {
            String sql = "insert into plan_member (plan_id, user_id) values (:plan_id, :user_id)";
            Map<String, Object> param = Map.of("plan_id", planId,
                    "user_id", memberId);

            jdbcTemplate.update(sql, param);
        }
    }

    private void deleteMember(Long planId) {
        String sql = "delete from plan_member where plan_id=:plan_id";
        Map<String, Object> param = Map.of("plan_id", planId);

        jdbcTemplate.update(sql, param);
    }

    public List<PastPlan> getPastPlan(Long teamId) {
        String sql = "select plan_id, fixed_date, fixed_time, plan_name, history " +
                        "from plan " +
                        "where team_id=:team_id and status=2 " +
                        "and (fixed_date < current_date() or (fixed_date = current_date() and fixed_time < current_time()))";
        Map<String, Object> param = Map.of("team_id", teamId);

        RowMapper<PastPlan> mapper = (rs, rowNum) -> {
            PastPlan plan = new PastPlan();
            String date = rs.getString("fixed_date").replace("-", ". ");
            String time = rs.getString("fixed_time");
            int timeEndIndex = time.length()-3;

            plan.setPlanId(rs.getLong("plan_id"));
            plan.setDate(date);
            plan.setTime(time.substring(0, timeEndIndex));
            plan.setPlanName(rs.getString("plan_name"));
            plan.setIsRegisteredToHistory(rs.getBoolean("history"));
            return plan;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public List<SideMenuFixedPlan> getFixedPlan(Long teamId) {
        String sql = "select plan_id, fixed_date, fixed_time, plan_name, history " +
                        "from plan " +
                        "where team_id=:team_id and status=2 " +
                        "  and (fixed_date > current_date() or (fixed_date = current_date() and fixed_time > current_time())) " +
                        "order by fixed_date, fixed_time";
        Map<String, Object> param = Map.of("team_id", teamId);

        RowMapper<SideMenuFixedPlan> mapper = (rs, rowNum) -> {
            SideMenuFixedPlan plan = new SideMenuFixedPlan();
            LocalDate fixedDate = LocalDate.parse(rs.getString("fixed_date"));
            LocalDate now = LocalDate.now();
            Long dDay = ChronoUnit.DAYS.between(now, fixedDate);

            String date = fixedDate.toString().replace("-", ". ");
            String time = rs.getString("fixed_time");
            int timeEndIndex = time.length()-3;

            plan.setPlanId(rs.getLong("plan_id"));
            plan.setDate(date);
            plan.setTime(time.substring(0, timeEndIndex));
            plan.setDDay(dDay);
            plan.setPlanName(rs.getString("plan_name"));
            return plan;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public List<PastPlan> getNotRegisteredToHistoryPlan(Long teamId) {
        String sql = "select plan_id, fixed_date, fixed_time, plan_name, history " +
                "from plan " +
                "where team_id=:team_id and status=2 and history=0 " +
                "and (fixed_date < current_date() or (fixed_date = current_date() and fixed_time <= current_time()))";
        Map<String, Object> param = Map.of("team_id", teamId);

        RowMapper<PastPlan> mapper = (rs, rowNum) -> {
            PastPlan plan = new PastPlan();
            String date = rs.getString("fixed_date").replace("-", ". ");
            String time = rs.getString("fixed_time");
            int timeEndIndex = time.length()-3;

            plan.setPlanId(rs.getLong("plan_id"));
            plan.setDate(date);
            plan.setTime(time.substring(0, timeEndIndex));
            plan.setPlanName(rs.getString("plan_name"));
            plan.setIsRegisteredToHistory(rs.getBoolean("history"));
            return plan;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public void deletePossibleDate(PlanMemberTime planMemberTime) {
        String sql = "delete from plan_member_time where plan_id=:plan_id and user_id=:user_id and possible_date=:possible_date";
        Map<String, Object> param = Map.of("plan_id", planMemberTime.getPlanId(),
                "user_id", planMemberTime.getUserId(),
                "possible_date", planMemberTime.getPossibleDate());

        jdbcTemplate.update(sql, param);
    }

    public void setHistoryInactive(Long planId) {
        String sql = "update plan set history=0 where plan_id=:plan_id and status=2 and history=1";
        Map<String, Object> param = Map.of("plan_id", planId);
        jdbcTemplate.update(sql, param);
    }

    public void deletePlanMemberTime(Long planId) {
        String sql = "delete from plan_member_time where plan_id=:plan_id";
        Map<String, Object> param = Map.of("plan_id", planId);
        jdbcTemplate.update(sql, param);
    }

    public Boolean isRegisteredToPlanMemberTime(Long userId, Long planId) {
        String sql = "select exists(select * from plan_member_time where user_id=:user_id and plan_id=:plan_id)";
        Map<String, Object> param = Map.of("plan_id", planId,
                "user_id", userId);
        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }
}