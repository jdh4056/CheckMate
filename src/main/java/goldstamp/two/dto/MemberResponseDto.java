// front + back/back/main/java/goldstamp/two/dto/MemberResponseDto.java
package goldstamp.two.dto;

import goldstamp.two.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MemberResponseDto {
    private Long id;
    private String loginId;
    private String name;
    private Gender gender;
    private LocalDate birthDay;
    private double height;
    private double weight;
}