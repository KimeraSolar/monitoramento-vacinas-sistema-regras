package io.gitHub.KimeraSolar.MonitoramentoVacinas;

import org.kie.api.runtime.KieSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.gitHub.KimeraSolar.MonitoramentoVacinas.MonitoramentoVacinasDrools.Message;


@RestController
public class DecisionController {
    private final KieSession kieSession;

    public DecisionController(KieSession kieSession) {
        this.kieSession = kieSession;
    }

    @PostMapping("/mensagem")
    private Message getDiscountPercent(@RequestBody Message message) {
        kieSession.insert(message);
        kieSession.fireAllRules();
        return message;
    }
}
