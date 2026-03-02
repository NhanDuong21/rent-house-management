package Controllers.admin;

import java.io.File;
import java.io.IOException;

import DALs.room.RoomImageDAO;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminRoomImageDeleteController extends HttpServlet {

    private final RoomImageDAO imgDao = new RoomImageDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int imageId = Integer.parseInt(req.getParameter("imageId"));
        String filename = req.getParameter("filename"); // để xóa file vật lý 

        Integer roomId = imgDao.deleteImageReturnRoomId(imageId);
        if (roomId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=delete_fail");
            return;
        }

        if (filename != null && !filename.isBlank()) {
            String path = req.getServletContext().getRealPath("/assets/images/rooms/" + filename);
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
        }

        resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&msg=img_deleted");
    }
}
