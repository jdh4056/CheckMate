// front + back/back/main/java/goldstamp/two/security/CustomUserDetailsService.java
package goldstamp.two.security;

import goldstamp.two.domain.Member;
import goldstamp.two.dto.MemberDto;
import goldstamp.two.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

//로그인이 동작할 때 실행
@RequiredArgsConstructor
@Service
@Log4j2
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("------------------loadUserByUsername-------------------" + username);

        Member member = memberRepository.getWithRoles(username);

        if(member == null) {
            throw new UsernameNotFoundException("Not Found");
        }

        MemberDto memberDTO = new MemberDto(
                member.getId(), // id 필드 추가
                member.getLoginId(),
                member.getPassword(),
                member.getName(),
                member.getMemberRoleList() //
                        .stream() //
                        .map(memberRole -> memberRole.name()).collect(Collectors.toList())); //

        log.info(memberDTO);

        return memberDTO;

    }
}