package br.com.robson.loja.orcamento.situacao;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class Aprovado extends SituacaoOrcamento{

	public BigDecimal calcularValorDeDescontoExtra(Orcamento orcamento) {
		return orcamento.getValor().multiply(new BigDecimal("0.05"));
	}
	
	@Override
	public void Finalizar(Orcamento orcamento) {
		orcamento.setSituacao(new Finalizado());
	}
}
