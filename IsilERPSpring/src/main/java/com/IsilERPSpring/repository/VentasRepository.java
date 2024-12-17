package com.IsilERPSpring.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Pedido;
import com.IsilERPSpring.entity.Ventas;

@Repository
public interface VentasRepository extends JpaRepository <Ventas, Integer> {
	List<Ventas> findAll();
	Page<Ventas> findAll(Pageable pageable);
	List<Ventas> findByFechaVentaBetween(Date fechaInicio, Date fechaFin);
	Ventas findByPedidoAndEstado(Pedido pedido, String estado);
	Ventas findById(int id);
	Ventas findByPedido(Pedido pedido);
}

