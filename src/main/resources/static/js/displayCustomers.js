function getFormData() {
  const formData = {
    country: document.getElementById("cb_Country").value,
    division: document.getElementById("cb_state").value,
    postal_code: document.getElementById("tf_PostalC").value,
    name: document.getElementById("tf_Name").value,
    address: document.getElementById("tf_Address").value,
    phone: document.getElementById("tf_Phone").value,
  };

  // Check if the customer ID is set to "#"
  if (document.getElementById("lb_custID").textContent !== "#") {
    formData.id = parseInt(document.getElementById("lb_custID").textContent);
  }

  return formData;
}
function getAllFormData() {
  const country = document.getElementById("cb_Country").value;
  const division = document.getElementById("cb_state").value;
  const postalCode = document.getElementById("tf_PostalC").value;
  const name = document.getElementById("tf_Name").value;
  const address = document.getElementById("tf_Address").value;
  const phone = document.getElementById("tf_Phone").value;

  // Check if any field is empty
  if (!country || !division || !postalCode || !name || !address || !phone) {
    alert("Please Input values in all fields");
    return null;
  }

  const formData = {
    country,
    division,
    postal_code: postalCode,
    name,
    address,
    phone,
  };

  // Check if the customer ID is set to "#"
  if (document.getElementById("lb_custID").textContent !== "#") {
    formData.id = parseInt(document.getElementById("lb_custID").textContent);
  }

  return formData;
}
async function initPage() {
  const countries = await fetchCountries();
  populateCountriesComboBox(countries);
  document.getElementById("cb_Country").addEventListener("change", onCountryChange);
  onCountryChange();
  loadCustomers();
  document.getElementById("btn_SaveAsEdit").addEventListener("click", saveAsEdit);
  document.getElementById("btn_SaveAsNew").addEventListener("click", saveAsNew);
  document.getElementById("btn_Filter").addEventListener("click", loadFilteredCustomers);
  document.getElementById("btn_ResetFilter").addEventListener("click", resetFiltersAndReloadTable);
  document.getElementById("bt_Delete").addEventListener("click", deleteCustomer);
  document.getElementById('btn_viewAppointments').addEventListener('click', function () {
    window.location.href = '/appointmentsView';

  });
   document.getElementById('btn_viewReports').addEventListener('click', function () {
           window.location.href = '/reportsView';
  });
}

async function fetchCountries() {
  const response = await fetch("/api/countries");
  const countries = await response.json();
  return countries;
}
async function onCountryChange() {
    const countryName = document.getElementById("cb_Country").value;

    const divisions = await fetchDivisions(countryName);
    populateDivisions(divisions);
}
function populateCountriesComboBox(countries) {
  const countrySelect = document.getElementById("cb_Country");
  countries.forEach(country => {
    const option = document.createElement("option");
    option.text = country;
    option.value = country;
    countrySelect.add(option);
  });
}
async function fetchDivisions(countryName) {
    const response = await fetch(`/api/divisions?countryName=${countryName}`);
    if (response.ok) {
        return await response.json();
    } else {
        console.error("Failed to fetch divisions");
        return [];
    }
}
function populateDivisions(divisions) {
    const stateCombobox = document.getElementById("cb_state");
    stateCombobox.innerHTML = ""; // Clear the existing options

    divisions.forEach(division => {
        const option = document.createElement("option");
        option.value = division;
        option.textContent = division;
        stateCombobox.appendChild(option);
    });
}
function saveAsEdit() {
  if (document.getElementById("lb_custID").textContent == "#") {
     alert("Please select a customer from the table below to edit");
     return;
    }
  const formData = getAllFormData();
  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;

  const now = new Date(); // Define the 'now' variable here
  const offsetInMinutes = now.getTimezoneOffset() - 360; // Central Time Zone is UTC-6, so the offset is -360 minutes
  const centralDate = new Date(now.getTime() + offsetInMinutes * 60 * 1000);
  const formattedCentralDate = centralDate.toISOString().replace('T', ' ').split('.')[0];
  formData.centralDateTime = formattedCentralDate; // Converted date in Central Time

  //formData.centralDateTime = centralDateTime.toISOString(); // Converted date in Central Time

  // Call your API to save the data as an edit
  fetch("/api/saveAsEdit", {
      method: "POST",
      headers: {
          "Content-Type": "application/json",
          [csrfHeaderName]: csrfToken
      },
      body: JSON.stringify(formData)
  }).then(response => {
        if (response.ok) {
            // Handle successful deletion
            alert("Customer edit successfully.");
            updateTable();
        }  else {
            // Handle other errors
            alert("An error occurred while deleting the customer.");
        }
    }).catch(error => {
        console.error("Error:", error);
    });

}

