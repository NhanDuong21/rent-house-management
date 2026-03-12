package Services.auth;

import DALs.auth.OtpCodeDAO;
import DALs.auth.TenantDAO;
import DALs.auth.StaffDAO;
import Models.entity.OtpCode;
import Utils.mail.MailUtil;
import Utils.security.HashUtil;
import Utils.security.OtpUtil;

import java.time.LocalDateTime;

/**
 * Service xử lý Forgot Password flow.
 *
 * Trả về enum-style String để controller phân biệt rõ từng trường hợp:
 *   "SENT"           → tìm thấy email, gửi OTP thành công
 *   "EMAIL_NOT_FOUND" → email không tồn tại trong DB
 *   "MAIL_FAILED"    → tìm thấy email nhưng gửi mail thất bại
 *
 * @author Duong Thien Nhan - CE190741
 */
public class ForgotPasswordService {

    private final OtpCodeDAO otpDAO    = new OtpCodeDAO();
    private final TenantDAO  tenantDAO = new TenantDAO();
    private final StaffDAO   staffDAO  = new StaffDAO();

    // ─── Bước 1: tìm email → gửi OTP ───────────────────────────────────────────

    public String sendForgotPasswordOtp(String email) {
        if (email == null || email.isBlank()) return "EMAIL_NOT_FOUND";

        // Tìm Tenant trước
        Integer tenantId = tenantDAO.findTenantIdByEmail(email);
        if (tenantId != null) {
            // Dùng tenantId — insert vào OTP_CODE bình thường (cột tenant_id)
            boolean sent = sendOtpFor(tenantId, email, "RESET_PASSWORD", false);
            return sent ? "SENT" : "MAIL_FAILED";
        }

        // Tìm Staff
        Integer staffId = staffDAO.findStaffIdByEmail(email);
        if (staffId != null) {
            
            boolean sent = sendOtpFor(staffId, email, "RESET_PASSWORD_STAFF", true);
            return sent ? "SENT" : "MAIL_FAILED";
        }

        return "EMAIL_NOT_FOUND";
    }

    // ─── Bước 2: verify OTP ─────────────────────────────────────────────────────

    public boolean verifyForgotPasswordOtp(String email, String otpInput) {
        if (email == null || otpInput == null || email.isBlank() || otpInput.isBlank()) return false;

        // Thử Tenant
        Integer tenantId = tenantDAO.findTenantIdByEmail(email);
        if (tenantId != null) {
            return doVerify(tenantId, "RESET_PASSWORD", otpInput);
        }

        // Thử Staff
        Integer staffId = staffDAO.findStaffIdByEmail(email);
        if (staffId != null) {
            return doVerify(staffId, "RESET_PASSWORD_STAFF", otpInput);
        }

        return false;
    }

    // ─── helpers ────────────────────────────────────────────────────────────────

    /**
     * @param isStaff true → dùng purpose RESET_PASSWORD_STAFF để tránh FK conflict nếu có
     */
    private boolean sendOtpFor(int userId, String email, String purpose, boolean isStaff) {
        String otpPlain = OtpUtil.generate6Digits();
        String otpHash  = HashUtil.md5(otpPlain);

        try {
            otpDAO.invalidateOldOtps(userId, purpose);

            int inserted = otpDAO.insertOtp(
                    userId,
                    purpose,
                    email,
                    otpHash,
                    LocalDateTime.now().plusMinutes(10)
            );

            if (inserted <= 0) {
                System.err.println("[ForgotPassword] insertOtp failed for userId=" + userId + " email=" + email);
                return false;
            }

            boolean mailSent = MailUtil.sendOtp(email, otpPlain);
            if (!mailSent) {
                System.err.println("[ForgotPassword] MailUtil.sendOtp failed for email=" + email);
            }
            return mailSent;

        } catch (Exception e) {
            System.err.println("[ForgotPassword] Exception in sendOtpFor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private boolean doVerify(int userId, String purpose, String otpInput) {
        OtpCode otp = otpDAO.findValidLatestOtp(userId, purpose);
        if (otp == null) return false;

        String inputHash = HashUtil.md5(otpInput.trim());
        if (!inputHash.equalsIgnoreCase(otp.getOtpHash())) return false;

        otpDAO.markUsed(otp.getOtpId());
        return true;
    }
}