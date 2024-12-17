package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.IsilERPSpring.entity.Distrito;
import com.IsilERPSpring.entity.Proveedor;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

	List<Proveedor> findAll();
	
	Page<Proveedor> findAll(Pageable pageable);
	
    Proveedor findById(int id);

    Proveedor findByRuc(String ruc);

    List<Proveedor> findByDistrito(Distrito distrito);
    
    public List<Proveedor> findByEstado(String estado);

    void deleteById(int id);
    
    public List<Proveedor> findByCategoria_IdCategoria(int IdCategoria);
}
