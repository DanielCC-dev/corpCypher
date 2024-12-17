package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository <Rol, Integer>{
	List<Rol> findAll();
	Rol findByNombre(String nombre);
}
