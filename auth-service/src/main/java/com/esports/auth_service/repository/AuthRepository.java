package com.esports.auth_service.repository;


import com.esports.auth_service.model.UsuarioAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<UsuarioAuth, Long> {
    Optional<UsuarioAuth> findByEmail(String email);
    boolean existsByEmail(String email);
}
