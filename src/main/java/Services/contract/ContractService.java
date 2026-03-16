package Services.contract;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import DALs.auth.OtpCodeDAO;
import DALs.auth.TenantDAO;
import DALs.contract.ContractDAO;
import DALs.contract.ContractOccupantDAO;
import Models.common.ServiceResult;
import Models.dto.ManagerContractRowDTO;
import Models.entity.Contract;
import Models.entity.Tenant;
import Utils.database.DBContext;
import Utils.mail.MailUtil;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-10
 */
public class ContractService {

    private final TenantDAO tenantDAO = new TenantDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final OtpCodeDAO otpDAO = new OtpCodeDAO();
    private final ContractOccupantDAO contractOccupantDAO = new ContractOccupantDAO();

    //FLOW 1: CREATE CONTRACT + CREATE TENANT (NO ACCOUNT) + OTP
    @SuppressWarnings("UseSpecificCatch")
    public ServiceResult createContractAndTenant(Contract c, Tenant t) {

        //1) Validate: Tenant
        if (t == null) {
            return ServiceResult.fail("Tenant không hợp lệ.");
        }

        String name = safe(t.getFullName());
        String identity = safe(t.getIdentityCode());
        String phone = safe(t.getPhoneNumber());
        String email = safe(t.getEmail());
        String address = safe(t.getAddress());

        if (name.isBlank()) {
            return ServiceResult.fail("Tên tenant không được để trống.");
        }
        if (identity.isBlank()) {
            return ServiceResult.fail("Identity Code tenant không được để trống.");
        }
        if (phone.isBlank()) {
            return ServiceResult.fail("SĐT tenant không được để trống.");
        }
        if (email.isBlank()) {
            return ServiceResult.fail("Email tenant không được để trống.");
        }
        if (address.isBlank()) {
            return ServiceResult.fail("Địa chỉ tenant không được để trống.");
        }

        if (t.getDateOfBirth() == null) {
            return ServiceResult.fail("Ngày sinh tenant không được để trống.");
        }
        if (t.getGender() == null) {
            return ServiceResult.fail("Giới tính tenant không được để trống.");
        }
        if (t.getGender() != 0 && t.getGender() != 1) {
            return ServiceResult.fail("Giới tính tenant không hợp lệ (0/1).");
        }

        String avatar = safe(t.getAvatar());
        if (avatar.isBlank()) {
            return ServiceResult.fail("Avatar tenant không được để trống.");
        }

        // normalize
        t.setFullName(name);
        t.setIdentityCode(identity);
        t.setPhoneNumber(phone);
        t.setEmail(email);
        t.setAddress(address);
        t.setAvatar(avatar);

        //2) Validate: Contract
        ServiceResult vc = validateContractCommon(c);
        if (!vc.isOk()) {
            return vc;
        }

        //3) Transaction
        try (Connection conn = new DBContext().getConnection()) {

            conn.setAutoCommit(false);

            // 1) Check email tồn tại (phone rely DB UNIQUE)
            if (tenantDAO.findByEmail(email) != null) {
                conn.rollback();
                return ServiceResult.fail("Email tenant đã tồn tại trong hệ thống.");
            }

            // 2) Insert tenant PENDING (full fields)
            int tenantId = tenantDAO.insertPendingTenant(conn, t);
            if (tenantId <= 0) {
                conn.rollback();
                return ServiceResult.fail("Không tạo được tenant (PENDING).");
            }

            // 3) Business rule: tenant chỉ có tối đa 1 ACTIVE/PENDING
            // (luồng no-account: tenant mới tạo nên chắc chắn chưa có, nhưng check luôn cho chắc)
            if (existsActiveOrPendingByTenant(conn, tenantId)) {
                conn.rollback();
                return ServiceResult.fail("Tenant này đang có hợp đồng ACTIVE/PENDING. Không thể tạo thêm.");
            }

            // 4) Insert contract PENDING
            c.setTenantId(tenantId);
            int contractId = contractDAO.insertPendingContract(conn, c);
            if (contractId <= 0) {
                conn.rollback();
                return ServiceResult.fail("Không tạo được contract (PENDING).");
            }

            // 4.1) Insert PRIMARY occupant
            int primaryOccupantId = contractOccupantDAO.insertPrimary(conn, contractId, tenantId, c.getStartDate(), "PENDING");
            if (primaryOccupantId <= 0) {
                conn.rollback();
                return ServiceResult.fail("Không tạo được người ở chính cho hợp đồng.");
            }

            // 5) Generate OTP + insert
            String otp = String.format("%06d", new Random().nextInt(1_000_000));
            otpDAO.insertFirstLoginOtp(conn, tenantId, email, otp);

            // 6) Send mail
            boolean mailOk = MailUtil.sendOtp(email, otp);
            if (!mailOk) {
                conn.rollback();
                return ServiceResult.fail("Gửi OTP thất bại. Vui lòng kiểm tra cấu hình mail.");
            }

            conn.commit();
            return ServiceResult.ok("Tạo contract + tenant PENDING và gửi OTP thành công.");

        } catch (SQLException e) {
            return ServiceResult.fail(mapSqlErrorToUi(e));
        } catch (Exception e) {
            return ServiceResult.fail("Lỗi hệ thống: " + (e.getMessage() == null ? "UNKNOWN" : e.getMessage()));
        }
    }

