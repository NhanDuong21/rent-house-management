<%-- 
    Document   : utilities
    Created on : Feb 25, 2026, 2:44:36 PM
    Author     : Bui Nhu Y
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="layout" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">

<layout:layout title="Manage Utilities"
               active="m_utilities"
               cssFile="${pageContext.request.contextPath}/assets/css/views/managerUtilities.css">

    <div class="mb-container">
        <div class="mb-bg-orb orb-1"></div>
        <div class="mb-bg-orb orb-2"></div>
        <div class="mb-bg-grid"></div>

        <!-- HEADER -->
        <section class="mb-hero">
            <div class="mb-hero-left">
                <div class="mb-badge">
                    <i class="bi bi-lightning-charge-fill"></i>
                    Utility Management Center
                </div>

                <h2 class="mb-hero-title">Manage Utilities</h2>
                <p class="mb-hero-subtitle">
                    Manage utility services, pricing, and subscriber lists with a more modern dashboard experience.
                </p>

                <div class="mb-hero-stats">
                    <div class="mb-stat-card">
                        <span class="mb-stat-icon"><i class="bi bi-grid-1x2-fill"></i></span>
                        <div>
                            <strong><c:out value="${empty utilities ? 0 : utilities.size()}"/></strong>
                            <small>Total Utilities</small>
                        </div>
                    </div>

                    <div class="mb-stat-card">
                        <span class="mb-stat-icon"><i class="bi bi-search-heart"></i></span>
                        <div>
                            <strong>Fast Search</strong>
                            <small>Live filter by utility name</small>
                        </div>
                    </div>
                </div>
            </div>

            <div class="mb-hero-right">
                <button type="button"
                        class="mb-primary-btn"
                        onclick="openModal('addModal')">
                    <i class="bi bi-plus-circle-fill"></i>
                    <span>Add Utility</span>
                </button>
            </div>
        </section>

        <!-- ALERT -->
        <div class="mb-alert-stack">
            <c:if test="${successMsg != null}">
                <div class="mb-alert mb-alert-success">
                    <div class="mb-alert-icon">
                        <i class="bi bi-check-circle-fill"></i>
                    </div>
                    <div class="mb-alert-content">
                        <strong>Success</strong>
                        <span>${successMsg}</span>
                    </div>
                    <button class="mb-alert-close" type="button" onclick="this.parentElement.remove()">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>
            </c:if>

            <c:if test="${errorMsg != null}">
                <div class="mb-alert mb-alert-error">
                    <div class="mb-alert-icon">
                        <i class="bi bi-exclamation-triangle-fill"></i>
                    </div>
                    <div class="mb-alert-content">
                        <strong>Error</strong>
                        <span>${errorMsg}</span>
                    </div>
                    <button class="mb-alert-close" type="button" onclick="this.parentElement.remove()">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>
            </c:if>
        </div>

        <!-- TOOLBAR -->
        <section class="mb-toolbar glass-card">
            <div class="mb-search-box">
                <div class="mb-search-form">
                    <i class="bi bi-search"></i>
                    <input type="text"
                           id="searchInput"
                           placeholder="Search by utility name...">
                </div>
            </div>

            <div class="mb-toolbar-right">
                <div class="mb-chip">
                    <i class="bi bi-stars"></i>
                    Smooth animated UI
                </div>
            </div>
        </section>

        <!-- MAIN CARD -->
        <section class="mb-card glass-card">
            <div class="mb-card-header">
                <div>
                    <div class="mb-card-title">All Utilities</div>
                    <div class="mb-card-subtitle">
                        Total: <strong><c:out value="${empty utilities ? 0 : utilities.size()}"/></strong> services
                    </div>
                </div>
            </div>

            <div class="mb-table-wrap">
                <table class="mb-table">
                    <thead>
                        <tr>
                            <th>Utility Name</th>
                            <th>Price</th>
                            <th>Subscribers</th>
                            <th>Action</th>
                        </tr>
                    </thead>

                    <tbody id="utilityTable">
                        <c:forEach var="u" items="${utilities}" varStatus="loop">
                            <tr class="utility-row" style="--delay:${loop.index};">
                                <td>
                                    <div class="mb-utility-cell">
                                        <div class="mb-utility-icon">
                                            <i class="bi bi-lightning-charge"></i>
                                        </div>
                                        <div>
                                            <div class="utilityName mb-utility-name">${u.utilityName}</div>
                                            <div class="mb-utility-meta">Service ID: #${u.utilityId}</div>
                                        </div>
                                    </div>
                                </td>

                                <td>
                                    <div class="mb-price-pill">
                                        <i class="bi bi-cash-coin"></i>
                                        <span>
                                            <fmt:formatNumber value="${u.standardPrice}"
                                                              type="number"
                                                              groupingUsed="true"/> đ/${u.unit}
                                        </span>
                                    </div>
                                </td>

                                <td style="white-space: nowrap;">
                                    <a href="${pageContext.request.contextPath}/manager/utilities?action=subscribers&id=${u.utilityId}&name=${u.utilityName}"
                                       class="mb-action-btn mb-view-btn">
                                        <i class="bi bi-people-fill"></i>
                                        <span>View</span>
                                    </a>
                                </td>

                                <td>
                                    <div class="action-buttons">
                                        <a href="${pageContext.request.contextPath}/manager/utilities?action=edit&id=${u.utilityId}"
                                           class="mb-action-btn mb-edit-btn">
                                            <i class="bi bi-pencil-square"></i>
                                            <span>Edit</span>
                                        </a>

                                        <a href="${pageContext.request.contextPath}/manager/utilities?action=delete&id=${u.utilityId}"
                                           class="mb-action-btn mb-delete-btn"
                                           onclick="return confirm('Are you sure you want to delete this utility?')">
                                            <i class="bi bi-trash-fill"></i>
                                            <span>Delete</span>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>

                        <tr id="notFoundUtility" style="display:none;">
                            <td colspan="4" class="mb-empty">
                                <div class="mb-empty-state">
                                    <i class="bi bi-search"></i>
                                    <p>No utilities found.</p>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </section>

        <!-- ADD MODAL -->
        <div id="addModal" class="mb-modal-overlay" onclick="handleOverlayClick(event, 'addModal')">
            <div class="mb-modal-box mb-modal-animate">
                <div class="mb-modal-glow"></div>

                <div class="mb-modal-header">
                    <div>
                        <div class="mb-modal-badge">
                            <i class="bi bi-plus-circle-fill"></i>
                            Create Utility
                        </div>
                        <h4>Add New Utility</h4>
                        <small>Create a new utility service for your property</small>
                    </div>

                    <button type="button"
                            class="mb-icon-btn"
                            onclick="closeModal('addModal')">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>

                <form action="${pageContext.request.contextPath}/manager/utilities" method="POST" class="mb-form-grid">
                    <input type="hidden" name="action" value="add"/>

                    <div class="mb-form-group">
                        <label>Utility Name</label>
                        <div class="mb-input-wrap">
                            <i class="bi bi-lightning"></i>
                            <input type="text"
                                   name="utilityName"
                                   placeholder="e.g., Parking Service"
                                   required>
                        </div>
                    </div>

                    <div class="mb-form-group">
                        <label>Price (VND)</label>
                        <div class="mb-input-wrap">
                            <i class="bi bi-currency-dollar"></i>
                            <input type="number"
                                   name="price"
                                   value="0"
                                   min="0"
                                   required>
                        </div>
                    </div>

                    <div class="mb-form-group">
                        <label>Unit</label>
                        <div class="mb-input-wrap">
                            <i class="bi bi-calendar3"></i>
                            <input type="text"
                                   name="unit"
                                   placeholder="month"
                                   required>
                        </div>
                    </div>

                    <div class="mb-modal-actions">
                        <button type="button"
                                class="mb-secondary-btn"
                                onclick="closeModal('addModal')">
                            <i class="bi bi-x-circle"></i>
                            Cancel
                        </button>

                        <button type="submit" class="mb-success-btn">
                            <i class="bi bi-plus-circle-fill"></i>
                            Add Utility
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- EDIT MODAL -->
        <div id="editModal" class="mb-modal-overlay" onclick="handleOverlayClick(event, 'editModal')">
            <div class="mb-modal-box mb-modal-animate">
                <div class="mb-modal-glow edit-glow"></div>

                <div class="mb-modal-header">
                    <div>
                        <div class="mb-modal-badge">
                            <i class="bi bi-pencil-square"></i>
                            Update Utility
                        </div>
                        <h4>
                            Edit Utility
                            <c:if test="${editUtility != null}">
                                - ${editUtility.utilityName}
                            </c:if>
                        </h4>
                        <small>Update the price for this utility service</small>
                    </div>

                    <button type="button"
                            class="mb-icon-btn"
                            onclick="closeModal('editModal')">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>

                <form action="${pageContext.request.contextPath}/manager/utilities" method="POST" class="mb-form-grid">
                    <input type="hidden" name="action" value="edit"/>
                    <input type="hidden" name="id" value="${editUtility.utilityId}"/>

                    <div class="mb-form-group">
                        <label>Price (VND)</label>
                        <div class="mb-input-wrap">
                            <i class="bi bi-cash-stack"></i>
                            <input type="number"
                                   name="price"
                                   value="${editUtility.standardPrice}"
                                   min="0"
                                   required>
                        </div>
                    </div>

                    <div class="mb-modal-actions">
                        <button type="button"
                                class="mb-secondary-btn"
                                onclick="closeModal('editModal')">
                            <i class="bi bi-x-circle"></i>
                            Cancel
                        </button>

                        <button type="submit" class="mb-success-btn">
                            <i class="bi bi-check2-circle"></i>
                            Save Changes
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- AUTO OPEN EDIT MODAL -->
        <c:if test="${editUtility != null}">
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    openModal('editModal');
                });
            </script>
        </c:if>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/pages/managerUtilities.js"></script>
</layout:layout>