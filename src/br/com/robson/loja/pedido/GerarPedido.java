package br.com.robson.loja.pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import br.com.robson.loja.orcamento.Orcamento;

public class GerarPedido {

	private String cliente;
	private BigDecimal valorOrcamento;
	private int quantidadeItens;
	
	public GerarPedido(String cliente, BigDecimal valorOrcamento, int quantidadeItens) {
		this.cliente = cliente;
		this.valorOrcamento = valorOrcamento;
		this.quantidadeItens = quantidadeItens;
	}
	
	public void executa() {
		Orcamento orcamento = new Orcamento(this.valorOrcamento, this.quantidadeItens);
		
		Pedido pedido = new Pedido(cliente, LocalDateTime.now(), orcamento);
		
		System.out.println("Salvar pedido no banco de dados");
		System.out.println("Enviar Email com dados do novo pedido");
	}
}
