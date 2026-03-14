package DALs.payment;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import Models.entity.Payment;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-12
 */
public class PaymentDAO extends DBContext {

    //Hàm này dùng để tìm kiếm giao dịch thanh toán qua ngân hàng gần đây nhất của một hợp đồng cụ thể.
    @SuppressWarnings("CallToPrintStackTrace")
    public Payment findLatestBankPaymentForContract(int contractId) {

        String sql = """
            SELECT TOP 1 payment_id, contract_id, bill_id, method, amount, paid_at, status, note
            FROM PAYMENT
            WHERE contract_id = ? AND method = 'BANK'
            ORDER BY paid_at DESC, payment_id DESC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Payment p = new Payment();
                    p.setPaymentId(rs.getInt("payment_id"));
                    p.setContractId(rs.getInt("contract_id"));
                    // bill_id null
                    p.setMethod(rs.getString("method"));
                    p.setAmount(rs.getBigDecimal("amount"));
                    p.setPaidAt(rs.getTimestamp("paid_at"));
                    p.setStatus(rs.getString("status"));
                    p.setNote(rs.getString("note"));
                    return p;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // tránh spam bấm nhiều lần: nếu đã có PENDING/CONFIRMED thì không insert nữa
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean hasPendingOrConfirmedBankForContract(int contractId) {

        String sql = """
            SELECT 1
            FROM PAYMENT
            WHERE contract_id = ?
              AND method = 'BANK'
              AND status IN ('PENDING','CONFIRMED')
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //check pending payment
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean hasPendingBankPayment(int contractId) {

        String sql = """
        SELECT TOP 1 1
        FROM PAYMENT
        WHERE contract_id = ?
          AND method = 'BANK'
          AND status = 'PENDING'
        ORDER BY paid_at DESC, payment_id DESC
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //inser new data vào dbo payment để hiển thị bên manager contract confirm
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean insertTenantConfirmTransfer(int contractId, java.math.BigDecimal amount) {

        String sql = """
            INSERT INTO PAYMENT (contract_id, bill_id, method, amount, paid_at, status, note)
            VALUES (?, NULL, 'BANK', ?, SYSDATETIME(), 'PENDING', N'Tenant confirmed transfer')
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setBigDecimal(2, amount);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    //tìm giao dịch chuyển khoản ngân hàng mới nhất đang ở trạng thái chờ và xác nhận nó đã thành công.
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean confirmLatestBankPayment(int contractId) {

        String sql = """
    UPDATE PAYMENT SET status = 'CONFIRMED' WHERE payment_id = (
            SELECT TOP 1 payment_id
            FROM PAYMENT
            WHERE contract_id = ?
              AND method = 'BANK'
              AND status = 'PENDING'
            ORDER BY paid_at DESC
        )
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getMonthlyRevenue() {

        String sql = """
        SELECT SUM(amount)
        FROM PAYMENT
        WHERE status = 'CONFIRMED'
          AND MONTH(paid_at) = MONTH(GETDATE())
          AND YEAR(paid_at) = YEAR(GETDATE())
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getTotalRevenue() {

        String sql = """
        SELECT SUM(amount)
        FROM PAYMENT
        WHERE status = 'CONFIRMED'
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
