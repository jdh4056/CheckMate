package goldstamp.two.controller;

import goldstamp.two.util.CustomJWTException;
import goldstamp.two.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh (
            @RequestHeader("Authorization") String authHeader,
            String refreshToken
    ) {
        if (authHeader == null) {
            throw new CustomJWTException("NULL_REFRESH");
        }

        if (authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID STRING");
        }

        //Bearer xxx
        String accessToken = authHeader.substring(7);
        //AccessToken의 만료여부 확인
        if (checkExpiredToken(accessToken) == false) { //만료가 되지 않은 토큰 -> 원래대로 엑세스 토큰과 리프레시 토큰을 가지고 방출
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }
        //Refresh토큰 검증
        Map<String, Object> claims = JWTUtil.validateToken(refreshToken);

        log.info("refresh ... claims: " + claims);

        String newAccessToken = JWTUtil.generateToken(claims, 10);

        String newRefreshToken = checkTime((Integer) claims.get("exp")) == true ? JWTUtil.generateToken(claims, 60 * 24) : refreshToken; //시간이 얼마 안남음 -> refresh토큰 생성

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

//시간이 1시간 미만으로 남았다면
        private boolean checkTime(Integer exp) {
//JWT exp를 날짜로 변환
        java.util.Date expDate = new java.util.Date( (long)exp * (1000 ));
//현재 시간과의 차이 계산 - 밀리세컨즈
        long gap = expDate.getTime() - System.currentTimeMillis();
//분단위 계산
        long leftMin = gap / (1000 * 60);
//1시간도 안남았는지..
            return leftMin < 60;
        }
        private boolean checkExpiredToken(String token) {
            try{
                JWTUtil.validateToken(token);
            }catch(CustomJWTException ex) {
                if(ex.getMessage().equals("Expired")){ //만료 메세지
                    return true;
                }
            }
            return false;
        }
    }

