package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "medicine_id")
    private long id;
    // 약에서 딱히 참조할 이유 없다
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
