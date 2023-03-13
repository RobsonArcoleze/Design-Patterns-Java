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


### Template Method

O Template Method é um padrão de projeto comportamental que define o esqueleto de um algoritmo na superclasse mas deixa as subclasses sobrescreverem etapas específicas do algoritmo sem modificar sua estrutura.
Vejamos o código para melhor entendimento:

```
public BigDecimal calcular(Orcamento orcamento) {
		if(orcamento.getValor().compareTo(new BigDecimal("500")) > 0) {
			return orcamento.getValor().multiply(new BigDecimal("0.05"));
		}
		return proximo.calcular(orcamento);
	}
```
Todas as classes filhas tinham em comum o método calcular, ainda que  com suas implementações especificas; ou seja sempre havia repetição de código. O que fazer nesse caso?

Bom nesse caso vamos extrair o metodo ```calcular ``` para a classe mãe; Nas classes filhas os metodos se chamarão ```efetuarCalculo```, Vejamos o código para melhor entendimento:


Classe mãe:
```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public abstract class Desconto {
	
	protected Desconto proximo;

	public Desconto(Desconto proximo) {
		this.proximo = proximo;
	}
	
	public BigDecimal calcular(Orcamento orcamento) {
		if (deveAplicar(orcamento)) {
			return efetuarCalculo(orcamento);
		}
		return proximo.calcular(orcamento);
	}
	
	protected abstract BigDecimal efetuarCalculo(Orcamento orcamento);
	protected abstract boolean deveAplicar (Orcamento orcamento);

}
```


Classe Filha:
```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class DescontoParaOrcamentoComMaisDeCincoItens extends Desconto {

	public DescontoParaOrcamentoComMaisDeCincoItens(Desconto proximo) {
		super(proximo);
	}

	@Override
	public BigDecimal efetuarCalculo(Orcamento orcamento) {
			return orcamento.getValor().multiply(new BigDecimal("0.1"));
	}

	@Override
	public boolean deveAplicar(Orcamento orcamento) {
		return orcamento.getQuantidadeItens() > 5;
	}
}
```


Classe Filha:
```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class DescontoParaOrcamentoComValorMaiorQueQuinhentos extends Desconto {

	public DescontoParaOrcamentoComValorMaiorQueQuinhentos(Desconto proximo) {
		super(proximo);
	}
	
	@Override
	public BigDecimal efetuarCalculo(Orcamento orcamento) {
			return orcamento.getValor().multiply(new BigDecimal("0.05"));
	}

	@Override
	public boolean deveAplicar(Orcamento orcamento) {
		return orcamento.getValor().compareTo(new BigDecimal("500")) > 0;
	}
}
```


Classe Filha:
```
package br.com.robson.loja.desconto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class SemDesconto extends Desconto {

	public SemDesconto() {
		super(null);
	}

	@Override
	public BigDecimal efetuarCalculo(Orcamento orcamento) {
		return BigDecimal.ZERO;
	}

	@Override
	public boolean deveAplicar(Orcamento orcamento) {
		return true;
	}
}
```

Analisando o código podemos perceber o uso do template method, onde o que era comum entre as classes filhas foram levadas para a mãe, sendo assim o metodo calcular foi implementado de forma concreta com parte da validação. Perceba que o metodo chama outro metodo abstrato que está sendo implementado pelas classes filhas, ou seja, o que era comum entre ambas foi para a classe mãe, o que era especifico, continua com as filhas.



### Pattern State

O State é um padrão de projeto comportamental que permite que um objeto altere seu comportamento quando seu estado interno muda. Parece como se o objeto mudasse de classe.

Agora vamos implementar uma nova regra de desconto extra, ou seja, caso meu cliente pechinche, tem-se a possibilidade de dar um desconto extra. A principio vamos colocar essa nova regra na classe Orcamento, a lógica ficara desta forma, vamos ao código:

```
package br.com.robson.loja.orcamento;

import java.math.BigDecimal;

public class Orcamento {

	private BigDecimal valor;
	private Integer quantidadeItens;
	private String situacao;

	public Orcamento(BigDecimal valor, Integer quantidadeItens) {
		this.valor = valor;
		this.quantidadeItens = quantidadeItens;
	}
	
	public void aplicarDescontoExtra() {
		BigDecimal valorDoDescontoExtra = BigDecimal.ZERO;
		if(situacao.equals("EM_ANALISE")){
			valorDoDescontoExtra = new BigDecimal("0.05");
		} else if (situacao.equals("APROVADO")) {
			valorDoDescontoExtra = new BigDecimal("0.02");
		}
		this.valor = this.valor.subtract(valorDoDescontoExtra);
	}
	
	public void aprovar() {
		this.situacao = "APROVADO";
	}

// GETTERS E SETTERS....
```

