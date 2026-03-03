<%-- 
Document   : billDetail.jsp
Created on : Feb 25, 2026, 1:32:10 AM
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

        <!-- PAGE HEADER -->
        <div class="tbd-pagehead">
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
        <div class="tbd-card">
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

                    <div class="tbd-status">
                        <c:choose>
                            <c:when test="${bill.status eq 'PAID'}">
                                <span class="tbd-badge paid">PAID</span>
                            </c:when>
                            <c:otherwise>
                                <span class="tbd-badge unpaid">UNPAID</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="tbd-divider"></div>

                <!-- BILL INFO -->
                <div class="tbd-grid-2">

                    <div class="tbd-line">
                        <span class="tbd-label">Room Number</span>
                        <span class="tbd-value">${roomNumber}</span>
                    </div>

                    <div class="tbd-line">
                        <span class="tbd-label">Billing Period</span>
                        <span class="tbd-value">
                            <fmt:setLocale value="en_US"/>
                            <fmt:formatDate value="${bill.billMonth}" pattern="MMMM"/>
                            Bill
                        </span>
                    </div>

                    <div class="tbd-line">
                        <span class="tbd-label">Issue Date</span>
                        <span class="tbd-value">
                            <fmt:setLocale value="en_US"/>
                            <fmt:formatDate value="${bill.billMonth}" pattern="dd/MM/yyyy"/>
                            Bill
                        </span>
                    </div>

                    <div class="tbd-line">
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

                    <c:forEach items="${ListBillDetail}" var="d">
                        <div class="tbd-row">
                            <span>
                                ${d.itemName}
                                <c:if test="${bill.billId == d.billId && d.utilityId == 1}">
                                      (Old: ${bill.oldElectricNumber}, New : ${bill.newElectricNumber})
                                </c:if>
                                <c:if test="${bill.billId == d.billId && d.utilityId == 2}">
                                       (Old: ${bill.oldWaterNumber}, New: ${bill.newWaterNumber})
                                </c:if>
                            </span>
                            <span>
                                <fmt:formatNumber value="${d.unitPrice * d.quantity}" type="number"/> ₫
                            </span>
                        </div>
                    </c:forEach>

                    <div class="tbd-total tbd-row">
                        <span>Total Amount</span>
                        <span>
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
                            <img src="${pageContext.request.contextPath}${qr}" class="tbd-qr-img"/>
                            <div class="tbd-qr-note">
                                Scan this QR code to make payment
                            </div>
                        </div>

                        <form action="${pageContext.request.contextPath}/manager/bills/paymentConfirm"
                              method="post"
                              style="margin-top:20px;">
                            <input type="hidden" name="billId" value="${bill.billId}">
                            <button class="tbd-btn-confirm">
                                <i class="bi bi-check-circle"></i>
                                Confirm Payment Received
                            </button>
                        </form>
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger">
                                ${errorMsg}
                            </div>
                        </c:if>
                    </div>
                </c:if>

            </div>
        </div>

    </div>

</layout:layout>