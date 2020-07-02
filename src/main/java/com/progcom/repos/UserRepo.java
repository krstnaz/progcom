package com.progcom.repos;

import com.progcom.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long>
{
    User findByUsername(String username);
}
