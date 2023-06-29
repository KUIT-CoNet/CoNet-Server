package com.kuit.conet.dao;

import com.kuit.conet.domain.Platform;
import com.kuit.conet.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
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

    // TODO: 기능 별 query 작성
    public Optional<User> findByPlatformAndPlatformId(Platform platform, String platformId) {
        String sql = "select exists(select * from user where platform=:platform and platformId=:platformId";
        Map<String, String> param = Map.of(
                "platform", platform.getPlatform(),
                "platform_id", platformId);

        RowMapper<User> mapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("userId"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
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

    public Optional<User> findById(Long userId) {
        String sql = "select * from user where userId=:userId";
        Map<String, Long> param = Map.of("userId", userId);

        RowMapper<User> mapper = new RowMapper<User>() {
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                User user = new User();
                user.setUserId(rs.getLong("userId"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
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

    public User save(User oauthUser) {
        // 회원가입 -> insert 한 후, 넣은 애 반환
        String sql = "insert into values(:name, :email, :password, :platform, :platformId)";
        Map<String, String> param = Map.of("name", oauthUser.getName().toString(),
                                            "email", oauthUser.getEmail().toString(),
                                            "password", oauthUser.getPassword().toString(),
                                            "platform", oauthUser.getPlatform().toString(),
                                            "platformId", oauthUser.getUserId().toString());

        jdbcTemplate.update(sql, param);
        return findByPlatformAndPlatformId(oauthUser.getPlatform(), oauthUser.getPlatformId()).get();
    }
}
