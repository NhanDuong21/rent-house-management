document.addEventListener("DOMContentLoaded", function () {
  const input = document.getElementById("subSearchInput");
  const tableBody = document.getElementById("subscriberTable");
  const notFound = document.getElementById("notFoundSub");

  function getRows() {
    return tableBody ? tableBody.querySelectorAll("tr:not(#notFoundSub)") : [];
  }

  function filterSubscribers() {
    if (!input) return;

    const keyword = input.value.trim().toLowerCase();
    const rows = getRows();
    let hasResult = false;

    rows.forEach((row) => {
      const text = row.innerText.toLowerCase();
      const matched = text.includes(keyword);

      row.style.display = matched ? "" : "none";

      if (matched) {
        hasResult = true;
      }
    });

    if (notFound) {
      notFound.style.display = hasResult ? "none" : "";
    }
  }

  if (input) {
    input.addEventListener("input", filterSubscribers);
  }

  // focus animation feeling
  if (input) {
    input.addEventListener("focus", function () {
      document.body.classList.add("searching-subscribers");
    });

    input.addEventListener("blur", function () {
      document.body.classList.remove("searching-subscribers");
    });
  }

  // animate visible rows slightly on load
  const rows = getRows();
  rows.forEach((row, index) => {
    row.style.setProperty("--delay", index);
  });
});
