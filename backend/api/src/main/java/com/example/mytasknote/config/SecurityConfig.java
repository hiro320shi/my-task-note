package com.example.mytasknote.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 開発中はとりあえず CSRF 無効（あとでちゃんと考える）
                .csrf(AbstractHttpConfigurer::disable)
                // URL ごとのアクセス制御
                .authorizeHttpRequests(auth -> auth
                        // ヘルスチェックは誰でもOK
                        .requestMatchers("/actuator/health").permitAll()
                        // ユーザ登録は認証不要
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        // 他は一旦全部許可（後で認証必須に変える）
                        .anyRequest().permitAll()
                )
                // フォームログイン / Basic 認証は今回使わないので無効化
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

}