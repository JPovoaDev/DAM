# Assignment 1 — Hello World and Simple To-Do App
Course: Desenvolvimento de Aplicações Móveis
Student(s): João Pedro Mulano Povoa
Date: 07/03/2026
Repository URL: https://github.com/JPovoaDev/DAM_TP1_Ex4_Ex7

## 1. Introduction
Este projeto foi desenvolvido como parte de um exercício prático de introdução ao sistema operativo Android. O principal objetivo foi familiarizar-me com a estrutura de um projeto Android no Android Studio, explorando o sistema de layouts, views (como `TextView`, `ImageView` e `CalendarView`), ficheiros de recursos (`strings.xml`, `colors.xml`) e a criação interativa de elementos de UI tanto em **modo portrait** (vertical) quanto em **modo landscape** (horizontal). O projeto está dividido em duas partes distintas: a construção e evolução de um ecrã "Hello World" com múltiplos atributos estilizados e um simples gestor de tarefas ("To-Do List") exclusivo para a vista em formato horizontal.

## 2. System Overview
A aplicação Android consiste numa única Activity (`MainActivity`) que exibe duas interfaces distintas, dependendo da orientação do dispositivo:
- **Portrait (Vertical)**: Uma demonstração de uso de componentes estáticos, que inclui textos coloridos e redimensionados, uma imagem figurativa e um `CalendarView`. 
- **Landscape (Horizontal)**: Uma aplicação prática de gestão de tarefas temporárias ("To-Do List"), em que do lado esquerdo o utilizador consegue inserir texto e adicionar à lista, e do lado direito é apresentado, em tempo real, um painel atualizável com as tarefas e uma contagem de tarefas.

## 3. Architecture and Design
A arquitetura baseia-se num sistema de Activities único, no qual o ecrã se molda através de *resource qualifiers* fornecidos pela plataforma Android:
- `res/layout/activity_main.xml`: Interface portrait.
- `res/layout-land/activity_main.xml`: Interface landscape.

Ambos os layouts recorrem ao `ConstraintLayout` para um posicionamento flexível que se adapta a diversos tamanhos de ecrã, suportado por elementos como `LinearLayout` (nomeadamente para acomodar dinamicamente novas views de texto) e controlos de estilo e tipografia unificados através de `strings.xml` e ficheiros font (`alfa_slab_one.xml`).

## 4. Implementation

### Parte 1: Hello World App v1 e v2 (Portrait / Landscape)
**O que era pedido:**
Alterar textos diretamente no `strings.xml` por forma a acomodar suporte nativo a internacionalizações, modificações no Layout XML (texto, fundo, tamanho, fonte e alinhamentos), inserir ImageView e CalendarView, alteração do nome da app, e definir layouts separados para landscape e portrait.

**Raciocínio de Implementação:**
A primeira medida foi não escrever hard-coded strings, mas invés disso referenciar as chaves em resourcing strings. Criei chaves como `hello_string` configurada para "Hello Android World!" e `my_first_app`. Em caso de mudar de idioma futuramente, bastará criar um `strings.xml` alternativo, o que é uma prática recomendada (Best Practice). A app foi nomeada `Hello World V1` e depois evoluiu para `Hello World V2`. Em relação às *views*, criei e dimensionei TextViews definindo constrangimentos relativos aos aspetos essenciais via `ConstraintLayout`. Definiram-se cores próprias provenientes de `colors.xml` ou código hexadecimal, bem como tamanho (`textSize`) e estilo de fonte (`textStyle`). 

**Exemplo de Código (Strings & UI Constraints):**
```xml
<string name="hello_string">Hello Android World!</string>
```
No Layout (Portrait):
```xml
<TextView
    android:id="@+id/textView3"
    android:text="@string/hello_string"
    android:textColor="#673AB7"
    android:textSize="24sp"
    android:textStyle="bold"
    ... />
```

### Parte 2: To-Do List Simples (Modo Landscape)
**O que era pedido:**
Implementar uma To-Do List que apenas fosse visualmente interativa e apresentada através da orientação *landscape*. A tela deve ser mapeada em duas colunas: uma para inserir o log de uma nova tarefa com um botão, e outra correspondendo ao lado direito onde seriam fixadas as tarefas adicionadas à lista e um rasto da sua contagem acumulada.

**Raciocínio de Implementação:**
No XML de modo Paisagem (`layout-land/activity_main.xml`), separei a visão em duas abas (esquerda e direita) criadas artificialmente com um delimitador gráfico (View `<View android:id="@+id/divider" ... />`). Na lateral esquerda coloquei inputs textuais (`TextInputEditText`), bem como a frase "Your remaining tasks". No lado oposto dediquei o espaço na totalidade para o `taskContainer`, um `LinearLayout` que permite o empilhamento vertical das sub-views.

