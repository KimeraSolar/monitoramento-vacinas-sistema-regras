package kimeraSolar.vacinas.backend.configuration;

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Configuration;

@Configuration
public class SensorsConfiguration {
    private List<Thread> sensorWrappers = new LinkedList<Thread>();

    public void addSensorWrapper(Thread sensorWrapperThread){
        sensorWrappers.add(sensorWrapperThread);
    }

    public List<Thread> getSensorWrappers(){
        return sensorWrappers;
    }
}
