package com.example.mytasknote.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.mytasknote.auth.jwt.JwtService;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // ★ ログインやユーザ登録、ヘルスチェックは JWT チェックをスキップ
        if (path.equals("/api/login")
                || (path.equals("/api/users") && "POST".equals(request.getMethod()))
                || path.equals("/actuator/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // JWT なし → SecurityConfig 側で 401/403 にされる
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.validateAndGetSubject(token);

            // ★ 認証済みとして SecurityContext に載せる
            var authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    // ★ 今後ロールを使うなら ROLE_USER を1つ付けておく
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
            // トークン不正 → 認証情報なしのまま次へ
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}