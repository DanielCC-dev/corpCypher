package com.IsilERPSpring.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.Usuario;
import com.IsilERPSpring.repository.ArticuloRepository;
import com.IsilERPSpring.repository.CategoriaRepository;

@Controller
@RequestMapping ("/articulo")
public class ArticuloController {

	@Autowired 
	ArticuloRepository articuloRepository;
	
	@Autowired
	CategoriaRepository categoriaRepository;
	
	public List<Articulo> obtenerArticulosActivos() {
	    return articuloRepository.findAll().stream()
	        .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
	        .collect(Collectors.toList());
	}
	
	 @GetMapping("/mostrarEliminar/{id}")
	 public String eliminar(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
		 Articulo objArticulo = articuloRepository.findById(id);
		 Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		 if (usuario != null) {
			model.addAttribute("usuario", usuario);
		 }
		 else {
			 return "redirect:/";
			}
		 // Guardar los valores de paginación en la sesión
		 session.setAttribute("offset", offset);
		 session.setAttribute("pageSize", pageSize);

		 // Paginación
		 model.addAttribute("pageSize", pageSize);
		 model.addAttribute("objArticulo", objArticulo);
		 return "eliminarArticulo";
	 }
	 
	@PostMapping("/eliminarArticulo")
	public String eliminar(HttpSession session, HttpServletRequest request,  @ModelAttribute("objArticulo") Articulo objArticulo, Model model) {
		Articulo objArticuloBD = articuloRepository.findById(objArticulo.getId());
		objArticuloBD.setMotivoEliminacion(objArticulo.getMotivoEliminacion());
		objArticuloBD.setEstado("Inactivo"); 
		objArticuloBD.setFechaEliminacion(Date.valueOf(LocalDate.now()));
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario != null) {
        	objArticuloBD.setUsuarioEliminacion(usuario.getCorreo());
        }
        
        articuloRepository.save(objArticuloBD);
        List<Articulo> listaArticulo = obtenerArticulosActivos();
		model.addAttribute("listaArticulo",listaArticulo);
		List<Categoria> listaCategorias = categoriaRepository.findAll();
        model.addAttribute("listaCategorias", listaCategorias);
        
        Integer offset = (Integer) session.getAttribute("offset");
	     Integer pageSize = (Integer) session.getAttribute("pageSize");
	     return "redirect:/home/mostrarGestionArticulo/"+offset +"/" + pageSize;
	}
	 
	 @PostMapping("/mostrarNuevo")
		public String mostrarNuevo(HttpServletRequest request, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
			Articulo objArticulo= new Articulo();
			model.addAttribute("objArticulo",objArticulo);
	        List<Categoria> listaCategorias = categoriaRepository.findAll();
	        model.addAttribute("listaCategorias", listaCategorias);
	        
	        // Guardar los valores de paginación en la sesión
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);

		    // Paginación
		    model.addAttribute("pageSize", pageSize);
		    
			return "nuevoArticulo";
		}
	 
	  @PostMapping("/registrar")
	    public String registrar(HttpSession session, HttpServletRequest request, @Valid @ModelAttribute("objArticulo") Articulo objArticulo, BindingResult result, Model model) {
	        if (result.hasErrors()) {
	            return "nuevoArticulo";
	        }
	        if (articuloRepository.findByNombre(objArticulo.getNombre()) != null) {
	            result.rejectValue("nombre", "error.objArticulo", "Ya existe este nombre");	 
	            List<Categoria> listaCategorias = categoriaRepository.findAll();
		        model.addAttribute("listaCategorias", listaCategorias);
	            return "nuevoArticulo";
	        }
	        if (objArticulo.getEstado() == null || objArticulo.getEstado().isEmpty()) {
		         objArticulo.setEstado("Activo"); 
		     }
	        articuloRepository.save(objArticulo);	       
		    List<Articulo> listaArticulo= articuloRepository.findAll();
		    model.addAttribute("listaArticulo",listaArticulo);
		    List<Categoria> listaCategorias = categoriaRepository.findAll();
	        model.addAttribute("listaCategorias", listaCategorias);
	        Integer offset = (Integer) session.getAttribute("offset");
		     Integer pageSize = (Integer) session.getAttribute("pageSize");
		     return "redirect:/home/mostrarGestionArticulo/"+offset +"/" + pageSize;
	    }
	 
	 @GetMapping("/mostrarEditar/{id}")
		public String mostrarEditar(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
			Articulo objArticulo = articuloRepository.findById(id);
			model.addAttribute("objArticulo",objArticulo);
			
			 // Guardar los valores de paginación en la sesión
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);

		    // Paginación
		    model.addAttribute("pageSize", pageSize);
			return "editarArticulo";
		}
	 
	 @PostMapping("/actualizar")
		public String actualizar(HttpSession session, HttpServletRequest request, @ModelAttribute("objArticulo") Articulo objArticulo, Model model) {
			Articulo objArticuloBD= articuloRepository.findById(objArticulo.getId());
			objArticuloBD.setNombre(objArticulo.getNombre());
			objArticuloBD.setDescripcion(objArticulo.getDescripcion());
			articuloRepository.save(objArticuloBD);
			List<Articulo> listaArticulo = obtenerArticulosActivos();
			model.addAttribute("listaArticulo",listaArticulo);
			List<Categoria> listaCategorias = categoriaRepository.findAll();
	        model.addAttribute("listaCategorias", listaCategorias);
	        
	        Integer offset = (Integer) session.getAttribute("offset");
		     Integer pageSize = (Integer) session.getAttribute("pageSize");
		     return "redirect:/home/mostrarGestionArticulo/"+offset +"/" + pageSize;
		}
	 
		@RequestMapping(value="/buscarPorCategoria", method=RequestMethod.GET)
		public String buscarPorCategoria(HttpServletRequest request, @RequestParam(value="idCategoria", required = false) String idCategoria, Model model) {
		    List<Articulo> listaArticulo;

		    if ("Todos".equals(idCategoria)) {
			     return "redirect:/home/mostrarGestionArticulo/0/10";
		    } else {
		        int categoriaId = Integer.parseInt(idCategoria);
		        Categoria objCategoria = categoriaRepository.findById(categoriaId);
		        listaArticulo = articuloRepository.findByCategoria(objCategoria).stream()
		    	        .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
		    	        .collect(Collectors.toList());
		        
		        String numeroDeElementos = "resultados filtrados";
				model.addAttribute("numeroDeElementos", numeroDeElementos);
		    }

		    List<Categoria> listaCategorias = categoriaRepository.findAll();
		    model.addAttribute("listaArticulo", listaArticulo);
		    model.addAttribute("listaCategorias", listaCategorias);
		    model.addAttribute("idCategoriaSeleccionada", idCategoria);
		    return "gestionArticulo";
		}
}
