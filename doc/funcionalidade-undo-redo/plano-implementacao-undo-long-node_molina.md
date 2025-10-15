# Plano de Implementação: Funcionalidade Desfazer/Refazer (Undo/Redo) no Editor de Nó Longo

Este documento detalha a análise e o plano de ação para adicionar a funcionalidade de desfazer (`Ctrl + Z`) e refazer (`Ctrl + Y`) ao editor de texto da janela "Edit Long Node".

## 1. Objetivo

O objetivo é melhorar a experiência de usuário no editor de nós longos, implementando o comportamento padrão de desfazer/refazer que é esperado em qualquer editor de texto. Isso permitirá que os usuários corrijam erros de digitação e edições de forma mais eficiente.

## 2. Análise Técnica

-   **Componente Alvo:** A janela de edição, acionada pelo atalho `Alt + Enter`, é implementada pela classe interna `LongNodeDialog` dentro do arquivo `freemind/view/mindmapview/EditNodeDialog.java`. O campo de texto principal é uma instância de `javax.swing.JTextArea`.

-   **Mecanismo de Implementação:** A biblioteca gráfica Java Swing, na qual o FreeMind é baseado, fornece um framework robusto para a funcionalidade de desfazer/refazer. A implementação padrão envolve os seguintes elementos:
    1.  **`javax.swing.undo.UndoManager`**: Uma classe que gerencia uma lista de edições desfazíveis (`UndoableEdit`).
    2.  **`DocumentListener`**: O `UndoManager` precisa ser registrado como um ouvinte no `Document` do `JTextArea`. O documento notifica o `UndoManager` sobre cada alteração (inserção, remoção de texto), que é então adicionada à pilha de "desfazer".
    3.  **`InputMap` e `ActionMap`**: Estes são os mecanismos padrão do Swing para mapear atalhos de teclado a ações específicas. A `InputMap` associa um `KeyStroke` (como `Ctrl + Z`) a um nome de ação (uma string, como "Undo"), e a `ActionMap` associa esse nome de ação a um objeto `Action` que efetivamente executa a lógica (chamar `undoManager.undo()`).

## 3. Plano de Ação Detalhado

A implementação será inteiramente contida no construtor da classe interna `LongNodeDialog` dentro de `freemind/view/mindmapview/EditNodeDialog.java`.

1.  **Instanciar o `UndoManager`**: Logo após a criação do `JTextArea`, uma instância de `UndoManager` será criada.
    ```java
    final javax.swing.undo.UndoManager undoManager = new javax.swing.undo.UndoManager();
    ```

2.  **Registrar o Listener**: O `UndoManager` será adicionado como um `UndoableEditListener` ao documento do `JTextArea`.
    ```java
    textArea.getDocument().addUndoableEditListener(undoManager);
    ```

3.  **Criar e Mapear a Ação de Desfazer (Undo)**:
    -   Uma nova `AbstractAction` será criada para encapsular a chamada a `undoManager.undo()`.
    -   Esta ação será adicionada à `ActionMap` do `JTextArea` com a chave "Undo".
    -   O atalho `Ctrl + Z` será mapeado para a chave "Undo" na `InputMap` do `JTextArea`.
    ```java
    // Adicionar Ação de Desfazer
    textArea.getActionMap().put("Undo", new javax.swing.AbstractAction("Undo") {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (undoManager.canUndo()) {
                undoManager.undo();
            }
        }
    });
    // Mapear Ctrl+Z para a Ação de Desfazer
    textArea.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_DOWN_MASK), "Undo");
    ```

4.  **Criar e Mapear a Ação de Refazer (Redo)**:
    -   O mesmo processo será repetido para a funcionalidade de refazer.
    -   Uma `AbstractAction` chamará `undoManager.redo()`.
    -   O atalho `Ctrl + Y` será mapeado para a ação "Redo".
    ```java
    // Adicionar Ação de Refazer
    textArea.getActionMap().put("Redo", new javax.swing.AbstractAction("Redo") {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            if (undoManager.canRedo()) {
                undoManager.redo();
            }
        }
    });
    // Mapear Ctrl+Y para a Ação de Refazer
    textArea.getInputMap().put(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_DOWN_MASK), "Redo");
    ```

**Observação:** Para evitar a necessidade de adicionar novas declarações `import` ao arquivo (o que pode complicar a operação de substituição de texto), serão utilizados os nomes de classe totalmente qualificados (ex: `javax.swing.undo.UndoManager`).

## 4. Conclusão

Esta abordagem implementa a funcionalidade de desfazer/refazer de forma robusta e encapsulada, utilizando as APIs padrão do Swing. A alteração é de baixo risco e não afeta nenhuma outra parte do sistema, entregando o comportamento esperado pelo usuário de forma limpa e eficiente.