    //FLOW 2: CREATE CONTRACT FOR EXISTING TENANT (HAS ACCOUNT)
    //block: tenant chỉ được có 1 ACTIVE hoặc 1 PENDING (cùng lúc)
    @SuppressWarnings("UseSpecificCatch")
    public ServiceResult createContractForExistingTenant(Contract c, int tenantId) {

        if (tenantId <= 0) {
            return ServiceResult.fail("Tenant không hợp lệ.");
        }

        // Validate contract fields common
        ServiceResult vc = validateContractCommon(c);
        if (!vc.isOk()) {
            return vc;
        }

        // set tenantId vào contract
        c.setTenantId(tenantId);

        try (Connection conn = new DBContext().getConnection()) {

            conn.setAutoCommit(false);

            // 1) Check tenant có tồn tại + ACTIVE (đúng list dropdown)
            Tenant t = tenantDAO.findById(tenantId);
            if (t == null) {
                conn.rollback();
                return ServiceResult.fail("Tenant không tồn tại.");
            }
            if (t.getAccountStatus() == null || !"ACTIVE".equalsIgnoreCase(t.getAccountStatus())) {
                conn.rollback();
                return ServiceResult.fail("Tenant chưa ACTIVE. Không thể tạo hợp đồng theo luồng has-account.");
            }

            // 2) Chặn tenant đã có ACTIVE hoặc PENDING
            if (existsActiveOrPendingByTenant(conn, tenantId)) {
                conn.rollback();
                return ServiceResult.fail("Tenant này đang có hợp đồng ACTIVE/PENDING. Không thể tạo thêm.");
            }

            // 3) Insert contract PENDING
            int newId = contractDAO.insertPendingContract(conn, c);
            if (newId <= 0) {
                conn.rollback();
                return ServiceResult.fail("Không tạo được contract (PENDING).");
            }

            // 3.1) Insert PRIMARY occupant
            int primaryOccupantId = contractOccupantDAO.insertPrimary(conn, newId, tenantId, c.getStartDate(), "PENDING");
            if (primaryOccupantId <= 0) {
                conn.rollback();
                return ServiceResult.fail("Không tạo được người ở chính cho hợp đồng.");
            }

            conn.commit();
            return ServiceResult.ok("Tạo contract (PENDING) cho tenant có account thành công.");

        } catch (SQLException e) {
            return ServiceResult.fail(mapSqlErrorToUi(e));
        } catch (Exception e) {
            return ServiceResult.fail("Lỗi hệ thống: " + (e.getMessage() == null ? "UNKNOWN" : e.getMessage()));
        }
    }

