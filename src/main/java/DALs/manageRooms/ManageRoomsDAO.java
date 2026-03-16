package DALs.manageRooms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Models.entity.Room;
import Utils.database.DBContext;

/**
 *
 * @author Truong Hoang Khang - CE190729
 */
public class ManageRoomsDAO extends DBContext {

    public List<Room> fetchAllRoom(int pageIndex, int pageSize, String search, String status) {
        List<Room> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder("""
            SELECT r.*, b.block_name
            FROM Room r
            JOIN Block b ON r.block_id = b.block_id
            WHERE r.status IN ('AVAILABLE','MAINTENANCE')
              AND r.room_number LIKE ?
        """);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND r.status = ? ");
        }

        sql.append("""
            ORDER BY r.room_id
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
        """);

        int offset = (pageIndex - 1) * pageSize;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int index = 1;

            ps.setString(index++, "%" + search + "%");

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(index++, status);
            }

            ps.setInt(index++, offset);
            ps.setInt(index, pageSize);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setBlockName(rs.getString("block_name"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setArea(rs.getBigDecimal("area"));
                r.setPrice(rs.getBigDecimal("price"));
                r.setFloor(rs.getInt("floor"));
                r.setMaxTenants(rs.getInt("max_tenants"));
                r.setMezzanine(rs.getBoolean("is_mezzanine"));
                r.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countRoom(String search, String status) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM Room r
            WHERE r.status IN ('AVAILABLE','MAINTENANCE')
              AND r.room_number LIKE ?
        """);

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND r.status = ? ");
        }

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int index = 1;

            ps.setString(index++, "%" + search + "%");

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(index++, status);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Room getRoomById(int id) {
        String sql = "SELECT room_id, status FROM Room WHERE room_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setStatus(rs.getString("status"));
                return r;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE Room SET status=? WHERE room_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}