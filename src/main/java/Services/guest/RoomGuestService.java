package Services.guest;

import java.math.BigDecimal;
import java.util.List;

import DALs.room.RoomDAO;
import DALs.room.RoomImageDAO;
import Models.dto.PageResult;
import Models.dto.RoomFilterDTO;
import Models.entity.Room;

/**
 * Description
 *
 * @author Duong Thien Nhan - CE190741
 * @since 2026-02-06
 */
public class RoomGuestService {

    private final RoomDAO rdao = new RoomDAO();
    private final RoomImageDAO imgDao = new RoomImageDAO();

    // phễu lọc (browser nhận string db nhận bigdec)
    public PageResult<Room> searchAvailablePaged(String minPrice, String maxPrice, String minArea, String maxArea,
            String hasAC, String hasMezzanine, int page, int pageSize) {

        RoomFilterDTO f = new RoomFilterDTO();
        f.setMinPrice(parseDecimal(minPrice));
        f.setMaxPrice(parseDecimal(maxPrice));
        f.setMinArea(parseDecimal(minArea));
        f.setMaxArea(parseDecimal(maxArea));
        f.setHasAirConditioning(parseTriState(hasAC));
        f.setHasMezzanine(parseTriState(hasMezzanine));

        int totalItems = rdao.countAvailable(f);
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

        List<Room> rooms = rdao.searchAvailablePaged(f, page, pageSize);

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

    public Room getRoomDetailForGuest(int roomId) {

        if (roomId <= 0) {
            return null;
        }

        Room room = rdao.findById(roomId);
        if (room == null) {
            return null;
        }

        // only allowed view room available
        if (!"AVAILABLE".equalsIgnoreCase(room.getStatus())) {
            return null;
        }

        room.setImages(imgDao.findByRoomId(roomId));
        return room;
    }
}
