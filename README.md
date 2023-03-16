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
package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public interface Imposto {

	BigDecimal calcula(Orcamento orcamento);
}
```

```
package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class ISS implements Imposto {

	@Override
	public BigDecimal calcula(Orcamento orcamento) {
		return orcamento.getValor().multiply(new BigDecimal("0.06"));
		}
}
```

```
package br.com.robson.loja.imposto;

import java.math.BigDecimal;

import br.com.robson.loja.orcamento.Orcamento;

public class ICMS implements Imposto {

	@Override
	public BigDecimal calcula(Orcamento orcamento) {
		return orcamento.getValor().multiply(new BigDecimal("0.1"));
	}

}
```


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


### Pattern Command


O Command é um padrão de projeto comportamental que transforma um pedido em um objeto independente que contém toda a informação sobre o pedido. Essa transformação permite que você parametrize métodos com diferentes pedidos, atrase ou coloque a execução do pedido em uma fila, e suporte operações que não podem ser feitas.

Pode ser aplicado em uma arquitetura de camada, onde temos a camada de banco de dados, entidades, services, controllers etc.

Basicamente é a separação entres os serviços.

### Pattern Observer

O padrão Observer é comumente utilizado por diversas bibliotecas que trabalham com eventos. Muitas tecnologias em Java, como o Spring e o CDI, possuem componentes que nos auxiliam a trabalhar com eventos.

A forma como o padrão foi implementado aqui na aula é a mais simples e pura, mas existem diversas modificações que podem ser feitas.

Para entender mais sobre a teoria deste padrão, você pode conferir este link: https://refactoring.guru/design-patterns/observer.


# Design Patterns-Java

Categorias


- Criacionais
- Estruturais
- Comportamentais


## Estruturais

Categorias:

- Adapter
- Decorator
- Composite
- Facade
- Proxy

### Adapter

O Adapter é um padrão de projeto estrutural que permite objetos com interfaces incompatíveis colaborarem entre si.

**Problema**
Imagine que você está criando uma aplicação de monitoramento do mercado de ações da bolsa. A aplicação baixa os dados as ações de múltiplas fontes em formato XML e então mostra gráficos e diagramas maneiros para o usuário.

Em algum ponto, você decide melhorar a aplicação ao integrar uma biblioteca de análise de terceiros. Mas aqui está a pegadinha: a biblioteca só trabalha com dados em formato JSON.

![img](https://refactoring.guru/images/patterns/diagrams/adapter/problem-pt-br.png?id=5429f5de17156d304a588b7cbaa7ed10)

**Solução**
Você pode criar um adaptador. Ele é um objeto especial que converte a interface de um objeto para que outro objeto possa entendê-lo.

Um adaptador encobre um dos objetos para esconder a complexidade da conversão acontecendo nos bastidores. O objeto encobrido nem fica ciente do adaptador. Por exemplo, você pode encobrir um objeto que opera em metros e quilômetros com um adaptador que converte todos os dados para unidades imperiais tais como pés e milhas.

Adaptadores podem não só converter dados em vários formatos, mas também podem ajudar objetos com diferentes interfaces a colaborar. Veja aqui como funciona:

O adaptador obtém uma interface, compatível com um dos objetos existentes.
Usando essa interface, o objeto existente pode chamar os métodos do adaptador com segurança.
Ao receber a chamada, o adaptador passa o pedido para o segundo objeto, mas em um formato e ordem que o segundo objeto espera.
Algumas vezes é possível criar um adaptador de duas vias que pode converter as chamadas em ambas as direções.

![img](https://refactoring.guru/images/patterns/diagrams/adapter/solution-pt-br.png?id=ffe986cb8e979f54610072f35928d04e)

Vamos voltar à nossa aplicação da bolsa de valores. Para resolver o dilema dos formatos incompatíveis, você pode criar adaptadores XML-para-JSON para cada classe da biblioteca de análise que seu código trabalha diretamente. Então você ajusta seu código para comunicar-se com a biblioteca através desses adaptadores. Quando um adaptador recebe uma chamada, ele traduz os dados entrantes XML em uma estrutura JSON e passa a chamada para os métodos apropriados de um objeto de análise encoberto.


Em nossa aplicação implementamos uma classe RegistroDeOrcamento, essa classe precisa se comunicar com uma "API Externa", há varias maneiras de comunição existentes no java, pensando nisso foi implementado o Pattern Adapter, criando uma interface com método post, sendo assim, independente da forma que use para fazer essa comunicação a classe RegistroDeOrcamento não sofre alterações, pois o que será injetado nela será uma interface e não uma classe concreta, ou seja, baixo acoplamento!
Vejamos o código para melhor entendimento:


RegistroDeOrcamento
```
package br.com.robson.loja.orcamento;

