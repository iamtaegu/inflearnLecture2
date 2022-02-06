package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
       http
                .csrf().disable()
                .headers().frameOptions().disable()//h2-console 화면을 사용하기 위해 해당 옵션 disable
                .and()
                .authorizeRequests()//URI 별 권한 관리 설정
                .antMatchers("*").permitAll()
                /*.antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.GUEST.name())
                .anyRequest().authenticated() // 설정된 값들 이외의 URI*/
                .and()
                .logout()//로그아웃 설정 진입점
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint()//OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 담당
                .userService(customOAuth2UserService); //로그인 성공 시 후속 조치를 진행할 UserService인터페이스 구현체 등록
    }
}