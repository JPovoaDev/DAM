# Registo de Prompts (Prompts Log)

Este documento regista os prompts utilizados para gerar a documentação e o código para o projeto.

## Prompt 1
**Objetivo:** Gerar a visão geral inicial da app.
**Prompt utilizado:** Generate docs/01_overview.md. App: Android app que recolhe imagens de cães da Dog API e as apresenta. Inclui: Objetivo, Utilizadores alvo, Comportamento geral do sistema. Mantém-no simples e claro.
**Resultado:** Criado o `01_overview.md` descrevendo o objetivo principal e o público da aplicação.

## Prompt 2
**Objetivo:** Definir o conjunto de funcionalidades da aplicação.
**Prompt utilizado:** Generate docs/02_features.md. Funcionalidades: 1. Recolher imagens de cães da Dog API 2. Mostrar imagens numa RecyclerView 3. Atualizar imagens 4. Indicador de carregamento 5. Ecrã de detalhes da imagem 6. Imagens favoritas (FIFO máx 5) 7. Cache até 50 imagens 8. Acesso offline 9. Gerir erros da API. Mantém-no estruturado.
**Resultado:** Criado o `02_features.md` listando as 9 funcionalidades principais pedidas numa estrutura limpa.

## Prompt 3
**Objetivo:** Definir os layouts dos ecrãs e componentes.
**Prompt utilizado:** Generate docs/03_screens.md. Ecrãs: Ecrã Principal: (Toolbar, RecyclerView, Botão de atualização, Indicador de carregamento), Ecrã de Detalhes: (Imagem completa, URL da imagem, Botão de favorito), Acesso aos Favoritos: (Secção mostrando até 5 imagens favoritas). Sê claro e estruturado.
**Resultado:** Criado o `03_screens.md` com secções mapeadas para os principais componentes de interface necessários.

## Prompt 4
**Objetivo:** Definir os modelos de dados da aplicação.
**Prompt utilizado:** Generate docs/04_data_model.md. Baseado na resposta da Dog API. Definir: ImageItem: id, url, FavoriteQueue: maxSize: 5, comportamento FIFO, Cache: maxSize: 50, armazena itens antes e depois da posição atual. Mantém-no alinhado com as data classes de Kotlin.
**Resultado:** Criado o `04_data_model.md` especificando esquemas de dados que mapeiam a resposta da API para estruturas de estado da aplicação.

## Prompt 5
**Objetivo:** Planear a navegação da aplicação.
**Prompt utilizado:** Generate docs/05_navigation.md. Navegação: MainActivity → ImageDetailsActivity. Mantém-no simples.
**Resultado:** Criado o `05_navigation.md` descrevendo a estrutura de encaminhamento a partir da Main Activity.

## Prompt 6
**Objetivo:** Definir a arquitetura do sistema.
**Prompt utilizado:** Generate docs/06_architecture.md usando MVVM. Camadas: UI, ViewModel, Repository, API Service. Inclui: Responsabilidades, Separação de responsabilidades, Suporte offline, Gestão de erros. Mantém-no académico e limpo.
**Resultado:** Criado o `06_architecture.md` impondo um fluxo de dependências MVVM.

## Prompt 7
**Objetivo:** Padronizar a utilização e restrições da API.
**Prompt utilizado:** Generate docs/07_api_usage.md usando Dog API. API endpoint: https://dog.ceo/api/breeds/image/random Método: GET Resposta: { "message": "image_url", "status": "success" } Mapeamento: message -> ImageItem.url IMPORTANTE: Esta é a ÚNICA API usada no projeto, não uses nenhuma outra API.
**Resultado:** Criado o `07_api_usage.md` documentando claramente o único endpoint de API autorizado para o projeto.

## Prompt 8
**Objetivo:** Construir um roteiro de implementação.
**Prompt utilizado:** Generate docs/08_implementation_plan.md. Divide em passos MUITO PEQUENOS. Usa Dog API e MVVM. Passos: [1-18 listados]. Mantém os passos curtos e claros.
**Resultado:** Criado o `08_implementation_plan.md` sequenciando o desenvolvimento em 18 passos fáceis de gerir.

## Prompt 9
**Objetivo:** Definir as restrições e comportamentos do agente de IA.
**Prompt utilizado:** Generate agents.md. Regras: - Ler sempre /docs primeiro - Seguir arquitetura MVVM - Seguir plano de implementação passo a passo - Usar apenas Kotlin - Usar apenas XML Views - Não gerar ficheiros grandes de uma vez. REGRA DE API: - Usar APENAS Dog API - Não mudar a API. Atua como um assistente de desenvolvimento disciplinado.
**Resultado:** Criado o `agents.md` na raiz do projeto para impor limites de desenvolvimento rigorosos.

