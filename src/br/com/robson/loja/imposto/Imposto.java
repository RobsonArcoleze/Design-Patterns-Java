package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public interface Imposto {

	BigDecimal calcula(Orcamento orcamento);
}
