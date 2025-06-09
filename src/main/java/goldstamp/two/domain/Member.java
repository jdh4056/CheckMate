// front + back/back/main/java/goldstamp/two/domain/Member.java
package goldstamp.two.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"prescriptions","memberRoleList"})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE) //DB만약에 MySQL이나 H2쓰면 SEQUNECE->IDENTITY
    @Column(name = "member_id")
    private long id;

    @Column(unique = true, nullable = false)
    private String loginId; // 이메일이나 사용자 ID

    private String name; //사용자 닉네임

    @JsonIgnore
    private String password; //사용자 비밀번호

    @ElementCollection(fetch = FetchType.LAZY) //권한 목록 (일반 사용자, 관리자 등)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private List<MemberRole> memberRoleList = new ArrayList<>();

    //권한 부여
    public void addRole(MemberRole role) {
        this.memberRoleList.add(role);
    }

    public void clearRole() {
        this.memberRoleList.clear();
    }

    public void changeName(String name) {
        this.name = name;
    }

    public void changePw(String pw) {
        this.password = pw;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    @Builder.Default // 이 줄을 추가합니다.
    private List<Prescription> prescriptions = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDay;

    private double height;

    private double weight;
}