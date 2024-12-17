package com.IsilERPSpring.controller;

import java.io.Console;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.IsilERPSpring.entity.Categoria;
import com.IsilERPSpring.entity.DetalleCompra;
import com.IsilERPSpring.entity.OrdenCompra;
import com.IsilERPSpring.entity.Proveedor;
import com.IsilERPSpring.repository.OrdenCompraRepository;
import com.IsilERPSpring.repository.ProveedorRepository;
import com.IsilERPSpring.repository.ArticuloRepository;
import com.IsilERPSpring.repository.DetalleCompraRepository;

@Controller
@RequestMapping("/ordenCompra")
public class OrdenCompraController {

	@Autowired
	ProveedorRepository proveedorRepository;
	
	@Autowired
	OrdenCompraRepository ordencompraRepository;
	
	@Autowired
	DetalleCompraRepository detallecompraRepository;
	
	@Autowired
	ArticuloRepository articuloRepository;
	
	@RequestMapping(value="/buscarOrdenCompra", method=RequestMethod.GET)
		public String buscarOrdenCompra(HttpServletRequest request, 
				@RequestParam(value="fechaInicio", required = false) String fechaInicioStr, 
		        @RequestParam(value="fechaFin", required = false) String fechaFinStr, Model model) {
		LocalDate fechaActual = LocalDate.now();
	    Date fechaInicio = null;
	    Date fechaFin = null;
	    
	    if (fechaInicioStr == null || fechaInicioStr.trim().isEmpty()) {
	        fechaInicio = Date.valueOf(fechaActual);
	        model.addAttribute("mensajeFI", "Error. La fecha de inicio no fue añadida.");
	        return "redirect:/home/mostrarGestionCompras/0/10";	        
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
	    List<OrdenCompra> listaCompras = ordencompraRepository.findByFechaRegistroBetween(fechaInicio, fechaFin);
	    model.addAttribute("listaCompras", listaCompras);
	    
	    return "gestionCompras";
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(HttpServletRequest request, @PathVariable("id") int id, HttpSession session) {
		OrdenCompra objOrdenCompraBD = ordencompraRepository.findById(id);
		String mensaje;
		if(objOrdenCompraBD.getEstado().compareTo("Pendiente de Aprovación")==0) {
			ordencompraRepository.deleteById(id);
			mensaje="La compra fue eliminada con exito";
		}else {
			mensaje="No se puede eliminar la orden de compra por tener un estado diferente a Pendiente de Aprovación";
		}
		Integer offset = (Integer) session.getAttribute("offset");
	     Integer pageSize = (Integer) session.getAttribute("pageSize");
		return "redirect:/home/mostrarGestionCompras/"+offset +"/" + pageSize;
	}
	
	@GetMapping("/ver/{id}")
	public String ver(HttpServletRequest request, @PathVariable("id") int id, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session){
		OrdenCompra objOrdenCompraBD = ordencompraRepository.findById(id);
		List<DetalleCompra> listaDetalle = detallecompraRepository.findByOrdenCompra(objOrdenCompraBD);
		model.addAttribute("ordenCompra", objOrdenCompraBD);
		model.addAttribute("listaDetalle", listaDetalle);

		session.setAttribute("offset", offset);
		session.setAttribute("pageSize", pageSize);
		model.addAttribute("pageSize", pageSize);
		return "verDetalleCompra";
	}
	
	@PostMapping("/mostrarNuevo")
	public String mostrarNuevo(HttpServletRequest request, Model model, @RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
		OrdenCompra ordenCompra = new OrdenCompra();
		model.addAttribute("objOrdenCompra", ordenCompra);
		List<Articulo> listaArticulos = articuloRepository.findAll().stream()
			    .filter(articulo -> !articulo.getEstado().equals("Inactivo"))
			    .collect(Collectors.toList());
		model.addAttribute("listaArticulos",listaArticulos);
		List<Proveedor> listaProveedores = proveedorRepository.findAll().stream()
				.filter(proveedor -> !proveedor.getEstado().equals("Inactivo"))
	            .collect(Collectors.toList());
		model.addAttribute("listaProveedores", listaProveedores);

		session.setAttribute("offset", offset);
		session.setAttribute("pageSize", pageSize);
		model.addAttribute("pageSize", pageSize);
		return "nuevaCompra";
	}
	
	@RequestMapping(value="/guardar", method=RequestMethod.GET, params="grabarCabecera")
	public String guardarCabecera (HttpServletRequest request, @ModelAttribute("objOrdenCompra")OrdenCompra ordenCompra, @RequestParam("idProveedor") int idProveedor, Model model, 
			@RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
		Proveedor proveedor = proveedorRepository.findById(idProveedor);
		ordenCompra.setEstado("Aprovado");
		ordenCompra.setProveedor(proveedor);
		ordencompraRepository.save(ordenCompra);
		model.addAttribute("objOrdenCompra", ordenCompra);
		Categoria categoriaProveedor = proveedor.getCategoria();
		List<Articulo> listaArticulos = articuloRepository.findByCategoriaAndEstado(categoriaProveedor);
		model.addAttribute("listaArticulos",listaArticulos);
		List<Proveedor> listaProveedores = proveedorRepository.findAll().stream()
				.filter(proveedores -> !proveedores.getEstado().equals("Inactivo"))
	            .collect(Collectors.toList());
		model.addAttribute("listaProveedores", listaProveedores);

		session.setAttribute("offset", offset);
		session.setAttribute("pageSize", pageSize);
		model.addAttribute("pageSize", pageSize);
		return "nuevaCompra";
	}
	
	@RequestMapping(value="/guardar", method=RequestMethod.GET, params="grabarDetalle")
	public String guardarDetalle(HttpServletRequest request, @ModelAttribute("objOrdenCompra")OrdenCompra ordenCompra, @RequestParam("idArticulo") int idArticulo, @RequestParam("cantidad") int cantidad, @RequestParam("precioUnitario") double precioUnitario, @RequestParam("precioTotal") double precioTotal, Model model,
			@RequestParam(value = "offset", defaultValue = "0") int offset, 
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, HttpSession session) {
		
		OrdenCompra ordenCompraBD = ordencompraRepository.findById(ordenCompra.getId());
		Articulo objArticuloBD = articuloRepository.findById(idArticulo);
		
		DetalleCompra detalleCompra = new DetalleCompra();
		detalleCompra.setCantidad(cantidad);
		detalleCompra.setPrecioUnitario(precioUnitario);
		detalleCompra.setPrecioTotal(precioTotal);
		detalleCompra.setArticulo(objArticuloBD);
		detalleCompra.setOrdenCompra(ordenCompraBD);
		detallecompraRepository.save(detalleCompra);
		
		objArticuloBD.setStock(objArticuloBD.getStock() + cantidad);
	    articuloRepository.save(objArticuloBD);
	    
		List<DetalleCompra> listaDetalle = detallecompraRepository.findByOrdenCompra(ordenCompraBD);
		model.addAttribute("listaDetalle", listaDetalle);
		model.addAttribute("objOrdenCompra", ordenCompraBD);
		
	    Proveedor proveedor = ordenCompraBD.getProveedor(); 
	    
	    Categoria categoriaProveedor = proveedor.getCategoria();
	    
	    List<Articulo> listaArticulos = articuloRepository.findAll().stream()
	        .filter(articulo -> articulo.getCategoria().equals(categoriaProveedor) && 
	                            !articulo.getEstado().equals("Inactivo"))
	        .collect(Collectors.toList());
	    model.addAttribute("listaArticulos", listaArticulos);
		
		List<Proveedor> listaProveedores = proveedorRepository.findAll().stream()
				.filter(proveedores -> !proveedores.getEstado().equals("Inactivo"))
	            .collect(Collectors.toList());
		model.addAttribute("listaProveedores", listaProveedores);

		session.setAttribute("offset", offset);
		session.setAttribute("pageSize", pageSize);
		model.addAttribute("pageSize", pageSize);
		return "nuevaCompra";
	}
	
	
	
}