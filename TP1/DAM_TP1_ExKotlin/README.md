# Trabalho 1 — Desenvolvimento de Aplicações Móveis (Kotlin)

Course: Desenvolvimento de Aplicações Móveis

Student(s): João Pedro Mulano Povoa

Date: 07/03/2026

Repository URL: https://github.com/JPovoaDev/DAM_TP1_Ex2_Ex6

## 1. Introduction
Este projeto foi desenvolvido no âmbito da disciplina de Desenvolvimento de Aplicações Móveis (DAM) e tem como principal objetivo a resolução de uma série de 4 exercícios práticos para consolidação dos conceitos base da linguagem Kotlin. 
Os exercícios abarcam temas desde estruturas de dados simples (arrays dinâmicos) e ciclos, operações aritméticas e lógicas bit a bit, sequências matemáticas, até à exploração de conceitos de Programação Orientada a Objetos (POO), herança e companion objects.

## 2. System Overview
O projeto está organizado em quatro diretórios principais, correspondentes a cada um dos exercícios propostos:
- `dam.exer_1`: Implementação de arrays dinamicamente para o cálculo de quadrados perfeitos.
- `dam.exer_2`: Uma calculadora interativa capaz de realizar tanto operações aritméticas básicas como operações lógicas e de bit-shift, com conversão para formato hexadecimal.
- `dam.exer_3`: Simulação da trajetória e perda de altura parcial de uma bola a saltar através de sequências geradas dinamicamente (`generateSequence`).
- `dam.exer.virtual_library`: Modelo de um sistema de gestão para uma biblioteca virtual, com suporte a livros físicos e digitais, recorrendo a classes abstratas, construtores e herança.

## 3. Architecture and Design
O design das soluções foi feito de modo a garantir uma arquitetura simples e direta focada na resolução de cada problema. No Exercício 1 e 3, a funcionalidade flui linearmente, tirando partido das funções nativas da linguagem Kotlin. No Exercício 2, a arquitetura baseia-se num loop interativo (`do...while`) que mantém a aplicação a correr e garante a validação dos dados injetados pelo utilizador através de blocos `try...catch`. Para o projeto da Biblioteca Virtual (Virtual Library), adotei uma abordagem de POO com a classe abstrata base `Book` da qual herdam as classes concretas `PhysicalBook` e `DigitalBook`.

## 4. Implementation
Nesta secção, explico as diferentes implementações de cada um dos quatro exercícios, com o foco nos pedidos de desenvolvimento e a minha abordagem.

### Exercício 1 - Quadrados Perfeitos
- **O que foi pedido:** Criar e manipular arrays de forma dinâmica para albergar e apresentar os primeiros 50 quadrados perfeitos.
- **Linha de pensamento:** O exercício pedia explicitamente os primeiros quadrados perfeitos. Num ciclo ou inicialização normal a base começa em 0, dando `0*0=0`. No entanto, como o 0 não é normalmente contabilizado como parte da progressão útil neste contexto, tive de forçar a iteração a iniciar no 1 ou calcular `(i+1)*(i+1)`.
- **Implementação final e observações:** O código resolve o pedido em três alíneas usando três estratégias de instanciação distintas (`IntArray`, método `map` de `(1..50)`, e `Array`). O método `map` foi especialmente interessante pois permitiu transformar os valores nativos em algo novo de modo limpo e sucinto.
```kotlin
// Exemplo Alínea B - Uso do map
val listValoresB = (1..50)
val quadrados = listValoresB.map { i -> i * i }
```

### Exercício 2 - Calculadora Interativa
- **O que foi pedido:** Implementar uma calculadora que inclua não só as quatro operações básicas (soma, subtração, divisão e multiplicação), mas também operadores lógicos/binários (`&&`, `||`, `!`, `shl`, `shr`) e representasse resultados em decimal e hexadecimal.
- **Linha de pensamento:** Necessitava de garantir que o programa continuaria a pedir dados validamente através de `do...while` em conjunto com a estrutura `when` e exceptions tratadas.
- **Implementação final e observações:** Consegui manter variáveis locais controladas (usar a conversão Boolean, por exemplo, verificando se o retorno numérico é != 0 ou não). A conversão matemática para hexadecimal foi garantida implementando manualmente `numToHex(a:Int)`, fazendo divisões agressivas por 16 e recorrendo a uma lista de restos das divisões.

