package Controllers.tenant;

import java.io.IOException;
import java.util.List;

import DALs.contract.ContractDAO;
import DALs.contract.OccupantDAO;
import DALs.payment.PaymentDAO;
import Models.authentication.AuthResult;
import Models.entity.Contract;
import Models.entity.Occupant;
import Models.entity.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class TenantContractDetailController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();
    private final OccupantDAO occupantDAO = new OccupantDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        if (auth == null || auth.getTenant() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/tenant/contract");
            return;
        }

        int contractId;
        try {
            contractId = Integer.parseInt(idRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/tenant/contract");
            return;
        }

        int tenantId = auth.getTenant().getTenantId();

        Contract c = contractDAO.findDetailForTenant(contractId, tenantId);
        if (c == null) {
            response.sendRedirect(request.getContextPath() + "/tenant/contract");
            return;
        }

        Payment latestPay = paymentDAO.findLatestBankPaymentForContract(contractId);
        List<Occupant> occupants = occupantDAO.findByContractId(contractId);

        request.setAttribute("contract", c);
        request.setAttribute("latestPayment", latestPay);
        request.setAttribute("occupants", occupants);

        request.getRequestDispatcher("/views/tenant/contractDetail.jsp").forward(request, response);
    }
}
