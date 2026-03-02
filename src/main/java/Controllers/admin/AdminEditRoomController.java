package Controllers.admin;

import java.io.IOException;
import java.math.BigDecimal;

import DALs.block.BlockDAO;
import DALs.room.RoomDAO;
import DALs.room.RoomImageDAO;
import Models.entity.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminEditRoomController extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomImageDAO imgDAO = new RoomImageDAO();
    private final BlockDAO blockDAO = new BlockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer idObj = parseIntNullable(req.getParameter("id"));
        if (idObj == null || idObj <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }
        int id = idObj;

        Room room = roomDAO.findById(id);
        if (room == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }

        req.setAttribute("room", room);
        req.setAttribute("images", imgDAO.findByRoomId(id));
        req.setAttribute("blocks", blockDAO.findAllActive());
        req.getRequestDispatcher("/views/admin/editRoom.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        Integer roomIdObj = parseIntNullable(req.getParameter("roomId"));
        if (roomIdObj == null || roomIdObj <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }
        int roomId = roomIdObj;

        Room r = roomDAO.findById(roomId);
        if (r == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }

        // đọc từ form (safe)
        BigDecimal price = parseDecimal(req.getParameter("price"));
        Integer floor = parseIntNullable(req.getParameter("floor"));
        Integer maxTenants = parseIntNullable(req.getParameter("maxTenants"));

        Integer blockIdObj = parseIntNullable(req.getParameter("blockId"));
        if (blockIdObj == null || blockIdObj <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=invalid_block");
            return;
        }
        int blockId = blockIdObj;

        if (!blockDAO.exists(blockId)) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=invalid_block");
            return;
        }

        // set vào entity
        r.setBlockId(blockId);
        r.setRoomNumber(req.getParameter("roomNumber"));
        r.setStatus(req.getParameter("status"));
        r.setArea(parseDecimal(req.getParameter("area")));
        r.setPrice(price != null ? price : BigDecimal.ZERO);
        r.setFloor(floor);
        r.setMaxTenants(maxTenants);

        String ac = req.getParameter("airConditioning");
        r.setAirConditioning("1".equals(ac) || "on".equalsIgnoreCase(ac));

        String mez = req.getParameter("isMezzanine");
        r.setMezzanine("1".equals(mez) || "on".equalsIgnoreCase(mez));

        r.setDescription(req.getParameter("description"));

        boolean ok = roomDAO.updateRoom(r);
        resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + (ok ? "&msg=updated" : "&err=update_fail"));
    }

    private BigDecimal parseDecimal(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                return null;
            }
            return new BigDecimal(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseIntNullable(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                return null;
            }
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
