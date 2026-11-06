package br.com.finatech.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.finatech.domain.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserName(String userName);

    Boolean existsByUserName(String username);

    @Query("SELECT u.userName FROM User u WHERE u.id = :id")
    Optional<String> findUsernameById(@Param("id") UUID id);
}