## Prompt 10
**Objetivo:** Abstrair toda a documentação num README na raiz.
**Prompt utilizado:** Generate README.md baseado em todos os ficheiros dentro de /docs. IMPORTANTE: - Não inventar nada - Usar Dog API - Refletir funcionalidades e ecrãs com precisão. Incluir: - Objetivo da app - Funcionalidades - API usada - Ecrãs - Como correr. Mantém-no limpo e estruturado.
**Resultado:** Criado um `README.md` unificado na raiz resumindo o objetivo, funcionalidades, API, ecrãs e instruções de execução baseado puramente na pasta `docs`.

## Prompt 11
**Objetivo:** Estabelecer regras de execução para os passos de implementação.
**Prompt utilizado:** Execute Step X de docs/08_implementation_plan.md. Regras: - Seguir MVVM - Usar apenas Dog API - Gerar apenas ficheiros necessários - Explicar antes de codificar - Não mudar ficheiros não relacionados. Aguarda próxima instrução.
**Resultado:** Regras de execução aceites; a aguardar pelo número do passo específico da tarefa para executar.

## Prompt 12
**Objetivo:** Executar o Passo 1 do plano de implementação.
**Prompt utilizado:** Follow docs/06_architecture.md (MVVM) and docs/07_api_usage.md (Dog API). Não uses nenhuma outra API. Gera apenas o que for necessário para este passo. Explica brevemente antes de codificar. Executa o Passo 1 de docs/08_implementation_plan.md. Cria um novo projeto Android usando: - Kotlin - XML Views (nada de Jetpack Compose) - Empty Activity. Explica a estrutura brevemente. Não adiciones funcionalidades extra ainda.
**Resultado:** Criada a estrutura base do projeto Android focada em XML views e Kotlin.

## Prompt 13
**Objetivo:** Executar o Passo 2 do plano de implementação.
**Prompt utilizado:** Para cada passo Seguir docs/06_architecture.md (MVVM) and docs/07_api_usage.md (Dog API). Não uses nenhuma outra API. Gera apenas o que for necessário para este passo. Explica brevemente antes de codificar. Executa o Passo 2. Cria a data class ImageItem baseada na Dog API. Campos: - id: String - url: String. Nota: A Dog API só devolve o URL, por isso gera um id único se necessário. Cria apenas a classe do modelo.
**Resultado:** Criada a data class `ImageItem` com um UUID gerado automaticamente para o identificador.

## Prompt 14
**Objetivo:** Executar o Passo 3 do plano de implementação.
**Prompt utilizado:** Execute Step 3. Adicionar dependências do Retrofit. Incluir: - Retrofit - conversor Gson. Não implementes a API ainda. Apenas configura as dependências.
**Resultado:** Adicionado o Retrofit, Gson e a permissão de INTERNET necessária ao projeto para preparar as tarefas de API de rede.

## Prompt 15
**Objetivo:** Executar o Passo 4 do plano de implementação.
**Prompt utilizado:** Execute Step 4. Criar interface do serviço de API usando a Dog API: https://dog.ceo/api/breeds/image/random. Usa Retrofit. Mapeia a resposta corretamente: message -> ImageItem.url. Não implementes o repositório ainda.
**Resultado:** Criadas as interfaces de API e a data class de resposta usando Retrofit para ir buscar dados à Dog API.

## Prompt 16
**Objetivo:** Executar o Passo 5 do plano de implementação.
**Prompt utilizado:** Execute Step 5. Criar camada de Repositório. Responsabilidades: - Chamar o serviço de API - Converter a resposta para ImageItem. Não adiciones caching ainda. Mantém-no simples.
**Resultado:** Criado o `DogRepository` para chamar de forma segura o endpoint de rede via uma coroutine IO e mapear a resposta para o modelo `ImageItem`.

## Prompt 17
**Objetivo:** Executar o Passo 6 do plano de implementação.
**Prompt utilizado:** Execute Step 6. Criar ViewModel. Responsabilidades: - Ir buscar dados ao Repositório - Expor lista LiveData de ImageItem - Gerir estado de carregamento. Não implementes a UI ainda.
**Resultado:** Implementado o `DogViewModel` gerindo estados assíncronos, expondo fluxos LiveData para imagens e estado de carregamento.

## Prompt 18
**Objetivo:** Executar o Passo 7 do plano de implementação.
**Prompt utilizado:** Execute Step 7. Criar Adapter da RecyclerView. Incluir: - ViewHolder - Ligar ImageItem (URL da imagem). Não geras eventos de clique ainda.
**Resultado:** Gerado o `DogAdapter`, o layout `item_dog_image.xml` e adicionada a dependência do Glide para facilitar o processamento de imagens no layout XML.

## Prompt 19
**Objetivo:** Executar o Passo 8 do plano de implementação.
**Prompt utilizado:** Execute Step 8. Desenhar activity_main.xml. Incluir: - RecyclerView - Botão (refresh) - ProgressBar (carregamento). Mantém o layout simples.
**Resultado:** Substituído o placeholder `activity_main.xml` por um ConstraintLayout contendo uma RecyclerView, um botão de refresh e uma ProgressBar centrada.

