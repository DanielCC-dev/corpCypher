package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository <Cliente,Integer>{
	List<Cliente> findAll();
	void deleteById(int id);	
	Cliente findById(int id);
	List<Cliente> findByTipoDoc(String tipoDoc);
	Cliente findByNroDoc(String nroDoc);
	Cliente findByCorreo(String nroDoc);
}
