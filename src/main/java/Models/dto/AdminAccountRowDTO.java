package Models.dto;

import java.sql.Timestamp;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-27
 */
public class AdminAccountRowDTO {

    private String accountType;
    private int accountId;
    private String email;
    private String fullName;
    private String role;
    private String status;
    private Timestamp createdAt;

    public AdminAccountRowDTO() {
    }

    public AdminAccountRowDTO(String accountType, int accountId, String email, String fullName, String role,
            String status, Timestamp createdAt) {
        this.accountType = accountType;
        this.accountId = accountId;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

}
