package kdg.be.backend.service.dto;

import lombok.Getter;

@Getter
public class CheckResult {
    private boolean isValid;
    private String message;

    public CheckResult(boolean isValid, String message) {
        this.isValid = isValid;
        this.message = message;
    }

}