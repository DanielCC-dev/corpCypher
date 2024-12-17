package com.IsilERPSpring.repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.Pedido;
import com.IsilERPSpring.entity.Ventas;

@Repository
public interface PedidoRepository  extends JpaRepository<Pedido,Integer>{
	List<Pedido> findAll();
	Page<Pedido> findAll(Pageable pageable);
	void deleteById(int id);
	Pedido findById(int id);
	List<Pedido> findByCliente(Cliente cliente);	
	@Query("SELECT p FROM Pedido p ORDER BY p.id DESC")
    List<Pedido> findAllByOrderByIdDesc();
}
