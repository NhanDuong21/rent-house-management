package DALs.auth;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Models.entity.Tenant;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-05
 */
public class TenantDAO extends DBContext {

    @SuppressWarnings("CallToPrintStackTrace")
    public Tenant findByEmail(String email) {
        String sql = "SELECT tenant_id, full_name, email, password_hash, account_status, must_set_password "
                + "FROM TENANT WHERE email = ?";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Tenant t = new Tenant();
                t.setTenantId(rs.getInt("tenant_id"));
                t.setFullName(rs.getString("full_name"));
                t.setEmail(rs.getString("email"));
                t.setPasswordHash(rs.getString("password_hash"));
                t.setAccountStatus(rs.getString("account_status"));
                t.setMustSetPassword(rs.getBoolean("must_set_password"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTokenForTenant(int tenantId, String token) {
        String sql = "UPDATE TENANT SET token = ? WHERE tenant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, tenantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("SAVE REMEMBER TOKEN ERROR: " + e.getMessage());
        }
    }

    public void clearTokenForTenant(int tenantId) {
        String sql = "UPDATE TENANT SET token = NULL WHERE tenant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("CLEAR REMEMBER TOKEN ERROR: " + e.getMessage());
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Tenant findByTokenForTenant(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }

        String sql = "select top 1 * from TENANT where token = ? and account_status = 'ACTIVE'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, token.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tenant tenant = new Tenant();
                    tenant.setTenantId(rs.getInt("tenant_id"));
                    tenant.setFullName(rs.getString("full_name"));
                    tenant.setIdentityCode(rs.getString("identity_code"));
                    tenant.setPhoneNumber(rs.getString("phone_number"));
                    tenant.setEmail(rs.getString("email"));
                    tenant.setAddress(rs.getString("address"));
                    tenant.setDateOfBirth(rs.getDate("date_of_birth"));
                    tenant.setGender(rs.getObject("gender") == null ? null : ((Number) rs.getObject("gender")).intValue());
                    tenant.setAvatar(rs.getString("avatar"));
                    tenant.setAccountStatus(rs.getString("account_status"));
                    tenant.setPasswordHash(rs.getString("password_hash"));
                    tenant.setMustSetPassword(rs.getBoolean("must_set_password"));
                    tenant.setCreatedAt(rs.getTimestamp("created_at"));
                    tenant.setUpdatedAt(rs.getTimestamp("updated_at"));
                    return tenant;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // add tenant pending
    public int insertPendingTenant(Connection conn, Tenant t) throws SQLException {

        String sql = """
        INSERT INTO TENANT (
            full_name, identity_code, phone_number, email, [address], date_of_birth, gender, avatar,
            account_status, password_hash, must_set_password
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING', NULL, 1)
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getFullName());
            ps.setString(2, t.getIdentityCode());
            ps.setString(3, t.getPhoneNumber());
            ps.setString(4, t.getEmail());
            ps.setString(5, t.getAddress());
            ps.setDate(6, t.getDateOfBirth());
            ps.setInt(7, t.getGender());
            ps.setString(8, t.getAvatar());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public String getPasswordHashById(int tenantId) {
        String sql = "SELECT password_hash FROM TENANT WHERE tenant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updatePasswordForTenant(int tenantId, String newHash) {
        String sql = """
        UPDATE TENANT
        SET password_hash = ?, must_set_password = 0, updated_at = SYSDATETIME()
        WHERE tenant_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateAccountStatus(int tenantId, String status) {
        String sql = "UPDATE TENANT SET account_status = ?, updated_at = SYSDATETIME() WHERE tenant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Tenant> findActiveTenants() {
        List<Tenant> list = new ArrayList<>();

        String sql = """
        SELECT tenant_id, full_name, email, phone_number
        FROM TENANT
        WHERE account_status = 'ACTIVE'
        ORDER BY full_name ASC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Tenant t = new Tenant();
                t.setTenantId(rs.getInt("tenant_id"));
                t.setFullName(rs.getString("full_name"));
                t.setEmail(rs.getString("email"));
                t.setPhoneNumber(rs.getString("phone_number"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Tenant findById(int tenantId) {
        String sql = """
        SELECT tenant_id, full_name, identity_code, phone_number, email, [address],
               date_of_birth, gender, avatar, account_status, password_hash, must_set_password,
               created_at, updated_at
        FROM TENANT
        WHERE tenant_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Tenant t = new Tenant();
                    t.setTenantId(rs.getInt("tenant_id"));
                    t.setFullName(rs.getString("full_name"));
                    t.setIdentityCode(rs.getString("identity_code"));
                    t.setPhoneNumber(rs.getString("phone_number"));
                    t.setEmail(rs.getString("email"));
                    t.setAddress(rs.getString("address"));
                    t.setDateOfBirth(rs.getDate("date_of_birth"));
                    t.setGender(rs.getObject("gender") == null ? null : ((Number) rs.getObject("gender")).intValue());
                    t.setAvatar(rs.getString("avatar"));
                    t.setAccountStatus(rs.getString("account_status"));
                    t.setPasswordHash(rs.getString("password_hash"));
                    t.setMustSetPassword(rs.getBoolean("must_set_password"));
                    t.setCreatedAt(rs.getTimestamp("created_at"));
                    t.setUpdatedAt(rs.getTimestamp("updated_at"));

                    return t;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //lọc ra những dòng có phone number giống nhưng mà phải khác tenant id ở giá trị thứ 2
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean existsPhoneExceptTenant(int tenantId, String phone) {
        String sql = "SELECT 1 FROM TENANT WHERE phone_number = ? AND tenant_id <> ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setInt(2, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updatePhoneForTenant(int tenantId, String newPhone) {
        String sql = "UPDATE TENANT SET phone_number = ?, updated_at = SYSDATETIME() WHERE tenant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPhone);
            ps.setInt(2, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //same logic updatePasswordForTenant do not use must set pass
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean adminResetPasswordForTenant(int tenantId, String newHash) {
        String sql = """
        UPDATE TENANT
        SET password_hash = ?, updated_at = SYSDATETIME()
        WHERE tenant_id = ?
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newHash);
            ps.setInt(2, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Tenant> getAllTenants() {
        List<Tenant> list = new ArrayList<>();
        try {
            String sql = "SELECT tenant_id, full_name, identity_code, phone_number, email, date_of_birth, gender, address, account_status FROM TENANT";
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tenant t = new Tenant();
                t.setTenantId(rs.getInt("tenant_id"));
                t.setFullName(rs.getString("full_name"));
                t.setIdentityCode(rs.getString("identity_code"));
                t.setPhoneNumber(rs.getString("phone_number"));
                t.setEmail(rs.getString("email"));
                t.setDateOfBirth(rs.getDate("date_of_birth"));
                t.setGender(rs.getObject("gender") == null ? null : ((Number) rs.getObject("gender")).intValue());
                t.setAddress(rs.getString("address"));
                t.setAccountStatus(rs.getString("account_status"));
                list.add(t);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Tenant> searchTenant(String keyword) {
        List<Tenant> list = new ArrayList<>();
        try {
            String sql = """
            SELECT tenant_id, full_name, identity_code, phone_number, email, date_of_birth, gender, address, account_status
            FROM TENANT
            WHERE full_name LIKE ?
               OR phone_number LIKE ?
               OR email LIKE ?
               OR identity_code LIKE ?
        """;

            PreparedStatement ps = connection.prepareStatement(sql);
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Tenant t = new Tenant();
                t.setTenantId(rs.getInt("tenant_id"));
                t.setFullName(rs.getString("full_name"));
                t.setIdentityCode(rs.getString("identity_code"));
                t.setPhoneNumber(rs.getString("phone_number"));
                t.setEmail(rs.getString("email"));
                t.setDateOfBirth(rs.getDate("date_of_birth"));
                t.setGender(rs.getObject("gender") == null ? null : ((Number) rs.getObject("gender")).intValue());
                t.setAddress(rs.getString("address"));
                t.setAccountStatus(rs.getString("account_status"));
                list.add(t);
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean updateTenant(Tenant t) {
        String sql = """
        UPDATE TENANT
        SET full_name = ?, 
            identity_code = ?, 
            phone_number = ?, 
            email = ?, 
            address = ?, 
            date_of_birth = ?, 
            gender = ?, 
            avatar = ?, 
            updated_at = GETDATE()
        WHERE tenant_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, t.getFullName());
            ps.setString(2, t.getIdentityCode());
            ps.setString(3, t.getPhoneNumber());
            ps.setString(4, t.getEmail());
            ps.setString(5, t.getAddress());
            ps.setDate(6, t.getDateOfBirth());
            ps.setObject(7, t.getGender());
            ps.setString(8, t.getAvatar());
            ps.setInt(9, t.getTenantId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean lockTenant(int tenantId) {
        String sql = "UPDATE TENANT SET account_status = 'LOCKED' WHERE tenant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean toggleStatus(int tenantId, String newStatus) {
        String sql = "UPDATE TENANT SET account_status = ?, updated_at = SYSDATETIME() WHERE tenant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy danh sách tenant có phân trang (không tìm kiếm).
     *
     * @param page trang hiện tại (bắt đầu từ 1)
     * @param pageSize số bản ghi mỗi trang
     */
    public List<Tenant> getTenantsPaged(int page, int pageSize) {
        List<Tenant> list = new ArrayList<>();
        String sql = """
            SELECT tenant_id, full_name, identity_code, phone_number, email,
                   date_of_birth, gender, address, account_status
            FROM TENANT
            ORDER BY tenant_id ASC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tenant t = new Tenant();
                t.setTenantId(rs.getInt("tenant_id"));
                t.setFullName(rs.getString("full_name"));
                t.setIdentityCode(rs.getString("identity_code"));
                t.setPhoneNumber(rs.getString("phone_number"));
                t.setEmail(rs.getString("email"));
                t.setDateOfBirth(rs.getDate("date_of_birth"));
                t.setGender(rs.getObject("gender") == null ? null : ((Number) rs.getObject("gender")).intValue());
                t.setAddress(rs.getString("address"));
                t.setAccountStatus(rs.getString("account_status"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm tổng số tenant (để tính tổng trang).
     */
    public int countAllTenants() {
        String sql = "SELECT COUNT(*) FROM TENANT";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Tìm kiếm tenant có phân trang.
     */
    public List<Tenant> searchTenantPaged(String keyword, int page, int pageSize) {
        List<Tenant> list = new ArrayList<>();
        String sql = """
            SELECT tenant_id, full_name, identity_code, phone_number, email,
                   date_of_birth, gender, address, account_status
            FROM TENANT
            WHERE full_name LIKE ?
               OR phone_number LIKE ?
               OR email LIKE ?
               OR identity_code LIKE ?
            ORDER BY tenant_id ASC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);
            ps.setInt(5, (page - 1) * pageSize);
            ps.setInt(6, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Tenant t = new Tenant();
                t.setTenantId(rs.getInt("tenant_id"));
                t.setFullName(rs.getString("full_name"));
                t.setIdentityCode(rs.getString("identity_code"));
                t.setPhoneNumber(rs.getString("phone_number"));
                t.setEmail(rs.getString("email"));
                t.setDateOfBirth(rs.getDate("date_of_birth"));
                t.setGender(rs.getObject("gender") == null ? null : ((Number) rs.getObject("gender")).intValue());
                t.setAddress(rs.getString("address"));
                t.setAccountStatus(rs.getString("account_status"));
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Đếm kết quả tìm kiếm (để tính tổng trang khi search).
     */
    public int countSearchTenant(String keyword) {
        String sql = """
            SELECT COUNT(*) FROM TENANT
            WHERE full_name LIKE ?
               OR phone_number LIKE ?
               OR email LIKE ?
               OR identity_code LIKE ?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);
            ps.setString(3, key);
            ps.setString(4, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
