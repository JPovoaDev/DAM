# TP4 de DAM: Kotlin Coroutines e Flows 
Boas! Este é o repositório do quarto trabalho prático (TP4) da cadeira de Desenvolvimento de Aplicações Móveis (DAM) no ISEL. 
O principal objetivo deste exercício foi meter as mãos na massa com a concorrência em Kotlin, pegando numa aplicação desktop (feita com Swing) com chamadas de rede bloqueantes e transformá-la numa aplicação reativa, fluída e à prova de freezes na interface, usando **Coroutines** e **Channels/Flows**.
## O que foi feito? 
O trabalho foi dividido em 3 grandes fases. Fica aqui o resumo:
### 1. O Tutorial da Praxe (Do Blocking aos Channels)
Comecei por seguir o [tutorial oficial de coroutines e channels da Kotlin](https://kotlinlang.org/docs/coroutines-and-channels.html). A app inicialmente era daquelas que "congela" a UI inteira enquanto vai buscar os dados ao GitHub. Fui evoluindo o código passando por várias fases clássicas de quem lida com chamadas assíncronas:
* **Blocking**: A bater na parede, a UI congelava.
* **Background & Callbacks**: A velha escola do multithreading, mas que rapidamente se torna um pesadelo de ler (o famoso *callback hell*).
* **Suspend**: A introdução do `suspend` no Kotlin. O código fica com um aspeto síncrono mas comporta-se de forma assíncrona.
* **Concurrent & Not Cancelable**: A fazer pedidos em paralelo (`async` / `await`), mas ainda com falhas no cancelamento se o utilizador desistisse a meio.
* **Progress & Channels**: A introdução de channels para ir enviando resultados intermédios em tempo real para a UI, em vez de esperar que o batch inteiro termine.
### 2. StateFlow e o Backing Property Pattern (V2)
Depois das bases assentes, fiz um clone do projeto para `intro-coroutinesV2` para integrar um controlo de estado mais moderno com **StateFlow**, em vez de depender tanto de eventos avulsos para a UI.
Para a gestão de estado:
* Criei a *data class* `LoadingStateData` para representar os vários estados possíveis (ex: a carregar, quantos faltam, concluído).
* Implementei o **Backing Property Pattern** na interface `Contributors`: 
  * Criei um `_loadingState` privado (do tipo `MutableStateFlow`) onde faço as atualizações de estado do meu lado;
  * Expus um `loadingState` público (do tipo `StateFlow` imutável) para a UI observar sem perigo de o conseguir alterar indevidamente.
* Criei os métodos `updateLoadingStatus` e `observeLoadingStatus`, fazendo a ponte entre a lógica de negócio e as reações visuais na aplicação.
### 3. Limpeza com um Progress Channel Intermédio
No *Step 3*, voltei à variante `loadContributors` no modo `CHANNELS` e dei-lhe uma valente limpeza. Em vez de enviar resultados diretamente da coroutine de *fetch* para o `updateResults`, meti um `Channel` intermédio (chamado `progressChannel`) pelo meio. 
**Porquê?** 
Isto permitiu uma melhor separação de responsabilidades. O emissor só se preocupa em enviar dados para o canal, e o consumidor do outro lado lê e atualiza a UI. Como bónus, melhorou o controlo de fluxo e tornou o cancelamento da operação (caso o utilizador mande parar o pedido) muito mais limpo e previsível.
---
## Como é que as peças encaixam? 
Para quem quiser mergulhar no código, a arquitetura agora funciona da seguinte forma:
* **`Contributors`**: É o motor da coisa. Lida com a lógica de ir buscar os dados. Agora também é a dona do *StateFlow* (`_loadingState`), que vai sendo atualizado ao longo dos pedidos.
* **`LoadingStateData` & `LoadingStatus`**: Funcionam como o "pacote de informação" sobre o que está a acontecer no momento. O modelo que o `StateFlow` carrega.
* **`ContributorsUI`**: A nossa interface (Swing). Agora é estúpida (no bom sentido): só se preocupa em desenhar botões e texto. Fica a observar (`observeLoadingStatus`) as emissões do StateFlow público (`loadingState`) e reage desenhando a barra de progresso ou preenchendo a lista à medida que os dados chegam dos **channels**.
* **Os Channels**: O `progressChannel` atua como um cano por onde escorrem os repositórios à medida que vão sendo lidos da API do GitHub. Permite que a UI seja atualizada de forma incremental e contínua.