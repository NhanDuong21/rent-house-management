<%-- 
    Document   : editBill
    Created on : Mar 13, 2026, 7:09:05 PM
    Author     : To Thi Thao Trang - CE191027
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>


<layout:layout title="Edit Bill"
               active="m_billing"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerGenerateBill.css">

    <div class="tbg-container">

        <!-- HEADER -->
        <div class="tbg-pagehead">

            <div class="tbg-pagehead-left">

                <a href="${pageContext.request.contextPath}/manager/billing" class="tbg-back">
                    <i class="bi bi-arrow-left"></i>
                    <span>Back to Bills</span>
                </a>

                <div class="tbg-breadcrumb">
                    <span>Manager</span>
                    <span>/</span>
                    <span>Billing</span>
                    <span>/</span>
                    <span class="active">Edit</span>
                </div>

            </div>

            <div class="tbg-pagehead-right">
                <div class="tbg-minihelp">
                    <i class="bi bi-pencil-square"></i>
                    <span>Edit Bill</span>
                </div>
            </div>

        </div>

        <!-- CARD -->
        <div class="tbg-card">
            <div class="tbg-card-body">

                <div class="tbg-title">
                    <i class="bi bi-receipt"></i>
                    Edit Bill
                </div>
                <div class="tbg-sub">
                    Update meter information
                </div>

                <div class="tbg-divider"></div>

                <form action="${pageContext.request.contextPath}/manager/billing/editBill" method="post"
                      class="tbg-form">
                    <input type="hidden" name="paymentStatus" value="${paymentStatus}">
                    <input type="hidden" name="billId" value="${bill.billId}"/>
                    <!-- TOP -->
                    <div class="tbg-form-top">

                        <div class="tbg-left">
                            <!-- BILL MONTH -->
                            <div class="tbg-field">
                                <label>Bill Month</label>

                                <input type="month" value="<fmt:formatDate value='${bill.billMonth}' pattern='yyyy-MM'/>" readonly>
                                <input type="hidden" name="billMonth" value="<fmt:formatDate value='${bill.billMonth}' pattern='yyyy-MM-dd'/>">
                            </div>   
                        </div>

                        <div class="tbg-right">
                            <!-- DUE DATE -->
                            <div class="tbg-field">
                                <label>Due Date</label>

                                <input type="date"
                                       name="dueDate"
                                       value="<fmt:formatDate value='${bill.dueDate}' pattern='yyyy-MM-dd'/>"
                                       required>
                            </div>

                        </div>

                    </div>

                    <!-- METER -->
                    <div class="tbg-meter-title">
                        <i class="bi bi-speedometer2"></i>
                        Meter Input
                    </div>

                    <div class="tbg-meter-grid">

                        <div class="tbg-field">
                            <label>Old Electric</label>
                            <input type="number" id="oldElectric"  name="oldElectric" value="${bill.oldElectricNumber}" required>
                        </div>

                        <div class="tbg-field">
                            <label>New Electric</label>
                            <input type="number" id="newElectric" name="newElectric" value="${bill.newElectricNumber}" required>
                        </div>

                        <div class="tbg-field">
                            <label>Electric Usage</label>
                            <input type="number" id="electricUsage"  readonly>
                        </div>

                        <div class="tbg-field">
                            <label>Old Water</label>
                            <input type="number" id="oldWater"  name="oldWater" value="${bill.oldWaterNumber}"  required>
                        </div>

                        <div class="tbg-field">
                            <label>New Water</label>
                            <input type="number" name="newWater" id="newWater" value="${bill.newWaterNumber}" required>
                        </div>

                        <div class="tbg-field">
                            <label>Water Usage</label>
                            <input type="number"  id="waterUsage"  readonly>
                        </div>

                    </div>

                    <!-- ACTION -->
                    <div class="tbg-actions">

                        <button type="submit" class="tbg-btn-generate">
                            <i class="bi bi-pencil-square"></i>
                            Update Bill
                        </button>

                    </div>

                    <!-- ERROR -->
                    <c:if test="${not empty error}">
                        <div class="tbg-error">
                            ${error}
                        </div>
                    </c:if>

                </form>

            </div>
        </div>

    </div>
    <script src="${pageContext.request.contextPath}/assets/js/pages/managerGenerateBill.js"></script>
</layout:layout>