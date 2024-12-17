package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Articulo;
import com.IsilERPSpring.entity.Categoria;

@Repository
public interface ArticuloRepository extends JpaRepository<Articulo,Integer>{
	List<Articulo> findAll();
	Page<Articulo> findAll(Pageable pageable);
	void deleteById(int id);
	Articulo findById(int id);	
	Articulo findByNombre(String nombre);
	List<Articulo> findByCategoria(Categoria categoria);
	public List<Articulo> findByCategoria_IdCategoria(int IdCategoria);
	
	@Query("SELECT a FROM Articulo a WHERE a.categoria = :categoria AND a.estado != 'Inactivo'")
	List<Articulo> findByCategoriaAndEstado(@Param("categoria") Categoria categoria);

}
