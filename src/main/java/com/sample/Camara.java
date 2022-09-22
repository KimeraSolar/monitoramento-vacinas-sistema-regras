package com.sample;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Camara extends MovingObject {

	private float temp;
	private List<Vacina> vacinas;
	private List<Gerente> gerentes;
	private Map<Date, Float> temperaturas;
	private boolean ativa;
	
	public Camara(String id) {
		super();
		this.setObjectId(id);
		this.gerentes = new LinkedList<Gerente>();
		this.vacinas = new LinkedList<Vacina>();
		this.ativa = false;
		this.temperaturas = new HashMap<Date, Float>();
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
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

	public Map<Date, Float> getTemperaturas() {
		return temperaturas;
	}

}
