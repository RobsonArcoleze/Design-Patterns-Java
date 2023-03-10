# Design Patterns-Java

Categorias


- Criacionais
- Estruturais
- Comportamentais


## Comportamentais

- Strategy
- Chain of Responsibility
- Template Method
- Satate
- Command
- Observer

### Strategy

Sempre quando se tem uma determinada regra e ela varia de acordo com o parametro ou tipos de informação, a primeira coisa que vem na mente é Switch case ou if else.
Aplicando o pattern Strategy o código mais fica mais coeso, separando em classes cada regra de negócio, por exemplo:

Tenho que calcular imposto sobre determindado produto, a variavel que muda para o calculo seria o imposto em si como ICMS, ISS etc. Porém a assinatura do método é a mesma.

Refatorando esse código repetitivo, implementamos uma Interface com a assinatura deste método, e separamos os impostos em classes implementando essa interface. Agora em uma classe chamada CalculadoraDeImpostos no metodo calcula, ao invés de if e else, simplesmente vamos passar como argumento deste método a interface Imposto, e como as classe ICMS e ISS implementam Imposto, elas irão calcular os seus respectivos impostos.


**ANTES**

Havia sido criado um ENUN para expressar o tipo de imposto
```
package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class CalculadoraDeImposto {

	public BigDecimal calcula(Orcamento orcamento, TipoImposto tipoImposto) {

		switch (tipoImposto) {
		case ICMS: {
			return orcamento.getValor().multiply(new BigDecimal("0.1"));
		}
		case ISS: {
			return orcamento.getValor().multiply(new BigDecimal("0.06"));
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + tipoImposto);
		}

	}
}
```

**DEPOIS**


```
package br.com.robson.loja.orcamento;

import java.math.BigDecimal;

public class Orcamento {

	private BigDecimal valor;

	public Orcamento(BigDecimal valor) {
		this.valor = valor;
	}

	public BigDecimal getValor() {
		return valor;
	}
}
```

```
package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class CalculadoraDeImposto {

	public BigDecimal calcula(Orcamento orcamento, Imposto imposto) {
		return imposto.calcula(orcamento);
	}
}
```


### Chain of Responsibility

Corrente de responsabilidades. Esse padrão diferente do Strategy que é conhecido a regra que precisa ser aplicada, no chain of responbility essa regra precisa ser testada. Vejamos um exemplo para melhor entendimento.


Essa Classe Irá testar se o desconto está sendo aplicado:
```
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
```

Essa classe é responsável por calcular o desconto:
```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class CalculadoraDeDescontos {

	public BigDecimal calcula(Orcamento orcamento) {
		if(orcamento.getQuantidadeItens() > 5) {
			return orcamento.getValor().multiply(new BigDecimal("0.1"));
		}
		if(orcamento.getValor().compareTo(new BigDecimal("500")) > 0) {
			return orcamento.getValor().multiply(new BigDecimal("0.1"));
		}
		return BigDecimal.ZERO;
	}
}
```

Observe que a verificação com if não pode ser tirada com a aplicação do padrão Strategy, mesmo que separe em classes distintas e implemente a assinatura do método de uma interface, ainda sim é preciso verficar com o if. Nesse caso o melhor padrão para se aplicar é Chain of Responsibility. Vejamos sua implementação:


Essa é a classe de teste, perceba que ela continua da mesma forma.
```
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
```


Agora vamos criar uma classe mãe abstrata:
```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public abstract class Desconto {
	
	protected Desconto proximo;

	public Desconto(Desconto proximo) {
		this.proximo = proximo;
	}
	
	public abstract BigDecimal calcular(Orcamento orcamento);

}
```

Perceba que temos um atributo protected, ou seja, ele é visivel para a classe e também para os filhos. Ele tem como tipo o 'Desconto' chamado de próximo, ou seja, na implementação se a sua verificação for falsa vai chamar o próximo desconto. Vejamos as classes que extendem o Desconto:


```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class DescontoParaOrcamentoComMaisDeCincoItens extends Desconto {

	public DescontoParaOrcamentoComMaisDeCincoItens(Desconto proximo) {
		super(proximo);
	}

	public BigDecimal calcular(Orcamento orcamento) {
		if(orcamento.getQuantidadeItens() > 5) {
			return orcamento.getValor().multiply(new BigDecimal("0.1"));
		}
		return proximo.calcular(orcamento);
	}
}

```

A primeira vista parece ser complicado de entender, mas basicamente o que esse código faz é verificar se o orçamento.getQuantidadeItens() > 5, então se aplica o desconto, caso contrário se chama o próximo desconto, vejamos as outras classe para melhor entendemos:

```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class DescontoParaOrcamentoComValorMaiorQueQuinhentos extends Desconto {

	public DescontoParaOrcamentoComValorMaiorQueQuinhentos(Desconto proximo) {
		super(proximo);
	}
	
	public BigDecimal calcular(Orcamento orcamento) {
		if(orcamento.getValor().compareTo(new BigDecimal("500")) > 0) {
			return orcamento.getValor().multiply(new BigDecimal("0.05"));
		}
		return proximo.calcular(orcamento);
	}
}
```


```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class SemDesconto extends Desconto {

	public SemDesconto() {
		super(null);
	}

	public BigDecimal calcular(Orcamento orcamento) {
		return BigDecimal.ZERO;
	}
}
```


Agora que criamos todas as classes que contem as regras de descontos, retornemos a classe CalculadoraDeDescontos, essa classe é o ponto chave para entendermos o código;

```
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

```

Observe como a classe ficou menos verbosa, e de fácil entendimento. Vamos entender o que esse código nos mostra.

Está sendo chamada a classe desconto com instancias de cada tipo de desconto. Lembre-se que cada tipo de desconto é filha de **Desconto**, e que dentro das classes filhas existe um construtor com o atributo "Desconto próximo", pois bem, quando chamo ``` new DescontoParaOrcamentoComMaisDeCincoItens() ``` dentro da sua classe é feita a verificação, caso seja verdadeiro ele retorna o desconto.calcular(orcamento), caso seja falso ele chama o próximo desconto ``` new DescontoParaOrcamentoComMaisDeCincoItens(new DescontoParaOrcamentoComValorMaiorQueQuinhentos() ```.

Para que se tenha um fim foi criado a classe Sem desconto, ou seja, depois de fazer todas as verificações, não existe mais desconto e se retorna um BigDecimal.ZERO;








