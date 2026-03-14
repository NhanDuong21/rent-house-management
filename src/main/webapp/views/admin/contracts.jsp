<%-- 
    Document   : contract
    Author     : Duong Thien Nhan - CE190741
--%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<layout:layout title="Manage Contracts"
               active="m_contracts"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerContracts.css">

    <div class="mc-container">

        <!-- HEADER -->
        <div class="mc-header">
            <div>
                <h2>Manage Contracts</h2>
                <p>View and manage all rental contracts</p>
            </div>
        </div>

        <!-- ALERTS -->
        <c:if test="${param.confirmed eq '1'}">
            <div class="mc-alert mc-alert-success">
                <span class="mc-alert-ico" aria-hidden="true">
                    <!-- bootstrap icon: check-circle -->
                    <svg viewBox="0 0 16 16">
                    <path d="M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zm3.354-8.354a.5.5 0 0 0-.708-.708L7.5 9.086 5.354 6.94a.5.5 0 1 0-.708.708l2.5 2.5a.5.5 0 0 0 .708 0l3.5-3.5z"/>
                    </svg>
                </span>
                <div>
                    <div class="mc-alert-title">Confirm contract successfully.</div>
                </div>
            </div>
        </c:if>

        <c:if test="${param.err eq '1'}">
            <div class="mc-alert mc-alert-danger">
                <span class="mc-alert-ico" aria-hidden="true">
                    <!-- bootstrap icon: x-circle -->
                    <svg viewBox="0 0 16 16">
                    <path d="M8 15A7 7 0 1 0 8 1a7 7 0 0 0 0 14zM5.354 5.354a.5.5 0 1 1 .708-.708L8 6.586l1.938-1.94a.5.5 0 1 1 .708.708L8.707 7.293l1.939 1.938a.5.5 0 0 1-.708.708L8 8.001 6.062 9.94a.5.5 0 0 1-.708-.708l1.939-1.939-1.94-1.939z"/>
                    </svg>
                </span>

                <div>
                    <div class="mc-alert-title">
                        Confirm failed.
                        <c:if test="${not empty param.code}">
                            <span class="mc-alert-code">code: ${param.code}</span>
                        </c:if>
                    </div>

                    <c:if test="${param.code eq 'NEED_TENANT_PAYMENT'}">
                        <div class="mc-alert-text">Please wait for tenant payment confirmation.</div>
                    </c:if>
                </div>
            </div>
        </c:if>

        <!-- SEARCH (giữ đúng param q/status/pageSize) -->
        <div class="mc-search-box">
            <form id="mcSearchForm" class="mc-search-form" method="get"
                  action="${pageContext.request.contextPath}/manager/contracts">

                <input id="mcQ" type="text" name="q"
                       value="${q}"
                       placeholder="Search by contract ID, room number, or tenant name...">

                <select id="mcStatus" name="status">
                    <option value="" ${empty status ? "selected" : ""}>All Status</option>
                    <option value="PENDING"   ${status eq 'PENDING' ? "selected" : ""}>PENDING</option>
                    <option value="ACTIVE"    ${status eq 'ACTIVE' ? "selected" : ""}>ACTIVE</option>
                    <option value="ENDED"     ${status eq 'ENDED' ? "selected" : ""}>ENDED</option>
                    <option value="CANCELLED" ${status eq 'CANCELLED' ? "selected" : ""}>CANCELLED</option>
                </select>

                <select id="mcPageSize" name="pageSize">
                    <option value="5"  ${pageSize == 5 ? "selected" : ""}>5 / page</option>
                    <option value="10" ${pageSize == 10 ? "selected" : ""}>10 / page</option>
                    <option value="20" ${pageSize == 20 ? "selected" : ""}>20 / page</option>
                    <option value="50" ${pageSize == 50 ? "selected" : ""}>50 / page</option>
                </select>

                <c:if test="${not empty q || not empty status}">
                    <a class="mc-clear-btn" href="${pageContext.request.contextPath}/manager/contracts">Clear</a>
                </c:if>
            </form>
        </div>

        <!-- TABLE -->
        <div id="contractTableWrapper">
            <jsp:include page="../manager/_contracts_table.jsp"/>
        </div>

    </div>

    <script>
        window.__CTX = "${pageContext.request.contextPath}";
    </script>
    <script src="${pageContext.request.contextPath}/assets/js/pages/managerContracts.js"></script>
    <script src="${pageContext.request.contextPath}/assets/js/pages/managerContractModal.js"></script>

    <!-- MODAL (giữ inline style để chắc chắn không tụt) -->
    <div id="contractTypeModal" style="
         display:none;
         position:fixed;
         inset:0;
         background:rgba(0,0,0,0.4);
         justify-content:center;
         align-items:center;
         z-index:9999;">

        <div class="mc-modal-dialog">

            <div class="mc-modal-title">Create Contract For?</div>

            <div class="mc-modal-actions">
                <a href="${pageContext.request.contextPath}/manager/contracts/create"
                   class="mc-modal-btn primary">
                    New Tenant (No Account)
                </a>

                <a href="${pageContext.request.contextPath}/manager/contracts/create-existing"
                   class="mc-modal-btn info">
                    Existing Tenant (Has Account)
                </a>
            </div>

            <button onclick="closeContractTypeModal()"
                    class="mc-modal-close"
                    type="button"
                    aria-label="Close">
                <!-- bootstrap icon: x-lg -->
                <svg viewBox="0 0 16 16" aria-hidden="true">
                <path d="M2.146 2.854a.5.5 0 1 1 .708-.708L8 7.293l5.146-5.147a.5.5 0 0 1 .708.708L8.707 8l5.147 5.146a.5.5 0 0 1-.708.708L8 8.707l-5.146 5.147a.5.5 0 0 1-.708-.708L7.293 8 2.146 2.854z"/>
                </svg>
            </button>

        </div>
    </div>

</layout:layout>