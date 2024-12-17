package com.IsilERPSpring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.IsilERPSpring.entity.Articulo;
import com.IsilERPSpring.entity.Categoria;
import com.IsilERPSpring.entity.Proveedor;
import com.IsilERPSpring.repository.ArticuloRepository;
import com.IsilERPSpring.repository.CategoriaRepository;
import com.IsilERPSpring.repository.ProveedorRepository;
import org.springframework.data.domain.Pageable; 

@Controller
@RequestMapping("/categoria")
public class CategoriaController {

    @Autowired
    CategoriaRepository categoriaRepository;
    
    @Autowired
    ArticuloRepository articuloRepository;
    
    @Autowired
    ProveedorRepository proveedorRepository;

    @PostMapping("/mostrarNuevo")
    public String mostrarNuevo(HttpSession session, Model model,  @RequestParam(value = "offset", defaultValue = "0") int offset, 
	                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
    	session.setAttribute("offset", offset);
	    session.setAttribute("pageSize", pageSize);

	    model.addAttribute("pageSize", pageSize);
	    
        Categoria objCategoria = new Categoria();
        model.addAttribute("objCategoria", objCategoria);
        return "nuevoCategoria";
    }


    @PostMapping("/registrar")
    public String registrar(@Valid @ModelAttribute("objCategoria") Categoria objCategoria, 
                            BindingResult result, 
                            HttpSession session,
                            Model model) {
        if (result.hasErrors()) {
            return "nuevoCategoria";
        }
        Integer offset = (Integer) session.getAttribute("offset");
	    Integer pageSize = (Integer) session.getAttribute("pageSize");
        categoriaRepository.save(objCategoria);
        return "redirect:/home/mostrarGestionCategorias/" + offset + "/" + pageSize;
    }




    @GetMapping("/eliminar/{id}")
    public String eliminar(HttpServletRequest request, @PathVariable("id") int id, RedirectAttributes redirectAttributes, HttpSession session) {
        List<Articulo> articulos = articuloRepository.findByCategoria_IdCategoria(id);
        List<Proveedor> proveedores = proveedorRepository.findByCategoria_IdCategoria(id);
        
        if (articulos.isEmpty() && proveedores.isEmpty()) {
            categoriaRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Categoría eliminada exitosamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la categoría porque está asociada a productos o proveedores.");
        }
        Integer offset = (Integer) session.getAttribute("offset");
	    Integer pageSize = (Integer) session.getAttribute("pageSize");
        // Redirige siempre a la página 1
        return "redirect:/home/mostrarGestionCategorias/" + offset + "/" + pageSize;
    }

}

