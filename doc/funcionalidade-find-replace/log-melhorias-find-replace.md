# Log de Melhorias e Correções na Funcionalidade Localizar/Substituir

Este documento serve como um registro cronológico e detalhado de todas as alterações, melhorias e correções implementadas na funcionalidade de Localizar e Substituir do editor de nó longo (`Alt+Enter`) do FreeMind. O objetivo é fornecer um contexto completo para futuras manutenções, depurações ou expansões da funcionalidade.

## 1. Introdução

A funcionalidade de Localizar e Substituir foi implementada para aumentar a produtividade na edição de textos longos. Desde a sua concepção inicial, diversas iterações foram realizadas para refinar a experiência do usuário e corrigir comportamentos inesperados.

## 2. Histórico de Alterações

### Fase 1: Implementação Inicial da Funcionalidade

*   **Objetivo:** Criar a base da funcionalidade de Localizar/Substituir.
*   **Arquivos Afetados:**
    *   `freemind/view/mindmapview/FindAndReplacePanel.java` (criado)
    *   `freemind/view/mindmapview/EditNodeDialog.java` (modificado)
*   **Principais Alterações:**
    *   Criação do `FindAndReplacePanel` como um `JPanel` reutilizável contendo os campos de busca, substituição, botões ("Localizar Próximo", "Substituir", "Substituir Todos", "Fechar") e um `JCheckBox` para "Diferenciar maiúsculas/minúsculas".
    *   Integração do `FindAndReplacePanel` no `EditNodeDialog`, adicionando-o ao layout principal e mantendo-o oculto por padrão.
    *   Mapeamento dos atalhos `Ctrl+F` e `Ctrl+H` para exibir o painel e focar no campo de busca.
    *   Implementação da lógica inicial para "Localizar Próximo" (busca circular, sensível a maiúsculas/minúsculas), "Substituir" (substitui a seleção e busca o próximo) e "Substituir Todos" (substitui todas as ocorrências).
    *   Adição de um método `getFindNextButton()` em `FindAndReplacePanel.java` para permitir que a lógica de "Substituir" acione programaticamente a busca pelo próximo.
*   **Documentação Gerada:**
    *   `doc/funcionalidade-find-replace/relatorio-implementacao-find-replace-long-node.md`
    *   `doc/funcionalidade-find-replace/analise-melhorias-find-replace-vscode.md`

### Fase 2: Correção do Redimensionamento da Janela

*   **Problema:** A janela do editor não se redimensionava automaticamente ao exibir ou ocultar o painel de busca, resultando em elementos da UI cortados ou sobrepostos.
*   **Solução:** Adição de chamadas a `LongNodeDialog.this.pack()` após tornar o `findAndReplacePanel` visível ou invisível.
*   **Arquivos Afetados:** `freemind/view/mindmapview/EditNodeDialog.java`.

### Fase 3: Correção do Bug do `Ctrl+Z` (Primeira Tentativa)

*   **Problema:** Em alguns casos, após interagir com o painel de busca, a primeira tentativa de "Desfazer" (`Ctrl+Z`) apagava todo o conteúdo da área de texto, e a ação correta só ocorria na segunda tentativa.
*   **Análise:** Identificado como um bug de sincronização de eventos e foco comum em Swing.
*   **Solução:** Envolver as chamadas `undoManager.undo()` e `undoManager.redo()` dentro das ações de "Desfazer" e "Refazer" em `javax.swing.SwingUtilities.invokeLater`.
*   **Arquivos Afetados:** `freemind/view/mindmapview/EditNodeDialog.java`.

### Fase 4: Melhoria: `Enter` para "Localizar Próximo"

*   **Objetivo:** Permitir que o usuário pressione `Enter` no campo de busca para acionar "Localizar Próximo".
*   **Solução (Primeira Tentativa):** Adicionar um `ActionListener` ao `searchField` que chamava `findNextButton.doClick()` e forçava o foco de volta para o `searchField`.
    *   **Problema Gerado:** O destaque da seleção na área de texto sumia, pois o foco não estava mais nela.
*   **Solução (Final):**
    *   Reverter a forçagem de foco para o `searchField`.
    *   Adicionar um `ActionListener` ao `searchField` que chama `findNextButton.doClick()`.
    *   Modificar o `KeyListener` existente na `JTextArea` para que, se o painel de busca estiver visível e `Enter` for pressionado, ele chame `findNextButton.doClick()` em vez de inserir uma nova linha ou submeter o diálogo. Isso permite que o `Enter` funcione repetidamente na área de texto para navegar pelos resultados, mantendo o destaque ativo.
*   **Arquivos Afetados:** `freemind/view/mindmapview/FindAndReplacePanel.java` (adicionado `addSearchFieldActionListener`), `freemind/view/mindmapview/EditNodeDialog.java`.

