package io.gitHub.KimeraSolar.MonitoramentoVacinas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

@SpringBootApplication
public class MonitoramentoVacinasDrools {

    public static final void main(String[] args) {
        SpringApplication.run(MonitoramentoVacinasDrools.class, args);
    }

    public static class Message {
        public static final int OHAYO   = 0;
        public static final int GOODMORNING = 1;

        private String message;

        private int status;

        public Message() {

        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus(final int status) {
            this.status = status;
        }

        public static Message doSomething(Message message) {
            return message;
        }

        public boolean isSomething(String msg,
                                   List<Object> list) {
            list.add( this );
            return this.message.equals( msg );
        }
    }

}
