/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Room> fetchAllRoom(int pageIndex, int pageSize) {
        List<Room> list = new ArrayList<>();

        String sql
                = "SELECT r.*, b.block_name "
                + "FROM Room r "
                + "JOIN Block b ON r.block_id = b.block_id "
                + "ORDER BY r.room_id "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        int offset = (pageIndex - 1) * pageSize;

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, offset);
            ps.setInt(2, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room p = new Room();
                    p.setRoomId(rs.getInt("room_id"));
                    p.setBlockName(rs.getString("block_name"));
                    p.setRoomNumber(rs.getString("room_number"));
                    p.setArea(rs.getBigDecimal("area"));
                    p.setPrice(rs.getBigDecimal("price"));
                    p.setFloor(rs.getInt("floor"));
                    p.setMaxTenants(rs.getInt("max_tenants"));
                    p.setMezzanine(rs.getBoolean("is_mezzanine"));
                    p.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                    p.setStatus(rs.getString("status"));
                    list.add(p);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public int countRoom() {
        String sql = "SELECT COUNT(*) FROM Room";
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Room getRoomById(int id) {
        String sql = "select room_id, status from Room where room_id=?";
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
