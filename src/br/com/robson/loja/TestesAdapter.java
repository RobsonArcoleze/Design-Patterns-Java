package br.com.robson.loja;

import java.math.BigDecimal;

import br.com.robson.loja.http.JavaHttpClient;
import br.com.robson.loja.orcamento.ItemOrcamento;
import br.com.robson.loja.orcamento.Orcamento;
import br.com.robson.loja.orcamento.RegistroDeOrcamento;

public class TestesAdapter {

	public static void main(String[] args) {
		
		Orcamento orcamento = new Orcamento();
		orcamento.adionarItem(new ItemOrcamento(new BigDecimal("200")));
		
		orcamento.aprovar();
		orcamento.finalizar();
		
		RegistroDeOrcamento registro = new RegistroDeOrcamento(new JavaHttpClient());
		registro.registrar(orcamento);

	}

}
