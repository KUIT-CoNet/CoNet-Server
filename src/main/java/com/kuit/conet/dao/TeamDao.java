package com.kuit.conet.dao;

import com.kuit.conet.domain.team.Team;
import com.kuit.conet.domain.team.TeamMember;
import com.kuit.conet.domain.user.User;
import com.kuit.conet.dto.response.StorageImgResponse;
import com.kuit.conet.dto.response.team.GetTeamMemberResponse;
import com.kuit.conet.dto.response.team.GetTeamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class TeamDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TeamDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Long saveTeam(Team team) {
        String sql = "insert into team (team_name, invite_code, team_image_url, code_generated_time) values (:team_name, :invite_code, :team_image_url, :code_generated_time)";
        Map<String, String> param = Map.of("team_name", team.getTeamName(),
                "invite_code", team.getInviteCode(),
                "team_image_url", team.getTeamImgUrl(),
                "code_generated_time", team.getCodeGeneratedTime().toString());

        jdbcTemplate.update(sql, param);

        String returnSql = "select team_id from team where team_name=:team_name and invite_code=:invite_code";
        Map<String, String> returnParam = Map.of("team_name", team.getTeamName(),
                "invite_code", team.getInviteCode());

        return jdbcTemplate.queryForObject(returnSql, returnParam, Long.class);
    }

    public TeamMember saveTeamMember(TeamMember teamMember) {
        String sql = "insert into team_member (team_id, user_id) values (:team_id , :user_id)";
        Map<String, Object> param = Map.of("team_id", teamMember.getTeamId(),
                "user_id", teamMember.getUserId());

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from team_member where team_id=:team_id and user_id=:user_id";
        Map<String, Object> returnParam = Map.of("team_id", teamMember.getTeamId(),
                "user_id", teamMember.getUserId());

        RowMapper<TeamMember> mapper = (rs, rowNum) -> {
            TeamMember member = new TeamMember();
            member.setTeamMemberId(rs.getLong("team_member_id"));
            member.setTeamId(rs.getLong("team_id"));
            member.setUserId(rs.getLong("user_id"));
            return member;
        };

        return jdbcTemplate.queryForObject(returnSql, returnParam, mapper);
    }

    public String codeUpdate(Long teamId, String newCode, Timestamp regeneratedtime) {
        String sql = "update team set invite_code=:invite_code, code_generated_time=:code_generated_time where team_id=:team_id";
        Map<String, String> param = Map.of("invite_code", newCode,
                "team_id", teamId.toString(),
                "code_generated_time", regeneratedtime.toString());

        jdbcTemplate.update(sql, param);

        String returnSql = "select invite_code from team where team_id=:team_id";
        Map<String, String> returnParam = Map.of("team_id", teamId.toString());

        return jdbcTemplate.queryForObject(returnSql, returnParam, String.class);
    }

    public Boolean validateDuplicateCode(String inviteCode) {
        String sql = "select exists(select * from team where invite_code=:invite_code);";
        Map<String, String> param = Map.of("invite_code", inviteCode);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Team getTeamFromInviteCode(String inviteCode) {
        String sql = "select * from team where invite_code=:invite_code";
        Map<String, String> param = Map.of("invite_code", inviteCode);

        RowMapper<Team> mapper = (rs, rowNum) -> {
            Team team = new Team();
            team.setTeamId(rs.getLong("team_id"));
            team.setTeamName(rs.getString("team_name"));
            team.setTeamImgUrl(rs.getString("team_image_url"));
            team.setInviteCode(rs.getString("invite_code"));
            team.setCodeGeneratedTime(rs.getTimestamp("code_generated_time"));
            return team;
        };

        Team team = jdbcTemplate.queryForObject(sql, param, mapper);

        return team;
    }

    public List<Team> getTeam(Long userId) {
        String sql = "select t.team_id, t.team_name, t.team_image_url, t.created_at " +
                "from team_member as tm join team as t on tm.team_id=t.team_id " +
                "where tm.user_id=:user_id order by tm.team_id desc";
        Map<String, Object> param = Map.of("user_id", userId);

        RowMapper<Team> mapper = (rs, rowNum) -> {
            Team team = new Team();
            team.setTeamId(rs.getLong("team_id"));
            team.setTeamName(rs.getString("team_name"));
            team.setTeamImgUrl(rs.getString("team_image_url"));
            team.setCodeGeneratedTime(rs.getTimestamp("created_at"));
            return team;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }

    public void leaveTeam(Long teamId, Long userId) {
        // plan_member_time 삭제
        String planMemberTimeSql = "delete pmt " +
                "from plan_member_time pmt left join plan p on pmt.plan_id=p.plan_id " +
                "where p.team_id=:team_id and pmt.user_id=:user_id";
        Map<String, Object> param = Map.of("user_id", userId,
                "team_id", teamId);
        jdbcTemplate.update(planMemberTimeSql, param);

        // plan_member 삭제
        String planMemberSql = "delete pm " +
                "from plan_member pm left join plan p on pm.plan_id=p.plan_id " +
                "where p.team_id=:team_id and pm.user_id=:user_id";
        jdbcTemplate.update(planMemberSql, param);

        // team_member 삭제
        String teamMemberSql = "delete from team_member where team_id=:team_id and user_id=:user_id";
        jdbcTemplate.update(teamMemberSql, param);
    }

    public void deleteTeam(Long teamId) {
        // history s3 사진 삭제

        // history 삭제
        String historySql = "delete h " +
                "from history h left join plan p on h.plan_id=p.plan_id " +
                "where p.team_id=:team_id and p.status=2 and p.history=1";
        Map<String, Object> param = Map.of("team_id", teamId);
        jdbcTemplate.update(historySql, param);

        //plan_member_time 삭제
        String planMemberTimeSql = "delete pmt " +
                "from plan_member_time pmt left join plan p on pmt.plan_id=p.plan_id " +
                "where p.team_id=:team_id";
        jdbcTemplate.update(planMemberTimeSql, param);

        // plan_member 삭제
        String planMemberSql = "delete pm " +
                "from plan_member pm left join plan p on pm.plan_id=p.plan_id " +
                "where p.team_id=:team_id";
        jdbcTemplate.update(planMemberSql, param);

        // plan 삭제
        String planSql = "delete p " +
                "from plan p left join team t on p.team_id=t.team_id " +
                "where p.team_id=:team_id";
        jdbcTemplate.update(planSql, param);

        // team_member 삭제
        String teamMemberUpdateSql = "delete from team_member where team_id=:team_id";
        jdbcTemplate.update(teamMemberUpdateSql, param);

        // team 삭제
        String teamUpdateSql = "delete from team where team_id=:team_id";
        jdbcTemplate.update(teamUpdateSql, param);
    }

    public Boolean isExistingUser(Long teamId, Long userId) {
            String sql = "select exists(select * from team_member where user_id=:user_id and team_id=:team_id);";
            Map<String, Object> param = Map.of("user_id", userId,
                    "team_id", teamId);

            return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public Boolean isExistTeam(Long teamId) {
        String sql = "select exists(select * from team where team_id=:team_id);";
        Map<String, Object> param = Map.of("team_id", teamId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public StorageImgResponse updateImg(Long teamId, String imgUrl) {
        String sql = "update team set team_image_url=:team_image_url where team_id=:team_id";
        Map<String, Object> param = Map.of("team_image_url", imgUrl,
                "team_id", teamId);

        jdbcTemplate.update(sql, param);

        String returnSql = "select team_name, team_image_url from team where team_id=:team_id";
        Map<String, Object> returnParam = Map.of("team_id", teamId);

        RowMapper<StorageImgResponse> returnMapper = (rs, rowNum) -> {
            StorageImgResponse storageImgResponse = new StorageImgResponse();
            storageImgResponse.setName(rs.getString("team_name"));
            storageImgResponse.setImgUrl(rs.getString("team_image_url"));
            return storageImgResponse;
        };

        return jdbcTemplate.queryForObject(returnSql, returnParam, returnMapper);
    }

    public void updateName(Long teamId, String teamName) {
        String sql = "update team set team_name=:team_name where team_id=:team_id";
        Map<String, Object> param = Map.of("team_name", teamName,
                "team_id", teamId);

        jdbcTemplate.update(sql, param);
    }

    public Long getTeamMemberCount(Long teamId) {
        String sql = "select count(*) from team_member where team_id=:team_id";
        Map<String, Object> param = Map.of("team_id", teamId);

        return jdbcTemplate.queryForObject(sql, param, Long.class);
    }

    public String getTeamImgUrl(Long teamId) {
        String sql = "select team_image_url from team where team_id=:team_id";
        Map<String, Object> param = Map.of("team_id", teamId);

        return jdbcTemplate.queryForObject(sql, param, String.class);
    }

    public Timestamp getCreatedTime(Long teamId) {
        String sql = "select created_at from team where team_id=:team_id";
        Map<String, Object> param = Map.of("team_id", teamId);

        return jdbcTemplate.queryForObject(sql, param, Timestamp.class);
    }

    public Boolean getBookmark(Long userId, Long teamId) {
        String sql = "select bookmark from team_member where user_id=:user_id and team_id=:team_id";
        Map<String, Object> param = Map.of("user_id", userId,
                "team_id", teamId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public GetTeamMemberResponse getTeamMembers(Long teamId) {
        String sql = "select u.name, u.user_id from team_member tm, user u " +
                "where tm.user_id=u.user_id " +
                "and u.status=1 and tm.team_id=:team_id order by tm.user_id";
        log.info("{}", teamId);
        Map<String, Object> param = Map.of("team_id", teamId);

        RowMapper<User> mapper = (rs, rowNum) -> {
            User user = new User();
            user.setUserId(rs.getLong("user_id"));
            user.setName(rs.getString("name"));
            return user;
        };

        List<User> userList = jdbcTemplate.query(sql, param, mapper);

        List<String> name = new ArrayList<>();
        List<Long> userId = new ArrayList<>();
        GetTeamMemberResponse teamMemberResponse = new GetTeamMemberResponse();
        teamMemberResponse.setUserName(name);
        teamMemberResponse.setUserId(userId);

        for(User user : userList) {
            teamMemberResponse.getUserId().add(user.getUserId());
            teamMemberResponse.getUserName().add(user.getName());
        }

        return teamMemberResponse;
    }

    public void bookmarkTeam(Long userId, Long teamId) {
        String sql = "update team_member set bookmark=1 where user_id=:user_id and team_id=:team_id";
        Map<String, Object> param = Map.of("user_id", userId,
                "team_id", teamId);

        jdbcTemplate.update(sql, param);
    }

    public void unBookmarkTeam(Long userId, Long teamId) {
        String sql = "update team_member set bookmark=0 where user_id=:user_id and team_id=:team_id";
        Map<String, Object> param = Map.of("user_id", userId,
                "team_id", teamId);

        jdbcTemplate.update(sql, param);
    }

    public GetTeamResponse getTeamDetail(Long teamId) {
        String sql = "select t.team_id, t.team_name, t.team_image_url, count(tm.user_id)" +
                "from team_member as tm join team as t on tm.team_id=t.team_id " +
                "where tm.team_id=:team_id";
        Map<String, Object> param = Map.of("team_id", teamId);

        RowMapper<GetTeamResponse> mapper = (rs, rowNum) -> {
            GetTeamResponse getTeamResponse = new GetTeamResponse();
            getTeamResponse.setTeamId(rs.getLong("team_id"));
            getTeamResponse.setTeamName(rs.getString("team_name"));
            getTeamResponse.setTeamImgUrl(rs.getString("team_image_url"));
            getTeamResponse.setTeamMemberCount(rs.getLong("count(tm.user_id)"));
            return getTeamResponse;
        };

        return jdbcTemplate.queryForObject(sql, param, mapper);
    }

    public List<Team> getBookmarks(Long userId) {
        String sql = "select t.team_id, t.team_name, t.team_image_url, t.created_at " +
                "from team_member as tm join team as t on tm.team_id=t.team_id " +
                "where tm.user_id=:user_id and tm.bookmark=1 order by tm.team_id desc";
        Map<String, Object> param = Map.of("user_id", userId);

        RowMapper<Team> mapper = (rs, rowNum) -> {
            Team team = new Team();
            team.setTeamId(rs.getLong("team_id"));
            team.setTeamName(rs.getString("team_name"));
            team.setTeamImgUrl(rs.getString("team_image_url"));
            team.setCodeGeneratedTime(rs.getTimestamp("created_at"));
            return team;
        };

        return jdbcTemplate.query(sql, param, mapper);
    }
}
