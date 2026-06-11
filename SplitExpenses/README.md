<!-- Replace [X] and Title -->
# Trabalho Final `Split Expenses`

**Course:** Desenvolvimento de Aplicações Móveis (DAM)

**Student Number:** `51392`

**Student Name:** `João Póvoa`

**Student Email:** `51392@alunos.isel.pt`

**Student class:** `LEIM61D`

**Student GitHub:** `https://github.com/JPovoaDev/DAM`

**Date:** `12 de Junho de 2026`

---

# Split Expenses

> Uma aplicação desenhada para dividir despesas de forma justa entre amigos e familiares. Acabaram-se as contas difíceis; a app encarrega-se de calcular quem deve a quem em tempo real.

## Features

- [x] **Gestão de Autenticação** — Registo e Login em tempo real integrando Firebase Authentication.
- [x] **Criação e Gestão de Grupos** — Os utilizadores podem criar grupos de viagens/jantares e adicionar membros registados.
- [x] **Cálculo de Dívidas (State sharing between users)** — O algoritmo calcula automaticamente as parcelas, identificando de forma "greedy" quem deve pagar a quem e sincronizando o estado com todos os membros na Firebase Firestore.
- [x] **AI integration (remote API)** — O ecrã 'Explorar' utiliza a API do modelo Google Gemini AI para injetar conteúdo dinâmico na aplicação.
- [x] **Design Moderno** — UI reativa contruída inteiramente com **Jetpack Compose** e **Material 3**.

## Stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · ViewModel · MVVM ·
StateFlow · Repository Pattern · Koin · Firebase Auth · Firebase Firestore.

## Architecture & Technical Decisions

Esta secção detalha as principais escolhas arquiteturais e tecnológicas da aplicação, justificando o porquê de terem sido adotadas em detrimento de abordagens mais antigas, em total alinhamento com as boas práticas lecionadas na cadeira de DAM.

### 1. Arquitetura MVVM (Model-View-ViewModel) e UDF (Unidirectional Data Flow)
**Como foi feito:** A aplicação foi estruturada separando rigorosamente a Interface de Utilizador (View), a Lógica de Apresentação (ViewModel) e os Dados (Model/Repository). O fluxo de dados é estritamente unidirecional (UDF): a View envia eventos (ex: `login()`, `addExpense()`) para o ViewModel, e o ViewModel processa a lógica de negócio e expõe um único estado imutável (`UiState`).
**Porquê:** Esta abordagem evita o "estado espalhado" (*spaghetti state*) e inconsistente ao longo dos ecrãs. A View torna-se passiva, apenas "reagindo" ao estado atual emitido pelo ViewModel. Isto facilita imenso a realização de testes unitários, garante a imutabilidade do comportamento da app e é o padrão oficial recomendado pela Google para desenvolvimento Android moderno.

### 2. Padrão de Repositório (Repository Pattern)
**Como foi feito:** Foram criadas classes como o `AuthRepository` e o `GroupRepository` cuja única responsabilidade é falar com a base de dados (Firebase Firestore) e com o serviço de autenticação (Firebase Auth). Os nossos ViewModels não sabem que o Firebase existe, apenas invocam funções Kotlin padrão pertencentes a estes Repositórios.
**Porquê:** O acoplamento direto entre a interface/lógica de apresentação e bibliotecas externas torna o código inflexível. Ao utilizarmos o padrão de Repositório, isolamos a origem dos dados (Princípio da Responsabilidade Única - SOLID). Se no futuro a Backend mudar do Firebase para uma API REST própria com Ktor ou Retrofit, não precisamos de alterar uma única linha na UI ou nos ViewModels.

### 3. Injeção de Dependências com Koin
**Como foi feito:** Em vez de instanciar manualmente e repetidamente dependências usando chamadas como `FirebaseAuth.getInstance()` espalhadas pelas Activities e Fragments, configurámos a framework **Koin** (no ficheiro `AppModule.kt`). O Koin atua como um fornecedor central que constrói os Repositórios como `Singletons` e os injeta de forma invisível nos construtores dos ViewModels. Na interface (Jetpack Compose), utilizamos apenas o elegante helper `koinViewModel()`.
**Porquê:** Fazer "hardcode" de inicializações destrói o princípio da inversão de dependência (Dependency Inversion). O Koin é desenhado com uma sintaxe fluente e puramente pensada para Kotlin, sendo muito mais leve e de fácil setup do que o Hilt. Resolve o escopo do ciclo de vida das classes para que os dados sobrevivam enquanto a App estiver a rodar e possam até ser facilmente simulados com *Mocks* durante os testes.

