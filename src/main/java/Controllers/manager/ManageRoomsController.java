/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controllers.manager;

import java.io.IOException;
import java.util.List;

import DALs.manageRooms.ManageRoomsDAO;
import Models.entity.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Truong Hoang Khang - CE190729
 */
public class ManageRoomsController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ManageRoomsDAO dao = new ManageRoomsDAO();

        int pageIndex = 1;
        int pageSize = 10;

        String page = request.getParameter("page");

        if (page != null) {
            try {
                pageIndex = Integer.parseInt(page);
            } catch (Exception e) {
                pageIndex = 1;
            }
        }

        int totalRoom = dao.countRoom();

        int totalPage = (int) Math.ceil((double) totalRoom / pageSize);

        if (totalPage == 0) {
            totalPage = 1;
        }

        if (pageIndex < 1) {
            pageIndex = 1;
        }

        if (pageIndex > totalPage) {
            pageIndex = totalPage;
        }

        List<Room> rooms = dao.fetchAllRoom(pageIndex, pageSize);

        request.setAttribute("Rooms", rooms);
        request.setAttribute("totalRoom", totalRoom);
        request.setAttribute("pageIndex", pageIndex);
        request.setAttribute("totalPage", totalPage);

        request.getRequestDispatcher("/views/manager/viewListRoom.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ManageRoomsDAO dao = new ManageRoomsDAO();

        try {

            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String status = request.getParameter("status");

            Room room = dao.getRoomById(roomId);

            if (room == null) {
                response.sendError(404);
                return;
            }

            if ("OCCUPIED".equalsIgnoreCase(room.getStatus())
                    || "INACTIVE".equalsIgnoreCase(room.getStatus())) {

                response.sendError(403);
                return;
            }

            dao.updateRoomStatus(roomId, status);

        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setStatus(200);
    }
}
