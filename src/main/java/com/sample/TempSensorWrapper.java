package com.sample;

import java.util.Random;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

public class TempSensorWrapper implements Runnable {

	KieSession kSession;
	FactHandle fact;
	Random rand;
	String sensorId;
	
	public TempSensorWrapper(String id, KieSession k, FactHandle f) {
		super();
		sensorId = id;
		kSession = k;
		fact = f;
		rand = new Random();
	}
	
	@Override
	public void run() {
		while(true) {
			try {	
				Camara c = (Camara) kSession.getObject(fact);
				c.setTemp(getSensorValue());
				System.out.println("Temperatura de " + c.getObjectId() + ": " + c.getTemp());
				kSession.update(fact, c);
				kSession.fireAllRules();
				
				Thread.sleep(1000*rand.nextInt(10)+1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public float getSensorValue() {
		Camara c = (Camara) kSession.getObject(fact);
		float val = c.getTemp();
		if (c.isAtiva()) {
			float var_min = (float) -0.5, var_max = (float) 0.5;
			int min = -10, max = 15;
			val = val + (float) (var_min + rand.nextDouble() * (var_max - var_min));
			val = Math.max(min, Math.min(max, val));
		} else {
			val = 0;
		}
		return val;
	}
}
