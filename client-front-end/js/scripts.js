// Initialize and add the map
function initMap() {
    // The location of Uluru
    const uluru = { lat: -25.344, lng: 131.036 };
    // The map, centered at Uluru
    const map = new google.maps.Map(document.getElementById("map"), {
    zoom: 4,
    center: uluru,
    });
    // The marker, positioned at Uluru
    const marker = new google.maps.Marker({
    position: uluru,
    map: map,
    });
}

function detalhesCamara(cam) {
	document.getElementById("nome-camara").innerHTML = `Câmara ` + cam
    fetch("http://localhost:8080/vacinas/" + cam + "/vacinas")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        document.getElementById("vacinas").innerHTML = ``
        response_json.forEach(createVacina)
    })
    /*
    fetch("http://localhost:8080/vacinas/" + cam + "/temperaturas")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        // Criar o grafico
    })
    */
}

function createVacina(vac) {
    let container = document.createElement("div")
    container.className = "col-xl-4"

    let classe = ''
    let descarte = ''
    if(vac.descartada){
        classe = 'bg-danger'
        descarte = 'Descartada'
    }else{
        classe = 'bg-success'
        descarte = 'Não descartada'
    }

    container.innerHTML = `
        <div class="card text-white ` + classe + ` mb-4">
            <div class="card-header">
                <i class="fas fa-syringe"></i>
                ` + vac.nome + `
            </div>
            <div class="card-body">
                <p class="card-text mb-1"><i class="fas fa-thermometer-half"></i>
                    Máx: ` + vac.tempMax.toFixed(2) + ` ºC
                </p>
                <p class="card-text mb-1"><i class="fas fa-thermometer-half"></i>
                    Min: ` + vac.tempMin.toFixed(2) + ` ºC
                </p>
                <p class="card-text mb-1"> Abastecimento: ` + new Date(vac.abastecimento.replace('[UTC]', '')).toLocaleString("pt-BR") + `</p>
            </div>
            <div class="card-footer small" style="text-align: end;">
                ` + descarte + `
            </div>
        </div>
    `

    document.getElementById("vacinas").appendChild(container)

}


function createChamber(chb) {
    let container = document.createElement("div")
    container.className = "col-xl-3"

    let card = document.createElement("div")
    let header = document.createElement("div")
    header.style = "text-align: end;"
    header.className = "card-header small"

    let footer = document.createElement("div")
    footer.className = "card-footer d-flex align-items-center justify-content-between"
    
    let anchor = document.createElement("a")
    anchor.innerText = "Ver detalhes"
    anchor.href = "javascript:detalhesCamara('" + chb.id + "')"
    
    let footerIconDiv = document.createElement("div")
    footerIconDiv.className = "small"

    let footerIcon = document.createElement("i")
    footerIcon.className = "fas fa-angle-right"

    footerIconDiv.appendChild(footerIcon)

    if (chb.ativo) {
        card.className = "card bg-primary text-white mb-4"
        header.innerText = "Ativa"
        anchor.className = "small text-white stretched-link"
    } else {
        card.className = "card bg-light text-dark mb-4"
        header.innerText = "Inativa"
        anchor.className = "small text-dark stretched-link"
    }

    let body = document.createElement("div")
    body.className = "card-body"

    let title = document.createElement("h5")
    title.className = "card-title"

    let titleIcon = document.createElement("i")
    titleIcon.className = "fas fa-thermometer-half"

    title.appendChild(titleIcon)
    title.append(" " + chb.temperatura.toFixed(2) + " ºC")

    let text = document.createElement("h3")
    text.className = "card-text"
    text.innerText = "Câmara " + chb.id

    body.appendChild(title)
    body.appendChild(text)

    footer.appendChild(anchor)
    footer.appendChild(footerIconDiv)

    card.append(header, body, footer)
    container.appendChild(card)

    document.getElementById("camaras").appendChild(container)
}

function initPage(){
    fetch("http://localhost:8080/vacinas/g01/camaras")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
    	response_json.forEach(createChamber)
    })
}