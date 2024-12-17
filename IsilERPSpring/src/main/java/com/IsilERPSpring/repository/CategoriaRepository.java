package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable

import com.IsilERPSpring.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Integer> {
	List<Categoria> findAll();
	Categoria findById(int id);
	Page<Categoria> findAll(Pageable pageable);

}
