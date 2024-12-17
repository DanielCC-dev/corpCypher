package com.IsilERPSpring.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.IsilERPSpring.entity.Articulo;
import com.IsilERPSpring.entity.Categoria;
import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.DetalleVentas;
import com.IsilERPSpring.entity.Pedido;
import com.IsilERPSpring.entity.Proveedor;
import com.IsilERPSpring.entity.Rol;
import com.IsilERPSpring.entity.Usuario;
import com.IsilERPSpring.entity.Ventas;
import com.IsilERPSpring.repository.ArticuloRepository;
import com.IsilERPSpring.repository.ClienteRepository;
import com.IsilERPSpring.repository.DetalleVentasRepository;
import com.IsilERPSpring.repository.PedidoRepository;
import com.IsilERPSpring.repository.ProveedorRepository;
import com.IsilERPSpring.repository.VentasRepository;

@Controller
@RequestMapping ("/ventas")
public class VentasController {
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ArticuloRepository articuloRepository;
	
    @Autowired
    ProveedorRepository proveedorRepository;
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	VentasRepository ventasRepository;
	
	@Autowired
	DetalleVentasRepository detalleVentasRepository;
	
	public List<Articulo> obtenerArticulosActivos() {
	    return articuloRepository.findAll().stream()
	        .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
	        .collect(Collectors.toList());
	}
	
	public List<Pedido> obtenerPedidosRecibidos(){
		return pedidoRepository.findAllByOrderByIdDesc().stream()
				.filter(pedido -> pedido.getEstado().equals("Recibido"))
				.collect(Collectors.toList());
	}
	
	 @GetMapping("/mostrarAnular/{id}")
	 public String eliminar(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
	            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
		 Ventas objVentasBD = ventasRepository.findById(id);
			
		 Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");

		 if (usuario != null) {
			model.addAttribute("usuario", usuario);
		 }
		 else {
			 return "redirect:/home/";
			}
		 System.out.println("Usuario en sesión: " + usuario);
		 List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(objVentasBD);
		 model.addAttribute("objVentas", objVentasBD);
		 model.addAttribute("listaDetalle", listaDetalle);
		 session.setAttribute("offset", offset);
		 session.setAttribute("pageSize", pageSize);
		 model.addAttribute("pageSize", pageSize);
		 
		 return "anularVenta";
	 }
	 
		@PostMapping("/anularVenta")
		public String eliminar(HttpSession session, HttpServletRequest request,  @ModelAttribute("objVentas") Ventas ventas, @RequestParam("pedidoId") int pedidoId, Model model) {
			Pedido pedido = pedidoRepository.findById(pedidoId);
		    pedido.setEstado("Recibido");
		    
			Ventas objVentasBD = ventasRepository.findById(ventas.getId());
			objVentasBD.setMotivoEliminacion(ventas.getMotivoEliminacion());
			objVentasBD.setEstado("Inactivo"); 
			objVentasBD.setFechaEliminacion(Date.valueOf(LocalDate.now()));
			
			Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	        if (usuario != null) {
	        	objVentasBD.setUsuarioEliminacion(usuario.getCorreo());
	        }
	        
	        List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(objVentasBD);
	        for (DetalleVentas detalle : listaDetalle) {
	            Articulo articulo = detalle.getArticulo();
	            if (articulo != null) {
	                int nuevoStock = articulo.getStock() + detalle.getCantidad();
	                articulo.setStock(nuevoStock);
	                articuloRepository.save(articulo); // Guardar los cambios
	            }
	        }
	        
	        ventasRepository.save(objVentasBD);
			//List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(objVentasBD);
			model.addAttribute("ventas", objVentasBD);
			model.addAttribute("listaDetalle", listaDetalle);
			Integer offset = (Integer) session.getAttribute("offset");
		     Integer pageSize = (Integer) session.getAttribute("pageSize");
		    return "redirect:/home/mostrarGestionVentas/"+offset +"/" + pageSize;
		}
	
