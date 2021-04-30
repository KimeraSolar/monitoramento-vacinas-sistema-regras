package com.sample;
import java.util.List;

public class Camara extends MovingObject {

	private float temp;
	private Vacina vacina;
	private List<Gerente> gerentes;
	private boolean ativa;
	
	public Camara(String id, Vacina vacina, List<Gerente> gerentes) {
		super();
		this.setObjectId(id);
		this.vacina = vacina;
		this.gerentes = gerentes;
		this.ativa = this.vacina == null ? false : true;
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}
	
	public Vacina getVacina() {
		return vacina;
	}

	public void setVacina(Vacina vacina) {
		this.vacina = vacina;
		this.ativa = this.vacina == null ? false : true;
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

}
