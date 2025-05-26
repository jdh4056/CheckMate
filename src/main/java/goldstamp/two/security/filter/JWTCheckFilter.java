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

    //예외 경로로 들어오면 filtering 안함. 예 : 로그인 화면
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        //true == not checking

        String path = request.getRequestURI();

        log.info("check uri----------"+path);

        if(path.startsWith("/api/member/")){
            return true;
        }

        //flase == checking
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

                    //filterChain.doFilter(request, response);

                    String loginId = (String) claims.get("loginId");
                    String password = (String) claims.get("password");
                    String name = (String) claims.get("name");
                    Boolean social = (Boolean) claims.get("social");
                    List<String> roleNames = (List<String>) claims.get("roleNames");
                    MemberDto memberDTO = new MemberDto(loginId, password, name, social.booleanValue(),
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




