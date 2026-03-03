package Models.entity;

import java.sql.Date;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-05
 */
public class Bill {

    private int billId;
    private int contractId;

    private Date billMonth;
    private Date dueDate;
    private String status;  // UNPAID/PAID/OVERDUE/CANCELLED
    private String note;

    private Integer oldElectricNumber;
    private Integer newElectricNumber;
    private Integer oldWaterNumber;
    private Integer newWaterNumber;

    public Bill() {
    }

    public Bill(int billId, int contractId, Date billMonth, Date dueDate, String status, String note,
            Integer oldElectricNumber, Integer newElectricNumber, Integer oldWaterNumber, Integer newWaterNumber) {
        this.billId = billId;
        this.contractId = contractId;
        this.billMonth = billMonth;
        this.dueDate = dueDate;
        this.status = status;
        this.note = note;
        this.oldElectricNumber = oldElectricNumber;
        this.newElectricNumber = newElectricNumber;
        this.oldWaterNumber = oldWaterNumber;
        this.newWaterNumber = newWaterNumber;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public Date getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(Date billMonth) {
        this.billMonth = billMonth;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getOldElectricNumber() {
        return oldElectricNumber;
    }

    public void setOldElectricNumber(Integer oldElectricNumber) {
        this.oldElectricNumber = oldElectricNumber;
    }

    public Integer getNewElectricNumber() {
        return newElectricNumber;
    }

    public void setNewElectricNumber(Integer newElectricNumber) {
        this.newElectricNumber = newElectricNumber;
    }

    public Integer getOldWaterNumber() {
        return oldWaterNumber;
    }

    public void setOldWaterNumber(Integer oldWaterNumber) {
        this.oldWaterNumber = oldWaterNumber;
    }

    public Integer getNewWaterNumber() {
        return newWaterNumber;
    }

    public void setNewWaterNumber(Integer newWaterNumber) {
        this.newWaterNumber = newWaterNumber;
    }

    @Override
    public String toString() {
        return "Bill{" + "billId=" + billId + ", contractId=" + contractId + ", billMonth=" + billMonth + ", dueDate=" + dueDate + ", status=" + status + ", note=" + note + ", oldElectricNumber=" + oldElectricNumber + ", newElectricNumber=" + newElectricNumber + ", oldWaterNumber=" + oldWaterNumber + ", newWaterNumber=" + newWaterNumber + '}';
    }
    

}
