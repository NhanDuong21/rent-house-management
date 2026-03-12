package Services.auth;

import DALs.auth.OtpCodeDAO;
import DALs.auth.StaffDAO;
import DALs.auth.TenantDAO;
import Models.authentication.AuthResult;
import Models.entity.OtpCode;
import Models.entity.Staff;
import Models.entity.Tenant;
import Utils.security.HashUtil;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-05
 */
public class AuthService {

    private final StaffDAO staffDAO = new StaffDAO();
    private final TenantDAO tenantDAO = new TenantDAO();
    private final OtpCodeDAO otpDAO = new OtpCodeDAO();

    public AuthResult login(String email, String rawPass) {
        if (email == null || rawPass == null) {
            return null;
        }
        //check staff
        Staff staff = staffDAO.findByEmail(email.trim());
        if (staff != null) {
            if (!"ACTIVE".equalsIgnoreCase(staff.getStatus())) {
                return null;
            }
            if (staff.getPasswordHash() == null) {
                return null;
            }
            if (!HashUtil.md5(rawPass).equalsIgnoreCase(staff.getPasswordHash())) {
                return null;
            }
            String role = staff.getStaffRole();
            return new AuthResult(role, null, staff);
        }

        //check tenant
        Tenant tenant = tenantDAO.findByEmail(email.trim());
        if (tenant == null) {
            return null;
        }

        if (tenant.getPasswordHash() == null) {
            return null;
        }

        if ("LOCKED".equalsIgnoreCase(tenant.getAccountStatus())) {
            return null;
        }

        if (!"ACTIVE".equalsIgnoreCase(tenant.getAccountStatus()) && !"PENDING".equalsIgnoreCase(tenant.getAccountStatus())) {
            return null;
        }

        if (!HashUtil.md5(rawPass).equalsIgnoreCase(tenant.getPasswordHash())) {
            return null;
        }
        return new AuthResult("TENANT", tenant, null);
    }

    public AuthResult loginByOtp(String email, String otpPlain) {
        Tenant t = tenantDAO.findByEmail(email.trim());
        if (t == null) {
            return null;
        }

        // LOCKED không cho login
        if ("LOCKED".equalsIgnoreCase(t.getAccountStatus())) {
            return null;
        }

        // chỉ cho OTP login nếu tenant chưa set password hoặc đang PENDING
        boolean noPassword = (t.getPasswordHash() == null || t.getPasswordHash().isBlank());
        boolean isPending = "PENDING".equalsIgnoreCase(t.getAccountStatus());
        if (!noPassword && !isPending) {
            return null;
        }

        OtpCode otp = otpDAO.findValidLatestOtp(t.getTenantId(), "FIRST_LOGIN");
        if (otp == null) {
            return null;
        }

        String inputHash = HashUtil.md5(otpPlain);
        if (!inputHash.equalsIgnoreCase(otp.getOtpHash())) {
            return null;
        }

        otpDAO.markUsed(otp.getOtpId());

        return new AuthResult("TENANT", t, null);
    }

    /**
     * Hàm mới độc lập — dùng cho forgot password flow (cả Tenant lẫn Staff).
     * Không đụng loginByOtp cũ.
     *
     * Tenant dùng purpose: RESET_PASSWORD
     * Staff  dùng purpose: RESET_PASSWORD_STAFF
     */
    public AuthResult loginByResetOtp(String email, String otpPlain) {
        if (email == null || otpPlain == null) return null;
        email = email.trim();

        // ── Thử Tenant ──────────────────────────────────────────────────────────
        Tenant t = tenantDAO.findByEmail(email);
        if (t != null) {
            if ("LOCKED".equalsIgnoreCase(t.getAccountStatus())) return null;

            OtpCode otp = otpDAO.findValidLatestOtp(t.getTenantId(), "RESET_PASSWORD");
            if (otp == null) return null;

            if (!HashUtil.md5(otpPlain).equalsIgnoreCase(otp.getOtpHash())) return null;

            otpDAO.markUsed(otp.getOtpId());
            return new AuthResult("TENANT", t, null);
        }

        // ── Thử Staff (Admin / Manager) ─────────────────────────────────────────
        Staff s = staffDAO.findByEmail(email);
        if (s != null) {
            if (!"ACTIVE".equalsIgnoreCase(s.getStatus())) return null;

            OtpCode otp = otpDAO.findValidLatestOtp(s.getStaffId(), "RESET_PASSWORD_STAFF");
            if (otp == null) return null;

            if (!HashUtil.md5(otpPlain).equalsIgnoreCase(otp.getOtpHash())) return null;

            otpDAO.markUsed(otp.getOtpId());
            return new AuthResult(s.getStaffRole(), null, s); // ADMIN hoặc MANAGER
        }

        return null;
    }
}