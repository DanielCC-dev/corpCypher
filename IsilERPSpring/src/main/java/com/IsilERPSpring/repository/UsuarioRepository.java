package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario,Integer>{
	Usuario findByCorreoAndPassword(String correo, String password);
	List<Usuario> findAll();
	void deleteById(int id);
	Usuario findById(int id);
	Usuario findByCorreo(String correo);
	List<Usuario> findByRolesNombre(String nombreRol);
	Page<Usuario> findAll(Pageable pageable);

	@Query("SELECT COUNT(u) FROM Usuario u JOIN u.roles r WHERE r.nombre = :nombreRol")
	int countUsuariosByRol(@Param("nombreRol") String nombreRol);
}
