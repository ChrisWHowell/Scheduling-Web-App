async function initPage() {
populateContacts();
populateCustomerIDs();
populateUserIDs();
populateTypes();
updateTable();
document.getElementById("btn_SaveAsEdit").addEventListener("click", saveAsEdit);
document.getElementById("btn_SaveAsNew").addEventListener("click", saveNewAppointment);
document.getElementById("btn_ClearInputs").addEventListener("click", clearInputs);
document.getElementById("bt_Delete").addEventListener("click", deleteAppointment);
document.getElementById('btn_viewCustomer').addEventListener('click', function () {
    window.location.href = '/displayCustomers'; });
document.getElementById('all').addEventListener('change', updateTable);
document.getElementById('month').addEventListener('change', updateTable);
document.getElementById('week').addEventListener('change', updateTable);
}
async function populateContacts() {
  const response = await fetch("/api/getAllContacts");
  const contacts = await response.json();
    const contactSelect = document.getElementById("contact");
    contacts.forEach(contact => {
      const option = document.createElement("option");
      option.text = contact;
      option.value = contact;
      contactSelect.add(option);
    });
}
async function populateCustomerIDs(){
  const response = await fetch("/api/getAllCustomerIDs");
  const customerIDs = await response.json();
    const customerIDSelect = document.getElementById("customer_id");
    customerIDs.forEach(customerID => {
        const option = document.createElement("option");
        option.text = customerID;
        option.value = customerID;
        customerIDSelect.add(option);
    });
}
async function populateTypes(){
  const response = await fetch("/api/getAllTypes");
  const types = await response.json();
    const typeSelect = document.getElementById("type");
    types.forEach(type => {
        const option = document.createElement("option");
        option.text = type;
        option.value = type;
        typeSelect.add(option);
    });
}
async function populateUserIDs(){
  const response = await fetch("/api/getAllUserIDs");
  const userIDs = await response.json();
    const userIDSelect = document.getElementById("user_id");
    userIDs.forEach(userID => {
        const option = document.createElement("option");
        option.text = userID;
        option.value = userID;
        userIDSelect.add(option);
    });
}

function getFormData(){
  const appointment = {
    customer_ID: document.getElementById("customer_id").value,
    title: document.getElementById("title").value,
    description: document.getElementById("description").value,
    location: document.getElementById("location").value,
    contact: document.getElementById("contact").value,
    type: document.getElementById("type").value,
    start_DateTime: document.getElementById("start_date").value,
    end_DateTime: document.getElementById("end_date").value,
    user_ID: document.getElementById("user_id").value,
  }
  // Check if the customer ID is set to "#"
    if (document.getElementById("appointment_id").textContent !== "#") {
      appointment.id = parseInt(document.getElementById("appointment_id").textContent);
    }
    return appointment;
}
function getAllFormData() {
  const customer_ID = document.getElementById("customer_id").value;
  const title = document.getElementById("title").value;
  const description = document.getElementById("description").value;
  const location = document.getElementById("location").value;
  const contact = document.getElementById("contact").value;
  const type = document.getElementById("type").value;
  const start_DateTime = document.getElementById("start_date").value;
  const end_DateTime = document.getElementById("end_date").value;
  const user_ID = document.getElementById("user_id").value;

  if(!customer_ID || !title || !description || !location || !contact || !type || !start_DateTime || !end_DateTime || !user_ID){
    alert("Please Input values in all fields");
    return null;
  }
  const appointment = {
    customer_ID,
    title,
    description,
    location,
    contact,
    type,
    start_DateTime,
    end_DateTime,
    user_ID,
  };

  // Check if the customer ID is set to "#"
  if (document.getElementById("appointment_id").textContent !== "#") {
        appointment.id = parseInt(document.getElementById("appointment_id").textContent);
      }
      return appointment;
}
function saveNewAppointment() {
    // Get the form data and build the Appointment object
    const appointment = getAllFormData();
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;
    // Send the appointment data to the API
    fetch('/api/saveNewAppointment', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeaderName]: csrfToken,
        },
        body: JSON.stringify(appointment)
    })
    .then(response => response.text())
    .then(result => {
        if (result === 'Success') {
            // Handle success, e.g., display a success message or refresh the appointment list
            alert("Save successful.");
           } else {
                // Handle error, e.g., display an error message or show an alert
                console.error('Error saving new appointment');
                alert("Save unsuccessful.");
            }
        })
        .catch(error => {
            // Handle fetch error, e.g., display an error message or show an alert
            console.error('Error:', error);
        });
        updateTable();
}
function saveAsEdit() {
    if (document.getElementById("appointment_id").textContent == "#") {
         alert("Please select an appointment from the table below to edit");
         return;
      }
    const appointment = getAllFormData();
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;
     // Call your API to save the data as an edit
      fetch("/api/saveEditAppointment", {
          method: "POST",
          headers: {
              "Content-Type": "application/json",
              [csrfHeaderName]: csrfToken,
          },
          body: JSON.stringify(appointment)
      }).then(response => {
          if (response.ok) {
              // Handle successful edit
              alert("Edit was successful.");
              updateTable();
          } else {
              // Handle error
              alert("Edit was unsuccessful.");
          }
      });
      updateTable();
}
function deleteAppointment() {
    const appointment = getFormData();
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
    const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;
    // Call your API to delete the appointment
    fetch("/api/deleteAppointment", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            [csrfHeaderName]: csrfToken,
        },
        body: JSON.stringify(appointment)
    }).then(response => {
        if (response.ok) {
            // Handle successful delete
            alert("Delete was successful.");
            updateTable();
        } else {
            // Handle error
            alert("Delete was unsuccessful.");
        }
    });
    updateTable();
}
function clearInputs() {
    document.getElementById("appointment_id").textContent = "#";
    document.getElementById("customer_id").value = "";
    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
    document.getElementById("location").value = "";
    document.getElementById("contact").value = "";
    document.getElementById("type").value = "";
    document.getElementById("start_date").value = "";
    document.getElementById("end_date").value = "";
    document.getElementById("user_id").value = "";
}
async function updateTable() {
  let appointments = [];

  if (document.getElementById('all').checked) {
    getTableAll();
  } else if (document.getElementById('month').checked) {
    getTableMonth();
  } else if (document.getElementById('week').checked) {
    getTableWeek();
  }

  displayAppointments(appointments);
}

