package org.levalnik.repository;

import org.levalnik.model.enums.Status;
import org.levalnik.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.status = ?1")
    Page<User> findByStatus(Status status, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE %?1% OR u.email LIKE %?1%")
    Page<User> searchUsers(String searchTerm, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.username = ?1 OR u.email = ?2")
    boolean existsByUsernameOrEmail(String username, String email);

    Optional<User> findByIdAndStatus(Long id, Status status);
}