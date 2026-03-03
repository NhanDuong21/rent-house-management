<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<t:layout title="Room List"
          active="room"
          cssFile="${pageContext.request.contextPath}/assets/css/views/manageRooms.css">

    <div class="manage-room container-fluid">
        <div class="page-header">
            <h2>Room List</h2>
            <p>View all rooms in the system</p>
        </div>

        <!-- SEARCH -->
        <div class="search-box">
            <i class="bi bi-search"></i>
            <input class="searchRoom"
                   type="text"
                   placeholder="Search by room number"
                   autocomplete="off">
        </div>

        <!-- CARD -->
        <div class="room-card">
            <h5>All Rooms (${totalRoom})</h5>

            <div class="room-table-wrap">
                <table class="room-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Block</th>
                            <th>Room</th>
                            <th>Area</th>
                            <th>Price</th>
                            <th>Floor</th>
                            <th>Max</th>
                            <th>Mezzanine</th>
                            <th>AC</th>
                            <th>Status</th>
                            <th style="width:130px;">Action</th>
                        </tr>
                    </thead>

                    <tbody id="roomTable">
                        <c:forEach items="${Rooms}" var="r">
                            <tr>
                                <td class="room-mono">${r.roomId}</td>
                                <td>${r.blockName}</td>
                                <td class="fw-bold roomNumber">${r.roomNumber}</td>
                                <td>${r.area}</td>
                                <td class="price">${r.price} đ</td>
                                <td>${r.floor}</td>
                                <td>${r.maxTenants}</td>

                                <!-- Mezzanine -->
                                <td>
                                    <span class="status ${r.mezzanine ? 'AVAILABLE' : 'MAINTENANCE'}">
                                        <c:choose>
                                            <c:when test="${r.mezzanine}">
                                                <i class="bi bi-check2-circle"></i> Yes
                                            </c:when>
                                            <c:otherwise>
                                                <i class="bi bi-x-circle"></i> No
                                            </c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>

                                <!-- AC -->
                                <td>
                                    <c:choose>
                                        <c:when test="${r.airConditioning}">
                                            <span class="room-ico room-ico-ac" title="Air Conditioning">
                                                <i class="bi bi-snow"></i>
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">No</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <!-- Status -->
                                <td>
                                    <span class="status ${r.status}">
                                        ${r.status}
                                    </span>
                                </td>

                                <!-- Action -->
                                <td class="text-center align-middle">
                                    <a href="${pageContext.request.contextPath}/manager/rooms?action=edit&id=${r.roomId}"
                                       class="room-action-btn">
                                        <i class="bi bi-pencil-square"></i>
                                        Edit
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>

                        <c:if test="${empty Rooms}">
                            <tr>
                                <td colspan="11" class="room-empty">
                                    No room found
                                </td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <!-- PAGINATION -->
            <div class="pagination-wrapper">
                <ul class="pagination">

                    <li class="${pageIndex == 1 ? 'disabled' : ''}">
                        <a href="${pageContext.request.contextPath}/manager/rooms?page=${pageIndex - 1}" aria-label="Previous">
                            <i class="bi bi-chevron-left"></i>
                        </a>
                    </li>

                    <c:set var="window" value="2" />
                    <c:set var="start" value="${pageIndex - window}" />
                    <c:set var="end" value="${pageIndex + window}" />

                    <c:if test="${start < 2}">
                        <c:set var="start" value="2" />
                    </c:if>

                    <c:if test="${end > totalPage - 1}">
                        <c:set var="end" value="${totalPage - 1}" />
                    </c:if>

                    <li class="${pageIndex == 1 ? 'active' : ''}">
                        <a href="${pageContext.request.contextPath}/manager/rooms?page=1">1</a>
                    </li>

                    <c:if test="${start > 2}">
                        <li class="disabled">
                            <a href="javascript:void(0)" aria-label="More pages">...</a>
                        </li>
                    </c:if>

                    <c:forEach begin="${start}" end="${end}" var="i">
                        <li class="${i == pageIndex ? 'active' : ''}">
                            <a href="${pageContext.request.contextPath}/manager/rooms?page=${i}">
                                ${i}
                            </a>
                        </li>
                    </c:forEach>

                    <c:if test="${end < totalPage - 1}">
                        <li class="disabled">
                            <a href="javascript:void(0)" aria-label="More pages">...</a>
                        </li>
                    </c:if>

                    <c:if test="${totalPage > 1}">
                        <li class="${pageIndex == totalPage ? 'active' : ''}">
                            <a href="${pageContext.request.contextPath}/manager/rooms?page=${totalPage}">
                                ${totalPage}
                            </a>
                        </li>
                    </c:if>

                    <li class="${pageIndex == totalPage ? 'disabled' : ''}">
                        <a href="${pageContext.request.contextPath}/manager/rooms?page=${pageIndex + 1}" aria-label="Next">
                            <i class="bi bi-chevron-right"></i>
                        </a>
                    </li>

                </ul>
            </div>

        </div>
    </div>

    <!-- JS (page only) -->
    <script src="${pageContext.request.contextPath}/assets/js/pages/manageRooms.js"></script>
</t:layout>