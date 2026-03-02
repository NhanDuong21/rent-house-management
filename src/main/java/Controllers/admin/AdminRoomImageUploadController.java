package Controllers.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.UUID;

import DALs.room.RoomImageDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5L * 1024 * 1024, //Mỗi file ảnh không được quá 5MB.
        maxRequestSize = 20L * 1024 * 1024 //Tổng dung lượng 1 lần gửi không quá 20MB
)
public class AdminRoomImageUploadController extends HttpServlet {

    private final RoomImageDAO imgDao = new RoomImageDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int roomId = Integer.parseInt(req.getParameter("roomId"));
        Part filePart = req.getPart("image"); // get data file tu admin

        if (filePart == null || filePart.getSize() == 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=no_file");
            return;
        }

        String uploadDir = req.getServletContext().getRealPath("/assets/images/rooms");
        new File(uploadDir).mkdirs(); //auto gen folder

        String submitted = Path.of(filePart.getSubmittedFileName()).getFileName().toString(); //get name file remove url
        String ext = submitted.contains(".") ? submitted.substring(submitted.lastIndexOf(".")) : ".jpg";
        String savedName = "room_" + roomId + "_" + UUID.randomUUID() + ext;

        File savedFile = new File(uploadDir, savedName); // uploadDir + "/" + savedName
        try (InputStream in = filePart.getInputStream(); OutputStream out = new FileOutputStream(savedFile)) {
            in.transferTo(out);
        }

        imgDao.insertImage(roomId, savedName);
        imgDao.ensureCover(roomId);

        resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&msg=uploaded");
    }
}
