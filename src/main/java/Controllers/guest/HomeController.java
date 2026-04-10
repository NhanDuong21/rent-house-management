package Controllers.guest;

import java.io.IOException;

import Models.authentication.AuthResult;
import Models.dto.PageResult;
import Models.entity.Room;
import Services.guest.RoomGuestService;
import Services.staff.RoomStaffService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class HomeController extends HttpServlet {

    private RoomGuestService guestService;
    private RoomStaffService staffService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // lazy init
            if (guestService == null) {
                guestService = new RoomGuestService();
            }
            if (staffService == null) {
                staffService = new RoomStaffService();
            }

            // lấy filter
            String minPrice = request.getParameter("minPrice");
            String maxPrice = request.getParameter("maxPrice");
            String minArea = request.getParameter("minArea");
            String maxArea = request.getParameter("maxArea");
            String hasAC = request.getParameter("hasAC");
            String hasMezzanine = request.getParameter("hasMezzanine");

            // lấy page
            int page = 1;
            int pageSize = 12;
            try {
                String p = request.getParameter("page");
                if (p != null) {
                    page = Integer.parseInt(p);
                }
            } catch (NumberFormatException ignored) {
            }

            // lấy role từ session
            HttpSession session = request.getSession(false);
            String role = null;
            if (session != null) {
                Object object = session.getAttribute("auth");
                if (!(object instanceof AuthResult ar)) {
                    if (object instanceof String str) {
                        role = str;
                    }
                } else {
                    role = ar.getRole();
                }
            }

            boolean isStaff = role != null
                    && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("MANAGER"));

            // phân trang
            PageResult<Room> result;
            if (isStaff) {
                result = staffService.searchForStaffPaged(
                        minPrice, maxPrice, minArea, maxArea,
                        hasAC, hasMezzanine, page, pageSize);
            } else {
                result = guestService.searchAvailablePaged(
                        minPrice, maxPrice, minArea, maxArea,
                        hasAC, hasMezzanine, page, pageSize);
            }

            request.setAttribute("rooms", result.getItems());
            request.setAttribute("page", result.getPage());
            request.setAttribute("totalPages", result.getTotalPages());
            request.setAttribute("totalItems", result.getTotalItems());

            // giữ lại filter
            request.setAttribute("minPrice", minPrice);
            request.setAttribute("maxPrice", maxPrice);
            request.setAttribute("minArea", minArea);
            request.setAttribute("maxArea", maxArea);
            request.setAttribute("hasAC", hasAC == null ? "any" : hasAC);
            request.setAttribute("hasMezzanine", hasMezzanine == null ? "any" : hasMezzanine);

            request.getRequestDispatcher("/views/home.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("HomeController failed: " + e.getMessage(), e);
        }
    }
}