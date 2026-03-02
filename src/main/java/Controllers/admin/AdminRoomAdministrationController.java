package Controllers.admin;

import java.io.IOException;
import java.util.List;

import DALs.room.RoomDAO;
import Models.dto.RoomFilterDTO;
import Models.entity.Room;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminRoomAdministrationController extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        int page = 1;
        int pageSize = 10;

        try {
            String p = req.getParameter("page");
            if (p != null) {
                page = Math.max(1, Integer.parseInt(p));
            }
        } catch (NumberFormatException ignored) {
        }

        // hiện tại chưa filter => DTO rỗng (tất cả null)
        RoomFilterDTO filter = new RoomFilterDTO();

        int total = roomDAO.countAll(filter);
        int totalPages = (int) Math.ceil(total * 1.0 / pageSize);

        if (totalPages > 0 && page > totalPages) {
            page = totalPages;
        }

        List<Room> rooms = roomDAO.searchAllPagedV2(filter, page, pageSize);

        req.setAttribute("rooms", rooms);
        req.setAttribute("total", total);
        req.setAttribute("page", page);
        req.setAttribute("totalPages", totalPages);

        req.getRequestDispatcher("/views/admin/roomAdministration.jsp").forward(req, resp);
    }
}
