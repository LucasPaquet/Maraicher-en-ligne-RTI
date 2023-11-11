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

    // Ajouter la nouvelle ligne au tableau
    maTable.appendChild(nouvelleLigne);
}

function videTable() {
    var maTable = document.getElementById("tableArticle");
    while (maTable.rows.length > 1) {
        maTable.deleteRow(-1); // supprimer dernière ligne
    }
}
