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
public class RoomTenantDTO {
    private int roomId;
    private int contract_id;
    private String roomNumber;
    private String tenantName;
    private BigDecimal monthlyRent;
    private int lastElectric;
    private int lastWater;

    public RoomTenantDTO() {
    }

    public RoomTenantDTO(int roomId, int contract_id, String roomNumber, String tenantName, BigDecimal monthlyRent, int lastElectric, int lastWater) {
        this.roomId = roomId;
        this.contract_id = contract_id;
        this.roomNumber = roomNumber;
        this.tenantName = tenantName;
        this.monthlyRent = monthlyRent;
        this.lastElectric = lastElectric;
        this.lastWater = lastWater;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getContract_id() {
        return contract_id;
    }

    public void setContract_id(int contract_id) {
        this.contract_id = contract_id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public BigDecimal getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(BigDecimal monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public int getLastElectric() {
        return lastElectric;
    }

    public void setLastElectric(int lastElectric) {
        this.lastElectric = lastElectric;
    }

    public int getLastWater() {
        return lastWater;
    }

    public void setLastWater(int lastWater) {
        this.lastWater = lastWater;
    }

    

}