## Prompt 20
**Objetivo:** Executar o Passo 9 do plano de implementação.
**Prompt utilizado:** Execute Step 9. Ligar o ViewModel à MainActivity. - Observar LiveData - Atualizar RecyclerView. Não implementes o refresh ainda.
**Resultado:** Gerados os construtores `ApiClient` e `ViewModelFactory`, configurando a `MainActivity.kt` para observar o LiveData e passar os valores ao `DogAdapter`.

## Prompt 21
**Objetivo:** Executar o Passo 10 do plano de implementação.
**Prompt utilizado:** Execute Step 10. Carregar e mostrar imagens na RecyclerView. Usa uma biblioteca de carregamento de imagens (Glide ou Coil). Garante que as imagens são mostradas corretamente a partir do URL.
**Resultado:** Implementado o pedido de captura inicial na `MainActivity.kt` ao criar, para que a aplicação consulte imediatamente e encaminhe uma imagem para ser processada pela nossa configuração existente do adapter Glide.

## Prompt 22
**Objetivo:** Executar o Passo 11 do plano de implementação.
**Prompt utilizado:** Execute Step 11. Implementar botão de refresh. Ao clicar: - Ir buscar nova(s) imagem(ns) - Atualizar RecyclerView. Mantém a lógica dentro do ViewModel.
**Resultado:** Ligado um OnClickListener ao botão de refresh, delegando o pedido de carregamento de dados explicitamente ao ViewModel.

## Prompt 23
**Objetivo:** Executar o Passo 12 do plano de implementação.
**Prompt utilizado:** Execute Step 12. Usar ProgressBar. Mostrar quando está a carregar: - Antes da chamada à API. Esconder quando: - Dados carregados. Ligar ao estado de carregamento do ViewModel.
**Resultado:** Verificados os observadores de estado pré-existentes dentro do `ViewModel` e da `MainActivity` que já cumprem perfeitamente os requisitos de rede da ProgressBar.

## Prompt 24
**Objetivo:** Executar o Passo 13 do plano de implementação.
**Prompt utilizado:** Execute Step 13. Criar ImageDetailsActivity. Mostrar: - Imagem completa - URL da imagem. Receber dados via Intent.
**Resultado:** Criada a `ImageDetailsActivity`, registada dinamicamente no Manifest, e estilizado o `activity_image_details.xml` capaz de capturar payloads de intent e desenhar as imagens da Dog API em alta resolução.

## Prompt 25
**Objetivo:** Executar o Passo 14 do plano de implementação.
**Prompt utilizado:** Execute Step 14. Adicionar click listener na RecyclerView. Ao clicar: - Abrir ImageDetailsActivity - Passar dados da imagem. Mantém a navegação simples.
**Resultado:** Ligado um listener de clique de item dinamicamente dentro do `DogAdapter`, disparando um Android Intent carregado com o payload do URL do item específico.

## Prompt 26
**Objetivo:** Executar o Passo 15 do plano de implementação.
**Prompt utilizado:** Execute Step 15. Implementar sistema de Favoritos. Regras: - Máx 5 itens - FIFO (remover mais antigo quando cheio). Permitir: - Adicionar/remover favorito. Não implementes acesso na UI ainda.
**Resultado:** Estabelecida uma fila FIFO local em memória dentro do `DogRepository`, restringindo a retenção a exatamente 5 elementos e fornecendo mecânicas de ADICIONAR/REMOVER.

## Prompt 27
**Objetivo:** Executar o Passo 16 do plano de implementação.
**Prompt utilizado:** Execute Step 16. Implementar sistema de cache: - Máx 50 itens - Guardar imagens localmente (memória é suficiente por agora). Não incluas favoritos na cache. Mantém a lógica dentro do Repositório.
**Resultado:** Criada uma lista de cache baseada em memória, limitando as instâncias de rede capturadas a 50 itens, desacoplada de forma limpa da lógica de favoritos.

## Prompt 28
**Objetivo:** Executar o Passo 17 do plano de implementação.
**Prompt utilizado:** Execute Step 17. Suporte a modo offline: - Se não houver internet: -> Carregar da cache -> Carregar favoritos. Gere de forma suave.
**Resultado:** Envolvida a tarefa da API Retrofit no repositório com um try-catch para servir automaticamente uma imagem aleatória guardada localmente em cache ou nos favoritos quando o dispositivo não consegue resolver os hosts de rede.

## Prompt 29
**Objetivo:** Executar o Passo 18 do plano de implementação.
**Prompt utilizado:** Execute Step 18. Gerir erros da API: - Falha de rede - Resposta inválida. Mostrar: - Toast ou mensagem. Garante que a app não crasha.
**Resultado:** Criado um feed de LiveData `errorMessage` dentro do `ViewModel` que captura erros terminais originados no repositório e os encaminha de forma limpa para um popup Toast na `MainActivity`.
