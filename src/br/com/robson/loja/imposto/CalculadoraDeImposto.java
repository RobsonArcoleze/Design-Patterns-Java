package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class CalculadoraDeImposto {

	public BigDecimal calcula(Orcamento orcamento, Imposto imposto) {
		return imposto.calcula(orcamento);
	}
}
