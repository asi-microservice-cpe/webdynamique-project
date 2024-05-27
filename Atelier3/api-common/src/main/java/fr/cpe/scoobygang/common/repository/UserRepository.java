package fr.cpe.scoobygang.common.repository;

import fr.cpe.scoobygang.common.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}