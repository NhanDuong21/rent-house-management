package Controllers.admin;

import java.io.IOException;

import Models.entity.Room;
import Services.staff.RoomStaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminEditRoomController extends HttpServlet {

    private final RoomStaffService roomStaffService = new RoomStaffService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer id = parseIntNullable(req.getParameter("id"));

        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }

        Room room = roomStaffService.getRoomForEdit(id);

        if (room == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }

        req.setAttribute("room", room);
        req.setAttribute("images", roomStaffService.getRoomImages(id));
        req.setAttribute("blocks", roomStaffService.getActiveBlocks());
        req.setAttribute("isInactive", "INACTIVE".equalsIgnoreCase(room.getStatus()));

        req.getRequestDispatcher("/views/admin/editRoom.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String result = roomStaffService.updateRoom(req);

        Integer roomId = parseIntNullable(req.getParameter("roomId"));

        if ("updated".equals(result)) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&msg=updated");
        } else {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=" + result);
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