Para ativar interação da UI programaticamente, fui para a classe associada (`MainActivity.kt`). Defini referências às Views e uma lista Kotlin `mutableListOf<String>()` para armazenar as tarefas. Captei a informação de que na arquitetura Android intersetar a ação de clique do botão (`FloatingActionButton`) dá-se recorrendo ao `setOnClickListener`.
Para afixar programaticamente instâncias `TextView` na interface direita do ecrã instanciei e parametrizei propriedades em tempo de execução via Kotlin. 

**Exemplo de Código (Criação Dinâmica e OnClickListener):**
```kotlin
val taskContainer = findViewById<LinearLayout>(R.id.taskContainer)
val button = findViewById<FloatingActionButton>(R.id.floatingActionButton)
var listOfTask = mutableListOf<String>()

button.setOnClickListener {
    listOfTask.add(taskToDo.text.toString())
    val taskView = TextView(this)
    taskView.text = taskToDo.text.toString()
    taskView.textSize = 20f
    
    // Adicionar a fonte customizada "alfa_slab_one" via código
    val typeface = ResourcesCompat.getFont(this, R.font.alfa_slab_one)
    taskView.setTypeface(typeface, Typeface.BOLD)
    taskView.setTextColor(resources.getColor(android.R.color.white, theme))

    // Empilhar visualmente a view criada
    taskContainer.addView(taskView)
    
    // Refletir tamanho atual da queue
    remainigTasksText.text = "Your remainig tasks: ${listOfTask.size}"
    taskToDo.text?.clear()
}
```

## 5. Testing and Validation
Os testes foram efetuados via emulador (AVD) do Android Studio. 
Validez comportamental verificada:
- A interface retrata os ficheiros sem colisão.
- Rotação altera instintivamente entre Portrait e Landscape.
- Inserir dados textuais ("tarefas") em modo de orientação Paisagem e o imediato premir de botão renderiza a tarefa na secção correta e atualiza adequadamente o valor total do tracking (`${listOfTask.size}`).
- A limpeza sistemática do InputEditText no finalístico de cada task afigura-se normal após invocação do `clear()`.

## 6. Usage Instructions
1. Executar a aplicação num dispositivo físico ou Emulador Android.
2. Com o dispositivo na vertical (Portrait), ver a interface "Hello World" com o Calendário.
3. Rodar o dispositivo ou o emulador para horizontal/paisagem (Landscape).
4. Do lado esquerdo, inserir o nome de uma tarefa na caixa do tipo "Write Your Tasks!".
5. Clica no ícone flutuante `+` (FloatingActionButton).
6. Observar o surgimento da tarefa no lado direito do ecrã e um aumento do contador em "Your remaining tasks".

# Development Process

## 12. Version Control and Commit History
Durante o desenvolvimento, o projeto foi acompanhado de um versionamento de código, como se refletem os saltos incrementais: `Hello World V1` e `Hello World V2`. 

## 13. Difficulties and Lessons Learned
- **Encontrar e Referenciar os Elementos Corretos:** Inicialmente, existiu um nível de complexidade moderada na compreensão e adaptação a layouts independentes (landscape vs. portrait). Adicionalmente, referenciar *Views* em `MainActivity` originadas no XML requer atenção especial à ID utilizada (`R.id.xxxx`). Foi assim que compreendi o auxílio direto do `findViewById()`.
- **Criação Dinâmica de Views:** Instanciar elementos de uma classe sub-herdada (`TextView(this)`) com os seus respetivos atributos (Size, Padding, Colors) de forma meramente programática não obedece totalmente a nomes idênticos implementados no modo XML. Como tal, a documentação oficial da Google para Android Developers revelou-se ser de enorme conveniência.
- **SetOnClickListener vs. Métodos Customizados:** Compreendi a forma como o `setOnClickListener` gere o controlo asíncrono da UI, atuando assim que a *view* regista a pressão táctil. Descobrir que existia o `taskToDo.text?.clear()` permitiu-me assegurar robustez anulando a propensão de bugs ou má User Experience de apagar conteúdo retroativamente. 
- **Fontes programáticas (`ResourcesCompat`):** Compreender como associar uma "FontFamily" que estava no pacote `res/font` a um `TextView` gerado por código levou-me a estudar implementações compatíveis com as versões mais recentes. Na API do Android, métodos como o `ResourcesCompat.getFont(this, R.font.alfa_slab_one)` foram integrados após análise da documentação, a fim de não ter bugs provenientes de versões antigas do Android (`Typeface.create()`).

## 14. Future Improvements
- Implementar persistência de dados localmente (através de `SharedPreferences` ou de uma Base de Dados em SQLite/Room) para que as tarefas não se rescrevam aos valores a zeros em eventualidades de se encerrar e abrir a aplicação.
- Incluir na lista da UI os recursos de se suprimir, sublinhar ou retificar de forma independente cada tarefa (`RecyclerView` ao invés de um simples linear parent container), permitindo maior flexibilidade em listas que ultrapassem os limites da resolução do ecrã.


