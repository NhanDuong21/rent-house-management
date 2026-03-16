package DALs.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-03-17
 */
public class OccupantDocumentDAO extends DBContext {

    public int insertDocument(Connection conn, int occupantId, String documentType, String fileUrl) throws SQLException {
        String sql = """
            INSERT INTO OCCUPANT_DOCUMENT
                (occupant_id, document_type, file_url, status)
            VALUES
                (?, ?, ?, 'ACTIVE')
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, occupantId);
            ps.setString(2, documentType);
            ps.setString(3, fileUrl);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }
}
