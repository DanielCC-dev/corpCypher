package com.IsilERPSpring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.IsilERPSpring.entity.Categoria;
import com.IsilERPSpring.entity.Rol;
import com.IsilERPSpring.entity.Usuario;
import com.IsilERPSpring.repository.RolRepository;
import com.IsilERPSpring.repository.UsuarioRepository;

@Controller
@RequestMapping ("/usuario")
public class UsuarioController {

	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	RolRepository rolRepository;
    
	@GetMapping("/eliminar/{id}")
	public String eliminar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
	    usuarioRepository.deleteById(id);
	    
	    Integer offset = (Integer) request.getSession().getAttribute("offset");
	    Integer pageSize = (Integer) request.getSession().getAttribute("pageSize");

	    // Paginación con los valores actuales
	    Pageable pageable = PageRequest.of(offset, pageSize);
	    Page<Usuario> listaUsuarios = usuarioRepository.findAll(pageable);

	    model.addAttribute("listaUsuarios", listaUsuarios);
	    model.addAttribute("currentPage", offset);
	    model.addAttribute("totalPages", listaUsuarios.getTotalPages());

	    return "redirect:/home/mostrarGestionUsuarios/"+offset +"/" + pageSize;
	}

		
	
	@PostMapping("/mostrarNuevo")
	public String mostrarNuevo(HttpSession session, Model model,
	                            @RequestParam(value = "offset", defaultValue = "0") int offset, 
	                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
	    Usuario objUsuario = new Usuario();
	    model.addAttribute("objUsuario", objUsuario);

	    // Lista de roles
	    List<Rol> listaRoles = rolRepository.findAll();
	    model.addAttribute("listaRoles", listaRoles);

	    // Guardar los valores de paginación en la sesión
	    session.setAttribute("offset", offset);
	    session.setAttribute("pageSize", pageSize);

	    // Paginación
	    model.addAttribute("pageSize", pageSize);

	    return "nuevoUsuario";
	}


	@GetMapping("/editar/{id}")
	public String mostrarEditar(HttpSession session, @PathVariable("id") int id, Model model,
	                             @RequestParam(value = "offset", defaultValue = "0") int offset, 
	                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
	    Usuario objUsuario = usuarioRepository.findById(id);
	    model.addAttribute("objUsuario", objUsuario);

	    List<Rol> listaRoles = rolRepository.findAll();
	    model.addAttribute("listaRoles", listaRoles);

	    session.setAttribute("offset", offset);
	    session.setAttribute("pageSize", pageSize);

	    model.addAttribute("pageSize", pageSize);

	    return "editarUsuario";
	}

		  
	 @PostMapping("/registrar")
	 public String registrar( HttpSession session, @Valid @ModelAttribute("objUsuario") Usuario objUsuario, BindingResult result, Model model) {
	     if (result.hasErrors()) {
	         List<Rol> listaRoles = rolRepository.findAll();
	         model.addAttribute("listaRoles", listaRoles);
	         model.addAttribute("objUsuario",objUsuario);
	         return "nuevoUsuario";
	     }
	     if (usuarioRepository.findByCorreo(objUsuario.getCorreo()) != null) {
	         result.rejectValue("correo", "error.objUsuario", "El correo ya está en uso");
	         List<Rol> listaRoles = rolRepository.findAll();
	         model.addAttribute("listaRoles", listaRoles);
	         model.addAttribute("objUsuario",objUsuario);
	         return "nuevoUsuario";
	     }
	     
	     String password = objUsuario.getPassword();

	     if (isShort(password)) {
	         result.rejectValue("password", "error.objUsuario", "La contraseña debe tener al menos 6 caracteres.");
	         List<Rol> listaRoles = rolRepository.findAll();
	         model.addAttribute("listaRoles", listaRoles);
	         model.addAttribute("objUsuario",objUsuario);
	         return "nuevoUsuario";
	     }

	     if (isLong(password)) {
	         result.rejectValue("password", "error.objUsuario", "La contraseña no puede tener más de 26 caracteres.");
	         List<Rol> listaRoles = rolRepository.findAll();
	         model.addAttribute("listaRoles", listaRoles);
	         model.addAttribute("objUsuario",objUsuario);
	         return "nuevoUsuario";
	     }

	     if (isInvalid(password)) {
	         result.rejectValue("password", "error.objUsuario", "La contraseña debe incluir al menos una mayúscula y una minúscula.");
	         List<Rol> listaRoles = rolRepository.findAll();
	         model.addAttribute("listaRoles", listaRoles);
	         model.addAttribute("objUsuario",objUsuario);
	         return "nuevoUsuario";
	     }
	     
	     int numAdmins = usuarioRepository.countUsuariosByRol("Administrador");
	     boolean esAdministrador = objUsuario.getRoles().stream()
	                             .anyMatch(rol -> "Administrador".equalsIgnoreCase(rol.getNombre()));
	     if (numAdmins >= 3 && esAdministrador) {
	         result.rejectValue("roles", "error.objUsuario", "No se pueden añadir más de 3 administradores.");
	         List<Rol> listaRoles = rolRepository.findAll();
	         model.addAttribute("listaRoles", listaRoles);
	         model.addAttribute("objUsuario", objUsuario);
	         return "nuevoUsuario";
	     }
	     
	     objUsuario.setPassword(PasswordUtil.hashPassword(objUsuario.getPassword()));
	     
	     // Obtener los parámetros de paginación desde la sesión
	     Integer offset = (Integer) session.getAttribute("offset");
	     Integer pageSize = (Integer) session.getAttribute("pageSize");
	     usuarioRepository.save(objUsuario);
	     List<Usuario> listaUsuarios = usuarioRepository.findAll();
	     model.addAttribute("listaUsuarios", listaUsuarios);
	     List<Rol> listaRoles = rolRepository.findAll();
		 model.addAttribute("listaRoles", listaRoles);
	     return "redirect:/home/mostrarGestionUsuarios/"+offset +"/" + pageSize;
	 }


		@PostMapping("/actualizar")
		public String actualizar(HttpSession session, @Valid @ModelAttribute("objUsuario") Usuario objUsuario, BindingResult result, Model model, @RequestParam("nombreRol") String nombreRol) {
		    if (result.hasErrors()) {
		        List<Rol> listaRoles = rolRepository.findAll();
		        model.addAttribute("listaRoles", listaRoles);
		        return "editarUsuario";
		    }
		    Usuario objUsuarioBD = usuarioRepository.findById(objUsuario.getId());
		    if (objUsuarioBD != null) {
		        if (usuarioRepository.findByCorreo(objUsuario.getCorreo()) != null && !objUsuarioBD.getCorreo().equals(objUsuario.getCorreo())) {
		            result.rejectValue("correo", "error.objUsuario", "El correo ya está en uso");
		            List<Rol> listaRoles = rolRepository.findAll();
		            model.addAttribute("listaRoles", listaRoles);
		            return "editarUsuario";
		        }
		        String password = objUsuario.getPassword();
		        if (password != null && !password.isEmpty()) {
		        	if (isShort(password)) {
			   	         result.rejectValue("password", "error.objUsuario", "La contraseña debe tener al menos 6 caracteres.");
			   	         List<Rol> listaRoles = rolRepository.findAll();
			   	         model.addAttribute("listaRoles", listaRoles);
			   	         return "nuevoUsuario";
			   	     }
	
			   	     if (isLong(password)) {
			   	         result.rejectValue("password", "error.objUsuario", "La contraseña no puede tener más de 26 caracteres.");
			   	         List<Rol> listaRoles = rolRepository.findAll();
			   	         model.addAttribute("listaRoles", listaRoles);
			   	         return "nuevoUsuario";
			   	     }
	
			   	     if (isInvalid(password)) {
			   	         result.rejectValue("password", "error.objUsuario", "La contraseña debe incluir al menos una mayúscula y una minúscula.");
			   	         List<Rol> listaRoles = rolRepository.findAll();
			   	         model.addAttribute("listaRoles", listaRoles);
			   	         return "nuevoUsuario";
			   	     }
		            objUsuarioBD.setPassword(PasswordUtil.hashPassword(objUsuario.getPassword()));
		        }
		        objUsuarioBD.setCorreo(objUsuario.getCorreo());
		        objUsuarioBD.setEstado(objUsuario.getEstado());
		        Rol rolSeleccionado = rolRepository.findByNombre(nombreRol);
		        if (rolSeleccionado != null) {
		            objUsuarioBD.getRoles().clear();
		            objUsuarioBD.getRoles().add(rolSeleccionado);
		        }
		        usuarioRepository.save(objUsuarioBD);
		    }
		    // Obtener los parámetros de paginación desde la sesión
		    Integer offset = (Integer) session.getAttribute("offset");
		    Integer pageSize = (Integer) session.getAttribute("pageSize");
		    List<Usuario> listaUsuarios = usuarioRepository.findAll();
		    model.addAttribute("listaUsuarios", listaUsuarios);
		    List<Rol> listaRoles = rolRepository.findAll();
		    model.addAttribute("listaRoles", listaRoles);
		    return "redirect:/home/mostrarGestionUsuarios/"+offset +"/" + pageSize;
		}

		
		@GetMapping("/usuariosxRol")
		public String usuariosxRol(@RequestParam(value = "nombreRol", required = false) String nombreRol, Model model) {
		    List<Usuario> listaUsuarios = usuarioRepository.findByRolesNombre(nombreRol);
		    
		    if (nombreRol.equals("Todos")) {
		    	return "redirect:/home/mostrarGestionUsuarios/0/10";
		    } else {
		        listaUsuarios = usuarioRepository.findByRolesNombre(nombreRol);
		        String numeroDeElementos = "resultados filtrados";
				model.addAttribute("numeroDeElementos", numeroDeElementos);
		    }
		    
		    model.addAttribute("listaUsuarios", listaUsuarios);
		    List<Rol> listaRoles = rolRepository.findAll();
		    model.addAttribute("listaRoles", listaRoles);
		    return "gestionUsuarios";
		}
		
		
		// Validar contraseña:
		private boolean isShort(String password) {
		    // Verifica si la contraseña es más corta que 6 caracteres
		    return password == null || password.length() < 6;
		}

		private boolean isLong(String password) {
		    // Verifica si la contraseña es más larga que 26 caracteres
		    return password.length() > 26;
		}

		private boolean isInvalid(String password) {
		    // Verifica si la contraseña tiene al menos una mayúscula y una minúscula
		    boolean hasUpperCase = password.chars().anyMatch(Character::isUpperCase);
		    boolean hasLowerCase = password.chars().anyMatch(Character::isLowerCase);
		    return !(hasUpperCase && hasLowerCase);
		}

		
}
