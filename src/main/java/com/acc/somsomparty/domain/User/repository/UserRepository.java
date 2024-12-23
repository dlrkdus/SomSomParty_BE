package com.acc.somsomparty.domain.User.repository;

import com.acc.somsomparty.domain.User.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
