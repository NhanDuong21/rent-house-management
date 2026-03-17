package Models.entity;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-03-17
 */
public class Occupant {

    private int occupantId;
    private int contractId;
    private String fullName;
    private String identityCode;
    private String phoneNumber;
    private String email;
    private String address;
    private Date dateOfBirth;
    private Integer gender;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // extra for view
    private String cccdFrontUrl;
    private String cccdBackUrl;

    public int getOccupantId() {
        return occupantId;
    }

    public void setOccupantId(int occupantId) {
        this.occupantId = occupantId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
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

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
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
