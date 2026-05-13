package com.esports.userservice.repository;

import com.esports.userservice.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByNicknameIgnoreCase(String nickname);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Usuario> findByNicknameIgnoreCase(String nickname);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    List<Usuario> findByRol(Usuario.RolUsuario rol);

    List<Usuario> findByEstado(Usuario.EstadoUsuario estado);

    List<Usuario> findByRolAndEstado(Usuario.RolUsuario rol, Usuario.EstadoUsuario estado);

    boolean existsByNicknameIgnoreCaseAndIdNot(String nickname, Long id);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
