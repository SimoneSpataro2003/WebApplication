document.getElementById("removePublicationButton").addEventListener("click", function() {
    document.getElementById("removePublicationForm").style.display = "block";
    document.getElementById("publicationForm").style.display = "none";
});

function removePublicationByNumber(event) {
    event.preventDefault();

    const publicationNumber = parseInt(document.getElementById("publicationNumber").value);

    const rows = document.getElementById('publicationTable').querySelectorAll('tbody tr');

    if (publicationNumber > 0 && publicationNumber <= rows.length) {
        rows[publicationNumber - 1].remove();
        document.getElementById("removePublicationForm").style.display = "none";
        document.getElementById("removePublicationByNumberForm").reset();
    } else {
        alert("Numero pubblicazione non valido!");
    }
}

document.querySelectorAll(".deleteButton").forEach(button => {
    button.addEventListener("click", function() {
        const row = button.closest("tr");
        row.parentNode.removeChild(row); // Elimina la riga
    });
});

function deleteRow(button) {
    const row = button.closest("tr"); // Trova la riga corrente
    row.parentNode.removeChild(row); // Elimina la riga
}