    //VALIDATION HELPERS
    private ServiceResult validateContractCommon(Contract c) {
        if (c == null) {
            return ServiceResult.fail("Contract không hợp lệ.");
        }
        if (c.getRoomId() <= 0) {
            return ServiceResult.fail("Room không hợp lệ.");
        }
        if (c.getCreatedByStaffId() <= 0) {
            return ServiceResult.fail("Staff không hợp lệ.");
        }

        if (c.getMonthlyRent() == null) {
            return ServiceResult.fail("Monthly rent không được để trống.");
        }
        if (c.getDeposit() == null) {
            return ServiceResult.fail("Deposit không được để trống.");
        }
        if (c.getMonthlyRent().signum() < 0) {
            return ServiceResult.fail("Monthly rent không hợp lệ (>= 0).");
        }
        if (c.getDeposit().signum() < 0) {
            return ServiceResult.fail("Deposit không hợp lệ (>= 0).");
        }

        if (c.getStartDate() == null) {
            return ServiceResult.fail("Start date không được để trống.");
        }
        if (c.getEndDate() == null) {
            return ServiceResult.fail("End date không được để trống.");
        }
        if (!c.getEndDate().after(c.getStartDate())) {
            return ServiceResult.fail("End date phải lớn hơn start date.");
        }

        String qr = safe(c.getPaymentQrData());
        if (qr.isBlank()) {
            return ServiceResult.fail("Payment QR data không được để trống.");
        }
        c.setPaymentQrData(qr);

        return ServiceResult.ok("OK");
    }

    /**
     * Check business rule: tenant chỉ được có tối đa 1 ACTIVE hoặc 1 PENDING
     * tại 1 thời điểm.
     */
    private boolean existsActiveOrPendingByTenant(Connection conn, int tenantId) throws SQLException {
        String sql = """
            SELECT TOP 1 1
            FROM CONTRACT
            WHERE tenant_id = ?
              AND [status] IN ('ACTIVE','PENDING')
        """;
        try (var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private String mapSqlErrorToUi(SQLException e) {

        String m = (e.getMessage() == null) ? "" : e.getMessage().toLowerCase();

        // UNIQUE email/phone
        if (m.contains("uq_tenant_email") || (m.contains("unique") && m.contains("email"))) {
            return "Email tenant đã tồn tại (trùng dữ liệu).";
        }
        if (m.contains("uq_tenant_phone") || (m.contains("unique") && m.contains("phone"))) {
            return "Số điện thoại tenant đã tồn tại (trùng dữ liệu).";
        }

        // room chỉ được có 1 contract ACTIVE, 1 contract PENDING
        if (m.contains("ux_contract_room_only_active")) {
            return "Phòng này đã có hợp đồng ACTIVE rồi.";
        }
        if (m.contains("ux_contract_room_only_pending")) {
            return "Phòng này đã có hợp đồng PENDING rồi.";
        }

        // check end_date > start_date
        if (m.contains("ck_contract_end_after_start")) {
            return "Ngày kết thúc phải lớn hơn ngày bắt đầu.";
        }

        // money nonnegative
        if (m.contains("ck_contract_money_nonnegative")) {
            return "Tiền thuê / tiền cọc không được âm.";
        }

        // connection closed / db down
        if (m.contains("connection is closed")) {
            return "Kết nối DB đã bị đóng. Vui lòng restart server hoặc kiểm tra DBContext.";
        }

        if (m.contains("ux_contract_occupant_contract_tenant")) {
            return "Người này đã tồn tại trong danh sách ở cùng của hợp đồng.";
        }

        if (m.contains("ux_contract_occupant_one_primary_per_contract")) {
            return "Hợp đồng này đã có người ở chính.";
        }

        return "Lỗi SQL: " + e.getMessage();
    }
    //VIEW CONTRACT LIST

    public int countContracts(String keyword, String status) {
        return contractDAO.countManagerContracts(keyword, status);
    }

    public List<ManagerContractRowDTO> findContracts(
            String keyword,
            String status,
            int page,
            int pageSize
    ) {
        return contractDAO.findManagerContracts(keyword, status, page, pageSize);
    }
}
