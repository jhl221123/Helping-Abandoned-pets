package com.catdog.help.repository.jdbcTemplate;

import com.catdog.help.domain.board.LikeBoard;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.repository.LikeBoardRepository;
import com.catdog.help.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
//@Repository
public class JdbcTemplateLikeBoardRepository implements LikeBoardRepository {

    private final NamedParameterJdbcTemplate template;
    private final BulletinBoardRepository bulletinBoardRepository;
    private final UserRepository userRepository;

    @Autowired
    public JdbcTemplateLikeBoardRepository(DataSource dataSource, BulletinBoardRepository bulletinBoardRepository, UserRepository userRepository) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.bulletinBoardRepository = bulletinBoardRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Long save(LikeBoard likeBoard) {
        String sql = "insert into like_board(board_id, user_id) values (:boardId, :userId)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("boardId", likeBoard.getBoard().getId())
                .addValue("userId", likeBoard.getUser().getId());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        likeBoard.setId(key);

        return likeBoard.getId();
    }

    @Override
    public LikeBoard findById(Long likeBoardId) {
        String sql = "select * from like_board where like_board_id = :likeBoardId";
        Map<String, Object> param = Map.of("likeBoardId", likeBoardId);
        try {
            return template.queryForObject(sql, param, likeBoardRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public LikeBoard findByIds(Long boardId, Long userId) {
        String sql = "select * from like_board where board_id = :boardId and user_id = :userId";
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("boardId", boardId)
                .addValue("userId", userId);
        try {
            return template.queryForObject(sql, param, likeBoardRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<LikeBoard> findAllByBoardId(Long boardId) {
        String sql = "select * from like_board where board_id = :boardId";
        Map<String, Object> param = Map.of("boardId", boardId);
        return template.query(sql, param, likeBoardRowMapper());
    }

    @Override
    public void delete(LikeBoard likeBoard) {
        String sql = "delete from like_board where like_board_id = :id";
        Map<String, Object> param = Map.of("id", likeBoard.getId());
        template.update(sql, param);
    }

    private RowMapper<LikeBoard> likeBoardRowMapper() {
        return ((rs, rowNum) -> {
            LikeBoard likeBoard = new LikeBoard();
            likeBoard.setId(rs.getLong("like_board_id"));
            likeBoard.setBoard(bulletinBoardRepository.findOne(rs.getLong("board_id")));
            likeBoard.setUser(userRepository.findById(rs.getLong("user_id")));
            return likeBoard;
        });
    }
}
