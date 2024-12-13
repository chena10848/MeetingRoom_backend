package com.example.demo.Repository;

import com.example.demo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.id, u.email, u.password, u.loginType FROM User u WHERE u.email = :email")
    List<Object> getUserPassword(String email);

    @Transactional
    @Query("SELECT u.email FROM User u WHERE u.id IN :participants")
    List<Object[]> getUserEmail(@Param("participants") Integer[] participants);

    @Query("SELECT u.id, u.userName, u.email FROM User u WHERE u.id != :userId")
    List<Object[]> getUserInformation(@Param("userId") Integer userId);

    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    Optional<User> findByUsername(@Param("userName") String userName);

}