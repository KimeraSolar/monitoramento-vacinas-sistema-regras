package kimeraSolar.vacinas.services.configurations.RegrasMonitoramento;

import org.springframework.context.annotation.Configuration;

@Configuration
public class PkgNameProvider {

    private String pkgName = "kimeraSolar.vacinas.rules";

    public String getPkgName (){
        return pkgName;
    } 

    public void setPkgName(String pkgName){
        this.pkgName = pkgName;
    }
    
}
