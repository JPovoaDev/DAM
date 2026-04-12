# Trabalho 2 — Desenvolvimento de Aplicações Móveis (Kotlin)

Course: Desenvolvimento de Aplicações Móveis

Student(s): João Pedro Mulano Povoa

Date: 11/04/2026

Repository URL: https://github.com/JPovoaDev/

## 1. Introduction
Este projeto foi desenvolvido no âmbito da disciplina de Desenvolvimento de Aplicações Móveis (DAM) e tem como principal objetivo a resolução de uma série de 4 exercícios práticos para consolidação de conceitos mais avançados da linguagem Kotlin.
Os exercícios abarcam temas como sealed classes e extension functions, generics e higher-order functions, processamento de dados em pipeline com lambdas, e sobrecarga de operadores com data classes e interfaces comparáveis.

## 2. System Overview
O projeto está organizado em quatro pacotes principais, correspondentes a cada um dos exercícios propostos:
- `dam.exer1`: Sistema de eventos tipados com sealed class, extension functions para filtragem, contabilização de gastos e processamento com lambdas.
- `dam.exer2`: Cache genérica com suporte a qualquer tipo de chave e valor, operações de inserção, remoção, transformação e filtragem.
- `dam.exer3`: Pipeline de processamento de strings com stages configuráveis dinamicamente através de lambdas, com suporte a execução em fork.
- `dam.exer4`: Vetor 2D com sobrecarga completa de operadores aritméticos, comparação por magnitude, produto escalar, normalização e acesso por índice.

## 3. Architecture and Design
O design das soluções foi feito de modo a garantir uma arquitetura simples e idiomática em Kotlin. No Exercício 1, a arquitetura baseia-se numa sealed class com subclasses tipadas e extension functions sobre `List<Event>`. No Exercício 2, adotei uma classe genérica `Cache<K, V>` com restrições de tipo (`K : Any, V : Any`) que encapsula um `MutableMap`. No Exercício 3, a arquitetura centra-se numa classe `Pipeline` que armazena stages como lambdas num `MutableMap` e os executa sequencialmente. No Exercício 4, a `data class Vec2` implementa `Comparable<Vec2>` e sobrecarrega operadores através das keywords `operator fun`.

## 4. Implementation
Nesta secção, explico as diferentes implementações de cada um dos quatro exercícios, com o foco nos pedidos de desenvolvimento e a minha abordagem.

### Exercício 1 - Sistema de Eventos (Event)
- **O que foi pedido:** Modelar um sistema de eventos de utilizador (Login, Purchase, Logout) com sealed class, implementar extension functions para filtrar eventos por utilizador, calcular o total gasto e processar eventos com um lambda.
- **Linha de pensamento:** A sealed class garante que todos os subtipos de `Event` são conhecidos em tempo de compilação, o que torna os blocos `when` exaustivos e seguros. As extension functions permitem adicionar comportamento à `List<Event>` sem alterar a classe base.
- **Implementação final e observações:** Implementei três extension functions: `filterByUser`, `totalSpent` e `processEvent`. A função `processEvent` recebe um lambda `(Event) -> Unit` que é aplicado a cada elemento da lista, o que permite ao chamador definir o comportamento de forma flexível. Adicionei getters auxiliares nas subclasses para simplificar o acesso às propriedades dentro dos blocos `when`.
```kotlin
// Extension function que aplica um lambda a cada evento da lista
fun List<Event>.processEvent(handler: (Event) -> Unit): Unit {
    for (i in 0..this.size - 1) {
        handler(this.get(i))
    }
}
```

### Exercício 2 - Cache Genérica (Cache)
- **O que foi pedido:** Implementar uma classe `Cache<K, V>` genérica com operações de `put`, `get`, `evict`, `size`, `getOrPut`, `transform`, `snapshot` e `filterValues`.
- **Linha de pensamento:** Encapsulei um `MutableMap<K, V>` como estrutura de dados interna. A restrição `: Any` nos parâmetros de tipo impede o uso de tipos anuláveis como chave ou valor, garantindo maior segurança. A função `transform` exigiu atenção especial ao operador `!!` para garantir que o compilador aceita o acesso ao valor sem null-check redundante.
- **Implementação final e observações:** A função `snapshot` devolve uma cópia imutável do mapa interno via `toMap()`, impedindo modificações externas ao estado da cache. A função `getOrPut` delega diretamente na função nativa do Kotlin com o mesmo nome, tirando partido da biblioteca standard.
```kotlin
// snapshot devolve uma cópia read-only do estado atual da cache
fun snapshot(): Map<K, V> {
    return map.toMap()
}
```

### Exercício 3 - Pipeline de Processamento (Pipeline)
- **O que foi pedido:** Implementar uma classe `Pipeline` que permite adicionar stages de transformação sobre listas de strings, executá-los sequencialmente e descrevê-los. Implementar ainda uma função `buildPipeline` e uma função `fork` para execução paralela em dois pipelines independentes.
- **Linha de pensamento:** Cada stage é armazenado como um par `(nome, lambda)` num `MutableMap`, o que permite iterar pela ordem de inserção e aplicar cada transformação ao resultado acumulado. A função `fork` recebe dois pipelines distintos e executa o mesmo input em ambos de forma independente, devolvendo um `Pair` com os dois resultados.
- **Implementação final e observações:** A função `buildPipeline` recebe um lambda que configura um `Pipeline` criado internamente, seguindo o padrão de builder com receiver. No `main`, construí um pipeline real sobre uma lista de logs que aplica trim, filtragem por "ERROR", conversão para maiúsculas e adição de índice.
```kotlin
// buildPipeline aplica o lambda de configuração ao pipeline criado internamente
fun buildPipeline(pipeline: (Pipeline) -> Unit): Pipeline {
    val pipeline1 = Pipeline()
    pipeline(pipeline1)
    return pipeline1
}
```

