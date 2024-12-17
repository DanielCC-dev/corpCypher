package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria,Integer> {
	List<Categoria> findAll();
	Categoria findById(int id);
}
