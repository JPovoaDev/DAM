# 03 Ecrãs

A aplicação está estruturada nos seguintes ecrãs e secções principais, garantindo uma experiência de utilizador simples e intuitiva.

## Ecrã Principal
O ponto de entrada principal da app, responsável por mostrar uma lista de imagens de cães.
- **Toolbar:** Contém o título da aplicação e as ações/navegação gerais.
- **RecyclerView:** Mostra a lista contínua ou atualizada de imagens de cães num formato de grelha ou lista.
- **Botão de Atualização:** Uma ação dedicada (ex: na toolbar ou um floating action button) para despoletar a recolha de um novo conjunto de imagens.
- **Indicador de Carregamento:** Feedback visual (ex: uma `ProgressBar` ou o loader do `SwipeRefreshLayout`) mostrado enquanto os pedidos de rede estão a decorrer.

## Ecrã de Detalhes
Acedido ao tocar numa imagem no Ecrã Principal, oferece uma visão mais próxima e ações específicas.
- **Imagem Completa:** Mostra a imagem do cão selecionado num formato maior e com alta resolução.
- **URL da Imagem:** Mostra o URL direto da fonte da imagem apresentada.
- **Botão de Favorito:** Permite ao utilizador alternar o estado de favorito (adicionar/remover) para a imagem atual.

## Acesso aos Favoritos
Uma secção ou ecrã dedicado que permite aos utilizadores verem rapidamente as imagens de cães que guardaram.
- **Lista de Favoritos:** Mostra uma lista curada de até no máximo 5 imagens favoritas, gerida automaticamente através de um sistema FIFO (a mais antiga é removida quando uma 6ª é adicionada).
