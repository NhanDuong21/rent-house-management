/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs.utilities;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Models.entity.Utility;
import Utils.database.DBContext;

/**
 *
 * @author Bui Nhu Y
 */
public class Utilities_UsageDAO extends DBContext {

    public List<Utility> getSubscribersByUtilityIdPaging(int id, int page, int pageSize) {
        List<Utility> list = new ArrayList<>();

        String sql = "SELECT DISTINCT "
                + "    r.room_id, "
                + "    r.room_number, "
                + "    t.full_name, "
                + "    ut.usage_date "
                + "FROM BILL_DETAIL bd "
                + "JOIN BILL b ON bd.bill_id = b.bill_id "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "JOIN ROOM r ON c.room_id = r.room_id "
                + "JOIN TENANT t ON c.tenant_id = t.tenant_id "
                + "JOIN UTILITY_USAGE ut ON c.contract_id = ut.contract_id "
                + "WHERE bd.utility_id = ? "
                + "ORDER BY r.room_id "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Utility u = new Utility();
                u.setUtilityId(rs.getInt("room_id"));
                u.setUtilityName(rs.getString("room_number"));
                u.setUnit(rs.getString("full_name"));
                u.setStatus(rs.getString("usage_date"));
                list.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countSubscribersByUtilityId(int id) {
        String sql = "SELECT COUNT(*) FROM ( "
                + "    SELECT DISTINCT "
                + "        r.room_id, "
                + "        r.room_number, "
                + "        t.full_name, "
                + "        ut.usage_date "
                + "    FROM BILL_DETAIL bd "
                + "    JOIN BILL b ON bd.bill_id = b.bill_id "
                + "    JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "    JOIN ROOM r ON c.room_id = r.room_id "
                + "    JOIN TENANT t ON c.tenant_id = t.tenant_id "
                + "    JOIN UTILITY_USAGE ut ON c.contract_id = ut.contract_id "
                + "    WHERE bd.utility_id = ? "
                + ") AS x";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveContractIdByTenantId(int tenantId) {
        String sql = "SELECT TOP 1 contract_id FROM CONTRACT "
                + "WHERE tenant_id = ? AND status = 'ACTIVE' "
                + "ORDER BY contract_id DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("contract_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // không tìm thấy contract
    }

    public boolean isUtilityAlreadySubscribed(int contractId, int utilityId) {
        String sql = "SELECT COUNT(*) FROM UTILITY_USAGE "
                + "WHERE contract_id = ? AND utility_id = ? "
                + "AND MONTH(usage_date) = MONTH(GETDATE()) "
                + "AND YEAR(usage_date) = YEAR(GETDATE())";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, contractId);
            ps.setInt(2, utilityId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int addMultipleUtilityUsage(int contractId, List<Integer> utilityIds) {
        String sql = "INSERT INTO UTILITY_USAGE (contract_id, utility_id, usage_date, quantity, created_at) "
                + "VALUES (?, ?, GETDATE(), 1, GETDATE())";
        int successCount = 0;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int utilityId : utilityIds) {
                if (isUtilityAlreadySubscribed(contractId, utilityId)) {
                    continue; // bỏ qua nếu đã đăng ký
                }
                ps.setInt(1, contractId);
                ps.setInt(2, utilityId);
                if (ps.executeUpdate() > 0) {
                    successCount++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return successCount;
    }

    public List<Integer> getSubscribedUtilityIds(int contractId) {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT utility_id FROM UTILITY_USAGE "
                + "WHERE contract_id = ? "
                + "AND MONTH(usage_date) = MONTH(GETDATE()) "
                + "AND YEAR(usage_date) = YEAR(GETDATE())";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, contractId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt("utility_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ids;
    }

    public int getUnpaidBillIdByTenantId(int tenantId) {
        String sql = "SELECT TOP 1 b.bill_id FROM BILL b "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "WHERE c.tenant_id = ? AND b.status != 'PAID' "
                + "ORDER BY b.bill_id DESC";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("bill_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void syncToBillDetail(int billId, int contractId) {
        String deleteSql = "DELETE FROM BILL_DETAIL "
                + "WHERE bill_id = ? "
                + "AND charge_type = 'UTILITY' "
                + "AND utility_id NOT IN ( "
                + "    SELECT utility_id FROM UTILITY "
                + "    WHERE utility_name LIKE '%Electric%' "
                + "    OR utility_name LIKE '%Water%' "
                + "    OR utility_name LIKE '%Internet%' "
                + ")";

        String insertSql = "INSERT INTO BILL_DETAIL (bill_id, utility_id, item_name, unit, unit_price, quantity, charge_type) "
                + "SELECT ?, uu.utility_id, u.utility_name, u.unit, u.standard_price, uu.quantity, 'UTILITY' "
                + "FROM UTILITY_USAGE uu "
                + "JOIN UTILITY u ON uu.utility_id = u.utility_id "
                + "WHERE uu.contract_id = ? "
                + "AND MONTH(uu.usage_date) = MONTH(GETDATE()) "
                + "AND YEAR(uu.usage_date) = YEAR(GETDATE())";
        try {
            PreparedStatement ps1 = connection.prepareStatement(deleteSql);
            ps1.setInt(1, billId);
            ps1.executeUpdate();

            PreparedStatement ps2 = connection.prepareStatement(insertSql);
            ps2.setInt(1, billId);
            ps2.setInt(2, contractId);
            ps2.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Xóa các utility bị untick
    public void removeUtilityUsages(int contractId, List<Integer> utilityIds) {
        if (utilityIds == null || utilityIds.isEmpty()) {
            return;
        }
        String sql = "DELETE FROM UTILITY_USAGE "
                + "WHERE contract_id = ? AND utility_id = ? "
                + "AND MONTH(usage_date) = MONTH(GETDATE()) "
                + "AND YEAR(usage_date) = YEAR(GETDATE())";
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int uid : utilityIds) {
                ps.setInt(1, contractId);
                ps.setInt(2, uid);
                ps.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nếu bill có status = 'PAID' thì tenant không được phép chỉnh sửa utility nữa.
    public boolean isBillLocked(int tenantId) {
        String sql = "SELECT TOP 1 b.status, b.bill_month, "
                + "(SELECT COUNT(*) FROM PAYMENT p WHERE p.bill_id = b.bill_id AND p.status = 'PENDING') AS hasPending "
                + "FROM BILL b "
                + "JOIN CONTRACT c ON b.contract_id = c.contract_id "
                + "WHERE c.tenant_id = ? "
                + "ORDER BY b.bill_id DESC"; // bỏ filter tháng, lấy bill mới nhất
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                int hasPending = rs.getInt("hasPending");
                java.sql.Date billMonth = rs.getDate("bill_month");

                // Lấy tháng đầu tiên của tháng hiện tại
                java.time.LocalDate firstDayThisMonth = java.time.LocalDate.now().withDayOfMonth(1);
                java.time.LocalDate billDate = billMonth.toLocalDate();

                // Lock nếu: bill tháng này/tương lai bị PAID, hoặc đang có PENDING payment
                boolean isPaidThisMonthOrLater = "PAID".equalsIgnoreCase(status)
                        && !billDate.isBefore(firstDayThisMonth);
                return isPaidThisMonthOrLater || hasPending > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
