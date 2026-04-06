package Models.dto;

import java.math.BigDecimal;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-06
 */
public class RoomFilterDTO {

    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private BigDecimal minArea;
    private BigDecimal maxArea;

    // null = Any, true = Yes, false = No
    private Boolean hasAirConditioning;
    private Boolean hasMezzanine;

    private String keyword;
    private String status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RoomFilterDTO() {
    }

    public RoomFilterDTO(BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minArea, BigDecimal maxArea,
            Boolean hasAirConditioning, Boolean hasMezzanine) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minArea = minArea;
        this.maxArea = maxArea;
        this.hasAirConditioning = hasAirConditioning;
        this.hasMezzanine = hasMezzanine;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public BigDecimal getMinArea() {
        return minArea;
    }

    public void setMinArea(BigDecimal minArea) {
        this.minArea = minArea;
    }

    public BigDecimal getMaxArea() {
        return maxArea;
    }

    public void setMaxArea(BigDecimal maxArea) {
        this.maxArea = maxArea;
    }

    public Boolean getHasAirConditioning() {
        return hasAirConditioning;
    }

    public void setHasAirConditioning(Boolean hasAirConditioning) {
        this.hasAirConditioning = hasAirConditioning;
    }

    public Boolean getHasMezzanine() {
        return hasMezzanine;
    }

    public void setHasMezzanine(Boolean hasMezzanine) {
        this.hasMezzanine = hasMezzanine;
    }

}
