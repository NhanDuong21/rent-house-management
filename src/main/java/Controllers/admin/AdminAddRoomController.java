/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.admin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

import DALs.block.BlockDAO;
import DALs.room.RoomDAO;
import DALs.room.RoomImageDAO;
import Models.entity.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 *
 * @author truon
 */
@MultipartConfig
public class AdminAddRoomController extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final RoomImageDAO imgDAO = new RoomImageDAO();
    private final BlockDAO blockDAO = new BlockDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("blocks", blockDAO.findAllActive());
        req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
    }

    @Override
    @SuppressWarnings("CallToPrintStackTrace")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        try {
            String blockMode = req.getParameter("blockMode");
            String blockIdRaw = req.getParameter("blockId");
            String newBlockNameRaw = req.getParameter("newBlockName");

            Integer blockId = null;
            String finalBlockName = null;

            if ("existing".equals(blockMode)) {
                if (blockIdRaw == null || blockIdRaw.trim().isEmpty()) {
                    forwardWithError(req, resp, "Please select a block");
                    return;
                }

                try {
                    blockId = Integer.parseInt(blockIdRaw);
                } catch (NumberFormatException e) {
                    forwardWithError(req, resp, "Invalid block selected");
                    return;
                }

                if (!blockDAO.exists(blockId)) {
                    forwardWithError(req, resp, "Selected block does not exist");
                    return;
                }

                finalBlockName = blockDAO.findNameById(blockId);
                if (finalBlockName == null || finalBlockName.trim().isEmpty()) {
                    forwardWithError(req, resp, "Cannot load block information");
                    return;
                }

            } else if ("new".equals(blockMode)) {
                if (newBlockNameRaw == null || newBlockNameRaw.trim().isEmpty()) {
                    forwardWithError(req, resp, "Please enter new block name");
                    return;
                }

                finalBlockName = normalizeBlockName(newBlockNameRaw);
                if (finalBlockName == null) {
                    {
                        forwardWithError(req, resp, "Block name must be exactly 1 letter from A to Z");
                        return;
                    }
                }

                Integer existingBlockId = blockDAO.findIdByName(finalBlockName);
                if (existingBlockId != null) {
                    blockId = existingBlockId;
                } else {
                    blockId = blockDAO.insertAndReturnId(finalBlockName);
                    if (blockId == -1) {
                        forwardWithError(req, resp, "Cannot create new block");
                        return;
                    }
                }

            } else {
                forwardWithError(req, resp, "Invalid block mode");
                return;
            }

            String roomInput = req.getParameter("roomNumber");
            if (roomInput == null || roomInput.trim().isEmpty()) {
                forwardWithError(req, resp, "Room number is required");
                return;
            }

            roomInput = roomInput.trim();

            if (!roomInput.matches("\\d{3}")) {
                forwardWithError(req, resp, "Room number must be exactly 3 digits");
                return;
            }

            Room r = new Room();
            r.setBlockId(blockId);
            r.setArea(parseDecimal(req.getParameter("area")));
            r.setPrice(parseDecimal(req.getParameter("price")));
            r.setFloor(parseInt(req.getParameter("floor")));
            r.setMaxTenants(parseInt(req.getParameter("maxTenants")));
            r.setStatus("AVAILABLE");
            r.setMezzanine(req.getParameter("isMezzanine") != null);
            r.setAirConditioning(req.getParameter("airConditioning") != null);
            r.setDescription(req.getParameter("description"));

            String blockLetter = extractBlockLetter(finalBlockName);
            if (blockLetter == null) {
                forwardWithError(req, resp, "Invalid block name format");
                return;
            }

            String finalRoomNumber = blockLetter + roomInput;
            r.setRoomNumber(finalRoomNumber);

            Collection<Part> parts = req.getParts();
            int imageCount = 0;

            for (Part p : parts) {
                if ("images".equals(p.getName()) && p.getSize() > 0) {
                    String contentType = p.getContentType();
                    if (contentType == null || !contentType.startsWith("image/")) {
                        forwardWithError(req, resp, "Only image files are allowed.");
                        return;
                    }
                    imageCount++;
                }
            }
            if (imageCount > 12) {
                forwardWithError(req, resp, "A maximum of 12 images are allowed.");
                return;
            }
            int roomId = roomDAO.addRoom(r);
            if (roomId <= 0) {
                forwardWithError(req, resp, "Create room failed. Room number may already exist in this block.");
                return;
            }
            String uploadPath = getServletContext().getRealPath("/assets/images/rooms");
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            for (Part p : parts) {
                if ("images".equals(p.getName()) && p.getSize() > 0) {
                    String originalFileName = p.getSubmittedFileName();
                    String ext = "";

                    int dotIndex = originalFileName.lastIndexOf('.');
                    if (dotIndex >= 0) {
                        ext = originalFileName.substring(dotIndex);
                    }
                    String fileName = UUID.randomUUID().toString() + ext;
                    p.write(uploadPath + File.separator + fileName);
                    imgDAO.insertImage(roomId, fileName);
                }
            }
            imgDAO.ensureCover(roomId);
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?msg=created");
        } catch (ServletException | IOException e) {
            e.printStackTrace();
            forwardWithError(req, resp, "System error: " + e.getMessage());
        }
    }

    private void forwardWithError(HttpServletRequest req, HttpServletResponse resp, String error)
            throws ServletException, IOException {
        req.setAttribute("blocks", blockDAO.findAllActive());
        req.setAttribute("err", error);
        req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
    }

    private String normalizeBlockName(String raw) {
        if (raw == null) {
            return null;
        }
        raw = raw.trim().toUpperCase();
        if (!raw.matches("[A-Z]")) {
            return null;
        }
        return "Khu " + raw;
    }

    private BigDecimal parseDecimal(String s) {
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private Integer parseInt(String s) {
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String extractBlockLetter(String blockName) {
        if (blockName == null) {
            return null;
        }
        blockName = blockName.trim();
        if (!blockName.matches("Khu\\s+[A-Z]")) {
            return null;
        }
        return blockName.substring(blockName.length() - 1);
    }
}
