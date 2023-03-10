package br.com.robson.loja;

import java.math.BigDecimal;

import br.com.robson.loja.imposto.CalculadoraDeImposto;
import br.com.robson.loja.imposto.ICMS;
import br.com.robson.loja.orcamento.Orcamento;

public class TestesImpostos {

	public static void main(String[] args) {
		
		Orcamento orcamento = new Orcamento(new BigDecimal("100"));
		CalculadoraDeImposto calculadora = new CalculadoraDeImposto();
		
		System.out.println(calculadora.calcula(orcamento, new ICMS()));
	}

}
