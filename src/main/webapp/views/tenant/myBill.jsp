<%-- 
    Document   : myBill
    Created on : Mar 2, 2026, 10:56:20 PM
    Author     : To Thi Thao Trang - CE191027
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<layout:layout title="My Bills"
               active="t_billing"
               cssFile="${pageContext.request.contextPath}/assets/css/views/tenantMyBill.css">

    <div class="tb-container">

        <!-- PAGE HEADER -->
        <div class="tb-header">
            <div>
                <h1>My Bills</h1>
                <span class="tb-sub">View and manage your monthly bills</span>
            </div>

            <!-- TABS -->
            <div class="tb-tabs">
                <a href="${pageContext.request.contextPath}/tenant/bill"
                   class="tb-tab active">
                    <i class="bi bi-receipt"></i> My Bills
                </a>

                <a href="${pageContext.request.contextPath}/tenant/paymentHistory"
                   class="tb-tab">
                    <i class="bi bi-clock-history"></i> Payment History
                </a>
            </div>
        </div>

        <!-- MAIN GRID -->
        <div class="tb-grid">

            <!-- LEFT SIDE - BILL DETAIL -->
            <div class="tb-card">

                <div class="tb-card-header">
                    <div>
                        <h2>Bill #${Bill.billId}</h2>
                        <span>Monthly rental invoice</span>
                    </div>

                    <c:choose>
                        <c:when test="${Bill.status eq 'PAID'}">
                            <span class="tb-badge paid">PAID</span>
                        </c:when>
                        <c:when test="${Bill.status eq 'OVERDUE'}">
                            <span class="tb-badge overdue">OVERDUE</span>
                        </c:when>
                        <c:when test="${Bill.status eq 'CANCELLED'}">
                            <span class="tb-badge cancelled">CANCELLED</span>
                        </c:when>
                        <c:otherwise>
                            <span class="tb-badge unpaid">UNPAID</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="tb-divider"></div>

                <!-- BILL INFO -->
                <div class="tb-grid-2">

                    <div class="tb-line">
                        <span class="tb-label">Room Number</span>
                        <span class="tb-value">${RoomNumber}</span>
                    </div>

                    <div class="tb-line">
                        <span class="tb-label">Billing Period</span>
                        <span class="tb-value">
                            <fmt:setLocale value="en_US"/>
                            <fmt:formatDate value="${Bill.billMonth}" pattern="MMMM"/>
                            Bill
                        </span>
                    </div>

                    <div class="tb-line">
                        <span class="tb-label">Issue Date</span>
                        <span class="tb-value">
                            <fmt:formatDate value="${Bill.billMonth}" pattern="dd/MM/yyyy"/>
                        </span>
                    </div>

                    <div class="tb-line">
                        <span class="tb-label">Due Date</span>
                        <span class="tb-value">
                            <fmt:formatDate value="${Bill.dueDate}" pattern="dd/MM/yyyy"/>
                        </span>
                    </div>

                </div>

                <div class="tb-divider soft"></div>

                <div class="tb-total-box">
                    <span class="tb-total-label">Total Amount</span>
                    <h3 class="tb-total-value">
                        <fmt:formatNumber value="${totalAmount}" type="number"/> VND
                    </h3>
                </div>

                <c:choose>

                    <c:when test="${Bill.status == 'PAID'}">
                        <div class="tb-paid-box success">
                            <div class="tb-paid-icon">✓</div>
                            <div class="tb-paid-title">This bill has been paid</div>
                            <div class="tb-paid-sub">Thank you for your payment</div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <div class="tb-paid-box danger">
                            <div class="tb-paid-icon">✕</div>
                            <div class="tb-paid-title">This bill is unpaid</div>
                            <div class="tb-paid-sub">Please complete your payment before due date</div>
                        </div>
                    </c:otherwise>

                </c:choose>

                <button class="tb-detail-btn" onclick="openModal()">
                    View Detail
                </button>
            </div>

            <!-- RIGHT SIDE - SUMMARY -->
            <div class="tb-summary">

                <!-- Room Number -->
                <div class="tb-summary-card primary">
                    <div class="tb-summary-icon">
                        <i class="bi bi-house-door"></i>
                    </div>
                    <div>
                        <span class="tb-summary-label">Room Number</span>
                        <h4>${RoomNumber}</h4>
                    </div>
                </div>

                <!-- Last Payment -->
                <div class="tb-summary-card ${empty lastPayment ? 'secondary' : 'success'}">

                    <div class="tb-summary-icon">
                        <i class="bi ${empty lastPayment ? 'bi-clock' : 'bi-check-circle'}"></i>
                    </div>

                    <div>
                        <span class="tb-summary-label">Last Payment</span>
                        <c:choose>

                            <c:when test="${not empty lastPayment}">
                                <h5>
                                    <fmt:formatDate value="${lastPayment.paidAt}" pattern="dd MMM yyyy"/>
                                </h5>

                                <small class="tb-payment-amount">
                                    <fmt:formatNumber value="${lastPayment.amount}" type="number"/> VND
                                </small>
                            </c:when>

                            <c:otherwise>
                                <h5 class="text-muted">No payment yet</h5>
                                <small class="tb-no-payment">
                                    Waiting for first payment
                                </small>
                            </c:otherwise>

                        </c:choose>

                    </div>
                </div>

                <!-- Unpaid Amount -->
                <div class="tb-summary-card danger">
                    <div class="tb-summary-icon">
                        <i class="bi bi-exclamation-circle"></i>
                    </div>
                    <div>
                        <span class="tb-summary-label">Unpaid Amount</span>
                        <h4>
                            <fmt:formatNumber value="${totalTenantUnpaid}" type="number"/> VND
                        </h4>
                    </div>
                </div>

            </div>

        </div>

    </div>
    <div id="billModal" class="custom-modal">

        <div class="custom-modal-content">

            <!-- Header -->
            <div class="custom-modal-header">
                <h5>Bill #${Bill.billId}</h5>
                <span class="close-btn" onclick="closeModal()">&times;</span>
            </div>
            <div class="modal-info-grid">

                <div>
                    <div class="modal-info-label">Room Number</div>
                    <div class="modal-info-value">${RoomNumber}</div>
                </div>

                <div>
                    <div class="modal-info-label">Bill Period</div>
                    <div class="modal-info-value">
                        <fmt:setLocale value="en_US"/>
                        <fmt:formatDate value="${Bill.billMonth}" pattern="MMMM"/> Bill
                    </div>
                </div>

                <div>
                    <div class="modal-info-label">Send Date</div>
                    <div class="modal-info-value">
                        <fmt:formatDate value="${Bill.billMonth}" pattern="dd/MM/yyyy"/>
                    </div>
                </div>

                <div>
                    <div class="modal-info-label">Due Date</div>
                    <div class="modal-info-value">
                        <fmt:formatDate value="${Bill.dueDate}" pattern="dd/MM/yyyy"/>
                    </div>
                </div>

            </div>

            <!-- Body -->
            <div class="custom-modal-body">

                <!-- Breakdown -->
                <c:forEach items="${ListBillDetail}" var="d">
                    <div class="tb-break-row">
                        <span>
                            ${d.itemName}

                            <!-- Electric -->
                            <c:if test="${Bill.billId == d.billId && d.utilityId == 1}">
                                <strong>(Old: ${Bill.oldElectricNumber}, New: ${Bill.newElectricNumber})</strong>
                            </c:if>

                            <!-- Water -->
                            <c:if test="${Bill.billId == d.billId && d.utilityId == 2}">
                                <strong>(Old: ${Bill.oldWaterNumber}, New: ${Bill.newWaterNumber})</strong>
                            </c:if>
                        </span>

                        <span>
                            <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="number"/> ₫
                        </span>
                    </div>
                </c:forEach>

                <div class="tb-break-total tb-break-row">
                    <span><strong>Total</strong></span>
                    <span>
                        <fmt:formatNumber value="${totalAmount}" type="number"/> ₫
                    </span>
                </div>

                <hr>

                <c:choose>

                    <c:when test="${Bill.status eq 'PAID'}">
                        <div class="alert alert-success">
                            This bill has been paid.
                        </div>
                    </c:when>

                    <c:when test="${Bill.status eq 'CANCELLED'}">
                        <div class="alert alert-secondary">
                            This bill has been cancelled.
                        </div>
                    </c:when>

                    <c:when test="${Bill.status eq 'UNPAID' or Bill.status eq 'OVERDUE'}">

                        <c:if test="${not empty pendingPayment}">
                            <div class="alert alert-warning">
                                Payment is waiting for manager confirmation.
                            </div>
                        </c:if>

                        <c:if test="${empty pendingPayment}">
                            <form action="${pageContext.request.contextPath}/tenant/payment"
                                  method="post">
                                <input type="hidden" name="billId" value="${Bill.billId}">
                                <input type="hidden" name="amount" value="${totalAmount}">

                                <label>Payment Method</label>
                                <select name="method" id="paymentMethod" required>
                                    <option value="" selected disabled>-- Select Payment Method --</option>
                                    <option value="BANK">Bank Transfer</option>
                                    <option value="CASH">Cash</option>
                                </select>
                                
                                <div id="qrContainer" style="display: none ; text-align:center;">
                                    <img id="qrImage"
                                         src="${pageContext.request.contextPath}${qr}"  alt="QR Code" style="width:200px; height:200px;">
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
    <script src="${pageContext.request.contextPath}/assets/js/pages/tenantBillDetail.js"></script>
</layout:layout>