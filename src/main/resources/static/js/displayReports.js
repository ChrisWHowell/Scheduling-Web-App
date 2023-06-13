async function initPage() {
    document.querySelectorAll('input[name="tab"]').forEach((tab) => {
        tab.addEventListener('change', () => {
            if (tab.checked) {
                console.log(tab.id.replace('tab_', ''));
                showTabContent(tab.id.replace('tab_', ''));
            }
        });
    });
    populateContacts();
    displayMonthlyTotalReport();
    displayTypeTotalReport();
    displayContactSchedules();
    document.getElementById('tab_MonthlyTotal').addEventListener('click', () => showTabContent('monthlyTotal'));
    document.getElementById('tab_TypeTotals').addEventListener('click', () => showTabContent('typeTotal'));
    document.getElementById('tab_ContactSchedules').addEventListener('click', () => showTabContent('contactSchedules'));
    document.getElementById('tab_loginStats').addEventListener('click', () => showTabContent('loginStats'));
    showTabContent('monthlyTotal');
    document.getElementById('cb_contacts').addEventListener('change', displayContactSchedules);
    document.getElementById('btn_reanalyse').addEventListener('click', async function() {
        const startDate = document.getElementById('dp_startDate').value;
        const endDate = document.getElementById('dp_endDate').value;

        const loginStats = await fetchLoginStats(startDate, endDate);
        updateLoginStatsTable(loginStats);
    });
    document.getElementById('btn_GoToCustomers').addEventListener('click', function () {
        window.location.href = '/displayCustomers'; });
    document.getElementById('btn_GoToAppointments').addEventListener('click', function () {
        window.location.href = '/appointmentsView'; });
}
async function populateContacts() {
  const response = await fetch("/api/getAllContactsforReport");
  const contacts = await response.json();
    const contactSelect = document.getElementById("cb_contacts");
    contacts.forEach(contact => {
      const option = document.createElement("option");
      option.text = contact;
      option.value = contact;
      contactSelect.add(option);
    });
}
function showTabContent(tabId) {
    // Get all tab content elements and hide them
    const tabContents = document.getElementsByClassName('tab-content');
    for (let i = 0; i < tabContents.length; i++) {
      tabContents[i].style.display = 'none';
    }

    // Show the selected tab content
    const targetTabContent = document.getElementById(tabId);
    if (targetTabContent) {
        targetTabContent.style.display = 'block';
    } else {
        console.error('Element with id', tabId, 'not found.');
    }
}

async function displayMonthlyTotalReport() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;
    const response = await fetch('/api/getTotalsByMonth', {
                                       method: 'GET', // Specify the method
                                       headers: {
                                           'Accept': 'application/json',
                                           'Content-Type': 'application/json',
                                           [csrfHeaderName]: csrfToken,
                                       },
                                   });
    const monthNTotals = await response.json();
    const tableBody = document.getElementById('monthTotalBody');
    tableBody.innerHTML = '';
    for(const monthNTotal of monthNTotals) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${monthNTotal.monthName}</td>
            <td>${monthNTotal.appointmentCount}</td>
        `;
        tableBody.appendChild(row);
    }
}
async function displayTypeTotalReport() {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;
    const response = await fetch('/api/getTotalsByType', {
                                       method: 'GET', // Specify the method
                                       headers: {
                                           'Accept': 'application/json',
                                           'Content-Type': 'application/json',
                                           [csrfHeaderName]: csrfToken,
                                       },
                                   });
    const typeNTotals = await response.json();
    const tableBody = document.getElementById('typeTotalBody');
    tableBody.innerHTML = '';
    for(const typeNTotal of typeNTotals) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${typeNTotal.typeName}</td>
            <td>${typeNTotal.appointmentCount}</td>
        `;
        tableBody.appendChild(row);
    }
}
async function displayContactSchedules() {
    const contact = document.getElementById('cb_contacts').value;
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;
    const response = await fetch('/api/getContactSchedule', {
                                       method: 'POST', // Specify the method
                                       headers: {
                                           'Accept': 'application/json',
                                           'Content-Type': 'application/json',
                                           [csrfHeaderName]: csrfToken,
                                       },
                                       body: JSON.stringify(contact)
                                   });
    const schedules = await response.json();
    const tableBody = document.getElementById('contactScheduleBody');
    tableBody.innerHTML = '';
    for(const appointment of schedules) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${appointment.id}</td>
            <td>${appointment.title}</td>
            <td>${appointment.type}</td>
            <td>${appointment.description}</td>
            <td>${appointment.start_DateTime}</td>
            <td>${appointment.end_DateTime}</td>
            <td>${appointment.customer_ID}</td>
        `;
        tableBody.appendChild(row);
    }
}
async function fetchLoginStats(startDate, endDate) {
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;

    const response = await fetch('/api/getLoginStats', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            [csrfHeaderName]: csrfToken,
        },
        body: JSON.stringify({ startDate, endDate })
    });

    return await response.json();
}

function updateLoginStatsTable(loginStats) {
    const tableBody = document.getElementById('loginStatsTableBody');
    tableBody.innerHTML = '';
    console.log(loginStats);
    console.log(loginStats.toString());
    for (const stat of loginStats) {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${stat.userName}</td>
            <td>${stat.countSuccess}</td>
            <td>${stat.countFailed}</td>
            <td>${stat.lastAttempt}</td>
        `;
        tableBody.appendChild(row);
    }
}