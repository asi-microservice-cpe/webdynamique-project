package fr.cpe.scoobygang.atelier2.service;

import fr.cpe.scoobygang.atelier2.model.User;
import fr.cpe.scoobygang.atelier2.repository.UserRepository;
import fr.cpe.scoobygang.atelier2.security.JWT;
import fr.cpe.scoobygang.atelier2.security.JWTService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private UserRepository userRepository;
    private JWTService jwtService;

    public UserService(UserRepository userRepository, JWTService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public void addUser(User user) {
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        Iterable<User> iterable = userRepository.findAll();
        return StreamSupport.stream(iterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public Optional<JWT> login(String surname, String password) {
        User existingUser = userRepository.findBySurname(surname);

        if (existingUser == null || !Objects.equals(existingUser.getPassword(), password)) {
            return Optional.empty();
        }

        JWT jwt = jwtService.fromUser(existingUser);
        return Optional.of(jwt);
    }
}