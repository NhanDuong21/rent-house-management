/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DALs.maintenanceRequest;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Models.dto.MaintenanceRequestDTO;
import Models.dto.TenantMyRoomDTO;
import Utils.database.DBContext;

/**
 *
 * @author Truong Hoang Khang - CE190729
 */
public class MaintenanceRequestDAO extends DBContext {

    public List<MaintenanceRequestDTO> getAllRequests(int page, int pageSize, String search) {
        List<MaintenanceRequestDTO> list = new ArrayList<>();
        String sql = """
        SELECT
            mr.request_id,
            mr.tenant_id,
            r.room_number,
            t.full_name,
            mr.issue_category,
            mr.status,
            mr.description
        FROM MAINTENANCE_REQUEST mr
        JOIN ROOM r ON mr.room_id = r.room_id
        JOIN TENANT t ON mr.tenant_id = t.tenant_id
        WHERE r.room_number LIKE ?
        ORDER BY mr.created_at DESC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + search + "%");
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MaintenanceRequestDTO dto = new MaintenanceRequestDTO();
                dto.setRequestId(rs.getInt("request_id"));
                dto.setTenantId(rs.getInt("tenant_id"));
                dto.setRoomNumber(rs.getString("room_number"));
                dto.setFullName(rs.getString("full_name"));
                dto.setIssueCategory(rs.getString("issue_category"));
                dto.setStatus(rs.getString("status"));
                dto.setDescription(rs.getString("description"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public MaintenanceRequestDTO getRequestById(int id) {
        String sql = "SELECT "
                + "mr.request_id, "
                + "mr.tenant_id, "
                + "r.room_number, "
                + "t.full_name, "
                + "mr.issue_category, "
                + "mr.status, "
                + "mr.description, "
                + "mr.image_url, "
                + "mr.created_at, "
                + "mr.completed_at "
                + "FROM MAINTENANCE_REQUEST mr "
                + "JOIN ROOM r ON mr.room_id = r.room_id "
                + "JOIN TENANT t ON mr.tenant_id = t.tenant_id "
                + "WHERE mr.request_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MaintenanceRequestDTO dto = new MaintenanceRequestDTO();
                    dto.setRequestId(rs.getInt("request_id"));
                    dto.setTenantId(rs.getInt("tenant_id"));
                    dto.setRoomNumber(rs.getString("room_number"));
                    dto.setFullName(rs.getString("full_name"));
                    dto.setIssueCategory(rs.getString("issue_category"));
                    dto.setStatus(rs.getString("status"));
                    dto.setDescription(rs.getString("description"));
                    dto.setImageUrl(rs.getString("image_url"));
                    dto.setCreatedAt(rs.getTimestamp("created_at"));
                    dto.setCompletedAt(rs.getTimestamp("completed_at"));
                    return dto;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public int countRequest(String search) {
        String sql = "SELECT COUNT(*) FROM MAINTENANCE_REQUEST mr "
                + "JOIN ROOM r ON mr.room_id = r.room_id "
                + "WHERE r.room_number LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + search + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void updateStatus(int id, String status) {
        String sql = """
        UPDATE MAINTENANCE_REQUEST
        SET status=?,
            completed_at =
                CASE WHEN ?='DONE'
                     THEN GETDATE()
                     ELSE NULL
                END
        WHERE request_id=?
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MaintenanceRequestDTO> getRequestsByTenantId(int tenantId, int page, int pageSize) {
        List<MaintenanceRequestDTO> list = new ArrayList<>();
        String sql = """
        SELECT
            mr.request_id, mr.tenant_id, r.room_number, t.full_name,
            mr.issue_category, mr.status, mr.description, mr.created_at
        FROM MAINTENANCE_REQUEST mr
        JOIN ROOM r ON mr.room_id = r.room_id
        JOIN TENANT t ON mr.tenant_id = t.tenant_id
        WHERE mr.tenant_id = ?
        ORDER BY mr.created_at DESC
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.setInt(2, (page - 1) * pageSize);
            ps.setInt(3, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MaintenanceRequestDTO dto = new MaintenanceRequestDTO();
                dto.setRequestId(rs.getInt("request_id"));
                dto.setTenantId(rs.getInt("tenant_id"));
                dto.setRoomNumber(rs.getString("room_number"));
                dto.setFullName(rs.getString("full_name"));
                dto.setIssueCategory(rs.getString("issue_category"));
                dto.setStatus(rs.getString("status"));
                dto.setDescription(rs.getString("description"));
                dto.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countRequestByTenantId(int tenantId) {
        String sql = "SELECT COUNT(*) FROM MAINTENANCE_REQUEST WHERE tenant_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Integer getUtilityIdByName(String name) {
        String sql = "SELECT utility_id FROM UTILITY WHERE utility_name = ? AND is_active = 1";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("utility_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<TenantMyRoomDTO> getRoomsByTenantId(int tenantId) {
        List<TenantMyRoomDTO> list = new ArrayList<>();
        String sql = """
        SELECT r.room_id, r.room_number
        FROM CONTRACT c
        JOIN ROOM r ON c.room_id = r.room_id
        WHERE c.tenant_id = ? AND c.status = 'ACTIVE'
        ORDER BY r.room_number
    """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                TenantMyRoomDTO dto = new TenantMyRoomDTO();
                dto.setRoomId(rs.getInt("room_id"));
                dto.setRoomNumber(rs.getString("room_number"));
                list.add(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void createRequest(int tenantId, int roomId, String category, String description, String images, Integer utilityId) {
        String insertSql = """
        INSERT INTO MAINTENANCE_REQUEST
        (tenant_id, room_id, issue_category, utility_id, description, image_url, status)
        VALUES (?, ?, ?, ?, ?, ?, 'PENDING')
    """;
        try (PreparedStatement ps = connection.prepareStatement(insertSql)) {
            ps.setInt(1, tenantId);
            ps.setInt(2, roomId);
            ps.setString(3, category);
            if (utilityId != null) {
                ps.setInt(4, utilityId);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, description);
            ps.setString(6, images);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countPendingRequests() {

        String sql = """
        SELECT COUNT(*)
        FROM MAINTENANCE_REQUEST
        WHERE status = 'PENDING'
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }
}
