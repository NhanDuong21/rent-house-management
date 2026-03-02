package Controllers.admin;

import java.io.IOException;

import DALs.room.RoomImageDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminRoomImageSetCoverController extends HttpServlet {

    private final RoomImageDAO imgDao = new RoomImageDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int roomId = Integer.parseInt(req.getParameter("roomId"));
        int imageId = Integer.parseInt(req.getParameter("imageId"));

        boolean ok = imgDao.setCover(roomId, imageId);
        resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + (ok ? "&msg=cover_set" : "&err=cover_fail"));
    }
}
