package Services.admin;

import DALs.admin.ManageAccountDAO;
import DALs.contract.ContractDAO;
import DALs.payment.PaymentDAO;
import DALs.room.RoomDAO;
import DALs.maintenanceRequest.MaintenanceRequestDAO;

public class DashboardService {

    RoomDAO roomDAO = new RoomDAO();
    ContractDAO contractDAO = new ContractDAO();
    PaymentDAO paymentDAO = new PaymentDAO();
    ManageAccountDAO accountDAO = new ManageAccountDAO();
    MaintenanceRequestDAO maintenanceDAO = new MaintenanceRequestDAO();

    public int getTotalTenants() {
        return accountDAO.countTenants();
    }

    public int getAvailableRooms() {
        return roomDAO.countAvailableRooms();
    }

    public int getMaintenanceRequests() {
        return maintenanceDAO.countPendingRequests();
    }

    public int getOccupiedRooms() {
        return roomDAO.countOccupiedRooms();
    }

    public double getMonthlyRevenue() {
        return paymentDAO.getMonthlyRevenue();
    }

    public double getTotalRevenue() {
        return paymentDAO.getTotalRevenue();
    }
    public int getActiveContracts(){
        return contractDAO.ActiveContracts();
    }
}