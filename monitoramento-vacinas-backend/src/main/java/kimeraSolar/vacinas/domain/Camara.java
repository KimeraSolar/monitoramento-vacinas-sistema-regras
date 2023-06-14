package kimeraSolar.vacinas.domain;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kimeraSolar.vacinas.domain.Eventos.LeituraTemperatura;

public class Camara extends MovingObject {

	private LeituraTemperatura temperaturaAtual;
	private List<Vacina> vacinas;
	private List<Gerente> gerentes;
	private Map<Date, LeituraTemperatura> temperaturas;
	private boolean ativa;
	private boolean hasPerigo;
	private boolean hasAlerta;
	
	public Camara(String id) {
		super();
		this.setObjectId(id);
		this.gerentes = new LinkedList<Gerente>();
		this.vacinas = new LinkedList<Vacina>();
		this.ativa = false;
		this.hasAlerta = false;
		this.hasPerigo = false;
		this.temperaturas = new HashMap<Date, LeituraTemperatura>();
	}

	public void setPerigo(boolean perigo){
		this.hasPerigo = perigo;
	}

	public void setAlerta(boolean alerta){
		this.hasAlerta = alerta;
	}

	public String getStatus(){
		if(!this.isAtiva()) return "Inativa";
		if(this.hasPerigo) return "Perigo";
		if(this.hasAlerta) return "Alerta";
		return "Normal";
	}

	public LeituraTemperatura getTemp() {
		return temperaturaAtual;
	}

	public void setTemp(LeituraTemperatura temp) {
		this.temperaturaAtual = temp;
		this.temperaturas.put(new Date(), temp);
	}
	
	public List<Vacina> getVacinas() {
		return vacinas;
	}

	public void setVacinas(List<Vacina> vacinas) {
		this.vacinas = vacinas;
		this.setAtiva(this.vacinas.isEmpty() ? false : true);
	}
	
	public void addVacina(Vacina v) {
		this.vacinas.add(v);
		this.setAtiva(true);
	}
	
	public void removeVacina(Vacina v) {
		this.vacinas.remove(v);
		this.setAtiva(this.vacinas.isEmpty() ? false : true);
		
	}

	public void setAtiva(Boolean ativa){
		this.ativa = ativa;
	}
	
	public boolean isAtiva() {
		return ativa;
	}

	public void sendMessage(String m) {
		for(Gerente g : gerentes) {
			g.sendMensagem(m);
		};
	}
	
	public void addGerente(Gerente g) {
		this.gerentes.add(g);
	}
	
	public void removeGerente(Gerente g) {
		this.gerentes.remove(g);
	}
	
	public Gerente gerenteMaisProx() {
		Gerente maisProx = gerentes.get(0);
		for (Gerente g : gerentes) {
			if (this.getDistance(g) < this.getDistance(maisProx) ) {
				maisProx = g;
			}
		}
		return maisProx;
	}

	public Map<Date, LeituraTemperatura> getTemperaturas() {
		return temperaturas;
	}

}
