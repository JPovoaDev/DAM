# 02 Funcionalidades

A aplicação inclui as seguintes funcionalidades principais:

1. **Recolha de Imagens de Cães**
   - Obtém imagens aleatórias de cães através da Dog API.
2. **Galeria de Imagens**
   - Apresenta as imagens obtidas de forma eficiente numa `RecyclerView` com scroll.
3. **Atualização de Imagens**
   - Oferece um mecanismo (ex: swipe-to-refresh ou um botão) para carregar novas imagens quando o utilizador quiser.
4. **Indicadores de Carregamento**
   - Mostra feedback visual enquanto as imagens e os dados estão a ser descarregados.
5. **Ecrã de Detalhes da Imagem**
   - Permite aos utilizadores tocar numa imagem específica para a ver em detalhe num ecrã à parte.
6. **Favoritar Imagens**
   - Os utilizadores podem marcar imagens como favoritas.
   - Segue uma estrutura FIFO (First-In, First-Out) com um limite máximo de 5 imagens favoritas.
   - Permite o acesso direto aos cinco itens favoritos a partir de qualquer ecrã através das suas imagens.
7. **Cache de Imagens**
   - Guarda até 50 imagens visualizadas localmente para poupar largura de banda e melhorar a performance.
   - Mantém pelo menos 10 itens à frente e 10 atrás da posição atual durante a navegação.
   - O indicador de carregamento é relativo aos itens que estão a ser carregados para a cache.
8. **Acesso Offline**
   - Permite aos utilizadores ver imagens guardadas na cache e favoritos mesmo sem ligação à internet.
9. **Gestão de Erros**
   - Lida de forma suave com erros de API, falhas de rede e outras exceções, dando feedback claro sem que a app vá abaixo.
