package com.IsilERPSpring.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@GetMapping("/")
	public String mostrarIndex(HttpServletRequest request) {
        return "index";
	}
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request) {
	    request.getSession().invalidate();
	    
	    return "redirect:/";
	}
	
	@RequestMapping(value="/validarUsuario", method=RequestMethod.POST)
	public String validarUsuario(HttpServletRequest request, @RequestParam("email") String email, @RequestParam("password") String password, Model model) {
	    Usuario objUsuario = usuarioRepository.findByCorreo(email);
	    if (objUsuario != null && PasswordUtil.matches(password, objUsuario.getPassword())) {
	    	request.getSession().setAttribute("usuario", objUsuario);
	        List<String> rolesNombres = objUsuario.getRoles().stream()
	                                               .map(Rol::getNombre)
	                                               .collect(Collectors.toList());
	         request.getSession().setAttribute("roles", rolesNombres);
	        return "principal";
	    } else {
	        model.addAttribute("error", "Correo o contraseña incorrectos");
	        return "redirect:/";
	    }
	}
	
	public Page<Usuario> usuariosxPaginacion(int offset, int pageSize){
		Page<Usuario> usuarios = usuarioRepository.findAll(PageRequest.of(offset, pageSize));
		return usuarios;
	}
	
	@GetMapping("/mostrarGestionUsuarios/{offset}/{pageSize}")
	public String mostrarGestionUsuarios(@PathVariable(value = "offset") Integer offset,
	                                      @PathVariable(value = "pageSize") Integer pageSize,
	                                      Model model, HttpServletRequest request, HttpSession session) {
	    if (offset == null) {
	        offset = 0;
	    }
	    if (pageSize == null) {
	    	pageSize = 10;
	    }
	    session.setAttribute("offset", offset);
	    session.setAttribute("pageSize", pageSize);

	    Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

	    if (usuario != null) {
	        model.addAttribute("usuario", usuario);

	        Page<Usuario> listaUsuarios = usuariosxPaginacion(offset, pageSize);  

	        model.addAttribute("listaUsuarios", listaUsuarios);  
	        model.addAttribute("currentPage", offset);
	        
	        int elementos = listaUsuarios.getNumberOfElements();
			String numeroDeElementos = elementos + " resultados";
	        model.addAttribute("numeroDeElementos", numeroDeElementos);
	        int totalPages = listaUsuarios.getTotalPages();
	        model.addAttribute("totalPages", totalPages);
	        
	        List<Rol> listaRoles = rolRepository.findAll();
	        model.addAttribute("listaRoles", listaRoles);
	    } else {
	        return "redirect:/";
	    }

	    return "gestionUsuarios";
	}

	
	@GetMapping("/mostrarPrincipal")
	public String mostrarPrincipal(HttpServletRequest request) {
		return "principal";
	}
	
	public Page<Proveedor> proveedorxPaginacion(int offset, int pageSize){
	    List<Proveedor> proveedoresFiltrados = proveedorRepository.findAll().stream()
	            .filter(proveedor -> !proveedor.getEstado().equals("Inactivo"))
	            .collect(Collectors.toList());

	    int start = offset * pageSize;
	    int end = Math.min((start + pageSize), proveedoresFiltrados.size());

	    if (start > end) {
	        return new PageImpl<>(new ArrayList<>());
	    }

	    List<Proveedor> sublist = proveedoresFiltrados.subList(start, end);

	    return new PageImpl<>(sublist, PageRequest.of(offset, pageSize), proveedoresFiltrados.size());
	}
	
	@GetMapping("/mostrarGestionProveedor/{offset}/{pageSize}")
	public String mostrarGestionProveedor(@PathVariable(value = "offset") Integer offset,
            @PathVariable(value = "pageSize") Integer pageSize,
            Model model, HttpServletRequest request, HttpSession session) {
		if (offset == null) {
	        offset = 0;
	    }
	    if (pageSize == null) {
	    	pageSize = 10;
	    }
	    session.setAttribute("offset", offset);
	    session.setAttribute("pageSize", pageSize);
	    
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	    
	    if (usuario != null) {
	        model.addAttribute("usuario", usuario);
	    }
	    else {
	    	return "redirect:/";
		}
	    
        Page<Proveedor> listaProveedor = proveedorxPaginacion(offset, pageSize); 

        model.addAttribute("listaProveedor", listaProveedor); 
        
        int elementos = listaProveedor.getNumberOfElements();
		String numeroDeElementos = elementos + " resultados";
        model.addAttribute("numeroDeElementos", numeroDeElementos);
        model.addAttribute("currentPage", offset);
        int totalPages = listaProveedor.getTotalPages();
        model.addAttribute("totalPages", totalPages);       
		
		
		List<Distrito> listaDistritos = distritoRepository.findAll();
		model.addAttribute("listaDistritos", listaDistritos);
		List<Categoria> listaCategorias = categoriaRepository.findAll();
        model.addAttribute("listaCategorias", listaCategorias);
		return "gestionProveedor";
	}
	
	public Page<Cliente> clientesxPaginacion(int offset, int pageSize){
	    List<Cliente> clientesFiltrados = clienteRepository.findAll().stream()
	            .filter(cliente -> !cliente.getEstadoCliente().equals("Inactivo"))
	            .collect(Collectors.toList());

	    int start = offset * pageSize;
	    int end = Math.min((start + pageSize), clientesFiltrados.size());

	    if (start > end) {
	        return new PageImpl<>(new ArrayList<>());
	    }

	    List<Cliente> sublist = clientesFiltrados.subList(start, end);

	    return new PageImpl<>(sublist, PageRequest.of(offset, pageSize), clientesFiltrados.size());
	}
	
	@GetMapping("/mostrarGestionCliente/{offset}/{pageSize}")
	public String mostrarGestionCliente(
			@PathVariable(value = "offset") Integer offset,
            @PathVariable(value = "pageSize") Integer pageSize,
            Model model, HttpServletRequest request, HttpSession session) {

		 if (offset == null) {
		        offset = 0;
		    }
		    if (pageSize == null) {
		    	pageSize = 10;
		    }
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);

		 Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

	    if (usuario != null) {
	        model.addAttribute("usuario", usuario);
	        
	        Page<Cliente> clientes = clientesxPaginacion(offset, pageSize); 

	        model.addAttribute("listaCliente", clientes); 
	        
	        int elementos = clientes.getNumberOfElements();
			String numeroDeElementos = elementos + " resultados";
	        model.addAttribute("numeroDeElementos", numeroDeElementos);
	        model.addAttribute("currentPage", offset);
	        int totalPages = clientes.getTotalPages();
	        model.addAttribute("totalPages", totalPages);
	    } else {
	        return "redirect:/";
	    }

	    return "gestionCliente";
	}
	
	public Page<Articulo> articuloxPaginacion(int offset, int pageSize){
	    List<Articulo> articulosFiltrados = articuloRepository.findAll().stream()
	            .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
	            .collect(Collectors.toList());

	    int start = offset * pageSize;
	    int end = Math.min((start + pageSize), articulosFiltrados.size());

	    if (start > end) {
	        return new PageImpl<>(new ArrayList<>());
	    }

	    List<Articulo> sublist = articulosFiltrados.subList(start, end);

	    return new PageImpl<>(sublist, PageRequest.of(offset, pageSize), articulosFiltrados.size());
	}

	@GetMapping("/mostrarGestionArticulo/{offset}/{pageSize}")
	public String mostrarGestionArticulo(@PathVariable(value = "offset") Integer offset,
            @PathVariable(value = "pageSize") Integer pageSize,
            Model model, HttpServletRequest request, HttpSession session) {
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	    
	    if (usuario != null) {
		Page<Articulo> listaArticulo = articuloxPaginacion(offset, pageSize); 

        model.addAttribute("listaArticulo",listaArticulo);
        
        int elementos = listaArticulo.getNumberOfElements();
		String numeroDeElementos = elementos + " resultados";
        model.addAttribute("numeroDeElementos", numeroDeElementos);
        model.addAttribute("currentPage", offset);
        int totalPages = listaArticulo.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        
        
		List<Categoria> listaCategorias = categoriaRepository.findAll();
        model.addAttribute("listaCategorias", listaCategorias);
		
	    }
	    else {
	    	return "redirect:/";
		}
	    return "gestionArticulo";
	}
	
	public Page<Pedido> pedidoxPaginacion(int offset, int pageSize){
		Page<Pedido> pedido = pedidoRepository.findAll(PageRequest.of(offset, pageSize));
		return pedido;
	}
	
	@GetMapping("/mostrarGestionPedido/{offset}/{pageSize}")
	public String mostrarGestionPedido(HttpServletRequest request, Model model, @PathVariable(value = "offset") Integer offset,
            @PathVariable(value = "pageSize") Integer pageSize, HttpSession session) {
		 if (offset == null) {
		        offset = 0;
		    }
		    if (pageSize == null) {
		    	pageSize = 10;
		    }
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);
		    
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	    
	    if (usuario != null) {
			Page<Pedido> listaPedido = pedidoxPaginacion(offset, pageSize);
			model.addAttribute("listaPedido",listaPedido);
			
			int elementos = listaPedido.getNumberOfElements();
			String numeroDeElementos = elementos + " resultados";
		    model.addAttribute("numeroDeElementos", numeroDeElementos);
	        model.addAttribute("currentPage", offset);
	        int totalPages = listaPedido.getTotalPages();
	        model.addAttribute("totalPages", totalPages);
			
			
			List<Cliente> listaCliente = clienteRepository.findAll();
			model.addAttribute("listaCliente", listaCliente);
	    }
	    else {
	    	return "redirect:/";
		}
		return "gestionPedido";		
	}
	
	
	public Page<OrdenCompra> comprasxPaginacion(int offset, int pageSize){
		Page<OrdenCompra> compras = ordencompraRepository.findAll(PageRequest.of(offset, pageSize));
		return compras;
	}
	
	@GetMapping("/mostrarGestionCompras/{offset}/{pageSize}")
	public String mostrarGestionCompras(HttpServletRequest request, Model model, @PathVariable(value = "offset") Integer offset,
            @PathVariable(value = "pageSize") Integer pageSize, HttpSession session) {
		
		 if (offset == null) {
		        offset = 0;
		    }
		    if (pageSize == null) {
		    	pageSize = 10;
		    }
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);
		    
		Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	    
	    if (usuario != null) {
			Page<OrdenCompra> listaCompras = comprasxPaginacion(offset, pageSize); 
			model.addAttribute("listaCompras", listaCompras);
			
			int elementos = listaCompras.getNumberOfElements();
			String numeroDeElementos = elementos + " resultados";
		    model.addAttribute("numeroDeElementos", numeroDeElementos);
	        model.addAttribute("currentPage", offset);
	        int totalPages = listaCompras.getTotalPages();
	        model.addAttribute("totalPages", totalPages);
			
	        List<Articulo> listaArticulo = articuloRepository.findAll().stream()
				    .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
				    .collect(Collectors.toList());
			model.addAttribute("listaArticulo",listaArticulo);
			List<Proveedor> listaProveedor = proveedorRepository.findAll().stream()
					.filter(proveedor -> !proveedor.getEstado().equals("Inactivo"))
		            .collect(Collectors.toList());
			model.addAttribute("listaProveedor", listaProveedor);
	    }
	    else {
	    	return "redirect:/";
		}
		return "gestionCompras";	
	}
	
	public Page<Ventas> ventasxPaginacion(int offset, int pageSize){
	    //return new PageImpl<>(sublist, PageRequest.of(offset, pageSize), proveedoresFiltrados.size());
	    
		List<Ventas> ventasFiltrados = ventasRepository.findAll().stream()
	            .filter(ventas -> !ventas.getEstado().equals("Inactivo"))
	            .collect(Collectors.toList());
		
		 int start = offset * pageSize;
		 int end = Math.min((start + pageSize), ventasFiltrados.size());

		 if (start > end) {
			 return new PageImpl<>(new ArrayList<>());
		 }
		 
		 List<Ventas> sublist = ventasFiltrados.subList(start, end);

		return new PageImpl<>(sublist, PageRequest.of(offset, pageSize), ventasFiltrados.size());
	}
	
	@GetMapping("/mostrarGestionVentas/{offset}/{pageSize}")
    public String mostrarGestionVentas(HttpServletRequest request, Model model, @PathVariable(value = "offset") Integer offset,
            @PathVariable(value = "pageSize") Integer pageSize, HttpSession session) {
		
		 if (offset == null) {
		        offset = 0;
		    }
		    if (pageSize == null) {
		    	pageSize = 10;
		    }
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);
		    
		 Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		    if (usuario != null) {
		        model.addAttribute("usuario", usuario);
		    }
		    else {
		    	return "redirect:/";
			}
		    
		    Page<Ventas> listaVenta = ventasxPaginacion(offset, pageSize); 
	        model.addAttribute("currentPage", offset);
	        int totalPages = listaVenta.getTotalPages();
	        model.addAttribute("totalPages", totalPages);
	        int elementos = listaVenta.getNumberOfElements();
			String numeroDeElementos = elementos + " resultados";
	        model.addAttribute("numeroDeElementos", numeroDeElementos);
	        model.addAttribute("listaVenta",listaVenta);
	        List<Articulo> listaArticulos = articuloRepository.findAll().stream()
				    .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
				    .collect(Collectors.toList());
	        List<Cliente> listaCliente = clienteRepository.findAll();
	        List<Pedido> listaPedido = pedidoRepository.findAll();
	        model.addAttribute("listaArticulos", listaArticulos);
	        model.addAttribute("listaCliente", listaCliente);
	        model.addAttribute("listaPedido", listaPedido);
	        return "gestionVentas";
    }
	
	public Page<Categoria> categoriaxPaginacion(int offset, int pageSize){
		Page<Categoria> categoria = categoriaRepository.findAll(PageRequest.of(offset, pageSize));
		return categoria;
	}
	
	@GetMapping("/mostrarGestionCategorias/{offset}/{pageSize}")
	public String mostrarGestionCategorias(HttpServletRequest request, @PathVariable(value = "offset") Integer offset,
            @PathVariable(value = "pageSize") Integer pageSize, HttpSession session,
	        Model model) {
		 if (offset == null) {
		        offset = 0;
		    }
		    if (pageSize == null) {
		    	pageSize = 10;
		    }
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);
		    
		 Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		    if (usuario != null) {
		        model.addAttribute("usuario", usuario);
		    }
		    else {
		    	return "redirect:/";
			}
		    
		    Page<Categoria> categorias = categoriaxPaginacion(offset, pageSize); 
	        model.addAttribute("currentPage", offset);
	        int totalPages = categorias.getTotalPages();
	        model.addAttribute("totalPages", totalPages);
	        int elementos = categorias.getNumberOfElements();
			String numeroDeElementos = elementos + " resultados";
	        model.addAttribute("numeroDeElementos", numeroDeElementos);
	        model.addAttribute("categorias",categorias);
	    return "gestionCategoria";
	}
}
