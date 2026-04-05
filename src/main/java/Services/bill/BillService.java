package Services.bill;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;

import DALs.Bill.BillDAO;
import Models.dto.RoomTenantDTO;
import Models.entity.Bill;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
public class BillService {

    private final BillDAO dao = new BillDAO();

    @SuppressWarnings("CallToPrintStackTrace")
    public String generateBill(int roomId, String billMonthStr, String dueDateStr, int oldElectric, int newElectric, int oldWater, int newWater) {
        try {
            // Convert
            LocalDate billMonth = LocalDate.parse(billMonthStr + "-01");
            LocalDate dueDate = LocalDate.parse(dueDateStr);

            // get month and year
            String[] parts = billMonthStr.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            // Get contract
            RoomTenantDTO contract = dao.getContractDatesByRoomId(roomId);
            if (contract == null) {
                return "Room has no active contract.";
            }

            LocalDate startDate = contract.getStartDate();
            LocalDate firstDateStartDate = startDate.withDayOfMonth(1);

            if(startDate.getDayOfMonth() > 20) {
                firstDateStartDate = startDate.plusMonths(1).withDayOfMonth(1);
            }
            // cannot gen bill before contract start
            if (billMonth.isBefore(firstDateStartDate)) {
                return "Cannot generate bill before allowed billing month.";
            }

            // meter validation
            if (newElectric < oldElectric) {
                return "New electric meter must be greater than old meter.";
            }
            if (newWater < oldWater) {
                return "New water meter must be greater than old meter.";
            }

            // FUTURE
            LocalDate firstDayCurrentMonth = LocalDate.now().withDayOfMonth(1);
            if (billMonth.isAfter(firstDayCurrentMonth)) {
                return "Cannot generate bill for future months.";
            }

            // duedate
            LocalDate minDueDate = billMonth.plusMonths(1);
            LocalDate maxDueDate = billMonth.plusMonths(1).plusDays(14);
            if (dueDate.isBefore(minDueDate) || dueDate.isAfter(maxDueDate)) {
                return "Due date must be between " + minDueDate + " and " + maxDueDate;
            }

            //check duplicate
            if (dao.isBillExist(roomId, month, year)) {
                return "This room already has a bill for this month";
            }

            // generate bill
            dao.generateBill(roomId, java.sql.Date.valueOf(billMonth), java.sql.Date.valueOf(dueDate), oldElectric, newElectric, oldWater, newWater);

            //update index room
            dao.updateRoomMeter(roomId, newElectric, newWater);

            return null; // success

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            return "Generate bill failed";
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public String updateBill(int billId, String paymentStatus, Date billMonth, Date dueDate, int oldElectric, int newElectric, int oldWater, int newWater) {
        try {
            BillDAO dao = new BillDAO();

            Bill bill = dao.findBillDetailByIdForManager(billId);
            if (bill == null) {
                return "Bill not found";
            }

            //chỉ cho edit khi UNPAID or payment status là PENDING
            if (!(bill.getStatus().equals("UNPAID") || "PENDING".equals(paymentStatus))) {
                return "Only UNPAID or PENDING bill can be edited.";
            }

            // meter validation
            if (newElectric < oldElectric) {
                return "New electric meter must be greater than old meter.";
            }

            if (newWater < oldWater) {
                return "New water meter must be greater than old meter.";
            }

            //dueDate validation
            LocalDate billMonthLocal = billMonth.toLocalDate();
            LocalDate dueDateLocal = dueDate.toLocalDate();

            LocalDate minDueDate = billMonthLocal.plusMonths(1);
            LocalDate maxDueDate = billMonthLocal.plusMonths(1).plusDays(14);

            if (dueDateLocal.isBefore(minDueDate) || dueDateLocal.isAfter(maxDueDate)) {
                return "Due date must be between " + minDueDate + " and " + maxDueDate;
            }

            //update bill
            boolean result = dao.updateBillMeter(billId, billMonth, dueDate, oldElectric, newElectric, oldWater, newWater);

            if (!result) {
                return "Update bill failed";
            }

            //update room meter when edit bill
            dao.updateRoomMeterByBillId(billId, newElectric, newWater);
            return null; // success
        } catch (Exception e) {
            e.printStackTrace();
            return "Update Bill error";
        }

    }
}
