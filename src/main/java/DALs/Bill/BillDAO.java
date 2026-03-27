package DALs.Bill;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.dto.ManagerBillRowDTO;
import Models.dto.RoomTenantDTO;
import Models.entity.Bill;
import Models.entity.BillDetail;
import Models.entity.Payment;
import Models.entity.Utility;
import Utils.database.DBContext;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
public class BillDAO extends DBContext {

    // =========================
    // GET LIST BILL - MANAGER
    // =========================
    @SuppressWarnings("CallToPrintStackTrace")
    public List<ManagerBillRowDTO> getManagerBills(int page, int pageSize) {
        List<ManagerBillRowDTO> listBill = new ArrayList<>();

        String sql = "SELECT b.bill_id, r.room_number, b.bill_month, "
                + "       t.full_name AS tenant_name, bl.block_name, "
                + "       b.due_date, x.total_amount, "
                + "       b.status, "
                + "       p.status AS payment_status "
                + "FROM BILL b "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "JOIN TENANT t ON c.tenant_id = t.tenant_id "
                + "JOIN ROOM r ON c.room_id = r.room_id "
                + "JOIN BLOCK bl ON r.block_id = bl.block_id "
                + "JOIN ( "
                + "   SELECT bill_id, SUM(unit_price * quantity) AS total_amount "
                + "   FROM BILL_DETAIL GROUP BY bill_id "
                + ") x ON b.bill_id = x.bill_id "
                + "LEFT JOIN PAYMENT p ON p.bill_id = b.bill_id "
                + "ORDER BY "
                + "CASE WHEN b.status = 'UNPAID' THEN 0 ELSE 1 END, "
                + "b.bill_month DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        int offset = (page - 1) * pageSize;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ManagerBillRowDTO dto = new ManagerBillRowDTO();
                dto.setBillId(rs.getInt("bill_id"));
                dto.setRoomNumber(rs.getString("room_number"));
                dto.setMonth(rs.getDate("bill_month"));
                dto.setTenantName(rs.getString("tenant_name"));
                dto.setBlockName(rs.getString("block_name"));
                dto.setDueDate(rs.getDate("due_date"));
                dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                dto.setStatus(rs.getString("status"));
                dto.setPaymentStatus(rs.getString("payment_status"));
                listBill.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listBill;
    }

