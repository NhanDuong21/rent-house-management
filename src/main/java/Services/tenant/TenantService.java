package Services.tenant;

import DALs.auth.TenantDAO;
import Models.common.ServiceResult;
import Models.entity.Tenant;
import java.util.List;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-26
 */
public class TenantService {

    private final TenantDAO tenantDAO = new TenantDAO();

    public ServiceResult updatePhone(int tenantId, String phoneRaw) {

        Tenant t = tenantDAO.findById(tenantId);
        if (t == null) {
            return ServiceResult.fail("NOT_FOUND");
        }

        if (!"ACTIVE".equalsIgnoreCase(t.getAccountStatus())) {
            return ServiceResult.fail("TENANT_NOT_ACTIVE");
        }

        String phone = (phoneRaw == null) ? "" : phoneRaw.trim();
        phone = phone.replaceAll("[\\s.]", "");

        if (phone.isBlank()) {
            return ServiceResult.fail("PHONE_REQUIRED");
        }

        // bắt đầu từ 0 và theo sau là 9 hoặc 10 số
        if (!phone.matches("^0\\d{9,10}$")) {
            return ServiceResult.fail("PHONE_FORMAT");
        }

        //trung thi khoi update ton tai nguyen
        if (phone.equals(t.getPhoneNumber())) {
            return ServiceResult.ok("OK");
        }

        if (tenantDAO.existsPhoneExceptTenant(tenantId, phone)) {
            return ServiceResult.fail("PHONE_EXISTS");
        }

        boolean ok = tenantDAO.updatePhoneForTenant(tenantId, phone);
        return ok ? ServiceResult.ok("OK") : ServiceResult.fail("UPDATE_FAILED");
    }

    public List<Tenant> getAllTenants() {
        return tenantDAO.getAllTenants();
    }

    public List<Tenant> searchTenant(String keyword) {
        return tenantDAO.searchTenant(keyword);
    }

    public List<Tenant> getTenantsPaged(int page, int pageSize) {
        return tenantDAO.getTenantsPaged(page, pageSize);
    }

    public int countAllTenants() {
        return tenantDAO.countAllTenants();
    }

    public List<Tenant> searchTenantPaged(String keyword, int page, int pageSize) {
        return tenantDAO.searchTenantPaged(keyword, page, pageSize);
    }

    public int countSearchTenant(String keyword) {
        return tenantDAO.countSearchTenant(keyword);
    }

    public Tenant findById(int id) {
        return tenantDAO.findById(id);
    }

    public ServiceResult toggleStatus(int tenantId) {
        Tenant t = tenantDAO.findById(tenantId);
        if (t == null) {
            return ServiceResult.fail("NOT_FOUND");
        }
        String current = t.getAccountStatus();
        String next = "ACTIVE".equalsIgnoreCase(current) ? "LOCKED" : "ACTIVE";
        boolean ok = tenantDAO.toggleStatus(tenantId, next);
        return ok ? ServiceResult.ok(next) : ServiceResult.fail("UPDATE_FAILED");
    }

    public boolean updateTenant(Tenant t) {
        validateTenant(t);
        return tenantDAO.updateTenant(t);
    }
    // ===== VALIDATION =====

    private void validateTenant(Tenant t) {

        // 1. Full Name
        String fullName = t.getFullName();
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full Name không được để trống.");
        }
        if (!fullName.matches("^[\\p{L}\\s]+$")) {
            throw new IllegalArgumentException("Full Name chỉ được chứa chữ cái.");
        }

        // 2. Identity Code
        String identityCode = t.getIdentityCode();
        if (identityCode == null || identityCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Citizen ID không được để trống.");
        }
        if (!identityCode.matches("^\\d{12}$")) {
            throw new IllegalArgumentException("Citizen ID phải đúng 12 chữ số.");
        }

        // 3. Phone Number
        String phone = t.getPhoneNumber();
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone Number không được để trống.");
        }

        // Không cho nhập chữ
        if (!phone.matches("^\\d+$")) {
            throw new IllegalArgumentException("Phone Number không được chứa chữ hoặc ký tự đặc biệt.");
        }

        // Phải đủ 10 số và bắt đầu bằng 0
        if (!phone.matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Phone Number phải có 10 số và bắt đầu bằng 0.");
        }

        // 4. Email
        String email = t.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }

        // Kiểm tra format email
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }

        // 5. Date of Birth
        if (t.getDateOfBirth() == null) {
            throw new IllegalArgumentException("Date of Birth không được để trống.");
        }

        java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
        if (t.getDateOfBirth().after(today)) {
            throw new IllegalArgumentException("Date of Birth không được lớn hơn ngày hiện tại.");
        }
    }

}