package Controllers.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import DALs.admin.ManageAccountDAO;
import Models.authentication.AuthResult;
import Models.dto.AdminAccountRowDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class AdminManageAccountsController extends HttpServlet {

    private final ManageAccountDAO accountDAO = new ManageAccountDAO();

    private AuthResult getAuth(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session == null) ? null : (AuthResult) session.getAttribute("auth");
    }

    private boolean isAdmin(AuthResult auth) {
        return auth != null && auth.getStaff() != null && "ADMIN".equalsIgnoreCase(auth.getRole());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AuthResult auth = getAuth(request);
        if (!isAdmin(auth)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = request.getParameter("role");
        if (role == null || role.isBlank()) {
            role = "ALL";
        } else {
            role = role.trim().toUpperCase();
            if (!role.equals("ALL") && !role.equals("TENANT") && !role.equals("MANAGER")) {
                role = "ALL";
            }
        }

        String keyword = request.getParameter("keyword");
        if (keyword != null && keyword.isBlank()) {
            keyword = null;
        }

        int page = 1;
        int pageSize = 10;

        try {
            page = Integer.parseInt(request.getParameter("page"));
            if (page < 1) {
                page = 1;
            }
        } catch (NumberFormatException ignored) {
        }

        try {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
            if (pageSize <= 0) {
                pageSize = 10;
            }
        } catch (NumberFormatException ignored) {
        }

        int offset = (page - 1) * pageSize;

        List<AdminAccountRowDTO> accounts = accountDAO.listAccounts(role, keyword, offset, pageSize);

        int totalRecords = accountDAO.countAccounts(role, keyword);
        int totalPages = (int) Math.ceil((double) totalRecords / pageSize);

        if (page > totalPages && totalPages > 0) {
            page = totalPages;
        }

        request.setAttribute("accounts", accounts);
        request.setAttribute("role", role);
        request.setAttribute("keyword", keyword);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);

        request.setAttribute("success", request.getParameter("success"));
        request.setAttribute("error", request.getParameter("error"));

        request.getRequestDispatcher("/views/admin/accounts.jsp").forward(request, response);
    }

    /**
     * Xử lý toggle-status từ fetch (AJAX). Trả về JSON: {"ok": true/false,
     * "message": "..."} Không redirect, không chuyển trang.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        AuthResult auth = getAuth(request);
        if (!isAdmin(auth)) {
            writeJson(response, false, "No permission.");
            return;
        }

        String action = request.getParameter("action");

        if ("toggle-status".equals(action)) {

            String accountType = request.getParameter("accountType");
            String currentStatus = request.getParameter("currentStatus");
            int accountId;

            try {
                accountId = Integer.parseInt(request.getParameter("accountId"));
            } catch (NumberFormatException e) {
                writeJson(response, false, "Invalid account ID.");
                return;
            }

            // Xác định trạng thái mới
            if ("STAFF".equalsIgnoreCase(accountType)) {
                String newStatus = "ACTIVE".equalsIgnoreCase(currentStatus) ? "INACTIVE" : "ACTIVE";

                // MANAGER: toggle tự do
                boolean ok = accountDAO.updateStaffStatus(accountId, newStatus);
                if (ok) {
                    writeJson(response, true, "Status updated successfully.");
                } else {
                    writeJson(response, false, "Failed to update status. Make sure this account is a Manager.");
                }

            } else if ("TENANT".equalsIgnoreCase(accountType)) {
                String newStatus = "ACTIVE".equalsIgnoreCase(currentStatus) ? "LOCKED" : "ACTIVE";
                // Chỉ được LOCK khi tất cả contract đã END hoặc CANCEL
                if ("LOCKED".equalsIgnoreCase(newStatus) && accountDAO.tenantHasActiveContract(accountId)) {
                    writeJson(response, false,
                            "Cannot lock this tenant. They still have active contract(s). All contracts must be ENDED or CANCELLED first.");
                    return;
                }
                boolean ok = accountDAO.updateTenantStatus(accountId, newStatus);
                if (ok) {
                    writeJson(response, true, "Status updated successfully.");
                } else {
                    writeJson(response, false, "Failed to update tenant status.");
                }

            } else {
                writeJson(response, false, "Unknown account type.");
            }

        } else {
            writeJson(response, false, "Unknown action.");
        }
    }

    /**
     * Ghi JSON tối giản ra response.
     */
    private void writeJson(HttpServletResponse response, boolean ok, String message) throws IOException {
        String safeMsg = message.replace("\\", "\\\\").replace("\"", "\\\"");
        String json = "{\"ok\":" + ok + ",\"message\":\"" + safeMsg + "\"}";
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
        }
    }
}
