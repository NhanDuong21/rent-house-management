package Services.staff;

import java.math.BigDecimal;
import java.util.List;

import DALs.block.BlockDAO;
import DALs.room.RoomDAO;
import DALs.room.RoomImageDAO;
import Models.dto.PageResult;
import Models.dto.RoomFilterDTO;
import Models.entity.Block;
import Models.entity.Room;
import Models.entity.RoomImage;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Description: staff xem full ( k phan biet status room )
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-09
 */
public class RoomStaffService {

    private final RoomDAO rdao = new RoomDAO();
    private final RoomImageDAO imgDao = new RoomImageDAO();
    private final BlockDAO blockDAO = new BlockDAO();

    //phễu lọc (browser nhận string db nhận bigdec)
    public List<Room> searchForStaff(String minPrice, String maxPrice, String minArea, String maxArea,
            String hasAC, String hasMezzanine) {
        RoomFilterDTO f = new RoomFilterDTO();
        f.setMinPrice(parseDecimal(minPrice));
        f.setMaxPrice(parseDecimal(maxPrice));
        f.setMinArea(parseDecimal(minArea));
        f.setMaxArea(parseDecimal(maxArea));
        f.setHasAirConditioning(parseTriState(hasAC));  // any/yes/no -> null/true/false
        f.setHasMezzanine(parseTriState(hasMezzanine));
        return rdao.searchAll(f);
    }

    public PageResult<Room> searchForStaffPaged(String minPrice, String maxPrice, String minArea, String maxArea,
            String hasAC, String hasMezzanine, int page, int pageSize) {

        RoomFilterDTO f = new RoomFilterDTO();
        f.setMinPrice(parseDecimal(minPrice));
        f.setMaxPrice(parseDecimal(maxPrice));
        f.setMinArea(parseDecimal(minArea));
        f.setMaxArea(parseDecimal(maxArea));
        f.setHasAirConditioning(parseTriState(hasAC));
        f.setHasMezzanine(parseTriState(hasMezzanine));

        int totalItems = rdao.countAll(f);
        int totalPages = (int) Math.ceil(totalItems * 1.0 / pageSize);

        if (totalPages < 1) {
            totalPages = 1;
        }
        if (page < 1) {
            page = 1;
        }
        if (page > totalPages) {
            page = totalPages;
        }

        List<Room> rooms = rdao.searchAllPaged(f, page, pageSize);

        PageResult<Room> pr = new PageResult<>();
        pr.setItems(rooms);
        pr.setPage(page);
        pr.setPageSize(pageSize);
        pr.setTotalItems(totalItems);
        pr.setTotalPages(totalPages);
        return pr;
    }

    private BigDecimal parseDecimal(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                return null;
            }
            return new BigDecimal(s.trim());
        } catch (Exception e) {
            return null;
        }
    }

    private Boolean parseTriState(String str) {
        if (str == null) {
            return null;
        }
        str = str.trim().toLowerCase();
        if (str.equals("yes") || str.equals("true") || str.equals("1")) {
            return true;
        }
        if (str.equals("no") || str.equals("false") || str.equals("0")) {
            return false;
        }
        return null; // any
    }

    private Integer parseIntNullable(String s) {
        try {
            if (s == null || s.trim().isEmpty()) {
                return null;
            }
            return Integer.valueOf(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    //role staff
    public Room getRoomDetailForStaff(int roomId) {

        if (roomId <= 0) {
            return null;
        }
        Room r = rdao.findById(roomId);
        r.setImages(imgDao.findByRoomId(roomId));
        return r;
    }

    public Room getRoomForEdit(int id) {
        return rdao.findById(id);
    }

    public List<RoomImage> getRoomImages(int roomId) {
        return imgDao.findByRoomId(roomId);
    }

    public List<Block> getActiveBlocks() {
        return blockDAO.findAllActive();
    }

    public String updateRoom(HttpServletRequest req) {

        Integer roomId = parseIntNullable(req.getParameter("roomId"));
        if (roomId == null) {
            return "invalid_room";
        }

        Room r = rdao.findById(roomId);
        if (r == null) {
            return "invalid_room";
        }

        String oldStatus = r.getStatus();
        String newStatus = req.getParameter("status");

        if ("INACTIVE".equalsIgnoreCase(oldStatus)) {
            return "room_inactive_locked";
        }

        if ("OCCUPIED".equalsIgnoreCase(oldStatus) && newStatus != null && !oldStatus.equalsIgnoreCase(newStatus)) {
            return "occupied_locked";
        }

        Integer blockId = parseIntNullable(req.getParameter("blockId"));
        if (blockId == null || !blockDAO.exists(blockId)) {
            return "invalid_block";
        }

        r.setBlockId(blockId);
        r.setRoomNumber(req.getParameter("roomNumber"));
        r.setStatus(newStatus);
        r.setPrice(parseDecimal(req.getParameter("price")));
        r.setArea(parseDecimal(req.getParameter("area")));
        r.setFloor(parseIntNullable(req.getParameter("floor")));
        r.setMaxTenants(parseIntNullable(req.getParameter("maxTenants")));
        r.setDescription(req.getParameter("description"));

        r.setAirConditioning("on".equalsIgnoreCase(req.getParameter("airConditioning")));
        r.setMezzanine("on".equalsIgnoreCase(req.getParameter("isMezzanine")));

        boolean ok = rdao.updateRoom(r);

        return ok ? "updated" : "update_fail";
    }
}
