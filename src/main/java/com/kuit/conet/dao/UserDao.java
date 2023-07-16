package com.kuit.conet.dao;

import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.User;
import com.kuit.conet.dto.response.user.UserResponse;
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

@Slf4j
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<Long> findByPlatformAndPlatformId(Platform platform, String platformId) {
        String sql = "select user_id from user where platform=:platform and platform_id=:platform_id";
        Map<String, String> param = Map.of(
                "platform", platform.getPlatform(),
                "platform_id", platformId);

        RowMapper<Long> mapper = new SingleColumnRowMapper<>(Long.class);

        return jdbcTemplate.query(sql, param, mapper);
    }

    public User findById(Long userId) {
        String sql = "select * from user where user_id=:user_id";
        Map<String, Long> param = Map.of("user_id", userId);

        RowMapper<User> mapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setUserImgUrl(rs.getString("img_url"));
                user.setServiceTerm(rs.getBoolean("service_term"));
                user.setOptionTerm(rs.getBoolean("option_term"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("platform_id"));
                return user;
            }
        };

        return jdbcTemplate.queryForObject(sql, param, mapper);
    }

    public User save(User oauthUser) {
        // 회원가입 -> insert 한 후, 넣은 애 반환
        String sql = "insert into user (email, platform, platform_id) values (:email, :platform, :platform_id)";
        Map<String, String> param = Map.of("email", oauthUser.getEmail(),
                "platform", oauthUser.getPlatform().toString(),
                "platform_id", oauthUser.getPlatformId());

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from user where platform=:platform and platform_id=:platform_id";
        Map<String, String> returnParam = Map.of(
                "platform", oauthUser.getPlatform().toString(),
                "platform_id", oauthUser.getPlatformId());

        RowMapper<User> returnMapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setUserImgUrl(rs.getString("img_url"));
                user.setServiceTerm(rs.getBoolean("service_term"));
                user.setOptionTerm(rs.getBoolean("option_term"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("platform_id"));
                return user;
            }
        };

        return jdbcTemplate.queryForObject(returnSql, returnParam, returnMapper);
    }

    public User agreeTermAndPutName(String name, Boolean optionTerm, Long userId) {
        String sql = "update user set name=:name, service_term=1, option_term=:option_term where user_id=:user_id";
        Map<String, Object> param = Map.of(
                "name", name,
                "option_term", optionTerm,
                "user_id", userId);

        jdbcTemplate.update(sql, param);

        String returnSql = "select * from user where user_id=:user_id";
        Map<String, Object> returnParam = Map.of("user_id", userId);

        RowMapper<User> returnMapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("user_id"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setUserImgUrl(rs.getString("img_url"));
                user.setServiceTerm(rs.getBoolean("service_term"));
                user.setOptionTerm(rs.getBoolean("option_term"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                user.setPlatformId(rs.getString("option_term"));
                return user;
            }
        };

        return jdbcTemplate.queryForObject(returnSql, returnParam, returnMapper);
    }


    public void deleteUser(Long userId) {
        // user의 platform, platformId 초기화
        String sql = "update user set platform='', platform_id='', service_term=0 where user_id=:user_id";
        Map<String, Object> param = Map.of("user_id", userId);

        jdbcTemplate.update(sql, param);

        // user가 참여한 모든 모임(team) 나가기
        sql = "update team_member set status=0 where user_id=:user_id";
        jdbcTemplate.update(sql, param);
    }

    public UserResponse getUser(Long userId) {
        String sql = "select name, email, img_url, platform from user where user_id=:user_id and status=1";
        Map<String, Object> param = Map.of("user_id", userId);

        RowMapper<UserResponse> mapper = new RowMapper<UserResponse>() {
            public UserResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
                UserResponse user = new UserResponse();
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setUserImgUrl(rs.getString("img_url"));
                String platform = rs.getString("platform");
                user.setPlatform(Platform.valueOf(platform));
                return user;
            }
        };

        return jdbcTemplate.queryForObject(sql, param, mapper);
    }

    public Boolean isExistUser(Long userId) {
        String sql = "select exists(select * from user where user_id=:user_id and status=1)";
        Map<String, Object> param = Map.of("user_id", userId);

        return jdbcTemplate.queryForObject(sql, param, Boolean.class);
    }

    public void updateImg(Long userId, String imgUrl) {
        String sql = "update user set img_url=:img_url where user_id=:user_id and status=1";
        Map<String, Object> param = Map.of("user_id", userId,
                "img_url", imgUrl);
        jdbcTemplate.update(sql, param);
    }

    public void updateName(Long userId, String name) {
        String sql = "update user set name=:name where user_id=:user_id and status=1";
        Map<String, Object> param = Map.of("user_id", userId,
                "name", name);
        jdbcTemplate.update(sql, param);
    }
}
