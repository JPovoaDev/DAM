# Trabalho Ex5 — System Info App

Course: Desenvolvimento de Aplicações Móveis (DAM)

Student: João Pedro Mulano Póvoa

Date: 07/03/2026

Repository URL: https://github.com/JPovoaDev/DAM/tree/main/TP1/DAM_TP1_SystemInfoApp

## 1. Introduction
Este projeto, chamado **System Info App**, foi desenvolvido como um exercício para a cadeira. O objetivo principal era criar uma aplicação Android que conseguisse ir buscar as informações do sistema e do dispositivo atual, mostrando tudo de forma clara num `TextView` multilinha (MultiLine TextView).

## 2. System Overview
A aplicação tem apenas um ecrã que funciona como um painel com os detalhes do telemóvel (ou emulador). Ela vai buscar automaticamente os dados de hardware e software — como o fabricante, a marca, o modelo e a versão do Android — e mostra tudo numa lista em texto. Tal como foi pedido, onde o resultado final é um ecrã simples e direto, onde as propriedades do sistema aparecem seguidas num `TextView` multilinha.

## 3. Architecture and Design
A arquitetura da app é bastante simples e usa apenas os componentes básicos do Android. A interface foi construída com um layout XML que contém o `TextView` configurado para mostrar várias linhas de texto. Toda a lógica de código está na `MainActivity`, que acede diretamente à API do sistema Android para ir buscar os dados de que precisamos. Isto faz com que a app seja leve e vá direto ao assunto, sem precisar de padrões de arquitetura complexos.

## 4. Implementation
A aplicação foi feita com as ferramentas normais do Android. Para cumprir o requisito de ir buscar a informação do sistema, usei a classe `android.os.Build`. Esta classe faz parte da API oficial do Android e permite-nos aceder de forma nativa a várias informações do dispositivo.

Para reunir os dados, acedi diretamente a várias propriedades do objeto `Build`:
- `Build.MANUFACTURER`: O fabricante do hardware.
- `Build.BRAND`: A marca em que o telemóvel se insere.
- `Build.MODEL`: O nome do modelo.
- `Build.DEVICE`: O nome de design industrial do equipamento.
- `Build.VERSION.SDK_INT`: A versão do SDK (o nível da API).
- `Build.VERSION.RELEASE`: A versão do Android visível para o utilizador.
- `Build.VERSION.INCREMENTAL`: O valor interno da build.
- `Build.DISPLAY`: O ID da build para ser mostrado.

Estas propriedades são todas padrão no sistema Android e estão acessíveis através da API oficial. Podem ser consultadas na [documentação do Android Developer sobre o `android.os.Build`](https://developer.android.com/reference/android/os/Build).

Aqui está um pequeno pedaço de código a mostrar como estes dados foram juntos:

```kotlin
val infoText = """
    Manufacturer: ${Build.MANUFACTURER}
    Brand: ${Build.BRAND}
    Device: ${Build.DEVICE}
    Model: ${Build.MODEL}
    Android Version: ${Build.VERSION.RELEASE}
    SDK Version: ${Build.VERSION.SDK_INT}
    Incremental Build: ${Build.VERSION.INCREMENTAL}
    Display Version: ${Build.DISPLAY}
""".trimIndent()

val textView = findViewById<TextView>(R.id.infoTextView)
textView.text = infoText
```

Apenas foram usadas as bibliotecas padrão do Android, como `android.os.Build` e `android.widget.TextView`. Não foi preciso usar e instalar nenhuma biblioteca externa para fazer o exercício.

## 5. Testing and Validation
Testei a aplicação a correr num emulador Android através do Android Studio. Todas as propriedades da build apareceram direito no ecrã, e batiam certo com a configuração do emulador que eu estava a usar. O `TextView` multilinha portou-se bem e ajustou-se para caber toda a informação.

## 6. Usage Instructions
1. Fazer clone do repositório para o PC local.
2. Abrir o projeto no Android Studio.
3. Fazer sync do projeto com o Gradle.
4. Ligar um dispositivo físico ou abrir um emulador Android.
5. Clicar no botão "Run" (ou Shift + F10) para compilar e abrir a app.
6. A app vai arrancar logo no ecrã com as informações do telemóvel.

# Development Process

## 12. Version Control and Commit History
Usei o Git para ir controlando as versões do meu trabalho. Os commits mostram o progresso do exercício: desde fazer primeiro a parte visual com o layout XML e o `TextView`, até ir para o Kotlin programar a parte de ir buscar as informações pela API `android.os.Build`.

## 13. Difficulties and Lessons Learned
No início, quando olhei para o exercício, não sabia como ir buscar as informações do dispositivo por código em Android. A solução foi ir à documentação oficial do Android. Depois de alguma pesquisa, percebi que o objeto `Build` nos dá logo estas propriedades todas à mão. 

Assim que entendi como o `Build` funcionava, o resto da aplicação foi bem pacífico de se fazer. Só tive de construir uma string e formata-la com aquelas variáveis e passá-la para o `TextView`. 
## 14. Future Improvements
Se tivesse de continuar a melhorar a app, faria algumas coisas:
- Pôr o texto mais elegante (por exemplo, usar código para meter coisas como "Manufacturer:" a negrito destacando da informação em si).
- Substituir o texto longo por uma lista a sério (`RecyclerView`), dividindo o que é hardware e software em categorias diferentes.
- Juntar um botão de "Copiar" para a pessoa poder tirar as notas rapidamente das especificações do seu telemóvel e colar noutro sítio.

## 15. AI Usage Disclosure
Usei inteligência artificial para me ajudar a rever o texto deste README e a estruturar as ideias. Ajudou-me a ter a certeza que as secções estavam todas aqui e fez com que a formatação em Markdown (especialmente a parte do código) ficasse mais apresentável.
