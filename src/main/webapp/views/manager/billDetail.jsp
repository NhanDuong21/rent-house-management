<%-- 
Document   : billDetail.jsp
Created on : Feb 25, 2026, 1:32:10 AM
Author     : To Thi Thao Trang - CE191027
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<layout:layout title="Bill Detail"
               active="m_billing"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerBillDetail.css">

    <div class="tbd-container">

        <!-- DECOR BACKGROUND -->
        <div class="tbd-blob tbd-blob-1"></div>
        <div class="tbd-blob tbd-blob-2"></div>
        <div class="tbd-blob tbd-blob-3"></div>

        <!-- PAGE HEADER -->
        <div class="tbd-pagehead tbd-reveal">
            <div class="tbd-pagehead-left">

                <a href="${pageContext.request.contextPath}/manager/billing" class="tbd-back">
                    <i class="bi bi-arrow-left"></i>
                    <span>Back to Bills</span>
                </a>

                <div class="tbd-breadcrumb">
                    <span>Manager</span>
                    <span>/</span>
                    <span>Billing</span>
                    <span>/</span>
                    <span class="active">Detail</span>
                </div>
            </div>

            <div class="tbd-pagehead-right">
                <div class="tbd-minihelp">
                    <i class="bi bi-shield-check"></i>
                    <span>Manager view</span>
                </div>
            </div>
        </div>

        <!-- MAIN CARD -->
        <div class="tbd-card tbd-reveal">
            <div class="tbd-card-body">

                <!-- TOP -->
                <div class="tbd-top">
                    <div>
                        <div class="tbd-title">
                            <i class="bi bi-receipt"></i>
                            Bill #${bill.billId}
                        </div>
                        <div class="tbd-sub">
                            Monthly rental invoice details
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${bill.status eq 'PAID'}">
                            <span class="tbd-badge paid">PAID</span>
                        </c:when>

                        <c:when test="${bill.status eq 'CANCELLED'}">
                            <span class="tbd-badge cancelled">CANCELLED</span>
                        </c:when>

                        <c:otherwise>
                            <span class="tbd-badge unpaid">UNPAID</span>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="tbd-divider"></div>

                <!-- BILL INFO -->
                <div class="tbd-grid-2">
                    <div class="tbd-line info-card">
                        <span class="tbd-label">Room Number</span>
                        <span class="tbd-value">${roomNumber}</span>
                    </div>

                    <div class="tbd-line info-card">
                        <span class="tbd-label">Billing Period</span>
                        <span class="tbd-value">
                            <fmt:setLocale value="en_US"/>
                            <fmt:formatDate value="${bill.billMonth}" pattern="MMMM"/>
                            Bill
                        </span>
                    </div>

                    <div class="tbd-line info-card">
                        <span class="tbd-label">Issue Date</span>
                        <span class="tbd-value">
                            <fmt:setLocale value="en_US"/>
                            <fmt:formatDate value="${bill.billMonth}" pattern="dd/MM/yyyy"/>
                            Bill
                        </span>
                    </div>

                    <div class="tbd-line info-card">
                        <span class="tbd-label">Due Date</span>
                        <span class="tbd-value">
                            <fmt:formatDate value="${bill.dueDate}" pattern="dd/MM/yyyy"/>
                        </span>
                    </div>
                </div>

                <div class="tbd-divider tbd-divider-soft"></div>

                <!-- BREAKDOWN -->
                <div class="tbd-section">
                    <div class="tbd-section-title">
                        <i class="bi bi-list-ul"></i>
                        Bill Breakdown
                    </div>

                    <div class="tbd-breakdown">
                        <c:forEach items="${ListBillDetail}" var="d" varStatus="loop">
                            <div class="tbd-row breakdown-row" style="--delay:${loop.index * 0.05}s;">
                                <span>
                                    ${d.itemName}
                                    <c:if test="${bill.billId == d.billId && d.utilityId == 1}">
                                        <span class="tbd-inline-note">
                                            (Old: ${bill.oldElectricNumber}, New: ${bill.newElectricNumber})
                                        </span>
                                    </c:if>
                                    <c:if test="${bill.billId == d.billId && d.utilityId == 2}">
                                        <span class="tbd-inline-note">
                                            (Old: ${bill.oldWaterNumber}, New: ${bill.newWaterNumber})
                                        </span>
                                    </c:if>
                                </span>
                                <span class="tbd-money">
                                    <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="number"/> ₫
                                </span>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="tbd-total tbd-row">
                        <span>Total Amount</span>
                        <span class="tbd-total-amount" data-total="${totalAmount}">
                            <fmt:formatNumber value="${totalAmount}" type="number"/> ₫
                        </span>
                    </div>
                </div>

                <!-- QR -->
                <c:if test="${bill.status eq 'UNPAID'}">
                    <div class="tbd-divider"></div>

                    <div class="tbd-qr-section">
                        <div class="tbd-qr-title">
                            <i class="bi bi-qr-code-scan"></i>
                            Payment QR
                        </div>

                        <div class="tbd-qr-box">
                            <div class="tbd-qr-glow"></div>
                            <img src="${pageContext.request.contextPath}${qr}" class="tbd-qr-img" alt="Payment QR"/>
                            <div class="tbd-qr-note">
                                Scan this QR code to make payment
                            </div>
                        </div>

                        <c:choose>

                            <c:when test="${paymentStatus eq 'PENDING'}">

                                <form action="${pageContext.request.contextPath}/manager/bills/paymentConfirm"
                                      method="post" class="tbd-confirm-form">
                                    <input type="hidden" name="billId" value="${bill.billId}">
                                    <button class="tbd-btn-confirm" id="confirmPaymentBtn">
                                        <i class="bi bi-check-circle"></i>
                                        <span>Confirm Payment Received</span>
                                    </button>
                                </form>

                            </c:when>

                            <c:otherwise>

                                <button class="tbd-btn-confirm" disabled style="margin-top:20px;">
                                    <i class="bi bi-check-circle"></i>
                                    <span>Confirm Payment Received</span>
                                </button>

                                <div class="tbd-qr-note tbd-wait-note">
                                    Waiting for tenant payment request
                                </div>

                            </c:otherwise>

                        </c:choose>

                        <c:if test="${not empty errorMsg}">
                            <div class="tbd-alert-danger">
                                ${errorMsg}
                            </div>
                        </c:if>

                    </div>
                </c:if>

            </div>
        </div>

    </div>

    <script src="${pageContext.request.contextPath}/assets/js/pages/managerBillDetail.js"></script>
</layout:layout>