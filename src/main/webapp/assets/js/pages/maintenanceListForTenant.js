(function () {
  const actionButtons = document.querySelectorAll(".mlt-action-btn");
  const pagerLinks = document.querySelectorAll(".mlt-pagination a");

  actionButtons.forEach(function (btn) {
    btn.addEventListener("click", function () {
      btn.classList.add("is-loading");
    });
  });

  pagerLinks.forEach(function (link) {
    link.addEventListener("click", function () {
      const li = link.closest("li");
      if (li && li.classList.contains("disabled")) {
        return;
      }
    });
  });
})();
