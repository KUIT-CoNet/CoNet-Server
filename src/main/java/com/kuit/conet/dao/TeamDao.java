package com.kuit.conet.dao;

import com.kuit.conet.domain.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.Map;

@Slf4j
@Repository
public class TeamDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TeamDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long saveTeam(Team team) {
        String sql = "insert into team (teamName, teamImgUrl, inviteCode, codeGeneratedTime) values (:teamName, :teamImgUrl, :inviteCode, :codeGeneratedTime)";
        Map<String, String> param = Map.of("teamName", team.getTeamName(),
                "teamImgUrl", team.getTeamImgUrl(),
                "inviteCode", team.getInviteCode(),
                "codeGeneratedTime", team.getCodeGeneratedTime().toString());

        jdbcTemplate.update(sql, param);

        String returnSql = "select teamId from team where teamName=:teamName and teamImgUrl=:teamImgUrl and inviteCode=:inviteCode";
        Map<String, String> returnParam = Map.of("teamName", team.getTeamName(),
                "teamImgUrl", team.getTeamImgUrl(),
                "inviteCode", team.getInviteCode());

        return jdbcTemplate.queryForObject(returnSql, returnParam, Long.class);
    }



    public Long saveTeamMember(Long teamId, Long userId) {
        String sql = "insert into teammember (teamId, userId) values (:teamId , :userId)";
        Map<String, String> param = Map.of("teamId", teamId.toString(),
                "userId", userId.toString());

        jdbcTemplate.update(sql, param);

        String returnSql = "select teamMemberId from teammember where teamId=:teamId and userId=:userId";
        Map<String, String> returnParam = Map.of("teamId", teamId.toString(),
                "userId", userId.toString());

        return jdbcTemplate.queryForObject(returnSql, returnParam, Long.class);
    }

    public String codeUpdate(Long teamId, String newCode, Timestamp regeneratedtime) {
        String sql = "update team set inviteCode=:inviteCode, codeGeneratedTime=:codeGeneratedTime where teamId=:teamId";
        Map<String, String> param = Map.of("inviteCode", newCode,
                "teamId", teamId.toString(),
                "codeGeneratedTime", regeneratedtime.toString());

        jdbcTemplate.update(sql, param);

        String returnSql = "select inviteCode from team where teamId=:teamId";
        Map<String, String> returnParam = Map.of("teamId", teamId.toString());

        return jdbcTemplate.queryForObject(returnSql, returnParam, String.class);
    }

    public Boolean validateDuplicateCode(String inviteCode) {
        String sql = "select EXISTS( SELECT * FROM team WHERE inviteCode = :inviteCode );";
        Map<String, String> param = Map.of("inviteCode", inviteCode);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Boolean isExistTeam(Long teamId) {
        String sql = "select exists(select * from team where teamId=:teamId);";
        Map<String, Object> param = Map.of("teamId", teamId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }
}
