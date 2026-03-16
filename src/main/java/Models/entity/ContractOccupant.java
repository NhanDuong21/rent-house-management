package Models.entity;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-03-15
 */
public class ContractOccupant {

    private int contractOccupantId;
    private int contractId;
    private int tenantId;
    private String occupantRole; // PRIMARY, MEMBER
    private String status;       // PENDING, ACTIVE, REMOVED
    private Date moveInDate;
    private Date moveOutDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // join từ TENANT
    private String fullName;
    private String identityCode;
    private String phoneNumber;
    private String email;
    private String address;
    private Date dateOfBirth;
    private Integer gender;
    private String avatar;
    private String accountStatus;

    // docs tiện render nhanh
    private String cccdFrontUrl;
    private String cccdBackUrl;

    public ContractOccupant() {
    }

    public int getContractOccupantId() {
        return contractOccupantId;
    }

    public void setContractOccupantId(int contractOccupantId) {
        this.contractOccupantId = contractOccupantId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getTenantId() {
        return tenantId;
    }

    public void setTenantId(int tenantId) {
        this.tenantId = tenantId;
    }

    public String getOccupantRole() {
        return occupantRole;
    }

    public void setOccupantRole(String occupantRole) {
        this.occupantRole = occupantRole;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getMoveInDate() {
        return moveInDate;
    }

    public void setMoveInDate(Date moveInDate) {
        this.moveInDate = moveInDate;
    }

    public Date getMoveOutDate() {
        return moveOutDate;
    }

    public void setMoveOutDate(Date moveOutDate) {
        this.moveOutDate = moveOutDate;
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

    public String getCccdFrontUrl() {
        return cccdFrontUrl;
    }

    public void setCccdFrontUrl(String cccdFrontUrl) {
        this.cccdFrontUrl = cccdFrontUrl;
    }

    public String getCccdBackUrl() {
        return cccdBackUrl;
    }

    public void setCccdBackUrl(String cccdBackUrl) {
        this.cccdBackUrl = cccdBackUrl;
    }
}
