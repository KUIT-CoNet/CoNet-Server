package com.kuit.conet.dao;

import com.kuit.conet.domain.history.History;
import com.kuit.conet.dto.request.history.HistoryRegisterRequest;
import com.kuit.conet.dto.response.history.HistoryRegisterResponse;
import com.kuit.conet.dto.response.history.HistoryResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class HistoryDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public HistoryDao(NamedParameterJdbcTemplate jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }

    public HistoryRegisterResponse registerToHistory(HistoryRegisterRequest registerRequest, String imgUrl) {
        Map<String, Object> planIdParam = Map.of("plan_id", registerRequest.getPlanId());

        String description = registerRequest.getDescription();
        if (description == null) description = "";

        String historyImgUrl = imgUrl;
        if (historyImgUrl == null) historyImgUrl="";

        // history 테이블에 추가
        String sql = "insert into history (plan_id, history_image_url, description) values (:plan_id, :history_image_url, :description)";
        Map<String, Object> param = Map.of("plan_id", registerRequest.getPlanId(),
                "history_image_url", historyImgUrl,
                "description", description);
        jdbcTemplate.update(sql, param);

        // plan 테이블의 history 를 1로 업데이트
        String planUpdateSql = "update plan set history=1 where plan_id=:plan_id and status=2";
        jdbcTemplate.update(planUpdateSql, planIdParam);

        // 등록한 history id 반환
        String returnSql = "select history_id from history where plan_id=:plan_id";

        RowMapper<HistoryRegisterResponse> returnMapper = (rs, rowNum) -> {
            HistoryRegisterResponse response = new HistoryRegisterResponse();
            response.setHistoryId(rs.getLong("history_id"));
            return response;
        };

        return jdbcTemplate.queryForObject(returnSql, planIdParam, returnMapper);
    }

    public void updateHistory(History history, Long planId) {
        String historyImgUrl = history.getHistoryImgUrl();
        if (historyImgUrl == null) historyImgUrl="";
        String description = history.getHistoryDescription();
        if (description == null) description = "";

        String sql = "update history set history_image_url=:img_url, description=:description where plan_id=:plan_id;";
        Map<String, Object> param = Map.of("img_url", historyImgUrl,
                "description", description,
                "plan_id", planId);

        jdbcTemplate.update(sql, param);
    }

    public Boolean isHistoryImageExist(Long planId) {
        String sql = "select history_image_url from history where plan_id=:plan_id";
        Map<String, Object> param = Map.of("plan_id", planId);

        String imgUrl = jdbcTemplate.queryForObject(sql, param, String.class);
        if (!imgUrl.equals("")) return true;
        else return false;
    }

    public String getHistoryImgUrl(Long planId) {
        String sql = "select history_image_url from history where plan_id=:plan_id";
        Map<String, Object> param = Map.of("plan_id", planId);

        return jdbcTemplate.queryForObject(sql, param, String.class);
    }

    public List<HistoryResponse> getHistory(Long teamId) {
        String sql = "select p.plan_id as plan_id, p.plan_name as plan_name, p.fixed_date as date, h.history_image_url as history_image_url, h.description as history_description\n" +
                        "from plan p, history h\n" +
                        "where p.plan_id=h.plan_id and p.status=2 and p.history=1\n" +
                        "  and p.team_id=:team_id";
        Map<String, Object> param = Map.of("team_id", teamId);

        RowMapper<HistoryResponse> mapper = (rs, rowNum) -> {
            HistoryResponse plan = new HistoryResponse();
            String date = rs.getString("date").replace("-", ". ");
            plan.setPlanId(rs.getLong("plan_id"));
            plan.setPlanName(rs.getString("plan_name"));
            plan.setPlanDate(date);
            plan.setHistoryImgUrl(rs.getString("history_image_url"));
            plan.setHistoryDescription(rs.getString("history_description"));
            return plan;
        };

        List<HistoryResponse> plans = jdbcTemplate.query(sql, param, mapper);

        for (HistoryResponse plan : plans) {
            Long planId = plan.getPlanId();
            String memberSql = "select count(*) " +
                                "from plan_member " +
                                "where plan_id=:plan_id and status=1";
            Map<String, Object> memberParam = Map.of("plan_id", planId);
            int memberNum = jdbcTemplate.queryForObject(memberSql, memberParam, Integer.class);
            // TODO: 회원 탈퇴해서 pm.status=0 인 사람과 모임 나가기해서 pm.status=0 인 사람 구분하기
            plan.setPlanMemberNum(memberNum);
        }

        return plans;
    }
}