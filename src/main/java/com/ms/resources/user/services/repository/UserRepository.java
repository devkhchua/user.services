package com.ms.resources.user.services.repository;

import com.ms.resources.user.services.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Repository class used in Java Persistence API to perform
//Persistence(INSERT INTO) or Access(RETRIEVE) Database using Entity Class
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUsername(String username);
}
