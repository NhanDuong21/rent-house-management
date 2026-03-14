<%-- 
    Document   : contract_table
    Author     : Duong Thien Nhan - CE190741
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="mc-card">

    <div class="mc-card-title">
        <c:choose>
            <c:when test="${not empty q || not empty status}">
                Results: <c:out value="${total}"/> contracts
                <c:if test="${not empty q}"> | keyword: "<c:out value="${q}"/>"</c:if>
                <c:if test="${not empty status}"> | status: <b><c:out value="${status}"/></b></c:if>
            </c:when>
            <c:otherwise>
                All Contracts (<c:out value="${total}"/>)
            </c:otherwise>
        </c:choose>
    </div>

    <table class="mc-table">
        <thead>
            <tr>
                <th>Contract ID</th>
                <th>Room</th>
                <th>Tenant Name</th>
                <th>Start Date</th>
                <th>Monthly Rent</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
        </thead>

        <tbody>
            <c:if test="${empty contracts}">
                <tr>
                    <td colspan="7" class="mc-empty">
                        No contracts found.
                    </td>
                </tr>
            </c:if>

            <c:forEach var="c" items="${contracts}">
                <tr>
                    <td class="mc-mono">${c.displayId}</td>
                    <td>${c.roomNumber}</td>
                    <td>${c.tenantName}</td>
                    <td>
                        <fmt:formatDate value="${c.startDate}" pattern="MMMM d, yyyy"/>
                    </td>
                    <td>
                        <fmt:formatNumber value="${c.monthlyRent}" type="number" groupingUsed="true"/> đ
                    </td>

                    <td>
                        <c:choose>
                            <c:when test="${c.status eq 'ACTIVE'}">
                                <span class="mc-badge active">ACTIVE</span>
                            </c:when>
                            <c:when test="${c.status eq 'PENDING'}">
                                <span class="mc-badge pending">PENDING</span>
                            </c:when>
                            <c:when test="${c.status eq 'ENDED'}">
                                <span class="mc-badge ended">ENDED</span>
                            </c:when>
                            <c:otherwise>
                                <span class="mc-badge cancelled">CANCELLED</span>
                            </c:otherwise>
                        </c:choose>
                    </td>

                    <td class="mc-actions">
                        <a href="${pageContext.request.contextPath}/manager/contract-detail?id=${c.contractId}"
                           class="mc-view-btn">
                            👁 View
                        </a>

                        <c:if test="${c.status eq 'PENDING'}">
                            <form method="post"
                                  action="${pageContext.request.contextPath}/manager/contracts/confirm"
                                  style="display:inline;">
                                <input type="hidden" name="contractId" value="${c.contractId}"/>

                                <button type="submit"
                                        class="mc-confirm-btn"
                                        onclick="return confirm('Confirm contract này? Contract->ACTIVE, Room->OCCUPIED, Tenant->ACTIVE');">
                                    Confirm
                                </button>
                            </form>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <!-- PAGINATION -->
    <c:if test="${totalPages > 1}">
        <div class="mc-pager">
            <div class="mc-pager-left">
                Page <c:out value="${page}"/> / <c:out value="${totalPages}"/> • Total: <c:out value="${total}"/>
            </div>

            <div class="mc-pager-right">
                <c:url var="baseUrl" value="/manager/contracts">
                    <c:param name="q" value="${q}"/>
                    <c:param name="status" value="${status}"/>
                    <c:param name="pageSize" value="${pageSize}"/>
                    <c:param name="ajax" value="1"/>
                </c:url>

                <c:choose>
                    <c:when test="${page > 1}">
                        <a class="mc-page-btn" href="${baseUrl}&page=${page - 1}">Prev</a>
                    </c:when>
                    <c:otherwise>
                        <span class="mc-page-btn disabled">Prev</span>
                    </c:otherwise>
                </c:choose>

                <c:set var="start" value="${page - 2}"/>
                <c:set var="end" value="${page + 2}"/>
                <c:if test="${start < 1}"><c:set var="start" value="1"/></c:if>
                <c:if test="${end > totalPages}"><c:set var="end" value="${totalPages}"/></c:if>

                <c:forEach var="p" begin="${start}" end="${end}">
                    <c:choose>
                        <c:when test="${p == page}">
                            <span class="mc-page-btn active"><c:out value="${p}"/></span>
                        </c:when>
                        <c:otherwise>
                            <a class="mc-page-btn" href="${baseUrl}&page=${p}"><c:out value="${p}"/></a>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>

                <c:choose>
                    <c:when test="${page < totalPages}">
                        <a class="mc-page-btn" href="${baseUrl}&page=${page + 1}">Next</a>
                    </c:when>
                    <c:otherwise>
                        <span class="mc-page-btn disabled">Next</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </c:if>

</div>