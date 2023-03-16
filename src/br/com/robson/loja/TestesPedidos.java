package br.com.robson.loja;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

import br.com.robson.loja.orcamento.Orcamento;
import br.com.robson.loja.pedido.GerarPedido;
import br.com.robson.loja.pedido.GerarPedidoHandler;
import br.com.robson.loja.pedido.acao.CriarPedidoNoBanco;
import br.com.robson.loja.pedido.acao.EnviarPedidoPorEmail;
import br.com.robson.loja.pedido.acao.LogDePedido;

public class TestesPedidos {

	public static void main(String[] args) {

		String cliente = "Ana Da Silva";
		BigDecimal valorOrcamento = new BigDecimal("745.99");
		int quantidadeItens = 3;
		
		GerarPedido gerador = new GerarPedido(cliente, valorOrcamento, quantidadeItens);
		GerarPedidoHandler handler = new GerarPedidoHandler(Arrays.asList(
				new EnviarPedidoPorEmail(),
				new CriarPedidoNoBanco(),
				new LogDePedido()));
		handler.executar(gerador);
	}

}
