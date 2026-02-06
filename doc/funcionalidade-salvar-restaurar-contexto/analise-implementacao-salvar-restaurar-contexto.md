# Análise de Implementação: Funcionalidade de Salvar e Restaurar Contexto

## 1. Visão Geral da Funcionalidade

A funcionalidade solicitada visa permitir que o usuário salve a posição de um nó selecionado no mapa mental, navegue para outro nó (potencialmente através de uma busca pelo texto do nó salvo) e, em seguida, retorne facilmente à posição original.

O fluxo de trabalho proposto para o usuário seria:
1.  Selecionar um nó de interesse.
2.  Pressionar um atalho de teclado para "salvar" o contexto atual (o nó focado).
3.  Opcionalmente, o sistema poderia iniciar uma busca por um nó com texto idêntico ao do nó salvo, permitindo ao usuário navegar para essa nova ocorrência.
4.  Após realizar edições ou outras ações, o usuário pressionaria o mesmo atalho de teclado novamente para retornar ao nó salvo originalmente.

Esta funcionalidade é particularmente útil em mapas mentais grandes e complexos, onde a navegação manual pode ser demorada.

## 2. Análise do Código-Fonte

Uma investigação detalhada do código-fonte do FreeMind revelou os seguintes pontos-chave para a implementação:

### 2.1. Arquitetura Principal

O FreeMind utiliza uma arquitetura Model-View-Controller (MVC):
-   **Controller:** `freemind.controller.Controller` e, mais especificamente, `freemind.modes.mindmapmode.MindMapController` gerenciam as ações do usuário. A nova ação será implementada e gerenciada dentro do `MindMapController`.
-   **Model:** `freemind.modes.MindMapNode` é a interface que representa um nó. A implementação concreta `freemind.modes.mindmapmode.MindMapNodeModel` contém os dados do nó.
-   **View:** `freemind.view.mindmapview.MapView` é responsável pela renderização do mapa.

### 2.2. Gerenciamento de Atalhos de Teclado e Ações

-   As ações do usuário são encapsuladas em classes que herdam de `javax.swing.AbstractAction`.
-   O `MindMapController` instancia essas ações (ex: `find = new FindAction(this);`).
-   Os atalhos de teclado (`KeyStroke`) são associados a essas ações, geralmente durante a construção dos menus na classe `MindMapController` (método `processMenuCategory`).

### 2.3. Seleção e Foco de Nós

-   O nó atualmente selecionado pode ser obtido através de `controller.getSelected()`.
-   Para navegar e focar em um nó específico, o método `controller.centerNode(MindMapNode node)` é utilizado. Ele centraliza a visão no nó e o seleciona.
-   A classe `freemind.modes.common.actions.FindAction` contém um método utilitário, `displayNode(MindMapNode node, ArrayList nodesUnfoldedByDisplay)`, que desdobra o caminho até um nó para garantir que ele esteja visível antes da centralização.

### 2.4. Recuperação de Dados do Nó

A interface `MindMapNode` fornece métodos essenciais para obter os dados de um nó:
-   `String getText()`: Retorna o conteúdo do nó, que pode ser texto simples ou HTML.
-   `String getPlainTextContent()`: Retorna uma versão em texto puro do conteúdo, com as tags HTML removidas. Este método é ideal para a lógica de busca.
-   `String getObjectId(ModeController controller)`: Retorna um ID único para o nó, fundamental para salvar e restaurar o contexto de forma confiável.

### 2.5. Funcionalidade de Busca (Find)

-   A funcionalidade de busca existente é implementada na classe `freemind.modes.common.actions.FindAction`.
-   O método `find(...)` nesta classe realiza uma busca em largura (breadth-first search) a partir de um nó inicial.
-   Ele utiliza o método `prepareTextContent` para normalizar o texto do nó (remover HTML, converter para minúsculas) antes de comparar com o termo de busca.

## 3. Plano de Implementação Proposto

Com base na análise, a implementação pode seguir os seguintes passos:

### Passo 1: Criar a Classe `SaveRestoreContextAction`

-   Criar uma nova classe interna `SaveRestoreContextAction` dentro de `freemind.modes.mindmapmode.MindMapController.java`.
-   Esta classe herdará de `javax.swing.AbstractAction`.
-   Ela precisará de variáveis de membro para armazenar o estado:
    -   `private MindMapNode savedNode = null;`
    -   `private String savedNodeText = null;`
    -   `private enum State { IDLE, CONTEXT_SAVED };`
    -   `private State currentState = State.IDLE;`