	@RequestMapping(value="/buscarVenta", method=RequestMethod.GET)
	public String buscarVenta(HttpServletRequest request, @RequestParam(value="fechaInicio", required = false) String fechaInicioStr, 
	        @RequestParam(value="fechaFin", required = false) String fechaFinStr, Model model) {
		LocalDate fechaActual = LocalDate.now();
	    Date fechaInicio = null;
	    Date fechaFin = null;
	    
	    if (fechaInicioStr == null || fechaInicioStr.trim().isEmpty()) {
	        fechaInicio = Date.valueOf(fechaActual);
	        model.addAttribute("mensajeFI", "Error. La fecha de inicio no fue añadida.");
	        return "redirect:/home/mostrarGestionVentas/0/10";	       
	    } else {
	        fechaInicio = Date.valueOf(fechaInicioStr);
	    }

	    if (fechaFinStr == null || fechaFinStr.trim().isEmpty()) {
	        fechaFin = Date.valueOf(fechaActual);
	        model.addAttribute("mensajeFF", "Se puso la fecha actual en Fecha Fin automaticamente porque no se añadió una fecha.");
	    } else {
	        fechaFin = Date.valueOf(fechaFinStr);
	    }
	    String numeroDeElementos = "resultados filtrados entre " + fechaInicio + " y " + fechaFin;
		 model.addAttribute("numeroDeElementos", numeroDeElementos);
		List<Ventas> listaVenta = ventasRepository.findByFechaVentaBetween(fechaInicio, fechaFin);
		model.addAttribute("listaVenta", listaVenta);
		return "gestionVentas";
	}
	
	@GetMapping("/ver/{id}")
	public String ver(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session){
		Ventas objVentasBD = ventasRepository.findById(id);
		List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(objVentasBD);
		model.addAttribute("ventas", objVentasBD);
		model.addAttribute("listaDetalle", listaDetalle);
		
		session.setAttribute("offset", offset);
		 session.setAttribute("pageSize", pageSize);
		 model.addAttribute("pageSize", pageSize);
		return "verVenta";
	}
	
	@PostMapping("/mostrarNuevaVenta")
	public String mostrarNuevo(HttpServletRequest request, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
		Ventas ventas = new Ventas();
		model.addAttribute("objVentas", ventas);
		List<Ventas> listaVenta = ventasRepository.findAll();
		List<Articulo> listaArticulos = obtenerArticulosActivos();
		List<Cliente> listaCliente = clienteRepository.findAll();
		List<Pedido> listaPedido = obtenerPedidosRecibidos();
		model.addAttribute("listaVenta", listaVenta);
		model.addAttribute("listaArticulos", listaArticulos);
		model.addAttribute("listaCliente", listaCliente);
		model.addAttribute("listaPedido", listaPedido);
		
		session.setAttribute("offset", offset);
		 session.setAttribute("pageSize", pageSize);
		 model.addAttribute("pageSize", pageSize);
		return "nuevaVenta";
	}
	
    @GetMapping("/cargarArticulo")
    public String cargarArticulo( @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session, @ModelAttribute("objVentas")Ventas ventas, @RequestParam("idArticulo") int idArticulo, Model model) {
        Ventas venta = ventasRepository.findById(ventas.getId());
        Articulo articulo = articuloRepository.findById(idArticulo);

        if (venta != null && articulo != null) {           
            List<Articulo> listaArticulos = obtenerArticulosActivos();
    		List<Cliente> listaCliente = clienteRepository.findAll();
    		List<Pedido> listaPedido = obtenerPedidosRecibidos();
    		model.addAttribute("listaArticulos", listaArticulos);
    		model.addAttribute("listaCliente", listaCliente);
    		model.addAttribute("listaPedido", listaPedido);
    		model.addAttribute("objVentas", venta);
            model.addAttribute("articuloSeleccionado", articulo);
            List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(venta);
			model.addAttribute("listaDetalle", listaDetalle);
			session.setAttribute("offset", offset);
			 session.setAttribute("pageSize", pageSize);
			 model.addAttribute("pageSize", pageSize);
        }
        return "nuevaVenta";
    }


