package br.com.robson.loja.orcamento.situacao;

import java.math.BigDecimal;

import br.com.robson.loja.DomainException;
import br.com.robson.loja.orcamento.Orcamento;

public abstract class SituacaoOrcamento {
	
	public BigDecimal calcularValorDeDescontoExtra(Orcamento orcamento) {
		return BigDecimal.ZERO;
	}

	public void aprovar (Orcamento orcamento) {
		throw new DomainException("Orçamento não pode ser Aprovado!");
	}
	
	public void reprovar (Orcamento orcamento) {
		throw new DomainException("Orçamento não pode ser Reprovado!");
	}
	
	public void Finalizar (Orcamento orcamento) {
		throw new DomainException("Orçamento não pode ser Finalizado!");
	}
}
