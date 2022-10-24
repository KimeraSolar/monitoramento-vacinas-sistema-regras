package kimeraSolar.vacinas.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/help")
public class helperController {

    @Autowired
    @Qualifier("applicationName")
    private String applicationName;
    
    @GetMapping("/about")
    public String aboutApplication(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                    .append("<h1>")
                    .append(applicationName)
                    .append("</h1>")
                    .append("<hr>");
        return stringBuilder.toString();
    }
}
