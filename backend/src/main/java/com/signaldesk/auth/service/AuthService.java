package com.signaldesk.auth.service;

import com.signaldesk.auth.dto.LoginRequest;
import com.signaldesk.auth.dto.RegisterRequest;
import com.signaldesk.auth.dto.TokenResponse;
import com.signaldesk.infrastructure.exception.BusinessException;
import com.signaldesk.infrastructure.exception.ErrorCode;
import com.signaldesk.user.domain.User;
import com.signaldesk.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final long accessTokenExpiration;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       @Value("${jwt.access-token-expiration:86400000}") long accessTokenExpiration) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.accessTokenExpiration = accessTokenExpiration;
    }

    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_TAKEN);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_TAKEN);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getUsername())
                .build();

        userRepository.save(user);
        return buildTokenResponse(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return buildTokenResponse(user);
    }

    private TokenResponse buildTokenResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getUsername());

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000)
                .user(TokenResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .displayName(user.getDisplayName())
                        .build())
                .build();
    }
}
