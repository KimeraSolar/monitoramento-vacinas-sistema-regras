package kimeraSolar.vacinas.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class helloController {
    
    @GetMapping("/hello")
    public String hello(){
        return "<h1>Ohayo Sekai</h1><hr><p>Good morning World!</p>";
    }
}
