# TP4 - AISimpleCalls | DAM @ ISEL

Boas! Este é o repositório do **AISimpleCalls**, um projeto de consola desenvolvido em Kotlin para o nosso exercício prático de Desenvolvimento de Aplicações Móveis (DAM) no ISEL. 

## O que é este projeto e para que serve?

O **AISimpleCalls** serve como uma "sandbox" para explorarmos a integração de Large Language Models (LLMs) nas nossas aplicações. Essencialmente, é uma app de consola no IntelliJ que nos permite fazer chamadas diretas às APIs da **OpenAI (GPT)** e da **Google (Gemini)**. A ideia é percebermos como comunicar com estes modelos, gerir configurações (como a temperatura e o limite de tokens), e formatar os outputs para algo estruturado (tipo JSON), o que é super útil para depois integrarmos numa app mobile a sério.

---

## Arquitetura e Como as Coisas se Ligam

Antes de pôr a mão na massa, gastei algum tempo a perceber como é que o projeto está estruturado. A arquitetura segue um padrão bem porreiro usando o *Factory Pattern* para abstrair qual a IA que estamos a chamar.

Aqui vai um resumo do papel de cada ficheiro/classe:
- **`Main`**: O ponto de entrada da aplicação. É aqui que decidimos qual o modelo a usar, criamos o assistente e fazemos a chamada final.
- **`AIAssistant`**: Uma interface (ou classe base) que define o contrato. Qualquer assistente de IA tem de implementar o método para gerar o texto (ex: `chat(...)`).
- **`AIAssistantFactory`**: O cérebro que decide qual a implementação instanciar com base na configuração escolhida (OPENAI, GEMINI, etc.).
- **Implementações Específicas**: 
  - **`AIAssistantOpenAI` / `AIAssistantGemini`**: Lidam com as chamadas standard via HTTP/REST para as respetivas APIs, gerindo a formatação dos payloads.
  - **`AIAssistantOpenAIClasses` / `AIAssistantGeminiClasses`**: Versões alternativas (geralmente usando SDKs específicos ou classes focadas na desserialização direta da resposta) para o mesmo efeito.
- **`Utils`**: Funções utilitárias, como carregar e ler o ficheiro de propriedades.
- **`config.properties`**: Onde guardamos as nossas chaves secretas (API keys) e os parâmetros da API. Nunca esquecer de meter isto no `.gitignore`!

**O fluxo de uma chamada é simples:** 
1. O `Main` lê o `config.properties` (através do `Utils`) e pede um assistente à `AIAssistantFactory`.
2. A Factory devolve a instância correta (ex: `AIAssistantGemini`).
3. O `Main` chama o método para comunicar com a IA, passando o nosso *prompt*.
4. O assistente monta o request HTTP com o payload certo, junta a API key nos headers, bate à porta da API (OpenAI/Gemini) e devolve-nos a string com a resposta.

---

## O que foi feito? (Tasks)

### Task 1: Setup e Exploração Inicial
Primeiro passo: pôr isto a correr e perceber como as engrenagens se movem.
- Fui ao `config.properties` e adicionei as minhas API keys da OpenAI e do Google (Gemini).
- Corri o projeto testando os 4 modos disponíveis: `OPENAI`, `GEMINI`, `OPENAI-CLASSES`, e `GEMINI-CLASSES`. 
- Aproveitei para navegar pelo código e mapear mentalmente o fluxo da informação entre o `Main`, a `Factory` e os assistentes. Tudo a bombar e a ligar corretamente aos endpoints!

### Task 2: Configuração Dinâmica (`temperature` e `max_tokens`)
Para não termos de andar a alterar e recompilar código sempre que queremos respostas mais ou menos criativas, passei estas configurações para o ficheiro `.properties`.
- **Como fiz:** Atualizei a leitura de propriedades (na classe `Utils`/`Main`) para conseguir ler os valores `temperature` e `max_tokens` do `config.properties`.
- **Detalhe técnico:** Implementei uma lógica defensiva para que estes parâmetros sejam **opcionais**. Ou seja, se o `config.properties` não tiver estas chaves lá pelo meio, a aplicação não estoira; em vez disso, assume um valor `null` ou omite os campos no JSON do request (deixando a própria API assumir os seus valores *default*).

### Task 3: Testes de Temperatura (Determinismo vs Criatividade)
Para ver a Task 2 em ação, andei a brincar com o parâmetro da temperatura, que controla a "aleatoriedade" das respostas.
- Fiz chamadas usando as temperaturas **0.0**, **0.5**, e **1.0**.
- **O que notei:** 
  - Com `temperature = 0.0` (baixa), a LLM é super determinística, focada e vai direta ao assunto. É o ideal para análise de dados e quando queremos a resposta mais "fiel" e previsível.
  - Com `temperature = 1.0` (alta), a IA solta o seu lado criativo. O vocabulário torna-se mais variado e o texto mais dinâmico. Testei com dois *prompts* diferentes e a diferença no tipo de discurso foi bastante notória (com a temperatura no máximo, a resposta é quase uma aventura literária).

### Task 4: Análise de Sentimento (Structured Output em JSON)
O objetivo aqui foi fazer a LLM devolver informação formatada de modo a que nós (no código Kotlin) possamos dar *parse* facilmente.
- Pedi à LLM para avaliar uma frase numa **escala de 7 pontos** (variando de "Very Negative" a "Very Positive").
- **Como fiz:** Trabalhei bem no *System Prompt* (a instrução base da IA), pedindo explicitamente para não haver conversa de chacha e para ela responder **única e exclusivamente** no formato JSON pretendido.
- O formato JSON que implementei tem os seguintes campos:
  ```json
  {
    "rating": "Very Positive",
    "justification": "A frase revela muito entusiasmo e alegria em aprender Kotlin."
  }
  ```
- **Conclusão:** Este tipo de abordagem com *Structured JSON* é fundamental se quisermos integrar chamadas à IA diretamente numa app (por exemplo, analisar reviews e atualizar uma UI) sem corrermos o risco de o texto deitar o nosso Parser abaixo.
---