### Exercício 3 - A Bola ao Salto
- **O que foi pedido:** Simular uma bola largada aos 100m, onde a cada salto atinge apenas 60% da altura do salto anterior, acabando por parar quando não conseguir realizar um salto acima de 1 metro. Apresentar os primeiros 15 saltos formatados a duas casas decimais.
- **Linha de pensamento:** Usar a função nativa `generateSequence`, em vez do típico loop, passando a altura do salto atual baseada numa operação simples: `altura * 0.6`.
- **Implementação final e observações:** Achei a lógica fluida. Combinei `.takeIf { it >= 1.0 }` como critério de paragem à geração. Posteriormente chamei o método `take(15)` mapeando a lista com `"%.2f".format(it)`.
```kotlin
val bounces = generateSequence(100.0) { (it * 0.6).takeIf { it >= 1.0 } }
```

### Exercício Virtual Library
- **O que foi pedido:** Implementar uma gestão de biblioteca. Diferenciar entre material físico e formatado digitalmente gerindo empréstimos e visualização de volumes de dados.
- **Linha de pensamento:** Um utilizador deveria ser capaz de pedir ou devolver livros através de getters/setters bem definidos. Incorporei um custom `setter` de `availableCopies` que alerta devoluções e ruturas de stock comparando `value` vs `field`. Para o controlo total de livros criados adotei um `companion object`.
- **Implementação final e observações:** A classe genérica `Book` atua como `abstract class` originando as subclasses `DigitalBook` (file size e format) e `PhysicalBook` (peso, hardcover). A biblioteca local guarda este pool de objetos na `MutableList<Book>`. O código demonstra uma distinção de dados através da chamada do `toString()`.

## 5. Testing and Validation
Todos os exercícios foram testados de modo manual. 
As sequências de cálculo numérico no array foram impressas na consola via iteráveis (`contentToString()`). Na calculadora inseri não-números para forçar o lançamento das exceções, o que provou funcionar com a captura do `NumberFormatException`. Na biblioteca virtual simulei várias instâncias e tentei esgotar as existências do livro e realizar devoluções para testar o custom setter associado ao stock, além de comparar os meus resultados com o output expectável que o docente colocou no pdf, essa validação é feita no ficheiro Main.kt.

## 6. Usage Instructions
1. Assegure-se de que tem o Kotlin e uma versão estável do SDK (ou JDK dependendo do IDE) instalada no seu sistema ou configurada no seu IntelliJ IDEA.
2. Clone o repositório ou abra os ficheiros no IntelliJ IDEA.
3. Navegue para o pacote que pretende iniciar (`dam.exer_1`, `dam.exer_2`, etc.).
4. Encontre o ficheiro Kotlin (`.kt`) que possui a função genérica `main(...)` e clique em Play / Run ou emule-a no terminal utilizando o compilador da linguagem `kotlinc`.

---

# Development Process

## 12. Version Control and Commit History
O projeto foi sendo atualizado à medida que os exercícios transitavam de fase de estudo (documentação) para fase de implementação e verificação.

## 13. Difficulties and Lessons Learned
- **Exercícios 1**: Tive de ir à documentação oficial do Kotlin para compreender as funções de manipulação de arrays pedidas, pois não as conhecia. Após uma curta leitura, entendi rapidamente como criar arrays dinamicamente e calcular iterativamente os quadrados resultantes em `(i+1)*(i+1)`.
- **Exercício 2**: Os operadores binários e de shift (`&&`, `||`, `!`, `shl`, `shr`) não estavam claros para mim numa fase inicial. A solução que implementei implicou converter os números em binários de forma estruturada e aplicar operações bitwise. A construção do algoritmo manual de conversão decimal para hexadecimal exigiu também construir uma função própria para obter o resto de divisões sucessivas.
- **Exercício 3**: Senti alguma dificuldade inicial em começar; porém, depois de voltar à documentação oficial para explorar a página referente à função `generateSequence`, percebi perfeitamente como esta funciona. A partir daí correu tudo muito bem e foi divertido de implementar.
- **Virtual Library**: Nenhuma dificuldade significativa. Só exigiu que interiorizasse as lógicas de criação de classes em Kotlin. Fui à documentação oficial do Kotlin: compreendi e dominei conceitos como declaração de classes, construtores, herança de propriedades entre parentes e filhos, e a partir daí implementei sem grandes contratempos conforme o enunciado.

## 14. Future Improvements
- Refinar as estruturas ou loops do Exercício 2 de forma a prevenir de forma ainda mais eficaz inputs inválidos do utilizador, criando modularidade externa e funções auxiliares.
- Aprofundar as interações de "Virtual Library" com ficheiros guardados localmente a simular uma persistência de dados efetiva com bancos de dados.

- ## 15. AI Usage Disclosure
Usei inteligência artificial para me ajudar a rever o texto deste README e a estruturar as ideias. Ajudou-me a ter a certeza que as secções estavam todas aqui e fez com que a formatação em Markdown (especialmente a parte do código) ficasse mais apresentável.