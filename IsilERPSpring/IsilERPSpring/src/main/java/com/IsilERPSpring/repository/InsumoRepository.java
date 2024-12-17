package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Insumo;

@Repository
public interface InsumoRepository extends JpaRepository <Insumo,Integer>{
	List<Insumo> findAll();
	Insumo findById(int id);
	
}
