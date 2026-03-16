document.addEventListener("DOMContentLoaded", function () {

    const roomSelect    = document.getElementById("roomSelect");
    const tenantInput   = document.getElementById("tenantName");
    const oldElectric   = document.getElementById("oldElectric");
    const newElectric   = document.getElementById("newElectric");
    const electricUsage = document.getElementById("electricUsage");
    const oldWater      = document.getElementById("oldWater");
    const newWater      = document.getElementById("newWater");
    const waterUsage    = document.getElementById("waterUsage");

    //  Tính usage
    function calcElectric() {
        electricUsage.value = (parseFloat(newElectric.value) || 0)
                            - (parseFloat(oldElectric.value) || 0);
        const usage  = newVal - oldVal;
        electricUsage.value = newElectric.value !== "" ? Math.max(0, usage) : "";
    }

    function calcWater() {
        waterUsage.value = (parseFloat(newWater.value) || 0)
                         - (parseFloat(oldWater.value) || 0);
        const usage  = newVal - oldVal;
        waterUsage.value = newWater.value !== "" ? Math.max(0, usage) : "";
    }

    oldElectric.addEventListener("input", calcElectric);
    newElectric.addEventListener("input", calcElectric);
    oldWater.addEventListener("input", calcWater);
    newWater.addEventListener("input", calcWater);

    //  Chọn phòng → load tenant + chỉ số cũ 
    function onRoomChange() {
        const selected = roomSelect.options[roomSelect.selectedIndex];

        tenantInput.value = selected.dataset.tenant || "";
        oldElectric.value = selected.dataset.oldElectric || 0;
        oldWater.value    = selected.dataset.oldWater    || 0;

        // reset new & usage
        newElectric.value   = "";
        newWater.value      = "";
        electricUsage.value = "";
        waterUsage.value    = "";
    }

    if (roomSelect) {
        roomSelect.addEventListener("change", onRoomChange);
    }
});