package com.kuit.conet.dao;

import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.Team;
import com.kuit.conet.domain.TeamMember;
import com.kuit.conet.domain.User;
import com.kuit.conet.dto.request.team.ParticipateTeamRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

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

        String returnSql = "select teamId from team where teamName=:teamName and teamImgUrl=:teamImgUrl and inviteCode=:inviteCode and status=1";
        Map<String, String> returnParam = Map.of("teamName", team.getTeamName(),
                "teamImgUrl", team.getTeamImgUrl(),
                "inviteCode", team.getInviteCode());

        return jdbcTemplate.queryForObject(returnSql, returnParam, Long.class);
    }

    public TeamMember saveTeamMember(TeamMember teamMember) {
        String sql = "insert into teammember (teamId, userId) values (:teamId , :userId)";
        Map<String, Object> param = Map.of("teamId", teamMember.getTeamId(),
                "userId", teamMember.getUserId());

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from teammember where teamId=:teamId and userId=:userId and status=1";
        Map<String, Object> returnParam = Map.of("teamId", teamMember.getTeamId(),
                "userId", teamMember.getUserId());

        RowMapper<TeamMember> mapper = new RowMapper<>() {
            public TeamMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                TeamMember member = new TeamMember();
                member.setTeamMemberId(rs.getLong("teamMemberId"));
                member.setTeamId(rs.getLong("teamId"));
                member.setUserId(rs.getLong("userId"));
                member.setStatus(rs.getBoolean("status"));
                return member;
            }
        };

        return jdbcTemplate.queryForObject(returnSql, returnParam, mapper);
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
        String sql = "select EXISTS( SELECT * FROM team WHERE inviteCode=:inviteCode and status=1);";
        Map<String, String> param = Map.of("inviteCode", inviteCode);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Team getTeamFromInviteCode(String inviteCode) {
        String sql = "select * from team where inviteCode=:invitedCode and status=1";
        Map<String, String> param = Map.of("invitedCode", inviteCode);

        RowMapper<Team> mapper = new RowMapper<>() {
            public Team mapRow(ResultSet rs, int rowNum) throws SQLException {
                Team team = new Team();
                team.setTeamId(rs.getLong("teamId"));
                team.setTeamName(rs.getString("teamName"));
                team.setTeamImgUrl(rs.getString("teamImgUrl"));
                team.setInviteCode(rs.getString("inviteCode"));
                team.setCodeGeneratedTime(rs.getTimestamp("codeGeneratedTime"));
                team.setStatus(rs.getBoolean("status"));
                return team;
            }
        };

        Team team = jdbcTemplate.queryForObject(sql, param, mapper);

        return team;
    }

    public Boolean isExistingUser(Long teamId, Long userId) {
            String sql = "select EXISTS(SELECT * FROM teamMember WHERE userId=:userId and teamId=:teamId and status=1);";
            Map<String, Object> param = Map.of("userId", userId,
                    "teamId", teamId);

            return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Boolean isExistTeam(Long teamId) {
        String sql = "select exists(select * from team where teamId=:teamId);";
        Map<String, Object> param = Map.of("teamId", teamId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }
}
