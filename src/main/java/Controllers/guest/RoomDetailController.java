package Controllers.guest;

import java.io.IOException;

import Models.authentication.AuthResult;
import Models.entity.Room;
import Services.guest.RoomGuestService;
import Services.staff.RoomStaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RoomDetailController extends HttpServlet {

    private RoomGuestService roomGuestService;
    private RoomStaffService roomStaffService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            if (roomGuestService == null)
                roomGuestService = new RoomGuestService();
            if (roomStaffService == null)
                roomStaffService = new RoomStaffService();

            int id;
            try {
                id = Integer.parseInt(request.getParameter("id"));
            } catch (NumberFormatException e) {
                response.sendError(400, "Invalid id");
                return;
            }

            String role = null;
            HttpSession session = request.getSession(false);
            if (session != null) {
                Object object = session.getAttribute("auth");
                if (object instanceof AuthResult authResult) {
                    role = authResult.getRole();
                }
            }

            boolean isStaff = role != null
                    && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("MANAGER"));

            Room room = isStaff
                    ? roomStaffService.getRoomDetailForStaff(id)
                    : roomGuestService.getRoomDetailForGuest(id);

            if (room == null) {
                response.sendError(404, "Not found");
                return;
            }

            request.setAttribute("room", room);
            request.setAttribute("ctx", request.getContextPath());
            request.getRequestDispatcher("/views/room/roomDetail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("RoomDetailController failed: " + e.getMessage(), e);
        }
    }
}