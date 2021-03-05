package com.sample;

public class Camara extends MovingObject {

	private float temp;
	
	public Camara(String id) {
		super();
		this.setObjectId(id);
	}

	public float getTemp() {
		return temp;
	}

	public void setTemp(float temp) {
		this.temp = temp;
	}
	
	public void sendMessage(String m) {
		System.out.println(m);
	}

}
