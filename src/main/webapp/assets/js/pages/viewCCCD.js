document.addEventListener("DOMContentLoaded", function () {
  const modalElement = document.getElementById("tcdImageModal");
  if (!modalElement || typeof bootstrap === "undefined") {
    return;
  }

  const imageModal = new bootstrap.Modal(modalElement);
  const previewImage = document.getElementById("tcdPreviewImage");
  const previewFallback = document.getElementById("tcdPreviewFallback");
  const imageLoading = document.getElementById("tcdImageLoading");
  const modalTitle = document.getElementById("tcdImageModalLabel");
  const openInNewTabLink = document.getElementById("tcdOpenImageNewTab");

  const triggerButtons = document.querySelectorAll(".js-image-popup");

  function resetModalState() {
    previewImage.classList.add("d-none");
    previewFallback.classList.add("d-none");
    imageLoading.classList.remove("d-none");

    previewImage.removeAttribute("src");
    previewImage.setAttribute("alt", "Preview image");

    openInNewTabLink.setAttribute("href", "#");
    modalTitle.textContent = "Image Preview";
  }

  function showImage(imageUrl, imageTitle) {
    resetModalState();

    if (imageTitle && imageTitle.trim() !== "") {
      modalTitle.textContent = imageTitle;
      previewImage.alt = imageTitle;
    }

    openInNewTabLink.href = imageUrl;

    const tempImage = new Image();

    tempImage.onload = function () {
      previewImage.src = imageUrl;
      imageLoading.classList.add("d-none");
      previewFallback.classList.add("d-none");
      previewImage.classList.remove("d-none");
    };

    tempImage.onerror = function () {
      imageLoading.classList.add("d-none");
      previewImage.classList.add("d-none");
      previewFallback.classList.remove("d-none");
    };

    tempImage.src = imageUrl;
    imageModal.show();
  }

  triggerButtons.forEach(function (button) {
    button.addEventListener("click", function () {
      const imageUrl = button.getAttribute("data-image-url");
      const imageTitle = button.getAttribute("data-image-title");

      if (!imageUrl) {
        return;
      }

      showImage(imageUrl, imageTitle);
    });
  });

  modalElement.addEventListener("hidden.bs.modal", function () {
    previewImage.removeAttribute("src");
    previewImage.classList.add("d-none");
    previewFallback.classList.add("d-none");
    imageLoading.classList.add("d-none");
  });
});
