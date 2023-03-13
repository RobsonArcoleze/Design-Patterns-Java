package br.com.robson.loja.orcamento.situacao;

import br.com.robson.loja.orcamento.Orcamento;

public class Reprovado extends SituacaoOrcamento{

	@Override
	public void Finalizar(Orcamento orcamento) {
		orcamento.setSituacao(new Finalizado());
	}
}
