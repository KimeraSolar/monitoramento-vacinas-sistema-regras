package com.sample;

public class Camara extends MovingObject {

	private float temp;
	private Vacina vacina;
	
	public Camara(String id, Vacina vacina) {
		super();
		this.setObjectId(id);
		this.vacina = vacina;
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
	}

	public void sendMessage(String m) {
		System.out.println(m);
	}

}
