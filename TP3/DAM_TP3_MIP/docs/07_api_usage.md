# 07 Utilização da API

Este documento descreve a fonte de dados de rede para a aplicação.

> [!IMPORTANT]
> **Cumprimento da Regra da API:** 
> A Dog API descrita abaixo é a **ÚNICA** API autorizada para utilização neste projeto. Nenhumas outras APIs externas, relacionadas com cães ou não, podem ser integradas na aplicação.

## Especificação da Dog API

### Detalhes do Endpoint
- **Base URL / Endpoint:** `https://dog.ceo/api/breeds/image/random`
- **Método HTTP:** `GET`
- **Objetivo:** Obtém um único recurso de imagem de cão aleatório do diretório público.

### Formato da Resposta
A API devolve um objeto JSON após um pedido bem-sucedido.

```json
{
  "message": "https://images.dog.ceo/breeds/hound-english/n02089973_1.jpg",
  "status": "success"
}
```

### Mapeamento de Dados
Os campos da resposta JSON são mapeados para a classe de dados interna `ImageItem` da seguinte forma:

| Resposta JSON da API | Modelo da Aplicação (`ImageItem`) | Descrição |
| :--- | :--- | :--- |
| `message` | `url` | A string do URL direto da imagem usada para obter o bitmap da imagem. |
| *(Derivado/Aleatório)* | `id` | Um ID para identificar unicamente a imagem (muitas vezes construído a partir do caminho/nome de ficheiro único do URL). |
| `status` | *(Não mapeado)* | Usado apenas internamente pela camada de rede para verificar um payload de `"success"` bem-sucedido. |