### Exercício 4 - Vetor 2D (Vec2)
- **O que foi pedido:** Implementar uma `data class Vec2` com sobrecarga dos operadores `+`, `-`, `*` (escalar), `-` unário, acesso por índice `[]`, e implementar `Comparable<Vec2>` por magnitude. Implementar ainda `magnitude`, `dot` e `normalized`.
- **Linha de pensamento:** A `data class` fornece automaticamente `equals`, `hashCode`, `toString` e as funções `component1`/`component2` para destructuring, pelo que não foi necessário implementá-las manualmente. A comparação é feita por magnitude usando `compareTo` delegado ao `Double.compareTo`.
- **Implementação final e observações:** O operador `*` foi sobrecarregado em duas direções: `Vec2 * Double` como método da classe, e `Double * Vec2` como extension function fora da classe. A função `normalized` lança `IllegalStateException` se a magnitude for zero. Tentei sobrepor `component1`/`component2` mas a `data class` já os implementa por defeito, o que o compilador não permite sobrescrever.
```kotlin
// Sobrecarga do operador * para Double * Vec2 como extension function
operator fun Double.times(vector2: Vec2): Vec2 {
    return Vec2(vector2.x * this, vector2.y * this)
}
```

## 5. Testing and Validation
Todos os exercícios foram testados de modo manual através das funções `main` presentes em cada ficheiro. No Exercício 1, imprimi todos os eventos com `processEvent` e verifiquei os totais de gastos por utilizador. No Exercício 2, testei as operações de `put`, `get`, `evict`, `getOrPut`, `transform` e `filterValues` com dois cenários distintos (cache de palavras e cache de IDs). No Exercício 3, executei o pipeline sobre uma lista de logs reais e verifiquei que apenas os erros surgiam no output, devidamente formatados. No Exercício 4, comparei o output do `main` com os valores esperados fornecidos pelo docente, validando todas as operações aritméticas, de comparação e de acesso por índice.

## 6. Usage Instructions
1. Assegure-se de que tem o Kotlin e uma versão estável do SDK (ou JDK dependendo do IDE) instalada no seu sistema ou configurada no seu IntelliJ IDEA.
2. Clone o repositório ou abra os ficheiros no IntelliJ IDEA.
3. Navegue para o pacote que pretende iniciar (`dam.exer1`, `dam.exer2`, `dam.exer3` ou `dam.exer4`).
4. Encontre o ficheiro Kotlin (`.kt`) que possui a função `main(...)` e clique em Play / Run ou execute-o no terminal com o compilador `kotlinc`.

---

# Development Process

## 12. Version Control and Commit History
O projeto foi sendo atualizado à medida que cada exercício transitava da fase de compreensão do enunciado para a fase de implementação e validação do output.

## 13. Difficulties and Lessons Learned
- **Exercício 1**: A maior dificuldade foi perceber como estruturar as extension functions de forma a que funcionassem apenas sobre `List<Event>` e não sobre qualquer lista genérica. Após consultar a documentação oficial do Kotlin sobre extension functions, ficou claro que basta declarar o tipo recetor correto antes do nome da função.
- **Exercício 2**: A restrição `: Any` nos parâmetros de tipo não era imediatamente óbvia para mim. Fui à documentação oficial e percebi que é necessária para impedir o uso de tipos anuláveis como chave ou valor do mapa, o que poderia causar comportamentos inesperados. O uso do operador `!!` dentro de `transform` exigiu também alguma atenção para garantir que o acesso ao valor era seguro naquele contexto.
- **Exercício 3**: A lógica de `fork` foi o ponto mais desafiante, pois exigiu que compreendesse que cada pipeline é independente e que o mesmo input deve ser passado a ambos sem que un afetasse o outro. A solução foi simplesmente chamar `execute` nos dois pipelines separadamente e devolver um `Pair` com os resultados.
- **Exercício 4**: Tentei implementar `component1` e `component2` para suportar destructuring customizado, mas apercebi-me que as `data class` já os geram automaticamente a partir das propriedades do construtor primário, e o Kotlin não permite sobrescrever funções geradas automaticamente. Consultei a documentação oficial e confirmei este comportamento, optando por documentar a tentativa no próprio código.

## 14. Future Improvements
- Adicionar testes unitários formais com JUnit para cada exercício, em vez de depender exclusivamente das funções `main` para validação.
- No Exercício 3, explorar a execução verdadeiramente paralela do `fork` usando coroutines do Kotlin (`async`/`await`) em vez de execução sequencial dos dois pipelines.
- No Exercício 2, adicionar suporte a um limite máximo de entradas na cache com uma política de eviction automática (ex: LRU).

## 15. AI Usage Disclosure
Usei inteligência artificial para me ajudar a rever o texto deste README e a estruturar as ideias. Ajudou-me a ter a certeza que as secções estavam todas presentes e fez com que a formatação em Markdown (especialmente a parte do código) ficasse mais apresentável.
