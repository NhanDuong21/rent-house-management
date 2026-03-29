package DALs.auth;

import Models.entity.OtpCode;
import Utils.database.DBContext;
import Utils.security.HashUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-11
 */
public class OtpCodeDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(OtpCodeDAO.class.getName());

    public void invalidateOldOtps(int tenantId, String purpose) {
        String sql = """
                UPDATE OTP_CODE
                SET used_at = SYSDATETIME()
                WHERE tenant_id = ?
                  AND purpose = ?
                  AND used_at IS NULL
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.setString(2, purpose);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to invalidate old OTPs. tenantId=%d, purpose=%s", tenantId, purpose),
                    e);
        }
    }

    public int insertOtp(int tenantId, String purpose, String receiver, String otpHash, LocalDateTime expiresAt) {
        String sql = """
                INSERT INTO OTP_CODE (tenant_id, purpose, receiver, otp_hash, expires_at, used_at)
                VALUES (?, ?, ?, ?, ?, NULL)
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, tenantId);
            ps.setString(2, purpose);
            ps.setString(3, receiver);
            ps.setString(4, otpHash);
            ps.setTimestamp(5, Timestamp.valueOf(expiresAt));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to insert OTP. tenantId=%d, purpose=%s, receiver=%s",
                            tenantId, purpose, receiver),
                    e);
        }

        return -1;
    }

    /**
     * Tìm OTP mới nhất còn hiệu lực để verify
     */
    public OtpCode findValidLatestOtp(int tenantId, String purpose) {
        String sql = """
                SELECT TOP 1 otp_id, tenant_id, purpose, receiver, otp_hash, expires_at, used_at
                FROM OTP_CODE
                WHERE tenant_id = ?
                  AND purpose = ?
                  AND used_at IS NULL
                  AND expires_at > SYSDATETIME()
                ORDER BY otp_id DESC
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.setString(2, purpose);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    OtpCode o = new OtpCode();
                    o.setOtpId(rs.getInt("otp_id"));
                    o.setTenantId(rs.getInt("tenant_id"));
                    o.setPurpose(rs.getString("purpose"));
                    o.setReceiver(rs.getString("receiver"));
                    o.setOtpHash(rs.getString("otp_hash"));
                    o.setExpiresAt(rs.getTimestamp("expires_at"));
                    o.setUsedAt(rs.getTimestamp("used_at"));
                    return o;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to find latest valid OTP. tenantId=%d, purpose=%s", tenantId, purpose),
                    e);
        }

        return null;
    }

    /**
     * Disable OTP khi đã sử dụng
     */
    public boolean markUsed(int otpId) {
        String sql = """
                UPDATE OTP_CODE
                SET used_at = SYSDATETIME()
                WHERE otp_id = ?
                  AND used_at IS NULL
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, otpId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to mark OTP as used. otpId=%d", otpId),
                    e);
        }

        return false;
    }

    /**
     * Login lần đầu với OTP
     */
    public void insertFirstLoginOtp(Connection conn, int tenantId, String email, String otpPlain)
            throws SQLException {

        String sql = """
                INSERT INTO OTP_CODE (tenant_id, purpose, receiver, otp_hash, expires_at, used_at)
                VALUES (?, 'FIRST_LOGIN', ?, ?, DATEADD(MINUTE, 10, SYSDATETIME()), NULL)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.setString(2, email);
            ps.setString(3, HashUtil.md5(otpPlain));
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to insert first login OTP. tenantId=%d, email=%s", tenantId, email),
                    e);
            throw e;
        }
    }
}