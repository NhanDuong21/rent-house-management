(function () {
  const input = document.getElementById("mlSearch");

  if (!input) return;

  let timer = null;

  input.addEventListener("input", function () {
    if (timer) clearTimeout(timer);

    timer = setTimeout(() => {
      input.closest("form").submit();
    }, 400);
  });
})();
