(function () {
  // ===== Lightbox for room images =====
  const imgs = Array.from(document.querySelectorAll(".js-room-img"));
  if (imgs.length) {
    const lb = document.getElementById("erLightbox");
    const lbImg = document.getElementById("erLbImg");
    const lbCount = document.getElementById("erLbCount");

    let index = 0;
    const sources = imgs
      .map((im) => im.getAttribute("data-src"))
      .filter(Boolean);

    function setCount() {
      if (lbCount) lbCount.textContent = `${index + 1}/${sources.length}`;
    }

    function setImage(i) {
      if (!sources.length) return;
      index = (i + sources.length) % sources.length;
      lbImg.src = sources[index];
      setCount();
    }

    function open(i) {
      lb.classList.add("is-open");
      lb.setAttribute("aria-hidden", "false");
      document.body.style.overflow = "hidden";
      setImage(i);
    }

    function close() {
      lb.classList.remove("is-open");
      lb.setAttribute("aria-hidden", "true");
      document.body.style.overflow = "";
      lbImg.src = "";
    }

    function next() {
      setImage(index + 1);
    }
    function prev() {
      setImage(index - 1);
    }

    imgs.forEach((im, i) => im.addEventListener("click", () => open(i)));

    const prevBtn = lb.querySelector(".er-lb-nav.prev");
    const nextBtn = lb.querySelector(".er-lb-nav.next");
    const closeBtn = lb.querySelector(".er-lb-close");

    if (prevBtn)
      prevBtn.addEventListener("click", (e) => (e.stopPropagation(), prev()));
    if (nextBtn)
      nextBtn.addEventListener("click", (e) => (e.stopPropagation(), next()));
    if (closeBtn)
      closeBtn.addEventListener("click", (e) => (e.stopPropagation(), close()));

    lb.addEventListener("click", (e) => {
      if (e.target && e.target.getAttribute("data-close") === "1") close();
    });

    document.addEventListener("keydown", (e) => {
      if (!lb.classList.contains("is-open")) return;
      if (e.key === "Escape") close();
      if (e.key === "ArrowRight") next();
      if (e.key === "ArrowLeft") prev();
    });
  }

  // ===== Delete Image Confirm (custom modal) =====
  const confirmModal = document.getElementById("erConfirm");
  const confirmText = document.getElementById("erConfirmText");
  const confirmOk = document.getElementById("erConfirmOk");

  let pendingForm = null;

  function openConfirm(filename) {
    if (!confirmModal) return;
    confirmModal.classList.add("is-open");
    confirmModal.setAttribute("aria-hidden", "false");
    document.body.style.overflow = "hidden";

    if (confirmText) {
      confirmText.textContent =
        'Delete image "' +
        (filename || "") +
        '"? This action cannot be undone.';
    }
  }

  function closeConfirm() {
    if (!confirmModal) return;
    confirmModal.classList.remove("is-open");
    confirmModal.setAttribute("aria-hidden", "true");
    document.body.style.overflow = "";
    pendingForm = null;
  }

  document.querySelectorAll(".js-del-img-form").forEach((form) => {
    form.addEventListener("submit", (e) => {
      e.preventDefault();
      pendingForm = form;
      openConfirm(form.getAttribute("data-img"));
    });
  });

  if (confirmOk) {
    confirmOk.addEventListener("click", () => {
      if (pendingForm) pendingForm.submit();
    });
  }

  if (confirmModal) {
    confirmModal.addEventListener("click", (e) => {
      if (e.target && e.target.getAttribute("data-close") === "1")
        closeConfirm();
    });
  }

  document.addEventListener("keydown", (e) => {
    if (!confirmModal || !confirmModal.classList.contains("is-open")) return;
    if (e.key === "Escape") closeConfirm();
  });
})();
