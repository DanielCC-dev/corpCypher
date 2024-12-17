package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.DetalleVentas;
import com.IsilERPSpring.entity.Ventas;

@Repository
public interface DetalleVentasRepository extends JpaRepository <DetalleVentas, Integer> {
	List<DetalleVentas> findByVentas(Ventas ventas);
}

