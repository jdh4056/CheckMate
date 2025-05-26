package goldstamp.two.dto;

import goldstamp.two.domain.Gender;
import lombok.Data;

import java.time.LocalDate;
@Data
public class MemberRequestDto {
    private String loginId;
    private String name;
    private String password;
    private Gender gender;
    private LocalDate birthDay;
    private double height;
    private double weight;
}
