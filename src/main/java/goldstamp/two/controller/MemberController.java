package goldstamp.two.controller;
import goldstamp.two.domain.Member;
import goldstamp.two.domain.MemberRole;
import goldstamp.two.dto.LoginRequestDto;
import goldstamp.two.dto.MemberDto;
import goldstamp.two.dto.MemberRequestDto;
import goldstamp.two.dto.MemberResponseDto;
import goldstamp.two.service.MemberService;
import goldstamp.two.util.CustomJWTException;
import goldstamp.two.util.JWTUtil;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;

    @Data
    public class CreateMemberResponse {
        private Long id;

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
        member.addRole(MemberRole.USER); // 회원가입 시 기본 USER 역할 부여
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PostMapping("/members/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDto loginRequest) {
        log.info("로그인 시도: " + loginRequest.getLoginId());
        try {
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getLoginId(), loginRequest.getPassword());

            Authentication authentication = authenticationManager.authenticate(authenticationToken);

            log.info("로그인 성공: " + authentication);

            MemberDto memberDTO = (MemberDto) authentication.getPrincipal();

            Map<String, Object> claims = memberDTO.getClaims();

            String accessToken = JWTUtil.generateToken(claims, 10);
            String refreshToken = JWTUtil.generateToken(claims, 60 * 24);

            claims.put("accessToken", accessToken);
            claims.put("refreshToken", refreshToken);

            return ResponseEntity.ok(claims);

        } catch (AuthenticationException e) {
            log.error("로그인 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "ERROR_LOGIN"));
        } catch (Exception e) {
            log.error("로그인 중 서버 오류: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "INTERNAL_SERVER_ERROR"));
        }
    }

    // Refresh Token 발급 엔드포인트 변경
    @RequestMapping("/members/{id}/refresh") // 경로 변경
    public Map<String, Object> refresh (
            @PathVariable("id") Long memberIdFromPath, // 경로 변수로 멤버 ID 받기
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Map<String, String> refreshRequestBody
    ) {
        String refreshToken = refreshRequestBody.get("refreshToken");

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID STRING");
        }

        String accessToken = authHeader.substring(7);

        // AccessToken의 만료여부 확인 (만료되지 않았다면 바로 반환)
        if (checkExpiredToken(accessToken) == false) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // Refresh토큰 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        // JWT 클레임에서 'id'를 추출하여 경로의 'id'와 비교하는 강력한 검증 로직
        Long memberIdFromClaims = ((Number) claims.get("id")).longValue(); // 클레임에서 ID 추출

        if (!memberIdFromPath.equals(memberIdFromClaims)) {
            // 경로의 ID와 토큰 클레임의 ID가 일치하지 않는 경우
            throw new CustomJWTException("MEMBER_ID_MISMATCH: Provided ID in path does not match token ID.");
        }

        log.info("refresh ... claims: " + claims);

        String newAccessToken = JWTUtil.generateToken(claims, 10);

        // 리프레시 토큰의 만료 시간이 1시간 미만으로 남았으면 새로운 리프레시 토큰 생성
        String newRefreshToken = checkTime((Integer) claims.get("exp")) ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    // 시간이 1시간 미만으로 남았다면
    private boolean checkTime(Integer exp) {
        Date expDate = new Date((long)exp * (1000 ));
        long gap = expDate.getTime() - System.currentTimeMillis();
        long leftMin = gap / (1000 * 60);
        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {
        try{
            JWTUtil.validateToken(token);
            return false;
        } catch(CustomJWTException ex) {
            if(ex.getMessage().equals("Expired")){
                return true;
            }
            throw ex;
        } catch(Exception e){
            throw new CustomJWTException("Error checking token expiration");
        }
    }

    @PatchMapping("/members/{id}")
    public MemberResponseDto updateMember(
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

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }
    @DeleteMapping("/members/{id}")
    public ResponseEntity<String> deleteMember(
            @PathVariable("id") Long memberIdToDelete,
            @AuthenticationPrincipal MemberDto currentUser // 현재 로그인한 사용자 정보 (JWT 필터에서 SecurityContextHolder에 저장된 정보)
    ) {
        // 1. 요청을 보낸 사용자가 해당 멤버 ID의 소유자인지 확인 (보안 검증)
        // JWT의 'id' 클레임과 URL 경로의 'id'가 일치하는지 확인합니다.
        if (!currentUser.getId().equals(memberIdToDelete)) {
            // 본인 계정이 아닌 다른 계정 삭제 시도 시 403 Forbidden 반환
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this member.");
        }

        try {
            memberService.deleteMember(memberIdToDelete);
            log.info("Member deleted successfully: ID={}", memberIdToDelete);
            return ResponseEntity.ok("Member with ID " + memberIdToDelete + " has been deleted successfully.");
            // 혹은 204 No Content를 반환하여 성공을 알릴 수도 있습니다.
            // return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            log.warn("Member deletion failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error deleting member with ID {}: {}", memberIdToDelete, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during member deletion.");
        }
    }
}