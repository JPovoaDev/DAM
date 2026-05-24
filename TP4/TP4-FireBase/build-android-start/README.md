# Trabalhos Práticos de DAM — TP4: Firebase 

 Este repositório contém a resolução do TP4 da cadeira de Desenvolvimento de Aplicações Móveis (DAM) no ISEL. O foco deste trabalho é a integração e utilização dos serviços do Google Firebase em aplicações Android.

## Friendly Chat (Codelab) 

A primeira parte consistiu em seguir o Codelab oficial do Firebase, desenvolvendo o **Friendly Chat**, uma aplicação de mensagens em tempo real.

### O que a app faz e o objetivo
O objetivo principal foi aprender a integrar o ecossistema Firebase numa app Android já existente (começámos com o projeto `build-android-start`). A aplicação final permite:
- Autenticação de utilizadores (Login/Registo com Email e Password).
- Envio e receção de mensagens de texto em tempo real num chat global.
- Envio de imagens (upload para o Cloud Storage).
- Sincronização automática e instantânea de dados entre todos os clientes.

### Configurações no Firebase
Para que tudo funcionasse, foi preciso ir à consola do Firebase e criar um novo projeto. Depois, ativei e configurei os seguintes serviços:
1. **Firebase Authentication**: Ativei o método de *Email/Password*.
2. **Firebase Realtime Database**: Criei a base de dados para guardar as mensagens do chat. Foi necessário ajustar as *Security Rules* para permitir leitura/escrita apenas a utilizadores autenticados.
3. **Cloud Storage**: Configurei o bucket de armazenamento para guardar as imagens enviadas no chat, também com regras restritas a utilizadores com login feito.

Para ligar a app ao projeto, descarreguei o ficheiro `google-services.json` e coloquei-o na diretoria `app/` do Android Studio.

### O que foi feito no código
A nível de código Kotlin/Android, integrei as bibliotecas do Firebase no `build.gradle`. Depois, implementei as lógicas de:
- **Authentication**: Usámos o FirebaseUI para gerir o fluxo de login de forma mais rápida. 
- **Realtime Database**: Adicionei os *Listeners* na Activity do chat (`ChildEventListener`) que reagem instantaneamente sempre que um nó de mensagens é alterado na cloud. A base de dados notifica a app e a interface (RecyclerView) é atualizada.
- **Cloud Storage**: Implementei o upload de imagens selecionadas na galeria, guardando a referência da imagem na Realtime Database para os outros utilizadores a conseguirem ver.

**Bug visual no Login:** 
Durante o desenvolvimento, deparei-me com um bug na UI em que a `ActionBar` ficava a sobrepor o campo de email no ecrã de login, impedindo a escrita. A forma que arranjei de contornar isto foi forçar a troca do tema diretamente no `onStart()` da `SignInActivity.kt` para um tema `NoActionBar` antes de o FirebaseUI fazer o render do layout. 



## A Arquitetura: Como o Firebase se encaixa no Android 

A integração destes componentes altera um bocado a forma como pensamos a arquitetura da app. Em vez de termos de gerir um backend tradicional (fazer *requests* HTTP, gerir websockets, lidar com concorrência e falhas de rede), o SDK do Firebase trata do trabalho pesado:

- **Authentication**: O SDK mantém o estado da sessão localmente. Não precisamos de gerir tokens manualmente; basta perguntar ao `FirebaseAuth.getInstance().currentUser` se há alguém logado ou não.
- **Realtime Database / Firestore**: Encaixam-se perfeitamente no padrão *Observer*. Nas nossas *Activities* ou *ViewModels*, subscrevemos os nós de dados. Se a rede for abaixo, o SDK guarda os dados em cache local e faz a sincronização silenciosa mal a net volte. Na prática, a UI só tem de reagir às alterações de dados na cloud.
- **Cloud Storage**: O fluxo normal é: a Activity pede uma foto da galeria -> fazemos upload para o Storage -> recebemos o URL de download -> guardamos esse URL na base de dados (Realtime DB ou Firestore) para associar a foto a um utilizador ou mensagem.

---

## Realtime Database vs Firestore: Diferenças na prática 

Embora ambos sejam bases de dados NoSQL da Google focadas em sincronização em tempo real, têm diferenças enormes na forma como estruturamos e consultamos os dados:

1. **Estrutura de Dados:**
   - **Realtime DB**: É basicamente uma árvore JSON gigante. É super rápida para sincronizar dados simples, mas se não tivermos cuidado com a hierarquia, fazer uma leitura de um nó pai pode obrigar a descarregar muita informação desnecessária dos nós filhos.
   - **Firestore**: Baseado em *Coleções* e *Documentos* (muito semelhante ao MongoDB). Podes ir buscar um documento sem arrastar as sub-coleções que estão lá dentro. Escala muito melhor e é ideal para organizar dados complexos.

2. **Pesquisas e Queries:**
   - **Realtime DB**: As queries são extremamente limitadas. Só podes ordenar ou filtrar por uma propriedade de cada vez.
   - **Firestore**: Permite queries compostas e complexas. Os índices são gerados automaticamente, o que torna as pesquisas bastante mais versáteis.

3. **Quando usar qual?** 
 **Realtime DB** serviu para o *Friendly Chat*, porque os dados são simples, lineares (mensagens sequenciais) e a latência precisa de ser mínima. Se estivéssemos a fazer uma app que exigisse relacionamentos ou pesquisas complexas, o **Firestore** seria a escolha óbvia.
