# 04 Modelo de Dados

O modelo de dados está estruturado com base nos princípios de Kotlin e alinha-se com os dados esperados da Dog API.

## Entidades Principais

### ImageItem
Representa uma única imagem de um cão obtida da rede ou da cache.
- `id: String` - Um identificador único para a imagem (pode ser extraído do URL se a API não fornecer um ID separado).
- `url: String` - O URL absoluto do recurso da imagem do cão.
- `title: String` - O nome descritivo do cão (combinação de raça e sub-raça).
- `breed: String` - O nome principal da raça.
- `subBreed: String?` - O nome opcional da sub-raça (null se não for aplicável).

*(Em Kotlin, isto mapeia diretamente para uma `data class` com estas propriedades.)*

## Estruturas de Memória e Armazenamento

### FavoriteQueue
Gere as imagens favoritas do utilizador usando uma política rigorosa de expulsão First-In, First-Out (FIFO).
- `maxSize: 5` - A fila mantém um máximo de 5 imagens de cada vez.
- **Comportamento:** Quando um 6º item é inserido, o item mais antigo (o primeiro a ser adicionado) é automaticamente expulso para manter o limite de tamanho.

### Cache
Mantém uma coleção local de imagens visualizadas recentemente para melhorar a velocidade de navegação e proporcionar uma melhor experiência offline.
- `maxSize: 50` - A cache local retém até 50 instâncias de `ImageItem`.
- **Comportamento:** Armazena itens de forma histórica. Especificamente, mantém itens tanto antes como depois da posição atual de scroll ou visualização, garantindo uma navegação para trás suave e minimizando chamadas de rede redundantes.
