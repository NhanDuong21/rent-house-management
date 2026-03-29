package DALs.admin;

import Models.dto.AdminAccountRowDTO;
import Models.entity.Tenant;
import Utils.database.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-27
 */
public class ManageAccountDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ManageAccountDAO.class.getName());

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

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to list accounts. roleFilter=%s, keyword=%s, offset=%d, limit=%d",
                            roleFilter, keyword, offset, limit),
                    e);
        }

        return list;
    }

    public int countAccounts(String roleFilter, String keyword) {
        boolean filterAll = (roleFilter == null || roleFilter.isBlank() || "ALL".equalsIgnoreCase(roleFilter));
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

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to count accounts. roleFilter=%s, keyword=%s", roleFilter, keyword),
                    e);
        }

        return 0;
    }

    /**
     * Kiểm tra tenant có contract nào chưa END hoặc CANCEL không.
     * Trả về true => còn contract active => KHÔNG được lock.
     */
    public boolean tenantHasActiveContract(int tenantId) {
        String sql = """
                SELECT COUNT(*)
                FROM CONTRACT
                WHERE tenant_id = ?
                  AND UPPER(status) NOT IN ('ENDED', 'CANCELLED')
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to check active contract for tenantId=%d", tenantId),
                    e);
        }

        return false;
    }

    /**
     * Cập nhật account_status cho TENANT.
     */
    public boolean updateTenantStatus(int tenantId, String newStatus) {
        String sql = "UPDATE TENANT SET account_status = ? WHERE tenant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, tenantId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to update tenant status. tenantId=%d, newStatus=%s", tenantId, newStatus),
                    e);
        }

        return false;
    }

    /**
     * Cập nhật status cho STAFF (chỉ MANAGER).
     */
    public boolean updateStaffStatus(int staffId, String newStatus) {
        String sql = "UPDATE STAFF SET status = ? WHERE staff_id = ? AND UPPER(staff_role) = 'MANAGER'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, staffId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to update staff status. staffId=%d, newStatus=%s", staffId, newStatus),
                    e);
        }

        return false;
    }

    /**
     * Check email tồn tại
     */
    public boolean existsEmail(String email) {
        String sql = """
                SELECT 1 FROM TENANT WHERE email = ?
                UNION
                SELECT 1 FROM STAFF WHERE email = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to check existing email. email=%s", email),
                    e);
        }

        return false;
    }

    /**
     * Create Account Tenant
     */
    public boolean insertTenant(String fullName,
            String identityCode,
            String phoneNumber,
            String email,
            String address,
            String dob,
            int gender,
            String passwordHash) {

        String sql = """
                INSERT INTO TENANT
                (full_name, identity_code, phone_number, email,
                 address, date_of_birth, gender, avatar,
                 account_status, password_hash,
                 must_set_password, created_at, updated_at, token)
                VALUES (?, ?, ?, ?, ?, ?, ?,
                        'assets/images/avatar/avDefault.png',
                        'ACTIVE', ?,
                        1, GETDATE(), GETDATE(), NULL)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, identityCode);
            ps.setString(3, phoneNumber);
            ps.setString(4, email);
            ps.setString(5, address);
            ps.setDate(6, java.sql.Date.valueOf(dob));
            ps.setInt(7, gender);
            ps.setString(8, passwordHash);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to insert tenant. email=%s, identityCode=%s", email, identityCode),
                    e);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING,
                    String.format("Invalid tenant date format. dob=%s, email=%s", dob, email),
                    e);
        }

        return false;
    }

    /**
     * Create Account manager
     */
    public boolean insertManager(String fullName,
            String identityCode,
            String phoneNumber,
            String email,
            String dob,
            int gender,
            String passwordHash) {

        String sql = """
                INSERT INTO STAFF
                (full_name, phone_number, email,
                 identity_code, date_of_birth, gender,
                 staff_role, password_hash,
                 avatar, status,
                 created_at, updated_at, token)
                VALUES (?, ?, ?, ?, ?, ?,
                        'MANAGER', ?,
                        'assets/images/avatar/avDefault.png',
                        'ACTIVE',
                        GETDATE(), GETDATE(), NULL)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, phoneNumber);
            ps.setString(3, email);
            ps.setString(4, identityCode);
            ps.setDate(5, java.sql.Date.valueOf(dob));
            ps.setInt(6, gender);
            ps.setString(7, passwordHash);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to insert manager. email=%s, identityCode=%s", email, identityCode),
                    e);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING,
                    String.format("Invalid manager date format. dob=%s, email=%s", dob, email),
                    e);
        }

        return false;
    }

    public int countTenants() {
        String sql = "SELECT COUNT(*) FROM TENANT";

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to count tenants.", e);
        }

        return 0;
    }

    public AdminAccountRowDTO getManagerById(int id) {
        String sql = """
                SELECT
                    staff_id,
                    full_name,
                    email,
                    phone_number,
                    identity_code,
                    date_of_birth,
                    gender,
                    status,
                    created_at
                FROM STAFF
                WHERE staff_id = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AdminAccountRowDTO dto = new AdminAccountRowDTO();
                    dto.setAccountType("STAFF");
                    dto.setAccountId(rs.getInt("staff_id"));
                    dto.setFullName(rs.getString("full_name"));
                    dto.setEmail(rs.getString("email"));
                    dto.setPhone(rs.getString("phone_number"));
                    dto.setIdentityCode(rs.getString("identity_code"));
                    dto.setDateOfBirth(rs.getDate("date_of_birth"));
                    dto.setGender(rs.getInt("gender"));
                    dto.setStatus(rs.getString("status"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));

                    return dto;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to get manager by id=%d", id),
                    e);
        }

        return null;
    }

    /**
     * Hàm update Manager
     */
    public boolean updateManager(int id, String name, String email,
            String phone, int gender, String dob, String identity, String status) {

        String sql = """
                UPDATE STAFF
                SET full_name = ?,
                    email = ?,
                    phone_number = ?,
                    gender = ?,
                    date_of_birth = ?,
                    identity_code = ?,
                    status = ?,
                    updated_at = GETDATE()
                WHERE staff_id = ?
                  AND staff_role = 'MANAGER'
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, gender);
            ps.setDate(5, java.sql.Date.valueOf(dob));
            ps.setString(6, identity);
            ps.setString(7, status);
            ps.setInt(8, id);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to update manager. id=%d, email=%s", id, email),
                    e);
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.WARNING,
                    String.format("Invalid manager date format. id=%d, dob=%s", id, dob),
                    e);
        }

        return false;
    }

    public Tenant getTenantById(int id) {
        String sql = "SELECT * FROM TENANT WHERE tenant_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

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
                    t.setGender(rs.getInt("gender"));
                    t.setAccountStatus(rs.getString("account_status"));

                    return t;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to get tenant by id=%d", id),
                    e);
        }

        return null;
    }

    /**
     * Update Tenant
     */
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
                    account_status = ?,
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
            ps.setString(8, t.getAccountStatus());
            ps.setInt(9, t.getTenantId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to update tenant. tenantId=%d, email=%s", t.getTenantId(), t.getEmail()), e);
        }

        return false;
    }
}