    // =========================
    // GET BILL DETAIL - MANAGER
    // =========================
    public Bill findBillDetailByIdForManager(int bill_id) {
        String sql = "SELECT *"
                + "FROM BILL "
                + "WHERE bill_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bill_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill b = new Bill();
                b.setBillId(rs.getInt("bill_id"));
                b.setContractId(rs.getInt("contract_id"));
                b.setBillMonth(rs.getDate("bill_month"));
                b.setDueDate(rs.getDate("due_date"));
                b.setStatus(rs.getString("status"));
                b.setNote(rs.getString("note"));
                b.setOldElectricNumber(rs.getInt("old_electric_number"));
                b.setNewElectricNumber(rs.getInt("new_electric_number"));
                b.setOldWaterNumber(rs.getInt("old_water_number"));
                b.setNewWaterNumber(rs.getInt("new_water_number"));
                return b;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // GET ROOM NUMBER BY BILL ID
    // ==========================================
    public String getStringRoomnumber(int bill_id) {
        String sql = "SELECT ROOM.room_number FROM BILL "
                + "INNER JOIN CONTRACT ON BILL.contract_id = CONTRACT.contract_id "
                + "INNER JOIN ROOM ON CONTRACT.room_id = ROOM.room_id "
                + "WHERE BILL.bill_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bill_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("room_number");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================
    // TOTAL AMOUNT - BILL DETAIL
    // =========================
    public BigDecimal totalAmount(int bill_id) {
        String sql = "SELECT ROUND(SUM(unit_price * quantity), 0) AS total_amount "
                + "FROM BILL_DETAIL "
                + "where bill_id = ? ";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bill_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // return 0 instead of null, advoid NullPointerException
        return BigDecimal.ZERO;
    }

    // =========================
    //  GET LIST BILL DETAIL (BREAKDOWN UI)
    // =========================
    public List<BillDetail> getListBillDetailByBillId(int bill_id) {
        List<BillDetail> listBillDetail = new ArrayList<>();
        String sql = "SELECT * "
                + "FROM BILL_DETAIL "
                + "WHERE bill_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bill_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BillDetail bd = new BillDetail();
                bd.setBillDetailId(rs.getInt("bill_detail_id"));
                bd.setBillId(rs.getInt("bill_id"));
                bd.setUtilityId(rs.getInt("utility_id"));
                bd.setItemName(rs.getString("item_name"));
                bd.setUnit(rs.getString("unit"));
                bd.setQuantity(rs.getBigDecimal("quantity"));
                bd.setUnitPrice(rs.getBigDecimal("unit_price"));
                bd.setChargeType(rs.getString("charge_type"));
                listBillDetail.add(bd);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listBillDetail;
    }

    // =========================
    //  GET QR CODE DATA - BILL DETAIL
    // =========================
    public String getQRFromContractByBillId(int bill_id) {
        String sql = "SELECT c.payment_qr_data "
                + "FROM BILL b "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "WHERE b.bill_id = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, bill_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("payment_qr_data");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================
    // Count Bill for pagination
    // =========================
    public int countManagerBills() {
        String sql = "SELECT COUNT(*) "
                + "FROM BILL b "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    // =========================
    // insert bill detail()
    // =========================
    public void insertBillDetail(int billId, Integer utilityId, String itemName, String unit, BigDecimal quantity, BigDecimal unitPrice, String type) throws SQLException {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new SQLException("Quantity cannot be negative");
        }
        BigDecimal amount = quantity.multiply(unitPrice);

        String sql = "INSERT INTO BILL_DETAIL "
                + "(bill_id, utility_id, item_name, unit, quantity, unit_price, charge_type) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, billId);

            if (utilityId == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, utilityId);
            }

            ps.setString(3, itemName);
            ps.setString(4, unit);
            ps.setBigDecimal(5, quantity);
            ps.setBigDecimal(6, unitPrice);
            ps.setString(7, type);

            ps.executeUpdate();
        }
    }

    // =========================
    // insert bill
    // =========================
    public int insertBill(int contractId, Date billMonthDate, Date dueDate,
            int oldE, int newE, int oldW, int newW) throws SQLException {

        // Check duplicate bill
        String checkSql = "SELECT bill_id FROM BILL WHERE contract_id = ? AND bill_month = ?";
        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setInt(1, contractId);
            check.setDate(2, billMonthDate);

            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                throw new SQLException("Bill for this month already exists!");
            }
        }

