package Controllers.manager;

import java.io.IOException;
import java.sql.Connection;

import DALs.contract.ContractDAO;
import Models.authentication.AuthResult;
import Models.entity.Contract;
import Utils.database.DBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class ManagerExtendContractController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();

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
        if (role == null || (!role.equals("MANAGER") && !role.equals("ADMIN"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idRaw = request.getParameter("contractId");
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

        Contract cur = contractDAO.findDetailForManager(contractId);

        if (cur == null) {
            response.sendRedirect(request.getContextPath() + "/manager/contracts");
            return;
        }

        if (!"ACTIVE".equalsIgnoreCase(cur.getStatus())) {
            response.sendRedirect(
                    request.getContextPath() + "/manager/contract-detail?id=" + contractId + "&err=NOT_ACTIVE");
            return;
        }

        request.setAttribute("cur", cur);

        request.getRequestDispatcher("/views/manager/extendContract.jsp").forward(request, response);
    }

    @Override
    @SuppressWarnings({ "UseSpecificCatch", "CallToPrintStackTrace" })
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        AuthResult auth = (session == null) ? null : (AuthResult) session.getAttribute("auth");

        if (auth == null || auth.getStaff() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = auth.getStaff().getStaffRole();
        if (role == null || (!role.equals("MANAGER") && !role.equals("ADMIN"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int contractId = Integer.parseInt(request.getParameter("contractId"));
            java.sql.Date newEndDate = java.sql.Date.valueOf(request.getParameter("endDate"));

            Contract cur = contractDAO.findDetailForManager(contractId);

            if (cur == null) {
                response.sendRedirect(request.getContextPath() + "/manager/contracts?err=NOT_FOUND");
                return;
            }

            if (!"ACTIVE".equalsIgnoreCase(cur.getStatus())) {
                response.sendRedirect(
                        request.getContextPath() + "/manager/contract-detail?id=" + contractId + "&err=NOT_ACTIVE");
                return;
            }

            if (cur.getEndDate() == null) {
                response.sendRedirect(request.getContextPath() + "/manager/contracts/extend?contractId=" + contractId
                        + "&err=NO_END_DATE");
                return;
            }

            if (newEndDate == null || !newEndDate.after(cur.getEndDate())) {
                response.sendRedirect(
                        request.getContextPath() + "/manager/contracts/extend?contractId=" + contractId + "&err=DATE");
                return;
            }

            try (Connection conn = new DBContext().getConnection()) {

                conn.setAutoCommit(false);

                boolean ok = contractDAO.extendActiveContract(conn, contractId, newEndDate);

                if (!ok) {
                    conn.rollback();
                    response.sendRedirect(request.getContextPath() + "/manager/contract-detail?id=" + contractId
                            + "&err=EXTEND_FAIL");
                    return;
                }

                conn.commit();

                response.sendRedirect(
                        request.getContextPath() + "/manager/contract-detail?id=" + contractId + "&extended=1");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/manager/contracts?err=1&code=EXTEND_EXCEPTION");
        }
    }
}