### 4. Jetpack Compose e Otimização de Estado (`collectAsStateWithLifecycle`)
**Como foi feito:** Toda a UI abandonou o antigo paradigma de layouts em ficheiros XML em favor do Jetpack Compose (UI Declarativa). Além disso, a observação do estado nos ecrãs (que provém de fluxos contínuos `StateFlow` nos ViewModels) foi implementada usando extensões sensíveis ao Lifecycle, especificamente o `.collectAsStateWithLifecycle()`.
**Porquê:** O Jetpack Compose é o padrão da indústria para desenho de UI Android, providenciando menos *boilerplate* e mais flexibilidade. A escolha do `.collectAsStateWithLifecycle()` em vez do `.collectAsState()` básico foi uma decisão de **performance e fiabilidade extrema**: esta função monitoriza se a App/Activity não está visível no ecrã (ex: utilizador foi ao Home Menu) e suspende momentaneamente a observação e processamento de dados do repositório, retomando quando volta. Isto poupa CPU, bateria, tráfego de dados do Firebase e previne crashes de alteração de interface quando a janela está em background.

### 5. Coroutines e Kotlin Flows em vez de Callbacks
**Como foi feito:** As primitivas assíncronas padrão do Java/Firebase baseadas em Listeners (ex: `addOnSuccessListener`, `addOnFailureListener`) foram substituídas pelo poder das Coroutines (`suspend functions` aliadas ao `.await()`). Para receber dados contínuos e reativos (como a lista de grupos em tempo real), encapsulámos os listeners de Firestore dentro de blocos `callbackFlow`, emitindo streams de dados puros (`Flow<T>`).
**Porquê:** O temido "Callback Hell" (onde cada operação aninha uma função dentro de outra função) produz um fluxo confuso. As Coroutines permitem lidar com operações de rede como se o código estivesse escrito sequencialmente e de forma síncrona, capturando exceções nativamente em blocos `try-catch`. O uso de `Flow` faz com que as alterações nos servidores se rebatam de forma automática para o telemóvel dos vários utilizadores envolvidos nas contas.

### 6. Algoritmo "Greedy" (Guloso) para Divisão de Dívidas
**Como foi feito:** No `GroupDetailViewModel`, os saldos não são geridos numa relação isolada de "cada despesa gera um pagamento a outro". O motor apanha todas as despesas e apura um "Saldo Líquido" centralizado para cada participante (+ valores a receber, - valores a dever). De seguida, usamos um "Greedy Algorithm" iterativo em que pegamos nas pessoas que devem e forçamo-las a saldar logo com as pessoas a quem é devido crédito até o limite desse crédito.
**Porquê:** Reduz drasticamente o número de transferências necessárias na vida real entre os amigos. Em vez do Utilizador A fazer uma transferência MBWay para o B e o B depois enviar ao C, o algoritmo neutraliza a teia de contas e encurta a corrente apontando diretamente quem tem de dar os saldos necessários aos credores finais. Otimizando o número de passos de regularização.

### 7. Segurança de Credenciais e AI Injection (BuildConfig)
**Como foi feito:** Integrámos inteligência artificial generativa invocando a API remota do **Google Gemini** para recomendar locais ou dados do grupo no ecrã "Explorar". Para não cometermos o erro crítico de colocar a chave `GEMINI_API_KEY` visível em `Strings.xml` ou código limpo, recorremos à geração do `BuildConfig`, lendo essa chave unicamente a partir de um `local.properties` do próprio computador que não sobe com os commits do Git.
**Porquê:** A segurança de credenciais API é crucial em qualquer relatório académico ou ambiente profissional. Mantendo a chave segura no Gradle isola-nos de ataques de raspagem (*scraping*) de repositórios open-source do GitHub por via de bots, bloqueando eventuais cobranças indesejadas pelo serviço da Google.

## AI Usage

O projeto contou com a assistência de ferramentas de Inteligência Artificial para acelerar o processo de reestruturação do código em arquitetura moderna de software e transição de Callbacks para fluxos reativos Kotlin (`Coroutines/Flows`). A API da Gemini AI (via Remote API) foi também adotada como *Feature* na App.
