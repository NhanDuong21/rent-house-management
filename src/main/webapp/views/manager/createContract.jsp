<%--
    Document   : createContract
    Author     : Duong Thien Nhan - CE190741
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="layout" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<layout:layout title="Create Contract" active="m_contracts"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerCreateContracts.css">

    <div class="mcc-wrap">

        <div class="mcc-pagehead">
            <div class="mcc-pagehead-left">
                <div class="mcc-title">
                    <i class="bi bi-file-earmark-plus"></i>
                    Create New Contract
                </div>
                <div class="mcc-subtitle">
                    Fill tenant info & contract terms to generate and send OTP.
                </div>

                <div class="mcc-breadcrumb">
                    <span class="mcc-bc-item">
                        <i class="bi bi-speedometer2"></i>
                        Manager
                    </span>
                    <span class="mcc-bc-sep">/</span>
                    <span class="mcc-bc-item">
                        <i class="bi bi-file-earmark-text"></i>
                        Contracts
                    </span>
                    <span class="mcc-bc-sep">/</span>
                    <span class="mcc-bc-item active">Create</span>
                </div>
            </div>

            <div class="mcc-pagehead-right">
                <span class="mcc-badge">
                    <i class="bi bi-shield-check"></i>
                    Manager
                </span>
            </div>
        </div>

        <div class="mcc-card">

            <c:if test="${not empty param.error}">
                <div class="mcc-alert mcc-alert-danger">
                    <div class="mcc-alert-ic"><i class="bi bi-exclamation-triangle-fill"></i></div>
                    <div class="mcc-alert-tx">
                        <div class="mcc-alert-title">Oops!</div>
                        <div class="mcc-alert-sub">${param.error}</div>
                    </div>
                </div>
            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/manager/contracts/create"
                  class="mcc-form"
                  enctype="multipart/form-data">

                <!-- Room -->
                <div class="mcc-section">
                    <div class="mcc-section-head">
                        <div class="mcc-section-title">
                            <i class="bi bi-door-open"></i>
                            Room
                        </div>
                        <div class="mcc-section-desc">Choose an available room to attach to this contract.</div>
                    </div>

                    <div class="mcc-field">
                        <label class="mcc-label">
                            <i class="bi bi-building"></i>
                            Select room
                        </label>

                        <div class="mcc-control">
                            <select name="roomId" class="form-control mcc-control-input" required>
                                <c:forEach var="r" items="${rooms}">
                                    <option value="${r.roomId}">
                                        ${r.roomNumber} - ${r.price}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="mcc-help">
                            <i class="bi bi-info-circle"></i>
                            Make sure the room is available and matches the tenant’s needs.
                        </div>
                    </div>
                </div>

                <!-- Tenant info -->
                <div class="mcc-section">
                    <div class="mcc-section-head">
                        <div class="mcc-section-title">
                            <i class="bi bi-person-vcard"></i>
                            Tenant Information
                        </div>
                        <div class="mcc-section-desc">Personal details used for contract identification & contact.</div>
                    </div>

                    <div class="mcc-grid-2">
                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-person"></i>
                                Tenant Name
                            </label>
                            <div class="mcc-control">
                                <input type="text" name="tenantName" class="form-control mcc-control-input" required
                                       placeholder="e.g. Nguyen Van A">
                            </div>
                        </div>

                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-credit-card-2-front"></i>
                                Citizen ID
                            </label>
                            <div class="mcc-control">
                                <input type="text" name="identityCode" class="form-control mcc-control-input" required
                                       placeholder="e.g. 012345678901">
                            </div>
                        </div>
                    </div>

                    <div class="mcc-grid-2">
                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-envelope"></i>
                                Email
                            </label>
                            <div class="mcc-control">
                                <input type="email" name="email" class="form-control mcc-control-input" required
                                       placeholder="e.g. tenant@email.com">
                            </div>
                        </div>

                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-telephone"></i>
                                Phone
                            </label>
                            <div class="mcc-control">
                                <input type="text" name="phone" class="form-control mcc-control-input" required
                                       placeholder="e.g. 09xxxxxxxx">
                            </div>
                        </div>
                    </div>

                    <div class="mcc-field">
                        <label class="mcc-label">
                            <i class="bi bi-geo-alt"></i>
                            Address
                        </label>
                        <div class="mcc-control">
                            <input type="text" name="address" class="form-control mcc-control-input" required
                                   placeholder="Street, ward, district...">
                        </div>
                    </div>

                    <div class="mcc-grid-2">
                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-cake2"></i>
                                Date of Birth
                            </label>
                            <div class="mcc-control">
                                <input type="date" name="dob" class="form-control mcc-control-input" required>
                            </div>
                        </div>

                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-gender-ambiguous"></i>
                                Gender
                            </label>
                            <div class="mcc-control">
                                <select name="gender" class="form-control mcc-control-input" required>
                                    <option value="">-- Select --</option>
                                    <option value="0">Female</option>
                                    <option value="1">Male</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <div class="mcc-grid-2">
                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-image"></i>
                                CCCD Front
                            </label>
                            <div class="mcc-control">
                                <input type="file"
                                       name="cccdFront"
                                       accept=".jpg,.jpeg,.png,.webp,image/*"
                                       class="form-control mcc-control-input"
                                       required>
                            </div>
                        </div>

                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-image"></i>
                                CCCD Back
                            </label>
                            <div class="mcc-control">
                                <input type="file"
                                       name="cccdBack"
                                       accept=".jpg,.jpeg,.png,.webp,image/*"
                                       class="form-control mcc-control-input"
                                       required>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Contract -->
                <div class="mcc-section">
                    <div class="mcc-section-head">
                        <div class="mcc-section-title">
                            <i class="bi bi-journal-text"></i>
                            Contract Terms
                        </div>
                        <div class="mcc-section-desc">Set rental pricing and contract period.</div>
                    </div>

                    <div class="mcc-grid-2">
                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-wallet2"></i>
                                Monthly Rent
                            </label>
                            <div class="mcc-control">
                                <input type="number" name="rent" class="form-control mcc-control-input" required min="0"
                                       placeholder="e.g. 3500000">
                            </div>
                        </div>

                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-safe2"></i>
                                Deposit
                            </label>
                            <div class="mcc-control">
                                <input type="number" name="deposit" class="form-control mcc-control-input" required min="0"
                                       placeholder="e.g. 7000000">
                            </div>
                        </div>
                    </div>

                    <div class="mcc-grid-2">
                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-calendar2-check"></i>
                                Start Date
                            </label>
                            <div class="mcc-control">
                                <input type="date" name="startDate" class="form-control mcc-control-input" required>
                            </div>
                        </div>

                        <div class="mcc-field">
                            <label class="mcc-label">
                                <i class="bi bi-calendar2-x"></i>
                                End Date
                            </label>
                            <div class="mcc-control">
                                <input type="date" name="endDate" class="form-control mcc-control-input" required>
                            </div>
                        </div>
                    </div>

                    <div class="mcc-help mcc-help-compact">
                        <i class="bi bi-shield-lock"></i>
                        After submit, the system will generate contract and send OTP to tenant for confirmation.
                    </div>
                </div>

                <div class="mcc-actions">
                    <button class="mcc-btn mcc-btn-primary" type="submit">
                        <i class="bi bi-send-check"></i>
                        Create Contract &amp; Send OTP
                    </button>
                </div>
            </form>
        </div>
    </div>

</layout:layout>