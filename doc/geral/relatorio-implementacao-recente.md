# Relatório de Implementações Recentes

Este documento sumariza as funcionalidades e correções implementadas recentemente na aplicação FreeMind, focando nas melhorias da funcionalidade de Localizar/Substituir e na adição de uma nova opção de exportação para a área de transferência.

## 1. Melhorias na Funcionalidade Localizar/Substituir

### 1.1. Implementação de "Localizar Anterior" e Contador de Ocorrências

**Objetivo:** Aprimorar a usabilidade da funcionalidade de Localizar/Substituir no editor de nó longo (`Alt+Enter`) com navegação para trás e feedback visual do número de ocorrências.

**Alterações Realizadas:**
-   **`FindAndReplacePanel.java`:**
    -   Adicionado um novo botão "Anterior" (`findPreviousButton`).
    -   Adicionado um `JLabel` (`matchCountLabel`) para exibir o contador de ocorrências (ex: "1 de 15").
    -   Expostos novos métodos públicos (`addFindPreviousListener`, `updateMatchCount`, `getSearchField`, `addMatchCaseListener`) para interação com `EditNodeDialog`.
-   **`EditNodeDialog.java`:**
    -   Refatoração completa da lógica de busca para pré-calcular todas as posições das correspondências no texto.
    -   Adicionadas variáveis de membro (`matchPositions`, `currentMatchIndex`) para gerenciar o estado da busca.
    -   Criado o método `updateSearch()` que é acionado por alterações no campo de busca ou na opção de "Diferenciar maiúsculas/minúsculas". Este método popula `matchPositions` e atualiza o contador total.
    -   Os listeners dos botões "Próximo" e "Anterior" foram reescritos para navegar na lista `matchPositions`, incluindo o comportamento de "wrap-around" (voltar ao início/fim da lista).

### 1.2. Correção Definitiva do Bug de "Desfazer Duplo"

**Objetivo:** Resolver o problema persistente onde `Ctrl+Z` exigia duas pressões para desfazer corretamente as ações de "Substituir" e "Substituir Todos".

**Análise:** O bug era causado por eventos `UndoableEdit` espúrios gerados por ações de UI (atualizações de contador, seleção de texto, foco) que ocorriam imediatamente após a modificação do texto.

**Solução Aplicada:**
-   **Para "Substituir Todos":** Foi implementado o padrão `CompoundEdit`. Um listener temporário foi usado para agrupar todos os eventos de edição (modificação de texto e UI) em um único `CompoundEdit`, que é então adicionado ao `UndoManager` como uma transação atômica.
-   **Para "Substituir" (Simples):** O mesmo padrão `CompoundEdit` foi aplicado. A lógica de `replaceSelection()` e as ações subsequentes (`updateSearch()`, `findNextButton.doClick()`) são agora encapsuladas em um `CompoundEdit`, garantindo um único evento de "desfazer".

### 1.3. Ajustes de Interface e Layout

**Objetivo:** Melhorar a clareza e estabilidade do layout do painel de Localizar/Substituir.

**Ajustes Realizados:**
-   O texto do `JCheckBox` "Diferenciar maiúsculas/minúsculas" foi encurtado para "Aa", com um "tooltip" contendo o texto completo.
-   O texto exibido no contador quando não há resultados foi alterado de "Nenhum resultado" para "0 de 0", para maior concisão e consistência com o formato "X de Y".

## 2. Nova Funcionalidade: Exportar Ramo para Área de Transferência

**Objetivo:** Adicionar uma nova ação que copie o conteúdo de um ramo de nó selecionado, no formato `.mi`, diretamente para a área de transferência do sistema, sem a necessidade de salvar em um arquivo.

**Alterações Realizadas:**
-   **`MindMapController.java`:**
    -   Criada uma nova classe interna `ExportBranchAsMIToClipboardAction`. Esta ação reutiliza a lógica de obtenção de XML do ramo e transformação XSLT da funcionalidade "Export Branch as MI".
    -   Em vez de usar um `JFileChooser` para salvar em arquivo, a ação agora utiliza a API `java.awt.datatransfer.Clipboard` para colocar o texto `.mi` resultante na área de transferência.
    -   Adicionado um novo campo `public Action exportBranchAsMIToClipboard` para expor a ação.
-   **`Resources_en.properties`:** Adicionada a chave `export_branch_as_mi_to_clipboard.text = Branch as MI to Clipboard`.
-   **`mindmap_menus.xml`:** Adicionado um novo item de menu para a ação "Branch as MI to Clipboard" no menu de contexto dos nós, logo abaixo da opção "Branch as MI".
-   **Atalho de Teclado:** Atribuído o atalho `Alt+Shift+C` à nova funcionalidade, com as devidas entradas em `freemind.properties` e `mindmap_menus.xml`.

### 2.1. Correção de Bug no Atalho de Teclado

**Objetivo:** Garantir que o atalho `Alt+Shift+C` copie apenas o ramo selecionado, e não o mapa inteiro.

**Análise:** O problema ocorria porque, ao ser acionada por um atalho global, a ação `exportBranchAsMIToClipboard` não recebia o contexto correto do nó selecionado, e `mMindMapController.getSelected()` estava retornando o nó raiz.

**Solução Aplicada (Tentativa Anterior):** A lógica do método `actionPerformed` em `ExportBranchAsMIToClipboardAction` foi modificada para:
-   Verificar explicitamente se **exatamente um** nó está selecionado usando `mMindMapController.getSelecteds().size() == 1`.
-   Obter o nó alvo da ação através de `(MindMapNode) mMindMapController.getSelecteds().get(0)`.
-   Se a condição de seleção não for atendida (nenhum ou múltiplos nós selecionados), a ação é abortada, com um feedback sonoro (`Toolkit.getDefaultToolkit().beep()`) e uma mensagem de aviso (`JOptionPane`), garantindo que a funcionalidade só opere no contexto pretendido.

**Status Atual:** **Esta correção não funcionou como esperado.** Após a implementação, o atalho `Alt+Shift+C` não copia mais o mapa inteiro, mas também **não copia nada para a área de transferência**, introduzindo uma nova regressão. A funcionalidade via menu de contexto continua funcionando corretamente. Será necessária uma nova investigação para resolver este problema.
