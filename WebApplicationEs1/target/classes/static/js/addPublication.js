console.log('Publications JS Loaded');

document.getElementById("addPublicationButton").addEventListener("click", function() {
    document.getElementById("publicationForm").style.display = "block";
    document.getElementById("removePublicationForm").style.display = "none";
});

document.getElementById("newPublicationForm").addEventListener("submit", function(event) {
    event.preventDefault();

    const title = document.getElementById("title").value;
    const year = document.getElementById("year").value;
    const journal = document.getElementById("journal").value;

    const tableBody = document.getElementById("publicationTable").getElementsByTagName('tbody')[0];

    addNewPublicationRow(tableBody, title, year, journal);

    document.getElementById("publicationForm").style.display = "none";
    document.getElementById("newPublicationForm").reset();
});

function addNewPublicationRow(tableBody, title, year, publisher) {
    const newRow = tableBody.insertRow();

    const titleCell = newRow.insertCell(0);
    const yearCell = newRow.insertCell(1);
    const publisherCell = newRow.insertCell(2);
    const deleteButtonCell = newRow.insertCell(3);

    titleCell.textContent = title;
    yearCell.innerHTML = `<span class="badge bg-primary">${year}</span>`;
    publisherCell.textContent = publisher;
    deleteButtonCell.innerHTML = `<button class="btn btn-danger btn-sm deleteButton"><i class="fa-solid fa-trash"></i></button>`;

    deleteButtonCell.querySelector(".deleteButton").addEventListener("click", function() {
        tableBody.removeChild(newRow);
        console.log('Publication row deleted');
    });

    console.log('New publication row added');
}
