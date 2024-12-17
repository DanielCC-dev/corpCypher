package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.Pedido;

@Repository
public interface PedidoRepository  extends JpaRepository<Pedido,Integer>{
	List<Pedido> findAll();
	void deleteById(int id);
	Pedido findById(int id);
	List<Pedido> findByCliente(Cliente cliente);
}
