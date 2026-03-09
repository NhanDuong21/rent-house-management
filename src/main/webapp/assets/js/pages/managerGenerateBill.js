document.addEventListener("DOMContentLoaded", function () {

    const roomSelect = document.getElementById("roomSelect");
    const tenantInput = document.getElementById("tenantName");
    // load tenant name when select
    function loadTenantByRoom() {
        const selectedOption = roomSelect.options[roomSelect.selectedIndex];
        const tenant = selectedOption.dataset.tenant || "";
        tenantInput.value = tenant;

    }
    // events
    if (roomSelect) {
        roomSelect.addEventListener("change", loadTenantByRoom);
    }
});

document.addEventListener("DOMContentLoaded", function () {

    const oldElectric = document.getElementById("oldElectric");
    const newElectric = document.getElementById("newElectric");
    const electricUsage = document.getElementById("electricUsage");

    const oldWater = document.getElementById("oldWater");
    const newWater = document.getElementById("newWater");
    const waterUsage = document.getElementById("waterUsage");

    function calcElectric() {
        let oldVal = parseFloat(oldElectric.value) || 0;
        let newVal = parseFloat(newElectric.value) || 0;
        electricUsage.value = newVal - oldVal;
    }

    function calcWater() {
        let oldVal = parseFloat(oldWater.value) || 0;
        let newVal = parseFloat(newWater.value) || 0;
        waterUsage.value = newVal - oldVal;
    }

    oldElectric.addEventListener("input", calcElectric);
    newElectric.addEventListener("input", calcElectric);

    oldWater.addEventListener("input", calcWater);
    newWater.addEventListener("input", calcWater);

});
