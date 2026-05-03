# 09 Extensões de Funcionalidades

Este documento detalha as funcionalidades avançadas implementadas ou planeadas para a aplicação Dog Image, expandindo a funcionalidade base com padrões arquiteturais modernos e elementos robustos de experiência de utilizador.

---

## 1. Padrão Arquitetural MVVM
**Descrição:** A aplicação utiliza o padrão Model-View-ViewModel (MVVM) para garantir uma separação limpa de responsabilidades, elevada testabilidade e uma gestão de estado consciente do ciclo de vida.

### Tarefas de Implementação
- [x] Criar o `DogViewModel` para manter e gerir o estado da UI.
- [x] Implementar o `DogRepository` como a fonte única de verdade para os dados.
- [x] Usar `LiveData` para fluxos de dados observáveis entre o ViewModel e as Activities.
- [x] Desacoplar a lógica de negócio dos componentes do framework Android.

### Alterações de UI Esperadas
- Sem alterações diretas na UI, mas a app torna-se mais responsiva e estável durante mudanças de configuração (como a rotação do ecrã).

### Plano de Implementação
- **Model:** Data class `ImageItem`.
- **View:** `MainActivity`, `ImageDetailsActivity` e layouts XML.
- **ViewModel:** `DogViewModel` coordena a obtenção de dados e as atualizações de estado.
- **Repository:** `DogRepository` abstrai o `DogApiService` e o caching local.

---

## 2. Indicador de Carregamento (ProgressBar)
**Descrição:** Fornece feedback visual ao utilizador durante operações de rede para indicar que os dados estão a ser carregados.

### Tarefas de Implementação
- [x] Adicionar uma `ProgressBar` ao layout `activity_main.xml`.
- [x] Ligar a visibilidade da `ProgressBar` ao LiveData `isLoading` no ViewModel.
- [x] Integrar com o `SwipeRefreshLayout` para estados de carregamento unificados.

### Alterações de UI Esperadas
- Uma barra de progresso centrada aparece quando a app inicia ou quando está a ser carregado um novo cão.
- A animação de swipe-to-refresh permanece ativa até que a chamada à API termine.

### Plano de Implementação
- O ViewModel define `_isLoading.value = true` antes da chamada à API e `false` no bloco `finally`.
- A UI observa o `isLoading` e alterna a visibilidade do elemento `ProgressBar`.

---

## 3. Ecrã de Detalhes da Imagem
**Descrição:** Um ecrã dedicado que oferece uma visão focada de uma imagem de cão selecionada, juntamente com metadados adicionais.

### Tarefas de Implementação
- [x] Criar `ImageDetailsActivity` e `activity_image_details.xml`.
- [x] Implementar click listeners no adapter da `RecyclerView` para despoletar a navegação.
- [x] Passar os dados da imagem (URL) através de extras do `Intent`.
- [x] Adicionar um botão de alternância de favorito no ecrã de detalhes.

### Alterações de UI Esperadas
- Clicar numa imagem na galeria abre uma visualização em ecrã inteiro.
- Suporte para o botão "Back" para voltar à galeria principal.

### Plano de Implementação
- Usar uma `ImageView` com um tipo de escala zoomable ou de largura total.
- Mostrar o URL de origem num `TextView`.
- Integração com o Repositório para marcar a imagem específica como favorita.

---

## 4. Itens Favoritos (Fila FIFO Máx 5)
**Descrição:** Permite aos utilizadores guardarem até 5 imagens favoritas. Quando o limite é atingido, o favorito mais antigo é removido para dar lugar ao novo (First-In, First-Out).

### Tarefas de Implementação
- [x] Implementar a lógica FIFO no `DogRepository` usando uma `MutableList`.
- [x] Limitar o tamanho da lista a 5 e remover o item no índice 0 ao adicionar um 6º.
- [ ] Implementar um "Favorites Hub" ou uma faixa horizontal no ecrã principal para acesso direto aos 5 favoritos.

### Alterações de UI Esperadas
- Uma secção dedicada (ou card) no ecrã principal a mostrar a contagem atual de favoritos.
- Acesso direto aos favoritos através das suas imagens a partir de um componente hub.

### Plano de Implementação
- O Repositório mantém uma lista `synchronized` para segurança entre threads.
- Lógica FIFO: `if (list.size > 5) list.removeAt(0)`.
- UI: Usar uma `RecyclerView` horizontal ou um conjunto estático de `ImageViews` para a funcionalidade de acesso direto.

---

## 5. Gestão de Cache (10 à Frente/Atrás)
**Descrição:** Mantém uma cache de até 50 itens e garante uma janela deslizante de 10 itens à frente e 10 atrás da posição atual para uma navegação fluida.

### Tarefas de Implementação
- [x] Implementar a cache básica de 50 itens no `DogRepository`.
- [ ] Adicionar lógica para seguir a "posição atual" no fluxo de imagens.
- [ ] Implementar o pré-carregamento (pre-fetching) de 10 imagens à frente da visualização atual.
- [ ] Garantir que o Indicador de Carregamento reflete o estado destes carregamentos em background.

### Alterações de UI Esperadas
- Scroll e navegação mais fluidos entre imagens à medida que estas são pré-carregadas em background.
- Redução de estados "em branco" ao visualizar detalhes ou ao fazer scroll para trás.

### Plano de Implementação
- Usar uma `LinkedList` ou estrutura semelhante para a cache de 50 itens.
- Implementar uma classe `CacheManager` (ou melhorar o Repositório) para lidar com a lógica da janela deslizante e disparar pré-carregamentos com base no índice atual.

---

## 6. Acesso Offline
**Descrição:** Garante que os utilizadores podem continuar a ver os seus favoritos e os últimos 50 itens em cache quando o dispositivo está offline.

### Tarefas de Implementação
- [x] Adicionar verificações de conectividade de rede ou gerir `UnknownHostException`.
- [x] Implementar lógica de fallback no Repositório para servir a partir da cache/favoritos quando as chamadas à API falham.
- [ ] (Opcional) Persistir a cache no disco usando Room ou armazenamento interno para persistência entre reinícios da app.

### Alterações de UI Esperadas
- A app mostra os cães em cache mesmo quando o modo de voo está ativo.
- Um Toast ou indicador de "A visualizar Offline" quando os dados são servidos a partir da cache.

### Plano de Implementação
- Intercetar exceções no Repositório.
- Devolver um item `random()` da lista `_cache` ou `_favorites` como fallback.

---

## 7. Gestão de Erros Suave
**Descrição:** Garante que a aplicação não crasha em caso de falhas de rede e fornece feedback claro e acionável ao utilizador.

### Tarefas de Implementação
- [x] Envolver as chamadas à API em blocos `try-catch`.
- [x] Expor mensagens de erro através do LiveData `errorMessage`.
- [x] Implementar mensagens Toast ou Snackbar na UI para os erros.

### Alterações de UI Esperadas
- Mensagens de erro amigáveis (ex: "Erro de Rede/API: Sem ligação") em vez de a app ir abaixo.
- Elementos da UI (como o Refresh) permanecem interativos após a ocorrência de um erro.

### Plano de Implementação
- O ViewModel captura as exceções e atualiza um LiveData baseado em strings.
- A `MainActivity` observa este LiveData e dispara um evento de UI (Toast/Snackbar).
