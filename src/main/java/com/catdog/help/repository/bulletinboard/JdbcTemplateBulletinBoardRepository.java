package com.catdog.help.repository.bulletinboard;

import com.catdog.help.domain.board.BulletinBoard;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
//@Repository
public class JdbcTemplateBulletinBoardRepository implements BulletinBoardRepository{

    private final NamedParameterJdbcTemplate template;

    public JdbcTemplateBulletinBoardRepository(DataSource dataSource) {
        this.template = new NamedParameterJdbcTemplate(dataSource);
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
        BulletinBoard board = template.queryForObject(sql, param, bulletinBoardRowMapper());
        // TODO: 2023-03-25 user , comment , likeBoard 주입
        return board;
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
//            board.setDates(Dates.builder()
//                    .createDate(rs.getTimestamp("create_date").toLocalDateTime())
//                    .lastModifiedDate(rs.getTimestamp("last_modified_date").toLocalDateTime())
//                    .deleteDate(rs.getTimestamp("delete_date").toLocalDateTime())
//                    .build());
            board.setViews(rs.getInt("board_views"));
            board.setRegion(rs.getString("region"));
            //참조 타입은 각각 매퍼로 가져온 다음에 주입해 줘야할 듯
            // TODO: 2023-03-24 BeanPropertyRowMapper 사용 시 상속받은 부모 프로퍼티는 파라미터로 접근이 안 되는 듯 하다. 블로그에 기술
            return board;
        });
    }
}
