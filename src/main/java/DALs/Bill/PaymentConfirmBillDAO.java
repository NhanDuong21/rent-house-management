/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs.Bill;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Utils.database.DBContext;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
public class PaymentConfirmBillDAO extends DBContext {

    BillDAO bd = new BillDAO();

    //comfirm payment - manager
    public void confirmPaymentForManager(int billId) throws SQLException {
        try {
            connection.setAutoCommit(false);
            String sqlCheck = "SELECT bill_id FROM BILL WHERE bill_id = ?";
            String sqlUpdate = "UPDATE BILL SET status = 'PAID' WHERE bill_id = ? AND status = 'UNPAID'";
            String sqlInsert = "INSERT INTO PAYMENT(contract_id, bill_id, method, amount, paid_at, status, note) "
                    + "VALUES (NULL, ?, 'BANK', ?, GETDATE(), 'CONFIRMED', 'Tenant confirmed transfer')";

            // Check bill existing
            try (PreparedStatement ps1 = connection.prepareStatement(sqlCheck)) {
                ps1.setInt(1, billId);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (!rs.next()) {
                        connection.rollback();
                        throw new SQLException("Bill không tồn tại: " + billId);
                    }

                    //Total amount
                    BigDecimal amount = bd.totalAmount(billId);
                    if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
                        connection.rollback();
                        throw new SQLException("Invalid amount for the bill " + billId);
                    }

                    // update bill
                    try (PreparedStatement ps2 = connection.prepareStatement(sqlUpdate)) {
                        ps2.setInt(1, billId);
                        if (ps2.executeUpdate() == 0) {
                            connection.rollback();
                            throw new SQLException("The bill has either been paid previously or does not exist.");
                        }
                    }

                    // insert payment
                    try (PreparedStatement ps3 = connection.prepareStatement(sqlInsert)) {
                        ps3.setInt(1, billId);
                        ps3.setBigDecimal(2, amount);
                        ps3.executeUpdate();
                    }
                    connection.commit();
                }
            }
        } catch (SQLException e) {
            connection.rollback();
            throw e; // ném ra ngoài để tầng trên xử lý (trả về lỗi cho user hoặc log)
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Exception ignored) {
            }
        }
    }

    public static void main(String[] args) {
        PaymentConfirmBillDAO pm = new PaymentConfirmBillDAO();
        try {
            pm.confirmPaymentForManager(1);
            System.out.println("success");
        } catch (SQLException e) {
            System.err.println("error");
            System.err.println( "error: " + e.getMessage());
        }
    }

}
