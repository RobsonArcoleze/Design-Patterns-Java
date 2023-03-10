package br.com.robson.loja;

import java.math.BigDecimal;

import br.com.robson.loja.desconto.CalculadoraDeDescontos;
import br.com.robson.loja.orcamento.Orcamento;

public class TestesDescontos {

	public static void main(String[] args) {
		
		Orcamento orcamento = new Orcamento(new BigDecimal("200"), 6);
		Orcamento orcamento2 = new Orcamento(new BigDecimal("1000"), 1);
		
		CalculadoraDeDescontos calculadora = new CalculadoraDeDescontos();
		
		System.out.println(calculadora.calcula(orcamento));
		System.out.println(calculadora.calcula(orcamento2));
	}

}
