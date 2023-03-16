package br.com.robson.loja;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.ItemOrcamento;
import br.com.robson.loja.orcamento.Orcamento;

public class TestesComposicao {

	public static void main(String[] args) {
		
		Orcamento antigo = new Orcamento();
		antigo.adionarItem(new ItemOrcamento(new BigDecimal("200")));
		antigo.reprovar();
		
		Orcamento novo = new Orcamento();
		novo.adionarItem(new ItemOrcamento(new BigDecimal("500")));
		novo.adionarItem(antigo);
		
		System.out.println(novo.getValor());
	}

}
