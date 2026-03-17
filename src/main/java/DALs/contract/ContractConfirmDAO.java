package DALs.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Models.dto.TxResult;
import Utils.database.DBContext;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class ContractConfirmDAO extends DBContext {

    @SuppressWarnings("CallToPrintStackTrace")
    public TxResult confirmContractWithDebug(int contractId) {

        String getSql = """
            SELECT contract_id, room_id, tenant_id, status
            FROM CONTRACT
            WHERE contract_id = ?
        """;

        String findOldActive = """
            SELECT TOP 1 contract_id
            FROM CONTRACT
            WHERE room_id = ?
              AND status = 'ACTIVE'
              AND contract_id <> ?
            ORDER BY end_date DESC, contract_id DESC
        """;

        String endOldActive = """
            UPDATE CONTRACT
            SET status = 'ENDED', updated_at = SYSDATETIME()
            WHERE contract_id = ? AND status = 'ACTIVE'
        """;

        String updateContract = """
            UPDATE CONTRACT
            SET status = 'ACTIVE', updated_at = SYSDATETIME()
            WHERE contract_id = ? AND status = 'PENDING'
        """;

        String updateRoom = """
            UPDATE ROOM
            SET status = 'OCCUPIED'
            WHERE room_id = ?
        """;

        String updateTenant = """
            UPDATE TENANT
            SET account_status = 'ACTIVE', updated_at = SYSDATETIME()
            WHERE tenant_id = ?
        """;

        String activateOccupants = """
            UPDATE OCCUPANT
            SET status = 'ACTIVE',
                updated_at = SYSDATETIME()
            WHERE contract_id = ?
              AND status = 'PENDING'
        """;

        String endOldActiveOccupants = """
            UPDATE OCCUPANT
            SET status = 'REMOVED',
                updated_at = SYSDATETIME()
            WHERE contract_id = ?
              AND status = 'ACTIVE'
        """;

        String confirmPayment = """
            UPDATE PAYMENT
            SET status = 'CONFIRMED'
            WHERE payment_id = (
                SELECT TOP 1 payment_id
                FROM PAYMENT
                WHERE contract_id = ?
                  AND method = 'BANK'
                  AND status = 'PENDING'
                ORDER BY paid_at DESC, payment_id DESC
            )
        """;

        try (Connection conn = new DBContext().getConnection()) {
            conn.setAutoCommit(false);

            int roomId;
            int tenantId;
            String status;

            try (PreparedStatement ps = conn.prepareStatement(getSql)) {
                ps.setInt(1, contractId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return new TxResult(false, "NOT_FOUND", "Contract not found");
                    }
                    status = rs.getString("status");
                    roomId = rs.getInt("room_id");
                    tenantId = rs.getInt("tenant_id");
                }
            }

            if (!"PENDING".equalsIgnoreCase(status)) {
                conn.rollback();
                return new TxResult(false, "NOT_PENDING", "Current status=" + status);
            }

            Integer oldActiveId = null;
            try (PreparedStatement ps = conn.prepareStatement(findOldActive)) {
                ps.setInt(1, roomId);
                ps.setInt(2, contractId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        oldActiveId = rs.getInt(1);
                    }
                }
            }

            if (oldActiveId != null) {
                try (PreparedStatement ps = conn.prepareStatement(endOldActive)) {
                    ps.setInt(1, oldActiveId);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(endOldActiveOccupants)) {
                    ps.setInt(1, oldActiveId);
                    ps.executeUpdate();
                }
            }

            int a1;
            try (PreparedStatement ps = conn.prepareStatement(updateContract)) {
                ps.setInt(1, contractId);
                a1 = ps.executeUpdate();
            }
            if (a1 <= 0) {
                conn.rollback();
                return new TxResult(false, "FAIL_CONTRACT_UPDATE", "Row affected=" + a1);
            }

            int a2;
            try (PreparedStatement ps = conn.prepareStatement(updateRoom)) {
                ps.setInt(1, roomId);
                a2 = ps.executeUpdate();
            }
            if (a2 <= 0) {
                conn.rollback();
                return new TxResult(false, "FAIL_ROOM_UPDATE", "roomId=" + roomId);
            }

            int a3;
            try (PreparedStatement ps = conn.prepareStatement(updateTenant)) {
                ps.setInt(1, tenantId);
                a3 = ps.executeUpdate();
            }
            if (a3 <= 0) {
                conn.rollback();
                return new TxResult(false, "FAIL_TENANT_UPDATE", "tenantId=" + tenantId);
            }

            try (PreparedStatement ps = conn.prepareStatement(activateOccupants)) {
                ps.setInt(1, contractId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(confirmPayment)) {
                ps.setInt(1, contractId);
                ps.executeUpdate();
            }

            conn.commit();
            return new TxResult(true, "OK", null);

        } catch (SQLException e) {
            e.printStackTrace();
            return new TxResult(false, "EXCEPTION", e.getMessage());
        }
    }

    public boolean confirmContract(int contractId) {
        return confirmContractWithDebug(contractId).isOk();
    }
}
