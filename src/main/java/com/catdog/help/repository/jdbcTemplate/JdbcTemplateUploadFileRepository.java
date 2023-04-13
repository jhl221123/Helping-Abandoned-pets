//package com.catdog.help.repository.jdbcTemplate;
//
//import com.catdog.help.domain.board.UploadFile;
//import com.catdog.help.repository.JpaBulletinBoardRepository;
//import com.catdog.help.repository.UploadFileRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
//import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.springframework.jdbc.support.GeneratedKeyHolder;
//import org.springframework.jdbc.support.KeyHolder;
//
//import javax.sql.DataSource;
//import java.util.List;
//import java.util.Map;
//
////@Repository
//public class JdbcTemplateUploadFileRepository implements UploadFileRepository {
//
//    private final NamedParameterJdbcTemplate template;
//    private final JpaBulletinBoardRepository jpaBulletinBoardRepository;
//
//
//    @Autowired
//    public JdbcTemplateUploadFileRepository(DataSource dataSource, JpaBulletinBoardRepository jpaBulletinBoardRepository) {
//        this.template = new NamedParameterJdbcTemplate(dataSource);
//        this.jpaBulletinBoardRepository = jpaBulletinBoardRepository;
//    }
//
//    @Override
//    public Long save(UploadFile uploadFile) {
//        String sql = "insert into upload_file(store_file_name, upload_file_name, board_id) " +
//                "values (:storeName, :uploadName, :boardId)";
//        MapSqlParameterSource param = new MapSqlParameterSource()
//                .addValue("storeName", uploadFile.getStoreFileName())
//                .addValue("uploadName", uploadFile.getUploadFileName())
//                .addValue("boardId", uploadFile.getBoard().getId());
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        template.update(sql, param, keyHolder);
//
//        long key = keyHolder.getKey().longValue();
//        uploadFile.setId(key);
//
//        return key;
//    }
//
//    @Override
//    public UploadFile findById(Long id) {
//        return null;
//    }
//
//    @Override
//    public List<UploadFile> findUploadFiles(Long boardId) {
//        String sql = "select * from upload_file where board_id = :boardId";
//        Map<String, Object> param = Map.of("boardId", boardId);
//        return template.query(sql, param, uploadFileRowMapper());
//    }
//
//    @Override
//    public Long delete(UploadFile uploadFile) {
//        return null;
//    }
//
//    private RowMapper<UploadFile> uploadFileRowMapper() {
//        return ((rs, rowNum) -> {
//            UploadFile uploadFile = new UploadFile();
//            uploadFile.setId(rs.getLong("upload_file_id"));
//            uploadFile.setStoreFileName(rs.getString("store_file_name"));
//            uploadFile.setUploadFileName(rs.getString("upload_file_name"));
//            uploadFile.setBoard(jpaBulletinBoardRepository.findById(rs.getLong("board_id")));
//            return uploadFile;
//        });
//    }
//}
