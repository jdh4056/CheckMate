package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Disease {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "disease_id")
    private long id;

    @JsonIgnore
    @OneToOne(mappedBy = "disease", fetch = FetchType.LAZY)
    private Prescription prescription;

    @Column(length = 1000)
    private String name;

<<<<<<< HEAD
    @Column(name = "description")
    private String explain; //MySQL에서 예약어라서 컬럼명으로 쓰면 안 됨
=======
    @Column(length = 3000)
    private String explain;
>>>>>>> upstream/main
}
