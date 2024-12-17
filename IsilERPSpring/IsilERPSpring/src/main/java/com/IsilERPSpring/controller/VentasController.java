package com.IsilERPSpring.controller;

import java.sql.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping ("/ventas")
public class VentasController {
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	ArticuloRepository articuloRepository;
	
	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	VentasRepository ventasRepository;
	
	@Autowired
	DetalleVentasRepository detalleVentasRepository;
	
	@RequestMapping(value="/buscarVenta", method=RequestMethod.GET)
	public String buscarVenta(HttpServletRequest request, @RequestParam(value="fechaInicio")Date fechaInicio, @RequestParam(value="fechaFin")Date fechaFin, Model model) {
		List<Ventas> listaVenta = ventasRepository.findByFechaVentaBetween(fechaInicio, fechaFin);
		model.addAttribute("listaVenta", listaVenta);
		return "gestionVentas";
	}
	
	@GetMapping("/ver/{id}")
	public String ver(HttpServletRequest request, @PathVariable("id") int id, Model model){
		Ventas objVentasBD = ventasRepository.findById(id);
		List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(objVentasBD);
		model.addAttribute("ventas", objVentasBD);
		model.addAttribute("listaDetalle", listaDetalle);
		return "verVenta";
	}
	
	@PostMapping("/mostrarNuevaVenta")
	public String mostrarNuevo(HttpServletRequest request, Model model) {
		Ventas ventas = new Ventas();
		model.addAttribute("objVentas", ventas);
		List<Ventas> listaVenta = ventasRepository.findAll();
		List<Articulo> listaArticulos = articuloRepository.findAll();
		List<Cliente> listaCliente = clienteRepository.findAll();
		List<Pedido> listaPedido = pedidoRepository.findAll();
		model.addAttribute("listaVenta", listaVenta);
		model.addAttribute("listaArticulos", listaArticulos);
		model.addAttribute("listaCliente", listaCliente);
		model.addAttribute("listaPedido", listaPedido);
		return "nuevaVenta";
	}
	
	@RequestMapping(value="/guardar", method=RequestMethod.GET, params="grabarVenta")
	public String guardarVenta (HttpServletRequest request, @ModelAttribute("objVentas")Ventas ventas, Model model) {
		ventasRepository.save(ventas);
		model.addAttribute("objVentas", ventas);
		List<Articulo> listaArticulos = articuloRepository.findAll();
		List<Cliente> listaCliente = clienteRepository.findAll();
		List<Pedido> listaPedido = pedidoRepository.findAll();
		model.addAttribute("listaArticulos", listaArticulos);
		model.addAttribute("listaCliente", listaCliente);
		model.addAttribute("listaPedido", listaPedido);
		return "nuevaVenta";
	}
	
	
	@RequestMapping(value="/guardar", method=RequestMethod.GET, params="grabarComponentes")
	public String guardarComponentes (HttpServletRequest request, @ModelAttribute("objVentas")Ventas ventas, @RequestParam("idArticulo") int idArticulo, @RequestParam("idCliente") int idCliente, @RequestParam("idPedido") int idPedido, Model model) {
		Ventas ventasBD = ventasRepository.findById(ventas.getId());
		Cliente clienteBD = clienteRepository.findById(idCliente);
		Articulo articuloBD = articuloRepository.findById(idArticulo);
		Pedido pedidoBD = pedidoRepository.findById(idPedido);
		DetalleVentas detalleVentas = new DetalleVentas();
		detalleVentas.setVentas(ventasBD);
		detalleVentas.setCliente(clienteBD);
		detalleVentas.setArticulo(articuloBD);
		detalleVentas.setPedido(pedidoBD);
		detalleVentasRepository.save(detalleVentas);
		List<DetalleVentas> listaDetalle = detalleVentasRepository.findByVentas(ventasBD);
		model.addAttribute("listaDetalle", listaDetalle);
		List<Articulo> listaArticulos = articuloRepository.findAll();
		List<Cliente> listaCliente = clienteRepository.findAll();
		List<Pedido> listaPedido = pedidoRepository.findAll();
		model.addAttribute("listaArticulos", listaArticulos);
		model.addAttribute("listaCliente", listaCliente);
		model.addAttribute("listaPedido", listaPedido);
		model.addAttribute("objVentas", ventasBD);
		return "nuevaVenta";
	}
	
	
	
	
}

