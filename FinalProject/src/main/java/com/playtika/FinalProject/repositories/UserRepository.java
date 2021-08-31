package com.playtika.FinalProject.repositories;

import com.playtika.FinalProject.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
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
