package com.IsilERPSpring.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.IsilERPSpring.entity.Articulo;
import com.IsilERPSpring.entity.Categoria;
import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.Distrito;
import com.IsilERPSpring.entity.OrdenCompra;
import com.IsilERPSpring.entity.Pedido;
import com.IsilERPSpring.entity.Proveedor;
import com.IsilERPSpring.entity.Rol;
import com.IsilERPSpring.entity.Usuario;
import com.IsilERPSpring.entity.Ventas;
import com.IsilERPSpring.repository.ArticuloRepository;
import com.IsilERPSpring.repository.CategoriaRepository;
import com.IsilERPSpring.repository.ClienteRepository;
import com.IsilERPSpring.repository.DistritoRepository;
import com.IsilERPSpring.repository.OrdenCompraRepository;
import com.IsilERPSpring.repository.PedidoRepository;
import com.IsilERPSpring.repository.ProveedorRepository;
import com.IsilERPSpring.repository.RolRepository;
import com.IsilERPSpring.repository.UsuarioRepository;
import com.IsilERPSpring.repository.VentasRepository;

@Controller
@RequestMapping ("/home")
public class HomeController {
	
	@Autowired 
	UsuarioRepository usuarioRepository;
	
	@Autowired
	ProveedorRepository proveedorRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ArticuloRepository articuloRepository;
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	RolRepository rolRepository;
	
	@Autowired
	DistritoRepository distritoRepository;	
	
	@Autowired
	OrdenCompraRepository ordencompraRepository;
	
	@Autowired
	VentasRepository ventasRepository;
	
	@Autowired
	CategoriaRepository categoriaRepository;
	
	
	
	@RequestMapping(value="validarUsuario", method=RequestMethod.POST)
	public String validarUsuario(HttpServletRequest request, @RequestParam(value="email") String email, @RequestParam(value="password") String password) {
		Usuario objUsuario = usuarioRepository.findByCorreoAndPassword(email, password);
		if(objUsuario!=null) {
			request.getSession().setAttribute("usuario", objUsuario);
	        //recuperamos el rol del usuario
	        List<String> rolesNombres = objUsuario.getRoles().stream()
	                                               .map(Rol::getNombre)
	                                               .collect(Collectors.toList());
	         request.getSession().setAttribute("roles", rolesNombres);
			return "principal";
		}
		else {
			return "index";
		}
	}
	
	@GetMapping("/mostrarGestionUsuarios")
	public String mostrarGestionUsuarios(HttpServletRequest request, Model model) {
		List<Usuario> listaUsuarios = usuarioRepository.findAll();
		model.addAttribute("listaUsuarios", listaUsuarios);
		List<Rol> listaRoles = rolRepository.findAll();
		model.addAttribute("listaRoles",listaRoles);
		return "gestionUsuarios";
	}
	
	
	@GetMapping("/mostrarPrincipal")
	public String mostrarPrincipal(HttpServletRequest request) {
		return "principal";
	}
	
	@GetMapping("/mostrarGestionProveedor")
	public String mostrarGestionProveedor(HttpServletRequest request, Model model) {
		List<Proveedor> listaProveedor = proveedorRepository.findAll();
		model.addAttribute("listaProveedor", listaProveedor);
		List<Distrito> listaDistritos = distritoRepository.findAll();
		model.addAttribute("listaDistritos", listaDistritos);
		List<Categoria> listaCategorias = categoriaRepository.findAll();
        model.addAttribute("listaCategorias", listaCategorias);
		return "gestionProveedor";
	}
	
	@GetMapping("/mostrarGestionCliente")
	public String mostrarGestionCliente(HttpServletRequest request, Model model) {
		// Recuperar el usuario desde la sesión
	    Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	    List<String> roles = (List<String>) request.getSession().getAttribute("roles");

	    // Pasar la información del usuario al modelo si está logeado
	    if (usuario != null) {
	        model.addAttribute("usuario", usuario);
	        model.addAttribute("roles", roles);
	    }
	    System.out.println("Usuario en sesión: " + usuario);
		List<Cliente> listaCliente = clienteRepository.findAll().stream()
			    .filter(cliente -> !cliente.getEstadoCliente().equals("Inactivo"))
			    .collect(Collectors.toList());
			model.addAttribute("listaCliente", listaCliente);
		return "gestionCliente";
	}

	@GetMapping("/mostrarGestionArticulo")
	public String mostrarGestionArticulo(HttpServletRequest request, Model model) {
		List<Articulo> listaArticulo = articuloRepository.findAll();
		model.addAttribute("listaArticulo",listaArticulo);
		List<Categoria> listaCategorias = categoriaRepository.findAll();
        model.addAttribute("listaCategorias", listaCategorias);
		return "gestionArticulo";
	}
	
	@GetMapping("/mostrarGestionPedido")
	public String mostrarGestionPedido(HttpServletRequest request, Model model) {
		List<Pedido> listaPedido = pedidoRepository.findAll();
		model.addAttribute("listaPedido",listaPedido);
		List<Cliente> listaCliente = clienteRepository.findAll();
		model.addAttribute("listaCliente", listaCliente);
		return "";		
	}
	
	@GetMapping("/mostrarGestionCompras")
	public String mostrarGestionCompras(HttpServletRequest request, Model model) {
		List<OrdenCompra> listaCompras = ordencompraRepository.findAll();
		model.addAttribute("listaCompras", listaCompras);
		return "gestionCompras";	
	}
	
	@GetMapping("/mostrarGestionVentas")
    public String mostrarGestionVentas(HttpServletRequest request, Model model) {
        List<Ventas> listaVenta = ventasRepository.findAll();
        model.addAttribute("listaVenta",listaVenta);
        List<Articulo> listaArticulos = articuloRepository.findAll();
        List<Cliente> listaCliente = clienteRepository.findAll();
        List<Pedido> listaPedido = pedidoRepository.findAll();
        model.addAttribute("listaArticulos", listaArticulos);
        model.addAttribute("listaCliente", listaCliente);
        model.addAttribute("listaPedido", listaPedido);
        return "";
    }
}
