package com.IsilERPSpring.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.OrdenCompra;

@Repository
public interface OrdenCompraRepository extends JpaRepository <OrdenCompra,Integer>{
	List<OrdenCompra> findAll();
	Page<OrdenCompra> findAll(Pageable pageable);
	List<OrdenCompra> findByFechaRegistroBetween(Date fechaInicio, Date fechaFin);
	OrdenCompra findById(int id);
	void deleteById(int id);
}
