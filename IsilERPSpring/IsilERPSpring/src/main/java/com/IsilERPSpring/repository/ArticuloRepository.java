package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Articulo;
import com.IsilERPSpring.entity.Categoria;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo,Integer>{
	List<Articulo> findAll();
	void deleteById(int id);
	Articulo findById(int id);	
	Articulo findByNombre(String nombre);
	List<Articulo> findByCategoria(Categoria categoria);
}
