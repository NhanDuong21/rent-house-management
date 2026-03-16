package Controllers.manager;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;

import DALs.room.RoomDAO;
import Models.authentication.AuthResult;
import Models.common.ServiceResult;
import Models.entity.Contract;
import Models.entity.Tenant;
import Services.contract.ContractService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@MultipartConfig
public class CreateContractController extends HttpServlet {

    private final RoomDAO roomDAO = new RoomDAO();
    private final ContractService service = new ContractService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("rooms", roomDAO.findAvailableRooms());
        request.getRequestDispatcher("/views/manager/createContract.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AuthResult auth = (AuthResult) request.getSession().getAttribute("auth");
        if (auth == null || auth.getStaff() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int roomId = Integer.parseInt(req(request, "roomId"));

            String tenantName = req(request, "tenantName");
            String identityCode = req(request, "identityCode");
            String email = req(request, "email");
            String phone = req(request, "phone");
            String address = req(request, "address");
            String dobRaw = req(request, "dob");
            String genderRaw = req(request, "gender");

            BigDecimal rent = new BigDecimal(req(request, "rent"));
            BigDecimal deposit = new BigDecimal(req(request, "deposit"));

            LocalDate startDate = LocalDate.parse(req(request, "startDate"));
            LocalDate endDate = LocalDate.parse(req(request, "endDate"));

            Part cccdFront = request.getPart("cccdFront");
            Part cccdBack = request.getPart("cccdBack");

            if (cccdFront == null || cccdFront.getSize() <= 0 || cccdBack == null || cccdBack.getSize() <= 0) {
                String err = java.net.URLEncoder.encode("Vui lòng upload đủ CCCD mặt trước và mặt sau.", "UTF-8");
                response.sendRedirect(request.getContextPath() + "/manager/contracts/create?error=" + err);
                return;
            }

            String uploadPath = getServletContext().getRealPath("/assets/images/tenant-docs/");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String frontExt = getFileExtension(cccdFront);
            String backExt = getFileExtension(cccdBack);

            long now = System.currentTimeMillis();
            String frontFile = now + "_primary_front" + frontExt;
            String backFile = now + "_primary_back" + backExt;

            cccdFront.write(uploadPath + File.separator + frontFile);
            cccdBack.write(uploadPath + File.separator + backFile);

            String frontUrl = "/assets/images/tenant-docs/" + frontFile;
            String backUrl = "/assets/images/tenant-docs/" + backFile;

            System.out.println("=== CREATE CONTRACT DEBUG ===");
            System.out.println("uploadPath = " + uploadPath);
            System.out.println("frontUrl = " + frontUrl);
            System.out.println("backUrl = " + backUrl);
            System.out.println("frontSize = " + cccdFront.getSize());
            System.out.println("backSize = " + cccdBack.getSize());

            Contract c = new Contract();
            c.setRoomId(roomId);
            c.setCreatedByStaffId(auth.getStaff().getStaffId());
            c.setStartDate(java.sql.Date.valueOf(startDate));
            c.setEndDate(java.sql.Date.valueOf(endDate));
            c.setMonthlyRent(rent);
            c.setDeposit(deposit);
            c.setPaymentQrData("/assets/images/qr/myqr.png");

            Tenant t = new Tenant();
            t.setFullName(tenantName);
            t.setIdentityCode(identityCode);
            t.setEmail(email);
            t.setPhoneNumber(phone);
            t.setAddress(address);
            t.setDateOfBirth(java.sql.Date.valueOf(LocalDate.parse(dobRaw)));
            t.setGender(Integer.valueOf(genderRaw));
            t.setAvatar("/assets/images/avatar/avtDefault.png");

            ServiceResult rs = service.createContractAndTenant(c, t, frontUrl, backUrl);
            System.out.println("createContractAndTenant => ok=" + rs.isOk() + ", msg=" + rs.getMessage());

            if (rs.isOk()) {
                String msg = java.net.URLEncoder.encode(rs.getMessage(), "UTF-8");
                response.sendRedirect(request.getContextPath() + "/manager/contracts?success=" + msg);
            } else {
                String err = java.net.URLEncoder.encode(rs.getMessage(), "UTF-8");
                response.sendRedirect(request.getContextPath() + "/manager/contracts/create?error=" + err);
            }
        } catch (Exception e) {
            e.printStackTrace();
            String err = java.net.URLEncoder.encode("Lỗi dữ liệu form: " + e.getMessage(), "UTF-8");
            response.sendRedirect(request.getContextPath() + "/manager/contracts/create?error=" + err);
        }
    }

    private String req(HttpServletRequest request, String name) {
        String v = request.getParameter(name);
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException("Missing field: " + name);
        }
        return v.trim();
    }

    private String getFileExtension(Part part) {
        String submitted = part.getSubmittedFileName();
        if (submitted == null || submitted.isBlank()) {
            return ".jpg";
        }
        int dot = submitted.lastIndexOf('.');
        if (dot < 0) {
            return ".jpg";
        }
        return submitted.substring(dot);
    }
}
