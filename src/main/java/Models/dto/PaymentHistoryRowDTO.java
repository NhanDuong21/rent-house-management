/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Models.dto;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
public class PaymentHistoryRowDTO {
    private int paymentId;
    private String fname;
    private String roomNumber;
    private String method;
    private String status;
    private Date billMonth;
    private Timestamp paidAt;
    private BigDecimal amount;

    public PaymentHistoryRowDTO() {
    }

    public PaymentHistoryRowDTO(int paymentId, String fname, String roomNumber, String method, String status, Date billMonth, Timestamp paidAt, BigDecimal amount) {
        this.paymentId = paymentId;
        this.fname = fname;
        this.roomNumber = roomNumber;
        this.method = method;
        this.status = status;
        this.billMonth = billMonth;
        this.paidAt = paidAt;
        this.amount = amount;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(Date billMonth) {
        this.billMonth = billMonth;
    }

    public Timestamp getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Timestamp paidAt) {
        this.paidAt = paidAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

     
}
