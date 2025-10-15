# Plano de Ação e Contexto: Funcionalidade Localizar/Substituir no Editor de Nó Longo

Este documento serve como um registro completo e ponto de retomada para a implementação da funcionalidade de Localizar (`Ctrl+F`) e Substituir (`Ctrl+H`) no editor de nós longos do FreeMind.

## 1. Contexto e Objetivo

### 1.1. Histórico Recente
Esta tarefa segue uma série de melhorias já implementadas no editor de nós longos (`Alt+Enter`), que incluem:
1.  A implementação da funcionalidade de Desfazer/Refazer (`Ctrl+Z` / `Ctrl+Y`).
2.  A transformação da configuração "Enter Confirms" em uma opção de usuário no painel de preferências.

O arquivo principal que controla este editor é `freemind/view/mindmapview/EditNodeDialog.java`, e o componente de texto é um `JTextArea`.

### 1.2. Objetivo da Tarefa
O objetivo é implementar uma funcionalidade de "Localizar e Substituir" para textos dentro do `JTextArea` do editor de nós longos. Isso é crucial para a produtividade ao editar blocos de texto muito grandes.

-   **Atalho de Ativação:** `Ctrl+F` para localizar, `Ctrl+H` para substituir.
-   **Interface:** Um painel de busca deve aparecer dentro da janela do editor, em vez de um diálogo separado, para uma experiência de usuário mais moderna e fluida.

## 2. Análise e Abordagem Proposta

Diferente de funcionalidades como "Desfazer", o Swing não oferece um componente pronto de "Localizar e Substituir". Portanto, a funcionalidade precisa ser construída.

A abordagem aprovada pelo usuário é **criar uma nova classe Java para um painel de busca reutilizável**.

-   **Classe do Painel:** `FindAndReplacePanel.java`
-   **Localização:** `freemind/view/mindmapview/`
-   **Comportamento:** O painel será um `JPanel` que ficará oculto por padrão e será exibido na parte superior ou inferior da área de texto quando `Ctrl+F` for pressionado.

### 2.1. Componentes da Interface do Painel

O painel de busca (`FindAndReplacePanel`) deverá conter os seguintes componentes:
-   `JTextField` para o termo de busca.
-   `JTextField` para o termo de substituição.
-   `JButton` para "Localizar Próximo".
-   `JButton` para "Substituir".
-   `JButton` para "Substituir Todos".
-   `JCheckBox` para "Diferenciar maiúsculas/minúsculas".
-   `JButton` para fechar/ocultar o painel.

## 3. Plano de Ação Detalhado (Para Próxima Instância)

A implementação deve seguir os seguintes passos:

### Passo 1: Criar a Classe `FindAndReplacePanel.java`
1.  **Criar o arquivo:** `freemind/view/mindmapview/FindAndReplacePanel.java`.
2.  **Estrutura da Classe:**
    -   A classe deve herdar de `javax.swing.JPanel`.
    -   Seu construtor deve inicializar e organizar todos os componentes da UI listados na seção 2.1 (usando um `LayoutManager` como `FlowLayout` ou `GridBagLayout`).
3.  **API Pública:** A classe deve expor métodos públicos para que o `EditNodeDialog` possa interagir com ela.
    -   `public String getSearchTerm()`
    -   `public String getReplaceTerm()`
    -   `public boolean isMatchCase()`
    -   `public void addFindNextListener(ActionListener listener)`
    -   `public void addReplaceListener(ActionListener listener)`
    -   `public void addReplaceAllListener(ActionListener listener)`
    -   `public void addCloseListener(ActionListener listener)`
    -   `public void requestFocusOnSearchField()`

### Passo 2: Integrar o Painel ao `EditNodeDialog.java`
1.  **Modificar `EditNodeDialog.java`:**
    -   No construtor da classe interna `LongNodeDialog`, instanciar o `FindAndReplacePanel`.
    -   Adicionar o painel ao layout principal do diálogo (ex: `BorderLayout.NORTH`), mantendo-o oculto por padrão (`findAndReplacePanel.setVisible(false)`).
2.  **Mapear Atalhos:**
    -   Usar a `InputMap` e `ActionMap` do `JTextArea` para mapear `Ctrl+F` e `Ctrl+H`.
    -   A ação associada a esses atalhos deve tornar o `FindAndReplacePanel` visível e chamar `requestFocusOnSearchField()`.

### Passo 3: Implementar a Lógica de Busca e Substituição
Ainda dentro de `EditNodeDialog.java`, criar os `ActionListeners` para os botões do painel.

1.  **Listener de "Localizar Próximo":**
    -   Obter o termo de busca e o estado do "match case" do painel.
    -   Obter o texto completo do `JTextArea`.
    -   A partir da posição atual do cursor (`textArea.getCaretPosition()`), usar `text.indexOf(searchTerm, fromIndex)` para encontrar a próxima ocorrência.
    -   Se a busca não encontrar a partir da posição atual, ela deve continuar do início do texto (busca circular).
    -   Se uma ocorrência for encontrada, usar `textArea.select(start, end)` para destacá-la.

2.  **Listener de "Substituir":**
    -   Verificar se há texto selecionado no `JTextArea`.
    -   Se o texto selecionado for igual ao termo de busca, usar `textArea.replaceSelection(replaceTerm)`.
    -   Após a substituição, acionar a lógica de "Localizar Próximo" para encontrar a próxima ocorrência.

3.  **Listener de "Substituir Todos":**
    -   Obter o texto completo do `JTextArea`.
    -   Usar `text.replace(searchTerm, replaceTerm)` (ou uma lógica mais complexa se "match case" estiver desativado) para substituir todas as ocorrências.
    -   Atualizar o `JTextArea` com o novo texto usando `textArea.setText(...)`.

4.  **Listener de "Fechar":**
    -   Chamar `findAndReplacePanel.setVisible(false)` para ocultar o painel.

## 4. Próximo Passo Imediato

A próxima ação a ser executada é **iniciar a criação do arquivo `freemind/view/mindmapview/FindAndReplacePanel.java`** com a estrutura básica da classe e seus componentes de interface, conforme descrito no Passo 1.
