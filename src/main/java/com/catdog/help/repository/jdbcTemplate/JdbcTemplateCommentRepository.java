package com.catdog.help.repository.jdbcTemplate;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.Comment;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.repository.CommentRepository;
import com.catdog.help.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JdbcTemplateCommentRepository implements CommentRepository {

    private final NamedParameterJdbcTemplate template;
    private final BulletinBoardRepository bulletinBoardRepository;
    private final UserRepository userRepository;

    @Autowired
    public JdbcTemplateCommentRepository(DataSource dataSource, BulletinBoardRepository bulletinBoardRepository, UserRepository userRepository) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.bulletinBoardRepository = bulletinBoardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Long save(Comment comment) {
        String sql = "insert into comment(comment_content, board_id, user_id, parent_id, create_date, last_modified_date, delete_date) " +
                "values (:content, :boardId, :userId, :parentId, :create, :modified, :delete)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("content", comment.getContent())
                .addValue("boardId", comment.getBoard().getId())
                .addValue("userId", comment.getUser().getId())
                .addValue("parentId", comment.getParent().getId())
                .addValue("create", comment.getDates().getCreateDate())
                .addValue("modified", comment.getDates().getLastModifiedDate())
                .addValue("delete", comment.getDates().getDeleteDate());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        comment.setId(key);

        return key;
    }

    @Override
    public Comment findById(Long commentId) {
        String sql = "select * from comment where comment_id = :id";
        Map<String, Object> param = Map.of("id", commentId);
        try {
            return template.queryForObject(sql, param, commentRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Comment> findAll(Long boardId) {
        String sql = "select * from comment where board_id = :board_id and parent_id is null";
        Map<String, Object> param = Map.of("board_id", boardId);
        return template.query(sql, param, commentRowMapper());
    }

    @Override
    public void delete(Comment comment) {
        String sql = "delete from comment where comment_id = :id";
        Map<String, Long> param = Map.of("id", comment.getId());
        template.update(sql, param);
    }

    private RowMapper<Comment> commentRowMapper() {
        return ((rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setId(rs.getLong("comment_id"));
            comment.setContent(rs.getString("comment_content"));
            comment.setBoard(bulletinBoardRepository.findOne(rs.getLong("board_id")));
            comment.setUser(userRepository.findById(rs.getLong("user_id")));
            comment.setDates(new Dates(((rs.getTimestamp("create_date") != null) ? rs.getTimestamp("create_date").toLocalDateTime() : null),
                    (rs.getTimestamp("last_modified_date") != null) ? rs.getTimestamp("last_modified_date").toLocalDateTime() : null,
                    (rs.getTimestamp("delete_date") != null) ? rs.getTimestamp("delete_date").toLocalDateTime() : null));
            return comment;
        });
    }
}
