package DALs.contract;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Models.entity.ContractOccupant;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-11
 */
public class ContractOccupantDAO extends DBContext {

    public int insertPrimary(Connection conn, int contractId, int tenantId, Date moveInDate, String status) throws SQLException {
        String sql = """
            INSERT INTO CONTRACT_OCCUPANT
                (contract_id, tenant_id, occupant_role, status, move_in_date)
            VALUES
                (?, ?, 'PRIMARY', ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, contractId);
            ps.setInt(2, tenantId);
            ps.setString(3, status);
            ps.setDate(4, moveInDate);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public int insertMember(Connection conn, int contractId, int tenantId, Date moveInDate, String status) throws SQLException {
        String sql = """
            INSERT INTO CONTRACT_OCCUPANT
                (contract_id, tenant_id, occupant_role, status, move_in_date)
            VALUES
                (?, ?, 'MEMBER', ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, contractId);
            ps.setInt(2, tenantId);
            ps.setString(3, status);
            ps.setDate(4, moveInDate);

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<ContractOccupant> findByContractId(int contractId) {
        List<ContractOccupant> list = new ArrayList<>();

        String sql = """
            SELECT
                co.contract_occupant_id,
                co.contract_id,
                co.tenant_id,
                co.occupant_role,
                co.status,
                co.move_in_date,
                co.move_out_date,
                co.created_at,
                co.updated_at,

                t.full_name,
                t.identity_code,
                t.phone_number,
                t.email,
                t.address,
                t.date_of_birth,
                t.gender,
                t.avatar,
                t.account_status,

                df.file_url AS cccd_front_url,
                db.file_url AS cccd_back_url

            FROM CONTRACT_OCCUPANT co
            JOIN TENANT t
                ON co.tenant_id = t.tenant_id

            LEFT JOIN TENANT_DOCUMENT df
                ON df.tenant_id = t.tenant_id
               AND df.document_type = 'CCCD_FRONT'
               AND df.status = 'ACTIVE'

            LEFT JOIN TENANT_DOCUMENT db
                ON db.tenant_id = t.tenant_id
               AND db.document_type = 'CCCD_BACK'
               AND db.status = 'ACTIVE'

            WHERE co.contract_id = ?
              AND co.status IN ('PENDING', 'ACTIVE')
            ORDER BY
                CASE co.occupant_role
                    WHEN 'PRIMARY' THEN 0
                    ELSE 1
                END,
                co.contract_occupant_id ASC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ContractOccupant o = mapRow(rs);
                    list.add(o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public int countPendingOrActiveByContractId(Connection conn, int contractId) throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM CONTRACT_OCCUPANT
            WHERE contract_id = ?
              AND status IN ('PENDING', 'ACTIVE')
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean belongsToPrimaryTenant(int contractId, int primaryTenantId) {
        String sql = """
            SELECT TOP 1 1
            FROM CONTRACT_OCCUPANT
            WHERE contract_id = ?
              AND tenant_id = ?
              AND occupant_role = 'PRIMARY'
              AND status IN ('PENDING', 'ACTIVE')
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setInt(2, primaryTenantId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean softRemoveMember(Connection conn, int contractOccupantId, int contractId) throws SQLException {
        String sql = """
            UPDATE CONTRACT_OCCUPANT
            SET status = 'REMOVED',
                updated_at = SYSDATETIME()
            WHERE contract_occupant_id = ?
              AND contract_id = ?
              AND occupant_role = 'MEMBER'
              AND status = 'PENDING'
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractOccupantId);
            ps.setInt(2, contractId);
            return ps.executeUpdate() > 0;
        }
    }

    public int activateOccupantsByContractId(Connection conn, int contractId) throws SQLException {
        String sql = """
            UPDATE CONTRACT_OCCUPANT
            SET status = 'ACTIVE',
                updated_at = SYSDATETIME()
            WHERE contract_id = ?
              AND status = 'PENDING'
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            return ps.executeUpdate();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean contractHasPrimary(int contractId) {
        String sql = """
            SELECT TOP 1 1
            FROM CONTRACT_OCCUPANT
            WHERE contract_id = ?
              AND occupant_role = 'PRIMARY'
              AND status IN ('PENDING', 'ACTIVE')
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

    private ContractOccupant mapRow(ResultSet rs) throws SQLException {
        ContractOccupant o = new ContractOccupant();

        o.setContractOccupantId(rs.getInt("contract_occupant_id"));
        o.setContractId(rs.getInt("contract_id"));
        o.setTenantId(rs.getInt("tenant_id"));
        o.setOccupantRole(rs.getString("occupant_role"));
        o.setStatus(rs.getString("status"));
        o.setMoveInDate(rs.getDate("move_in_date"));
        o.setMoveOutDate(rs.getDate("move_out_date"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));

        o.setFullName(rs.getString("full_name"));
        o.setIdentityCode(rs.getString("identity_code"));
        o.setPhoneNumber(rs.getString("phone_number"));
        o.setEmail(rs.getString("email"));
        o.setAddress(rs.getString("address"));
        o.setDateOfBirth(rs.getDate("date_of_birth"));

        Object genderObj = rs.getObject("gender");
        o.setGender(genderObj == null ? null : rs.getInt("gender"));

        o.setAvatar(rs.getString("avatar"));
        o.setAccountStatus(rs.getString("account_status"));

        o.setCccdFrontUrl(rs.getString("cccd_front_url"));
        o.setCccdBackUrl(rs.getString("cccd_back_url"));

        return o;
    }
}
