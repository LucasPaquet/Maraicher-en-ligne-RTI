(function() {
    miseAJourTable(); // s'execute au cahrgement du script
})();

document.getElementById('add').addEventListener("click",function(e) {
    miseAJourTable()
    /*
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function()
    {

    };
    xhr.open("POST","http://localhost:8080/api/tasks",true);
    xhr.responseType = "text";
    xhr.setRequestHeader("Content-type","text/plain");
    var body = "ca marche";
    xhr.send(body);
    document.getElementById('description').value = "";
    document.getElementById('id').value = "";

     */

});

document.getElementById('update').addEventListener("click",function(e) {
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function()
    {
        console.log(this);
        if (this.readyState == 4 && this.status == 200)
        {
            console.log(this.response);
            miseAJourTable();

            var textLog = document.getElementById("textLog");
            textLog.innerHTML = this.responseText;
        }
        else if (this.readyState == 4) {
            alert("Une erreur est survenue...");
        }
    };
    var url = "http://127.0.0.1:8080/api/tasks?id=" + document.getElementById('idInput').value;
    xhr.open("PUT",url,true);

    xhr.responseType = "text";
    xhr.setRequestHeader("Content-type","text/plain");

    var body = document.getElementById('prixInput').value;
    body += "&" + document.getElementById('stockInput').value;

    xhr.send(body);

    document.getElementById('idInput').value = "";
    document.getElementById('intituleInput').value = "";
    document.getElementById('stockInput').value = "";
    document.getElementById('prixInput').value = "";


});

function miseAJourTable()
{
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function()
    {
        console.log(this);
        if (this.readyState == 4 && this.status == 200)
        {
            console.log(this.response);
            articles = this.response;
            videTable();
            articles.forEach(function(article) {
                ajouteLigne(article.id,article.intitule, article.prix, article.stock);
            })
        }
        else if (this.readyState == 4) {
            alert("Une erreur est survenue...");
        }
    };
    xhr.open("GET","http://localhost:8080/api/tasks",true);
    xhr.responseType = "json";
    xhr.send();

}

function ajouteLigne(id,intitule, prix, stock)
{
    var maTable = document.getElementById("tableArticle");

    // Créer une nouvelle ligne
    var nouvelleLigne = document.createElement("tr");

    // Créer des cellules
    celluleId = document.createElement("td");
    celluleId.textContent = id;
    celluleIntitule = document.createElement("td");
    celluleIntitule.textContent = intitule;
    cellulePrix = document.createElement("td");
    cellulePrix.textContent = prix;
    celluleStock = document.createElement("td");
    celluleStock.textContent = stock;

    // Ajouter les cellules à la ligne
    nouvelleLigne.appendChild(celluleId);
    nouvelleLigne.appendChild(celluleIntitule);
    nouvelleLigne.appendChild(cellulePrix);
    nouvelleLigne.appendChild(celluleStock);

    attacherGestionnaireLigne(nouvelleLigne); // pour ajouter au gestionnaire d'event

    // Ajouter la nouvelle ligne au tableau
    maTable.appendChild(nouvelleLigne);
}

function videTable() {
    var maTable = document.getElementById("tableArticle");
    while (maTable.rows.length > 1) {
        maTable.deleteRow(-1); // supprimer dernière ligne
    }
}

function attacherGestionnaireLigne(ligne) {
    ligne.addEventListener("click", function() {
        // Récupérer les cellules de la ligne cliquée
        var cells = ligne.cells;

        // Récupérer les valeurs des cellules
        var id = cells[0].textContent;
        var intitule = cells[1].textContent;
        var prix = cells[2].textContent;
        var stock = cells[3].textContent;

        // Mettre à jour les champs d'entrée avec les valeurs
        document.getElementById("idInput").value = id;
        document.getElementById("intituleInput").value = intitule;
        document.getElementById("prixInput").value = prix;
        document.getElementById("stockInput").value = stock;
    });
}