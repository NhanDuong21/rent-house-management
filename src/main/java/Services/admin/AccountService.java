package Services.admin;

import DALs.admin.ManageAccountDAO;
import static Utils.security.HashUtil.md5;

public class AccountService {

    private final ManageAccountDAO dao = new ManageAccountDAO();

    public void createAccount(String role,
                              String fullName,
                              String identityCode,
                              String phoneNumber,
                              String email,
                              String address,
                              String dob,
                              int gender,
                              String password) {

        // Validate password
        if (password == null || password.length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters.");
        }

        // Check duplicate email
        if (dao.existsEmail(email)) {
            throw new RuntimeException("Email already exists.");
        }

        String hashed = md5(password);

        switch (role.toUpperCase()) {

            case "TENANT" -> {
                boolean ok = dao.insertTenant(
                        fullName,
                        identityCode,
                        phoneNumber,
                        email,
                        address,
                        dob,
                        gender,
                        hashed
                );
                if (!ok) throw new RuntimeException("Failed to create tenant.");
            }

            case "MANAGER" -> {
                boolean ok = dao.insertManager(
                        fullName,
                        identityCode,
                        phoneNumber,
                        email,
                        dob,
                        gender,
                        hashed
                );
                if (!ok) throw new RuntimeException("Failed to create manager.");
            }

            default -> throw new RuntimeException("Invalid role selected.");
        }
    }

   
}