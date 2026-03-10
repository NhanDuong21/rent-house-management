/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.admin;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String blockNameRaw = req.getParameter("blockName");
            if (blockNameRaw == null || blockNameRaw.trim().isEmpty()) {
                req.setAttribute("blocks", blockDAO.findAllActive());
                req.setAttribute("err", "Please enter block name");
                req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
                return;
            }
            blockNameRaw = blockNameRaw.trim();
            if (!blockNameRaw.toLowerCase().startsWith("khu")) {
                req.setAttribute("blocks", blockDAO.findAllActive());
                req.setAttribute("err", "Block name must start with 'Khu'");
                req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
                return;
            }
            String remain = blockNameRaw.substring(3).trim();
            String finalBlockName = "Khu " + remain;

            Integer blockId = blockDAO.findIdByName(finalBlockName);
            if (blockId == null) {
                blockId = blockDAO.insertAndReturnId(finalBlockName);
                if (blockId == -1) {
                    req.setAttribute("blocks", blockDAO.findAllActive());
                    req.setAttribute("err", "Cannot create new block");
                    req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
                    return;
                }
            }
            Room r = new Room();
            r.setBlockId(blockId);
            r.setRoomNumber(req.getParameter("roomNumber"));
            r.setArea(parseDecimal(req.getParameter("area")));
            r.setPrice(parseDecimal(req.getParameter("price")));
            r.setFloor(parseInt(req.getParameter("floor")));
            r.setMaxTenants(parseInt(req.getParameter("maxTenants")));
            r.setStatus("AVAILABLE");
            r.setMezzanine(req.getParameter("isMezzanine") != null);
            r.setAirConditioning(req.getParameter("airConditioning") != null);
            r.setDescription(req.getParameter("description"));

            String roomInput = req.getParameter("roomNumber");

            if (roomInput == null || roomInput.trim().isEmpty()) {
                req.setAttribute("err", "Room number is required");
                req.setAttribute("blocks", blockDAO.findAllActive());
                req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
                return;
            }
            roomInput = roomInput.trim();
            String blockSuffix = finalBlockName.substring(4).trim();
            String finalRoomNumber;
            if (roomInput.toUpperCase().startsWith(blockSuffix.toUpperCase())) {
                finalRoomNumber = roomInput;
            } else {
                finalRoomNumber = blockSuffix + roomInput;
            }
            r.setRoomNumber(finalRoomNumber);
            Collection<Part> parts = req.getParts();
            int imageCount = 0;

            for (Part p : parts) {
                if ("images".equals(p.getName()) && p.getSize() > 0) {
                    imageCount++;
                }
            }
            if (imageCount > 12) {
                req.setAttribute("blocks", blockDAO.findAllActive());
                req.setAttribute("err", "A maximum of 12 images are allowed.");
                req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
                return;
            }
            int roomId = roomDAO.addRoom(r);
            if (roomId <= 0) {
                req.setAttribute("blocks", blockDAO.findAllActive());
                req.setAttribute("err", "Create room failed. Room number may already exist in this block.");
                req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
                return;
            }
            String uploadPath = getServletContext().getRealPath("/assets/images/rooms");
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            for (Part p : parts) {
                if ("images".equals(p.getName()) && p.getSize() > 0) {

                    String fileName = System.currentTimeMillis() + "_" + p.getSubmittedFileName();

                    p.write(uploadPath + File.separator + fileName);

                    imgDAO.insertImage(roomId, fileName);
                }
            }
            imgDAO.ensureCover(roomId);
            resp.sendRedirect(req.getContextPath() + "/admin/rooms?msg=created");
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("blocks", blockDAO.findAllActive());
            req.setAttribute("err", "System error: " + e.getMessage());
            req.getRequestDispatcher("/views/admin/createRoom.jsp").forward(req, resp);
        }
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
        } catch (Exception e) {
            return null;
        }
    }
}
