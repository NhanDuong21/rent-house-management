package Controllers.admin;

import java.io.IOException;

import DALs.room.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminRestoreRoomController extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Integer idObj = parseIntNullable(req.getParameter("id"));
        if (idObj == null || idObj <= 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }

        boolean ok = roomDAO.restoreRoom(idObj);
        resp.sendRedirect(req.getContextPath() + "/admin/rooms" + (ok ? "?msg=restored" : "?err=restore_fail"));
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
