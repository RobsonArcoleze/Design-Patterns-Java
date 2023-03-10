package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class ISS implements Imposto {

	@Override
	public BigDecimal calcula(Orcamento orcamento) {
		return orcamento.getValor().multiply(new BigDecimal("0.06"));
		}
}
