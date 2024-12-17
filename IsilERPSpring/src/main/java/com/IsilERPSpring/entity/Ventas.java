package com.IsilERPSpring.entity;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Ventas")
public class Ventas {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	
	@Column(name = "fechaVenta")
    private Date fechaVenta;
	
	//@Column(name = "cantidad")
	//private int cantidad;
	
	//@Column(name = "precioUnitario")
	//private double precioUnitario;
	
	@Column(name = "precioTotal")
	private double precioTotal;
	
	@Column(name = "tipoVenta")
	private String tipoVenta;
	
	@Column(name="motivoEliminacion")
	private String motivoEliminacion;
	
	@Column(name="fechaEliminacion")
	private Date fechaEliminacion;
		
	@Column(name="usuarioEliminacion")
	private String usuarioEliminacion;
	
	@Column(name="estado")
	private String estado;

	@ManyToOne
	@JoinColumn(name="id_pedido", referencedColumnName = "id")
	private Pedido pedido;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getFechaVenta() {
		return fechaVenta;
	}

	public void setFechaVenta(Date fechaVenta) {
		this.fechaVenta = fechaVenta;
	}

	public double getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(double precioTotal) {
		this.precioTotal = precioTotal;
	}
	
	public String getTipoVenta() {
		return tipoVenta;
	}

	public void setTipoVenta(String tipoVenta) {
		this.tipoVenta = tipoVenta;
	}

	public Pedido getPedido() {
		return pedido;
	}

	public void setPedido(Pedido pedido) {
		this.pedido = pedido;
	}

	public String getMotivoEliminacion() {
		return motivoEliminacion;
	}

	public void setMotivoEliminacion(String motivoEliminacion) {
		this.motivoEliminacion = motivoEliminacion;
	}

	public Date getFechaEliminacion() {
		return fechaEliminacion;
	}

	public void setFechaEliminacion(Date fechaEliminacion) {
		this.fechaEliminacion = fechaEliminacion;
	}

	public String getUsuarioEliminacion() {
		return usuarioEliminacion;
	}

	public void setUsuarioEliminacion(String usuarioEliminacion) {
		this.usuarioEliminacion = usuarioEliminacion;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	
	
}
