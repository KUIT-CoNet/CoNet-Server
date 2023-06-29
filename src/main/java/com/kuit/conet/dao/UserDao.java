package com.kuit.conet.dao;

import com.kuit.conet.domain.Platform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.Map;

@Slf4j
@Repository
public class UserDao {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    // TODO: 기능 별 query 작성
    public boolean findByPlatformAndPlatformId(Platform platform, String platformId) {
        String sql = "select exists(select userId from user where platform=:platform and platformId=:platformId";
        Map<String, String> param = Map.of(
                "platform", platform.getPlatform(),
                "platform_id", platformId);
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, param, boolean.class));
    }



}
