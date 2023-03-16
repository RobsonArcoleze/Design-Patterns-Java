package br.com.robson.loja.orcamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import br.com.robson.loja.orcamento.situacao.EmAnalise;
import br.com.robson.loja.orcamento.situacao.Finalizado;
import br.com.robson.loja.orcamento.situacao.SituacaoOrcamento;

public class Orcamento implements Orcavel{

	private BigDecimal valor;
	private SituacaoOrcamento situacao;
	private List<Orcavel> itens;

	public Orcamento() {
		this.valor = BigDecimal.ZERO;
		this.situacao = new EmAnalise();
		this.itens = new ArrayList<>();
	}
	
	public void aplicarDescontoExtra() {
		BigDecimal valorDoDescontoExtra = this.situacao.calcularValorDeDescontoExtra(this);
		
		this.valor = this.valor.subtract(valorDoDescontoExtra);
	}
	
	public void aprovar() {
		this.situacao.aprovar(this);
	}
	
	public void reprovar() {
		this.situacao.reprovar(this);
	}
	
	public void finalizar() {
		this.situacao.Finalizar(this);
	}

	public SituacaoOrcamento getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoOrcamento situacao) {
		this.situacao = situacao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Integer getQuantidadeItens() {
		return itens.size();
	}
	
	public boolean isFinalizado() {
		return situacao instanceof Finalizado;
	}
	
	public void adionarItem(Orcavel item) {
		this.valor = valor.add(item.getValor());
		this.itens.add(item);
	}
	
	
}
