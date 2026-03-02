package DALs.block;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import Models.entity.Block;
import Utils.database.DBContext;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-03-02
 */
public class BlockDAO extends DBContext {

    @SuppressWarnings("CallToPrintStackTrace")
    public List<Block> findAllActive() {
        String sql = """
            SELECT block_id, block_name
            FROM BLOCK
            ORDER BY block_name
        """;

        List<Block> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Block b = new Block();
                b.setBlockId(rs.getInt("block_id"));
                b.setBlockName(rs.getString("block_name"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    //check block exits
    @SuppressWarnings("CallToPrintStackTrace")
    public boolean exists(int blockId) {
        String sql = "SELECT 1 FROM BLOCK WHERE block_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, blockId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