	@RequestMapping(value="/guardar", method=RequestMethod.GET, params="grabarVenta")
	public String guardarVenta ( HttpServletRequest request, @ModelAttribute("objVentas")Ventas ventas, Model model,  @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session, BindingResult result ) {
		if (result.hasErrors()) {
			model.addAttribute("objVentas", ventas);
			List<Articulo> listaArticulos = articuloRepository.findAll().stream()
			        .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
			        .collect(Collectors.toList());;
			List<Cliente> listaCliente = clienteRepository.findAll();
			List<Pedido> listaPedido = obtenerPedidosRecibidos();
			model.addAttribute("listaArticulos", listaArticulos);
			model.addAttribute("listaCliente", listaCliente);
			model.addAttribute("listaPedido", listaPedido);
			session.setAttribute("offset", offset);
			 session.setAttribute("pageSize", pageSize);
			 model.addAttribute("pageSize", pageSize);
			return "nuevaVenta";
	     }
		int idPedido = ventas.getPedido().getId();
		Pedido pedido = pedidoRepository.findById(idPedido);
		//System.out.println("Tipo venta " + ventas.getTipoVenta());
		if("Online".equals(ventas.getTipoVenta())) {
			pedido.setEstado("Enviado");
		} 
		else if("Presencial".equals(ventas.getTipoVenta())){
			pedido.setEstado("Atendido");
		}
		if (ventas.getEstado() == null || ventas.getEstado().isEmpty()) {
	         ventas.setEstado("Activo");
	     }		
		if (ventas.getPedido().getId() == 0) {
			 result.rejectValue("pedido.id", "error.ventas", "El pedido es necesario");
			 model.addAttribute("objVentas", ventas);
				List<Articulo> listaArticulos = articuloRepository.findAll().stream()
				        .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
				        .collect(Collectors.toList());;
				List<Cliente> listaCliente = clienteRepository.findAll();
				List<Pedido> listaPedido = obtenerPedidosRecibidos();
				model.addAttribute("listaArticulos", listaArticulos);
				model.addAttribute("listaCliente", listaCliente);
				model.addAttribute("listaPedido", listaPedido);
				session.setAttribute("offset", offset);
				 session.setAttribute("pageSize", pageSize);
				 model.addAttribute("pageSize", pageSize);
				 return "nuevaVenta";
	     }	
		
		ventasRepository.save(ventas);
		model.addAttribute("objVentas", ventas);
		List<Articulo> listaArticulos = articuloRepository.findAll().stream()
		        .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
		        .collect(Collectors.toList());;
		List<Cliente> listaCliente = clienteRepository.findAll();
		List<Pedido> listaPedido = obtenerPedidosRecibidos();
		model.addAttribute("listaArticulos", listaArticulos);
		model.addAttribute("listaCliente", listaCliente);
		model.addAttribute("listaPedido", listaPedido);
		session.setAttribute("offset", offset);
		 session.setAttribute("pageSize", pageSize);
		 model.addAttribute("pageSize", pageSize);
		return "nuevaVenta";
	}
	
