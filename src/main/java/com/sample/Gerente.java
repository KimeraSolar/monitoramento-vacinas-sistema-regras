package com.sample;

public class Gerente extends MovingObject {
	
	private String nome;
	
	public Gerente(String nome, String id) {
		super();
		this.nome = nome;
		this.setObjectId(id);
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void sendMensagem(String m) {
		System.out.print("Mensagem para gerente " + this.getNome() + ": ");
		System.out.println(m);
	}

}
