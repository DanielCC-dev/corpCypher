package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.DetalleCompra;
import com.IsilERPSpring.entity.OrdenCompra;

@Repository
public interface DetalleCompraRepository extends JpaRepository <DetalleCompra,Integer>{
	List<DetalleCompra> findByOrdenCompra(OrdenCompra ordenCompra);
	
}