        String sql = """
            INSERT INTO BILL (
                contract_id, bill_month, due_date, [status], note,
                old_electric_number, new_electric_number,
                old_water_number, new_water_number
            )
            VALUES (?, ?, ?, 'UNPAID', ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, contractId);
            ps.setDate(2, billMonthDate);
            ps.setDate(3, dueDate);

            ps.setString(4,
                    "Bill "
                    + billMonthDate.toLocalDate().getMonthValue()
                    + "/"
                    + billMonthDate.toLocalDate().getYear()
            );

            ps.setInt(5, oldE);
            ps.setInt(6, newE);
            ps.setInt(7, oldW);
            ps.setInt(8, newW);

            int affected = ps.executeUpdate();

            if (affected == 0) {
                throw new SQLException("Insert bill failed!");
            }

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new SQLException("Failed to get bill ID!");
            }
        }
    }

    // =========================
    //  LẤY CONTRACT_ID CHO GENERATE BILL
    // =========================
    public int getActiveContractByRoom(int roomId) {

        String sql = """
                        SELECT contract_id
                        FROM CONTRACT
                        WHERE room_id = ?
                        AND status = 'ACTIVE'
                    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("contract_id");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // không có contract
    }

    // =========================
    //  LẤY Room price CHO from Contract
    // =========================
    public BigDecimal getRoomPrice(int contractId) {
        String sql = """
                        SELECT monthly_rent
                        FROM CONTRACT 
                        WHERE contract_id = ?
                    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal("monthly_rent");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return BigDecimal.ZERO;
    }

    // =========================
    //  get Utility By Name
    // =========================
    public Utility getUtilityByName(String nameUtilities) {
        String sql = """
                        SELECT *
                        FROM UTILITY
                        WHERE utility_name = ?
                    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, nameUtilities);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Utility u = new Utility();

                u.setUtilityId(rs.getInt("utility_id"));
                u.setUtilityName(rs.getString("utility_name"));
                u.setUnit(rs.getString("unit"));
                u.setStandardPrice(rs.getBigDecimal("standard_price"));
                u.setActive(rs.getBoolean("is_active"));
                u.setStatus(rs.getString("status"));
                u.setCreatedAt(rs.getTimestamp("created_at"));
                u.setUpdatedAt(rs.getTimestamp("updated_at"));
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==========================================
    // SYNC UTILITY USAGE TO BILL DETAIL
    // ==========================================
    public void insertUtilityUsageToBill(int billId, int contractId, Date billMonth) throws SQLException {

        String sql = """
                        SELECT u.utility_id, u.utility_name, u.unit, u.standard_price,
                            SUM(uu.quantity) total
                        FROM UTILITY_USAGE uu
                        JOIN UTILITY u ON uu.utility_id = u.utility_id
                        WHERE uu.contract_id = ?
                        AND MONTH(uu.usage_date) = MONTH(?)
                        AND YEAR(uu.usage_date) = YEAR(?)
                        GROUP BY u.utility_id, u.utility_name, u.unit, u.standard_price
                    """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, contractId);
            ps.setDate(2, billMonth);
            ps.setDate(3, billMonth);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                insertBillDetail(billId,
                        rs.getInt("utility_id"),
                        rs.getString("utility_name"),
                        rs.getString("unit"),
                        rs.getBigDecimal("total"),
                        rs.getBigDecimal("standard_price"),
                        "UTILITY"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==========================================
    // MASTER PROCESS: GENERATE FULL MONTHLY BILL
    // ==========================================
    public int generateBill(int roomId, Date billMonth, Date dueDate, int oldE, int newE, int oldW, int newW) throws SQLException {
        int contractId = getActiveContractByRoom(roomId);
        if (contractId == -1) {
            throw new SQLException("No active contract");
        }
        connection.setAutoCommit(false);

        try {
            int billId = insertBill(contractId, billMonth, dueDate, oldE, newE, oldW, newW);

            BigDecimal roomPrice = getRoomPrice(contractId);
            Utility wifi = getUtilityByName("Internet");
            Utility electric = getUtilityByName("Electric");
            Utility water = getUtilityByName("Water");

            int month = billMonth.toLocalDate().getMonthValue();
            int year = billMonth.toLocalDate().getYear();
            // RENT
            insertBillDetail(billId, null, "Room Rent " + month + "/" + year, "month", BigDecimal.ONE, roomPrice, "RENT");
            //INTERNET
            if (wifi != null) {
                insertBillDetail(billId, wifi.getUtilityId(), wifi.getUtilityName() + " " + month + "/" + year, wifi.getUnit(), BigDecimal.ONE, wifi.getStandardPrice(), "UTILITY");
            }

            //ELECTRIC
            int electricUsage = newE - oldE;
            if (electric != null && electricUsage > 0) {
                insertBillDetail(billId, electric.getUtilityId(), electric.getUtilityName() + " " + month + "/" + year, electric.getUnit(), BigDecimal.valueOf(electricUsage), electric.getStandardPrice(), "UTILITY");
            }

            // WATER
            int waterUsage = newW - oldW;
            if (water != null && waterUsage > 0) {
                insertBillDetail(billId, water.getUtilityId(), water.getUtilityName() + " " + month + "/" + year, water.getUnit(), BigDecimal.valueOf(waterUsage), water.getStandardPrice(), "UTILITY");
            }

            //UTILITY
            insertUtilityUsageToBill(billId, contractId, billMonth);

            connection.commit();
            return billId;
        } catch (Exception e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

   public boolean updateBillMeter(int billId, Date billMonth, Date dueDate,
        int oldElectric, int newElectric, int oldWater, int newWater) {

    try {
        connection.setAutoCommit(false);

        // Update BILL
        String sql = """
        UPDATE BILL
        SET bill_month = ?, due_date = ?,
            old_electric_number = ?, new_electric_number = ?,
            old_water_number = ?, new_water_number = ?
        WHERE bill_id = ?
        """;

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setDate(1, billMonth);
        ps.setDate(2, dueDate);
        ps.setInt(3, oldElectric);
        ps.setInt(4, newElectric);
        ps.setInt(5, oldWater);
        ps.setInt(6, newWater);
        ps.setInt(7, billId);
        ps.executeUpdate();

        // Lấy utility trước
        Utility electric = getUtilityByName("Electric");
        Utility water = getUtilityByName("Water");

        //  XÓA đúng điện + nước cũ
        String deleteSql = "DELETE FROM BILL_DETAIL WHERE bill_id = ? AND utility_id = ?";
        PreparedStatement psDel = connection.prepareStatement(deleteSql);

        if (electric != null) {
            psDel.setInt(1, billId);
            psDel.setInt(2, electric.getUtilityId());
            psDel.executeUpdate();
        }

        if (water != null) {
            psDel.setInt(1, billId);
            psDel.setInt(2, water.getUtilityId());
            psDel.executeUpdate();
        }

        int month = billMonth.toLocalDate().getMonthValue();
        int year = billMonth.toLocalDate().getYear();

        // 4. Tính usage
        int electricUsage = newElectric - oldElectric;
        int waterUsage = newWater - oldWater;

        //  Insert lại ELECTRIC
        if (electric != null && electricUsage > 0) {
            insertBillDetail( billId, electric.getUtilityId(), "Electric " + month + "/" + year, electric.getUnit(), BigDecimal.valueOf(electricUsage),  electric.getStandardPrice(), "UTILITY");
        }

        // Insert lại WATER
        if (water != null && waterUsage > 0) {
            insertBillDetail(billId, water.getUtilityId(), "Water " + month + "/" + year, water.getUnit(), BigDecimal.valueOf(waterUsage), water.getStandardPrice(),"UTILITY" );
        }

        connection.commit();
        return true;

    } catch (Exception e) {
        try {
            connection.rollback();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        e.printStackTrace();
    } finally {
        try {
            connection.setAutoCommit(true);
        } catch (Exception e) {
        }
    }

    return false;
}

    // ==========================================
    // CHECK IF BILL ALREADY EXISTS FOR MONTH
    // ==========================================
    public boolean isBillExist(int roomId, int month, int year) throws SQLException {

        String sql = """
        SELECT 1
        FROM BILL b
        JOIN CONTRACT c ON b.contract_id = c.contract_id
        WHERE c.room_id = ?
        AND MONTH(b.bill_month) = ?
        AND YEAR(b.bill_month) = ?
        AND b.status != 'CANCELLED'
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, roomId);
            ps.setInt(2, month);
            ps.setInt(3, year);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // =========================
    // get Bill detail current for tenant
    // =========================
    public Bill getCurrentBillForTenant(int tenant_id) {
        String sql = "SELECT TOP 1 b.* "
                + "FROM BILL b "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "WHERE c.tenant_id = ? AND c.status = 'ACTIVE' "
                + "ORDER BY CASE WHEN b.status='UNPAID' THEN 0 ELSE 1 END, b.bill_month DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenant_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Bill b = new Bill();
                b.setBillId(rs.getInt("bill_id"));
                b.setContractId(rs.getInt("contract_id"));
                b.setBillMonth(rs.getDate("bill_month"));
                b.setDueDate(rs.getDate("due_date"));
                b.setStatus(rs.getString("status"));
                b.setNote(rs.getString("note"));
                b.setOldElectricNumber(rs.getInt("old_electric_number"));
                b.setNewElectricNumber(rs.getInt("new_electric_number"));
                b.setOldWaterNumber(rs.getInt("old_water_number"));
                b.setNewWaterNumber(rs.getInt("new_water_number"));
                return b;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================
    // get total tenant unpaid
    // =========================
    public BigDecimal getTotalTenantUnpaid(int tenant_id) {
        String sql = """
                    SELECT SUM(d.unit_price * d.quantity) AS total_unpaid
                    FROM BILL b
                    JOIN CONTRACT c ON b.contract_id = c.contract_id
                    JOIN BILL_DETAIL d ON b.bill_id = d.bill_id
                    WHERE c.contract_id = (
                        SELECT TOP 1 contract_id
                        FROM CONTRACT
                        WHERE tenant_id = ? AND status = 'ACTIVE'
                    )
                    AND b.status = 'UNPAID'
                """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenant_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next() && rs.getBigDecimal(1) != null) {
                return rs.getBigDecimal(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    // =========================
    // GET LASTPAYMENT
    // =========================
    public Payment getLastPaidAmountByTenant(int tenant_id) {
        String sql = """
                        SELECT TOP 1 P.*
                        FROM PAYMENT P
                        JOIN BILL B ON P.bill_id = B.bill_id
                        JOIN CONTRACT C ON B.contract_id = C.contract_id
                        WHERE C.contract_id = (
                            SELECT TOP 1 contract_id
                            FROM CONTRACT
                            WHERE tenant_id = ? AND status = 'ACTIVE'
                        )
                        ORDER BY P.paid_at DESC
                    """;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenant_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("payment_id"));
                p.setContractId(rs.getInt("contract_id"));
                p.setBillId(rs.getInt("bill_id"));
                p.setMethod(rs.getString("method"));
                p.setAmount(rs.getBigDecimal("amount"));
                p.setPaidAt(rs.getTimestamp("paid_at"));
                p.setStatus(rs.getString("status"));
                p.setNote(rs.getString("note"));
                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // =========================
    // GET Room Number By TenantId
    // =========================
    public String getRoomNumberByTenantId(int tenant_id) {
        String sql = "SELECT R.room_number "
                + "FROM CONTRACT C "
                + "JOIN ROOM R ON C.room_id = R.room_id "
                + "WHERE C.tenant_id = ? AND C.status = 'ACTIVE'";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenant_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("room_number");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "No Room";
    }

    // =========================
    // GET Bill Detail By Id For Tenant
    // =========================
    public Bill findBillDetailByIdForTenant(int billId, int tenantId) {
        String sql = """
        SELECT b.*
        FROM BILL b
        JOIN CONTRACT c ON b.contract_id = c.contract_id
        WHERE b.bill_id = ?
          AND c.tenant_id = ?
          AND c.status = 'ACTIVE'
    """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, billId);
            ps.setInt(2, tenantId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Bill b = new Bill();
                b.setBillId(rs.getInt("bill_id"));
                b.setContractId(rs.getInt("contract_id"));
                b.setBillMonth(rs.getDate("bill_month"));
                b.setDueDate(rs.getDate("due_date"));
                b.setStatus(rs.getString("status"));
                b.setNote(rs.getString("note"));
                b.setOldElectricNumber(rs.getInt("old_electric_number"));
                b.setNewElectricNumber(rs.getInt("new_electric_number"));
                b.setOldWaterNumber(rs.getInt("old_water_number"));
                b.setNewWaterNumber(rs.getInt("new_water_number"));
                return b;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // =========================
    // GET List Bill For Tenant
    // =========================
    public List<ManagerBillRowDTO> listBillForTenant(int tenant_id) {
        List<ManagerBillRowDTO> list = new ArrayList<>();
        String sql = """
                        SELECT b.bill_id, r.room_number, b.bill_month,
                            t.full_name AS tenant_name, bl.block_name,
                            b.due_date,
                            (SELECT COALESCE(SUM(d.unit_price * d.quantity), 0)
                                FROM BILL_DETAIL d WHERE d.bill_id = b.bill_id) AS total_amount,
                            b.status,
                            p.status AS payment_status
                        FROM BILL b
                        JOIN CONTRACT c ON b.contract_id = c.contract_id
                        JOIN TENANT t ON c.tenant_id = t.tenant_id
                        JOIN ROOM r ON c.room_id = r.room_id
                        JOIN BLOCK bl ON r.block_id = bl.block_id
                        LEFT JOIN PAYMENT p ON p.bill_id = b.bill_id
                        WHERE c.tenant_id = ? AND c.status = 'ACTIVE'
                        ORDER BY CASE WHEN b.status = 'UNPAID' THEN 0 ELSE 1 END, b.bill_month DESC
                    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenant_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ManagerBillRowDTO dto = new ManagerBillRowDTO();
                dto.setBillId(rs.getInt("bill_id"));
                dto.setRoomNumber(rs.getString("room_number"));
                dto.setMonth(rs.getDate("bill_month"));
                dto.setTenantName(rs.getString("tenant_name"));
                dto.setBlockName(rs.getString("block_name"));
                dto.setDueDate(rs.getDate("due_date"));
                dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                dto.setStatus(rs.getString("status"));
                dto.setPaymentStatus(rs.getString("payment_status"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================
    // GET OCCUPIED ROOMS WITH LAST METER READS
    // ==========================================
    public List<RoomTenantDTO> getRoomsWithTenant() {
        List<RoomTenantDTO> list = new ArrayList<>();
        String sql = """
                    SELECT r.room_id, r.room_number, t.full_name, c.contract_id, c.monthly_rent,
                        ISNULL(last_bill.new_electric_number, 0) AS last_electric,
                        ISNULL(last_bill.new_water_number, 0)    AS last_water
                    FROM ROOM r
                    JOIN CONTRACT c ON r.room_id = c.room_id
                    JOIN TENANT t   ON t.tenant_id = c.tenant_id
                    OUTER APPLY (
                        SELECT TOP 1 new_electric_number, new_water_number
                        FROM BILL
                        WHERE contract_id = c.contract_id AND status != 'CANCELLED'
                        ORDER BY bill_month DESC
                    ) AS last_bill
                    WHERE c.status = 'ACTIVE' AND r.status = 'OCCUPIED'
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                RoomTenantDTO dto = new RoomTenantDTO();
                dto.setRoomId(rs.getInt("room_id"));
                dto.setRoomNumber(rs.getString("room_number"));
                dto.setTenantName(rs.getString("full_name"));
                dto.setContract_id(rs.getInt("contract_id"));
                dto.setMonthlyRent(rs.getBigDecimal("monthly_rent"));
                dto.setLastElectric(rs.getInt("last_electric")); // thêm mới
                dto.setLastWater(rs.getInt("last_water"));

                list.add(dto);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================
    // get startdate and end_date cua contract = roomid
    // ==========================================
    public RoomTenantDTO getContractDatesByRoomId(int roomId) {
        String sql = """
        SELECT c.start_date, c.end_date
        FROM CONTRACT c
        WHERE c.room_id = ? AND c.status = 'ACTIVE'
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                RoomTenantDTO dto = new RoomTenantDTO();
                dto.setStartDate(rs.getDate("start_date").toLocalDate());
                java.sql.Date end = rs.getDate("end_date");
                if (end != null) {
                    dto.setEndDate(end.toLocalDate());
                }
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
