package com.NoCountry.Patrickscoin.services.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.NoCountry.Patrickscoin.dto.request.UserDto;
import com.NoCountry.Patrickscoin.dto.response.UserLoguedDtoResponse;
import com.NoCountry.Patrickscoin.entities.User;
import com.NoCountry.Patrickscoin.entities.Wallet;
import com.NoCountry.Patrickscoin.entities.enumeration.Role;
import com.NoCountry.Patrickscoin.exception.UserException;
import com.NoCountry.Patrickscoin.mapper.UserMapper;
import com.NoCountry.Patrickscoin.repositories.UserRepository;
import com.NoCountry.Patrickscoin.services.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    private final JwtService jwtservice;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(UserDto userdto) {
        Wallet wallet = new Wallet();
        User user = User.builder()
            .email(userdto.getEmail())
            .password(passwordEncoder.encode(userdto.getPassword()))
            .lastName(userdto.getLastName())
            .name(userdto.getName())
            .role(Role.USER)
            .build();
            wallet.setUser(user);
            user.setWallet(wallet);
            userRepository.save(user);
    }

    @Override
    public UserLoguedDtoResponse login(UserDto user) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        User userEntity = userRepository.findByEmail(user.getEmail());
        String token = jwtservice.getToken(userEntity);

        return new UserLoguedDtoResponse(
            userEntity.getId(),
            userEntity.getName(),
            userEntity.getLastName(),
            userEntity.getEmail(),
            userEntity.getPassword(),
            token
        );
    }
    @Override
    public UserDto findById(Long id) throws Exception {
        User user = userRepository.findById(id).orElseThrow(() -> {
            throw new UserException("Usuario no encontrado");
        });
        return UserMapper.entityToDto(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<String> findAllEmail(){
        return findAll().stream().map(User::getEmail).toList();
    }

    public UserDto findByEmail(String email, String password) throws Exception {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserException("Usuario no encontrado");
        }
        
        // Verificar si la contraseña coincide
        if (!user.getPassword().equals(password)) {
            throw new UserException("La contraseña proporcionada no coincide para el usuario con email: " + email);
        }

        // Convertir la entidad User a un DTO
        UserDto userDto = UserMapper.entityToDto(user);
        return userDto; // Usuario encontrado y contraseña correcta
    }

}
