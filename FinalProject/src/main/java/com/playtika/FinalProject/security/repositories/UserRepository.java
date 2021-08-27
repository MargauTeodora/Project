package com.playtika.FinalProject.security.repositories;

import com.playtika.FinalProject.security.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);
    User findByUsername(String username);

    boolean existsByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

    @Transactional
    default void updateByUsername(String username){
        User userToUpdate=findByUsername(username);
    }
    @Override
    void delete(User user);
}
