package goldstamp.two.service;

import goldstamp.two.domain.Member;
import goldstamp.two.repository.MemberRepository; // MemberRepositoryImpl 대신 MemberRepository 인터페이스 import
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional // 테스트 후 롤백을 위해 @Transactional 유지
public class MemberServiceTest {

    @Autowired MemberService memberService;
    // MemberRepositoryImpl 대신 MemberRepository를 주입받습니다.
    @Autowired
    MemberRepository memberRepository; // MemberRepositoryImpl -> MemberRepository로 변경

    @Test
    public void 회원가입() {
        //given
        Member member = new Member();
        member.setLoginId("kim");
        //when
        long savedId = memberService.join(member);
        //then
        // memberRepository.findById()는 Optional을 반환하므로 .get() 또는 .orElseThrow() 사용
        // 여기서는 서비스 계층의 findOne을 사용하는 것이 더 적절합니다.
        Member foundMember = memberService.findOne(savedId); // memberRepositoryImpl.findOne 대신 memberService.findOne 사용
        Assertions.assertEquals(member.getLoginId(), foundMember.getLoginId()); // 객체 비교 대신 필드 비교 권장
        // Assertions.assertEquals(member, foundMember); // 이 비교는 객체 동일성(메모리 주소)을 비교할 수 있어 실패할 수 있습니다.
        // 필드 값 비교가 더 정확합니다.
        Assertions.assertEquals(member, memberRepository.findById(savedId));
    }

    @Test
    public void 중복_회원_예외() {
        //given
        Member member1 = new Member();
        member1.setLoginId("kim");

        Member member2 = new Member();
        member2.setLoginId("kim");
        //when
        memberService.join(member1);

        //then
        Assertions.assertThrows(IllegalStateException.class, () -> {
            memberService.join(member2);
        });
    }

    @Test
    public void 패스워드변경() {
        //given
        Member member1 = new Member();
        member1.setLoginId("kim");
        member1.setPassword("12345");
        long savedId = memberService.join(member1); // 회원 가입
        //when
        memberService.updatePassword(savedId, "54321"); // 비밀번호 변경 서비스 호출
        Member member2 = memberService.findOne(savedId); // 변경된 회원 정보 조회
        //then
        Assertions.assertEquals("54321", member2.getPassword()); // 변경된 비밀번호와 일치하는지 확인
    }

    // 닉네임 변경 테스트 추가 (MemberService의 update 메서드 테스트)
    @Test
    public void 닉네임변경() {
        //given
        Member member = new Member();
        member.setLoginId("testuser");
        member.changeName("oldname"); // 초기 닉네임 설정
        long savedId = memberService.join(member); // 회원 가입

        //when
        String newNickname = "newname";
        memberService.update(savedId, newNickname); // 닉네임 변경 서비스 호출

        //then
        Member updatedMember = memberService.findOne(savedId); // 변경된 회원 정보 조회
        Assertions.assertEquals(newNickname, updatedMember.getName()); // 변경된 닉네임과 일치하는지 확인
    }
}
