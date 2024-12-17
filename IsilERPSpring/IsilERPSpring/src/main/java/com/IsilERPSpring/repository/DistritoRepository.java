package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Distrito;

@Repository
public interface DistritoRepository extends JpaRepository<Distrito,Integer>{
	List<Distrito> findAll();
	Distrito findById(int id);
}


