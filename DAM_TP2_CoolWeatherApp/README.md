# CoolWeatherApp

**Course:** Desenvolvimento de Aplicações Móveis (DAM)  
**Student(s):** João Pedro Mulano Póvoa
**Date:** 11 de Abril de 2026  
**Repository URL:** [https://github.com/JPovoaDev/DAM](https://github.com/JPovoaDev/DAM)

---

## 1. Introduction
A **CoolWeatherApp** é uma aplicação Android desenvolvida em Kotlin que permite aos utilizadores consultar dados meteorológicos detalhados em tempo real. A aplicação consome a API pública **Open-Meteo** para obter informações como temperatura, velocidade e direção do vento, pressão atmosférica e estado do tempo. 

As principais funcionalidades incluem:
*   **Localização GPS:** Obtenção automática das coordenadas do dispositivo.
*   **Interface Dinâmico:** Temas automáticos para Dia/Noite e suporte completo para as orientações Portrait (vertical) e Landscape (horizontal).
*   **Dados em Tempo Real:** Atualização manual e ao arranque da aplicação.

## 2. System Overview
O projeto está estruturado em dois ficheiros principais de código que separam a representação dos dados da lógica de negócio:
*   **`MainActivity.kt`**: Contém toda a lógica da aplicação, incluindo a interação com o utilizador, gestão de permissões, chamadas à API, parsing de recursos XML e atualização do interface gráfico (UI).
*   **`WeatherData.kt`**: Define as *data classes* necessárias para o mapeamento direto do JSON devolvido pela API Open-Meteo, utilizando a biblioteca Gson. Inclui também a classe de suporte `WeatherCodeInfo`.

## 3. Architecture and Design
A arquitetura da aplicação foca-se na fluidez do interface e na gestão eficiente de recursos:
*   **Thread-based API Calls:** Para evitar o bloqueio da *Main Thread* (UI), todas as operações de rede são executadas numa `Thread` separada, com o retorno ao interface garantido pelo método `runOnUiThread`.
*   **TypedArray para Recursos XML:** Em vez de *hardcoding*, os códigos WMO são lidos de um ficheiro XML (`weather_codes.xml`) utilizando `getStringArray` para o índice principal e `obtainTypedArray` para ler os sub-arrays com metadados.
*   **Temas Dinâmicos:** A aplicação seleciona o tema (`Theme_Day`, `Theme_Night`, etc.) em tempo de execução com base na hora solar (nascer/pôr do sol) e na orientação do ecrã, garantindo uma experiência visual consistente.

## 4. Implementation

### Localização GPS
A aplicação utiliza o `LocationManager` do sistema para obter a última localização conhecida através do `GPS_PROVIDER` ou `NETWORK_PROVIDER`. 
*   **Permissões:** É implementado o fluxo de permissões em *runtime* (Android 6.0+), solicitando `ACCESS_FINE_LOCATION`.
*   **Fallback:** Caso o utilizador negue a permissão ou o GPS esteja desativado, a aplicação utiliza Lisboa (38.7º, -9.1º) como localização padrão para garantir que o utilizador vê sempre dados meteorológicos.

### Chamada à API
As chamadas são feitas para o *endpoint* da Open-Meteo via pedidos HTTP `GET`.
*   **Lógica:** O pedido é iniciado numa `Thread` dedicada mal a localização é determinada.
*   **Parsing:** A resposta JSON é convertida em objetos Kotlin através da biblioteca **Gson**, facilitando o acesso a campos profundos como a temperatura horária ou as horas do nascer e pôr do sol.

### Códigos WMO
O mapeamento dos códigos meteorológicos (WMO codes) para descrições e ícones é feito de forma extensível:
*   **Processamento:** A função `getWeatherCodeMap()` lê o ficheiro `weather_codes.xml`.
*   **Ícones:** Dependendo do campo `hasDayNight` no XML, a aplicação seleciona automaticamente a versão "day" ou "night" do ícone correspondente ao estado do tempo (ex: `clear_day` vs `clear_night`).

### Temas e fundos
A personalização visual ocorre em dois momentos:
1.  **Temas:** O método `setTheme()` é invocado no `onCreate()`, antes do `super.onCreate()`, permitindo alterar as cores de sistema e da ActionBar.
2.  **Fundos:** Durante a atualização do UI (`updateUI`), o método `setBackgroundResource()` é usado para trocar a imagem de fundo do contentor principal, distinguindo entre dia/noite e portrait/landscape (ex: `sunny_bg_land`).

## 5. Testing and Validation
Foram realizados testes manuais para validar os seguintes cenários:
*   **Permissões:** Verificação do comportamento com permissão concedida vs negada.
*   **Conetividade:** Validação de mensagens de erro amigáveis (Toast) quando não há acesso à internet.
*   **Mudança de Estado:** Teste de rotação de ecrã para garantir que o tema e o fundo se adaptam corretamente sem perder os dados atuais.
*   **Interface:** Verificação da atualização dos campos de texto e ícones ao premir o botão "ATUALIZAR".

## 6. Usage Instructions
1.  Clonar o repositório.
2.  Abrir o projeto no **Android Studio** (Koala ou superior).
3.  Sincronizar o projeto com o Gradle.
4.  Executar num dispositivo físico ou emulador com API 24+.
5.  Aceitar a permissão de localização para obter dados locais ou inserir coordenadas manualmente.

## 12. Version Control and Commit History
O desenvolvimento seguiu as melhores práticas de controlo de versão usando Git:
*   Commits regulares divididos por funcionalidades (Core logic, GPS integration, UI Polishing).
*   Uso de mensagens de commit claras e descritivas.
*   Ramificação para desenvolvimento de novas funcionalidades antes da integração na `main`.

## 13. Difficulties and Lessons Learned
*   **Gestão de Recursos:** A utilização de `obtainTypedArray` revelou-se um desafio inicial, mas permitiu criar um sistema de mapeamento de códigos meteorológicos muito mais limpo e fácil de manter do que múltiplos `when` statements.
*   **Ciclo de Vida:** Compreender que o tema deve ser definido antes do `super.onCreate()` foi essencial para evitar inconsistências visuais na mudança de orientação.
*   **Assincronia:** Reforço da importância de gerir as Threads corretamente para evitar crashes ao tentar tocar no UI a partir de uma thread secundária.

## 14. Future Improvements
*   **Pesquisa por Nome:** Implementação de Geocodificação para permitir procurar cidades por nome em vez de apenas coordenadas.
*   **Favoritos:** Adicionar uma base de dados local (Room) para guardar localizações favoritas.
*   **Gráficos:** Representação visual da evolução da temperatura ao longo do dia usando uma biblioteca de gráficos.

## 15. AI Usage Disclosure
Neste projeto, foram utilizadas ferramentas de IA (como o Gemini/ChatGPT) para auxílio na geração da documentação técnica e README, garantindo a correção gramatical e organização estrutural.
