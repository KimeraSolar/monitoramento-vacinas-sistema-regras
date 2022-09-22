// Initialize and add the map
function initMap(locations) {
    // Localização do gerente
    let gerente = {}
    locations.forEach(function(location){
        if(location.key == "Gerente"){
            gerente.lat = location.value.latitude
            gerente.lng = location.value.longitude
        }
    })

    const map = new google.maps.Map(document.getElementById("map"), {
    zoom: 4,
    center: gerente,
    })

    locations.forEach(function(location){
        new google.maps.Marker( { 
            position : { 
                lat : location.value.latitude, 
                lng : location.value.longitude
            },
            map : map,
            title : location.key,
            icon : location.key == "Gerente" ? "http://maps.google.com/mapfiles/ms/icons/green-dot.png" : "http://maps.google.com/mapfiles/ms/icons/red-dot.png"
        } ) 
    })

}

function detalhesCamara(cam) {
    document.getElementById("detalhes-placeholder").innerHTML = ``
	document.getElementById("nome-camara").innerHTML = `Câmara ` + cam
    fetch("http://localhost:8080/vacinas/" + cam + "/vacinas")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        document.getElementById("vacinas").innerHTML = ``
        response_json.forEach(createVacina)
    })
    
    fetch("http://localhost:8080/vacinas/" + cam + "/temperaturas")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        criaGrafico( response_json )
    })
}

function criaGrafico( temperaturas ){
    temperaturas.sort(function(a, b) {
    let keyA = new Date(a.key.replace('[UTC]', '')),
    keyB = new Date(b.key.replace('[UTC]', ''));
    // Compare the 2 dates
    if (keyA < keyB) return -1;
    if (keyA > keyB) return 1;
    return 0;
    })

    temperaturas = temperaturas.slice(temperaturas.length - 20, temperaturas.length)

    let labels = []
    let dataset = []
    temperaturas.forEach(function(temp){
        labels.push(new Date(temp.key.replace('[UTC]', '')).toLocaleString("pt-BR"))
        dataset.push(temp.value)
    })

    var ctx = document.createElement("canvas");
    ctx.getContext("2d").height = "40"
    
    let area = document.getElementById("myAreaChart")
    area.innerHTML = ``
    area.appendChild(ctx)
    
    new Chart(ctx, {
        type: 'line',
        data: {
          labels: labels,
          datasets: [{ 
              data: dataset,
              label: "Temperatura",
              borderColor: "#3e95cd",
              fill: true
            }
          ]
        }
    });

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

function printMensagens(mensagens){
    mensagens.sort(function(a, b) {
    let keyA = new Date(a.key.replace('[UTC]', '')),
    keyB = new Date(b.key.replace('[UTC]', ''));
    // Compare the 2 dates
    if (keyA < keyB) return 1;
    if (keyA > keyB) return -1;
    return 0;
    });

    let container_pai = document.getElementById("mensagens")
    mensagens.forEach(function(mensagem){
        let container = document.createElement("div")
        container.className = "alert alert-secondary"
        container.role = "alert"
        container.innerHTML = `
            <p>` + mensagem.value + `</p>
            <hr>
            <p class="mb-0 small">` + new Date(mensagem.key.replace('[UTC]', '')).toLocaleString("pt-BR") + `</p>
        `
        container_pai.appendChild(container)
    })

}

function createGerente( gerente ){
    let container = document.getElementById("gerentes")
    let button = document.createElement("button")
    button.className = "btn btn-sm btn-outline-secondary button-gerente"
    button.type = "button"
    button.innerText = gerente.value
    container.appendChild(button)
    button.addEventListener("click", function(){
        initPage(gerente.key)
    })
}

function initDocument(){
    fetch("http://localhost:8080/vacinas/gerentes")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        response_json.sort(function(a, b){
            if (a.value < b.value) return -1;
            if (a.value > b.value) return 1;
            return 0;
        })
        response_json.forEach(createGerente)
    })
}

function initPage(gerente){
    
    fetch("http://localhost:8080/vacinas/" + gerente + "/camaras")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        document.getElementById("camaras").innerHTML = ``
    	response_json.forEach(createChamber)
    })
    
    fetch("http://localhost:8080/vacinas/" + gerente + "/mensagens")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        document.getElementById("mensagens").innerHTML = ``
        printMensagens(response_json)
    })

    fetch("http://localhost:8080/vacinas/" + gerente + "/locations")
    .then(function( response ){
        return response.json()
    })
    .then(function( response_json ){
        initMap(response_json)
    })

    document.getElementById("vacinas").innerHTML = ``
    document.getElementById("nome-camara").innerHTML = ``
    document.getElementById("myAreaChart").innerHTML = ``
    document.getElementById("detalhes-placeholder").innerHTML = `Escolha uma câmara para ver detalhes.`
}