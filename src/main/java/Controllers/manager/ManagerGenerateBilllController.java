package Controllers.manager;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import DALs.Bill.BillDAO;
import Models.dto.RoomTenantDTO;
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

            // Convert
            LocalDate billMonth = LocalDate.parse(billMonthStr + "-01");
            LocalDate dueDate = LocalDate.parse(dueDateStr);

            String[] parts = billMonthStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            int oldElectric = Integer.parseInt(request.getParameter("oldElectric"));
            int newElectric = Integer.parseInt(request.getParameter("newElectric"));
            int oldWater = Integer.parseInt(request.getParameter("oldWater"));
            int newWater = Integer.parseInt(request.getParameter("newWater"));

            BillDAO dao = new BillDAO();
            RoomTenantDTO contract = dao.getContractDatesByRoomId(roomId);
            if (contract == null) {
                loadRooms(request);
                request.setAttribute("error", "Room has no active contract.");
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }
            LocalDate startDate = contract.getStartDate();

            // BEFORE START
            if (billMonth.isBefore(startDate.withDayOfMonth(1))) {
                loadRooms(request);
                request.setAttribute("error", "Cannot generate bill before contract start date.");
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }


            // VALIDATE METER
            if (newElectric < oldElectric) {
                loadRooms(request);
                request.setAttribute("error", "New electric meter must be greater than old meter.");
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }
            if (newWater < oldWater) {
                loadRooms(request);
                request.setAttribute("error", "New water meter must be greater than old meter.");
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }
            //ngày đầu tiên của tháng hiện tại
            LocalDate firstDayCurrentMonth = LocalDate.now().withDayOfMonth(1);

            if (billMonth.isAfter(firstDayCurrentMonth)) {
                loadRooms(request);
                request.setAttribute("error", "Cannot generate bill for future months.");
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }

            LocalDate minDueDate = billMonth.plusMonths(1);          // 01 tháng sau
            LocalDate maxDueDate = billMonth.plusMonths(1).plusDays(14); // 15 của tháng sau

            //duedate thõa khi nằm trong 1 - 15 ngày của tháng sau
            if (dueDate.isBefore(minDueDate) || dueDate.isAfter(maxDueDate)) {
                loadRooms(request);
                request.setAttribute("error",
                        "Due date must be between "
                        + minDueDate + " and "
                        + maxDueDate);
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }

            // CHECK DUPLICATE BILL
            if (dao.isBillExist(roomId, month, year)) {
                loadRooms(request);
                request.setAttribute("error", "This room already has a bill for this month");
                request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
                return;
            }

            // GENERATE BILL
            dao.generateBill(roomId, java.sql.Date.valueOf(billMonth), java.sql.Date.valueOf(dueDate), oldElectric, newElectric, oldWater, newWater);
            response.sendRedirect(request.getContextPath() + "/manager/billing");

        } catch (Exception e) {
            e.printStackTrace();
            loadRooms(request);
            request.setAttribute("error", "Generate bill failed");
            request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
        }
    }

}
