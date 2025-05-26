
package goldstamp.two.config;

import goldstamp.two.security.filter.JWTCheckFilter;
import goldstamp.two.security.handler.APILoginFailHandler;
import goldstamp.two.security.handler.APILoginSuccessHandler;
import goldstamp.two.security.handler.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity
public class CustomSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("----------------security config-------------------");

        http.cors(httpSecurityCorsConfigurer-> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
                });

        //아래의 cors기능 쓰기
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        http.formLogin(config -> {
            config.loginPage("/api/member/login");
            config.successHandler(new APILoginSuccessHandler());
            config.failureHandler(new APILoginFailHandler());
        });

        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(config ->{

            config.accessDeniedHandler(new CustomAccessDeniedHandler());
                });
//        http.authorizeHttpRequests(authorize -> authorize
//                // 로그인 및 회원가입 관련 경로들은 인증 없이도 접근 가능
//                .requestMatchers("/api/member/login").permitAll() // 로그인 POST 요청
//                .requestMatchers("/api/member/join").permitAll() // 회원가입 POST 요청
//                .requestMatchers("/").permitAll() // 루트 경로 (HomeController의 home())
//                .requestMatchers("/error").permitAll() // 에러 페이지
//
//                // 특정 역할만 접근 가능한 경로 추가 예정
//                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin 으로 시작하는 모든 요청은 ADMIN 권한 필요
//
//                // 그 외 모든 요청은 인증된 사용자만 접근 가능하도록 설정
//                .anyRequest().authenticated()
//        );

        //csrf 기능 x
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    //사용자 계정 암호화

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //CORS 정책을 정의하는 데 사용. e.g 어떤 도메인에서 요청을 허용 / 어떤 HTTP method 허용
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        //모든 출처(도메인)을 허용 나중에 확인 필요
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT","DELETE"));
        //허용할 HTTP 메서드 설정 / cleint의 Put 요청 범위
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control","Content-Type"));
        //허용할 HTTP 헤더 설정.
        configuration.setAllowCredentials(true);
        //쿠키 및 인증 정보 포함한 요청 가능.
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //URL 패턴에 대한 CORS 설정 등록
        source.registerCorsConfiguration("/**", configuration);
        //모든 URL 경로 /**dp eogo configuration 적용
        return source;
        //객체 반환



    }
}


