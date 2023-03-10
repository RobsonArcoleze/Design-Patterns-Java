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


