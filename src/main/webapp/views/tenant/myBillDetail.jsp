<%-- 
    Document   : myBillDetail
    Created on : Mar 5, 2026, 10:24:04 PM
    Author     : To Thi Thao Trang - CE191027
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<layout:layout title="Bill Detail"
               active="t_billing"
               cssFile="${pageContext.request.contextPath}/assets/css/views/tenantMyBillDetail.css">

    <div class="tbd-container">

        <!-- PAGE HEADER -->
        <div class="tbd-pagehead">
            <div class="tbd-pagehead-left">
                <a href="${pageContext.request.contextPath}/tenant/bill" class="tbd-back">
                    <i class="bi bi-arrow-left"></i>
                    <span>Back to Bills</span>
                </a>

                <div class="tbd-breadcrumb">
                    <span>Tenant</span>
                    <span>/</span>
                    <span>Billing</span>
                    <span>/</span>
                    <span class="active">Detail</span>
                </div>

            </div>

        </div>


        <!-- MAIN CARD -->
        <div class="tbd-card">
            <div class="tbd-card-body">
                <!-- TITLE -->
                <div class="tbd-top">
                    <div>
                        <div class="tbd-title">
                            <i class="bi bi-receipt"></i>
                            Bill #${billDetail.billId}
                        </div>
                        <div class="tbd-sub">
                            Monthly rental invoice
                        </div>
                    </div>

                    <c:choose>

                        <c:when test="${billDetail.status eq 'PAID'}">
                            <span class="tbd-badge paid">PAID</span>
                        </c:when>

                        <c:when test="${billDetail.status eq 'CANCELLED'}">
                            <span class="tbd-badge cancelled">CANCELLED</span>
                        </c:when>

                        <c:otherwise>
                            <span class="tbd-badge unpaid">UNPAID</span>
                        </c:otherwise>

                    </c:choose>
                </div>
                <div class="tbd-divider"></div>
                <!-- BILL INFO -->
                <div class="modal-section">
                    <h4>Bill Information</h4>
                    <div class="modal-info-grid">
                        <div>
                            <div class="modal-info-label">Room Number</div>
                            <div class="modal-info-value">${RoomNumber}</div>
                        </div>

                        <div>
                            <div class="modal-info-label">Bill Period</div>
                            <div class="modal-info-value">
                                <fmt:setLocale value="en_US"/>
                                <fmt:formatDate value="${billDetail.billMonth}" pattern="MMMM"/> Bill
                            </div>
                        </div>

                        <div>
                            <div class="modal-info-label">Issue Date</div>
                            <div class="modal-info-value">
                                <fmt:formatDate value="${billDetail.billMonth}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>

                        <div>
                            <div class="modal-info-label">Due Date</div>
                            <div class="modal-info-value">
                                <fmt:formatDate value="${billDetail.dueDate}" pattern="dd/MM/yyyy"/>
                            </div>
                        </div>

                    </div>

                </div>

                <!-- BILL BREAKDOWN -->
                <div class="modal-section">
                    <h4>Bill Breakdown</h4>
                    <c:forEach items="${ListBillDetail}" var="d">

                        <div class="tb-break-row">
                            <span>
                                ${d.itemName}
                                <!-- Electric -->
                                <c:if test="${billDetail.billId == d.billId && d.utilityId == 1}">
                                    <strong>
                                        (Old: ${billDetail.oldElectricNumber}, New: ${billDetail.newElectricNumber})
                                    </strong>
                                </c:if>

                                <!-- Water -->
                                <c:if test="${billDetail.billId == d.billId && d.utilityId == 2}">
                                    <strong>
                                        (Old: ${billDetail.oldWaterNumber}, New: ${billDetail.newWaterNumber})
                                    </strong>
                                </c:if>

                                <c:if test="${ billDetail.billId == d.billId && d.chargeType ne 'RENT' && d.utilityId != 1 && d.utilityId != 2 && d.utilityId != 3 }">
                                    <strong>
                                        (Quantity: <fmt:formatNumber value="${d.quantity}"/>)
                                    </strong>
                                </c:if>
                            </span>

                            <span>
                                <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="number"/> ₫
                            </span>
                        </div>
                    </c:forEach>

                    <div class="tb-break-total tb-break-row">
                        <span>Total Amount</span>
                        <span>
                            <fmt:formatNumber value="${totalAmount}" type="number"/> ₫
                        </span>
                    </div>
                </div>

                <!-- BILL STATUS / PAYMENT -->
                <div class="modal-section">

                    <c:choose>
                        <c:when test="${billDetail.status eq 'PAID'}">
                            <div class="alert alert-success">
                                This bill has been paid.
                            </div>
                        </c:when>

                        <c:when test="${billDetail.status eq 'CANCELLED'}">
                            <div class="alert alert-secondary">
                                This bill has been cancelled.
                            </div>
                        </c:when>

                        <c:when test="${billDetail.status eq 'UNPAID'}">

                            <c:if test="${not empty pendingPayment}">
                                <div class="alert alert-warning payment-alert">
                                    Payment is waiting for manager confirmation.
                                </div>
                            </c:if>

                            <c:if test="${empty pendingPayment}">
                                <form action="${pageContext.request.contextPath}/tenant/payment" method="post">

                                    <input type="hidden" name="billId" value="${billDetail.billId}">
                                    <input type="hidden" name="amount" value="${totalAmount}">

                                    <label>Payment Method</label>
                                    <select name="method" id="paymentMethod" required>
                                        <option value="" selected disabled>
                                            -- Select Payment Method --
                                        </option>
                                        <option value="BANK">Bank Transfer</option>
                                        <option value="CASH">Cash</option>
                                    </select>

                                    <!-- QR -->
                                    <div class="qr" style="text-align:center;margin:10px;">
                                        <img src="${pageContext.request.contextPath}${qr}"
                                             style="width:200px;height:200px;">
                                    </div>

                                    <button type="submit" class="submit-btn">
                                        Submit Payment
                                    </button>

                                </form>
                            </c:if>

                        </c:when>
                    </c:choose>

                </div>

            </div>

        </div>

    </div>
    <script src="${pageContext.request.contextPath}/assets/js/pages/tenantBillDetail.js"></script>
</layout:layout>
