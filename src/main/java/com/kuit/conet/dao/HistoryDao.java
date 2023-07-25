package com.kuit.conet.dao;

import com.kuit.conet.dto.request.history.HistoryRegisterRequest;
import com.kuit.conet.dto.response.history.HistoryRegisterResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

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

}