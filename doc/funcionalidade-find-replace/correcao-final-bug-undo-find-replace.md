# Relatório de Correção Definitiva: Bug do "Desfazer Duplo"

Este documento detalha a análise e a solução final para o bug persistente do "Desfazer Duplo" (`Ctrl+Z`) que ocorria nas funcionalidades "Substituir" e "Substituir Todos" do editor de nó longo.

## 1. Análise do Problema

O sintoma do bug era idêntico para as duas funcionalidades: após uma substituição, a primeira pressão de `Ctrl+Z` causava um comportamento inesperado (geralmente, apagar todo o conteúdo do texto), e apenas a segunda pressão restaurava o texto ao seu estado original.

A causa raiz era que **duas** edições desfazíveis (`UndoableEdit`) estavam sendo adicionadas à pilha do `UndoManager` para uma única ação do usuário:
1.  A edição legítima, proveniente da modificação do texto (`replaceSelection` ou `setText`).
2.  Uma edição espúria e muitas vezes corrompida, gerada por ações de UI (como mudança de foco, seleção de texto ou redimensionamento da janela) que ocorriam imediatamente após a modificação do texto.

O `UndoManager`, ao receber duas edições, exigia duas chamadas de `undo()` para reverter a operação completa.

## 2. A Solução Implementada

A solução final exigiu duas estratégias diferentes, uma para cada tipo de substituição, devido às particularidades de cada uma.

### 2.1. Correção para "Substituir" (Simples)

-   **Contexto:** A ação "Substituir" realiza a troca do texto selecionado (`textArea.replaceSelection()`) e, em seguida, chama `findAndReplacePanel.getFindNextButton().doClick()` para encontrar a próxima ocorrência.
-   **Problema:** A chamada `doClick()` aciona eventos de UI (seleção e foco) que geravam a segunda edição espúria.
-   **Solução Aplicada:** A solução foi "esconder" a ação de UI do `UndoManager`. O listener foi desativado temporariamente antes da chamada `doClick()` e reativado imediatamente depois.

    ```java
    // ...
    textArea.replaceSelection(replaceTerm);
    // ...

    // Desativa o listener, executa a ação de UI e o reativa.
    textArea.getDocument().removeUndoableEditListener(undoManager);
    findAndReplacePanel.getFindNextButton().doClick();
    textArea.getDocument().addUndoableEditListener(undoManager);
    ```
    Isso preveniu que a edição espúria fosse criada, deixando apenas a edição limpa do `replaceSelection` na pilha de desfazer.

### 2.2. Correção para "Substituir Todos"

-   **Contexto:** A ação "Substituir Todos" é mais complexa. Ela usa `textArea.setText()` para substituir todo o conteúdo do documento, seguida por múltiplas ações de UI (`requestFocusInWindow`, `setVisible`, `pack`), tudo dentro de um `SwingUtilities.invokeLater`.
-   **Problema:** Esta combinação se mostrou mais resistente. A simples desativação do listener não foi suficiente, pois a própria interação entre `setText` e as outras chamadas dentro do mesmo ciclo de eventos do Swing corrompia a pilha de "desfazer".
-   **Solução Aplicada (Padrão `CompoundEdit`):** A solução definitiva foi usar o padrão de design `CompoundEdit`, a maneira canônica do Swing para agrupar múltiplas edições em uma única transação atômica.

    ```java
    // Dentro do invokeLater...
    final javax.swing.undo.CompoundEdit compoundEdit = new javax.swing.undo.CompoundEdit();
    // Cria um listener temporário que captura QUALQUER edição e a adiciona ao "compoundEdit".
    final javax.swing.event.UndoableEditListener tempListener = new javax.swing.event.UndoableEditListener() {
        public void undoableEditHappened(javax.swing.event.UndoableEditEvent e) {
            compoundEdit.addEdit(e.getEdit());
        }
    };

    // Troca o listener principal pelo temporário.
    textArea.getDocument().removeUndoableEditListener(undoManager);
    textArea.getDocument().addUndoableEditListener(tempListener);

    // Executa TODAS as ações (modificação de texto e UI).
    textArea.setText(textToSet);
    textArea.requestFocusInWindow();
    // ...

    // Restaura o listener principal.
    textArea.getDocument().removeUndoableEditListener(tempListener);
    compoundEdit.end(); // Finaliza a "gravação".
    undoManager.addEdit(compoundEdit); // Adiciona o CompoundEdit como UMA ÚNICA edição.
    textArea.getDocument().addUndoableEditListener(undoManager);
    ```
    Em vez de tentar evitar as edições espúrias, esta abordagem as "abraça". Ela captura todas as edições geradas pela sequência de ações e as agrupa em um único `CompoundEdit`. Para o `UndoManager`, toda a operação complexa se torna um único item na pilha, que é desfeito corretamente com um único `Ctrl+Z`.

## 3. Conclusão

A aplicação de duas estratégias distintas, alinhadas com a complexidade de cada funcionalidade, permitiu a correção completa e robusta do bug do "Desfazer Duplo", restaurando o comportamento esperado pelo usuário e aumentando a estabilidade do editor.
