/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Models.dto;

import java.math.BigDecimal;

/**
 *
 * @author To Thi Thao Trang - CE191027
 */
public class UtilityUsageDTO {

    private int utilityId;
    private String utilityName;
    private int quantity;
    private String unit;
    private BigDecimal price;

    public UtilityUsageDTO() {
    }

    public UtilityUsageDTO(int utilityId, String utilityName, int quantity, String unit, BigDecimal price) {
        this.utilityId = utilityId;
        this.utilityName = utilityName;
        this.quantity = quantity;
        this.unit = unit;
        this.price = price;
    }

    public int getUtilityId() {
        return utilityId;
    }

    public void setUtilityId(int utilityId) {
        this.utilityId = utilityId;
    }

    public String getUtilityName() {
        return utilityName;
    }

    public void setUtilityName(String utilityName) {
        this.utilityName = utilityName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
