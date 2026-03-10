package Controllers.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
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
        maxFileSize = 5L * 1024 * 1024,
        maxRequestSize = 20L * 1024 * 1024
)
public class AdminRoomImageUploadController extends HttpServlet {

    private final RoomImageDAO imgDao = new RoomImageDAO();

    private String getSourceUploadPath() {
        return getServletContext().getInitParameter("roomImageSourceDir");
    }

    private String getRuntimeUploadPath(HttpServletRequest req) {
        return req.getServletContext().getRealPath("/assets/images/rooms");
    }

    private void saveToFile(Part filePart, File dest) throws IOException {
        File parent = dest.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }

        try (InputStream in = filePart.getInputStream(); OutputStream out = new FileOutputStream(dest)) {
            in.transferTo(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int roomId;
        try {
            roomId = Integer.parseInt(req.getParameter("roomId"));
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?err=invalid_room");
            return;
        }

        Part filePart = req.getPart("image");
        if (filePart == null || filePart.getSize() == 0) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=no_file");
            return;
        }

        String contentType = filePart.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=invalid_file");
            return;
        }

        String submittedFileName = filePart.getSubmittedFileName();
        if (submittedFileName == null || submittedFileName.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=invalid_file");
            return;
        }

        String safeFileName = Path.of(submittedFileName).getFileName().toString();
        String ext = safeFileName.contains(".")
                ? safeFileName.substring(safeFileName.lastIndexOf(".")).toLowerCase()
                : ".jpg";

        Set<String> allowedExts = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");
        if (!allowedExts.contains(ext)) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=invalid_ext");
            return;
        }

        String sourceDir = getSourceUploadPath();
        String runtimeDir = getRuntimeUploadPath(req);

        if (sourceDir == null || sourceDir.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=upload_path_missing");
            return;
        }

        String savedName = "room_" + roomId + "_" + UUID.randomUUID() + ext;

        File sourceFile = new File(sourceDir, savedName);
        File runtimeFile = new File(runtimeDir, savedName);

        try {
            saveToFile(filePart, sourceFile);

            File runtimeParent = runtimeFile.getParentFile();
            if (!runtimeParent.exists()) {
                runtimeParent.mkdirs();
            }

            if (!sourceFile.getCanonicalPath().equals(runtimeFile.getCanonicalPath())) {
                Files.copy(sourceFile.toPath(), runtimeFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            System.out.println("source path: " + sourceFile.getAbsolutePath());
            System.out.println("source tomcat: " + runtimeFile.getAbsolutePath());
            System.out.println("file save: " + savedName);

        } catch (IOException e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&err=upload_fail");
            return;
        }

        imgDao.insertImage(roomId, savedName);
        imgDao.ensureCover(roomId);

        resp.sendRedirect(req.getContextPath() + "/admin/rooms/edit?id=" + roomId + "&msg=uploaded");
    }
}
