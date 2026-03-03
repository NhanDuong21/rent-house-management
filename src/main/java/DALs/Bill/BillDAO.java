package DALs.Bill;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Models.dto.ManagerBillRowDTO;
import Models.entity.Bill;
import Models.entity.BillDetail;
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
    public List<ManagerBillRowDTO> getManagerBills(String keyword, String status, int page, int pageSize) {
    List<ManagerBillRowDTO> listBill = new ArrayList<>();

    String sql =
        "SELECT * FROM ( " +
        "  SELECT b.bill_id, r.room_number, b.bill_month, " +
        "         t.full_name AS tenant_name, bl.block_name, " +
        "         b.due_date, x.total_amount, b.status, " +
        "         ROW_NUMBER() OVER (ORDER BY b.bill_month DESC) AS rn " +
        "  FROM BILL b " +
        "  JOIN CONTRACT c ON b.contract_id = c.contract_id " +
        "  JOIN TENANT t ON c.tenant_id = t.tenant_id " +
        "  JOIN ROOM r ON c.room_id = r.room_id " +
        "  JOIN BLOCK bl ON r.block_id = bl.block_id " +
        "  JOIN ( " +
        "     SELECT bill_id, SUM(unit_price * quantity) AS total_amount " +
        "     FROM BILL_DETAIL GROUP BY bill_id " +
        "  ) x ON b.bill_id = x.bill_id " +
        "  WHERE 1=1 ";

    if (keyword != null && !keyword.trim().isEmpty()) {
        sql += " AND (CAST(b.bill_id AS VARCHAR) LIKE ? OR r.room_number LIKE ?) ";
    }

    if (status != null && !status.trim().isEmpty()) {
        sql += " AND b.status = ? ";
    }

    sql += ") t WHERE rn BETWEEN ? AND ?";

    try {
        PreparedStatement ps = connection.prepareStatement(sql);
        int index = 1;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = "%" + keyword.trim() + "%";
            ps.setString(index++, kw);
            ps.setString(index++, kw);
        }

        if (status != null && !status.trim().isEmpty()) {
            ps.setString(index++, status.trim().toUpperCase()); // ⭐ FIX CHÍNH
        }

        int start = (page - 1) * pageSize + 1;
        int end = page * pageSize;
        ps.setInt(index++, start);
        ps.setInt(index, end);

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
                + "where bill_id = ? "
                + "GROUP BY bill_id;";
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

    public int countSearchManagerBills(String status, String keyword) {
        String sql = "SELECT COUNT(*)  "
                + "FROM BILL b "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "JOIN ROOM r ON c.room_id = r.room_id "
                + "WHERE 1=1";

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql += " AND (CAST(b.bill_id AS VARCHAR) LIKE ? OR r.room_number LIKE ?) ";
        }

        if (status != null && !status.isEmpty()) {
            sql += " AND b.status = ? ";
        }
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int index = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword.trim() + "%";
                ps.setString(index++, kw);
                ps.setString(index++, kw);
            }

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(index++, status.trim().toUpperCase());
}
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
        }
        return 0;
    }

    public static void main(String[] args) {
        BillDAO bd = new BillDAO();

        System.out.println(bd.getStringRoomnumber(1));
    }

}
