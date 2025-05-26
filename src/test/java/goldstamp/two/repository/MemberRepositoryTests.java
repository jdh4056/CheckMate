package goldstamp.two.repository;


import goldstamp.two.domain.Member;
import goldstamp.two.domain.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.extern.log4j.*; // Log4j2 임포트
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Log4j2
@Transactional
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; //똑같은 password이지만 생성되는 문자의 값이 다름.

    @Test
    public void testInsertMember() {
        for (int i = 0; i < 10 ; i++) {

            Member member = Member.builder()
                    .loginId("user"+i+"@aaa.com")
                    .password( passwordEncoder.encode("1111"))
                    .name("USER"+i)
                    .build();
            member.addRole(MemberRole.USER);

            if(i > 5) {
                member.addRole(MemberRole.MANAGER);
            }
            if(i >= 8){
                member.addRole(MemberRole.ADMIN);
            }

            memberRepository.save(member);
        }
    }
    @Test
    public void testRead() {

        String email = "user9@aaa.com";

        Member member = memberRepository.getWithRoles(email); //자동으로 조인처리가 되었는지 확인

        log.info("-----------------");
        log.info(member);
        log.info(member.getMemberRoleList());
    }


}
