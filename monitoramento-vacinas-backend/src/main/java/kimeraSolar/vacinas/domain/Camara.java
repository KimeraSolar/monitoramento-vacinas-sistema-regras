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
	
	public Camara(String id) {
		super();
		this.setObjectId(id);
		this.gerentes = new LinkedList<Gerente>();
		this.vacinas = new LinkedList<Vacina>();
		this.ativa = false;
		this.temperaturas = new HashMap<Date, LeituraTemperatura>();
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
		this.ativa = this.vacinas.isEmpty() ? false : true;
	}
	
	public void addVacina(Vacina v) {
		this.vacinas.add(v);
		this.ativa = true;
	}
	
	public void removeVacina(Vacina v) {
		this.vacinas.remove(v);
		this.ativa = this.vacinas.isEmpty() ? false : true;
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
