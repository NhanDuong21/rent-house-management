<%-- 
    Document   : inputMeter
    Created on : Mar 9, 2026, 11:54:08 PM
    Author     : To Thi Thao Trang - CE191027
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<layout:layout title="Input Meter"
               active="m_rooms"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerInputBill.css">

    <div class="tbg-container">

        <!-- HEADER -->
        <div class="tbg-pagehead">
            <div class="tbg-pagehead-left">
                <a href="${pageContext.request.contextPath}/manager/billing" class="tbg-back">
                    <i class="bi bi-arrow-left"></i>
                    <span>Back to Room</span>
                </a>

                <div class="tbg-breadcrumb">
                    <span>Manager</span>
                    <span>/</span>
                    <span>Room</span>
                    <span>/</span>
                    <span class="active">Input Meter</span>
                </div>
            </div>

            <div class="tbg-pagehead-right">
                <div class="tbg-minihelp">
                    <i class="bi bi-lightning-charge"></i>
                    <span>Enter electric & water meter readings</span>
                </div>
            </div>
        </div>


        <!-- CARD -->
        <div class="tbg-card">
            <div class="tbg-card-body">

                <div class="tbg-title">
                    <i class="bi bi-lightning-charge"></i> Input Meter Readings
                </div>

                <div class="tbg-sub">
                    Enter the latest electric and water meter readings for this room
                </div>


                <!-- BILL INFO -->
                <div class="tbg-grid-2">

                    <div class="tbg-field">
                        <label>Room Number</label>
                        <span class="tbg-value">${roomNumber}</span>
                    </div>

                    <div class="tbg-field">
                        <label>Billing Period</label>
                        <span class="tbg-value">
                            <fmt:setLocale value="en_US"/>
                            Bill <fmt:formatDate value="${bill.billMonth}" pattern="MMMM yyyy"/> 
                        </span>
                    </div>

                    <div class="tbg-field">
                        <label>Issue Date</label>
                        <span class="tbg-value">
                            <fmt:formatDate value="${bill.billMonth}" pattern="dd/MM/yyyy"/>
                        </span>
                    </div>

                    <div class="tbg-field">
                        <label>Due Date</label>
                        <span class="tbg-value">
                            <fmt:formatDate value="${bill.dueDate}" pattern="dd/MM/yyyy"/>
                        </span>
                    </div>

                </div>


                <div class="tbg-divider"></div>


                <!-- FORM -->
                <form action="${pageContext.request.contextPath}/manager/billing/input"  method="post" class="tbg-form">
                    <input type="hidden" name="billId" value="${bill.billId}">

                    <!-- ELECTRIC -->
                    <div class="tbg-section-title">Electricity Meter</div>

                    <div class="tbg-grid">

                        <div class="tbg-field">
                            <label>Old Electric Reading</label>
                            <input type="number"  name="oldElectric" id="oldElectric" min="0" value="${bill.oldElectricNumber}" step="1" placeholder="Previous reading">
                        </div>

                        <div class="tbg-field">
                            <label>New Electric Reading</label>
                            <input type="number"  name="newElectric" id="newElectric"  min="0" value="${bill.newElectricNumber}" step="1" placeholder="Current meter reading">
                        </div>

                        <div class="tbg-field">
                            <label>Electric Usage (kWh)</label>
                            <input type="number" id="electricUsage" readonly>
                        </div>

                    </div>


                    <!-- WATER -->
                    <div class="tbg-section-title">Water Meter</div>

                    <div class="tbg-grid">

                        <div class="tbg-field">
                            <label>Old Water Reading</label>
                            <input type="number" name="oldWater"  id="oldWater" min="0" value="${bill.oldWaterNumber}" step="1" placeholder="Previous reading">
                        </div>

                        <div class="tbg-field">
                            <label>New Water Reading</label>
                            <input type="number" name="newWater" id="newWater" min="0" value="${bill.newWaterNumber}" step="1" placeholder="Current meter reading">
                        </div>

                        <div class="tbg-field">
                            <label>Water Usage (m³)</label>
                            <input type="number" id="waterUsage" readonly>
                        </div>

                    </div>


                    <div class="tbg-actions">
                        <button type="submit" class="tbg-btn-generate">
                            <i class="bi bi-check-circle"></i>
                            Save Meter
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