package cacttus.securedapi.service;

import cacttus.securedapi.config.JwtService;
import cacttus.securedapi.dto.AuthentiationRequestDto;
import cacttus.securedapi.dto.AuthenticationResponseDto;
import cacttus.securedapi.model.Role;
import cacttus.securedapi.model.User;
import cacttus.securedapi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponseDto register(AuthentiationRequestDto dto){
        var user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .firstName(dto.getFirstName())
                .role(Role.USER)
                .build();
        user = userRepository.save(user);
        return AuthenticationResponseDto.builder()
                .token(jwtService.generateToken(user))
                .email(user.getEmail())
                .build();
    }

    public AuthenticationResponseDto login(AuthentiationRequestDto dto){
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.getEmail(),
                dto.getPassword()
        ));
        var user = userRepository.findByEmail(dto.getEmail());
        if (user.isPresent()){
            return AuthenticationResponseDto.builder()
                    .token(jwtService.generateToken(user.get()))
                    .email(user.get().getEmail())
                    .build();
        }
        return AuthenticationResponseDto.builder().build();

    }


}
