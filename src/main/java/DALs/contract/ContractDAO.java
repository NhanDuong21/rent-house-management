package DALs.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import DALs.payment.PaymentDAO;
import Models.common.ServiceResult;
import Models.dto.ManagerContractRowDTO;
import Models.dto.TenantMyRoomDTO;
import Models.entity.Contract;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-11
 */
public class ContractDAO extends DBContext {

    public int insertPendingContract(Connection conn, Contract c) throws SQLException {
        String sql = """
                INSERT INTO CONTRACT (room_id, tenant_id, created_by_staff_id, start_date, end_date, monthly_rent, deposit, payment_qr_data, status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, c.getRoomId());
            ps.setInt(2, c.getTenantId());
            ps.setInt(3, c.getCreatedByStaffId());
            ps.setDate(4, c.getStartDate());
            ps.setDate(5, c.getEndDate());
            ps.setBigDecimal(6, c.getMonthlyRent());
            ps.setBigDecimal(7, c.getDeposit());
            ps.setString(8, c.getPaymentQrData());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    //get list manage contract
    @SuppressWarnings("CallToPrintStackTrace")
    public List<ManagerContractRowDTO> getManagerContracts() {
        List<ManagerContractRowDTO> list = new ArrayList<>();

        String sql = """
SELECT CONTRACT.contract_id, ROOM.room_number, TENANT.full_name as tenant_name , CONTRACT.start_date, CONTRACT.monthly_rent, CONTRACT.status
FROM     CONTRACT INNER JOIN
                  ROOM ON CONTRACT.room_id = ROOM.room_id INNER JOIN
                  TENANT ON CONTRACT.tenant_id = TENANT.tenant_id
            ORDER BY CONTRACT.created_at DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                ManagerContractRowDTO contractRowDTO = new ManagerContractRowDTO();
                contractRowDTO.setContractId(rs.getInt("contract_id"));
                contractRowDTO.setRoomNumber(rs.getString("room_number"));
                contractRowDTO.setTenantName(rs.getString("tenant_name"));
                contractRowDTO.setStartDate(rs.getDate("start_date"));
                contractRowDTO.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
                contractRowDTO.setStatus(rs.getString("status"));
                list.add(contractRowDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //nếu m có 100 hợp đồng và mỗi trang hiển thị 10 cái, 
    //m cần con số 100 này để vẽ ra các nút chuyển trang 1, 2, 3, ..., 10 trên UI. 
    //nếu không m sẽ không biết khi nào thì hết dữ liệu để dừng phân trang.
    @SuppressWarnings("CallToPrintStackTrace")
    public int countManagerContracts(String keyword, String status) {
        String sql = """
        SELECT COUNT(*)
        FROM CONTRACT c
        JOIN ROOM r ON c.room_id = r.room_id
        JOIN TENANT t ON c.tenant_id = t.tenant_id
        WHERE 1=1
          AND (
                ? IS NULL OR ? = '' OR
                CAST(c.contract_id AS NVARCHAR(20)) LIKE '%' + ? + '%' OR
                r.room_number LIKE '%' + ? + '%' OR
                t.full_name LIKE '%' + ? + '%'
              )
          AND (
                ? IS NULL OR ? = '' OR
                c.status = ?
              )
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String k = (keyword == null) ? "" : keyword.trim();
            String s = (status == null) ? "" : status.trim();

            int i = 1;
            ps.setString(i++, k);
            ps.setString(i++, k);
            ps.setString(i++, k);
            ps.setString(i++, k);
            ps.setString(i++, k);

            ps.setString(i++, s);
            ps.setString(i++, s);
            ps.setString(i++, s);

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

    @SuppressWarnings("CallToPrintStackTrace")
    public List<ManagerContractRowDTO> findManagerContracts(String keyword, String status, int page, int pageSize) {
        List<ManagerContractRowDTO> list = new ArrayList<>();

        if (page <= 0) {
            page = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        int offset = (page - 1) * pageSize;

        String sql = """
        SELECT c.contract_id, r.room_number, t.full_name as tenant_name,
               c.start_date, c.monthly_rent, c.status
        FROM CONTRACT c
        JOIN ROOM r ON c.room_id = r.room_id
        JOIN TENANT t ON c.tenant_id = t.tenant_id
        WHERE 1=1
          AND (
                ? IS NULL OR ? = '' OR
                CAST(c.contract_id AS NVARCHAR(20)) LIKE '%' + ? + '%' OR
                r.room_number LIKE '%' + ? + '%' OR
                t.full_name LIKE '%' + ? + '%'
              )
          AND (
                ? IS NULL OR ? = '' OR
                c.status = ?
              )
        ORDER BY c.created_at DESC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            String k = (keyword == null) ? "" : keyword.trim();
            String s = (status == null) ? "" : status.trim();

            int i = 1;
            ps.setString(i++, k);
            ps.setString(i++, k);
            ps.setString(i++, k);
            ps.setString(i++, k);
            ps.setString(i++, k);

            ps.setString(i++, s);
            ps.setString(i++, s);
            ps.setString(i++, s);

            ps.setInt(i++, offset);
            ps.setInt(i++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ManagerContractRowDTO dto = new ManagerContractRowDTO();
                    dto.setContractId(rs.getInt("contract_id"));
                    dto.setRoomNumber(rs.getString("room_number"));
                    dto.setTenantName(rs.getString("tenant_name"));
                    dto.setStartDate(rs.getDate("start_date"));
                    dto.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
                    dto.setStatus(rs.getString("status"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //get list contract theo tenantId
    //sort theo status, pending gan 0,active gan 1, uu tien pending len dau
    @SuppressWarnings("CallToPrintStackTrace")
    public List<Contract> findByTenantId(int tenantId) {
        List<Contract> list = new ArrayList<>();

        String sql = """
        SELECT
            c.contract_id, c.room_id, c.tenant_id, c.created_by_staff_id, 
            c.start_date, c.end_date, c.monthly_rent, c.deposit, 
            c.payment_qr_data, c.status, c.created_at, c.updated_at, r.room_number, b.block_name 
        FROM CONTRACT c
        JOIN ROOM r ON c.room_id = r.room_id
        LEFT JOIN BLOCK b ON r.block_id = b.block_id
        WHERE c.tenant_id = ?
        ORDER BY
            CASE c.status WHEN 'PENDING' THEN 0 WHEN 'ACTIVE' THEN 1 ELSE 2 END, c.created_at DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Contract c = new Contract();
                    c.setContractId(rs.getInt("contract_id"));
                    c.setRoomId(rs.getInt("room_id"));
                    c.setTenantId(rs.getInt("tenant_id"));
                    c.setCreatedByStaffId(rs.getInt("created_by_staff_id"));
                    c.setStartDate(rs.getDate("start_date"));
                    c.setEndDate(rs.getDate("end_date"));
                    c.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
                    c.setDeposit(rs.getBigDecimal("deposit"));
                    c.setPaymentQrData(rs.getString("payment_qr_data"));
                    c.setStatus(rs.getString("status"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    c.setUpdatedAt(rs.getTimestamp("updated_at"));
                    c.setRoomNumber(rs.getString("room_number"));
                    c.setBlockName(rs.getString("block_name"));

                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    //get contract theo id de tenant ko xem contract cua ngkhac
    @SuppressWarnings("CallToPrintStackTrace")
    public Contract findByIdForTenant(int contractId, int tenantId) {

        String sql = """
        SELECT
            c.contract_id, c.room_id, c.tenant_id, c.created_by_staff_id,
            c.start_date, c.end_date, c.monthly_rent, c.deposit,
            c.payment_qr_data, c.status, c.created_at, c.updated_at,
            r.room_number, b.block_name
        FROM CONTRACT c
        JOIN ROOM r ON c.room_id = r.room_id
        LEFT JOIN BLOCK b ON r.block_id = b.block_id
        WHERE c.contract_id = ? AND c.tenant_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setInt(2, tenantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract c = new Contract();
                    c.setContractId(rs.getInt("contract_id"));
                    c.setRoomId(rs.getInt("room_id"));
                    c.setTenantId(rs.getInt("tenant_id"));
                    c.setCreatedByStaffId(rs.getInt("created_by_staff_id"));
                    c.setStartDate(rs.getDate("start_date"));
                    c.setEndDate(rs.getDate("end_date"));
                    c.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
                    c.setDeposit(rs.getBigDecimal("deposit"));
                    c.setPaymentQrData(rs.getString("payment_qr_data"));
                    c.setStatus(rs.getString("status"));
                    c.setCreatedAt(rs.getTimestamp("created_at"));
                    c.setUpdatedAt(rs.getTimestamp("updated_at"));
                    c.setRoomNumber(rs.getString("room_number"));
                    c.setBlockName(rs.getString("block_name"));
                    return c;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Contract findDetailForTenant(int contractId, int tenantId) {

        String sql = """
        SELECT
            c.contract_id, c.room_id, c.tenant_id, c.created_by_staff_id,
            c.start_date, c.end_date, c.monthly_rent, c.deposit,
            c.payment_qr_data, c.status, c.created_at, c.updated_at,

            r.room_number, r.floor, r.area, r.max_tenants,
            r.is_mezzanine, r.has_air_conditioning, r.[description] AS room_description,
            b.block_name,

            t.full_name AS tenant_name,
            t.email AS tenant_email,
            t.phone_number AS tenant_phone,
            t.identity_code AS tenant_identity,
            t.date_of_birth AS tenant_dob,
            t.[address] AS tenant_address,

            a.full_name AS landlord_name,
            a.phone_number AS landlord_phone,
            a.email AS landlord_email,
            a.identity_code AS landlord_identity,
            a.date_of_birth AS landlord_dob

        FROM CONTRACT c
        JOIN ROOM r ON c.room_id = r.room_id
        LEFT JOIN BLOCK b ON r.block_id = b.block_id
        JOIN TENANT t ON c.tenant_id = t.tenant_id

        CROSS APPLY (
            SELECT TOP 1 *
            FROM STAFF
            WHERE staff_role = 'ADMIN' AND [status] = 'ACTIVE'
            ORDER BY staff_id ASC
        ) a

        WHERE c.contract_id = ? AND c.tenant_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setInt(2, tenantId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Contract c = new Contract();

                //contract
                c.setContractId(rs.getInt("contract_id"));
                c.setRoomId(rs.getInt("room_id"));
                c.setTenantId(rs.getInt("tenant_id"));
                c.setCreatedByStaffId(rs.getInt("created_by_staff_id"));
                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));
                c.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
                c.setDeposit(rs.getBigDecimal("deposit"));
                c.setPaymentQrData(rs.getString("payment_qr_data"));
                c.setStatus(rs.getString("status"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setUpdatedAt(rs.getTimestamp("updated_at"));

                //room && block
                c.setRoomNumber(rs.getString("room_number"));
                c.setBlockName(rs.getString("block_name"));
                c.setFloor((Integer) rs.getObject("floor"));
                c.setArea(rs.getBigDecimal("area"));
                c.setMaxTenants((Integer) rs.getObject("max_tenants"));
                c.setIsMezzanine((Boolean) rs.getObject("is_mezzanine"));
                c.setHasAirConditioning((Boolean) rs.getObject("has_air_conditioning"));
                c.setRoomDescription(rs.getString("room_description"));

                //party B
                c.setTenantName(rs.getString("tenant_name"));
                c.setTenantEmail(rs.getString("tenant_email"));
                c.setTenantPhoneNumber(rs.getString("tenant_phone"));
                c.setTenantIdentityCode(rs.getString("tenant_identity"));
                c.setTenantDateOfBirth(rs.getDate("tenant_dob"));
                c.setTenantAddress(rs.getString("tenant_address"));

                //party A
                c.setLandlordFullName(rs.getString("landlord_name"));
                c.setLandlordPhoneNumber(rs.getString("landlord_phone"));
                c.setLandlordEmail(rs.getString("landlord_email"));
                c.setLandlordIdentityCode(rs.getString("landlord_identity"));
                c.setLandlordDateOfBirth(rs.getDate("landlord_dob"));

                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public Contract findDetailForManager(int contractId) {

        String sql = """
        SELECT
            c.contract_id, c.room_id, c.tenant_id, c.created_by_staff_id,
            c.start_date, c.end_date, c.monthly_rent, c.deposit,
            c.payment_qr_data, c.status, c.created_at, c.updated_at,

            r.room_number, r.floor, r.area, r.max_tenants,
            r.is_mezzanine, r.has_air_conditioning, r.[description] AS room_description,
            b.block_name,

            t.full_name AS tenant_name,
            t.email AS tenant_email,
            t.phone_number AS tenant_phone,
            t.identity_code AS tenant_identity,
            t.date_of_birth AS tenant_dob,
            t.[address] AS tenant_address,

            a.full_name AS landlord_name,
            a.phone_number AS landlord_phone,
            a.email AS landlord_email,
            a.identity_code AS landlord_identity,
            a.date_of_birth AS landlord_dob

        FROM CONTRACT c
        JOIN ROOM r ON c.room_id = r.room_id
        LEFT JOIN BLOCK b ON r.block_id = b.block_id
        JOIN TENANT t ON c.tenant_id = t.tenant_id
        JOIN STAFF cb ON c.created_by_staff_id = cb.staff_id

        CROSS APPLY (
            SELECT TOP 1 *
            FROM STAFF
            WHERE staff_role = 'ADMIN' AND [status] = 'ACTIVE'
            ORDER BY staff_id ASC
        ) a

        WHERE c.contract_id = ?
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                Contract c = new Contract();

                // contract
                c.setContractId(rs.getInt("contract_id"));
                c.setRoomId(rs.getInt("room_id"));
                c.setTenantId(rs.getInt("tenant_id"));
                c.setCreatedByStaffId(rs.getInt("created_by_staff_id"));
                c.setStartDate(rs.getDate("start_date"));
                c.setEndDate(rs.getDate("end_date"));
                c.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
                c.setDeposit(rs.getBigDecimal("deposit"));
                c.setPaymentQrData(rs.getString("payment_qr_data"));
                c.setStatus(rs.getString("status"));
                c.setCreatedAt(rs.getTimestamp("created_at"));
                c.setUpdatedAt(rs.getTimestamp("updated_at"));

                // room & block
                c.setRoomNumber(rs.getString("room_number"));
                c.setBlockName(rs.getString("block_name"));
                c.setFloor((Integer) rs.getObject("floor"));
                c.setArea(rs.getBigDecimal("area"));
                c.setMaxTenants((Integer) rs.getObject("max_tenants"));
                c.setIsMezzanine((Boolean) rs.getObject("is_mezzanine"));
                c.setHasAirConditioning((Boolean) rs.getObject("has_air_conditioning"));
                c.setRoomDescription(rs.getString("room_description"));

                // tenant (party B)
                c.setTenantName(rs.getString("tenant_name"));
                c.setTenantEmail(rs.getString("tenant_email"));
                c.setTenantPhoneNumber(rs.getString("tenant_phone"));
                c.setTenantIdentityCode(rs.getString("tenant_identity"));
                c.setTenantDateOfBirth(rs.getDate("tenant_dob"));
                c.setTenantAddress(rs.getString("tenant_address"));

                // landlord/admin (party A)
                c.setLandlordFullName(rs.getString("landlord_name"));
                c.setLandlordPhoneNumber(rs.getString("landlord_phone"));
                c.setLandlordEmail(rs.getString("landlord_email"));
                c.setLandlordIdentityCode(rs.getString("landlord_identity"));
                c.setLandlordDateOfBirth(rs.getDate("landlord_dob"));

                return c;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateStatus(int contractId, String status) {
        String sql = "UPDATE CONTRACT SET status = ?, updated_at = SYSDATETIME() WHERE contract_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, contractId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //update status contract to ENDED and sign time update to updated_at
    public int expireActiveContracts(Connection conn) throws SQLException {
        String sql = """
        UPDATE CONTRACT
        SET status = 'ENDED',
            updated_at = SYSDATETIME()
        WHERE status = 'ACTIVE'
          AND end_date IS NOT NULL
          AND end_date < CAST(GETDATE() AS date)
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    //update room ve available 
    public int releaseRoomsWithoutActiveContract(Connection conn) throws SQLException {
        String sql = """
        UPDATE ROOM
        SET status = 'AVAILABLE'
        WHERE status = 'OCCUPIED'
          AND room_id IN (
              SELECT r.room_id
              FROM ROOM r
              LEFT JOIN CONTRACT c
                ON c.room_id = r.room_id
               AND c.status = 'ACTIVE'
              WHERE r.status = 'OCCUPIED'
              GROUP BY r.room_id
              HAVING COUNT(c.contract_id) = 0
          )
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            return ps.executeUpdate();
        }
    }

    /**
     * Chạy job expire không throw ra ngoài để tránh làm gãy request. Gọi từ
     * Filter (mỗi X phút).
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public void runExpireJobSafely() {
        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);

            expireActiveContracts(conn);
            releaseRoomsWithoutActiveContract(conn);

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra sự tồn tại của hợp đồng đang hoạt động cho một phòng. Dùng để
     * chặn việc tạo nhiều hợp đồng 'ACTIVE' trên cùng một phòng tại một thời
     * điểm.
     *
     * @param roomId ID của phòng cần kiểm tra
     * @return true nếu phòng đã có người ở (Busy), false nếu phòng trống
     * (Available)
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean existsActiveContractForRoom(int roomId) {

        String sql = """
        SELECT 1
        FROM CONTRACT
        WHERE room_id = ?
          AND status = 'ACTIVE'
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean existsActiveContractForTenant(int tenantId) {
        String sql = "SELECT 1 FROM CONTRACT WHERE tenant_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean existsPendingContractForTenant(int tenantId) {
        String sql = "SELECT 1 FROM CONTRACT WHERE tenant_id = ? AND status = 'PENDING'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * active room id hiện tại của tenant, nếu không có thì trả -1
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public int getActiveRoomIdOfTenant(int tenantId) {
        String sql = "SELECT TOP 1 room_id FROM CONTRACT WHERE tenant_id = ? AND status = 'ACTIVE' ORDER BY contract_id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("room_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * active contract id hiện tại của tenant, nếu không có thì -1
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public int getActiveContractIdOfTenant(int tenantId) {
        String sql = "SELECT TOP 1 contract_id FROM CONTRACT WHERE tenant_id = ? AND status = 'ACTIVE' ORDER BY contract_id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("contract_id");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Terminate contract (ACTIVE/PENDING -> CANCELLED) Rule: - block if
     * contract already ENDED/CANCELLED - block if has pending BANK payment - if
     * terminating ACTIVE: auto cancel any PENDING contracts in same room
     * (renew) - recalc ROOM status: if any ACTIVE or PENDING => OCCUPIED else
     * AVAILABLE - recalc TENANT account_status: ACTIVE if has ACTIVE, else
     * PENDING if has PENDING, else ACTIVE
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public ServiceResult terminateContractByManager(int contractId) {
        String lockSql = """
            SELECT contract_id, room_id, tenant_id, status
            FROM CONTRACT WITH (UPDLOCK, ROWLOCK)
            WHERE contract_id = ?
        """;

        String cancelCurrentSql = """
            UPDATE CONTRACT
            SET status = 'CANCELLED',
                updated_at = SYSDATETIME()
            WHERE contract_id = ?
              AND status IN ('ACTIVE','PENDING')
        """;

        String cancelPendingSameRoomSql = """
            UPDATE CONTRACT
            SET status = 'CANCELLED',
                updated_at = SYSDATETIME()
            WHERE room_id = ?
              AND status = 'PENDING'
        """;

        String roomHasActiveSql = "SELECT TOP 1 1 FROM CONTRACT WHERE room_id = ? AND status = 'ACTIVE'";
        String roomHasPendingSql = "SELECT TOP 1 1 FROM CONTRACT WHERE room_id = ? AND status = 'PENDING'";

        String updateRoomSql = "UPDATE ROOM SET status = ? WHERE room_id = ?";

        String tenantHasActiveSql = "SELECT TOP 1 1 FROM CONTRACT WHERE tenant_id = ? AND status = 'ACTIVE'";
        String tenantHasPendingSql = "SELECT TOP 1 1 FROM CONTRACT WHERE tenant_id = ? AND status = 'PENDING'";
        String updateTenantSql = "UPDATE TENANT SET account_status = ? WHERE tenant_id = ?";

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);

            // 1) Khóa dữ liệu và kiểm tra tồn tại
            // 2) Kiểm tra trạng thái: Chỉ cho phép hủy ACTIVE hoặc PENDING
            // 3) Kiểm tra công nợ: Chặn nếu có giao dịch chuyển khoản chưa xác nhận
            // 4) Thực hiện đổi trạng thái sang CANCELLED
            // 5) Xử lý dây chuyền: Hủy các "hợp đồng nối đuôi" (gia hạn) của cùng phòng
            // 6) Tính toán lại để giải phóng phòng nếu không còn ai thuê
            // 7) Tính toán lại trạng thái người thuê để cập nhật quyền truy cập app
            int roomId;
            int tenantId;
            String oldStatus;

            // 1) lock + load
            try (PreparedStatement ps = conn.prepareStatement(lockSql)) {
                ps.setInt(1, contractId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return ServiceResult.fail("NOT_FOUND");
                    }
                    roomId = rs.getInt("room_id");
                    tenantId = rs.getInt("tenant_id");
                    oldStatus = rs.getString("status");
                }
            }

            // 2) validate status
            if (oldStatus == null
                    || (!oldStatus.equals("ACTIVE") && !oldStatus.equals("PENDING"))) {
                conn.rollback();
                return ServiceResult.fail("NOT_TERMINATABLE");
            }

            // 3) block if pending bank payment exists (reuse đúng logic đang dùng confirm)
            PaymentDAO paymentDAO = new PaymentDAO();
            boolean hasPendingPayment = paymentDAO.hasPendingBankPayment(contractId);
            if (hasPendingPayment) {
                conn.rollback();
                return ServiceResult.fail("HAS_PENDING_PAYMENT");
            }

            // 4) cancel current
            int updated;
            try (PreparedStatement ps = conn.prepareStatement(cancelCurrentSql)) {
                ps.setInt(1, contractId);
                updated = ps.executeUpdate();
            }
            if (updated == 0) {
                conn.rollback();
                return ServiceResult.fail("CANCEL_FAILED");
            }

            // 5) If terminating ACTIVE: cancel PENDING renews in same room
            if (oldStatus.equals("ACTIVE")) {
                try (PreparedStatement ps = conn.prepareStatement(cancelPendingSameRoomSql)) {
                    ps.setInt(1, roomId);
                    ps.executeUpdate();
                }
            }

            // 6) Recalc room status
            boolean roomHasActive;
            try (PreparedStatement ps = conn.prepareStatement(roomHasActiveSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    roomHasActive = rs.next();
                }
            }

            boolean roomHasPending = false;
            if (!roomHasActive) {
                try (PreparedStatement ps = conn.prepareStatement(roomHasPendingSql)) {
                    ps.setInt(1, roomId);
                    try (ResultSet rs = ps.executeQuery()) {
                        roomHasPending = rs.next();
                    }
                }
            }

            String newRoomStatus = (roomHasActive || roomHasPending) ? "OCCUPIED" : "AVAILABLE";
            try (PreparedStatement ps = conn.prepareStatement(updateRoomSql)) {
                ps.setString(1, newRoomStatus);
                ps.setInt(2, roomId);
                ps.executeUpdate();
            }

            // 7) Recalc tenant account_status
            boolean tenantHasActive;
            try (PreparedStatement ps = conn.prepareStatement(tenantHasActiveSql)) {
                ps.setInt(1, tenantId);
                try (ResultSet rs = ps.executeQuery()) {
                    tenantHasActive = rs.next();
                }
            }

            boolean tenantHasPending = false;
            if (!tenantHasActive) {
                try (PreparedStatement ps = conn.prepareStatement(tenantHasPendingSql)) {
                    ps.setInt(1, tenantId);
                    try (ResultSet rs = ps.executeQuery()) {
                        tenantHasPending = rs.next();
                    }
                }
            }

            String newAccStatus = tenantHasActive ? "ACTIVE" : (tenantHasPending ? "PENDING" : "ACTIVE");
            try (PreparedStatement ps = conn.prepareStatement(updateTenantSql)) {
                ps.setString(1, newAccStatus);
                ps.setInt(2, tenantId);
                ps.executeUpdate();
            }

            conn.commit();
            return ServiceResult.ok("OK");

        } catch (Exception e) {
            e.printStackTrace();
            return ServiceResult.fail("EXCEPTION");
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public TenantMyRoomDTO findActiveMyRoomByTenantId(int tenantId) {
        String sql = """
            SELECT TOP 1
                c.contract_id,
                r.room_id, r.block_id, b.block_name, r.room_number, r.area, r.price, r.status AS room_status, r.floor, r.max_tenants, r.is_mezzanine, r.has_air_conditioning, r.description, 
                img.image_url AS cover_image
            FROM CONTRACT c
            JOIN ROOM r ON r.room_id = c.room_id
            JOIN BLOCK b ON b.block_id = r.block_id
            LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
            WHERE c.tenant_id = ? AND c.status = 'ACTIVE'
            ORDER BY c.created_at DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TenantMyRoomDTO dto = new TenantMyRoomDTO();
                    dto.setContractId(rs.getInt("contract_id"));
                    dto.setRoomId(rs.getInt("room_id"));
                    dto.setBlockId(rs.getInt("block_id"));
                    dto.setBlockName(rs.getString("block_name"));
                    dto.setRoomNumber(rs.getString("room_number"));
                    dto.setArea(rs.getBigDecimal("area"));
                    dto.setPrice(rs.getBigDecimal("price"));
                    dto.setRoomStatus(rs.getString("room_status"));
                    dto.setFloor((Integer) rs.getObject("floor"));
                    dto.setMaxTenants((Integer) rs.getObject("max_tenants"));
                    dto.setMezzanine(rs.getBoolean("is_mezzanine"));
                    dto.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                    dto.setDescription(rs.getString("description"));
                    dto.setCoverImage(rs.getString("cover_image"));
                    return dto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //check coi có contract nào đang activce/pending theo roomId
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean hasBlockingContractByRoomId(int roomId) {
        String sql = """
        SELECT TOP 1 1
        FROM CONTRACT
        WHERE room_id = ?
          AND status IN ('ACTIVE','PENDING')
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