### Fase 5: Melhorias de Usabilidade (Esc e Auto-fechar)

*   **Objetivo:** Refinar o comportamento das teclas e do painel para um fluxo de trabalho mais intuitivo.
*   **Problema:** `Enter` após "Substituir" ainda buscava, `Esc` não fechava o painel.
*   **Solução:**
    *   **`Esc` para Fechar Painel:** Modificação do `KeyListener` da `JTextArea`: se `Esc` for pressionado e o painel de busca estiver visível, ele fecha o painel e redimensiona a janela. Caso contrário, executa a ação original de cancelar o diálogo.
    *   **Auto-fechar após "Substituir Todos":** Adição de `findAndReplacePanel.setVisible(false)` e `LongNodeDialog.this.pack()` ao final do listener de "Substituir Todos".
*   **Arquivos Afetados:** `freemind/view/mindmapview/EditNodeDialog.java`.

### Fase 6: Refatoração: Gerenciamento de Estado Explícito (`isInFindMode`)

*   **Objetivo:** Resolver "engasgos", `Ctrl+Z` persistente em alguns casos, e o fluxo confuso do `Enter` após "Substituir", através de um gerenciamento de estado mais robusto.
*   **Solução:**
    *   Introdução da variável `private boolean isInFindMode = false;` na classe `LongNodeDialog`.
    *   **Ativação:** `isInFindMode` é definido como `true` quando o painel de busca é aberto (`Ctrl+F`/`H`).
    *   **Desativação:** `isInFindMode` é definido como `false` quando o painel é fechado (via `Esc` ou 'X') ou quando uma ação de "Substituir" ou "Substituir Todos" é realizada.
    *   **Uso do Estado:** O `Enter` na área de texto principal agora só aciona "Localizar Próximo" se `isInFindMode` for `true` (além do painel estar visível).
*   **Arquivos Afetados:** `freemind/view/mindmapview/EditNodeDialog.java`.

### Fase 7: Correção Definitiva do Bug do `Ctrl+Z` (Após "Substituir Todos")

*   **Problema:** O bug de `Ctrl+Z` (apagar tudo) ainda persistia especificamente após a execução de "Substituir Todos".
*   **Análise:** A operação `textArea.setText()` (usada em "Substituir Todos"), combinada com outras atualizações de UI e foco no mesmo ciclo de eventos, estava corrompendo o `UndoableEdit` registrado.
*   **Solução:** Envolver *todo* o bloco de atualização da UI e do documento (`textArea.setText()`, `textArea.requestFocusInWindow()`, `findAndReplacePanel.setVisible(false)`, `LongNodeDialog.this.pack()`) no listener de "Substituir Todos" dentro de um `javax.swing.SwingUtilities.invokeLater`. Isso garante que a modificação do documento e as atualizações visuais ocorram em um ciclo de eventos limpo, prevenindo a corrupção do `UndoManager`.
*   **Arquivos Afetados:** `freemind/view/mindmapview/EditNodeDialog.java`.

### Fase 8: Correção Final e Robusta do Bug do "Desfazer Duplo"

*   **Problema:** As tentativas anteriores não resolveram completamente o bug do `Ctrl+Z`. A ação "Substituir" foi corrigida em uma etapa, mas "Substituir Todos" ainda apresentava o comportamento de "desfazer duplo".
*   **Análise:** A causa raiz era a geração de um evento de "desfazer" (`UndoableEdit`) espúrio pelas ações de UI que seguiam a modificação do texto. A complexidade da ação "Substituir Todos", que envolve `setText()` e múltiplas atualizações de UI dentro de um `invokeLater`, exigiu uma solução mais robusta do que as tentativas anteriores.
*   **Solução:**
    *   **Para "Substituir":** A solução de desativar temporariamente o `UndoManager` durante a chamada `doClick()` se provou eficaz e foi mantida.
    *   **Para "Substituir Todos":** Foi implementado o padrão de design `CompoundEdit` do Swing. Toda a sequência de ações (modificação do texto e atualizações da UI) foi envolvida por um listener temporário que agrupa todos os eventos de edição gerados em um único `CompoundEdit`. Este `CompoundEdit` é então adicionado ao `UndoManager` como uma única transação atômica, garantindo que um único `Ctrl+Z` desfaça toda a operação corretamente.
*   **Arquivos Afetados:** `freemind/view/mindmapview/EditNodeDialog.java`.
*   **Documentação Gerada:**
    *   `doc/funcionalidade-find-replace/correcao-final-bug-undo-find-replace.md`

## 3. Conclusão

As melhorias e correções foram implementadas de forma iterativa, buscando aprimorar a usabilidade e a estabilidade da funcionalidade de Localizar/Substituir. O estado atual representa uma versão robusta e alinhada com as expectativas de um editor moderno.