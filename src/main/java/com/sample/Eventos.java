package com.sample;

import java.util.Date;

public class Eventos {
	
	public static class MudancaTemperatura{
		public MudancaTemperatura(Camara camara, float temp, Date inicio) {
			super();
			this.camara = camara;
			this.temp = temp;
			this.inicio = inicio;
		}
		
		private Camara camara;
		private float temp;
		private Date inicio;
		
		public Camara getCamara() {
			return camara;
		}
		public void setCamara(Camara camara) {
			this.camara = camara;
		}
		public float getTemp() {
			return temp;
		}
		public void setTemp(float temp) {
			this.temp = temp;
		}
		public Date getInicio() {
			return inicio;
		}
		public void setInicio(Date inicio) {
			this.inicio = inicio;
		}
	}
	
	public static class Perigo{ 
		public Perigo(Camara camara, float temp, boolean ativo, Date inicio) {
			super();
			this.camara = camara;
			this.temp = temp;
			this.ativo = ativo;
			this.inicio = inicio;
			this.duration = 0;
		}
		
		private Camara camara;
		private float temp;
		private boolean ativo;
		private Date inicio;
		private long duration;
		
		public Camara getCamara() {
			return camara;
		}
		public void setCamara(Camara camara) {
			this.camara = camara;
		}
		public float getTemp() {
			return temp;
		}
		public void setTemp(float temp) {
			this.temp = temp;
		}
		public boolean isAtivo() {
			return ativo;
		}
		public void setAtivo(boolean ativo) {
			this.ativo = ativo;
			if (ativo == false) {
				this.setDuration(System.currentTimeMillis() - this.getInicio().getTime());
			}
		}
		public Date getInicio() {
			return inicio;
		}
		public void setInicio(Date inicio) {
			this.inicio = inicio;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
	};
	
	public static class Alerta{
		
		public Alerta(Camara camara, Gerente gerente, float temp, boolean ativo, Date inicio) {
			super();
			this.camara = camara;
			this.gerente = gerente;
			this.temp = temp;
			this.ativo = ativo;
			this.inicio = inicio;
		}
		
		private Camara camara;
		private Gerente gerente;
		private float temp;
		private boolean ativo;
		private Date inicio;
		private long duration;
		
		public Camara getCamara() {
			return camara;
		}
		public void setCamara(Camara camara) {
			this.camara = camara;
		}
		public Gerente getGerente() {
			return gerente;
		}
		public void setGerente(Gerente gerente) {
			this.gerente = gerente;
		}
		public float getTemp() {
			return temp;
		}
		public void setTemp(float temp) {
			this.temp = temp;
		}
		public boolean isAtivo() {
			return ativo;
		}
		public void setAtivo(boolean ativo) {
			this.ativo = ativo;
		}
		public Date getInicio() {
			return inicio;
		}
		public void setInicio(Date inicio) {
			this.inicio = inicio;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		
	}

}
