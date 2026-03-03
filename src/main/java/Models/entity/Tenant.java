package Models.entity;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-05
 */
public class Tenant {

    private int tenantId;
    private String fullName;
    private String identityCode;
    private String phoneNumber;
    private String email;
    private String address;
    private Date dateOfBirth;
    private Integer gender; // 0/1 or null
    private String avatar;

    private String accountStatus;   // LOCKED/ACTIVE
    private String passwordHash;    // nullable
    private boolean mustSetPassword;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Tenant() {
    }

    public Tenant(int tenantId, String fullName, String identityCode, String phoneNumber, String email, Date dateOfBirth) {
        this.tenantId = tenantId;
        this.fullName = fullName;
        this.identityCode = identityCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public Tenant(int tenantId, String fullName, String identityCode, String phoneNumber, String email, String address,
            Date dateOfBirth, Integer gender, String avatar, String accountStatus, String passwordHash,
            boolean mustSetPassword, Timestamp createdAt, Timestamp updatedAt) {
        this.tenantId = tenantId;
        this.fullName = fullName;
        this.identityCode = identityCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.avatar = avatar;
        this.accountStatus = accountStatus;
        this.passwordHash = passwordHash;
        this.mustSetPassword = mustSetPassword;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIdentityCode() {
        return identityCode;
    }

    public void setIdentityCode(String identityCode) {
        this.identityCode = identityCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public boolean isMustSetPassword() {
        return mustSetPassword;
    }

    public void setMustSetPassword(boolean mustSetPassword) {
        this.mustSetPassword = mustSetPassword;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

}