function displayAppointments(appointments) {
  const tableBody = document.getElementById('appointmentTableBody');
  tableBody.innerHTML = '';

  for (const appointment of appointments) {
    const row = document.createElement('tr');
    row.innerHTML = `
        <td>${appointment.id}</td>
        <td>${appointment.title}</td>
        <td>${appointment.description}</td>
        <td>${appointment.location}</td>
        <td>${appointment.contact}</td>
        <td>${appointment.type}</td>
        <td>${appointment.start_DateTime}</td>
        <td>${appointment.end_DateTime}</td>
        <td>${appointment.customer_ID}</td>
        <td>${appointment.user_ID}</td>
    `;
    tableBody.appendChild(row);
    row.addEventListener('click', () => onRowClick(appointment));
  }
}

async function getTableAll() {
  const csrfToken = document.querySelector('meta[name="_csrf"]').content;
  const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;

  try {
    const response = await fetch('/api/getAllAppointments', {
      method: 'GET',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
        [csrfHeaderName]: csrfToken,
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const appointments = await response.json();
    displayAppointments(appointments);
  } catch (error) {
    console.error(error);
  }
}
async function getTableMonth(){
   const csrfToken = document.querySelector('meta[name="_csrf"]').content;
     const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;

     try {
       const response = await fetch('/api/getThisMonthsAppointments', {
         method: 'GET',
         headers: {
           'Accept': 'application/json',
           'Content-Type': 'application/json',
           [csrfHeaderName]: csrfToken,
         },
       });

       if (!response.ok) {
         throw new Error(`HTTP error! status: ${response.status}`);
       }

       const appointments = await response.json();
       displayAppointments(appointments);
     } catch (error) {
       console.error(error);
     }
}
async function getTableWeek(){
    const csrfToken = document.querySelector('meta[name="_csrf"]').content;
        const csrfHeaderName = document.querySelector('meta[name="_csrf_header"]').content;

        try {
          const response = await fetch('/api/getThisWeeksAppointments', {
            method: 'GET',
            headers: {
              'Accept': 'application/json',
              'Content-Type': 'application/json',
              [csrfHeaderName]: csrfToken,
            },
          });

          if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
          }

          const appointments = await response.json();
          displayAppointments(appointments);
        } catch (error) {
          console.error(error);
        }
}
async function onRowClick(appointment) {
    // Populate the form fields with the clicked customer's data
    document.getElementById('appointment_id').textContent = appointment.id;
    document.getElementById('description').value = appointment.description;
    document.getElementById('customer_id').value = appointment.customer_ID;
    document.getElementById('user_id').value = appointment.user_ID;
    document.getElementById('contact').value = appointment.contact;
    document.getElementById('title').value = appointment.title;
    document.getElementById('type').value = appointment.type;
    document.getElementById('location').value = appointment.location;
    document.getElementById('start_date').value = appointment.start_DateTime ;
    document.getElementById('end_date').value = appointment.end_DateTime;
}