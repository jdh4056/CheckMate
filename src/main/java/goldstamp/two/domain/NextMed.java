package goldstamp.two.domain;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class NextMed {

    private String nextDisease;

    private int duringDay;

    public NextMed() {}

    public NextMed(String nextDisease, int duringDay) {
        this.nextDisease = nextDisease;
        this.duringDay = duringDay;
    }
}
