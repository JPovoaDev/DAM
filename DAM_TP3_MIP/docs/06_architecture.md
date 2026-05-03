# 06 Arquitetura

Este projeto segue rigorosamente o padrão arquitetural Model-View-ViewModel (MVVM). Este paradigma impõe uma separação rigorosa de responsabilidades, desacoplando a lógica da interface de utilizador das operações de dados subjacentes.

## Camadas Arquiteturais

### 1. Camada de Interface de Utilizador (UI)
**Componentes:** `Activities`, XML Layouts, `RecyclerView`, Adapters.
**Responsabilidades:** 
- Renderiza o estado visual para o utilizador.
- Captura eventos de entrada (ex: cliques, pull-to-refresh).
- Observa os dados expostos pelo ViewModel e atualiza a UI de forma declarativa.
- Não contém absolutamente nenhuma lógica de negócio ou mecanismos de obtenção de dados.

### 2. Camada de ViewModel
**Componentes:** Classes `ViewModel`, `LiveData` ou `StateFlow`.
**Responsabilidades:**
- Atua como intermediário entre a UI e o Repositório.
- Mantém e gere o estado relacionado com a UI de uma forma consciente do ciclo de vida (lifecycle-aware).
- Expõe fluxos de dados observáveis (`LiveData`/`StateFlow`) aos quais a camada de UI subscreve.
- Encaminha as ações do utilizador da UI para o Repositório, garantindo que a thread principal (UI thread) permanece livre.

### 3. Camada de Repositório (Repository)
**Componentes:** Classes `Repository`, Gestores de Cache, Gestores de Favoritos.
**Responsabilidades:**
- Serve como a "Fonte Única de Verdade" (Single Source of Truth - SSOT) para os dados da aplicação.
- Abstrai as fontes de dados do ViewModel. Decide se deve ir buscar dados novos à API remota ou devolver dados da cache local.
- Gere a lógica de persistência local (ex: manter a Cache de 50 itens e a fila FIFO de 5 Favoritos).

### 4. Camada de Serviço de API (API Service)
**Componentes:** Interface de Rede (ex: abstrações de Retrofit/HttpURLConnection).
**Responsabilidades:**
- Gere a comunicação de rede e os pedidos HTTP para a Dog API.
- Desserializa os payloads JSON em objetos de dados Kotlin ao nível da aplicação (`ImageItem`).

## Características do Sistema

### Separação de Responsabilidades
Ao delinear as responsabilidades entre as camadas de UI, ViewModel e Dados, a arquitetura garante que os componentes permanecem modulares. A UI está completamente isolada do conhecimento da rede, enquanto o ViewModel atua exclusivamente como uma ponte conceptual, garantindo elevada testabilidade e manutenibilidade.

### Suporte Offline
A camada de Repositório é responsável por determinar a disponibilidade de uma ligação de rede. Quando está offline, recorre suavemente à Cache em memória (até 50 itens) ou à fila de Favoritos para servir conteúdo, permitindo uma funcionalidade parcial ininterrupta.

### Gestão de Erros
Falhas de rede, exceções de parsing e cenários de timeout são intercetados nas camadas de Repositório ou Serviço de API. Estas exceções são traduzidas em wrappers de estado estruturados e amigáveis para a UI (ex: uma sealed class `Result` ou `UiState`) e passadas para o ViewModel. Isto garante que a aplicação não crasha e que a UI pode renderizar estados de erro ou mecanismos de repetição apropriadamente.
