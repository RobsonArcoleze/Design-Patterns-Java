package br.com.robson.loja.pedido;

import java.math.BigDecimal;

public class GerarPedido {

	private String cliente;
	private BigDecimal valorOrcamento;
	private int quantidadeItens;

	public GerarPedido(String cliente, BigDecimal valorOrcamento, int quantidadeItens) {
		this.cliente = cliente;
		this.valorOrcamento = valorOrcamento;
		this.quantidadeItens = quantidadeItens;
	}

	public String getCliente() {
		return cliente;
	}

	public BigDecimal getValorOrcamento() {
		return valorOrcamento;
	}

	public int getQuantidadeItens() {
		return quantidadeItens;
	}
}
