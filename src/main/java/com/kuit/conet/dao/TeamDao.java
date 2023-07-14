package com.kuit.conet.dao;

import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.Team;
import com.kuit.conet.domain.TeamMember;
import com.kuit.conet.domain.User;
import com.kuit.conet.dto.request.team.ParticipateTeamRequest;
import com.kuit.conet.dto.response.team.GetTeamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
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
        String sql = "insert into team (team_name, team_image_url, invite_code, code_generated_time) values (:team_name, :team_image_url, :invite_code, :code_generated_time)";
        Map<String, String> param = Map.of("team_name", team.getTeamName(),
                "team_image_url", team.getTeamImgUrl(),
                "invite_code", team.getInviteCode(),
                "code_generated_time", team.getCodeGeneratedTime().toString());

        jdbcTemplate.update(sql, param);

        String returnSql = "select team_id from team where team_name=:team_name and team_image_url=:team_image_url and invite_code=:invite_code and status=1";
        Map<String, String> returnParam = Map.of("team_name", team.getTeamName(),
                "team_image_url", team.getTeamImgUrl(),
                "invite_code", team.getInviteCode());

        return jdbcTemplate.queryForObject(returnSql, returnParam, Long.class);
    }

    public TeamMember saveTeamMember(TeamMember teamMember) {
        String sql = "insert into team_member (team_id, user_id) values (:team_id , :user_id)";
        Map<String, Object> param = Map.of("team_id", teamMember.getTeamId(),
                "user_id", teamMember.getUserId());

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from team_member where team_id=:team_id and user_id=:user_id and status=1";
        Map<String, Object> returnParam = Map.of("team_id", teamMember.getTeamId(),
                "user_id", teamMember.getUserId());

        RowMapper<TeamMember> mapper = new RowMapper<>() {
            public TeamMember mapRow(ResultSet rs, int rowNum) throws SQLException {
                TeamMember member = new TeamMember();
                member.setTeamMemberId(rs.getLong("team_member_id"));
                member.setTeamId(rs.getLong("team_id"));
                member.setUserId(rs.getLong("user_id"));
                member.setStatus(rs.getBoolean("status"));
                return member;
            }
        };

        return jdbcTemplate.queryForObject(returnSql, returnParam, mapper);
    }

    public String codeUpdate(Long teamId, String newCode, Timestamp regeneratedtime) {
        String sql = "update team set invite_code=:invite_code, code_generated_time=:code_generated_time where team_id=:team_id and status=1";
        Map<String, String> param = Map.of("invite_code", newCode,
                "team_id", teamId.toString(),
                "code_generated_time", regeneratedtime.toString());

        jdbcTemplate.update(sql, param);

        String returnSql = "select invite_code from team where team_id=:team_id and status=1";
        Map<String, String> returnParam = Map.of("team_id", teamId.toString());

        return jdbcTemplate.queryForObject(returnSql, returnParam, String.class);
    }

    public Boolean validateDuplicateCode(String inviteCode) {
        String sql = "select exists(select * from team where invite_code=:invite_code and status=1);";
        Map<String, String> param = Map.of("invite_code", inviteCode);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Team getTeamFromInviteCode(String inviteCode) {
        String sql = "select * from team where invite_code=:invite_code and status=1";
        Map<String, String> param = Map.of("invite_code", inviteCode);

        RowMapper<Team> mapper = new RowMapper<>() {
            public Team mapRow(ResultSet rs, int rowNum) throws SQLException {
                Team team = new Team();
                team.setTeamId(rs.getLong("team_id"));
                team.setTeamName(rs.getString("team_name"));
                team.setTeamImgUrl(rs.getString("team_image_url"));
                team.setInviteCode(rs.getString("invite_code"));
                team.setCodeGeneratedTime(rs.getTimestamp("code_generated_time"));
                team.setStatus(rs.getBoolean("status"));
                return team;
            }
        };

        Team team = jdbcTemplate.queryForObject(sql, param, mapper);

        return team;
    }

    public List<GetTeamResponse> getTeam(Long userId) {
        String sql = "select t.team_name, t.team_image_url " +
                "from team_member as tm join team as t on tm.team_id=t.team_id " +
                "where tm.user_id=:user_id and tm.status=1";
        Map<String, Object> param = Map.of("user_id", userId);

        RowMapper<GetTeamResponse> mapper = new RowMapper<GetTeamResponse>() {
            public GetTeamResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                GetTeamResponse response = new GetTeamResponse();
                response.setTeam_name(rs.getString("team_name"));
                response.setTeam_image_url(rs.getString("team_image_url"));
                return response;
            }
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public Boolean leaveTeam(Long teamId, Long userId) {
        String sql = "update team_member set status=0 where team_id=:team_id and user_id=:user_id";
        Map<String, Object> param = Map.of("user_id", userId,
                "team_id", teamId);

        jdbcTemplate.update(sql, param);

        String returnSql = "select status from team_member where team_id=:team_id and user_id=:user_id";
        Map<String, Object> returnParam = Map.of("user_id", userId,
                "team_id", teamId);

        return jdbcTemplate.queryForObject(returnSql, returnParam, Boolean.class);
    }

    public Boolean deleteTeam(Long teamId) {
        String teamUpdateSql = "update team set status=0 where team_id=:team_id";
        Map<String, Object> teamUpdateParam = Map.of("team_id", teamId);

        jdbcTemplate.update(teamUpdateSql, teamUpdateParam);

        String teamMemberUpdateSql = "update team_member set status=0 where team_id=:team_id";
        Map<String, Object> teamMemberUpdateParam = Map.of("team_id", teamId);

        jdbcTemplate.update(teamMemberUpdateSql, teamMemberUpdateParam);

        String returnSql = "select status from team where team_id=:team_id";
        Map<String, Object> returnParam = Map.of("team_id", teamId);

        return jdbcTemplate.queryForObject(returnSql, returnParam, Boolean.class);
    }

    public Boolean isExistingUser(Long teamId, Long userId) {
            String sql = "select exists(select * from team_member where user_id=:user_id and team_id=:team_id and status=1);";
            Map<String, Object> param = Map.of("user_id", userId,
                    "team_id", teamId);

            return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Boolean isExistTeam(Long teamId) {
        String sql = "select exists(select * from team where team_id=:team_id and status=1);";
        Map<String, Object> param = Map.of("team_id", teamId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }
}
