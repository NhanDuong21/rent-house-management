/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs.Bill;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.dto.PaymentHistoryRowDTO;
import Models.entity.Payment;
import Utils.database.DBContext;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
public class PaymentConfirmBillDAO extends DBContext {

    BillDAO bd = new BillDAO();

    public void createPaymentForTenant(int billId, String method, BigDecimal amount) throws SQLException {
        try {
            connection.setAutoCommit(false);

            // Check bill tồn tại và đang UNPAID
            String sqlCheckBill = "SELECT status FROM BILL WHERE bill_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(sqlCheckBill)) {
                ps.setInt(1, billId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        throw new SQLException("Bill does not exist.");
                    }

                    String status = rs.getString("status");
                    if (!"UNPAID".equals(status)) {
                        connection.rollback();
                        throw new SQLException("Bill cannot be paid.");
                    }
                }
            }
            //Insert PAYMENT (PENDING)
            String sqlInsert
                    = "INSERT INTO PAYMENT (bill_id, method, amount, paid_at, status, note) "
                    + "VALUES (?, ?, ?, GETDATE(), 'PENDING', 'The invoice payment request has been submitted.')";

            try (PreparedStatement ps = connection.prepareStatement(sqlInsert)) {
                ps.setInt(1, billId);
                ps.setString(2, method);
                ps.setBigDecimal(3, amount);
                ps.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void confirmPaymentForManager(int billId) throws SQLException {
        try {
            connection.setAutoCommit(false);

            String sqlUpdatePayment
                    = "UPDATE PAYMENT SET status = 'CONFIRMED' "
                    + "WHERE payment_id = ("
                    + "SELECT TOP 1 payment_id "
                    + "FROM PAYMENT "
                    + "WHERE bill_id = ? AND status = 'PENDING' "
                    + "ORDER BY paid_at DESC)";

            String sqlUpdateBill = "UPDATE BILL SET status = 'PAID' "
                    + "WHERE bill_id = ?";

            String sqlUpdatePaymentNote
                    = "UPDATE PAYMENT SET note = 'Payment successful' "
                    + "WHERE payment_id = ("
                    + "SELECT TOP 1 payment_id "
                    + "FROM PAYMENT "
                    + "WHERE bill_id = ? AND status = 'CONFIRMED' "
                    + "ORDER BY paid_at DESC)";

            try (PreparedStatement ps1 = connection.prepareStatement(sqlUpdatePayment)) {
                ps1.setInt(1, billId);
                if (ps1.executeUpdate() == 0) {
                    connection.rollback();
                    throw new SQLException("No pending payment found.");
                }
            }
            try (PreparedStatement ps2 = connection.prepareStatement(sqlUpdatePaymentNote)) {
                ps2.setInt(1, billId);
                ps2.executeUpdate();
            }

            try (PreparedStatement ps3 = connection.prepareStatement(sqlUpdateBill)) {
                ps3.setInt(1, billId);
                ps3.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void rejectPayment(int billId) throws SQLException {
        try {
            connection.setAutoCommit(false);

            String sqlUpdatePayment
                    = "UPDATE PAYMENT SET status = 'REJECTED' "
                    + "WHERE bill_id = ? AND status = 'PENDING'";

            String sqlUpdateBill
                    = "UPDATE BILL SET status = 'UNPAID' "
                    + "WHERE bill_id = ?";

            // Update PAYMENT
            try (PreparedStatement ps1 = connection.prepareStatement(sqlUpdatePayment)) {
                ps1.setInt(1, billId);

                if (ps1.executeUpdate() == 0) {
                    connection.rollback();
                    throw new SQLException("No pending payment found to reject.");
                }
            }

            // Update BILL
            try (PreparedStatement ps2 = connection.prepareStatement(sqlUpdateBill)) {
                ps2.setInt(1, billId);
                ps2.executeUpdate();
            }

            connection.commit();

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // check bill co dang cho duyet ko - null -> tenant chua gui request thanh toan trong payment
    public Payment getPendingPaymentByBillId(int billId) {
        String sql = "SELECT * "
                + "FROM PAYMENT "
                + "WHERE bill_id = ? AND status = 'PENDING'";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Payment p = new Payment();
                p.setPaymentId(rs.getInt("payment_id"));
                p.setBillId(rs.getInt("bill_id"));
                p.setMethod(rs.getString("method"));
                p.setAmount(rs.getBigDecimal("amount"));
                p.setPaidAt(rs.getTimestamp("paid_at"));
                p.setStatus(rs.getString("status"));

                return p;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<PaymentHistoryRowDTO> getAllPaymentHistoryByTenantId(int tenant_id) {
        List<PaymentHistoryRowDTO> list = new ArrayList<>();

      String sql = "SELECT "
                    + "p.payment_id, "
                    + "t.full_name, "
                    + "r.room_number, "
                    + "p.method, "
                    + "p.status, "
                    + "b.bill_month, "
                    + "p.paid_at, "
                    + "p.amount "
                    + "FROM PAYMENT p "
                    + "LEFT JOIN CONTRACT c ON p.contract_id = c.contract_id "
                    + "LEFT JOIN BILL b ON p.bill_id = b.bill_id "
                    + "LEFT JOIN CONTRACT c2 ON b.contract_id = c2.contract_id "
                    + "LEFT JOIN ROOM r ON r.room_id = ISNULL(c.room_id, c2.room_id) "
                    + "LEFT JOIN TENANT t ON t.tenant_id = ISNULL(c.tenant_id, c2.tenant_id) "
                    + "WHERE t.tenant_id = ? "
                    + "ORDER BY p.paid_at DESC";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, tenant_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PaymentHistoryRowDTO pm = new PaymentHistoryRowDTO();
                pm.setPaymentId(rs.getInt("payment_id"));
                pm.setFname(rs.getString("full_name"));
                pm.setRoomNumber(rs.getString("room_number"));
                pm.setMethod(rs.getString("method"));
                pm.setStatus(rs.getString("status"));
                pm.setBillMonth(rs.getDate("bill_month"));
                pm.setPaidAt(rs.getTimestamp("paid_at"));
                pm.setAmount(rs.getBigDecimal("amount"));
                list.add(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

        // getStatus payment = bill_id
    public String getLatestPaymentStatus(int billId) {
        String sql = """
                    SELECT TOP 1 status
                    FROM PAYMENT
                    WHERE bill_id = ?
                    ORDER BY paid_at DESC
                """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, billId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
