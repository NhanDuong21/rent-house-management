(function () {

  const input = document.getElementById("mlSearch");
  const status = document.getElementById("mlStatus"); // NEW

  let timer = null;

  // SEARCH realtime
  if (input) {
    input.addEventListener("input", function () {

      if (timer) clearTimeout(timer);

      timer = setTimeout(() => {
        input.closest("form").submit();
      }, 400);

    });
  }

  // NEW: FILTER when change status
  if (status) {
    status.addEventListener("change", function () {
      status.closest("form").submit();
    });
  }

})();