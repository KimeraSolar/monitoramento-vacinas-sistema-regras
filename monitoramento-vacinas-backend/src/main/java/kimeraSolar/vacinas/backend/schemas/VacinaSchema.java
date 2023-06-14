package kimeraSolar.vacinas.backend.schemas;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class VacinaSchema {
    public VacinaSchema(String nome, float tempMax, float tempMin, Date abastecimento, String status) {
        super();
        this.name = nome;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.abastecimentoDate = abastecimento;
        this.vencimentoDate = DateUtils.addDays(abastecimento, 30);
        this.status = status;
    }
    
    public String name;
    public float tempMax;
    public float tempMin;
    public Date abastecimentoDate;
    public Date vencimentoDate;
    public String status;
    
}
