package Controllers.manager;

import java.io.IOException;
import java.util.List;

import DALs.auth.TenantDocumentDAO;
import DALs.contract.ContractDAO;
import DALs.contract.OccupantDAO;
import DALs.payment.PaymentDAO;
import Models.entity.Contract;
import Models.entity.Occupant;
import Models.entity.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/manager/contract-detail")
public class ManagerContractDetailController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();
    private final OccupantDAO occupantDAO = new OccupantDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final TenantDocumentDAO tenantDocumentDAO = new TenantDocumentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        int contractId;
        try {
            contractId = Integer.parseInt(idRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        Contract c = contractDAO.findDetailForManager(contractId);
        if (c == null) {
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        List<Occupant> occupants = occupantDAO.findByContractId(contractId);
        Payment latestPay = paymentDAO.findLatestBankPaymentForContract(contractId);

        String tenantCccdFront = tenantDocumentDAO.findActiveFileUrlByType(c.getTenantId(), "CCCD_FRONT");
        String tenantCccdBack = tenantDocumentDAO.findActiveFileUrlByType(c.getTenantId(), "CCCD_BACK");

        System.out.println("contractId = " + contractId);
        System.out.println("tenantId = " + c.getTenantId());
        System.out.println("tenantCccdFront = " + tenantCccdFront);
        System.out.println("tenantCccdBack = " + tenantCccdBack);
        System.out.println("occupants size = " + (occupants == null ? 0 : occupants.size()));

        request.setAttribute("contract", c);
        request.setAttribute("occupants", occupants);
        request.setAttribute("latestPayment", latestPay);
        request.setAttribute("tenantCccdFront", tenantCccdFront);
        request.setAttribute("tenantCccdBack", tenantCccdBack);

        request.getRequestDispatcher("/views/manager/contractDetail.jsp").forward(request, response);
    }
}
