# Notes Pro - TP4 DAM @ ISEL
 Este projeto foi desenvolvido no âmbito da disciplina de Desenvolvimento de Aplicações Móveis (DAM) do ISEL.

## O que é isto?
Começámos com o projeto base `NotesProXMLViews3`. Basicamente, uma app de gestão de notas com autenticação (Email/Password + verificação de email) e persistência de dados no Firebase Cloud Firestore. A partir daí, arregaçámos as mangas e continuámos o desenvolvimento em Kotlin para adicionar umas features que a tornam muito mais *premium*.

### O que foi adicionado (As novas features)
Para além do básico, implementei três features:
1. **Imagem por nota (opcional):** Podes anexar uma imagem a cada nota.
2. **Personalização do fundo:** Dá para escolher uma cor de fundo específica para a nota, ou até mesmo uma imagem!
3. **Sistema de Tags:** Podes adicionar tags/categorias às notas e usar filtros para encontrar o que queres facilmente.

---

## Como é que a magia acontece (Implementação Técnica)

Aqui fica um resumo de como as coisas estão ligadas desde que clicas num botão até ao Firebase.

### 1. Imagem opcional por nota
- **Fluxo:** UI (Botão para escolher imagem na galeria) -> Activity/Fragment recebe o URI da imagem -> ViewModel prepara o upload -> Firebase Cloud Storage recebe o ficheiro e devolve o URL de download -> Esse URL é guardado no documento da nota no Cloud Firestore.
- **Porquê Storage e não Base64?** Guardar imagens grandes em Base64 no Firestore é pedir para esgotar a quota rapidamente e tornar as queries super lentas. O Storage foi feito exatamente para isto.

### 2. Troca de fundo da nota
- **Fluxo:** UI (Color picker ou seletor de imagem de fundo) -> Atualização do UI state na ViewModel -> O valor da cor (em hex) ou o URL da imagem de fundo é guardado num campo específico do documento da nota no Firestore (ex: `backgroundColor` ou `backgroundImageUrl`).
- Quando a nota é carregada na RecyclerView ou na vista de detalhe, lemos este campo e aplicamos ao background (usando Glide/Coil se for imagem, ou `setBackgroundColor` se for cor).

### 3. Sistema de Tags
- **Fluxo:** UI (Input text com botão de "adicionar tag" ou chips) -> A lista de tags é gerida na ViewModel -> Quando a nota é guardada, as tags vão como um array de strings (`List<String>`) para o Firestore.
- **Filtragem:** Na UI de listagem, temos uma barra com os chips das tags disponíveis. Quando clicas numa, a ViewModel faz uma query ao Firestore usando `whereArrayContains("tags", tagSelecionada)`. O Firestore devolve só as notas relevantes e atualizamos a RecyclerView.

---

## Arquitetura & Classes (O esqueleto da app)

Seguimos a arquitetura recomendada pela Google (MVVM), para não termos *Spaghetti Code*:

- **Activities/Fragments:** Tratam puramente da UI. Mostram os dados (observando LiveData/StateFlow) e capturam os cliques do utilizador.
- **ViewModels:** Contêm a lógica de apresentação. Guardam o estado das notas, tags selecionadas, e comunicam com o Repositório. Assim, se a malta rodar o ecrã, não perdemos os dados a meio da edição.
- **Repository:** A ponte entre a app e a cloud. É aqui que estão as chamadas diretas à API do Firebase (Firestore e Storage). Se um dia quisermos mudar para outra base de dados (esperemos que não), só temos de mexer aqui.
- **Data Models (Classes de dados):** Ex: `Note(id, title, content, imageUrl, backgroundColor, tags, timestamp, userId)`. Ajudam a mapear diretamente os documentos do Firestore para objetos Kotlin.

---

## Decisões Técnicas (Os "Porquês")

- **Tags como Array vs Sub-coleção:** Decidi guardar as tags como um array dentro do documento da nota. Como o limite de tamanho do documento é 1MB e as tags são strings pequenas, é a forma mais eficiente. Facilita muito na hora de fazer queries com o `whereArrayContains`.
- **Upload de Imagens:** Optei por fazer o upload para o Firebase Storage de forma assíncrona com Coroutines. Só depois de ter a garantia que a imagem subiu com sucesso e tenho o `downloadUrl`, é que faço o "commit" do documento no Firestore. Evita termos URLs partidos na BD.

---

## The GOAT App 🐐
Por que é que esta app é a *Greatest Of All Time*? 
Porque junta o útil ao agradável. Não é só um bloco de notas. Com os fundos personalizados, imagens anexadas e as tags, passou de uma app que se faz num tutorial no YouTube, para uma app que eu efetivamente usaria no dia a dia para organizar o meu semestre no ISEL. É robusta, reativa e está ligada à cloud de forma eficiente. 

