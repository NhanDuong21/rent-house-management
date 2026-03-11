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
        java.time.LocalDate billMonth = java.time.LocalDate.parse(billMonthStr + "-01");
        java.time.LocalDate dueDate = java.time.LocalDate.parse(dueDateStr);
        java.time.LocalDate now = java.time.LocalDate.now().withDayOfMonth(1);


        // Bill month không được quá khứ
        if (billMonth.isBefore(now)) {
            loadRooms(request);
            request.setAttribute("error", "Bill month cannot be in the past");
            request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
            return;
        }


        //  Due date >= bill month
        if (dueDate.isBefore(billMonth)) {
            loadRooms(request);
            request.setAttribute("error", "Due date must be after bill month");
            request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
            return;
        }

        // cộng thêm 1 tháng
        LocalDate nextMonth = billMonth.plusMonths(1);

        // Due date phải là tháng trước khi tạo bill 1 tháng(true thì ko lỗi)
        if (dueDate.getMonthValue() != nextMonth.getMonthValue()
        || dueDate.getYear() != nextMonth.getYear()) {

            loadRooms(request);
            request.setAttribute("error", "Due date must be in the next month of bill month");
            request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
            return;
        }


        //Không được trùng bill
        BillDAO dao = new BillDAO();

        String[] parts = billMonthStr.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        if (dao.isBillExist(roomId, month, year)) {
            loadRooms(request);
            request.setAttribute("error", "This room already has a bill for this month");
            request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
            return;
        }

        // CREATE BILL
        dao.createDraftBill(roomId, month, year, java.sql.Date.valueOf(dueDate));
        response.sendRedirect(request.getContextPath() + "/manager/billing");

    } catch (Exception e) {
        e.printStackTrace();
        request.setAttribute("error", "Generate bill failed");
        request.getRequestDispatcher("/views/manager/generateBill.jsp").forward(request, response);
    }
}

}
