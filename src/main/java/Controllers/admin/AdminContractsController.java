/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers.admin;

import Models.dto.ManagerContractRowDTO;
import Services.contract.ContractService;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author LapNH
 */
@WebServlet(name = "AdminContractsController", urlPatterns = {"/admin/contracts"})
public class AdminContractsController extends HttpServlet {

    private final ContractService contractService = new ContractService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");
        String status = request.getParameter("status");

        int page = 1;
        int pageSize = 10;

        int total = contractService.countContracts(q, status);
        int totalPages = (int) Math.ceil(total / (double) pageSize);

        List<ManagerContractRowDTO> list
                = contractService.findContracts(q, status, page, pageSize);

        request.setAttribute("contracts", list);
        request.setAttribute("total", total);
        request.setAttribute("totalPages", totalPages);

        request.getRequestDispatcher("/views/admin/contracts.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
