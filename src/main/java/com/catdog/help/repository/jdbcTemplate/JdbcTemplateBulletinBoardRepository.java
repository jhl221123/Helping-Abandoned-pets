package com.catdog.help.repository.jdbcTemplate;

import com.catdog.help.domain.Dates;
import com.catdog.help.domain.board.BulletinBoard;
import com.catdog.help.repository.BulletinBoardRepository;
import com.catdog.help.repository.CommentRepository;
import com.catdog.help.repository.LikeBoardRepository;
import com.catdog.help.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JdbcTemplateBulletinBoardRepository implements BulletinBoardRepository {

    private final NamedParameterJdbcTemplate template;
    private final UserRepository userRepository;
//    private final CommentRepository commentRepository;
//    private final LikeBoardRepository likeBoardRepository;

    @Autowired
    public JdbcTemplateBulletinBoardRepository(DataSource dataSource, UserRepository userRepository) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
        this.userRepository = userRepository;
        // 의존관계 주입 시 순환 참조발생
//        this.commentRepository = new JdbcTemplateCommentRepository(dataSource, this, userRepository);
//        this.likeBoardRepository = new JdbcTemplateLikeBoardRepository(dataSource, this, userRepository);
    }

    @Override
    public Long save(BulletinBoard bulletinBoard) {
        String sql = "insert into board(board_title, board_content, region, board_views, create_date, last_modified_date, delete_date, user_id, dtype) " +
                "values (:title, :content, :region, :views, :create, :modified, :delete, :user_id, :dtype)";

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("title", bulletinBoard.getTitle())
                .addValue("content", bulletinBoard.getContent())
                .addValue("region", bulletinBoard.getRegion())
                .addValue("views", bulletinBoard.getViews())
                .addValue("create", bulletinBoard.getDates().getCreateDate())
                .addValue("modified", bulletinBoard.getDates().getLastModifiedDate())
                .addValue("delete", bulletinBoard.getDates().getDeleteDate())
                .addValue("user_id", bulletinBoard.getUser().getId())
                .addValue("dtype", "Bulletin");

        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(sql, param, keyHolder);

        long key = keyHolder.getKey().longValue();
        bulletinBoard.setId(key);
        return bulletinBoard.getId();
    }

    @Override
    public BulletinBoard findOne(Long id) {
        String sql = "select * from board where board_id = :id";
        Map<String, Object> param = Map.of("id", id);
        BulletinBoard board = template.queryForObject(sql, param, bulletinBoardRowMapper()); // TODO: 2023-03-26 존재하지 않는 아이디로 조회시 null 반환하기때문에 try-catch 사용

        return board;
    }

    @Override
    public List<BulletinBoard> findPage(int start, int end) {
        // TODO: 2023-03-27 페이징 처리
        return null;
    }

    @Override
    public List<BulletinBoard> findAll() {
        String sql = "select * from board order by create_date desc";
        return template.query(sql, bulletinBoardRowMapper());
    }

    @Override
    public Long delete(BulletinBoard board) {
        String sql = "delete from board where id = :id";
        Map<String, Long> param = Map.of("id", board.getId());
        template.update(sql, param);
        return board.getId();
    }

    private RowMapper<BulletinBoard> bulletinBoardRowMapper() {
        return ((rs, rowNum) -> {
            BulletinBoard board = new BulletinBoard();
            board.setId(rs.getLong("board_id"));
            board.setTitle(rs.getString("board_title"));
            board.setContent(rs.getString("board_content"));
            board.setDates(new Dates(((rs.getTimestamp("create_date") != null) ? rs.getTimestamp("create_date").toLocalDateTime() : null),
                                        (rs.getTimestamp("last_modified_date") != null) ? rs.getTimestamp("last_modified_date").toLocalDateTime() : null,
                                        (rs.getTimestamp("delete_date") != null) ? rs.getTimestamp("delete_date").toLocalDateTime() : null));
            board.setViews(rs.getInt("board_views"));
            board.setRegion(rs.getString("region"));
            board.setUser(userRepository.findById(rs.getLong("user_id")));
//            board.setComments(commentRepository.findAll(rs.getLong("board_id"))); 순환참조 발생
//            board.setLikeBoards(likeBoardRepository.findAllByBoardId(rs.getLong("board_id")));
            // 1+n query -> join 사용해야할듯
            // TODO: 2023-03-24 BeanPropertyRowMapper 사용 시 상속받은 부모 프로퍼티는 파라미터로 접근이 안 되는 듯 하다. 블로그에 기술
            return board;
        });
    }
}
