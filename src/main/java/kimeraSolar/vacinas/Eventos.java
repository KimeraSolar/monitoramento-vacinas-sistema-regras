package kimeraSolar.vacinas;

import java.util.Date;

public class Eventos {
	
	public static class LeituraTemperatura{
		public LeituraTemperatura(Camara camara, float temp, Date inicio) {
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
		public Perigo(Camara camara, Vacina vacina, float temp, boolean ativo, Date inicio) {
			super();
			this.camara = camara;
			this.vacina = vacina;
			this.temp = temp;
			this.ativo = ativo;
			this.inicio = inicio;
			this.duration = 0;
		}
		
		private Camara camara;
		private Vacina vacina;
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
		public Vacina getVacina() {
			return vacina;
		}
		public void setVacina(Vacina vacina) {
			this.vacina = vacina;
		}
	};
	
	public static class Alerta{
		
		public Alerta(Camara camara, Vacina vacina, Gerente gerente, float temp, boolean ativo, Date inicio) {
			super();
			this.camara = camara;
			this.vacina = vacina;
			this.gerente = gerente;
			this.temp = temp;
			this.ativo = ativo;
			this.inicio = inicio;
		}
		
		private Camara camara;
		private Vacina vacina;
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
		public Vacina getVacina() {
			return vacina;
		}
		public void setVacina(Vacina vacina) {
			this.vacina = vacina;
		}
		
	}
	
	public static class Descarte{
		
		public Descarte(Alerta alerta, Date timestamp) {
			super();
			this.timestamp = timestamp;
			this.alerta = alerta;
		}
		
		private Date timestamp;
		private Alerta alerta;
		
		public Date getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(Date timestamp) {
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
