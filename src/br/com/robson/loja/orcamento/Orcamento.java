package br.com.robson.loja.orcamento;

import java.math.BigDecimal;

public class Orcamento {

	private BigDecimal valor;
	private Integer quantidadeItens;
	private String situacao;

	public Orcamento(BigDecimal valor, Integer quantidadeItens) {
		this.valor = valor;
		this.quantidadeItens = quantidadeItens;
	}
	
	public void aplicarDescontoExtra() {
		BigDecimal valorDoDescontoExtra = BigDecimal.ZERO;
		if(situacao.equals("EM_ANALISE")){
			valorDoDescontoExtra = new BigDecimal("0.05");
		} else if (situacao.equals("APROVADO")) {
			valorDoDescontoExtra = new BigDecimal("0.02");
		}
		this.valor = this.valor.subtract(valorDoDescontoExtra);
	}
	
	public void aprovar() {
		this.situacao = "APROVADO";
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Integer getQuantidadeItens() {
		return quantidadeItens;
	}
	
	
}
