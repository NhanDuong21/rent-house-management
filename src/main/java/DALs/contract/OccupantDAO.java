package DALs.contract;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Models.entity.Occupant;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-03-17
 */
public class OccupantDAO extends DBContext {

    public int insertOccupant(Connection conn, Occupant o) throws SQLException {
        String sql = """
            INSERT INTO OCCUPANT
                (contract_id, full_name, identity_code, phone_number, email, address, date_of_birth, gender, status)
            VALUES
                (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, o.getContractId());
            ps.setString(2, o.getFullName());
            ps.setString(3, o.getIdentityCode());
            ps.setString(4, o.getPhoneNumber());
            ps.setString(5, o.getEmail());
            ps.setString(6, o.getAddress());
            ps.setDate(7, o.getDateOfBirth());
            if (o.getGender() == null) {
                ps.setNull(8, java.sql.Types.TINYINT);
            } else {
                ps.setInt(8, o.getGender());
            }
            ps.setString(9, o.getStatus());

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

    public int countPendingOrActiveByContractId(Connection conn, int contractId) throws SQLException {
        String sql = """
            SELECT COUNT(*)
            FROM OCCUPANT
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

    public int activateOccupantsByContractId(Connection conn, int contractId) throws SQLException {
        String sql = """
            UPDATE OCCUPANT
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

    public int removeOccupantsByContractId(Connection conn, int contractId) throws SQLException {
        String sql = """
            UPDATE OCCUPANT
            SET status = 'REMOVED',
                updated_at = SYSDATETIME()
            WHERE contract_id = ?
              AND status = 'ACTIVE'
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            return ps.executeUpdate();
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Occupant> findByContractId(int contractId) {
        List<Occupant> list = new ArrayList<>();

        String sql = """
            SELECT
                o.occupant_id,
                o.contract_id,
                o.full_name,
                o.identity_code,
                o.phone_number,
                o.email,
                o.address,
                o.date_of_birth,
                o.gender,
                o.status,
                o.created_at,
                o.updated_at,
                df.file_url AS cccd_front_url,
                db.file_url AS cccd_back_url
            FROM OCCUPANT o
            LEFT JOIN OCCUPANT_DOCUMENT df
                ON df.occupant_id = o.occupant_id
               AND df.document_type = 'CCCD_FRONT'
               AND df.status = 'ACTIVE'
            LEFT JOIN OCCUPANT_DOCUMENT db
                ON db.occupant_id = o.occupant_id
               AND db.document_type = 'CCCD_BACK'
               AND db.status = 'ACTIVE'
            WHERE o.contract_id = ?
              AND o.status IN ('PENDING', 'ACTIVE')
            ORDER BY o.occupant_id ASC
        """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Occupant o = new Occupant();
                    o.setOccupantId(rs.getInt("occupant_id"));
                    o.setContractId(rs.getInt("contract_id"));
                    o.setFullName(rs.getString("full_name"));
                    o.setIdentityCode(rs.getString("identity_code"));
                    o.setPhoneNumber(rs.getString("phone_number"));
                    o.setEmail(rs.getString("email"));
                    o.setAddress(rs.getString("address"));
                    o.setDateOfBirth(rs.getDate("date_of_birth"));

                    Object genderObj = rs.getObject("gender");
                    o.setGender(genderObj == null ? null : rs.getInt("gender"));

                    o.setStatus(rs.getString("status"));
                    o.setCreatedAt(rs.getTimestamp("created_at"));
                    o.setUpdatedAt(rs.getTimestamp("updated_at"));
                    o.setCccdFrontUrl(rs.getString("cccd_front_url"));
                    o.setCccdBackUrl(rs.getString("cccd_back_url"));
                    list.add(o);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
