package Controllers.manager;

import java.io.IOException;
import java.util.List;

import DALs.contract.ContractDAO;
import DALs.contract.ContractOccupantDAO;
import DALs.payment.PaymentDAO;
import Models.authentication.AuthResult;
import Models.entity.Contract;
import Models.entity.ContractOccupant;
import Models.entity.Payment;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/manager/contract-detail")
public class ManagerContractDetailController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();
    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ContractOccupantDAO occupantDAO = new ContractOccupantDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        if (auth == null || auth.getStaff() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = auth.getStaff().getStaffRole();
        if (!"MANAGER".equals(role) && !"ADMIN".equals(role)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        int contractId = Integer.parseInt(idRaw);

        Contract c = contractDAO.findDetailForManager(contractId);
        if (c == null) {
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        Payment latestPay = paymentDAO.findLatestBankPaymentForContract(contractId);

        List<ContractOccupant> occupants = occupantDAO.findByContractId(contractId);

        request.setAttribute("contract", c);
        request.setAttribute("latestPayment", latestPay);
        request.setAttribute("occupants", occupants);

        request.getRequestDispatcher("/views/manager/contractDetail.jsp")
                .forward(request, response);
    }
}
