document.addEventListener("DOMContentLoaded", function () {
  const imagePreviewModal = document.getElementById("imagePreviewModal");
  const previewImg = document.getElementById("imagePreviewTag");
  const previewTitle = document.getElementById("imagePreviewTitle");

  if (!imagePreviewModal || !previewImg || !previewTitle) {
    return;
  }

  imagePreviewModal.addEventListener("show.bs.modal", function (event) {
    const triggerButton = event.relatedTarget;
    if (!triggerButton) return;

    const imgSrc = triggerButton.getAttribute("data-img-src");
    const imgTitle = triggerButton.getAttribute("data-img-title");

    previewImg.src = imgSrc || "";
    previewImg.alt = imgTitle || "Image Preview";
    previewTitle.textContent = imgTitle || "Image Preview";
  });

  imagePreviewModal.addEventListener("hidden.bs.modal", function () {
    previewImg.src = "";
    previewImg.alt = "Preview";
    previewTitle.textContent = "Image Preview";
  });
});
