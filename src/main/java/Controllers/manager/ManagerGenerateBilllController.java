package Controllers.manager;

import java.io.IOException;
import java.util.List;

import DALs.Bill.BillDAO;
import Models.dto.RoomTenantDTO;
import Services.bill.BillService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
@WebServlet(name = "ManagerGenerateBilll", urlPatterns = {"/manager/billing/generate"})
public class ManagerGenerateBilllController extends HttpServlet {

    // LOAD ROOM LIST
    private void loadRooms(HttpServletRequest request) {
        BillDAO dao = new BillDAO();
        List<RoomTenantDTO> listRoom = dao.getRoomsWithTenant();
        request.setAttribute("listRoom", listRoom);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        loadRooms(request);
        request.getRequestDispatcher("/views/manager/generateBill.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {

            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String billMonthStr = request.getParameter("billMonth"); // yyyy-MM
            String dueDateStr = request.getParameter("dueDate");

            int oldElectric = Integer.parseInt(request.getParameter("oldElectric"));
            int newElectric = Integer.parseInt(request.getParameter("newElectric"));
            int oldWater = Integer.parseInt(request.getParameter("oldWater"));
            int newWater = Integer.parseInt(request.getParameter("newWater"));

            BillService service = new BillService();
            String error = service.generateBill(roomId, billMonthStr, dueDateStr, oldElectric, newElectric, oldWater, newWater);

            //validate
            if(error != null) {
                loadRooms(request);
                request.setAttribute("error", error);
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/manager/billing");
        } catch (Exception e) {
            e.printStackTrace();
            loadRooms(request);
            request.setAttribute("error", "Generate bill failed");
            request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
        }
    }

}
