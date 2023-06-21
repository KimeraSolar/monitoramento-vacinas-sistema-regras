package kimeraSolar.vacinas.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class Eventos {
	
	public static class LeituraTemperatura implements Serializable{
		public LeituraTemperatura(Camara camara, float temp, Timestamp inicio) {
			super();
			this.camara = camara;
			this.temp = temp;
			this.inicio = inicio;
		}
		
		private Camara camara;
		private float temp;
		private Timestamp inicio;
		
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
		public Timestamp getInicio() {
			return inicio;
		}
		public void setInicio(Timestamp inicio) {
			this.inicio = inicio;
		}
	}
	
	public static class Perigo implements Serializable{ 
		public Perigo(Camara camara, Vacina vacina, float temp, boolean ativo, Timestamp inicio) {
			super();
			this.camara = camara;
			this.vacina = vacina;
			this.temp = temp;
			this.ativo = ativo;
			this.inicio = inicio;
			this.duration = 0;
		}

		public Perigo(Camara camara, Vacina vacina, LeituraTemperatura leituraTemperatura, boolean ativo, Timestamp inicio){
			super();
			this.camara = camara;
			this.vacina = vacina;
			this.temp = leituraTemperatura.getTemp();
			this.leituraTemperatura = leituraTemperatura;
			this.ativo = ativo;
			this.inicio = inicio;
			this.duration = 0;
		}
		
		private Camara camara;
		private Vacina vacina;
		private float temp;
		private LeituraTemperatura leituraTemperatura;
		private boolean ativo;
		private Timestamp inicio;
		private long duration;

		public LeituraTemperatura getLeituraTemperatura(){
			return leituraTemperatura;
		}

		public void setLeituraTemperatura(LeituraTemperatura leituraTemperatura){
			this.leituraTemperatura = leituraTemperatura;
		}
		
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
		public Timestamp getInicio() {
			return inicio;
		}
		public void setInicio(Timestamp inicio) {
			this.inicio = inicio;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public Vacina getVacina() {
			return vacina;
		}
		public void setVacina(Vacina vacina) {
			this.vacina = vacina;
		}
	};
	
	public static class Alerta implements Serializable{
		
		public Alerta(Camara camara, Vacina vacina, Gerente gerente, float temp, boolean ativo, Timestamp inicio) {
			super();
			this.camara = camara;
			this.vacina = vacina;
			this.gerente = gerente;
			this.temp = temp;
			this.ativo = ativo;
			this.inicio = inicio;
		}

		public Alerta(Camara camara, Vacina vacina, Gerente gerente, LeituraTemperatura leituraTemperatura, boolean ativo, Timestamp inicio){
			super();
			this.camara = camara;
			this.vacina = vacina;
			this.gerente = gerente;
			this.temp = leituraTemperatura.getTemp();
			this.leituraTemperatura = leituraTemperatura;
			this.ativo = ativo;
			this.inicio = inicio;
		}
		
		private Camara camara;
		private Vacina vacina;
		private Gerente gerente;
		private float temp;
		private boolean ativo;
		private Timestamp inicio;
		private long duration;
		private LeituraTemperatura leituraTemperatura;

		public LeituraTemperatura getLeituraTemperatura(){
			return leituraTemperatura;
		}

		public void setLeituraTemperatura(LeituraTemperatura leituraTemperatura){
			this.leituraTemperatura = leituraTemperatura;
		}
		
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
		public Timestamp getInicio() {
			return inicio;
		}
		public void setInicio(Timestamp inicio) {
			this.inicio = inicio;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public Vacina getVacina() {
			return vacina;
		}
		public void setVacina(Vacina vacina) {
			this.vacina = vacina;
		}
		
	}
	
	public static class Descarte implements Serializable{
		
		public Descarte(Alerta alerta, Timestamp timestamp) {
			super();
			this.timestamp = timestamp;
			this.alerta = alerta;
		}
		
		private Timestamp timestamp;
		private Alerta alerta;
		
		public Timestamp getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		public Alerta getAlerta() {
			return alerta;
		}
		public void setAlerta(Alerta alerta) {
			this.alerta = alerta;
		}
		
	}

}
