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

    private String getSourceUploadPath() {
        return getServletContext().getInitParameter("roomImageSourceDir");
    }

    private String getRuntimeUploadPath(HttpServletRequest req) {
        return req.getServletContext().getRealPath("/assets/images/rooms");
    }

    private void deleteIfExists(String folderPath, String filename) {
        if (folderPath == null || folderPath.isBlank()) {
            return;
        }

        File file = new File(folderPath, filename);
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        int imageId;
        try {
            imageId = Integer.parseInt(req.getParameter("imageId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_image");
            return;
        }

        String filename = req.getParameter("filename");

        Integer roomId = imgDao.deleteImageReturnRoomId(imageId);
        if (roomId == null) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=delete_fail");
            return;
        }

        if (filename != null && !filename.isBlank()) {
            filename = new File(filename).getName();

            deleteIfExists(getSourceUploadPath(), filename);
            deleteIfExists(getRuntimeUploadPath(req), filename);
        }

        resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&msg=img_deleted");
    }
}
