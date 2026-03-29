package DALs.block;

import Models.entity.Block;
import Utils.database.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-03-02
 */
public class BlockDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(BlockDAO.class.getName());

    public List<Block> findAllActive() {
        String sql = """
                SELECT block_id, block_name
                FROM BLOCK
                ORDER BY block_name
                """;

        List<Block> list = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Block b = new Block();
                b.setBlockId(rs.getInt("block_id"));
                b.setBlockName(rs.getString("block_name"));
                list.add(b);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to find all blocks.", e);
        }

        return list;
    }

    public boolean exists(int blockId) {
        String sql = "SELECT 1 FROM BLOCK WHERE block_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, blockId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to check block existence. blockId=%d", blockId),
                    e);
        }

        return false;
    }

    public int insertAndReturnId(String blockName) {
        String sql = "INSERT INTO BLOCK(block_name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(
                sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, blockName);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to insert block. blockName=%s", blockName),
                    e);
        }

        return -1;
    }

    public Integer findIdByName(String blockName) {
        String sql = "SELECT block_id FROM BLOCK WHERE LOWER(block_name) = LOWER(?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, blockName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("block_id");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    String.format("Failed to find block id by name. blockName=%s", blockName),
                    e);
        }

        return null;
    }
}