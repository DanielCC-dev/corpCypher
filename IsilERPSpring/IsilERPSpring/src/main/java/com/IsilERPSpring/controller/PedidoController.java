package com.IsilERPSpring.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

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

import com.IsilERPSpring.entity.Cliente;
import com.IsilERPSpring.entity.Pedido;
import com.IsilERPSpring.repository.ClienteRepository;
import com.IsilERPSpring.repository.PedidoRepository;

@Controller
@RequestMapping("/pedido")
public class PedidoController {

	@Autowired
	PedidoRepository pedidoRepository;
	
	@Autowired
	ClienteRepository clienteRepository;
	
	 @InitBinder
	    public void initBinder(WebDataBinder binder) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
	    }
	
	 @GetMapping("/eliminar/{id}")
	 public String eliminar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
		 pedidoRepository.deleteById(id);
		 List<Pedido> listaPedido = pedidoRepository.findAll();
		 model.addAttribute("listaPedido", listaPedido);
		 return "gestionPedido";
	 }
	 
	 @PostMapping("/mostrarNuevo")
		public String mostrarNuevo(HttpServletRequest request, Model model) {
			Pedido objPedido= new Pedido();
			model.addAttribute("objPedido",objPedido);
			List<Cliente> listaCliente = clienteRepository.findAll();
			model.addAttribute("listaCliente",listaCliente);
			return "nuevoPedido";
		}
	 
	 @PostMapping("/registrar")
	 public String registrar(@ModelAttribute("objPedido") Pedido objPedido, @RequestParam("dniCliente") String dniCliente, Model model) {
	     Cliente cliente = clienteRepository.findByNroDoc(dniCliente);
	     if (cliente != null) {
	         objPedido.setCliente(cliente);
	         pedidoRepository.save(objPedido);
	         List<Pedido> listaPedido = pedidoRepository.findAll();
	         model.addAttribute("listaPedido", listaPedido);
	         return "gestionPedido";
	     } else {
	         model.addAttribute("error", "Cliente no encontrado para el DNI proporcionado");
	         return "error";
	     }
	 }
	 
	 @GetMapping("/editar/{id}")
		public String editar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
			Pedido objPedido = pedidoRepository.findById(id);
			model.addAttribute("objPedido",objPedido);
			return "editarPedido";
		}
	 
	 @PostMapping("/actualizar")
		public String actualizar(HttpServletRequest request, @ModelAttribute("objPedido") Pedido objPedido, Model model) {
			Pedido objPedidoBD= pedidoRepository.findById(objPedido.getId());
			objPedidoBD.setEstado(objPedido.getEstado());
			pedidoRepository.save(objPedidoBD);
			List<Pedido> listaPedido = pedidoRepository.findAll();
			model.addAttribute("listaPedido",listaPedido);
			List<Cliente> listaCliente = clienteRepository.findAll();
			model.addAttribute("listaCliente",listaCliente);
			return "gestionPedido";
		}
	 
	 @GetMapping("/buscarClientexDni")
	 public String buscarClientexDni(@RequestParam("dniCliente") String dniCliente, Model model) {
	     Cliente cliente = clienteRepository.findByNroDoc(dniCliente);
	     if (cliente != null) {
	         List<Pedido> listaPedido = pedidoRepository.findByCliente(cliente);
	         model.addAttribute("listaPedido", listaPedido);
	         model.addAttribute("dniCliente", dniCliente);
	     } else {
	         model.addAttribute("error", "No se encontró ningún cliente con el DNI especificado: " + dniCliente);
	         List<Pedido> listaPedido = pedidoRepository.findAll();
	         model.addAttribute("listaPedido", listaPedido);
	     }
	     List<Cliente> listaCliente = clienteRepository.findAll();
	     model.addAttribute("listaCliente", listaCliente);
	     return "gestionPedido";
	 }


}
