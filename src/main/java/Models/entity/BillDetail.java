package Models.entity;

import java.math.BigDecimal;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-05
 */
public class BillDetail {

    private int billDetailId;
    private int billId;
    private Integer utilityId; // nullable
    private String itemName;
    private String unit;
    private BigDecimal quantity;   // DECIMAL(18,2)
    private BigDecimal unitPrice;  // DECIMAL(18,2)
    private String chargeType;     // RENT/UTILITY/OTHER

    public BillDetail() {
    }

    public BillDetail(int billDetailId, int billId, Integer utilityId, String itemName, String unit,
            BigDecimal quantity, BigDecimal unitPrice, String chargeType) {
        this.billDetailId = billDetailId;
        this.billId = billId;
        this.utilityId = utilityId;
        this.itemName = itemName;
        this.unit = unit;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.chargeType = chargeType;
    }

    public int getBillDetailId() {
        return billDetailId;
    }

    public void setBillDetailId(int billDetailId) {
        this.billDetailId = billDetailId;
    }

    public int getBillId() {
        return billId;
    }

    public void setBillId(int billId) {
        this.billId = billId;
    }

    public Integer getUtilityId() {
        return utilityId;
    }

    public void setUtilityId(Integer utilityId) {
        this.utilityId = utilityId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getChargeType() {
        return chargeType;
    }

    public void setChargeType(String chargeType) {
        this.chargeType = chargeType;
    }

    @Override
    public String toString() {
        return "BillDetail{" + "billDetailId=" + billDetailId + ", billId=" + billId + ", utilityId=" + utilityId + ", itemName=" + itemName + ", unit=" + unit + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", chargeType=" + chargeType + '}';
    }
    

}
