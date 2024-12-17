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

import com.IsilERPSpring.entity.DetalleCompra;
import com.IsilERPSpring.entity.Insumo;
import com.IsilERPSpring.entity.OrdenCompra;
import com.IsilERPSpring.repository.OrdenCompraRepository;
import com.IsilERPSpring.repository.DetalleCompraRepository;
import com.IsilERPSpring.repository.InsumoRepository;

@Controller
@RequestMapping("/ordenCompra")
public class OrdenCompraController {

	@Autowired
	OrdenCompraRepository ordencompraRepository;
	
	@Autowired
	DetalleCompraRepository detallecompraRepository;
	
	@Autowired
	InsumoRepository insumoRepository;
	
	@RequestMapping(value="/buscarOrdenCompra", method=RequestMethod.GET)
		public String buscarOrdenCompra(HttpServletRequest request, @RequestParam(value="fechaInicio") Date fechaInicio, @RequestParam(value="fechaFin") Date fechaFin, Model model) {
		List<OrdenCompra> listaCompras = ordencompraRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);
		model.addAttribute("listaCompras", listaCompras);
		return "gestionCompras";
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(HttpServletRequest request, @PathVariable("id") int id) {
		OrdenCompra objOrdenCompraBD = ordencompraRepository.findById(id);
		String mensaje;
		if(objOrdenCompraBD.getEstado().compareTo("Pendiente de Aprovación")==0) {
			ordencompraRepository.deleteById(id);
			mensaje="La compra fue eliminada con exito";
		}else {
			mensaje="No se puede eliminar la orden de compra por tener un estado diferente a Pendiente de Aprovación";
		}
		return "redirect:/home/mostrarGestionCompras";
	}
	
	@GetMapping("/ver/{id}")
	public String ver(HttpServletRequest request, @PathVariable("id") int id, Model model){
		OrdenCompra objOrdenCompraBD = ordencompraRepository.findById(id);
		List<DetalleCompra> listaDetalle = detallecompraRepository.findByOrdenCompra(objOrdenCompraBD);
		model.addAttribute("ordenCompra", objOrdenCompraBD);
		model.addAttribute("listaDetalle", listaDetalle);
		return "verDetalleCompra";
	}
	
	@PostMapping("/mostrarNuevo")
	public String mostrarNuevo(HttpServletRequest request, Model model) {
		OrdenCompra ordenCompra = new OrdenCompra();
		model.addAttribute("objOrdenCompra", ordenCompra);
		return "nuevaCompra";
	}
	
	@RequestMapping(value="/guardar", method=RequestMethod.GET, params="grabarCabecera")
	public String guardarCabecera (HttpServletRequest request, @ModelAttribute("objOrdenCompra")OrdenCompra ordenCompra, Model model) {
		ordenCompra.setEstado("Pendiente de Aprovación");
		ordencompraRepository.save(ordenCompra);
		model.addAttribute("objOrdenCompra", ordenCompra);
		List<Insumo> listaInsumos = insumoRepository.findAll();
		model.addAttribute("listaInsumos", listaInsumos);
		return "nuevaCompra";
	}
	
	@RequestMapping(value="/guardar", method=RequestMethod.GET, params="grabarDetalle")
	public String guardarDetalle(HttpServletRequest request, @ModelAttribute("objOrdenCompra")OrdenCompra ordenCompra, @RequestParam("idInsumo") int idInsumo, @RequestParam("cantidad") int cantidad, @RequestParam("precioUnitario") double precioUnitario, @RequestParam("precioTotal") double precioTotal, Model model) {
		OrdenCompra ordenCompraBD = ordencompraRepository.findById(ordenCompra.getId());
		Insumo objInsumoBD = insumoRepository.findById(idInsumo);
		DetalleCompra detalleCompra = new DetalleCompra();
		detalleCompra.setCantidad(cantidad);
		detalleCompra.setPrecioUnitario(precioUnitario);
		detalleCompra.setPrecioTotal(precioTotal);
		detalleCompra.setInsumo(objInsumoBD);
		detalleCompra.setOrdenCompra(ordenCompraBD);
		detallecompraRepository.save(detalleCompra);
		List<DetalleCompra> listaDetalle = detallecompraRepository.findByOrdenCompra(ordenCompraBD);
		model.addAttribute("listaDetalle", listaDetalle);
		model.addAttribute("objOrdenCompra", ordenCompraBD);
		List<Insumo> listaInsumos = insumoRepository.findAll();
		model.addAttribute("listaInsumos", listaInsumos);
		return "nuevaCompra";
	}
	
	
	
}