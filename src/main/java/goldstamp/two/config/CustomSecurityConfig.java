package goldstamp.two.config;

import goldstamp.two.security.filter.JWTCheckFilter;
import goldstamp.two.security.handler.CustomAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
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

    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        log.info("----------------security config-------------------");

        http.cors(httpSecurityCorsConfigurer-> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable());

        // http.formLogin 제거
        // http.formLogin(config -> {
        //     config.loginPage("/api/member/login");
        //     config.successHandler(new APILoginSuccessHandler());
        //     config.failureHandler(new APILoginFailHandler());
        // });

        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(config ->{

            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
        http.authorizeHttpRequests(authorize -> authorize
                // 로그인 및 회원가입 관련 경로들은 인증 없이도 접근 가능
                // .requestMatchers("/api/member/login").permitAll() // /api/member 경로 제거
                .requestMatchers("/members").permitAll() // 회원가입 POST 요청
                .requestMatchers("/members/login").permitAll() // 통합된 로그인 엔드포인트 허용
                .requestMatchers("/").permitAll() // 루트 경로 (HomeController의 home())
                .requestMatchers("/error").permitAll() // 에러 페이지

                // 특정 역할만 접근 가능한 경로 추가 예정
                .requestMatchers("/admin/**").hasRole("ADMIN") // /admin 으로 시작하는 모든 요청은 ADMIN 권한 필요

                // 그 외 모든 요청은 인증된 사용자만 접근 가능하도록 설정
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT","DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control","Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}