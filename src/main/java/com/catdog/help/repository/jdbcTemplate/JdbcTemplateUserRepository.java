package com.catdog.help.repository.jdbcTemplate;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.user.Gender;
import com.catdog.help.domain.user.User;
import com.catdog.help.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
//@Repository
public class JdbcTemplateUserRepository implements UserRepository {

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateUserRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
    }

    @Override
    public void save(User user) {
        String sql = "insert into users(user_email_id, user_password, user_nickname, user_name, user_age, user_gender, create_date, last_modified_date, delete_date) " +
                "values (:email, :password, :nickname, :name, :age, :gender, :create, :modified, :delete)"; // users 주의

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("email", user.getEmailId())
                .addValue("password", user.getPassword())
                .addValue("nickname", user.getNickName())
                .addValue("name", user.getName())
                .addValue("age", user.getAge())
                .addValue("gender", user.getGender().toString())
                .addValue("create", user.getDates().getCreateDate())
                .addValue("modified", user.getDates().getLastModifiedDate())
                .addValue("delete", user.getDates().getDeleteDate());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);
        user.setId(keyHolder.getKey().longValue()); //테스트 아이디 저장용
    }

    @Override
    public User findById(Long id) {
        String sql = "select * from users where user_id = :id";
        Map<String, Object> param = Map.of("id", id);
        try {
            return template.queryForObject(sql, param, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        // TODO: 2023-03-25 bulletinBoards, likeBoards, comments 주입
    }

    @Override
    public User findByEmailId(String emailId) {
        String sql = "select * from users where user_email_id = :emailId";
        Map<String, Object> param = Map.of("emailId", emailId);
        try {
            return template.queryForObject(sql, param, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        // TODO: 2023-03-25 bulletinBoards, likeBoards, comments 주입
    }


    @Override
    public User findByNickName(String nickName) {
        String sql = "select * from users where user_nickname = :nickName";
        Map<String, Object> param = Map.of("nickName", nickName);
        try {
            return template.queryForObject(sql, param, userRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        // TODO: 2023-03-25 bulletinBoards, likeBoards, comments 주입
    }

    @Override
    public List<User> findAll() {
        String sql = "select * from users";
        return template.query(sql, userRowMapper());
    }

    @Override
    public Long delete(User user) {
        String sql = "delete from users where user_id = :id";
        Map<String, Object> param = Map.of("id", user.getId());
        return user.getId();
    }

    private RowMapper<User> userRowMapper() {
        return ((rs, rowNum) -> {
            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setEmailId(rs.getString("user_email_id"));
            user.setPassword(rs.getString("user_password"));
            user.setNickName(rs.getString("user_nickname"));
            user.setName(rs.getString("user_name"));
            user.setAge(rs.getInt("user_age"));
            user.setGender(Gender.valueOf(rs.getString("user_gender")));
            user.setDates(new Dates(((rs.getTimestamp("create_date") != null) ? rs.getTimestamp("create_date").toLocalDateTime() : null),
                                        (rs.getTimestamp("last_modified_date") != null) ? rs.getTimestamp("last_modified_date").toLocalDateTime() : null,
                                        (rs.getTimestamp("delete_date") != null) ? rs.getTimestamp("delete_date").toLocalDateTime() : null));
            return user;
        });
    }
}
