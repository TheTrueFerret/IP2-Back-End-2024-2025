package kdg.be.backend.domain;

//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

//@Entity
@Getter
@Setter
public class GameSession {
    //@Id
//    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private LocalDateTime dateTime;

    public GameSession() {} // jpa

    public GameSession(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }


}