function saveAsNew() {
    const formData = getAllFormData();
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;

    const now = new Date(); // Define the 'now' variable here
    const offsetInMinutes = now.getTimezoneOffset() - 360; // Central Time Zone is UTC-6, so the offset is -360 minutes
    const centralDate = new Date(now.getTime() + offsetInMinutes * 60 * 1000);
    const formattedCentralDate = centralDate.toISOString().replace('T', ' ').split('.')[0];
    formData.centralDateTime = formattedCentralDate; // Converted date in Central Time
  // Call your API to save the data as a new record
  fetch("/api/saveAsNew", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      [csrfHeaderName]: csrfToken
    },
    body: JSON.stringify(formData)
  }).then(response => {
    if (response.ok) {
      alert("Creating New Customer was successful.");
                updateTable();
    } else {
      alert("Creating New Customer was unsuccessful.");
    }
  });
}
async function loadCustomers() {
    const response = await fetch('/api/customer_List');
    const customers = await response.json();

    const tableBody = document.querySelector('#tv_CustomerList tbody');

    // Clear existing rows
    tableBody.innerHTML = '';

    // Create new rows for each customer
    for (const customer of customers) {
        const row = document.createElement('tr');

        row.innerHTML = `
            <td>${customer.id}</td>
            <td>${customer.name}</td>
            <td>${customer.address}</td>
            <td>${customer.postal_code}</td>
            <td>${customer.division}</td>
            <td>${customer.country}</td>
            <td>${customer.phone}</td>
        `;

        // Add a click event listener for the row
        row.addEventListener('click', () => onRowClick(customer));

        // Add the row to the table body
        tableBody.appendChild(row);
    }
}
async function loadFilteredCustomers() {
     const formData = getFormData();
     const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
     const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

         const response = await fetch('/api/applyFilter', {
             method: 'POST',
             headers: {
                 'Content-Type': 'application/json',
                 'Accept': 'application/json',
                 [csrfHeaderName]: csrfToken,
             },
             body: JSON.stringify(formData),
         });
        const customers = await response.json();
        console.log('Process has reached the filtered method');
        const tableBody = document.querySelector('#tv_CustomerList tbody');

        // Clear existing rows
        tableBody.innerHTML = '';

        // Create new rows for each customer
        for (const customer of customers) {
            const row = document.createElement('tr');

            row.innerHTML = `
                <td>${customer.id}</td>
                <td>${customer.name}</td>
                <td>${customer.address}</td>
                <td>${customer.postal_code}</td>
                <td>${customer.division}</td>
                <td>${customer.country}</td>
                <td>${customer.phone}</td>
            `;

            // Add a click event listener for the row
            row.addEventListener('click', () => onRowClick(customer));

            // Add the row to the table body
            tableBody.appendChild(row);
        }
}
async function onRowClick(customer) {
    // Populate the form fields with the clicked customer's data
    document.getElementById('cb_Country').value = customer.country;
    await onCountryChange();
    document.getElementById('cb_state').value = customer.division;
    document.getElementById('tf_PostalC').value = customer.postal_code;
    document.getElementById('tf_Name').value = customer.name;
    document.getElementById('tf_Address').value = customer.address;
    document.getElementById('tf_Phone').value = customer.phone;
    document.getElementById('lb_custID').textContent = customer.id;

}
function updateTable() {
  // Clear the table
  let table = document.querySelector('#tv_CustomerList'); // Assuming 'tv_CustomerList' is the table ID
  console.log('table:', table); // Debugging line
  let tableBody = table.querySelector('tbody');
  console.log('tableBody:', tableBody); // Debugging line
  tableBody.innerHTML = '';

  // Fetch new data and update the table
  loadCustomers();
}
function resetFiltersAndReloadTable() {
    // Clear the values of the input elements
    document.querySelector('#tf_PostalC').value = '';
    document.querySelector('#tf_Name').value = '';
    document.querySelector('#tf_Address').value = '';
    document.querySelector('#tf_Phone').value = '';

    // Reset the selected options for the <select> elements
    document.querySelector('#cb_Country').selectedIndex = '';
    document.querySelector('#cb_state').selectedIndex = '';
    document.querySelector('#lb_custID').textContent = '#';

    // Reload the table by calling the loadCustomers() function
    loadCustomers();
}
async function deleteCustomer(){
    const formData = getFormData();
        const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;

      fetch("/api/deleteCustomer", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          [csrfHeaderName]: csrfToken
        },
        body: JSON.stringify(formData)
      }).then(response => {
        if (response.ok) {
              // Handle successful deletion
              alert("Customer deleted successfully.");
              updateTable();
            } else if (response.status === 403) {
              alert("You do not have the correct permissions to do this action.");
            } else {
              // Handle other errors
              alert("Failed to delete customer.");
            }
          });
      resetFiltersAndReloadTable();
    }

