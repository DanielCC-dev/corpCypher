package com.IsilERPSpring.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Ventas;

@Repository
public interface VentasRepository extends JpaRepository <Ventas, Integer> {
	List<Ventas> findAll();
	List<Ventas> findByFechaVentaBetween(Date fechaInicio, Date fechaFin);
	Ventas findById(int id);
}

