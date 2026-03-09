
document.addEventListener("DOMContentLoaded", function () {
    const input = document.querySelector(".searchBill");
    const statusSelected = document.getElementById("status");
    const dateSelected = document.querySelector(".bill-date");
    const rows = document.querySelectorAll("#billTable tr:not(#notFoundBill)");
    const notFound = document.getElementById("notFoundBill");
    if (!input || !statusSelected||!dateSelected)
        return;
    function filterBills() {
        const searchKeyword = input.value.toLowerCase();
        const selectedStatus = statusSelected.value;
        const selectedDate  = dateSelected.value;
        let countBill = 0;

        rows.forEach(row => {
            const billIdText = row.querySelector(".billId");
            const roomNumberText = row.querySelector(".roomNumber");
            const statusText = row.querySelector(".mb-badge");
            const dateText = row.querySelector(".dateBill");
            if (!roomNumberText || !billIdText || !statusText|| !dateText)
                return;

            const billId = billIdText.textContent.toLowerCase();
            const roomNumber = roomNumberText.textContent.toLowerCase();
            const billStatus = statusText.textContent.trim();
            const dateBill = dateText.textContent.trim();
            //Search condition
            const matchSearch = roomNumber.includes(searchKeyword) || billId.includes(searchKeyword);

            //status condition(all or PAID/UNPAID)
            const matchStatus = selectedStatus === "" || billStatus === selectedStatus;

            //search date
            let matchDate = true;
            if(selectedDate !== ""){
                const[year, month] = selectedDate.split("-");
                
                const parts = dateBill.split("/");
                const monthBill = parts[1];
                const yearBill = parts[2];

                matchDate = (monthBill === month && yearBill === year);
            }
           
            
            if (matchSearch && matchStatus && matchDate) {
                row.style.display = "";
                countBill++;
            } else {
                row.style.display = "none";
            }
        });
        notFound.style.display = countBill === 0 ? "" : "none";
    }

    //search
    input.addEventListener("keyup", filterBills);
    //filter status
    statusSelected.addEventListener("change", filterBills);
    //filter date
    dateSelected.addEventListener("change", filterBills);
    
    

});


 const dateInput = document.querySelector(".bill-date");
 const wrapper = document.querySelector(".date-btn");
dateInput.addEventListener("change", function() {
    if(this.value) {
        this.classList.add("has-value");
        wrapper.style.width = "auto";
        wrapper.style.padding ="0 8px";
    }
});