import java.util.Map;

import br.com.robson.loja.DomainException;
import br.com.robson.loja.http.HttpAdapter;

public class RegistroDeOrcamento {

	private HttpAdapter http;
	
	
	public RegistroDeOrcamento(HttpAdapter http) {
		this.http = http;
	}


	public void registrar(Orcamento orcamento) {
		if(!orcamento.isFinalizado()) {
			throw new DomainException("Orçamento não finalizado!");
		}
		
		String url = "http://api.externa/orcamento";
		Map<String, Object> dados = Map.of(
				"valor", orcamento.getValor(),
				"quantidadeItens", orcamento.getQuantidadeItens()
				);
		
		http.post(url, dados);
	}
}
```

Interface
```
package br.com.robson.loja.http;

import java.util.Map;

public interface HttpAdapter {

	void post (String url, Map<String, Object> dados);
}
```

Classe que implementa a Interface
```
package br.com.robson.loja.http;

import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class JavaHttpClient implements HttpAdapter {

	@Override
	public void post(String url, Map<String, Object> dados) {
		try {
			URL urlDaApi= new URL(url);
			URLConnection connection = urlDaApi.openConnection();
			connection.connect();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao enviar requisição	");

		}	
		
	}

}
```

Aplicando o Pattern Adapter, é possivel mudar a implementação de comunição apenas criando uma nova classe que implemente a Interface, sem mexer nas outras formas de comunicação.


### Decorator

O Decorator é um padrão de projeto estrutural que permite que você acople novos comportamentos para objetos ao colocá-los dentro de invólucros de objetos que contém os comportamentos.

![img](https://refactoring.guru/images/patterns/content/decorator/decorator.png?id=710c66670c7123e0928d3b3758aea79e)


**Problema**
Imagine que você está trabalhando em um biblioteca de notificação que permite que outros programas notifiquem seus usuários sobre eventos importantes.

A versão inicial da biblioteca foi baseada na classe Notificador que tinha apenas alguns poucos campos, um construtor, e um único método enviar. O método podia aceitar um argumento de mensagem de um cliente e enviar a mensagem para uma lista de emails que eram passadas para o notificador através de seu construtor. Uma aplicação de terceiros que agia como cliente deveria criar e configurar o objeto notificador uma vez, e então usá-lo a cada vez que algo importante acontecesse.

![img](https://refactoring.guru/images/patterns/diagrams/decorator/problem1-pt-br.png?id=56c7f877dbf344107a9162b81b7dca3a)

Em algum momento você se dá conta que os usuários da biblioteca esperam mais que apenas notificações por email. Muitos deles gostariam de receber um SMS acerca de problemas críticos. Outros gostariam de ser notificados no Facebook, e, é claro, os usuários corporativos adorariam receber notificações do Slack.

![img](https://refactoring.guru/images/patterns/diagrams/decorator/problem2.png?id=ba5d5e106ea8c4848d60e230feca9135)

Quão difícil isso seria? Você estende a classe Notificador e coloca os métodos de notificação adicionais nas novas subclasses. Agora o cliente deve ser instanciado à classe de notificação que deseja e usar ela para todas as futura notificações.

Mas então alguém, com razão, pergunta a você, “Por que você não usa diversos tipos de notificação de uma só vez? Se a sua casa pegar fogo, você provavelmente vai querer ser notificado por todos os canais.”

Você tenta resolver esse problema criando subclasses especiais que combinam diversos tipos de métodos de notificação dentro de uma classe. Contudo, rapidamente você nota que isso irá inflar o código imensamente, e não só da biblioteca, o código cliente também.

![img](https://refactoring.guru/images/patterns/diagrams/decorator/problem3.png?id=f3b3e7a107d870871f2c3167adcb7ccb)

Você precisa encontrar outra maneira de estruturar classes de notificação para que o número delas não quebre um recorde do Guinness acidentalmente.

**Solução**
Estender uma classe é a primeira coisa que vem à mente quando você precisa alterar o comportamento de um objeto. Contudo, a herança vem com algumas ressalvas sérias que você precisa estar ciente.

A herança é estática. Você não pode alterar o comportamento de um objeto existente durante o tempo de execução. Você só pode substituir todo o objeto por outro que foi criado de uma subclasse diferente.
As subclasses só podem ter uma classe pai. Na maioria das linguagens, a herança não permite que uma classe herde comportamentos de múltiplas classes ao mesmo tempo.
Uma das maneiras de superar essas ressalvas é usando Agregação ou Composição  ao invés de Herança. Ambas alternativas funcionam quase da mesma maneira: um objeto tem uma referência com outro e delega alguma funcionalidade, enquanto que na herança, o próprio objeto é capaz de fazer a função, herdando o comportamento da sua superclasse.

Com essa nova abordagem você pode facilmente substituir o objeto “auxiliador” por outros, mudando o comportamento do contêiner durante o tempo de execução. Um objeto pode usar o comportamento de várias classes, ter referências a múltiplos objetos, e delegar qualquer tipo de trabalho a eles. A agregação/composição é o princípio chave por trás de muitos padrões de projeto, incluindo o Decorator. Falando nisso, vamos voltar à discussão desse padrão.

![img](https://refactoring.guru/images/patterns/diagrams/decorator/solution1-pt-br.png?id=2678803c5fbd7265a1f993d1c514d250)

“Envoltório” (ing. “wrapper”) é o apelido alternativo para o padrão Decorator que expressa claramente a ideia principal dele. Um envoltório é um objeto que pode ser ligado com outro objeto alvo. O envoltório contém o mesmo conjunto de métodos que o alvo e delega a ele todos os pedidos que recebe. Contudo, o envoltório pode alterar o resultado fazendo alguma coisa ou antes ou depois de passar o pedido para o alvo.

Quando um simples envoltório se torna um verdadeiro decorador? Como mencionei, o envoltório implementa a mesma interface que o objeto envolvido. É por isso que da perspectiva do cliente esses objetos são idênticos. Faça o campo de referência do envoltório aceitar qualquer objeto que segue aquela interface. Isso lhe permitirá cobrir um objeto em múltiplos envoltórios, adicionando o comportamento combinado de todos os envoltórios a ele.

No nosso exemplo de notificações vamos deixar o simples comportamento de notificação por email dentro da classe ```Notificador``` base, mas transformar todos os métodos de notificação em decoradores.

![img](https://refactoring.guru/images/patterns/diagrams/decorator/solution2.png?id=cbee4a27080ce3a0bf773482613e1347)

O código cliente vai precisar envolver um objeto notificador básico em um conjunto de decoradores que coincidem com as preferências do cliente. Os objetos resultantes serão estruturados como uma pilha.

![img](https://refactoring.guru/images/patterns/diagrams/decorator/solution3-pt-br.png?id=70a468aff2bd17b4d2d0ad3ce5acc484)

O último decorador na pilha seria o objeto que o cliente realmente trabalha. Como todos os decoradores implementam a mesma interface que o notificador base, o resto do código cliente não quer saber se ele funciona com o objeto “puro” do notificador ou do decorador.

Podemos utilizar a mesma abordagem para vários comportamentos tais como formatação de mensagens ou compor uma lista de recipientes. O cliente pode decorar o objeto com quaisquer decoradores customizados, desde que sigam a mesma interface que os demais.

### Pattern Composite

**Propósito**
O Composite é um padrão de projeto estrutural que permite que você componha objetos em estruturas de árvores e então trabalhe com essas estruturas como se elas fossem objetos individuais.

![img](https://refactoring.guru/images/patterns/content/composite/composite.png?id=73bcf0d94db360b636cd745f710d19db)

**Problema**
Usar o padrão Composite faz sentido apenas quando o modelo central de sua aplicação pode ser representada como uma árvore.

Por exemplo, imagine que você tem dois tipos de objetos: Produtos e Caixas. Uma Caixa pode conter diversos Produtos bem como um número de Caixas menores. Essas Caixas menores também podem ter alguns Produtos ou até mesmo Caixas menores que elas, e assim em diante.

Digamos que você decida criar um sistema de pedidos que usa essas classes. Os pedidos podem conter produtos simples sem qualquer compartimento, bem como caixas recheadas com produtos... e outras caixas. Como você faria para determinar o preço total desse pedido?

![img](https://refactoring.guru/images/patterns/diagrams/composite/problem-pt-br.png?id=76ac3c711bfdff9dcaebc2f31e3b4359)

Você pode tentar uma solução direta: desempacotar todas as caixas, verificar cada produto e então calcular o total. Isso pode ser viável no mundo real; mas em um programa, não é tão simples como executar uma iteração. Você tem que conhecer as classes dos Produtos e Caixas que você está examinando, o nível de aninhamento das caixas e outros detalhes cabeludos de antemão. Tudo isso torna uma solução direta muito confusa ou até impossível.

**Solução**
O padrão Composite sugere que você trabalhe com Produtos e Caixas através de uma interface comum que declara um método para a contagem do preço total.

Como esse método funcionaria? Para um produto, ele simplesmente retornaria o preço dele. Para uma caixa, ele teria que ver cada item que ela contém, perguntar seu preço e então retornar o total para essa caixa. Se um desses itens for uma caixa menor, aquela caixa também deve verificar seu conteúdo e assim em diante, até que o preço de todos os componentes internos sejam calculados. Uma caixa pode até adicionar um custo extra para o preço final, como um preço de embalagem.

![img](https://refactoring.guru/images/patterns/content/composite/composite-comic-1-pt-br.png?id=f1e77dc84bf43a9b5b12a835969619b1)

O maior benefício dessa abordagem é que você não precisa se preocupar sobre as classes concretas dos objetos que compõem essa árvore. Você não precisa saber se um objeto é um produto simples ou uma caixa sofisticada. Você pode tratar todos eles com a mesma interface. Quando você chama um método os próprios objetos passam o pedido pela árvore.


### Pattern Facade

**Propósito**
O Facade é um padrão de projeto estrutural que fornece uma interface simplificada para uma biblioteca, um framework, ou qualquer conjunto complexo de classes.

![img](https://refactoring.guru/images/patterns/content/facade/facade.png?id=1f4be17305b6316fbd548edf1937ac3b)

**Problema**
Imagine que você precisa fazer seu código funcionar com um amplo conjunto de objetos que pertencem a uma sofisticada biblioteca ou framework. Normalmente, você precisaria inicializar todos aqueles objetos, rastrear as dependências, executar métodos na ordem correta, e assim por diante.

Como resultado, a lógica de negócio de suas classes vai ficar firmemente acoplada aos detalhes de implementação das classes de terceiros, tornando difícil compreendê-lo e mantê-lo.

**Solução**
Uma fachada é uma classe que fornece uma interface simples para um subsistema complexo que contém muitas partes que se movem. Uma fachada pode fornecer funcionalidades limitadas em comparação com trabalhar com os subsistemas diretamente. Contudo, ela inclui apenas aquelas funcionalidades que o cliente se importa.

Ter uma fachada é útil quando você precisa integrar sua aplicação com uma biblioteca sofisticada que tem dúzias de funcionalidades, mas você precisa de apenas um pouquinho delas.

Por exemplo, uma aplicação que carrega vídeos curtos engraçados com gatos para redes sociais poderia potencialmente usar uma biblioteca de conversão de vídeo profissional. Contudo, tudo que ela realmente precisa é uma classe com um único método codificar(nomeDoArquivo, formato). Após criar tal classe e conectá-la com a biblioteca de conversão de vídeo, você terá sua primeira fachada.


