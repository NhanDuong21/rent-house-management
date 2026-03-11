<%-- 
    Document   : paymentHistory.jsp
    Created on : Mar 4, 2026, 2:59:48 AM
    Author     : To Thi Thao Trang - CE191027
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<layout:layout title="Payment History"
               active="t_billing"
               cssFile="${pageContext.request.contextPath}/assets/css/views/tenantPaymentHistory.css">

    <div class="tph-container">

        <!-- HEADER -->
        <div class="tph-header">
            <div>
                <h1>Payment History</h1>
                <span class="tph-sub">View all your payment transactions</span>
            </div>

            <div class="tph-tabs">
                <a href="${pageContext.request.contextPath}/tenant/bill"
                   class="tph-tab">
                    <i class="bi bi-receipt"></i> My Bills
                </a>

                <a href="${pageContext.request.contextPath}/tenant/paymentHistory"
                   class="tph-tab active">
                    <i class="bi bi-clock-history"></i> Payment History
                </a>
            </div>
        </div>

        <!-- TABLE CARD -->
        <div class="tph-card">

            <table class="tph-table">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>Room</th>
                        <th>Method</th>
                        <th>Status</th>
                        <th>Bill Month</th>
                        <th>Paid Date</th>
                        <th>Amount</th>
                    </tr>
                </thead>

                <tbody>
                    <c:forEach items="${paymentHistory}" var="p" varStatus="st">
                        <tr>
                            <td>${st.index + 1}</td>
                            <td>${p.roomNumber}</td>
                            <td>${p.method}</td>

                            <td>
                                <c:choose>
                                    <c:when test="${p.status == 'CONFIRMED'}">
                                        <span class="tph-badge confirmed">CONFIRMED</span>
                                    </c:when>

                                    <c:when test="${p.status == 'PENDING'}">
                                        <span class="tph-badge pending">PENDING</span>
                                    </c:when>

                                    <c:when test="${p.status == 'REJECTED'}">
                                        <span class="tph-badge rejected">REJECTED</span>
                                    </c:when>

                                    <c:otherwise>
                                        <span class="tph-badge">${p.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <c:choose>

                                    <c:when test="${empty p.billMonth}"> Deposit </c:when>
                                    <c:otherwise>
                                        <fmt:formatDate value="${p.billMonth}" pattern="MMM yyyy"/>
                                    </c:otherwise>

                                </c:choose>
                            </td>

                            <td>
                                <fmt:formatDate value="${p.paidAt}" pattern="dd/MM/yyyy"/>
                            </td>

                            <td class="amount">
                                <fmt:formatNumber value="${p.amount}" type="number"/> ₫
                            </td>


                        </tr>
                    </c:forEach>

                    <c:if test="${empty paymentHistory}">
                        <tr>
                            <td colspan="7" class="empty">
                                No payment history found.
                            </td>
                        </tr>
                    </c:if>

                </tbody>
            </table>

        </div>

    </div>

</layout:layout>
