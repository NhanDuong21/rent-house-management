package DALs.room;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Models.entity.RoomImage;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-07
 */
public class RoomImageDAO extends DBContext {

    @SuppressWarnings("CallToPrintStackTrace")
    public List<RoomImage> findByRoomId(int roomId) {
        String sql = """
            SELECT image_id, room_id, image_url, is_cover, sort_order
            FROM ROOM_IMAGE
            WHERE room_id = ?
            ORDER BY is_cover DESC, sort_order ASC, image_id ASC
        """;

        List<RoomImage> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomImage img = new RoomImage();
                    img.setImageId(rs.getInt("image_id"));
                    img.setRoomId(rs.getInt("room_id"));
                    img.setImageUrl(rs.getString("image_url"));
                    img.setCover(rs.getBoolean("is_cover"));
                    img.setSortOrder(rs.getInt("sort_order"));
                    list.add(img);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * mục đích của getMaxSortOrder và insertImage là khi updload 1 ảnh mới sẽ
     * đảm bảo không bị chèn ảnh lộn xộn thì trước khi insert nó sẽ check coi là
     * phòng hiện tại có bn ảnh, getMaxSortOrder sẽ lấy stt max+1 để cái ảnh mới
     * có sort_odder lớn nhất, giúp mình dễ sắp xếp ảnh nào hiện trc và sau
     */
    private int getMaxSortOrder(int roomId) throws Exception {
        String sql = "SELECT ISNULL(MAX(sort_order), 0) AS max_sort FROM ROOM_IMAGE WHERE room_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("max_sort");
                }
            }
        }
        return 0;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean insertImage(int roomId, String imageUrl) {
        String sql = "INSERT ROOM_IMAGE(room_id, image_url, is_cover, sort_order) VALUES (?, ?, 0, ?)";
        try {
            int nextSort = getMaxSortOrder(roomId) + 1;
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, roomId);
                ps.setString(2, imageUrl);
                ps.setInt(3, nextSort);
                return ps.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean setCover(int roomId, int imageId) {
        String resetSql = "UPDATE ROOM_IMAGE SET is_cover=0 WHERE room_id=?";
        String setSql = "UPDATE ROOM_IMAGE SET is_cover=1 WHERE room_id=? AND image_id=?";

        try {
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(resetSql)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            int updated;
            try (PreparedStatement ps = connection.prepareStatement(setSql)) {
                ps.setInt(1, roomId);
                ps.setInt(2, imageId);
                updated = ps.executeUpdate();
            }

            connection.commit();
            connection.setAutoCommit(true);
            return updated > 0;

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignored) {
            }
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * còn deleteImageReturnRoomId và ensureCover mục đích là auto đôn ảnh khác
     * lên làm cover nếu admin lỡ tay xóa nhầm cover current, lúc này
     * ensureCover sẽ nhảy vào check nếu phòng mà ko còn cover nữa thì sẽ auto
     * lấy tấm ảnh kế tiếp để làm cover
     */
    @SuppressWarnings("CallToPrintStackTrace")
    public Integer deleteImageReturnRoomId(int imageId) {
        String getSql = "SELECT room_id, is_cover FROM ROOM_IMAGE WHERE image_id=?";
        String delSql = "DELETE FROM ROOM_IMAGE WHERE image_id=?";

        try {
            Integer roomId = null;
            boolean wasCover = false;

            try (PreparedStatement ps = connection.prepareStatement(getSql)) {
                ps.setInt(1, imageId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }
                    roomId = rs.getInt("room_id");
                    wasCover = rs.getBoolean("is_cover");
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(delSql)) {
                ps.setInt(1, imageId);
                ps.executeUpdate();
            }

            if (wasCover) {
                ensureCover(roomId);
            }
            return roomId;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void ensureCover(int roomId) {
        String hasCoverSql = "SELECT 1 FROM ROOM_IMAGE WHERE room_id=? AND is_cover=1";
        String pickSql = """
        SELECT TOP 1 image_id
        FROM ROOM_IMAGE
        WHERE room_id=?
        ORDER BY sort_order ASC, image_id ASC
    """;
        String setSql = "UPDATE ROOM_IMAGE SET is_cover=1 WHERE image_id=?";

        try {
            // nếu roomId đó có cover rồi thì skip
            try (PreparedStatement ps = connection.prepareStatement(hasCoverSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return;
                    }
                }
            }

            int imageId = -1;
            try (PreparedStatement ps = connection.prepareStatement(pickSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        imageId = rs.getInt("image_id");
                    }
                }
            }

            if (imageId > 0) {
                try (PreparedStatement ps = connection.prepareStatement(setSql)) {
                    ps.setInt(1, imageId);
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
