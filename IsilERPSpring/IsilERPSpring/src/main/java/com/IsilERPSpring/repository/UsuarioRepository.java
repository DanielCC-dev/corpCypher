package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
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
}
