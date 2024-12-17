package com.IsilERPSpring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.IsilERPSpring.entity.Articulo;
import com.IsilERPSpring.entity.Categoria;
import com.IsilERPSpring.repository.ArticuloRepository;
import com.IsilERPSpring.repository.CategoriaRepository;

@Controller
@RequestMapping ("/articulo")
public class ArticuloController {

	@Autowired 
	ArticuloRepository articuloRepository;
	
	@Autowired
	CategoriaRepository categoriaRepository;
	
	 @GetMapping("/eliminar/{id}")
	 public String eliminar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
		 articuloRepository.deleteById(id);
		 List<Articulo> listaArticulo = articuloRepository.findAll();
		 model.addAttribute("listaArticulo", listaArticulo);
		 return "gestionArticulo";
	 }
	 
	 @PostMapping("/mostrarNuevo")
		public String mostrarNuevo(HttpServletRequest request, Model model) {
			Articulo objArticulo= new Articulo();
			model.addAttribute("objArticulo",objArticulo);
	        List<Categoria> listaCategorias = categoriaRepository.findAll();
	        model.addAttribute("listaCategorias", listaCategorias);
			return "nuevoArticulo";
		}
	 
	  @PostMapping("/registrar")
	    public String registrar(HttpServletRequest request, @Valid @ModelAttribute("objArticulo") Articulo objArticulo, BindingResult result, Model model) {
	        if (result.hasErrors()) {
	            return "nuevoArticulo";
	        }
	        if (articuloRepository.findByNombre(objArticulo.getNombre()) != null) {
	            result.rejectValue("nombre", "error.objArticulo", "Ya existe este nombre");	            
	            return "nuevoArticulo";
	        }
	        articuloRepository.save(objArticulo);	       
		    List<Articulo> listaArticulo= articuloRepository.findAll();
		    model.addAttribute("listaArticulo",listaArticulo);
		    return "redirect:/home/mostrarGestionArticulo";
	    }
	 
	 @GetMapping("/mostrarEditar/{id}")
		public String mostrarEditar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
			Articulo objArticulo = articuloRepository.findById(id);
			model.addAttribute("objArticulo",objArticulo);
			return "editarArticulo";
		}
	 
	 @PostMapping("/actualizar")
		public String actualizar(HttpServletRequest request, @ModelAttribute("objArticulo") Articulo objArticulo, Model model) {
			Articulo objArticuloBD= articuloRepository.findById(objArticulo.getId());
			objArticuloBD.setNombre(objArticulo.getNombre());
			objArticuloBD.setDescripcion(objArticulo.getDescripcion());
			objArticuloBD.setPrecio(objArticulo.getPrecio());
			objArticuloBD.setStock(objArticulo.getStock());
			articuloRepository.save(objArticuloBD);
			List<Articulo> listaArticulo = articuloRepository.findAll();
			model.addAttribute("listaArticulo",listaArticulo);
			return "gestionArticulo";
		}
	 
		@RequestMapping(value="/buscarPorCategoria", method=RequestMethod.GET)
		public String buscarPorCategoria(HttpServletRequest request, @RequestParam(value="idCategoria", required = false) String idCategoria, Model model) {
		    List<Articulo> listaArticulo;

		    if ("Todos".equals(idCategoria)) {
		    	listaArticulo = articuloRepository.findAll();
		    } else {
		        int categoriaId = Integer.parseInt(idCategoria);
		        Categoria objCategoria = categoriaRepository.findById(categoriaId);
		        listaArticulo = articuloRepository.findByCategoria(objCategoria);
		    }

		    List<Categoria> listaCategorias = categoriaRepository.findAll();
		    model.addAttribute("listaArticulo", listaArticulo);
		    model.addAttribute("listaCategorias", listaCategorias);
		    model.addAttribute("idCategoriaSeleccionada", idCategoria);
		    return "gestionArticulo";
		}
}
