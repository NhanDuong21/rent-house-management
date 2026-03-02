package Controllers.admin;

import java.io.IOException;

import DALs.contract.ContractDAO;
import DALs.room.RoomDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminDeleteRoomController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String idRaw = req.getParameter("id");
        int roomId;

        try {
            roomId = Integer.parseInt(idRaw);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }

        // chặn nếu có contract ACTIVE/PENDING
        if (contractDAO.hasBlockingContractByRoomId(roomId)) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=room_in_use");
            return;
        }

        boolean ok = roomDAO.updateStatus(roomId, "INACTIVE");
        if (!ok) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=delete_fail");
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/admin/rooms?msg=deleted");
    }
}
