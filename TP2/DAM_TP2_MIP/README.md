# Trabalho 2 — Desenvolvimento de Aplicações Móveis 

Course: Desenvolvimento de Aplicações Móveis(DAM)

Student(s): João Pedro Mulano Póvoa

Date: 12/04/2024

Repository URL: https://github.com/JPovoaDev/DAM/edit/main/TP2/DAM_TP2_MIP

## 1. Introduction
Este projeto consiste no desenvolvimento de uma aplicação Android nativa, em Kotlin, que permite visualizar imagens aleatórias de cães utilizando a "Dog API". O trabalho aborda temas fundamentais do desenvolvimento moderno em Android, nomeadamente a arquitetura MVVM (Model-View-ViewModel), o consumo de APIs REST com Retrofit, a gestão de estado com LiveData, e a implementação de mecanismos de cache local e sistemas de favoritos com lógica FIFO (First-In, First-Out).

## 2. System Overview
*   **`com.example.dogviewer`**: Contém as Activities principais (`MainActivity`, `ImageDetailsActivity`, `FavoritesActivity`) que gerem os ecrãs da app.
*   **`com.example.dogviewer.model`**: Define a data class `ImageItem`, que representa o modelo de dados de uma imagem de cão.
*   **`com.example.dogviewer.network`**: Inclui as interfaces e clientes Retrofit (`DogApiService`, `ApiClient`) para comunicação com a Dog API.
*   **`com.example.dogviewer.repository`**: Contém o `DogRepository`, que serve como fonte única de verdade, gerindo a cache e os favoritos.
*   **`com.example.dogviewer.viewmodel`**: Implementa o `DogViewModel` para separar a lógica de negócio da interface e gerir o estado da UI.
*   **`com.example.dogviewer.ui.adapter`**: Contém o `DogAdapter`, responsável por ligar os dados à `RecyclerView` da galeria.

## 3. Architecture and Design
A aplicação segue rigorosamente o padrão **MVVM**. A decisão de design focou-se na separação total de responsabilidades: a UI apenas observa dados e não conhece a origem destes; o **ViewModel** gere o estado e sobrevive a rotações de ecrã; e o **Repository** abstrai a complexidade dos dados (decidindo entre API, Cache ou Favoritos). Para a gestão de memória, optou-se por uma estrutura de lista mutável com lógica de expulsão manual (FIFO), garantindo que a app mantém uma pegada leve sem necessidade de uma base de dados complexa para requisitos simples de cache.

## 4. Implementation

### Exercício 1: Core App & Networking (MVVM + Retrofit)
**O que foi pedido:** Implementar a estrutura base da app para consumir a Dog API e mostrar imagens aleatórias numa lista, usando MVVM.
**Linha de pensamento:** Comecei por definir o modelo de dados e configurar o Retrofit. Criei um ViewModel que dispara o pedido assíncrono e expõe o resultado via LiveData, garantindo que a `MainActivity` apenas tenha de observar e atualizar o adapter.
**Implementação final e observações:** Consegui uma integração fluida onde o utilizador pode atualizar a imagem com um simples clique. A maior dificuldade foi garantir o mapeamento correto do JSON.
```kotlin
// Exemplo de definição do serviço de API com Retrofit
interface DogApiService {
    @GET("api/breeds/image/random")
    suspend fun getRandomDog(): DogApiResponse
}
```

### Exercício 2: Detalhes e Navegação
**O que foi pedido:** Criar um ecrã de detalhes para cada imagem e permitir a navegação entre o ecrã principal e o de detalhe.
**Linha de pensamento:** Utilizei `Intents` para passar o URL e o ID da imagem selecionada. No ecrã de detalhes, implementei a lógica para adicionar ou remover dos favoritos, comunicando com o repositório.
**Implementação final e observações:** A navegação funciona de forma nativa e eficiente. O desafio foi garantir que o estado do botão de favorito estivesse sempre sincronizado com o repositório.
```kotlin
// Passagem de dados via Intent para o ecrã de detalhes
val intent = Intent(context, ImageDetailsActivity::class.java).apply {
    putExtra("IMAGE_URL", dog.url)
    putExtra("IMAGE_ID", dog.id)
}
context.startActivity(intent)
```

### Exercício 3: Cache e Lógica FIFO
**O que foi pedido:** Implementar uma cache de 50 itens e uma lista de 5 favoritos, ambos usando lógica FIFO para manter os limites.
**Linha de pensamento:** No Repositório, usei listas mutáveis. Sempre que um item é adicionado e o limite é atingido, removo o primeiro elemento da lista (o mais antigo), garantindo a rotação constante.
**Implementação final e observações:** A lógica FIFO funciona perfeitamente, impedindo o crescimento descontrolado da memória. Implementei também um fallback offline que serve dados da cache se a rede falhar.
```kotlin
// Lógica FIFO para favoritos (limite de 5)
fun toggleFavorite(item: ImageItem) {
    if (favorites.contains(item)) {
        favorites.remove(item)
    } else {
        if (favorites.size >= 5) favorites.removeAt(0) // Remove o mais antigo
        favorites.add(item)
    }
}
```

## 5. Testing and Validation
Os testes foram realizados manualmente utilizando o emulador do Android Studio e um dispositivo físico. Validei o fluxo de rede carregando múltiplas imagens seguidas, testei a rotação do ecrã para confirmar que a imagem atual não se perdia (validando o ViewModel) e simulei o modo offline (Airplane Mode) para garantir que a app recorria corretamente à cache e aos favoritos guardados sem crashar.

## 6. Usage Instructions
1.  Clonar o repositório para a máquina local.
2.  Abrir o projeto no **Android Studio** (Electric Eel ou superior).
3.  Aguardar a sincronização do **Gradle** e descarregamento de dependências (Retrofit, Glide, etc.).
4.  Ligar um emulador ou dispositivo físico com Android 8.0+.
5.  Clicar no botão **Run 'app'** (ícone verde de reprodução).

## 12. Version Control and Commit History
O desenvolvimento seguiu uma abordagem de commits incrementais e descritivos. Cada fase do plano de implementação (Setup, Modelos, UI, Cache) foi acompanhada por pequenos commits, facilitando o tracking de alterações e a reversão de bugs durante o desenvolvimento da lógica FIFO.

## 13. Difficulties and Lessons Learned
*   **Exercício 1:** Dificuldade em gerir exceções de rede; aprendi a usar blocos try-catch dentro das Coroutines para evitar crashes.
*   **Exercício 2:** Passagem de dados complexos entre ecrãs; solidifiquei o uso de Intent Extras.
*   **Exercício 3:** Sincronização da UI com a cache em memória; percebi a importância de expor o estado do repositório via LiveData/StateFlow.

## 14. Future Improvements
*   **Persistência Real:** Migrar a cache e favoritos para uma base de dados **Room** para manter os dados após fechar a app.
*   **Filtros de Raça:** Adicionar a funcionalidade de pesquisar e filtrar imagens por raças específicas.
*   **Interface Dinâmica:** Implementar animações de transição partilhada entre o ecrã principal e o de detalhe.

## 15. AI Usage Disclosure
Este ficheiro README.md foi revisto e estruturado com o auxílio de inteligência artificial para garantir a conformidade com os requisitos académicos solicitados.
