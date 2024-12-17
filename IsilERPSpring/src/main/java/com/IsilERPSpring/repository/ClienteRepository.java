package com.IsilERPSpring.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page; // Importar Page
import org.springframework.data.domain.Pageable; // Importar Pageable

import com.IsilERPSpring.entity.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    List<Cliente> findAll();
    void deleteById(int id);
    Cliente findById(int id);
    List<Cliente> findByTipoDoc(String tipoDoc);
    Cliente findByNroDoc(String nroDoc);
    Cliente findByCorreo(String nroDoc);

    // Método para obtener clientes con paginación
    Page<Cliente> findAll(Pageable pageable);	
}