Perceba que a estrutura do nosso código apresenta um monte laços condicionais novamente, iguais aqueles que vimos no começo da nossa implementação, além do valor das nossas condições serem do tipo String, e poderiam ser do tipo Enum. O que fazer para melhorar esse código?

Vejamos agora a implementação do **Pattern State**!

O Pattern State é muito parecido com o Strategy, pois trabalha para eliminar um monte de aninhamento de IF e ELSE, com isso, o código ficará mais legivel, e cada classe terá sua própria responsabilidade. Vejamos como o código ficou:

Primeiro Criamos uma Classe abstrata para sinalizar os estados da situção do orcamento, através dela vamos transitar entre os possiveis estados que o orçamento pode se encontrar. Vejamos o código como ficou:

```
package br.com.robson.loja.orcamento.situacao;

import java.math.BigDecimal;

import br.com.robson.loja.DomainException;
import br.com.robson.loja.orcamento.Orcamento;

public abstract class SituacaoOrcamento {
	
	public BigDecimal calcularValorDeDescontoExtra(Orcamento orcamento) {
		return BigDecimal.ZERO;
	}

	public void aprovar (Orcamento orcamento) {
		throw new DomainException("Orçamento não pode ser Aprovado!");
	}
	
	public void reprovar (Orcamento orcamento) {
		throw new DomainException("Orçamento não pode ser Reprovado!");
	}
	
	public void Finalizar (Orcamento orcamento) {
		throw new DomainException("Orçamento não pode ser Finalizado!");
	}
}
```
 Essa classe está representando os possiveis estados em que a situção do orçamento se encontra, porém nela não está contida a situação em análise, pois se está em enalise, não está aprovada, reprovada ou finalizada.
 
 Vejamo sas implementações das outras classes:

```
package br.com.robson.loja.orcamento.situacao;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class EmAnalise extends SituacaoOrcamento{

	public BigDecimal calcularValorDeDescontoExtra(Orcamento orcamento) {
		return orcamento.getValor().multiply(new BigDecimal("0.05"));
	}
	
	@Override
	public void aprovar(Orcamento orcamento) {
		orcamento.setSituacao(new Aprovado());
	}
	
	@Override
	public void reprovar(Orcamento orcamento) {
		orcamento.setSituacao(new Reprovado());
	}
}

```


```
package br.com.robson.loja.orcamento.situacao;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class Aprovado extends SituacaoOrcamento{

	public BigDecimal calcularValorDeDescontoExtra(Orcamento orcamento) {
		return orcamento.getValor().multiply(new BigDecimal("0.05"));
	}
	
	@Override
	public void Finalizar(Orcamento orcamento) {
		orcamento.setSituacao(new Finalizado());
	}
}
```


A classe finalizado não faz nada, pois está finalizado!
```
package br.com.robson.loja.orcamento.situacao;

public class Finalizado extends SituacaoOrcamento{
}

```


```
package br.com.robson.loja.orcamento.situacao;

import br.com.robson.loja.orcamento.Orcamento;

public class Reprovado extends SituacaoOrcamento{

	@Override
	public void Finalizar(Orcamento orcamento) {
		orcamento.setSituacao(new Finalizado());
	}
}
```

Agora que geramos uma classe abstrata, onde cada estado a implementa, vejamos como fica o código na classe Orçamento:

```
package br.com.robson.loja.orcamento;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.situacao.EmAnalise;
import br.com.robson.loja.orcamento.situacao.SituacaoOrcamento;

public class Orcamento {

	private BigDecimal valor;
	private Integer quantidadeItens;
	private SituacaoOrcamento situacao;

	public Orcamento(BigDecimal valor, Integer quantidadeItens) {
		this.valor = valor;
		this.quantidadeItens = quantidadeItens;
		this.situacao = new EmAnalise();
	}
	
	public void aplicarDescontoExtra() {
		BigDecimal valorDoDescontoExtra = this.situacao.calcularValorDeDescontoExtra(this);
		
		this.valor = this.valor.subtract(valorDoDescontoExtra);
	}
	
	public void aprovar() {
		this.situacao.aprovar(this);
	}
	
	public void reprovar() {
		this.situacao.reprovar(this);
	}
	
	public void finalizar() {
		this.situacao.Finalizar(this);
	}

	public SituacaoOrcamento getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoOrcamento situacao) {
		this.situacao = situacao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public Integer getQuantidadeItens() {
		return quantidadeItens;
	}
	
	
}

```

A classe abstrata é declarada na classe orçamento; Situação é chamada no construtor sendo instanciada como em análise.

Perceba que o método aplicarDescontoExtra() não tem mais IF e ELSE, agora pega-se o estado atual da situação e chama-se o calcularValorDeDescontoExtra passando como argumento o proprio Orçamento (this).


Aplicando este Pattern foi possivel deixar o código mais coeso, tendo cada classe com suas implementações

