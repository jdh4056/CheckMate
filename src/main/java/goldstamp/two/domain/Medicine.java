package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "medicine_id")
    private long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    @Column(length = 1000)
    private String medicineName;
    @Column(length = 1000)
    private String efficient;
    @Column(length = 1000)
    private String useMethod;
    @Column(length = 1000)
    private String acquire;
    @Column(length = 1500)
    private String warning;
}
