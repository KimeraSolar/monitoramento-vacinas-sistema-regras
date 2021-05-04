package com.sample;

import java.util.Date;
import java.util.Random;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class TempSensorWrapper implements Runnable {

	KieSession kSession;
	FactHandle fact;
	Random rand;
	String sensorId;
	String opMode;
	SensorBehaviour sensor;
	
	public TempSensorWrapper(String id, KieSession k, FactHandle f, String mode) {
		super();
		sensorId = id;
		kSession = k;
		fact = f;
		rand = new Random();
		this.setOpMode(mode);
		
		int min = 0, max = 10;
		Camara c = (Camara) kSession.getObject(fact);
		c.setTemp(min + rand.nextFloat()*(max - min));
	}
	
	@Override
	public void run() {
		while(true) {
			try {	
				Camara c = (Camara) kSession.getObject(fact);
				if(c.isAtiva()) {
					c.setTemp(sensor.getSensorValue());
					kSession.update(fact, c);
					kSession.insert( new Eventos.LeituraTemperatura(c, c.getTemp(), new Date()) );
				}
				System.out.println("Temperatura de " + c.getObjectId() + ": " + c.getTemp());
				kSession.fireAllRules();
				
				Thread.sleep(1000*rand.nextInt(10)+1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getOpMode() {
		return opMode;
	}

	public void setOpMode(String opMode) {
		this.opMode = opMode;
		if (opMode == "smooth") {
			sensor = new SmoothRandomSensor((float)-10, (float)15, rand, kSession, fact);
		}else if(opMode == "increasing") {
			sensor = new IncreasingSensor((float)20, kSession, fact);
		}else if(opMode == "decreasing") {
			sensor = new DecreasingSensor((float)-15, kSession, fact);
		}else {
			sensor = new RandomSensor((float)-10, (float)15, rand);
		}
	}
	
	public static interface SensorBehaviour{
		public float getSensorValue();
	}
	
	public static class RandomSensor implements SensorBehaviour{
		public RandomSensor(float maxValue, float minValue, Random rand) {
			super();
			this.maxValue = maxValue;
			this.minValue = minValue;
			this.rand = rand;
		}
		
		private float maxValue;
		private float minValue;
		private Random rand;
		
		public float getSensorValue() {
			float val = (minValue + rand.nextFloat() * (maxValue - minValue));
			return val;
		}
		public float getMaxValue() {
			return maxValue;
		}
		public void setMaxValue(float maxValue) {
			this.maxValue = maxValue;
		}
		public float getMinValue() {
			return minValue;
		}
		public void setMinValue(float minValue) {
			this.minValue = minValue;
		}
		public Random getRand() {
			return rand;
		}
		public void setRand(Random rand) {
			this.rand = rand;
		}
	}
	
	public static class DecreasingSensor implements SensorBehaviour{
		public DecreasingSensor(float minValue, KieSession kSession, FactHandle fact) {
			super();
			this.minValue = minValue;
			this.kSession = kSession;
			this.fact = fact;
		}

		private float minValue;
		private KieSession kSession;
		private FactHandle fact;
	
		public float getSensorValue() {
			Camara c = (Camara) kSession.getObject(fact);
			float val = c.getTemp();
			if (c.isAtiva()) {
				val = val - (float) 0.5;
				val = Math.max(val, minValue);
			}
			return val;
		}

		public float getMinValue() {
			return minValue;
		}

		public void setMinValue(float minValue) {
			this.minValue = minValue;
		}

		public KieSession getkSession() {
			return kSession;
		}

		public void setkSession(KieSession kSession) {
			this.kSession = kSession;
		}

		public FactHandle getFact() {
			return fact;
		}

		public void setFact(FactHandle fact) {
			this.fact = fact;
		}
	}
	
	public static class IncreasingSensor implements SensorBehaviour{
		public IncreasingSensor(float maxValue, KieSession kSession, FactHandle fact) {
			super();
			this.maxValue = maxValue;
			this.kSession = kSession;
			this.fact = fact;
		}

		private float maxValue;
		private KieSession kSession;
		private FactHandle fact;
		
		public float getSensorValue() {
			Camara c = (Camara) kSession.getObject(fact);
			float val = c.getTemp();
			if (c.isAtiva()) {
				val = val + (float) 0.5;
				val = Math.min(val, maxValue);
			}
			return val;
		}

		public float getMaxValue() {
			return maxValue;
		}

		public void setMaxValue(float maxValue) {
			this.maxValue = maxValue;
		}

		public KieSession getkSession() {
			return kSession;
		}

		public void setkSession(KieSession kSession) {
			this.kSession = kSession;
		}

		public FactHandle getFact() {
			return fact;
		}

		public void setFact(FactHandle fact) {
			this.fact = fact;
		}
	}
	
	public static class SmoothRandomSensor implements SensorBehaviour{
		public SmoothRandomSensor(float minValue, float maxValue, Random rand, KieSession kSession, FactHandle fact) {
			super();
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.rand = rand;
			this.kSession = kSession;
			this.fact = fact;
		}

		private float minValue;
		private float maxValue;
		private Random rand;
		private KieSession kSession;
		private FactHandle fact;
		
		public float getSensorValue() {
			Camara c = (Camara) kSession.getObject(fact);
			float val = c.getTemp();
			if (c.isAtiva()) {
				float var_min = (float) -0.5, var_max = (float) 0.5;
				val = val + (var_min + rand.nextFloat() * (var_max - var_min));
				val = Math.max(minValue, Math.min(maxValue, val));
			} else {
				val = 0;
			}
			return val;
		}

		public float getMinValue() {
			return minValue;
		}

		public void setMinValue(float minValue) {
			this.minValue = minValue;
		}

		public float getMaxValue() {
			return maxValue;
		}

		public void setMaxValue(float maxValue) {
			this.maxValue = maxValue;
		}

		public Random getRand() {
			return rand;
		}

		public void setRand(Random rand) {
			this.rand = rand;
		}

		public KieSession getkSession() {
			return kSession;
		}

		public void setkSession(KieSession kSession) {
			this.kSession = kSession;
		}

		public FactHandle getFact() {
			return fact;
		}

		public void setFact(FactHandle fact) {
			this.fact = fact;
		}
	}

}
