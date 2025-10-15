# Análise de Melhorias para a Funcionalidade Localizar/Substituir

Este documento analisa a implementação atual da funcionalidade de Localizar e Substituir e propõe um plano de melhorias para aumentar sua usabilidade e alinhá-la com as expectativas de editores modernos, como o VSCode.

## 1. Contexto

A funcionalidade básica foi implementada com sucesso. No entanto, para tornar a experiência mais fluida e poderosa, podemos adicionar vários refinamentos inspirados em ferramentas de desenvolvimento padrão da indústria.

## 2. Análise Comparativa e Propostas de Melhoria

A seguir, uma lista de funcionalidades comuns em editores como o VSCode e como poderíamos implementá-las.

### Melhoria 1: Ações via Tecla "Enter"

-   **Comportamento Atual**: Pressionar `Enter` nos campos de busca ou substituição não tem efeito.
-   **Comportamento Desejado (VSCode)**: Pressionar `Enter` no campo de busca aciona a ação "Localizar Próximo". Pressionar `Shift+Enter` aciona "Localizar Anterior".
-   **Plano de Ação**:
    1.  Adicionar um `ActionListener` ao `searchField` em `FindAndReplacePanel.java`.
    2.  No `EditNodeDialog.java`, implementar a lógica deste listener para que ele dispare um clique programático no botão "Localizar Próximo": `findNextButton.doClick()`.
    3.  Para o `Shift+Enter`, seria necessário adicionar um `KeyListener` para detectar o modificador `Shift`, o que é mais complexo. Uma primeira etapa mais simples seria focar apenas no `Enter`.

### Melhoria 2: Busca "Localizar Anterior"

-   **Comportamento Atual**: Apenas a busca para frente ("Localizar Próximo") está implementada.
-   **Comportamento Desejado (VSCode)**: Ter um botão e um atalho para "Localizar Anterior".
-   **Plano de Ação**:
    1.  Adicionar um botão "Localizar Anterior" ao `FindAndReplacePanel`.
    2.  Adicionar um método `addFindPreviousListener` à sua API.
    3.  No `EditNodeDialog.java`, implementar o listener correspondente. A lógica de busca usaria `text.lastIndexOf(searchTerm, fromIndex)`, onde `fromIndex` seria a posição *antes* do início da seleção atual.

### Melhoria 3: Contador de Ocorrências

-   **Comportamento Atual**: O usuário não sabe quantas ocorrências do termo existem no texto.
-   **Comportamento Desejado (VSCode)**: Exibir um contador (ex: "3 de 15") que mostra a posição da ocorrência atual em relação ao total.
-   **Plano de Ação**:
    1.  Adicionar um `JLabel` (ex: `matchCountLabel`) ao `FindAndReplacePanel`.
    2.  Criar um novo método em `EditNodeDialog` que, ao iniciar uma busca, primeiro itera por todo o texto para contar todas as ocorrências. 
    3.  Armazenar as posições de todas as ocorrências em uma lista (`List<Integer>`).
    4.  Atualizar o `matchCountLabel` com o total (ex: `... de 15`).
    5.  A lógica de "Localizar Próximo/Anterior" passaria a navegar por essa lista de posições em vez de usar `indexOf`/`lastIndexOf` repetidamente, e atualizaria o contador da posição atual (ex: "3 de ...").

### Melhoria 4: Busca em Tempo Real (Real-time Highlighting)

-   **Comportamento Atual**: As ocorrências só são destacadas uma a uma, ao clicar no botão.
-   **Comportamento Desejado (VSCode)**: Todas as ocorrências do termo de busca são destacadas no texto assim que o usuário digita.
-   **Plano de Ação**:
    1.  Esta é uma melhoria complexa em Swing. Exigiria o uso de um `Highlighter` customizado no `JTextArea`.
    2.  Um `DocumentListener` seria adicionado ao `searchField`. A cada alteração no texto de busca, ele removeria os destaques antigos e adicionaria novos destaques para todas as ocorrências encontradas.
    3.  Esta abordagem pode ter implicações de performance em textos muito grandes.

## 3. Próximos Passos Recomendados

Sugere-se implementar as melhorias de forma incremental, na seguinte ordem de prioridade:

1.  **Implementar a Melhoria 1 (Ações via Tecla "Enter")**: É a mais simples e de maior impacto na usabilidade para quem prefere o teclado.
2.  **Implementar a Melhoria 2 (Busca "Localizar Anterior")**: Adiciona uma funcionalidade de navegação essencial que está faltando.
3.  **Implementar a Melhoria 3 (Contador de Ocorrências)**: Fornece um contexto valioso ao usuário durante a busca.
4.  **Avaliar a Melhoria 4 (Busca em Tempo Real)**: Considerar a complexidade e o benefício antes de iniciar a implementação.
