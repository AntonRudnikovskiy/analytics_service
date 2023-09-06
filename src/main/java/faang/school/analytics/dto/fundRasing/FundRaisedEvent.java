package faang.school.analytics.dto.fundRasing;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FundRaisedEvent {
    private long donorId;
    private long projectId;
    @Pattern(regexp = "[0-9]+")
    private String amount;
    private LocalDateTime timestamp;
}
