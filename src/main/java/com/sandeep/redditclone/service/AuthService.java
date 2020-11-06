package com.sandeep.redditclone.service;

import com.sandeep.redditclone.dto.AuthenticationResponse;
import com.sandeep.redditclone.dto.LoginRequest;
import com.sandeep.redditclone.dto.RefreshTokenRequest;
import com.sandeep.redditclone.dto.RegisterRequest;
import com.sandeep.redditclone.exceptions.SpringRedditException;
import com.sandeep.redditclone.model.NotificationEmail;
import com.sandeep.redditclone.model.User;
import com.sandeep.redditclone.model.VerificationToken;
import com.sandeep.redditclone.repository.UserRepository;
import com.sandeep.redditclone.repository.VerificationTokenRepository;
import com.sandeep.redditclone.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * This class contains the main business logics to register the users
 * i.e creating user object and saving to DB and send verification emails
 * Get the varibales from DTO class
 */
@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;
    //We are interacting with relational DB use @Transactional
    @Transactional
    public void signUp(RegisterRequest registerRequest){
        User user=new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        //Instant.now() is used to store created time
        user.setCreated(Instant.now());
        user.setEnabled(false);
        userRepository.save(user);
       String token= generateVerificationToken(user);
       mailService.sendMail(new NotificationEmail("Please activate your account",user.getEmail(),"Thank you " +
               "for signing up to Spring Reddit, please click on the below url to activate your account : "
               + "http://localhost:8080/api/auth/accountVerification/" + token));
    }

    /**
     * To verify the user by sending verification token to email ID.
     * UUID will generate a unique and random 128 bit value which we can use as our verification token.
     * Store the token in DB so that it will be present in DB even after many days.
     * @param user
     */
    private String generateVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);
        return token;

    }
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.
                getContext().getAuthentication().getPrincipal();
        return userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
    }
    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken= verificationTokenRepository.findByToken(token);
        verificationToken.orElseThrow(() -> new SpringRedditException("Invalid token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String userName= verificationToken.getUser().getUsername();
       User user= userRepository.findByUsername(userName).orElseThrow(()-> new SpringRedditException("User name " +userName+ " not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenService.generateRefreshToken().getToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(loginRequest.getUsername())
                .build();
    }
    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
        String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
        return AuthenticationResponse.builder()
                .authenticationToken(token)
                .refreshToken(refreshTokenRequest.getRefreshToken())
                .expiresAt(Instant.now().plusMillis(jwtProvider.getJwtExpirationInMillis()))
                .username(refreshTokenRequest.getUsername())
                .build();
    }
    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();
    }
}
