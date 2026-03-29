<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<layout:layout title="Extend Contract"
               active="m_contracts"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerExtendContract.css?v=2">

    <c:set var="cur" value="${cur}"/>

    <div class="me-container">

        <a href="${pageContext.request.contextPath}/manager/contract-detail?id=${cur.contractId}" class="me-back">
            ← Back to Contract Detail
        </a>

        <div class="me-card">
            <div class="me-card-head">
                <div>
                    <div class="me-title">
                        Extend Contract #<fmt:formatNumber value="${cur.contractId}" pattern="000000"/>
                    </div>
                    <div class="me-sub">
                        Room <b><c:out value="${cur.roomNumber}"/></b>
                        <span class="me-dot">•</span>
                        Tenant <b><c:out value="${cur.tenantName}"/></b>
                    </div>
                </div>
            </div>

            <c:if test="${param.err eq 'DATE'}">
                <div class="me-alert me-alert-danger">
                    New end date must be greater than current end date.
                </div>
            </c:if>

            <c:if test="${param.err eq 'EXTEND_FAIL'}">
                <div class="me-alert me-alert-danger">
                    Extend contract failed. Please try again.
                </div>
            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/manager/contracts/extend"
                  class="me-form"
                  id="extendContractForm">

                <input type="hidden" name="contractId" value="${cur.contractId}"/>

                <div class="me-grid">

                    <div class="me-field">
                        <label class="me-label" for="currentStartDate">Current Start Date</label>
                        <input class="me-input"
                               id="currentStartDate"
                               type="date"
                               value="<fmt:formatDate value='${cur.startDate}' pattern='yyyy-MM-dd'/>"
                               readonly>
                    </div>

                    <div class="me-field">
                        <label class="me-label" for="currentEndDate">Current End Date</label>
                        <input class="me-input"
                               id="currentEndDate"
                               type="date"
                               value="<fmt:formatDate value='${cur.endDate}' pattern='yyyy-MM-dd'/>"
                               readonly>
                    </div>

                    <div class="me-field me-field-full">
                        <label class="me-label" for="newEndDate">New End Date</label>
                        <input class="me-input"
                               id="newEndDate"
                               type="date"
                               name="endDate"
                               required
                               autocomplete="off">
                        <div class="me-hint" id="extendHint">
                            The contract will be extended by exactly <b>1 year</b> from the current end date.
                        </div>
                        <div class="me-error" id="newEndDateError" aria-live="polite"></div>
                    </div>

                </div>

                <div class="me-summary me-reveal" id="extendSummary">
                    <div class="me-summary-head">
                        <div class="me-summary-title">Extension Summary</div>
                    </div>

                    <div class="me-summary-grid">
                        <div class="me-summary-item">
                            <div class="me-summary-label">Current End Date</div>
                            <div class="me-summary-value" id="summaryCurrentEnd">-</div>
                        </div>

                        <div class="me-summary-item">
                            <div class="me-summary-label">Expected New End Date</div>
                            <div class="me-summary-value" id="summaryExpectedEnd">-</div>
                        </div>

                        <div class="me-summary-item">
                            <div class="me-summary-label">Selected New End Date</div>
                            <div class="me-summary-value" id="summarySelectedEnd">-</div>
                        </div>

                        <div class="me-summary-item">
                            <div class="me-summary-label">Status</div>
                            <div class="me-summary-value" id="summaryStatus">Pending</div>
                        </div>
                    </div>
                </div>

                <div class="me-actions">
                    <button type="submit"
                            id="submitBtn"
                            class="me-btn me-btn-primary"
                            data-confirm="Extend this contract by updating its end date?">
                        <span class="me-btn-text">Extend Contract</span>
                    </button>

                    <a class="me-btn me-btn-ghost"
                       href="${pageContext.request.contextPath}/manager/contract-detail?id=${cur.contractId}">
                        Cancel
                    </a>
                </div>

                <div class="me-note">
                    This action updates the <b>end date</b> of the current active contract. No new contract will be created.
                </div>
            </form>
        </div>

    </div>

    <script src="${pageContext.request.contextPath}/assets/js/pages/managerExtendContract.js?v=1"></script>
</layout:layout>