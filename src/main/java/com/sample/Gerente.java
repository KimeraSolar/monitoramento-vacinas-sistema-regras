package com.sample;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Gerente extends MovingObject {
	
	private String nome;
	private List<Camara> camaras;
	private Map<Date, String> mensagens;
	
	public Gerente(String id, String nome) {
		super();
		this.nome = nome;
		this.setObjectId(id);
		this.mensagens = new HashMap<Date, String>();
		this.camaras = new LinkedList<Camara>();
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void sendMensagem(String m) {
		this.mensagens.put(new Date(), m);
	}

	public List<Camara> getCamaras() {
		return camaras;
	}

	public void setCamaras(List<Camara> camaras) {
		this.camaras = camaras;
	}
	
	public void addCamara(Camara camara) {
		this.camaras.add(camara);
	}

	public Map<Date, String> getMensagens() {
		return mensagens;
	}

}
