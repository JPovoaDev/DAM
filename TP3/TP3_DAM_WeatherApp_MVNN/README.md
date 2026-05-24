# CoolJetpackWeatherApp — Trabalho Prático 3

**Unidade Curricular:** Desenvolvimento de Aplicações Móveis (DAM)  
**Instituto Superior de Engenharia de Lisboa (ISEL)**  
**Semestre:** 6.º Semestre  
**Restrições de uso de ferramentas:** AC: YES · AI: NO  

---

## Índice

1. [Visão Geral do Projeto](#1-visão-geral-do-projeto)
2. [Arquitetura MVVM](#2-arquitetura-mvvm)
   - [2.1 Camada de Dados](#21-camada-de-dados-data-layer)
   - [2.2 Camada ViewModel](#22-camada-viewmodel)
   - [2.3 Camada de UI — Jetpack Compose](#23-camada-de-ui--jetpack-compose)
3. [Design da Interface](#3-design-da-interface)
4. [Suporte Multilingue](#4-suporte-multilingue)
5. [Location Picker — Integração com Google Maps](#5-location-picker--integração-com-google-maps)
   - [5.1 Descrição da Funcionalidade](#51-descrição-da-funcionalidade)
   - [5.2 Integração do Google Maps SDK](#52-integração-do-google-maps-sdk)
   - [5.3 Configuração da API Key](#53-configuração-da-api-key)
   - [5.4 Implementação Iterativa do Mapa](#54-implementação-iterativa-do-mapa)
6. [Funcionalidades Opcionais](#6-funcionalidades-opcionais)
7. [Conceitos-Chave Aprendidos](#7-conceitos-chave-aprendidos)
8. [Conclusão](#8-conclusão)

---

## 1. Visão Geral do Projeto

Esta aplicação Android — **CoolJetpackWeatherApp** — é uma evolução direta de uma WeatherApp anterior, desenvolvida para uma fase mais avançada do trabalho prático de DAM. O objetivo da refatoração foi aplicar o padrão arquitetural **MVVM (Model–View–ViewModel)** em conjunto com **Jetpack Compose**, substituindo a abordagem imperativa baseada em XML que havia sido utilizada na versão inicial.

A aplicação permite ao utilizador consultar condições meteorológicas em tempo real para qualquer localização geográfica do mundo, introduzindo as coordenadas manualmente ou selecionando-as diretamente num mapa interativo. Os dados são obtidos através da API pública **Open-Meteo**, que devolve informação como temperatura, velocidade e direção do vento, código de condição meteorológica WMO e pressão ao nível do mar.

### Motivação para a Migração para Jetpack Compose

A versão anterior da WeatherApp recorria a layouts XML e à manipulação explícita de `Views` através de `findViewById` e observadores manuais. Essa abordagem, embora funcional, tornava o código mais verboso e dificultava a separação clara entre a lógica de apresentação e a lógica de negócio.

A transição para Jetpack Compose permitiu descrever a interface de forma **declarativa** — o estado da UI é um reflexo direto dos dados expostos pelo ViewModel —, eliminando a necessidade de atualizar widgets manualmente. Em vez de escrever `textView.text = valor`, passa-se a escrever `Text(text = valor)` dentro de um `@Composable`, e o Compose trata da recomposição automática quando o estado muda.

---

## 2. Arquitetura MVVM

O projeto foi estruturado seguindo as três camadas canónicas do padrão MVVM, com separação clara de responsabilidades por pacote:

```
com.example.cooljetpackweatherapp/
├── data/               ← Camada de dados
│   ├── WeatherData.kt
│   ├── WeatherApiClient.kt
│   └── FavoriteLocation.kt
├── ui/                 ← Camada de UI (Compose)
│   ├── WeatherScreen.kt
│   ├── CoordinatesCard.kt
│   ├── WeatherCard.kt
│   ├── WeatherRow.kt
│   ├── FavoriteLocationsRow.kt
│   ├── WeatherUIState.kt
│   └── LocationPickerActivity.kt
├── viewport/           ← Camada ViewModel
│   ├── WeatherViewModel.kt
│   └── LocationPickerViewModel.kt
└── MainActivity.kt
```

---

### 2.1 Camada de Dados (Data Layer)

#### WeatherData.kt — Modelos de Domínio

Todos os modelos de dados que representam a resposta da API Open-Meteo foram anotados com `@Serializable`, da biblioteca `kotlinx.serialization`. Esta anotação permite que o cliente HTTP proceda ao parsing automático do JSON de resposta, sem necessidade de parsers manuais.

As classes de domínio principais são:

- **`WeatherData`** — classe raiz que agrega `latitude`, `longitude`, `timezone`, `current_weather` e `hourly`. O campo `daily` é opcional (`= null`) por não ser sempre solicitado na query.
- **`CurrentWeather`** — contém os campos `temperature`, `windspeed`, `winddirection`, `weathercode`, `time`, `interval` e `is_day` (representado como `Int`, onde `1` corresponde a dia e `0` a noite).
- **`Hourly`** — lista de leituras horárias de `temperature_2m`, `weathercode` e `pressure_msl`, utilizadas para obter, por exemplo, a pressão atmosférica da primeira leitura disponível.
- **`WMO_WeatherCode`** — enum que mapeia os códigos WMO (World Meteorological Organization) para um nome de ficheiro de ícone correspondente. Contempla condições desde céu limpo (código 0) até trovoadas com granizo intenso (código 99), com distinção entre versões diurnas e noturnas para alguns códigos.

#### WeatherApiClient.kt — Cliente HTTP com Ktor

O cliente HTTP foi implementado como um `object` Kotlin (singleton) utilizando a biblioteca **Ktor**, com o plugin `ContentNegotiation` configurado para deserialização automática de JSON:

```kotlin
private val client = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true  // evita erros com campos extra da API
        })
    }
}
```

A opção `ignoreUnknownKeys = true` foi uma decisão consciente: a API Open-Meteo devolve campos adicionais (como `generationtime_ms` e `elevation`) que não são necessários na aplicação, e esta flag previne que a deserialização falhe por causa deles.

A função `getWeather` é `suspend` — é chamada de forma assíncrona a partir do `viewModelScope` no ViewModel — e constrói a URL de pedido dinamicamente com `buildString`, concatenando parâmetros como `latitude`, `longitude`, `current_weather=true` e os campos `hourly` desejados. Em caso de erro de rede ou parsing, o bloco `try/catch` devolve `null`, e o ViewModel não atualiza o estado da UI.

---

### 2.2 Camada ViewModel

#### WeatherViewModel.kt — Gestão de Estado e Eventos

O `WeatherViewModel` estende `ViewModel()` do Jetpack e é responsável por toda a lógica de apresentação e coordenação entre a UI e a camada de dados.

O estado observável da UI é exposto através de um `StateFlow<WeatherUIState>`, que a UI recolhe com `collectAsState()`:

```kotlin
private val _uiState = MutableStateFlow(WeatherUIState())
val uiState: StateFlow<WeatherUIState> = _uiState.asStateFlow()
```

Os campos de latitude e longitude são geridos separadamente com `mutableStateOf`, pois são atualizados de forma síncrona e reativa a partir dos campos de texto da UI:

```kotlin
var latt by mutableStateOf("")
    private set

var long by mutableStateOf("")
    private set
```

#### Eventos Expostos

| Função | Descrição |
|---|---|
| `updateLatitude(String)` | Atualiza o valor da latitude inserida |
| `updateLongitude(String)` | Atualiza o valor da longitude inserida |
| `updateLocationName(String)` | Atualiza o nome da localização a guardar |
| `fetchWeather()` | Valida as coordenadas e lança a chamada à API |
| `addFavoriteLocation()` | Guarda a localização atual na lista de favoritos |
| `selectFavoriteLocation(FavoriteLocation)` | Seleciona um favorito e dispara `fetchWeather()` |

#### Unidirectional Data Flow (UDF)

O fluxo de dados segue estritamente o padrão unidirecional: a UI dispara **eventos** (p.ex. clique no botão), o ViewModel processa e atualiza o **estado**, e a UI recompõe-se automaticamente. A UI nunca modifica o estado diretamente — toda a mutação passa pelos métodos públicos do ViewModel.

Quando `fetchWeather()` é invocado, a validação das coordenadas (`toFloatOrNull() ?: return`) é feita antes de lançar a coroutine, garantindo que chamadas à API só ocorrem com valores válidos. A atualização do estado usa `.update { currentState -> currentState.copy(...) }`, preservando todos os campos não alterados.

#### WeatherUIState.kt — Estado da UI

```kotlin
data class WeatherUIState(
    val latitude: String = "",
    val longitude: String = "",
    val temperature: Float = 0f,
    val windspeed: Float = 0f,
    val winddirection: Int = 0,
    val seaLevelPressure: Float = 0f,
    val weathercode: Int = 0,
    val time: String = "",
    val isDay: Boolean = true
)
```

A classe `WeatherUIState` é uma `data class` imutável com valores por defeito, o que permite criar estados iniciais sem argumentos e copiar estados parcialmente com `.copy()`.

---

### 2.3 Camada de UI — Jetpack Compose

#### Composables Principais e Separação por Ficheiro

A interface foi dividida em ficheiros independentes, cada um responsável por um componente bem definido:

| Ficheiro | Composable(s) | Responsabilidade |
|---|---|---|
| `WeatherScreen.kt` | `WeatherUI`, `PortraitWeatherUI`, `LandscapeWeatherUI` | Ponto de entrada da UI; routing por orientação |
| `CoordinatesCard.kt` | `CoordinatesCard` | Inputs de coordenadas e nome, botão de guardar |
| `WeatherCard.kt` | `WeatherCard` | Exibição dos dados meteorológicos |
| `WeatherRow.kt` | `WeatherRow` | Linha de label + valor reutilizável |
| `FavoriteLocationsRow.kt` | `FavoriteLocationsRow` | Lista horizontal de favoritos |

#### State Hoisting

Todos os composables seguem o princípio de *state hoisting*: o estado é mantido no ViewModel e passado para os composables como parâmetros simples (`String`, `Float`, `Boolean`). As ações de mutação são passadas como lambdas (`(String) -> Unit`, `() -> Unit`), tornando todos os composables **stateless** e, consequentemente, mais testáveis e reutilizáveis.

Por exemplo, `CoordinatesCard` não sabe nem precisa de saber que existe um ViewModel — recebe apenas os valores atuais e os callbacks:

```kotlin
CoordinatesCard(
    latitude = latitude,
    longitude = longitude,
    onLatitudeChange = { weatherViewModel.updateLatitude(it) },
    onLongitudeChange = { weatherViewModel.updateLongitude(it) },
    onSaveLocation = { weatherViewModel.addFavoriteLocation() },
    onLocationPickerClick = onLocationPickerClick
)
```

#### Portrait vs. Landscape

O composable `WeatherUI` deteta a orientação do dispositivo com `LocalConfiguration.current` e delega para `PortraitWeatherUI` ou `LandscapeWeatherUI`. Em modo paisagem, o ícone meteorológico e os dois cards (`CoordinatesCard` e `WeatherCard`) são dispostos lado a lado numa `Row` com `Modifier.weight(1f)`, aproveitando melhor o espaço horizontal disponível. Em modo retrato, a disposição é vertical numa `Column` com scroll.

---

## 3. Design da Interface

A interface organiza-se em torno de três componentes visuais principais, cada um com uma responsabilidade específica:

### CoordinatesCard

Implementado como um `Card` do Material 3 com elevação de 4dp, contém:
- Um `IconButton` com o ícone `Icons.Default.Public` que abre o `LocationPickerActivity`
- Dois `OutlinedTextField` para latitude e longitude
- Um `OutlinedTextField` para o nome da localização
- Um `Button` para guardar a localização nos favoritos

### WeatherCard

Igualmente um `Card` com elevação, exibe as condições meteorológicas atuais através de múltiplas instâncias do componente `WeatherRow`, passando cada par label/valor:

```kotlin
WeatherRow("Sea Level Pressure", "$seaLevelPressure hPa")
WeatherRow("Wind Direction", "$windDirection°")
WeatherRow("Wind Speed", "$windSpeed km/h")
WeatherRow("Temperature", "$temperature °C")
WeatherRow("Time", time)
```

### WeatherRow

Componente atómico que renderiza uma `Row` com `Arrangement.SpaceBetween`, colocando o label à esquerda e o valor à direita. A reutilização deste componente garante consistência visual em toda a `WeatherCard`, evitando duplicação de código de layout.

### Ícone Meteorológico

O ícone exibido é determinado em tempo de execução com base no código WMO devolvido pela API. Para os códigos 0, 1 e 2 (céu limpo, maioritariamente limpo e parcialmente nublado), existe uma variante diurna e outra noturna, determinada pelo campo `is_day` da `CurrentWeather`. O nome do recurso drawable é construído dinamicamente e resolvido com `context.resources.getIdentifier(...)`.

### Adaptação à Orientação

Em modo paisagem (`ORIENTATION_LANDSCAPE`), o layout reorganiza-se para tirar partido da largura extra: o ícone fica à esquerda, e os dois cards ocupam cada um metade da largura disponível. Esta lógica está isolada em `WeatherScreen.kt`, sem tocar nos composables filhos — um exemplo concreto de separação de responsabilidades.

---

## 4. Suporte Multilingue

O suporte a múltiplos idiomas foi implementado através do mecanismo padrão do Android: o ficheiro `res/values/strings.xml` para inglês (idioma padrão) e `res/values-pt/strings.xml` para português.

O sistema de recursos do Android seleciona automaticamente o ficheiro correto com base nas preferências de idioma configuradas no dispositivo, sem necessidade de lógica adicional no código.

A importância de não utilizar *hardcoded strings* vai além da internacionalização: facilita a manutenção do texto da aplicação num único lugar, evita inconsistências de nomenclatura e segue as boas práticas recomendadas pela própria Google para desenvolvimento Android.

---

## 5. Location Picker — Integração com Google Maps

### 5.1 Descrição da Funcionalidade

O `LocationPickerActivity` é uma `ComponentActivity` independente que apresenta um mapa interativo em ecrã completo. O utilizador pode clicar em qualquer ponto do mapa para selecionar coordenadas geográficas; um marcador é colocado nesse ponto e aparece um botão "Confirm Location" na parte inferior do ecrã. Ao confirmar, as coordenadas são devolvidas à `MainActivity` através do mecanismo de `ActivityResult`, sem necessidade de partilhar estado entre atividades ou usar soluções alternativas mais frágeis.

A arquitetura desta funcionalidade replica o padrão MVVM: existe um `LocationPickerViewModel` dedicado que mantém a localização selecionada, e o composable lê e escreve exclusivamente através deste ViewModel.

### 5.2 Integração do Google Maps SDK

Para integrar o Google Maps numa aplicação Android, foi necessário seguir um processo de configuração na plataforma Google Cloud:

1. **Criação de conta no Google Cloud Console** — acedendo a [console.cloud.google.com](https://console.cloud.google.com).
2. **Criação de um projeto** — dentro da consola, criou-se um novo projeto dedicado à aplicação.
3. **Ativação da API** — no menu "APIs & Services" > "Library", pesquisou-se e ativou-se o **Maps SDK for Android**.
4. **Geração da API Key** — em "APIs & Services" > "Credentials", criou-se uma credencial do tipo *API Key*. É possível (e recomendado) restringir a chave a uma aplicação Android específica, usando o *package name* e o SHA-1 da keystore.

No ficheiro `build.gradle.kts`, foram adicionadas as dependências necessárias:

```kotlin
implementation("com.google.android.gms:play-services-maps:19.0.0")
implementation("com.google.maps.android:maps-compose:4.3.3")
```

A biblioteca `maps-compose` é um wrapper oficial em Jetpack Compose sobre o SDK nativo do Google Maps, que expõe o mapa como um composable `GoogleMap(...)`.

### 5.3 Configuração da API Key

#### Inserção no AndroidManifest.xml

A API Key é fornecida ao SDK através de uma `<meta-data>` dentro do bloco `<application>` no ficheiro `AndroidManifest.xml`:

```xml
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="MAPS_API_KEY_AQUI" />
```

O SDK do Google Maps lê esta entrada automaticamente no arranque da aplicação. Sem ela, o mapa não inicializa e aparece um ecrã cinzento com uma mensagem de erro de autenticação.

#### Boas Práticas — Não Expor a API Key

Expor uma API Key diretamente num repositório público é uma prática de segurança gravemente desaconselhada, pois qualquer pessoa com acesso ao código pode usar a chave para fazer chamadas à API à custa do proprietário.

A solução adotada neste projeto foi armazenar a chave no ficheiro `local.properties` (que está listado no `.gitignore` e nunca é enviado para o repositório) e injetá-la como um recurso de string em tempo de compilação, via `build.gradle.kts`:

```kotlin
// build.gradle.kts
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

buildTypes {
    debug {
        resValue("string", "MAPS_API_KEY", localProperties["MAPS_API_KEY"] as String)
    }
}
```

E em `local.properties` (ficheiro local, não versionado):
```
MAPS_API_KEY=AIzaSy...
```

Desta forma, o valor da chave nunca fica exposto no código-fonte versionado, e qualquer colaborador que clone o repositório precisa de fornecer a sua própria chave localmente.

### 5.4 Implementação Iterativa do Mapa

A integração do mapa não foi feita de uma só vez — foi construída de forma incremental, testando cada etapa antes de avançar para a seguinte.

#### Fase 1 — Mapa Básico Estático

O primeiro objetivo foi simplesmente fazer o mapa aparecer no ecrã. Foi criada a `LocationPickerActivity` com um `setContent` mínimo, contendo apenas o composable `GoogleMap`:

```kotlin
GoogleMap(
    modifier = Modifier.fillMaxSize(),
    cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(38.7, -9.1), 5f)
    }
)
```

A posição inicial foi definida manualmente para Lisboa (`38.7, -9.1`) com um zoom de nível 5, que mostrava Portugal e países vizinhos. Nesta fase, o mapa era apenas visual — sem qualquer interação funcional.

Uma dificuldade imediata foi perceber que o `rememberCameraPositionState` não aceita `null` como posição inicial — o SDK exige que a câmara tenha uma posição válida desde o início. Por isso, a posição é inicializada no próprio bloco lambda, em vez de ser atribuída depois.

#### Fase 2 — Captura de Cliques no Mapa

O passo seguinte foi reagir ao clique do utilizador. O composable `GoogleMap` expõe o parâmetro `onMapClick: (LatLng) -> Unit`, que é invocado com as coordenadas do ponto clicado:

```kotlin
GoogleMap(
    ...
    onMapClick = { latLng ->
        locationPickerViewModel.updateSelectedLocation(latLng)
    }
)
```

O `LocationPickerViewModel` mantém a localização selecionada com `mutableStateOf<LatLng?>(null)`. Optou-se por criar um ViewModel separado para esta atividade porque o Android não permite que o mesmo ViewModel seja partilhado entre duas `Activity` distintas — cada atividade tem o seu próprio `ViewModelStore`. Esta foi uma decisão que surgiu de um erro inicial de tentar passar o `WeatherViewModel` para a `LocationPickerActivity`, o que não era possível sem mecanismos externos.

#### Fase 3 — Marcador Visual

Com a localização a ser capturada no ViewModel, o passo seguinte foi mostrar visualmente o ponto selecionado. Dentro do bloco de conteúdo do `GoogleMap`, utilizou-se um `selectedLocation?.let { ... }` para mostrar condicionalmente um `Marker`:

```kotlin
selectedLocation?.let {
    Marker(
        state = MarkerState(position = it),
        title = "Localização selecionada"
    )
}
```

O uso de `?.let` garantiu que o marcador só aparece depois de o utilizador ter clicado — antes disso, `selectedLocation` é `null` e nenhum marcador é renderizado.

#### Fase 4 — Botão de Confirmação e Retorno de Dados

O último passo foi implementar o mecanismo de retorno de dados para a `MainActivity`. A abordagem escolhida foi o padrão nativo do Android: `setResult(RESULT_OK, Intent)` com os valores em extras.

O botão de confirmação só aparece após uma seleção, usando novamente `selectedLocation?.let { ... }`:

```kotlin
selectedLocation?.let {
    Button(
        onClick = {
            val resultIntent = Intent()
            resultIntent.putExtra("latitude", it.latitude.toFloat())
            resultIntent.putExtra("longitude", it.longitude.toFloat())
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        },
        modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
        ...
    ) {
        Text("Confirm Location")
    }
}
```

Na `MainActivity`, o resultado é recebido através de `registerForActivityResult` com o contrato `StartActivityForResult`:

```kotlin
private val locationPickerLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == RESULT_OK) {
        val lat = result.data?.getFloatExtra("latitude", 0f) ?: 0f
        val lon = result.data?.getFloatExtra("longitude", 0f) ?: 0f
        weatherViewModel.updateLatitude(lat.toString())
        weatherViewModel.updateLongitude(lon.toString())
    }
}
```

O `weatherViewModel` foi declarado como `lateinit var` fora do `setContent` precisamente para ser acessível dentro do callback do launcher, que vive no contexto da `Activity` e não do Compose.

---

## 6. Funcionalidades Opcionais

### Localizações Favoritas

Foi implementado um sistema de localizações favoritas em memória (sem persistência em base de dados). O utilizador pode atribuir um nome a uma localização e guardá-la com o botão "Save Location". A lista de favoritos é mantida no `WeatherViewModel` como uma `List<FavoriteLocation>` gerida com `mutableStateOf`:

```kotlin
var favoriteLocations by mutableStateOf(listOf<FavoriteLocation>())
    private set
```

A adição de um favorito cria um novo objeto `FavoriteLocation` com nome, latitude e longitude, e atualiza a lista por substituição (`favoriteLocations = favoriteLocations + newLocation`), garantindo imutabilidade e recomposição correta.

### Lista Horizontal de Favoritos (FavoriteLocationsRow)

Os favoritos são apresentados numa `LazyRow`, que permite scroll horizontal eficiente mesmo com muitas entradas. Cada entrada é um `Button` com o nome da localização; ao clicar, o ViewModel chama `selectFavoriteLocation(location)`, que atualiza as coordenadas e dispara imediatamente `fetchWeather()`.

```kotlin
LazyRow(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    contentPadding = PaddingValues(horizontal = 16.dp)
) {
    items(favoriteLocations) { location ->
        Button(onClick = { onLocationClick(location) }) {
            Text(text = location.name)
        }
    }
}
```

O uso de `LazyRow` em vez de `Row` com `forEach` é uma decisão de performance: `LazyRow` só compõe os itens visíveis no ecrã, o que é relevante quando a lista cresce.

---

## 7. Conceitos-Chave Aprendidos

### MVVM (Model–View–ViewModel)

A aplicação desta arquitetura tornou evidente a sua principal vantagem: a **separação de responsabilidades**. O ViewModel não importa nenhuma classe do pacote `ui`, e os composables não têm lógica de negócio. Esta separação facilita a manutenção, torna o código mais previsível e, em teoria, permite testar o ViewModel de forma isolada sem emulador.

### Jetpack Compose

A transição do paradigma imperativo (XML + `View`) para o declarativo (Compose) exigiu uma mudança de mentalidade: em vez de pensar "quando isto acontece, atualiza aquele widget", passa-se a pensar "dado este estado, a UI deve ter este aspeto". O resultado é um código mais conciso e com menos estados implícitos.

### Gestão de Estado

Aprendeu-se a distinção prática entre `StateFlow` (para estado complexo partilhado, com suporte a `collectAsState`) e `mutableStateOf` (para campos simples com reatividade imediata no Compose). Também ficou clara a importância de manter o estado privado no ViewModel e expor apenas setters controlados.

### Integração com API REST

A integração com a Open-Meteo através do Ktor demonstrou como um cliente HTTP assíncrono com deserialização automática pode simplificar significativamente o código de rede. A configuração `ignoreUnknownKeys = true` e o tratamento de erros com `try/catch` são exemplos de decisões práticas necessárias ao trabalhar com APIs reais.

### Google Maps SDK

A integração do Google Maps SDK introduziu conceitos específicos do ecossistema Google: criação e restrição de API Keys, gestão da posição da câmara (`CameraPosition`, `rememberCameraPositionState`), deteção de cliques no mapa e renderização condicional de marcadores. O uso da biblioteca `maps-compose` demonstrou também como SDKs nativos podem ser envoltos em composables para se integrarem naturalmente num projeto Compose.

---

## 8. Conclusão

Este trabalho prático representou um avanço significativo em relação à WeatherApp anterior, tanto em termos de organização do código como de maturidade arquitetural. A adoção do padrão MVVM forçou uma reflexão mais cuidada sobre onde cada tipo de lógica deve residir, enquanto o Jetpack Compose simplificou a escrita de interfaces adaptativas — como a gestão automática de portrait/landscape — que na versão XML exigiria mais código de configuração.

A integração com o Google Maps foi o componente mais desafiante, sobretudo no que respeita à gestão do ciclo de vida entre duas `Activity` distintas e ao mecanismo de retorno de dados com `ActivityResult`. A solução final — dois ViewModels independentes e comunicação via `Intent` extra — é simples, mas resultou de um processo iterativo que envolveu tentativas, erros e uma compreensão progressiva do ciclo de vida Android.

As aprendizagens mais relevantes podem resumir-se em três pontos:

1. **Arquitetura importa desde o início** — tentar refatorar para MVVM a posteriori é possível, mas mais trabalhoso do que começar com a estrutura correta.
2. **O Compose muda o modo de pensar a UI** — a UI como função do estado é um modelo mental mais robusto do que a manipulação direta de widgets.
3. **APIs externas têm nuances** — tanto a Open-Meteo como o Google Maps SDK exigiram configurações específicas (flags de deserialização, API keys, posições de câmara não-nulas) que só se descobrem ao trabalhar com elas concretamente.

---

*Documento elaborado como parte da entrega do Trabalho Prático 3 da unidade curricular de Desenvolvimento de Aplicações Móveis — ISEL, 2025/2026.*
