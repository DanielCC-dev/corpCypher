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
import org.springframework.web.bind.annotation.*;

import com.IsilERPSpring.entity.Distrito;
import com.IsilERPSpring.entity.Proveedor;
import com.IsilERPSpring.entity.Usuario;
import com.IsilERPSpring.repository.CategoriaRepository;
import com.IsilERPSpring.repository.DistritoRepository;
import com.IsilERPSpring.repository.ProveedorRepository;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    ProveedorRepository proveedorRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    DistritoRepository distritoRepository;

    public List<Proveedor> obtenerProveedorsActivos() {
        return proveedorRepository.findAll().stream()
            .filter(proveedor -> !proveedor.getEstado().equals("Inactivo"))
            .collect(Collectors.toList());
    }
    
	 @PostMapping("/mostrarNuevo")
		public String mostrarNuevo(HttpServletRequest request, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
	             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
			Proveedor objProveedor = new Proveedor();
			model.addAttribute("objProveedor",objProveedor);
			model.addAttribute("listaDistritos", distritoRepository.findAll());
		    model.addAttribute("listaCategorias", categoriaRepository.findAll());
		    // Guardar los valores de paginación en la sesión
			 session.setAttribute("offset", offset);
			 session.setAttribute("pageSize", pageSize);

			 // Paginación
			 model.addAttribute("pageSize", pageSize);
			return "nuevoProveedor";
		}

    @PostMapping("/desactivarProveedor")
    public String desactivar(HttpSession session, HttpServletRequest request, @ModelAttribute("objProveedor") Proveedor objProveedor, Model model) {
        Proveedor objProveedorBD = proveedorRepository.findById(objProveedor.getId());
        if (objProveedorBD == null) {
            return "errorPage"; 
        }
        objProveedorBD.setMotivoEliminacion(objProveedor.getMotivoEliminacion());
        objProveedorBD.setEstado("Inactivo");
        objProveedorBD.setFechaEliminacion(Date.valueOf(LocalDate.now()));
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario != null) {
            objProveedorBD.setUsuarioEliminacion(usuario.getCorreo());
        }
        proveedorRepository.save(objProveedorBD);
        List<Proveedor> listaProveedor = obtenerProveedorsActivos();
        model.addAttribute("listaProveedor", listaProveedor);
        Integer offset = (Integer) session.getAttribute("offset");
	     Integer pageSize = (Integer) session.getAttribute("pageSize");
	     return "redirect:/home/mostrarGestionProveedor/"+offset +"/" + pageSize;
    }

    
    @GetMapping("/mostrarDesactivar/{id}")
    public String mostrarDesactivar(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
        Proveedor objProveedor = proveedorRepository.findById(id);
        if (objProveedor == null) {
            return "errorPage"; 
        }
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");        

        if (usuario != null) {
            model.addAttribute("usuario", usuario);
        }
        else {
        	return "redirect:/";
		}
        System.out.println("Usuario en sesión: " + usuario);
        model.addAttribute("objProveedor", objProveedor);
        	// Guardar los valores de paginación en la sesión
     		 session.setAttribute("offset", offset);
     		 session.setAttribute("pageSize", pageSize);

     		 // Paginación
     		 model.addAttribute("pageSize", pageSize);
        return "desactivarProveedor";
    }

    @PostMapping("/registrar")
    public String registrarProveedor(HttpSession session, @Valid @ModelAttribute("objProveedor") Proveedor objProveedor,
                                     BindingResult result, Model model) {
        String ruc = objProveedor.getRuc();
        if (!(ruc.startsWith("10") || ruc.startsWith("20"))) {
            result.rejectValue("ruc", "error.objProveedor", "El RUC debe comenzar con 10 o 20");
        }

        if (result.hasErrors()) {
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            model.addAttribute("listaCategorias", categoriaRepository.findAll());
            return "nuevoProveedor";
        }

        if (proveedorRepository.findByRuc(objProveedor.getRuc()) != null) {
            result.rejectValue("ruc", "error.objProveedor", "El RUC ya está en uso");
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            model.addAttribute("listaCategorias", categoriaRepository.findAll());
            return "nuevoProveedor";
        }
	     if (objProveedor.getEstado() == null || objProveedor.getEstado().isEmpty()) {
	         objProveedor.setEstado("Activo");
	     }
        proveedorRepository.save(objProveedor);
        Integer offset = (Integer) session.getAttribute("offset");
	     Integer pageSize = (Integer) session.getAttribute("pageSize");
	     return "redirect:/home/mostrarGestionProveedor/"+offset +"/" + pageSize;
    }


    @GetMapping("/editar/{id}")
    public String editar(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
        Proveedor objProveedor = proveedorRepository.findById(id);
        if (objProveedor == null) {
            return "errorPage"; 
        }

        model.addAttribute("objProveedor", objProveedor);
        model.addAttribute("listaDistritos", distritoRepository.findAll());
        model.addAttribute("listaCategorias", categoriaRepository.findAll());
        
        	// Guardar los valores de paginación en la sesión
     		 session.setAttribute("offset", offset);
     		 session.setAttribute("pageSize", pageSize);

     		 // Paginación
     		 model.addAttribute("pageSize", pageSize);
        return "editarProveedor";
    }

    @PostMapping("/actualizar")
    public String actualizar(HttpSession session, HttpServletRequest request, @Valid @ModelAttribute("objProveedor") Proveedor objProveedor,
                             BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            model.addAttribute("listaCategorias", categoriaRepository.findAll());
            return "editarProveedor";
        }

        Proveedor objProveedorBD = proveedorRepository.findById(objProveedor.getId());
        if (objProveedorBD != null) {
            objProveedorBD.setTelefono(objProveedor.getTelefono());
            proveedorRepository.save(objProveedorBD);
        }
        Integer offset = (Integer) session.getAttribute("offset");
	     Integer pageSize = (Integer) session.getAttribute("pageSize");
	     return "redirect:/home/mostrarGestionProveedor/"+offset +"/" + pageSize;
    }

    @RequestMapping(value="/buscarProveedor", method=RequestMethod.GET)
    public String buscarProveedor(HttpServletRequest request, @RequestParam(value="idDistrito", required = false) String idDistrito, Model model) {
        List<Proveedor> listaProveedor;

        if ("Todos".equals(idDistrito)) {
   	     return "redirect:/home/mostrarGestionProveedor/0/10";
        } else {
            int distritoId = Integer.parseInt(idDistrito);
            Distrito objDistrito = distritoRepository.findById(distritoId);
            if (objDistrito == null) {
                return "errorPage"; 
            }
            listaProveedor = proveedorRepository.findByDistrito(objDistrito).stream()
                    .filter(proveedor -> !proveedor.getEstado().equals("Inactivo"))
                    .collect(Collectors.toList());
            String numeroDeElementos = "resultados filtrados";
			model.addAttribute("numeroDeElementos", numeroDeElementos);
        }

        List<Distrito> listaDistritos = distritoRepository.findAll();
        model.addAttribute("listaProveedor", listaProveedor);
        model.addAttribute("listaDistritos", listaDistritos);
        model.addAttribute("idDistritoSeleccionada", idDistrito);
        return "gestionProveedor";
    }
}
