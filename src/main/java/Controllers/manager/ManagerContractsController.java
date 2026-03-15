package Controllers.manager;

import java.io.IOException;
import java.util.List;

import DALs.contract.ContractDAO;
import Models.dto.ManagerContractRowDTO;
import Services.contract.ContractService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author Duong Thien Nhan - CE190741
 */
public class ManagerContractsController extends HttpServlet {

    private final ContractService contractService = new ContractService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        String status = request.getParameter("status");

        int page = 1;
        int pageSize = 10;

        try {
            page = Integer.parseInt(request.getParameter("page"));
        } catch (NumberFormatException ignored) {
        }
        try {
            pageSize = Integer.parseInt(request.getParameter("pageSize"));
        } catch (NumberFormatException ignored) {
        }

        if (page <= 0) {
            page = 1;
        }
        if (pageSize <= 0) {
            pageSize = 10;
        }
        if (pageSize > 50) {
            pageSize = 50;
        }

        int total = contractService.countContracts(q, status);
        int totalPages = (int) Math.ceil(total / (double) pageSize);
        if (totalPages <= 0) {
            totalPages = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<ManagerContractRowDTO> list
                = contractService.findContracts(q, status, page, pageSize);

        request.setAttribute("contracts", list);
        request.setAttribute("total", total);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("q", q);
        request.setAttribute("status", status);

        // AJAX request: chỉ trả fragment table
        if ("1".equals(request.getParameter("ajax"))) {
            request.getRequestDispatcher("/views/manager/_contracts_table.jsp")
                    .forward(request, response);
            return;
        }

        request.getRequestDispatcher("/views/manager/contracts.jsp")
                .forward(request, response);
    }

}
