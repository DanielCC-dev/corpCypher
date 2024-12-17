package com.IsilERPSpring.controller;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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

import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.Usuario;
import com.IsilERPSpring.repository.ClienteRepository;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

	@Autowired
	ClienteRepository clienteRepository;
	
	public List<Cliente> obtenerClientesActivos() {
	    return clienteRepository.findAll().stream()
	        .filter(cliente -> !cliente.getEstadoCliente().equals("Inactivo"))
	        .collect(Collectors.toList());
	}

	
	@PostMapping("/eliminarCliente")
	 public String eliminar(HttpServletRequest request,  @ModelAttribute("objCliente") Cliente objCliente, Model model) {
		 Cliente objClienteBD= clienteRepository.findById(objCliente.getId());
			objClienteBD.setMotivoEliminacion(objCliente.getMotivoEliminacion());
			objClienteBD.setEstadoCliente("Inactivo"); 
			objClienteBD.setFechaEliminacion(Date.valueOf(LocalDate.now()));
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	        if (usuario != null) {
	            objClienteBD.setUsuarioEliminacion(usuario.getCorreo());
	        }

			clienteRepository.save(objClienteBD);
			List<Cliente> listaCliente = obtenerClientesActivos();
			model.addAttribute("listaCliente",listaCliente);
			return "gestionCliente";
	 }
	
	 @PostMapping("/mostrarNuevo")
		public String mostrarNuevo(HttpServletRequest request, Model model) {
			Cliente objCliente= new Cliente();
			model.addAttribute("objCliente",objCliente);
			return "nuevoCliente";
		}
	 
	 @GetMapping("/mostrarEliminar/{id}")
		public String mostrarEliminar(HttpServletRequest request,@PathVariable("id") int id, Model model) {
		 Cliente objCliente = clienteRepository.findById(id);
		 // Recuperar el usuario desde la sesión
		 Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
		 List<String> roles = (List<String>) request.getSession().getAttribute("roles");

		 // Pasar la información del usuario al modelo si está logeado
		 if (usuario != null) {
		      model.addAttribute("usuario", usuario);
		      model.addAttribute("roles", roles);
		 }
		 System.out.println("Usuario en sesión: " + usuario);
		 model.addAttribute("objCliente",objCliente);
		 return "eliminarCliente";
		}
	 
	 @PostMapping("/registrar")
	 public String registrar(HttpServletRequest request, @Valid @ModelAttribute("objCliente") Cliente objCliente, BindingResult result, Model model) {
	     if (result.hasErrors()) {
	         return "nuevoCliente";
	     }
	     boolean hasErrors = false;
	     if (clienteRepository.findByNroDoc(objCliente.getNroDoc()) != null) {
	         result.rejectValue("nroDoc", "error.objCliente", "El documento ya está en uso");
	         hasErrors = true;
	     }
	     if (clienteRepository.findByCorreo(objCliente.getCorreo()) != null) {
	         result.rejectValue("correo", "error.objCliente", "El correo ya está en uso");
	         hasErrors = true;
	     }
	     
	     String tipoDoc = objCliente.getTipoDoc();
	     String nroDoc = objCliente.getNroDoc();

	     if ("RUC".equals(tipoDoc)) {
	         
	         if (!nroDoc.matches("^(10|20)\\d{9}$")) {
	             result.rejectValue("nroDoc", "error.objCliente", "El RUC debe tener 11 dígitos y comenzar con 10 o 20");
	             hasErrors = true;
	         }
	     } else if ("DNI".equals(tipoDoc)) {
	         
	    	    if (nroDoc.length() != 8 || !nroDoc.matches("\\d+")) {
	    	        result.rejectValue("nroDoc", "error.objCliente", "El DNI debe tener exactamente 8 dígitos numéricos");
	    	        hasErrors = true;
	    	    } else if (nroDoc.matches("^[0]{3,}.*")) {
	    	        // Validación para los ceros iniciales: no puede comenzar con más de dos ceros
	    	        result.rejectValue("nroDoc", "error.objCliente", "El DNI no puede comenzar con más de dos ceros");
	    	        hasErrors = true;
	    	    } else if (nroDoc.matches("(\\d)\\1{7}")) {
	    	        result.rejectValue("nroDoc", "error.objCliente", "El DNI no puede tener todos los dígitos iguales");
	    	        hasErrors = true;
	    	    }
	     } else {
	         result.rejectValue("tipoDoc", "error.objCliente", "Tipo de documento no válido");
	         hasErrors = true;
	     }

	     if (hasErrors) {
	         return "nuevoCliente";
	     }
	     
	 
	     if (objCliente.getEstadoCliente() == null || objCliente.getEstadoCliente().isEmpty()) {
	         objCliente.setEstadoCliente("Activo");  // Asignamos "Activo" si no tiene un valor
	     }
	     
	     clienteRepository.save(objCliente);
	     List<Cliente> listaCliente = obtenerClientesActivos();
	     model.addAttribute("listaCliente", listaCliente);
	     return "gestionCliente";
	 }

	 
	 @GetMapping("/editar/{id}")
		public String mostrarEditar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
			Cliente objCliente = clienteRepository.findById(id);
			model.addAttribute("objCliente",objCliente);
			return "editarCliente";
		}
	 
	 @PostMapping("/actualizar")
		public String actualizar(HttpServletRequest request, @ModelAttribute("objCliente") Cliente objCliente, Model model) {
			Cliente objClienteBD= clienteRepository.findById(objCliente.getId());
			objClienteBD.setNombre(objCliente.getNombre());
//			objClienteBD.setApellido(objCliente.getApellido());
			objClienteBD.setTelefono(objCliente.getTelefono());
			objClienteBD.setDireccion(objCliente.getDireccion());
			objClienteBD.setCorreo(objCliente.getCorreo());
			clienteRepository.save(objClienteBD);
			List<Cliente> listaCliente = obtenerClientesActivos();
			model.addAttribute("listaCliente",listaCliente);
			return "gestionCliente";
		}
	 
	 @RequestMapping(value="/buscarCliente", method=RequestMethod.GET)
	 public String buscarCliente(HttpServletRequest request, @RequestParam(value="tipoDoc", required = false) String tipoDoc, Model model) {
	     List<Cliente> listaCliente = clienteRepository.findByTipoDoc(tipoDoc);

	     if ("Todos".equals(tipoDoc)) {
	         listaCliente = obtenerClientesActivos();
	     } else{
	         listaCliente = clienteRepository.findByTipoDoc(tipoDoc); 
	     }

	     model.addAttribute("listaCliente", listaCliente);
	     model.addAttribute("tipoDocSeleccionado", tipoDoc);
	     return "gestionCliente";
	 }

}
