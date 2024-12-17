package com.IsilERPSpring.controller;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.IsilERPSpring.entity.Categoria;
import com.IsilERPSpring.entity.Distrito;
import com.IsilERPSpring.entity.Proveedor;
import com.IsilERPSpring.entity.Usuario;
import com.IsilERPSpring.repository.CategoriaRepository;
import com.IsilERPSpring.repository.DistritoRepository;
import com.IsilERPSpring.repository.ProveedorRepository;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    @Autowired
    ProveedorRepository proveedorRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    DistritoRepository distritoRepository;

    // Obtener proveedores activos
    public List<Proveedor> obtenerProveedorsActivos() {
        return proveedorRepository.findAll().stream()
            .filter(proveedor -> !proveedor.getEstado().equals("Inactivo"))
            .collect(Collectors.toList());
    }

    // Desactivar proveedor
    @PostMapping("/desactivarProveedor")
    public String desactivar(HttpServletRequest request, @ModelAttribute("objProveedor") Proveedor objProveedor, Model model) {
        Proveedor objProveedorBD = proveedorRepository.findById(objProveedor.getId());
        if (objProveedorBD == null) {
            // Manejo de error si el proveedor no existe
            return "errorPage"; // Redirige a una página de error o muestra un mensaje
        }
        objProveedorBD.setMotivoDesactivacion(objProveedor.getMotivoEliminacion());
        objProveedorBD.setEstado("Inactivo");
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        if (usuario != null) {
            objProveedorBD.setUsuarioEliminacion(usuario.getCorreo());
        }
        proveedorRepository.save(objProveedorBD);
        List<Proveedor> listaProveedor = obtenerProveedorsActivos();
        model.addAttribute("listaProveedor", listaProveedor);
        return "gestionProveedor";
    }


    // Mostrar la página de desactivación de proveedor
    @GetMapping("/mostrarDesactivar/{id}")
    public String mostrarDesactivar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
        Proveedor objProveedor = proveedorRepository.findById(id);
        if (objProveedor == null) {
            return "errorPage"; // Redirigir a una página de error si el proveedor no existe
        }

        // Recuperar el usuario desde la sesión
        Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
        List<String> roles = (List<String>) request.getSession().getAttribute("roles");

        // Pasar la información del usuario al modelo si está logeado
        if (usuario != null) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", roles);
        }
        model.addAttribute("objProveedor", objProveedor);
        return "desactivarProveedor";
    }

    // Registrar un nuevo proveedor
    @PostMapping("/registrar")
    public String registrarProveedor(@Valid @ModelAttribute("objProveedor") Proveedor objProveedor,
                                     BindingResult result, Model model) {
        String ruc = objProveedor.getRuc();
        if (!(ruc.startsWith("10") || ruc.startsWith("20"))) {
            result.rejectValue("ruc", "error.objProveedor", "El RUC debe comenzar con 10 o 20");
        }

        if (result.hasErrors()) {
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            model.addAttribute("listaCategorias", categoriaRepository.findAll());
            return "nuevoProveedor";
        }

        if (proveedorRepository.findByRuc(objProveedor.getRuc()) != null) {
            result.rejectValue("ruc", "error.objProveedor", "El RUC ya está en uso");
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            model.addAttribute("listaCategorias", categoriaRepository.findAll());
            return "nuevoProveedor";
        }

        proveedorRepository.save(objProveedor);
        return "redirect:/home/mostrarGestionProveedor";
    }

    // Mostrar la página para editar un proveedor
    @GetMapping("/editar/{id}")
    public String editar(HttpServletRequest request, @PathVariable("id") int id, Model model) {
        Proveedor objProveedor = proveedorRepository.findById(id);
        if (objProveedor == null) {
            return "errorPage"; // Redirigir a una página de error si el proveedor no existe
        }

        model.addAttribute("objProveedor", objProveedor);
        model.addAttribute("listaDistritos", distritoRepository.findAll());
        model.addAttribute("listaCategorias", categoriaRepository.findAll());
        return "editarProveedor";
    }

    // Actualizar proveedor
    @PostMapping("/actualizar")
    public String actualizar(HttpServletRequest request, @Valid @ModelAttribute("objProveedor") Proveedor objProveedor,
                             BindingResult result, Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listaDistritos", distritoRepository.findAll());
            model.addAttribute("listaCategorias", categoriaRepository.findAll());
            return "editarProveedor";
        }

        Proveedor objProveedorBD = proveedorRepository.findById(objProveedor.getId());
        if (objProveedorBD != null) {
            objProveedorBD.setNombre(objProveedor.getNombre());
            objProveedorBD.setDireccion(objProveedor.getDireccion());
            objProveedorBD.setTelefono(objProveedor.getTelefono());
            objProveedorBD.setRuc(objProveedor.getRuc());
            objProveedorBD.setCategoria(objProveedor.getCategoria());
            objProveedorBD.setDistrito(objProveedor.getDistrito());
            objProveedorBD.setEstado(objProveedor.getEstado());
            proveedorRepository.save(objProveedorBD);
        }

        return "redirect:/home/mostrarGestionProveedor";
    }

    // Buscar proveedores por distrito
    @RequestMapping(value="/buscarProveedor", method=RequestMethod.GET)
    public String buscarProveedor(HttpServletRequest request, @RequestParam(value="idDistrito", required = false) String idDistrito, Model model) {
        List<Proveedor> listaProveedor;

        if ("Todos".equals(idDistrito)) {
            listaProveedor = proveedorRepository.findAll();
        } else {
            int distritoId = Integer.parseInt(idDistrito);
            Distrito objDistrito = distritoRepository.findById(distritoId);
            if (objDistrito == null) {
                return "errorPage"; // Redirigir a una página de error si el distrito no existe
            }
            listaProveedor = proveedorRepository.findByDistrito(objDistrito);
        }

        List<Distrito> listaDistritos = distritoRepository.findAll();
        model.addAttribute("listaProveedor", listaProveedor);
        model.addAttribute("listaDistritos", listaDistritos);
        model.addAttribute("idDistritoSeleccionada", idDistrito);
        return "gestionProveedor";
    }
}
