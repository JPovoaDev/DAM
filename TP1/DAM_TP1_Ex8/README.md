# Trabalho X — Football Scout

Disciplina: Desenvolvimento de Aplicação Moveis
Aluno :João Pedro Mulano Póvoa
Data: 08/03/2026
URL do Repositório: https://github.com/JPovoaDev/DAM_TP1_Ex8

## 1. Introdução
A aplicação Football Scout foi desenvolvida com o intuito de centralizar informações relativas ao mundo do futebol. Frequentemente, os utilizadores necessitam de aceder a múltiplas plataformas para consultar o histórico de um jogador, os próximos encontros de uma equipa ou as classificações das ligas. O objetivo deste projeto passa por agregar estes dados numa aplicação Android intuitiva, possibilitando a pesquisa de jogadores e equipas, bem como a gestão de favoritos.

## 2. Visão Geral do Sistema
O Football Scout atua como uma base de dados desportiva, fornecendo as seguintes funcionalidades ao utilizador:
- **Pesquisa de Jogadores**: Permite a consulta de atletas profissionais, acedendo a dados como biografia, atributos físicos (altura e peso) e nacionalidade.
- **Histórico de Carreira**: Apresenta, de forma cronológica, as equipas que um determinado jogador representou ao longo da sua carreira.
- **Informação de Equipas**: Possibilita a pesquisa de clubes, detalhando informações sobre o estádio, a história da instituição, o calendário de jogos (anteriores e futuros) e a posição atual na respetiva liga.
- **Favoritos**: Adicionalmente, permite ao utilizador guardar perfis de jogadores e equipas localmente, facilitando o acesso futuro através do ecrã principal.

## 3. Arquitetura e Design
A aplicação foi construída com base na arquitetura **MVVM (Model-View-ViewModel)**, que é a norma recomendada pela Google para o desenvolvimento em Android.

**Estrutura de Pastas e Componentes:**
- `ui`: Engloba as Activities, Fragments e Adapters, sendo a camada estritamente responsável pela apresentação visual.
- `viewmodel`: Contém as classes ViewModel que mantêm os dados de interface independentes do ciclo de vida dos ecrãs (ex: rotações do dispositivo).
- `data/repository`: Implementa o padrão de Repositórios, abstraindo a fonte de dados (API remota ou base de dados local).
- `data/api`: Inclui as definições do Retrofit para a comunicação centralizada com a rede.
- `data/database`: Contém as entidades e os DAOs correspondentes à base de dados Room (SQLite) para armazenamento persistente dos favoritos.

**Decisões de Design**: A adoção do padrão MVVM, complementado com Repositórios, assegura um elevado nível de desacoplamento. A camada de apresentação (UI) interage exclusivamente com o ViewModel, desconhecendo a origem concreta dos dados, o que promove a escalabilidade e facilita a manutenção do código.

## 4. Implementação
**Componentes Principais:**
- `FootballScoutApp`: Classe Application responsável pela inicialização do contexto e configuração estática da base de dados.
- `RetrofitClient`: Instancia o cliente OkHttp e o Retrofit para garantir o processamento dos pedidos HTTP e a desserialização do formato JSON.
- `AppDatabase`: Instância da base de dados local, gerida através da biblioteca nativa Room.
- `PlayerRepository` e `TeamRepository`: Classes que gerem o acesso aos dados. Recebem as estruturas JSON e mapeiam essas respostas para modelos de domínio da aplicação (`Player`, `Team`, `Match`).

**APIs Utilizadas:**
O fornecimento de dados assenta no recurso à **TheSportsDB API** (plano gratuito académico). Para o efeito, são efetuados pedidos aos *endpoints* `/searchplayers.php`, `/lookupformerteams.php` e `/searchteams.php`.

**Listas e Adapters:**
A renderização dinâmica de dados no ecrã (como resultados de pesquisa) recorre à utilização do componente `RecyclerView`, otimizado pela implementação de classes `ListAdapter`. Este modelo permite garantir uma atualização de estado muito eficiente e fluida no lado do utilizador.

## 5. Testes e Validação
**Estratégia de Testes:**
Procedeu-se a testes controlados num emulador Android (AVD) e num dispositivo físico, validando o correto funcionamento dos fluxos de navegação pretendidos para cada funcionalidade da aplicação.

**Casos Limite e Limitações:**
- **Pesquisas Inválidas**: O sistema foi testado mediante a introdução de cadeias de caracteres geradas aleatoriamente, verificando-se o correto tratamento da interface (apresentação de mensagens como "No results found" em vez da interrupção abrupta da execução).
- **Limitações da API Gratuita**: Dado o uso da versão não-comercial da TheSportsDB API, a ausência de campos retornados constituiu um desafio. Tive de fazer com que a aplicação não desse erro nessas alturas, mostrando textos como "Por determinar" quando a API vinha com campos vazios (`null`).

