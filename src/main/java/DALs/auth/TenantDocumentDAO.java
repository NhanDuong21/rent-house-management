package DALs.auth;

import Models.entity.TenantDocument;
import Utils.database.DBContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-03-15
 */
public class TenantDocumentDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(TenantDocumentDAO.class.getName());

    public int insertDocument(Connection conn, int tenantId, String documentType, String fileUrl) throws SQLException {
        String sql = """
                INSERT INTO TENANT_DOCUMENT
                    (tenant_id, document_type, file_url, status)
                VALUES
                    (?, ?, ?, 'ACTIVE')
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tenantId);
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
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to insert tenant document. tenantId=%d, documentType=%s, fileUrl=%s",
                            tenantId, documentType, fileUrl),
                    e);
            throw e;
        }

        return -1;
    }

    public List<TenantDocument> findByTenantId(int tenantId) {
        List<TenantDocument> list = new ArrayList<>();

        String sql = """
                SELECT
                    document_id,
                    tenant_id,
                    document_type,
                    file_url,
                    status,
                    uploaded_at
                FROM TENANT_DOCUMENT
                WHERE tenant_id = ?
                  AND status = 'ACTIVE'
                ORDER BY uploaded_at DESC, document_id DESC
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TenantDocument d = new TenantDocument();
                    d.setDocumentId(rs.getInt("document_id"));
                    d.setTenantId(rs.getInt("tenant_id"));
                    d.setDocumentType(rs.getString("document_type"));
                    d.setFileUrl(rs.getString("file_url"));
                    d.setStatus(rs.getString("status"));
                    d.setUploadedAt(rs.getTimestamp("uploaded_at"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to find documents by tenantId=%d", tenantId),
                    e);
        }

        return list;
    }

    public String findActiveFileUrlByType(int tenantId, String documentType) {
        String sql = """
                SELECT TOP 1 file_url
                FROM TENANT_DOCUMENT
                WHERE tenant_id = ?
                  AND document_type = ?
                  AND status = 'ACTIVE'
                ORDER BY uploaded_at DESC, document_id DESC
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.setString(2, documentType);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("file_url");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to find active file URL. tenantId=%d, documentType=%s",
                            tenantId, documentType),
                    e);
        }

        return null;
    }

    public boolean hasRequiredCccd(int tenantId) {
        String sql = """
                SELECT COUNT(DISTINCT document_type)
                FROM TENANT_DOCUMENT
                WHERE tenant_id = ?
                  AND status = 'ACTIVE'
                  AND document_type IN ('CCCD_FRONT', 'CCCD_BACK')
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) >= 2;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to check required CCCD documents for tenantId=%d", tenantId),
                    e);
        }

        return false;
    }

    public boolean softDeleteById(Connection conn, int documentId) throws SQLException {
        String sql = """
                UPDATE TENANT_DOCUMENT
                SET status = 'DELETED'
                WHERE document_id = ?
                  AND status = 'ACTIVE'
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, documentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to soft delete tenant document. documentId=%d", documentId),
                    e);
            throw e;
        }
    }
}