package DALs.room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.dto.RoomFilterDTO;
import Models.entity.Room;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-06
 */
public class RoomDAO extends DBContext {

    //view available
    @Deprecated
    @SuppressWarnings("CallToPrintStackTrace")
    public List<Room> searchAvailable(RoomFilterDTO filterDTO) {
        List<Room> list = new ArrayList<>();
        String sql = """
    SELECT r.room_id, r.block_id, r.room_number, r.area, r.price, r.status, r.floor, r.max_tenants, r.is_mezzanine, r.description, r.has_air_conditioning, img.image_url AS cover_image
    FROM ROOM r
    INNER JOIN BLOCK b ON b.block_id = r.block_id
	LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
    WHERE r.status = 'AVAILABLE'
      AND (? IS NULL OR r.price >= ?)
      AND (? IS NULL OR r.price <= ?)
      AND (? IS NULL OR r.area  >= ?)
      AND (? IS NULL OR r.area  <= ?)
      AND (? IS NULL OR r.has_air_conditioning = ?) 
      AND (? IS NULL OR r.is_mezzanine = ?)
    ORDER BY b.block_name, r.room_number
""";

        try {
            PreparedStatement ps = connection.prepareStatement(sql);

            int i = 1;
            //moi query has 2 param
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setObject(i++, filterDTO.getHasAirConditioning()); // null/true/false
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasMezzanine()); // null/true/false
            ps.setObject(i++, filterDTO.getHasMezzanine());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setBlockId(rs.getInt("block_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setArea(rs.getBigDecimal("area"));
                r.setPrice(rs.getBigDecimal("price"));
                r.setStatus(rs.getString("status"));
                r.setFloor((Integer) rs.getObject("floor")); //oj easy debug
                r.setMaxTenants((Integer) rs.getObject("max_tenants"));
                r.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                r.setMezzanine(rs.getBoolean("is_mezzanine"));
                r.setRoomImage(rs.getString("cover_image")); // left join ROOM_IMAGE
                r.setDescription(rs.getString("description"));
                list.add(r);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //PAGINATION - GUEST - TENANT
    @SuppressWarnings("CallToPrintStackTrace")
    public List<Room> searchAvailablePaged(RoomFilterDTO filterDTO, int page, int pageSize) {
        List<Room> list = new ArrayList<>();

        String sql = """
        SELECT r.room_id, r.block_id, r.room_number, r.area, r.price, r.status, 
               r.floor, r.max_tenants, r.is_mezzanine, r.description, 
               r.has_air_conditioning, img.image_url AS cover_image
        FROM ROOM r
        INNER JOIN BLOCK b ON b.block_id = r.block_id
        LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
        WHERE r.status = 'AVAILABLE'
          AND (? IS NULL OR r.price >= ?)
          AND (? IS NULL OR r.price <= ?)
          AND (? IS NULL OR r.area  >= ?)
          AND (? IS NULL OR r.area  <= ?)
          AND (? IS NULL OR r.has_air_conditioning = ?)
          AND (? IS NULL OR r.is_mezzanine = ?)
        ORDER BY b.block_name, r.room_number
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;

            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasMezzanine());
            ps.setObject(i++, filterDTO.getHasMezzanine());

            int offset = (page - 1) * pageSize;
            ps.setInt(i++, offset);
            ps.setInt(i++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("room_id"));
                    r.setBlockId(rs.getInt("block_id"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setArea(rs.getBigDecimal("area"));
                    r.setPrice(rs.getBigDecimal("price"));
                    r.setStatus(rs.getString("status"));
                    r.setFloor((Integer) rs.getObject("floor"));
                    r.setMaxTenants((Integer) rs.getObject("max_tenants"));
                    r.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                    r.setMezzanine(rs.getBoolean("is_mezzanine"));
                    r.setRoomImage(rs.getString("cover_image"));
                    r.setDescription(rs.getString("description"));
                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    //method đếm tổng số lượng phòng đang ở status 
    //AVAILABLE dựa trên các điều kiện lọc (filter) 
    @SuppressWarnings("CallToPrintStackTrace")
    public int countAvailable(RoomFilterDTO filterDTO) {

        String sql = """
        SELECT COUNT(DISTINCT r.room_id) AS total
        FROM ROOM r
        INNER JOIN BLOCK b ON b.block_id = r.block_id
        LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
        WHERE r.status = 'AVAILABLE'
          AND (? IS NULL OR r.price >= ?)
          AND (? IS NULL OR r.price <= ?)
          AND (? IS NULL OR r.area  >= ?)
          AND (? IS NULL OR r.area  <= ?)
          AND (? IS NULL OR r.has_air_conditioning = ?)
          AND (? IS NULL OR r.is_mezzanine = ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;

            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasMezzanine());
            ps.setObject(i++, filterDTO.getHasMezzanine());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //display full room ( ko phan biet room status )
    @Deprecated
    @SuppressWarnings("CallToPrintStackTrace")
    public List<Room> searchAll(RoomFilterDTO filterDTO) {
        List<Room> list = new ArrayList<>();
        String sql = """
    SELECT r.room_id, r.block_id, r.room_number, r.area, r.price, r.status, r.floor, r.max_tenants, r.is_mezzanine, r.description, r.has_air_conditioning, img.image_url AS cover_image
    FROM ROOM r
    INNER JOIN BLOCK b ON b.block_id = r.block_id
	LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
    WHERE 1 = 1
      AND (? IS NULL OR r.price >= ?)
      AND (? IS NULL OR r.price <= ?)
      AND (? IS NULL OR r.area  >= ?)
      AND (? IS NULL OR r.area  <= ?)
      AND (? IS NULL OR r.has_air_conditioning = ?) 
      AND (? IS NULL OR r.is_mezzanine = ?)
    ORDER BY b.block_name, r.room_number
    """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            int i = 1;
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasMezzanine());
            ps.setObject(i++, filterDTO.getHasMezzanine());

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setBlockId(rs.getInt("block_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setArea(rs.getBigDecimal("area"));
                r.setPrice(rs.getBigDecimal("price"));
                r.setStatus(rs.getString("status"));
                r.setFloor((Integer) rs.getObject("floor"));
                r.setMaxTenants((Integer) rs.getObject("max_tenants"));
                r.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                r.setMezzanine(rs.getBoolean("is_mezzanine"));
                r.setRoomImage(rs.getString("cover_image"));
                r.setDescription(rs.getString("description"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //PAGINATION - STAFF
    @SuppressWarnings("CallToPrintStackTrace")
    public List<Room> searchAllPaged(RoomFilterDTO filterDTO, int page, int pageSize) {
        List<Room> list = new ArrayList<>();

        String sql = """
        SELECT r.room_id, r.block_id, r.room_number, r.area, r.price, r.status, 
               r.floor, r.max_tenants, r.is_mezzanine, r.description, 
               r.has_air_conditioning, img.image_url AS cover_image
        FROM ROOM r
        INNER JOIN BLOCK b ON b.block_id = r.block_id
        LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
        WHERE 1=1
          AND (? IS NULL OR r.price >= ?)
          AND (? IS NULL OR r.price <= ?)
          AND (? IS NULL OR r.area  >= ?)
          AND (? IS NULL OR r.area  <= ?)
          AND (? IS NULL OR r.has_air_conditioning = ?)
          AND (? IS NULL OR r.is_mezzanine = ?)
        ORDER BY b.block_name, r.room_number
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;

            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasMezzanine());
            ps.setObject(i++, filterDTO.getHasMezzanine());

            int offset = (page - 1) * pageSize;
            ps.setInt(i++, offset);
            ps.setInt(i++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("room_id"));
                    r.setBlockId(rs.getInt("block_id"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setArea(rs.getBigDecimal("area"));
                    r.setPrice(rs.getBigDecimal("price"));
                    r.setStatus(rs.getString("status"));
                    r.setFloor((Integer) rs.getObject("floor"));
                    r.setMaxTenants((Integer) rs.getObject("max_tenants"));
                    r.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                    r.setMezzanine(rs.getBoolean("is_mezzanine"));
                    r.setRoomImage(rs.getString("cover_image"));
                    r.setDescription(rs.getString("description"));
                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public int countAll(RoomFilterDTO filterDTO) {

        String sql = """
        SELECT COUNT(DISTINCT r.room_id) AS total
        FROM ROOM r
        INNER JOIN BLOCK b ON b.block_id = r.block_id
        LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
        WHERE 1=1
          AND (? IS NULL OR r.price >= ?)
          AND (? IS NULL OR r.price <= ?)
          AND (? IS NULL OR r.area  >= ?)
          AND (? IS NULL OR r.area  <= ?)
          AND (? IS NULL OR r.has_air_conditioning = ?)
          AND (? IS NULL OR r.is_mezzanine = ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;

            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasMezzanine());
            ps.setObject(i++, filterDTO.getHasMezzanine());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    //display cover image trong detail
    @SuppressWarnings("CallToPrintStackTrace")
    public Room findById(int roomId) {
        String sql = """
SELECT        ROOM.room_id, ROOM.block_id, BLOCK.block_name, ROOM.room_number, ROOM.area, ROOM.price, ROOM.status, ROOM.floor, ROOM.max_tenants, ROOM.is_mezzanine, ROOM.description, 
                         ROOM.has_air_conditioning, img.image_url AS cover_image
FROM            ROOM INNER JOIN
                         BLOCK ON ROOM.block_id = BLOCK.block_id
                         LEFT JOIN ROOM_IMAGE img ON img.room_id = ROOM.room_id AND img.is_cover = 1
WHERE   ROOM.room_id = ?
        """;

        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery();) {
                if (rs.next()) {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("room_id"));
                    r.setBlockId(rs.getInt("block_id"));
                    r.setBlockName(rs.getString("block_name"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setArea(rs.getBigDecimal("area"));
                    r.setPrice(rs.getBigDecimal("price"));
                    r.setStatus(rs.getString("status"));
                    r.setFloor((Integer) rs.getObject("floor")); //oj easy debug
                    r.setMaxTenants((Integer) rs.getObject("max_tenants"));
                    r.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                    r.setMezzanine(rs.getBoolean("is_mezzanine"));
                    r.setRoomImage(rs.getString("cover_image"));
                    r.setDescription(rs.getString("description"));
                    return r;
                }
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Room> findAvailableRooms() {
        List<Room> list = new ArrayList<>();

        String sql = """
        SELECT room_id, room_number, price
        FROM ROOM
        WHERE status = 'AVAILABLE'
        ORDER BY room_number
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room r = new Room();
                r.setRoomId(rs.getInt("room_id"));
                r.setRoomNumber(rs.getString("room_number"));
                r.setPrice(rs.getBigDecimal("price"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateStatus(int roomId, String status) {
        String sql = "UPDATE ROOM SET status=? WHERE room_id=? AND status <> 'INACTIVE'";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean restoreRoom(int roomId) {
        String sql = "UPDATE ROOM SET status='AVAILABLE' WHERE room_id=? AND status='INACTIVE'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertRoom(int blockId,
            String roomNumber,
            double area,
            double price,
            String status,
            int floor,
            int maxTenants,
            boolean isMezzanine,
            boolean hasAirConditioning,
            String description) {

        String query = "INSERT INTO ROOM (block_id, room_number, area, price, status, floor, max_tenants, is_mezzanine, has_air_conditioning, description) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            var ps = connection.prepareStatement(query);

            ps.setInt(1, blockId);
            ps.setString(2, roomNumber);
            ps.setDouble(3, area);
            ps.setDouble(4, price);
            ps.setString(5, status);
            ps.setInt(6, floor);
            ps.setInt(7, maxTenants);
            ps.setBoolean(8, isMezzanine);
            ps.setBoolean(9, hasAirConditioning);
            ps.setString(10, description);

            int rs = ps.executeUpdate();
            return rs > 0;

        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Room> searchAllPagedV2(RoomFilterDTO filterDTO, int page, int pageSize) {
        List<Room> list = new ArrayList<>();

        String sql = """
        SELECT r.room_id, r.block_id, b.block_name, r.room_number, r.area, r.price, r.status,
               r.floor, r.max_tenants, r.is_mezzanine, r.description,
               r.has_air_conditioning, img.image_url AS cover_image
        FROM ROOM r
        INNER JOIN BLOCK b ON b.block_id = r.block_id
        LEFT JOIN ROOM_IMAGE img ON img.room_id = r.room_id AND img.is_cover = 1
        WHERE 1=1
          AND (? IS NULL OR r.price >= ?)
          AND (? IS NULL OR r.price <= ?)
          AND (? IS NULL OR r.area  >= ?)
          AND (? IS NULL OR r.area  <= ?)
          AND (? IS NULL OR r.has_air_conditioning = ?)
          AND (? IS NULL OR r.is_mezzanine = ?)
        ORDER BY b.block_name, r.room_number
        OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            int i = 1;

            ps.setBigDecimal(i++, filterDTO.getMinPrice());
            ps.setBigDecimal(i++, filterDTO.getMinPrice());

            ps.setBigDecimal(i++, filterDTO.getMaxPrice());
            ps.setBigDecimal(i++, filterDTO.getMaxPrice());

            ps.setBigDecimal(i++, filterDTO.getMinArea());
            ps.setBigDecimal(i++, filterDTO.getMinArea());

            ps.setBigDecimal(i++, filterDTO.getMaxArea());
            ps.setBigDecimal(i++, filterDTO.getMaxArea());

            ps.setObject(i++, filterDTO.getHasAirConditioning());
            ps.setObject(i++, filterDTO.getHasAirConditioning());

            ps.setObject(i++, filterDTO.getHasMezzanine());
            ps.setObject(i++, filterDTO.getHasMezzanine());

            int offset = (page - 1) * pageSize;
            ps.setInt(i++, offset);
            ps.setInt(i++, pageSize);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setRoomId(rs.getInt("room_id"));
                    r.setBlockId(rs.getInt("block_id"));
                    r.setBlockName(rs.getString("block_name"));
                    r.setRoomNumber(rs.getString("room_number"));
                    r.setArea(rs.getBigDecimal("area"));
                    r.setPrice(rs.getBigDecimal("price"));
                    r.setStatus(rs.getString("status"));
                    r.setFloor((Integer) rs.getObject("floor"));
                    r.setMaxTenants((Integer) rs.getObject("max_tenants"));
                    r.setAirConditioning(rs.getBoolean("has_air_conditioning"));
                    r.setMezzanine(rs.getBoolean("is_mezzanine"));
                    r.setRoomImage(rs.getString("cover_image"));
                    r.setDescription(rs.getString("description"));
                    list.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean updateRoom(Room r) {
        String sql = """
        UPDATE ROOM SET block_id=?, room_number=?, area=?, price=?, status=?, floor=?, max_tenants=?, is_mezzanine=?,
                        has_air_conditioning=?, description=?
        WHERE room_id=? AND status NOT IN ('INACTIVE','OCCUPIED')
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            int i = 1;
            ps.setInt(i++, r.getBlockId());
            ps.setString(i++, r.getRoomNumber());
            ps.setBigDecimal(i++, r.getArea());
            ps.setBigDecimal(i++, r.getPrice());
            ps.setString(i++, r.getStatus());
            ps.setObject(i++, r.getFloor());
            ps.setObject(i++, r.getMaxTenants());
            ps.setBoolean(i++, r.isMezzanine());
            ps.setBoolean(i++, r.isAirConditioning());
            ps.setString(i++, r.getDescription());
            ps.setInt(i++, r.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public int addRoom(Room r) {

        String sql = """
        INSERT INTO ROOM (
            block_id,
            room_number,
            area,
            price,
            status,
            floor,
            max_tenants,
            is_mezzanine,
            has_air_conditioning,
            description
        )
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (PreparedStatement ps = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            int i = 1;

            ps.setInt(i++, r.getBlockId());
            ps.setString(i++, r.getRoomNumber());
            ps.setBigDecimal(i++, r.getArea());
            ps.setBigDecimal(i++, r.getPrice());
            ps.setString(i++, r.getStatus());
            ps.setObject(i++, r.getFloor());
            ps.setObject(i++, r.getMaxTenants());
            ps.setBoolean(i++, r.isMezzanine());
            ps.setBoolean(i++, r.isAirConditioning());
            ps.setString(i++, r.getDescription());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // room_id
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }
}
