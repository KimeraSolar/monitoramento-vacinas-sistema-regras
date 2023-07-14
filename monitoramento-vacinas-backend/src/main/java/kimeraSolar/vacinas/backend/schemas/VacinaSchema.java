package kimeraSolar.vacinas.backend.schemas;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

public class VacinaSchema implements Serializable{
    public VacinaSchema(String name, double tempMin, double tempMax, long tempoDescarte, Date abastecimento, String status) {
        super();
        this.name = name;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.tempoDescarte = tempoDescarte;
        this.abastecimentoDate = abastecimento;
        this.vencimentoDate = DateUtils.addDays(abastecimento, 30);
        this.status = status;
    }

    public VacinaSchema(String name, double tempMin, double tempMax, long tempoDescarte, Date abastecimento, Date vencimento, String status) {
        super();
        this.name = name;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.tempoDescarte = tempoDescarte;
        this.abastecimentoDate = abastecimento;
        this.vencimentoDate = vencimento;
        this.status = status;
    }

    public VacinaSchema(String name, double tempMin, double tempMax, long tempoDescarte, Date abastecimento, Date vencimento) {
        super();
        this.name = name;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.tempoDescarte = tempoDescarte;
        this.abastecimentoDate = abastecimento;
        this.vencimentoDate = vencimento;
    }

    public VacinaSchema(){
        this.name = null;
        this.tempMax = 0;
        this.tempMin = 0;
        this.tempoDescarte = 0;
        this.abastecimentoDate = null;
        this.vencimentoDate = null;
        this.status = null;
    };
    
    public String name;
    public double tempMin;
    public double tempMax;
    public long tempoDescarte;
    public Date abastecimentoDate;
    public Date vencimentoDate;
    public String status;

    public static class TipoSchema implements Serializable{
        public TipoSchema(String name, double tempMin, double tempMax, long tempoDescarte){
            this.name = name;
            this.tempMax = tempMax;
            this.tempMin = tempMin;
            this.tempoDescarte = tempoDescarte;
        }
        public String name;
        public double tempMin;
        public double tempMax;
        public long tempoDescarte;
    }
    
}
