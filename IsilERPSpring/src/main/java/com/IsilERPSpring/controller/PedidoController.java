package com.IsilERPSpring.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.IsilERPSpring.entity.Articulo;
import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.DetalleVentas;
import com.IsilERPSpring.entity.Pedido;
import com.IsilERPSpring.entity.Ventas;
import com.IsilERPSpring.repository.ArticuloRepository;
import com.IsilERPSpring.repository.ClienteRepository;
import com.IsilERPSpring.repository.DetalleVentasRepository;
import com.IsilERPSpring.repository.PedidoRepository;
import com.IsilERPSpring.repository.VentasRepository;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	ArticuloRepository articuloRepository;
	
	@Autowired
	ClienteRepository clienteRepository;	

	@Autowired
	VentasRepository ventasRepository;
	
	@Autowired
	DetalleVentasRepository detalleVentasRepository;
	
	 @InitBinder
	    public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	    }
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
	 
		@GetMapping("/ver/{id}")
		public String ver(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
		    Pedido objPedidoBD = pedidoRepository.findById(id);
		    
		    if (objPedidoBD == null) {
		        return "error"; 
		    }
		    
		    Ventas objVentasBD = ventasRepository.findByPedidoAndEstado(objPedidoBD, "Activo"); 
		    
		    if (objVentasBD == null) {
		        return "error"; 
		    }

		    List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(objVentasBD);
		    
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);

		    model.addAttribute("pageSize", pageSize);
		    
		    model.addAttribute("ventas", objVentasBD);
		    model.addAttribute("listaDetalle", listaDetalle);
		    model.addAttribute("pedido", objPedidoBD);  

		    return "verPedido";
		}
	 
	 @PostMapping("/mostrarNuevo")
		public String mostrarNuevo(HttpServletRequest request, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
			Pedido objPedido= new Pedido();
			model.addAttribute("objPedido",objPedido);
			List<Cliente> listaCliente = clienteRepository.findAll();
			model.addAttribute("listaCliente",listaCliente);
			
		    session.setAttribute("offset", offset);
		    session.setAttribute("pageSize", pageSize);

		    model.addAttribute("pageSize", pageSize);
			return "nuevoPedido";
		}
	 
	 @PostMapping("/registrar")
	 public String registrar(HttpSession session, @ModelAttribute("objPedido") Pedido objPedido,  @RequestParam("nroDoc") String nroDoc, @RequestParam("actionType") String actionType, Model model ) {
	     Cliente cliente = clienteRepository.findByNroDoc(nroDoc);
	     if (cliente != null) {
	         objPedido.setCliente(cliente);
	         pedidoRepository.save(objPedido);

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

	         if ("salir".equals(actionType)) {
	        	 Integer offset = (Integer) session.getAttribute("offset");
	    		 Integer pageSize = (Integer) session.getAttribute("pageSize");
	        	 return  "redirect:/home/mostrarGestionPedido/" + offset + "/" + pageSize;
	         } else if ("continuar".equals(actionType)) {
	             return "nuevaVenta";
	         }
	     } else {
	         model.addAttribute("mensaje", "Error: El cliente no existe en el sistema. Añádalo en el área de Clientes.");
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
	         return "nuevoPedido";
	     }
	     Integer offset = (Integer) session.getAttribute("offset");
		 Integer pageSize = (Integer) session.getAttribute("pageSize");
	     return  "redirect:/home/mostrarGestionPedido/" + offset + "/" + pageSize;
	 }

	 @GetMapping("/buscarClientexDni")
	 public String buscarClientexDni(@RequestParam("dniCliente") String dniCliente, Model model, HttpSession session) {
	     Cliente cliente = clienteRepository.findByNroDoc(dniCliente);
	     if (cliente != null) {
	         List<Pedido> listaPedido = pedidoRepository.findByCliente(cliente);
	         model.addAttribute("listaPedido", listaPedido);
	         model.addAttribute("dniCliente", dniCliente);
	         
	         String numeroDeElementos = " resultados filtrados";
			 model.addAttribute("numeroDeElementos", numeroDeElementos);
	     } else {
		     return  "redirect:/home/mostrarGestionPedido/0/10";
	     }
	     List<Cliente> listaCliente = clienteRepository.findAll();
	     model.addAttribute("listaCliente", listaCliente);
	     return "gestionPedido";
	 }


}