## 6. Instruções de Utilização
- **Requisitos do Sistema**: Dispositivo Android com sistema operativo Nougat (Android 7.0 - API 24) ou superior e acesso permanente à internet.
- **Versão do Ambiente de Desenvolvimento**: Android Studio Jellyfish (2023.3.1) ou versão mais recente.
- **Processo de Compilação**: Importar a diretoria raiz do projeto no Android Studio e aguardar a conclusão automática da sincronização do Gradle.
- **Execução**: Iniciar um emulador (AVD) configurado ou conectar um dispositivo físico por USB (com a depuração ativa), e desencadear a opção "Run" no compilador.

---

# Secções de Engenharia de Software Autónoma

## 7. Estratégia de "Prompting"
No que diz respeito à utilização da Inteligência Artificial, a abordagem baseou-se em instruções direcionadas. Em vez de pedir "faz a app por mim", eu colava a resposta JSON da API e pedia à IA para me ajudar a criar as *Data Classes* normais em Kotlin. Quando a app dava algum erro no telemóvel, eu copiava a mensagem de erro do Logcat e perguntava o porque do erro.

## 8. Fluxo de Trabalho com Agente Autónomo
As ferramentas de IA intervieram no processo formativo através da modelação seguinte:
- **Planeamento**: Apoio nas decisões sobre a organização de pastas arquiteturais e respetivas transições através do Navigation Component.
- **Estruturação de Código**: Apoio substancial na escrita de código mecânico e normativo (criação de DAOs SQL), otimizando o esforço programático final.
- **Depuração Lógica (Debugging)**: Avaliação de *exceptions* complexas geradas nativamente, como uma quebra relacionada a atributos cromáticos incompatíveis num ficheiro XML (`?attr/colorOutlineVariant`). A identificação célere foi apoiada por transcrições de mensagens do compilador.

## 9. Verificação de Artefactos Gerados por IA
Para atestar a validade de qualquer modelo importado com a ajuda exterior, realizaram-se execuções iterativas compiladas em ambiente de teste imediato, evitando um processo de cópia cega. Adicionalmente, fez-se o escrutínio dos pedidos HTTP efetuados assegurando convergência efetiva das propriedades e domínios gerados.

## 10. Contribuição Humana vs IA
- **O meu trabalho**: Fui eu que decidi a ideia da aplicação, escolhi que API usar para as procuras, desenhei a estrutura dos ecrãs na componente gráfica, escolhi as paletes de cor e testei tudo à mão. Toda a arquitetura global e as escolhas de navegação partiram da minha responsabilidade.
- **A ajuda da IA**: Ajudou muito a poupar tempo com o código das *RecyclerViews*, nas corrotinas (operações em segundo plano para não congelar o ecrã) e a explicar ou a desempatar encravanços grandes sempre que eu via uma página em branco e não sabia localizar a linha em culpa.

## 11. Uso Ético e Responsável
Como a utilização de ferramentas de Inteligência Artificial era um requisito obrigatório para a realização deste trabalho, pedi à IA para me assistir ao longo de todo o processo de desenvolvimento. No entanto, para garantir que compreendia a matéria e não estava apenas a copiar o resultado, verifiquei tudo passo a passo. Antes de avançar para a fase seguinte ou de fazer *commits* definitivos do código gerado, certifiquei-me de que o código compilava bem e, mais importante, de que percebia perfeitamente a função que cada linha desempenhava. Isto permitiu-me rejeitar propostas erradas da IA (como *links* em falta).

---

# Processo de Desenvolvimento

## 12. Controlo de Versões e Histórico de Commits
A plataforma Git foi utilizada ao longo do decorrer do projeto. O fluxo de trabalho adotou progressões unitárias ("commits"), de forma a evitar problemas ou perdas de dados drásticas antes e depois da inserção de nova mecânica crítica à lógica principal do protótipo.

## 13. Dificuldades e Lições Aprendidas
As condicionantes relativas à obtenção de dados públicos através do modelo TheSportsDB emergiram como principal obstáculo estrutural. Frequentemente, a API disponibilizada faculta propriedades omissas ao que constava na documentação de origem. A principal lição resultou no tratamento contínuo das falhas e das propriedades nulas (*safe-calls Kotlin*), precavendo os temidos erros estruturais `NullPointerException`. Aprendeu-se a vantagem do foco pragmático contido à camada dos repositórios abstratos.

## 14. Melhorias Futuras
- Fazer em que o ecrã Favoritos também funcione totalmente sem rede aproveitando dados gravados nas tabelas antes do acesso ser cortado (Modo Offline).
- Substituir as listagens contínuas nativas pela implementação funcional das regras de biblioteca Paging 3, em prol de resiliência ao carregar ficheiros muito extensos, poupando a memória RAM geral.
- Configuração final para suportar temas escuros (`Dark Mode`) baseados no sistema operativo nativo.

---

## 15. Declaração do Uso de IA
Confirma-se o recurso estrito, ético e pontual à estrutura artificial auxiliar:
- **Google Gemini / Assistentes IA**: Obtenção de clarificações limitadas ao Android Studio de forma educacional e assistência declarativa perante relatórios pesados descritivos durante avaliações complexas e processos de *debugging*.

*Confirmo que revi e compreendo o funcionamento de todo o código e conteúdo que estou a entregar neste relatório.*
