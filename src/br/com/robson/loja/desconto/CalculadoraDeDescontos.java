package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class CalculadoraDeDescontos {

	public BigDecimal calcula(Orcamento orcamento) {
		
		Desconto desconto = new DescontoParaOrcamentoComMaisDeCincoItens(
				new DescontoParaOrcamentoComValorMaiorQueQuinhentos(
						new SemDesconto()));
		
		return desconto.calcular(orcamento);
	}
}
