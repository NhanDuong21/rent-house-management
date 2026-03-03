package DALs.admin;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Models.dto.AdminAccountRowDTO;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-27
 */
public class ManageAccountDAO extends DBContext {

    @SuppressWarnings("CallToPrintStackTrace")
    public List<AdminAccountRowDTO> listAccounts(String roleFilter, String keyword, int offset, int limit) {
        List<AdminAccountRowDTO> list = new ArrayList<>();

        boolean filterAll = (roleFilter == null || roleFilter.isBlank() || "ALL".equalsIgnoreCase(roleFilter));
        boolean hasKeyword = (keyword != null && !keyword.isBlank());
        String kw = hasKeyword ? "%" + keyword.trim() + "%" : null;

        String sql = """
        SELECT account_type, account_id, email, full_name, role, status, created_at
        FROM (
            SELECT 
                'TENANT' AS account_type,
                t.tenant_id AS account_id,
                t.email AS email,
                t.full_name AS full_name,
                'TENANT' AS role,
                t.account_status AS status,
                t.created_at AS created_at
            FROM TENANT t

            UNION ALL

            SELECT
                'STAFF' AS account_type,
                s.staff_id AS account_id,
                s.email AS email,
                s.full_name AS full_name,
                'MANAGER' AS role,
                s.status AS status,
                s.created_at AS created_at
            FROM STAFF s
            WHERE UPPER(s.staff_role) = 'MANAGER'
        ) x
        WHERE (? = 1 OR x.role = ?)
          AND (? IS NULL OR x.full_name LIKE ? OR x.email LIKE ?)
        ORDER BY x.created_at DESC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;

            ps.setInt(i++, filterAll ? 1 : 0);
            ps.setString(i++, filterAll ? "" : roleFilter);

            if (hasKeyword) {
                ps.setString(i++, kw);
                ps.setString(i++, kw);
                ps.setString(i++, kw);
            } else {
                ps.setNull(i++, java.sql.Types.VARCHAR);
                ps.setNull(i++, java.sql.Types.VARCHAR);
                ps.setNull(i++, java.sql.Types.VARCHAR);
            }

            ps.setInt(i++, offset);
            ps.setInt(i++, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AdminAccountRowDTO dto = new AdminAccountRowDTO();
                    dto.setAccountType(rs.getString("account_type"));
                    dto.setAccountId(rs.getInt("account_id"));
                    dto.setEmail(rs.getString("email"));
                    dto.setFullName(rs.getString("full_name"));
                    dto.setRole(rs.getString("role"));
                    dto.setStatus(rs.getString("status"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(dto);
                }
            }

        } catch (Exception e) {
            System.out.println("[APP][ManageAccountDAO] ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public int countAccounts(String roleFilter, String keyword) {

        boolean filterAll = (roleFilter == null
                || roleFilter.isBlank()
                || "ALL".equalsIgnoreCase(roleFilter));

        boolean hasKeyword = (keyword != null && !keyword.isBlank());
        String kw = hasKeyword ? "%" + keyword.trim() + "%" : null;

        String sql = """
            SELECT COUNT(*) 
            FROM (
                SELECT t.tenant_id AS id, t.email, t.full_name, 'TENANT' AS role
                FROM TENANT t
                UNION ALL
                SELECT s.staff_id, s.email, s.full_name, 'MANAGER'
                FROM STAFF s
                WHERE UPPER(s.staff_role) = 'MANAGER'
            ) x
            WHERE (? = 1 OR x.role = ?)
              AND (? IS NULL OR x.full_name LIKE ? OR x.email LIKE ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;

            ps.setInt(i++, filterAll ? 1 : 0);
            ps.setString(i++, filterAll ? "" : roleFilter);

            if (hasKeyword) {
                ps.setString(i++, kw);
                ps.setString(i++, kw);
                ps.setString(i++, kw);
            } else {
                ps.setNull(i++, java.sql.Types.VARCHAR);
                ps.setNull(i++, java.sql.Types.VARCHAR);
                ps.setNull(i++, java.sql.Types.VARCHAR);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
