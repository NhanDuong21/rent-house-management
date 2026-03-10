/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Models.dto;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Description: DTO displayed list BILL(Manager)
 * @author To Thi Thao Trang - CE191027
 */
public class ManagerBillRowDTO {
    private int billId;
    private String roomNumber;
    private Date month;
    private String tenantName;
    private String blockName;
    private Date dueDate;
    private BigDecimal TotalAmount;
    private String status;
    private String paymentStatus;

    public ManagerBillRowDTO() {
    }

    public ManagerBillRowDTO(int billId, String roomNumber, Date month, String tenantName, String blockName, Date dueDate, BigDecimal TotalAmount, String status, String paymentStatus) {
        this.billId = billId;
        this.roomNumber = roomNumber;
        this.month = month;
        this.tenantName = tenantName;
        this.blockName = blockName;
        this.dueDate = dueDate;
        this.TotalAmount = TotalAmount;
        this.status = status;
        this.paymentStatus = paymentStatus;
    }
    
    
    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public Date getMonth() {
        return month;
    }

    public void setMonth(Date month) {
        this.month = month;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(BigDecimal TotalAmount) {
        this.TotalAmount = TotalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "ManagerBillRowDTO{" + "billId=" + billId + ", roomNumber=" + roomNumber + ", month=" + month + ", tenantName=" + tenantName + ", blockName=" + blockName + ", dueDate=" + dueDate + ", TotalAmount=" + TotalAmount + ", status=" + status + ", paymentStatus=" + paymentStatus + '}';
    }
    
    
    
}

     