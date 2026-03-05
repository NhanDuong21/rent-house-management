<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="t" tagdir="/WEB-INF/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="ctx" value="${pageContext.request.contextPath}" />

<t:layout title="Edit Room"
          active="a_rooms"
          cssFile="${ctx}/assets/css/views/admin/editRoom.css">

    <c:set var="r" value="${room}" />
    <c:set var="isInactive" value="${r.status == 'INACTIVE'}" />

    <div class="er-container" style="max-width: 1100px; margin: 0 auto;">

        <!-- HEADER -->
        <div class="er-header">
            <div>
                <h2>
                    Room: <b>${r.roomNumber}</b>
                    <c:if test="${isInactive}">
                        <span class="er-badge cover" style="margin-left:10px;background:#6b7280;">
                            <i class="bi bi-slash-circle-fill"></i> INACTIVE
                        </span>
                    </c:if>
                </h2>
                <p>
                    <c:choose>
                        <c:when test="${isInactive}">
                            Room is archived. Restore it to enable editing and image management.
                        </c:when>
                        <c:otherwise>
                            Update room information and manage room images
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>

            <div style="display:flex; gap:10px; align-items:center; flex-wrap:wrap;">
                <!-- INACTIVE -> show RESTORE instead of Edit/Save mindset -->
                <c:if test="${isInactive}">
                    <button type="button"
                            class="er-btn success js-restore-room"
                            data-restore-url="${ctx}/admin/rooms/restore?id=${r.roomId}"
                            data-room-name="${r.roomNumber}">
                        <i class="bi bi-arrow-counterclockwise"></i> Restore
                    </button>
                </c:if>

                <a class="er-btn" href="${ctx}/admin/rooms">
                    <i class="bi bi-arrow-left"></i> Back
                </a>
            </div>
        </div>

        <!-- LOCKED ALERT -->
        <c:if test="${isInactive}">
            <div class="er-alert er-alert-danger">
                <span class="er-alert-ico">
                    <i class="bi bi-exclamation-triangle-fill"></i>
                </span>
                <div>
                    <div class="er-alert-title">Locked</div>
                    <div class="er-alert-text">
                        Room is <b>INACTIVE</b> (archived/sold). You cannot edit this room or manage its images.
                        Use <b>Restore</b> to reopen it.
                    </div>
                </div>
            </div>
        </c:if>

        <!-- ALERTS -->
        <c:if test="${not empty param.msg || not empty param.err}">
            <c:choose>
                <c:when test="${not empty param.msg}">
                    <div class="er-alert er-alert-success">
                        <span class="er-alert-ico">
                            <i class="bi bi-check-circle-fill"></i>
                        </span>
                        <div>
                            <div class="er-alert-title">Success</div>
                            <div class="er-alert-text">${param.msg}</div>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="er-alert er-alert-danger">
                        <span class="er-alert-ico">
                            <i class="bi bi-exclamation-triangle-fill"></i>
                        </span>
                        <div>
                            <div class="er-alert-title">Error</div>
                            <div class="er-alert-text">${param.err}</div>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <!-- ===== ROOM FORM ===== -->
        <form method="post"
              action="${ctx}/admin/rooms/edit"
              class="er-card er-form"
              <c:if test="${isInactive}">onsubmit="return false;"</c:if>>

                  <div class="er-card-title">Room Information</div>

                  <input type="hidden" name="roomId" value="${r.roomId}"/>

              <div class="row g-3">
                  <!-- BLOCK SELECT -->
                  <div class="col-md-3">
                      <label class="form-label">Block</label>
                      <select class="form-select" name="blockId" required ${isInactive ? 'disabled' : ''}>
                          <c:forEach items="${blocks}" var="b">
                              <option value="${b.blockId}" ${b.blockId == r.blockId ? 'selected' : ''}>
                                  ${b.blockName}
                              </option>
                          </c:forEach>
                      </select>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">Room Number</label>
                      <input class="form-control"
                             type="text"
                             name="roomNumber"
                             value="${r.roomNumber}"
                             required
                             ${isInactive ? 'readonly' : ''}/>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">Status</label>
                      <select class="form-select" name="status" required ${isInactive ? 'disabled' : ''}>
                          <option value="AVAILABLE"    ${r.status=='AVAILABLE'?'selected':''}>AVAILABLE</option>
                          <option value="OCCUPIED"     ${r.status=='OCCUPIED'?'selected':''}>OCCUPIED</option>
                          <option value="MAINTENANCE"  ${r.status=='MAINTENANCE'?'selected':''}>MAINTENANCE</option>
                          <option value="INACTIVE"     ${r.status=='INACTIVE'?'selected':''}>INACTIVE</option>
                      </select>

                      <c:if test="${!isInactive}">
                          <div class="text-muted" style="font-size:12px;margin-top:6px;font-weight:600;">
                              Tip: Use INACTIVE only when the room is permanently archived/sold.
                          </div>
                      </c:if>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">Price</label>
                      <input class="form-control"
                             type="number"
                             name="price"
                             value="${r.price}"
                             min="0"
                             step="1"
                             required
                             ${isInactive ? 'readonly' : ''}/>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">Area (m²)</label>
                      <input class="form-control"
                             type="number"
                             name="area"
                             value="${r.area}"
                             min="0"
                             step="0.01"
                             ${isInactive ? 'readonly' : ''}/>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">Floor</label>
                      <input class="form-control"
                             type="number"
                             name="floor"
                             value="${r.floor}"
                             min="0"
                             ${isInactive ? 'readonly' : ''}/>
                  </div>

                  <div class="col-md-3">
                      <label class="form-label">Max tenants</label>
                      <input class="form-control"
                             type="number"
                             name="maxTenants"
                             value="${r.maxTenants}"
                             min="1"
                             ${isInactive ? 'readonly' : ''}/>
                  </div>

                  <div class="col-md-3 d-flex align-items-end gap-3">
                      <div class="form-check">
                          <input class="form-check-input"
                                 type="checkbox"
                                 name="airConditioning"
                                 ${r.airConditioning ? 'checked' : ''}
                                 ${isInactive ? 'disabled' : ''}/>
                          <label class="form-check-label">Has AC</label>
                      </div>

                      <div class="form-check">
                          <input class="form-check-input"
                                 type="checkbox"
                                 name="isMezzanine"
                                 ${r.mezzanine ? 'checked' : ''}
                                 ${isInactive ? 'disabled' : ''}/>
                          <label class="form-check-label">Mezzanine</label>
                      </div>
                  </div>

                  <div class="col-12">
                      <label class="form-label">Description</label>
                      <textarea class="form-control"
                                name="description"
                                rows="3"
                                ${isInactive ? 'readonly' : ''}>${r.description}</textarea>
                  </div>

                  <div class="col-12">
                      <div class="er-actions">
                          <!-- Save hidden/disabled when INACTIVE -->
                          <c:if test="${!isInactive}">
                              <button class="er-btn primary" type="submit">
                                  <i class="bi bi-check2-circle"></i> Save
                              </button>
                          </c:if>

                          <a class="er-btn" href="${ctx}/admin/rooms">
                              <i class="bi bi-arrow-left"></i> Back
                          </a>
                      </div>
                  </div>
              </div>
        </form>

        <div style="height: 14px;"></div>

        <!-- ===== IMAGES ===== -->
        <div class="er-card">
            <div class="er-card-title">Room Images</div>

            <!-- upload (locked if inactive) -->
            <c:if test="${!isInactive}">
                <form method="post"
                      action="${ctx}/admin/rooms/images/upload"
                      enctype="multipart/form-data"
                      class="er-upload">
                    <input type="hidden" name="roomId" value="${r.roomId}"/>
                    <input class="form-control" type="file" name="image" accept="image/*" required/>
                    <button class="er-btn success" type="submit">
                        <i class="bi bi-upload"></i> Upload
                    </button>
                </form>
            </c:if>

            <c:if test="${isInactive}">
                <div class="text-muted" style="font-weight:600;margin-bottom:14px;">
                    Images are locked because this room is INACTIVE.
                </div>
            </c:if>

            <c:if test="${empty images}">
                <div class="text-muted" style="font-weight:600;">No images yet.</div>
            </c:if>

            <c:if test="${not empty images}">
                <div class="er-img-grid">
                    <c:forEach items="${images}" var="img">
                        <div class="er-img-card">
                            <img class="er-img js-room-img"
                                 data-src="${ctx}/assets/images/rooms/${img.imageUrl}"
                                 src="${ctx}/assets/images/rooms/${img.imageUrl}"
                                 alt="Room image"/>

                            <div class="er-img-meta">
                                <div>
                                    <c:if test="${img.cover}">
                                        <span class="er-badge cover">
                                            <i class="bi bi-star-fill"></i> COVER
                                        </span>
                                    </c:if>
                                </div>

                                <div class="er-actions">
                                    <!-- actions locked if inactive -->
                                    <c:if test="${!isInactive}">
                                        <c:if test="${!img.cover}">
                                            <form method="post" action="${ctx}/admin/rooms/images/cover" style="display:inline;">
                                                <input type="hidden" name="roomId" value="${r.roomId}"/>
                                                <input type="hidden" name="imageId" value="${img.imageId}"/>
                                                <button class="er-btn" type="submit" style="padding:6px 12px;font-size:13px;">
                                                    <i class="bi bi-image"></i> Set cover
                                                </button>
                                            </form>
                                        </c:if>

                                        <form method="post"
                                              action="${ctx}/admin/rooms/images/delete"
                                              style="display:inline;"
                                              class="js-del-img-form"
                                              data-img="${img.imageUrl}">
                                            <input type="hidden" name="imageId" value="${img.imageId}"/>
                                            <input type="hidden" name="filename" value="${img.imageUrl}"/>
                                            <button class="er-btn danger" type="submit" style="padding:6px 12px;font-size:13px;">
                                                <i class="bi bi-trash3"></i> Delete
                                            </button>
                                        </form>
                                    </c:if>

                                    <c:if test="${isInactive}">
                                        <button class="er-btn" type="button" disabled style="padding:6px 12px;font-size:13px;opacity:.7;cursor:not-allowed;">
                                            <i class="bi bi-lock-fill"></i> Locked
                                        </button>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
        </div>

        <!-- ===== LIGHTBOX ===== -->
        <div class="er-lightbox" id="erLightbox" aria-hidden="true">
            <div class="er-backdrop" data-close="1"></div>

            <div class="er-lb-box" role="dialog" aria-modal="true">
                <div class="er-lb-top">
                    <div>
                        <i class="bi bi-image"></i> Preview
                        <span style="opacity:.75;margin-left:10px;" id="erLbCount">1/1</span>
                    </div>

                    <button type="button" class="er-lb-close" data-close="1" title="Close">
                        <i class="bi bi-x-lg"></i>
                    </button>
                </div>

                <div class="er-lb-body">
                    <img class="er-lb-img" id="erLbImg" alt="Preview"/>
                    <button type="button" class="er-lb-nav prev" title="Prev">
                        <i class="bi bi-chevron-left"></i>
                    </button>
                    <button type="button" class="er-lb-nav next" title="Next">
                        <i class="bi bi-chevron-right"></i>
                    </button>
                </div>
            </div>
        </div>

        <!-- ===== CONFIRM DELETE IMAGE ===== -->
        <div class="er-confirm" id="erConfirm" aria-hidden="true">
            <div class="er-backdrop" data-close="1"></div>

            <div class="er-confirm-box" role="dialog" aria-modal="true">
                <div class="er-confirm-title">
                    <i class="bi bi-exclamation-triangle-fill" style="color:#ef4444;margin-right:8px;"></i>
                    Confirm delete
                </div>
                <div class="er-confirm-sub" id="erConfirmText">Delete this image?</div>

                <div class="er-confirm-actions">
                    <button type="button" class="er-btn" data-close="1" style="padding:8px 12px;">
                        <i class="bi bi-x"></i> Cancel
                    </button>
                    <button type="button" class="er-btn danger" id="erConfirmOk" style="padding:8px 12px;">
                        <i class="bi bi-trash3-fill"></i> Delete
                    </button>
                </div>
            </div>
        </div>

        <!-- ===== CONFIRM RESTORE ROOM ===== -->
        <div class="er-confirm" id="erRestoreConfirm" aria-hidden="true">
            <div class="er-backdrop" data-close="1"></div>

            <div class="er-confirm-box" role="dialog" aria-modal="true">
                <div class="er-confirm-title">
                    <i class="bi bi-arrow-counterclockwise" style="color:#16a34a;margin-right:8px;"></i>
                    Confirm restore
                </div>
                <div class="er-confirm-sub" id="erRestoreText">Restore this room?</div>

                <div class="er-confirm-actions">
                    <button type="button" class="er-btn" data-close="1" style="padding:8px 12px;">
                        <i class="bi bi-x"></i> Cancel
                    </button>
                    <a href="#" class="er-btn success" id="erRestoreGo" style="padding:8px 12px;">
                        <i class="bi bi-arrow-counterclockwise"></i> Restore
                    </a>
                </div>
            </div>
        </div>

    </div>

    <script src="${ctx}/assets/js/pages/admin/editRoom.js"></script>
</t:layout>