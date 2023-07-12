package com.kuit.conet.dao;

import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Optional<Long> findByPlatformAndPlatformId(Platform platform, String platformId) {
        String sql = "select userId from user where platform=:platform and platformId=:platformId";
        Map<String, String> param = Map.of(
                "platform", platform.getPlatform(),
                "platformId", platformId);

        RowMapper<Long> mapper = new SingleColumnRowMapper<>(Long.class);

        List<Long> userIdList = jdbcTemplate.query(sql, param, mapper);
        return userIdList.isEmpty() ? Optional.empty() : Optional.of(userIdList.get(0));
    }

    public Optional<User> findById(Long userId) {
        String sql = "select * from user where userId=:userId";
        Map<String, Long> param = Map.of("userId", userId);

        RowMapper<User> mapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("userId"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setServiceTerm(rs.getBoolean("serviceTerm"));
                user.setOptionTerm(rs.getBoolean("optionTerm"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("platformId"));
                return user;
            }
        };
        User user = jdbcTemplate.queryForObject(sql, param, mapper);
        Optional<User> returnUser = Optional.ofNullable(user);

        return returnUser;
    }

    public Optional<User> save(User oauthUser) {
        // 회원가입 -> insert 한 후, 넣은 애 반환
        String sql = "insert into user (email, platform, platformId) values (:email, :platform, :platformId)";
        Map<String, String> param = Map.of("email", oauthUser.getEmail(),
                "platform", oauthUser.getPlatform().toString(),
                "platformId", oauthUser.getPlatformId());

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from user where platform=:platform and platformId=:platformId";
        Map<String, String> returnParam = Map.of(
                "platform", oauthUser.getPlatform().toString(),
                "platformId", oauthUser.getPlatformId());

        RowMapper<User> returnMapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("userId"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setServiceTerm(rs.getBoolean("serviceTerm"));
                user.setOptionTerm(rs.getBoolean("optionTerm"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("platformId"));
                return user;
            }
        };

        User user = jdbcTemplate.queryForObject(returnSql, returnParam, returnMapper);

        return Optional.ofNullable(user);
    }

    public Optional<User> agreeTermAndPutName(String name, Boolean optionTerm, Long userId) {
        String sql = "update user set name=:name, serviceTerm=1, optionTerm=:optionTerm where userId=:userId";
        Map<String, Object> param = Map.of(
                "name", name,
                "optionTerm", optionTerm,
                "userId", userId);

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from user where userId=:userId";
        Map<String, Object> returnParam = Map.of("userId", userId);

        RowMapper<User> returnMapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("userId"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setServiceTerm(rs.getBoolean("serviceTerm"));
                user.setOptionTerm(rs.getBoolean("optionTerm"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("platformId"));
                return user;
            }
        };

        User user = jdbcTemplate.queryForObject(returnSql, returnParam, returnMapper);

        return Optional.ofNullable(user);
    }


    public void deleteUser(Long userId) {
        // user의 platform, platformId 초기화
        String sql = "update user set platform='', platformId='', serviceTerm=0 where userId=:userId";
        Map<String, Object> param = Map.of(
                "userId", userId);

        jdbcTemplate.update(sql, param);

        // user가 참여한 모든 모임(team) 나가기
        sql = "update teamMember set status=0 where userId=:userId";
        jdbcTemplate.update(sql, param);
    }
}
