package com.IsilERPSpring.controller;

import java.util.Collections;
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
import org.springframework.web.bind.annotation.RequestParam;

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
		 List<Usuario> listaUsuarios = usuarioRepository.findAll();
		 model.addAttribute("listaUsuarios", listaUsuarios);
		 return "gestionUsuarios";
	 }
		
	 @PostMapping("/mostrarNuevo")
		public String mostrarNuevo(HttpServletRequest request, Model model) {
			Usuario objUsuario= new Usuario();
			model.addAttribute("objUsuario",objUsuario);
			List<Rol> listaRoles = rolRepository.findAll();
			model.addAttribute("listaRoles",listaRoles);
			return "nuevoUsuario";
		}
		
		  
	 @PostMapping("/registrar")
		    public String registrar(HttpServletRequest request, @Valid @ModelAttribute("objUsuario") Usuario objUsuario, BindingResult result, Model model) {
		        if (result.hasErrors()) {
		        	List<Rol> listaRoles = rolRepository.findAll();
		            model.addAttribute("listaRoles", listaRoles);
		        	return "nuevoUsuario";
		        }
		        if (usuarioRepository.findByCorreo(objUsuario.getCorreo()) != null) {
		            result.rejectValue("correo", "error.objUsuario", "El correo ya está en uso");
		            List<Rol> listaRoles = rolRepository.findAll();
		            model.addAttribute("listaRoles", listaRoles);
		            return "nuevoUsuario";
		        }
		        usuarioRepository.save(objUsuario);
		        List<Usuario> listaUsuarios = usuarioRepository.findAll();
		        model.addAttribute("listaUsuarios", listaUsuarios);
		        return "gestionUsuarios";
		    }
		
		@GetMapping("/editar/{id}")
		public String editar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
			Usuario objUsuario = usuarioRepository.findById(id);
			model.addAttribute("objUsuario",objUsuario);
			List<Rol> listaRoles = rolRepository.findAll();
            model.addAttribute("listaRoles", listaRoles);
			return "editarUsuario";
		}

		@PostMapping("/actualizar")
		public String actualizar(HttpServletRequest request, @Valid @ModelAttribute("objUsuario") Usuario objUsuario, BindingResult result, Model model, @RequestParam("nombreRol") String nombreRol) {
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
		        objUsuarioBD.setCorreo(objUsuario.getCorreo());
		        objUsuarioBD.setEstado(objUsuario.getEstado());
		        objUsuarioBD.setPassword(objUsuario.getPassword());
		        Rol rolSeleccionado = rolRepository.findByNombre(nombreRol);
		        if (rolSeleccionado != null) {
		            objUsuarioBD.getRoles().clear();
		            objUsuarioBD.getRoles().add(rolSeleccionado);
		        }
		        usuarioRepository.save(objUsuarioBD);
		    }
		    List<Usuario> listaUsuarios = usuarioRepository.findAll();
		    model.addAttribute("listaUsuarios", listaUsuarios);
		    return "gestionUsuarios";
		}

		
		@GetMapping("/usuariosxRol")
		public String usuariosxRol(@RequestParam(value = "nombreRol", required = false) String nombreRol, Model model) {
		    List<Usuario> listaUsuarios = usuarioRepository.findByRolesNombre(nombreRol);
		    
		    if (nombreRol.equals("Todos")) {
		        listaUsuarios = usuarioRepository.findAll();
		    } else {
		        listaUsuarios = usuarioRepository.findByRolesNombre(nombreRol);
		    }
		    
		    model.addAttribute("listaUsuarios", listaUsuarios);
		    List<Rol> listaRoles = rolRepository.findAll();
		    model.addAttribute("listaRoles", listaRoles);
		    return "gestionUsuarios";
		}

		
}
