# 08 Plano de Implementação

Este plano divide o desenvolvimento da aplicação Dog Image em passos muito pequenos e fáceis de gerir.

> [!IMPORTANT]
> - Todos os passos têm de usar exclusivamente **Kotlin** e **XML Views** (Nada de Jetpack Compose).
> - Respeita a arquitetura **MVVM** definida nos documentos anteriores.
> - A aplicação depende exclusivamente da **Dog API** (`https://dog.ceo/api/breeds/image/random`).

## Fase 1: Configuração do Projeto e Modelos Base

**1. Criar o Projeto Android**
- Inicializar um novo projeto Android padrão.
- Configurar os ficheiros de build para Kotlin e XML Views tradicionais.

**2. Criar a Data Class ImageItem**
- Definir `ImageItem` com as propriedades `id` (String), `url` (String) e `title` (String), mapeando para a resposta da Dog API.

## Fase 2: Camada de Rede e Dados (API e Repositório)

**3. Configurar Dependências do Retrofit**
- Adicionar dependências de Retrofit, Gson (ou Moshi) e Coroutines de Kotlin ao `build.gradle.kts`.
- Adicionar permissões padrão de Internet ao Android Manifest.

**4. Criar o Serviço de API (Dog API)**
- Criar a interface Retrofit definindo o pedido `GET` para a Dog API (`https://dog.ceo/api/breeds/image/random`).

**5. Criar o Repositório**
- Construir a classe que será a fonte única de verdade e que interage com o Serviço de API.

## Fase 3: Configurar Camada de Apresentação (ViewModel e Base da UI)

**6. Criar o ViewModel**
- Configurar um ViewModel usando `LiveData` ou `StateFlow` para manter o estado da aplicação.
- Ligar o ViewModel para ir buscar dados através do Repositório.

**7. Criar o Adapter da RecyclerView**
- Implementar um adapter e o padrão ViewHolder capaz de ligar os dados de `ImageItem` a um layout de item de lista (ex: usando Glide ou Coil para carregar imagens).

**8. Desenhar o `activity_main.xml`**
- Construir o layout principal com uma Toolbar, `SwipeRefreshLayout` (para atualizar e carregar) e uma `RecyclerView` para a galeria.

## Fase 4: Juntar Tudo

**9. Ligar o ViewModel à UI**
- Observar o estado do ViewModel na `MainActivity` e passar as atualizações para o adapter da RecyclerView.

**10. Mostrar Imagens**
- Verificar se o carregamento de rede consegue obter e mapear o URL da imagem para a UI com sucesso.

**11. Adicionar Funcionalidade de Atualização (Refresh)**
- Implementar um trigger do utilizador (ex: swipe-to-refresh) para ir buscar uma nova imagem de cão aleatória e adicioná-la ao estado da aplicação.

**12. Adicionar Indicador de Carregamento**
- Ligar os indicadores de progresso da UI para mostrarem quando há chamadas à API ativas no ViewModel.

## Fase 5: Navegação e Ecrãs Secundários

**13. Criar a ImageDetailsActivity**
- Gerar a `ImageDetailsActivity` e o seu layout XML correspondente contendo uma visualização de imagem em ecrã inteiro, um campo de texto para o URL e um botão de favorito.

**14. Implementar Navegação**
- Adicionar click listeners aos itens da `RecyclerView` para despoletar um `Intent` que navega nativamente para o Ecrã de Detalhes.

## Fase 6: Requisitos Avançados (Cache e Favoritos)

**15. Implementar Favoritos (FIFO máximo 5)**
- Melhorar o repositório para manter uma fila estruturada de até 5 itens favoritados. Expulsar o item mais antigo ao adicionar o 6º.
- Implementar um componente de UI (ex: uma faixa horizontal) para acesso direto a estes 5 favoritos a partir de qualquer ecrã.

**16. Implementar Cache (Máximo 50)**
- Estabelecer uma cache em memória ou persistida que guarde uma sequência máxima dos 50 itens visualizados recentemente.
- Implementar lógica para manter pelo menos 10 itens à frente e 10 atrás da posição atual durante a navegação.
- Garantir que o indicador de carregamento é relativo ao que se pretende carregar.

**17. Adicionar Suporte Offline**
- Atualizar a lógica da Camada de Dados. Quando a conectividade falha, servir conteúdo da Cache local de 50 itens e/ou da fila de Favoritos.

**18. Gerir Erros da API**
- Implementar uma gestão de exceções robusta ao nível dos pedidos à API e ligar estas falhas a estados da UI para mostrar mensagens de erro (Snackbars/Toasts) de forma suave em vez de a app ir abaixo.
