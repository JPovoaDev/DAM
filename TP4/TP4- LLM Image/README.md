# TP4 - Integração da Gemini API (Processamento de Imagens) 

Este repositório contém o quarto trabalho prático (TP4) da cadeira de Desenvolvimento de Aplicações Móveis (DAM) no ISEL. O objetivo principal deste exercício foi explorar a API do Gemini (Google) e ver como podemos processar imagens diretamente a partir de uma app Android.

## 🎯 O que a app faz

Basicamente, a app mostra de três imagens pré-definidas (bolos e bolachas). O utilizador pode selecionar uma delas e escrever um *prompt* para enviar ao modelo Gemini (neste caso, o modelo que suporta visão). Por exemplo, podemos pedir a receita de um bolo, pedir sugestões de nomes criativos para um produto, ou fazer qualquer outra pergunta baseada no que está na foto.

## Configuração do Projeto e API Key

O setup inicial foi bastante direto:
1. Criei um novo projeto no Android Studio usando o template **"Gemini API Starter"**. Este template já traz as dependências necessárias (SDK do Google AI client for Android).
2. Fui ao [Google AI Studio](https://aistudio.google.com/) gerar uma **API Key** para aceder ao Gemini.
3. Para não deixar a chave exposta no código (boas práticas sempre!), configurei-a no ficheiro `local.properties` (que não vai para o GitHub) adicionando a linha correspondente à API Key. Depois, a chave é lida através do `BuildConfig` ou do `local.properties` diretamente na inicialização do modelo.

## Fluxo Principal

O funcionamento é simples:
1. **Seleção e Prompt:** O utilizador escolhe uma imagem na UI (uma lista em scroll) e escreve o que quer saber sobre ela na *text box*.
2. **Chamada à API:** Quando carrega no botão para enviar (ex: "Bake"), a app junta a imagem (Bitmap) e o texto (*prompt*), cria um pedido e chama o modelo Gemini.
3. **Resposta:** Como é um pedido de rede, a chamada é feita de forma assíncrona. Usamos um ViewModel com um fluxo de dados (como `StateFlow`) para gerir o estado da UI. Enquanto a resposta não chega, pode mostrar-se um estado de carregamento e, assim que a API responde com a string de texto, a UI é atualizada com a resposta gerada.

## Estrutura e Componentes

A arquitetura tenta seguir os princípios base de Android:
- **UI (Jetpack Compose):** Responsável apenas por mostrar as imagens, recolher o input do utilizador e mostrar o output do modelo (ou erros). Reage automaticamente às alterações de estado.
- **ViewModel (`BakingViewModel`):** Onde reside a lógica de apresentação e a gestão do estado. É aqui que lançamos a *coroutine* para fazer o pedido à API sem bloquear a *Main Thread*. O ViewModel segura o `UiState` (Initial, Loading, Success, Error).
- **Integração Gemini:** Instancia-se o `GenerativeModel` (geralmente apontando para o `gemini-1.5-flash` que é rápido e multimodal) passando a API key, e utiliza-se o método `generateContent` que suporta texto e imagens.


