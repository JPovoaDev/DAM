# 05 Navegação

A aplicação utiliza um sistema de encaminhamento simples baseado em Activities.

## Fluxo de Navegação

**`MainActivity`** → **`ImageDetailsActivity`**

- **MainActivity:** Inicia como a activity principal. Funciona como o hub que contém a galeria de imagens e a lista de favoritos.
- **ImageDetailsActivity:** É lançada através de um `Intent` quando se toca numa imagem específica na `MainActivity`. O payload do intent levará os dados necessários (como o URL e o ID da imagem) para renderizar o ecrã de detalhes corretamente.
