package goldstamp.two.controller;
import goldstamp.two.domain.Member;
import goldstamp.two.dto.MemberRequestDto;
import goldstamp.two.dto.MemberResponseDto;
import goldstamp.two.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService; //서비스 계층을 통해 비즈니스 로직 수행

    //회원 등록
    @Data
    public class CreateMemberResponse {
        private Long id;

        // Long id를 받는 생성자만 정의
        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }

    @PostMapping("/members")
    public CreateMemberResponse saveMember(@RequestBody @Valid MemberRequestDto request) {
        Member member = new Member();
        member.setLoginId(request.getLoginId());
        member.setName(request.getName());
        member.setPassword(request.getPassword());
        member.setGender(request.getGender());
        member.setBirthDay(request.getBirthDay());
        member.setHeight(request.getHeight());
        member.setWeight(request.getWeight());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    //회원 수정
    @PatchMapping("/members/{id}")
    public MemberResponseDto updateMemberResponse(
            @PathVariable("id") Long id,
            @RequestBody @Valid MemberRequestDto request) {
        memberService.updateMember(id, request);
        Member findMember = memberService.findOne(id);
        return new MemberResponseDto(
                findMember.getId(),
                findMember.getLoginId(),
                findMember.getName(),
                findMember.getGender(),
                findMember.getBirthDay(),
                findMember.getHeight(),
                findMember.getWeight()
        );
    }
    //회원 조회
    @GetMapping("/members")
    public Result members() {
        List<Member> findMembers = memberService.findMembers();
        List<MemberResponseDto> collect = findMembers.stream()
                .map(m -> new MemberResponseDto(
                        m.getId(),
                        m.getLoginId(),
                        m.getName(),
                        m.getGender(),
                        m.getBirthDay(),
                        m.getHeight(),
                        m.getWeight()))
                .collect(Collectors.toList());
        return new Result(collect);
    }

    //응답 데이터를 감싸는 wrapper
    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
}
