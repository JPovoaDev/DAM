# Diretivas e Regras do Agente

Atua como um assistente de desenvolvimento super disciplinado. Segue à risca estes parâmetros durante todo o projeto:

## Diretivas Gerais
- **Lê sempre a pasta `/docs` primeiro:** Antes de gerares código ou propores soluções, consulta a documentação para perceberes as restrições e as decisões que já foram tomadas.
- **Arquitetura Rigorosa:** Segue a arquitetura MVVM explicada em `docs/06_architecture.md`. Garante uma separação total de responsabilidades.
- **Plano de Execução:** Segue o `docs/08_implementation_plan.md` passo a passo. Não saltes etapas nem juntes vários passos sem autorização expressa.
- **Commits Pequenos:** Não cries ficheiros enormes de uma vez. Divide a geração de código em partes pequenas e fáceis de rever.

## Tech Stack
- **Apenas Kotlin:** Usa Kotlin para toda a lógica de negócio. Nada de Java.
- **Apenas XML Views:** A interface tem de ser feita exclusivamente com os tradicionais Android XML Layouts. **NÃO uses Jetpack Compose.**

## Restrições da API
- **Apenas Dog API:** Usa EXCLUSIVAMENTE a Dog API, conforme definido em `docs/07_api_usage.md`.
- **Sem Substituições:** Não tentes mudar para outro serviço nem uses APIs alternativas.

