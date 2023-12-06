(function() {
    miseAJourTable(); // s'execute au cahrgement du script
})();

document.getElementById('update').addEventListener("click",function() {
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function()
    {
        console.log(this);
        if (this.readyState === 4 && this.status === 200)
        {
            console.log(this.response);
            miseAJourTable();

            let textLog = document.getElementById("textLog");
            textLog.innerHTML = this.responseText;
        }
        else if (this.readyState === 4) {
            alert("Une erreur est survenue...");
        }
    };
    let url = "http://127.0.0.1:8080/api/tasks?id=" + document.getElementById('idInput').value;
    xhr.open("PUT",url,true);

    xhr.responseType = "text";
    xhr.setRequestHeader("Content-type","text/plain");

    let body = document.getElementById('prixInput').value;
    body += "&" + document.getElementById('stockInput').value;

    xhr.send(body);

    document.getElementById('idInput').value = "";
    document.getElementById('intituleInput').value = "";
    document.getElementById('stockInput').value = "";
    document.getElementById('prixInput').value = "";


});

function miseAJourTable()
{
    let xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function()
    {
        console.log(this);
        if (this.readyState === 4 && this.status === 200)
        {
            console.log(this.response);
            let articles = this.response;
            videTable();
            articles.forEach(function(article) {
                ajouteLigne(article.id,article.intitule, article.prix, article.stock, article.image);
            })
        }
        else if (this.readyState === 4) {
            alert("Une erreur est survenue...");
        }
    };
    xhr.open("GET","http://localhost:8080/api/tasks",true);
    xhr.responseType = "json";
    xhr.send();

}

function ajouteLigne(id,intitule, prix, stock, image)
{
    let maTable = document.getElementById("tableArticle");

    // Créer une nouvelle ligne
    let nouvelleLigne = document.createElement("tr");

    // Créer des cellules
    let celluleId = document.createElement("td");
    celluleId.textContent = id;
    let celluleIntitule = document.createElement("td");
    celluleIntitule.textContent = intitule;
    let cellulePrix = document.createElement("td");
    cellulePrix.textContent = prix;
    let celluleStock = document.createElement("td");
    celluleStock.textContent = stock;

    // Ajouter les cellules à la ligne
    nouvelleLigne.appendChild(celluleId);
    nouvelleLigne.appendChild(celluleIntitule);
    nouvelleLigne.appendChild(cellulePrix);
    nouvelleLigne.appendChild(celluleStock);

    attacherGestionnaireLigne(nouvelleLigne, image); // pour ajouter au gestionnaire d'event

    // Ajouter la nouvelle ligne au tableau
    maTable.appendChild(nouvelleLigne);
}

function videTable() {
    let maTable = document.getElementById("tableArticle");
    while (maTable.rows.length > 1) {
        maTable.deleteRow(-1); // supprimer dernière ligne
    }
}

function attacherGestionnaireLigne(ligne, image) {
    ligne.addEventListener("click", function() {
        // Récupérer les cellules de la ligne cliquée
        let cells = ligne.cells;

        // Récupérer les valeurs des cellules
        let id = cells[0].textContent;
        let intitule = cells[1].textContent;
        let prix = cells[2].textContent;
        let stock = cells[3].textContent;

        // Mettre à jour les champs d'entrée avec les valeurs
        document.getElementById("idInput").value = id;
        document.getElementById("intituleInput").value = intitule;
        document.getElementById("prixInput").value = prix;
        document.getElementById("stockInput").value = stock;

        document.getElementById("imgProduit").src = "images/" + image;
    });
}