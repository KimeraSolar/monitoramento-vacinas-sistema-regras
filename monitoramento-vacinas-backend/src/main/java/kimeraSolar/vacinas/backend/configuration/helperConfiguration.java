package kimeraSolar.vacinas.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class helperConfiguration {
    
    @Bean(name="applicationName")
    public String applicationName(){
        return "Sistema de Monitoramento de Vacinas";
    }
}
