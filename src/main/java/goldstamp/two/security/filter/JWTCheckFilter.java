// front + back/back/main/java/goldstamp/two/security/filter/JWTCheckFilter.java
package goldstamp.two.security.filter;

import com.google.gson.Gson;
import goldstamp.two.dto.MemberDto;
import goldstamp.two.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;


@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        log.info("check uri----------"+path);

        // `/members/login`, `/members` (회원가입), `/members/{id}/refresh` 경로를 필터링 건너뛰도록 설정
        // `members/**`는 `/members/`로 시작하는 모든 경로를 포함합니다.
        if(path.startsWith("/members/login") || path.equals("/members") || path.startsWith("/members/") && path.endsWith("/refresh") ){
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal (HttpServletRequest request, HttpServletResponse
            response, FilterChain filterChain) throws ServletException, IOException {
        log.info("------------------JWTCheckFilter.................");
        String authHeaderStr = request.getHeader("Authorization");

        try {
            //Bearer accestoken...
            String accessToken = authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.validateToken(accessToken);

            log.info("JWT claims: " + claims);

            Long id = ((Number) claims.get("id")).longValue();
            String loginId = (String) claims.get("loginId");
            String password = (String) claims.get("password");
            String name = (String) claims.get("name");
            List<String> roleNames = (List<String>) claims.get("roleNames");
            MemberDto memberDTO = new MemberDto(id, loginId, password, name,
                    roleNames);
            log.info("-----------------------------------");
            log.info(memberDTO);
            log.info(memberDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(memberDTO, password, memberDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            log.error("JWT Check Error..............");
            log.error(e.getMessage());
            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
}