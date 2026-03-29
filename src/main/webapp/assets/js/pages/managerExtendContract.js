(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("extendContractForm");
    const currentEndInput = document.getElementById("currentEndDate");
    const newEndInput = document.getElementById("newEndDate");
    const submitBtn = document.getElementById("submitBtn");
    const errorBox = document.getElementById("newEndDateError");

    const summary = document.getElementById("extendSummary");
    const summaryCurrentEnd = document.getElementById("summaryCurrentEnd");
    const summaryExpectedEnd = document.getElementById("summaryExpectedEnd");
    const summarySelectedEnd = document.getElementById("summarySelectedEnd");
    const summaryStatus = document.getElementById("summaryStatus");

    if (!form || !currentEndInput || !newEndInput || !submitBtn) return;

    const currentEndValue = currentEndInput.value;
    if (!currentEndValue) return;

    const currentEndDate = parseLocalDate(currentEndValue);
    const expectedNewEndDate = addOneYear(currentEndDate);
    const expectedNewEndValue = formatDate(expectedNewEndDate);

    // Auto fill luôn ngày mới = current end date + 1 year
    newEndInput.value = expectedNewEndValue;

    // Chỉ cho chọn đúng ngày sau 1 năm
    // Giữ editable để UX tự nhiên hơn, nhưng vẫn validate JS
    newEndInput.min = expectedNewEndValue;
    newEndInput.max = expectedNewEndValue;

    // Render summary
    summaryCurrentEnd.textContent = formatDisplayDate(currentEndDate);
    summaryExpectedEnd.textContent = formatDisplayDate(expectedNewEndDate);
    summarySelectedEnd.textContent = formatDisplayDate(expectedNewEndDate);
    summaryStatus.textContent = "Valid";
    summary.classList.add("is-visible");

    validateNewEndDate();

    newEndInput.addEventListener("input", function () {
      validateNewEndDate();
      updateSummary();
    });

    newEndInput.addEventListener("change", function () {
      validateNewEndDate();
      updateSummary();
    });

    form.addEventListener("submit", function (e) {
      const isValid = validateNewEndDate();

      if (!isValid) {
        e.preventDefault();
        newEndInput.focus();
        return;
      }

      const message =
        submitBtn.getAttribute("data-confirm") ||
        "Extend this contract by updating its end date?";

      if (!window.confirm(message)) {
        e.preventDefault();
        return;
      }

      setSubmittingState(true);
    });

    function validateNewEndDate() {
      const selectedValue = newEndInput.value;

      if (!selectedValue) {
        setInvalid("Please select the new end date.");
        return false;
      }

      if (selectedValue !== expectedNewEndValue) {
        setInvalid(
          "New end date must be exactly 1 year after the current end date (" +
            expectedNewEndValue +
            ").",
        );
        return false;
      }

      clearInvalid();
      return true;
    }

    function setInvalid(message) {
      newEndInput.classList.add("is-invalid");
      newEndInput.setCustomValidity(message);
      if (errorBox) errorBox.textContent = message;
      if (summaryStatus) summaryStatus.textContent = "Invalid";
    }

    function clearInvalid() {
      newEndInput.classList.remove("is-invalid");
      newEndInput.setCustomValidity("");
      if (errorBox) errorBox.textContent = "";
      if (summaryStatus) summaryStatus.textContent = "Valid";
    }

    function updateSummary() {
      const selectedValue = newEndInput.value;
      if (summarySelectedEnd) {
        summarySelectedEnd.textContent = selectedValue
          ? formatDisplayDate(parseLocalDate(selectedValue))
          : "-";
      }
    }

    function setSubmittingState(isSubmitting) {
      if (!isSubmitting) return;

      submitBtn.disabled = true;
      submitBtn.classList.add("is-submitting");

      const textEl = submitBtn.querySelector(".me-btn-text");
      if (textEl) {
        textEl.innerHTML =
          '<span class="me-btn-spinner" aria-hidden="true"></span> Extending...';
      }
    }

    function parseLocalDate(yyyyMMdd) {
      const parts = yyyyMMdd.split("-");
      return new Date(Number(parts[0]), Number(parts[1]) - 1, Number(parts[2]));
    }

    function addOneYear(date) {
      const result = new Date(date.getTime());
      const originalDay = result.getDate();

      result.setFullYear(result.getFullYear() + 1);

      // Fix edge case 29/02
      if (result.getDate() !== originalDay) {
        result.setDate(0);
      }

      return result;
    }

    function formatDate(date) {
      const y = date.getFullYear();
      const m = String(date.getMonth() + 1).padStart(2, "0");
      const d = String(date.getDate()).padStart(2, "0");
      return y + "-" + m + "-" + d;
    }

    function formatDisplayDate(date) {
      const d = String(date.getDate()).padStart(2, "0");
      const m = String(date.getMonth() + 1).padStart(2, "0");
      const y = date.getFullYear();
      return d + "/" + m + "/" + y;
    }
  });
})();