### Passo 2: Implementar a Lógica da Ação

O método `actionPerformed(ActionEvent e)` da nova classe conterá a lógica principal, baseada em uma máquina de estados simples:

1.  **Estado `IDLE` (Nenhum contexto salvo):**
    -   Obter o nó selecionado com `getSelected()`.
    -   Se nenhum nó estiver selecionado, não fazer nada (ou emitir um som de aviso).
    -   Armazenar o nó: `savedNode = controller.getSelected();`
    -   Armazenar o texto do nó: `savedNodeText = savedNode.getPlainTextContent();`
    -   Mudar o estado para `CONTEXT_SAVED`.
    -   Opcional: Exibir uma mensagem de status na barra inferior (ex: "Contexto salvo: [texto do nó]").

2.  **Estado `CONTEXT_SAVED` (Contexto já salvo):**
    -   Verificar se `savedNode` ainda existe e é válido.
    -   Usar `controller.centerNode(savedNode)` para navegar de volta ao nó original.
    -   Garantir que o nó esteja visível usando uma lógica similar a `FindAction.displayNode()`.
    -   Resetar o estado para `IDLE`.
    -   `savedNode = null;`
    -   `savedNodeText = null;`
    -   Opcional: Exibir mensagem "Contexto restaurado."

### Passo 3: Integrar a Nova Ação ao `MindMapController`

1.  **Declaração:** Adicionar um novo campo no `MindMapController`:
    ```java
    public Action saveRestoreContext = null;
    ```
2.  **Instanciação:** Instanciar a ação no método `createStandardActions()`:
    ```java
    saveRestoreContext = new SaveRestoreContextAction();
    ```
3.  **Atalho e Menu:** Associar a ação a um atalho de teclado. Uma sugestão inicial seria `Ctrl+J`. Isso pode ser feito no método `processMenuCategory` ou em um método similar, adicionando a ação a um menu existente (como o menu "Navegar" ou "Editar") para que o atalho seja registrado.
    ```java
    // Em algum lugar da inicialização de menus
    add(holder, "navigate/save_restore_context", saveRestoreContext, "keystroke_save_restore_context");
    ```
    E adicionar a propriedade `keystroke_save_restore_context=ctrl J` ao arquivo `Resources_en.properties`.

### Passo 4: Implementar a Lógica de Busca (Feature Estendida)

Para a parte da funcionalidade que busca por um texto similar, a lógica em `actionPerformed` seria expandida:

-   A máquina de estados teria três fases: `IDLE`, `CONTEXT_SAVED`, `NAVIGATED_TO_MATCH`.
-   **No estado `CONTEXT_SAVED`:** Em vez de voltar ao nó original, o sistema iniciaria uma busca.
    -   Reutilizar a lógica de `FindAction.find()`, passando `savedNodeText` como o termo de busca.
    -   O ponto de partida da busca seria o `savedNode` para encontrar a *próxima* ocorrência.
    -   Se uma correspondência for encontrada, navegar para ela e mudar o estado para `NAVIGATED_TO_MATCH`.
-   **No estado `NAVIGATED_TO_MATCH`:**
    -   Navegar de volta para o `savedNode` original.
    -   Resetar o estado para `IDLE`.

## 4. Questões em Aberto e Considerações

-   **Atalho de Teclado:** Qual é o melhor atalho a ser usado? `Ctrl+J` é uma sugestão, mas precisa ser verificado para evitar conflitos.
-   **Interface do Usuário (UI):** Como o usuário saberá que um contexto foi salvo? Uma mensagem na barra de status parece ser a abordagem menos intrusiva.
-   **Persistência:** O contexto salvo deve persistir entre diferentes mapas abertos na mesma sessão? A proposta atual é que o contexto seja válido apenas para o mapa atual e seja perdido ao fechar o mapa.
-   **Nó Excluído:** O que acontece se o `savedNode` for excluído antes de ser restaurado? A implementação deve verificar se `savedNode` ainda faz parte da árvore do mapa antes de tentar navegar para ele. Se não existir, o estado deve ser resetado para `IDLE`.
-   **Complexidade da Busca:** A busca inicial deve ser por texto exato ou pode ser mais flexível? Para começar, a busca por texto exato (usando `getPlainTextContent()`) é a mais simples e segura.