	@PostMapping("/agregarArticulo")
	public String agregarArticulo( @ModelAttribute("objVentas") Ventas ventas, @RequestParam("idArticulo") int idArticulo, @RequestParam("cantidad") int cantidad,Model model,  @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {

	    // Buscar la venta y el artículo
	    Ventas venta = ventasRepository.findById(ventas.getId());
	    Articulo articulo = articuloRepository.findById(idArticulo);

	    if (venta != null && articulo != null) {
	        // Verificar si el artículo ya está en el detalle de ventas
	        List<DetalleVentas> detallesExistentes = detalleVentasRepository.findByVentas(venta);
	        boolean articuloYaAgregado = detallesExistentes.stream()
	                .anyMatch(detalle -> detalle.getArticulo().getId() == idArticulo);

	        if (articuloYaAgregado) {
	        	List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(venta);
		        model.addAttribute("listaDetalle", listaDetalle);
	        	List<Articulo> listaArticulos = obtenerArticulosActivos();
	    	    List<Cliente> listaCliente = clienteRepository.findAll();
	    	    List<Pedido> listaPedido = obtenerPedidosRecibidos();
	    	    model.addAttribute("listaArticulos", listaArticulos);
	    	    model.addAttribute("listaCliente", listaCliente);
	    	    model.addAttribute("listaPedido", listaPedido);
	    	    model.addAttribute("objVentas", venta);
	            model.addAttribute("mensaje", "Error: El artículo ya fue agregado al detalle de la venta.");
	        } else if (articulo.getStock() < cantidad) {
	        	List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(venta);
		        model.addAttribute("listaDetalle", listaDetalle);
	        	List<Articulo> listaArticulos = obtenerArticulosActivos();
	    	    List<Cliente> listaCliente = clienteRepository.findAll();
	    	    List<Pedido> listaPedido = obtenerPedidosRecibidos();
	    	    model.addAttribute("listaArticulos", listaArticulos);
	    	    model.addAttribute("listaCliente", listaCliente);
	    	    model.addAttribute("listaPedido", listaPedido);
	    	    model.addAttribute("objVentas", venta);
	            model.addAttribute("mensaje", "Error: No hay suficiente stock disponible para el artículo.");
	        } else {
	            double precioItemTotal = articulo.getPrecio() * cantidad;

	            // Crear el detalle de la venta
	            DetalleVentas detalle = new DetalleVentas();
	            detalle.setVentas(venta);
	            detalle.setArticulo(articulo);
	            detalle.setCantidad(cantidad);
	            BigDecimal precioItemTotalRedondeado = new BigDecimal(precioItemTotal)
	                                                      .setScale(2, RoundingMode.HALF_UP);
	            detalle.setPrecioItemTotal(precioItemTotalRedondeado.doubleValue());

	            // Guardar el detalle
	            detalleVentasRepository.save(detalle);

	            // Actualizar el stock del artículo
	            articulo.setStock(articulo.getStock() - cantidad);
	            articuloRepository.save(articulo);

	            // Actualizar el precio total de la venta
	            double nuevoPrecioTotal = venta.getPrecioTotal() + precioItemTotalRedondeado.doubleValue();
	            
	            BigDecimal precioTotalVentaRedondeado = new BigDecimal(nuevoPrecioTotal)
	                                                        .setScale(2, RoundingMode.HALF_UP);
	            
	            venta.setPrecioTotal(precioTotalVentaRedondeado.doubleValue());
	            ventasRepository.save(venta);

	            // Actualizar la lista de detalles
	            List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(venta);
	            model.addAttribute("listaDetalle", listaDetalle);

	            model.addAttribute("objVentas", venta);
	            model.addAttribute("mensaje", "Artículo agregado al detalle de la venta con éxito.");
	            
	        }
	    } else {
	        model.addAttribute("mensaje", "Error: Venta o artículo no encontrado.");
	    }

	    // Cargar las listas necesarias para la vista
	    List<Articulo> listaArticulos = obtenerArticulosActivos();
	    List<Cliente> listaCliente = clienteRepository.findAll();
	    List<Pedido> listaPedido = obtenerPedidosRecibidos();
	    model.addAttribute("listaArticulos", listaArticulos);
	    model.addAttribute("listaCliente", listaCliente);
	    model.addAttribute("listaPedido", listaPedido);
	    session.setAttribute("offset", offset);
		 session.setAttribute("pageSize", pageSize);
		 model.addAttribute("pageSize", pageSize);
	    return "nuevaVenta";
	}

}

