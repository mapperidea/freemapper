# Algoritmo de Seleção de Ícones no FreeMind

Este documento descreve o fluxo de trabalho e o algoritmo por trás da funcionalidade de seleção de ícones no FreeMind, que é acionada pelo atalho `Alt+I`.

## Visão Geral

A funcionalidade permite ao usuário adicionar ícones a um ou mais nós selecionados através de um pop-up de seleção. Este pop-up exibe todos os ícones disponíveis em uma grade, permite a navegação via teclado e mouse, e oferece uma função de busca para filtrar os ícones por nome.

O processo é uma colaboração entre três classes principais:
1.  **`IconSelectionPlugin.java`**: O plugin que é acionado pelo atalho e orquestra a exibição do diálogo.
2.  **`IconSelectionPopupDialog.java`**: A classe que implementa a interface gráfica (GUI) do pop-up de seleção.
3.  **`IconAction.java`**: Uma classe de ação que representa a adição de um ícone específico a um nó.

## Fluxo de Execução Detalhado

O processo completo, desde a interação do usuário até a aplicação do ícone, segue os seguintes passos:

### 1. Iniciação via Atalho

-   O usuário pressiona `Alt+I`.
-   O sistema de mapeamento de teclas do FreeMind, configurado em `freemind.properties`, identifica que este atalho está associado à classe `IconSelectionPlugin`.
    -   `keystroke_accessories/plugins/IconSelectionPlugin.properties.properties_key=alt I`
-   O método `invoke()` da instância de `IconSelectionPlugin` é chamado.

### 2. Preparação do Diálogo (`IconSelectionPlugin`)

-   O método `invoke()` obtém o `MindMapController` para acessar o estado atual do mapa.
-   Ele recupera a lista de todas as ações de ícone disponíveis (`controller.iconActions`). Esta lista é um `Vector` que contém objetos `IconAction` pré-instanciados, um para cada ícone que o FreeMind conhece.
-   Uma nova instância de `IconSelectionPopupDialog` é criada, recebendo a lista de ações como parâmetro.
-   O diálogo é posicionado próximo ao nó selecionado na tela e é exibido de forma modal, aguardando a interação do usuário.

### 3. Interação no Pop-up de Seleção (`IconSelectionPopupDialog`)

Esta classe é responsável por toda a lógica da interface do usuário.

-   **Construção da GUI**:
    -   Os ícones recebidos são ordenados alfabeticamente por sua descrição.
    -   Os ícones são dispostos em uma grade (`GridLayout`), cujo número de colunas é calculado para que o layout seja o mais próximo possível de um quadrado.
    -   Cada ícone é um `JLabel` com um `MouseListener`.

-   **Navegação e Seleção**:
    -   O usuário pode navegar entre os ícones usando as **teclas de seta**.
    -   Passar o mouse sobre um ícone (`mouseEntered`) ou navegar com as setas atualiza o ícone selecionado.
    -   O ícone atualmente selecionado é destacado com uma borda verde e sua descrição (e atalho, se houver) é exibida em um `JLabel` na parte inferior do diálogo.

-   **Seleção Direta por Atalho**:
    -   O diálogo escuta eventos de teclado (`KeyListener`). Se o usuário pressionar uma tecla que corresponde ao atalho de um ícone específico (ex: '1' para o ícone "full-1"), o diálogo é imediatamente fechado e o ícone correspondente é selecionado.

-   **Funcionalidade de Busca**:
    -   Ao pressionar a tecla **`/`**, o diálogo entra no modo de busca.
    -   Caracteres subsequentes (letras e números) são concatenados para formar um termo de busca (`searchText`).
    -   A cada caractere digitado, o método `filterIcons()` é chamado.
    -   `filterIcons()` itera sobre todos os ícones, tornando visíveis apenas aqueles cuja descrição (em minúsculas) contém o `searchText`.
    -   O primeiro ícone que corresponde à busca é automaticamente selecionado.
    -   Pressionar `Enter` finaliza a busca, mantendo os ícones filtrados. `Backspace` apaga o último caractere da busca, e `Escape` cancela a busca e fecha o diálogo.

-   **Confirmação e Fechamento**:
    -   O usuário pode confirmar a seleção de três maneiras: clicando em um ícone, pressionando `Enter` ou `Espaço` com um ícone selecionado.
    -   Esta ação chama o método `addIcon()`, que armazena o índice do ícone selecionado e fecha o diálogo.
    -   Pressionar `Escape` fecha o diálogo sem nenhuma seleção.

### 4. Execução da Ação (`IconSelectionPlugin`)

-   Após o fechamento do diálogo, o controle retorna ao método `invoke()`.
-   O método obtém o resultado (o índice do ícone selecionado) do diálogo.
-   Se um ícone foi selecionado (resultado >= 0), ele recupera a `Action` correspondente (uma instância de `IconAction`) da lista original.
-   Finalmente, ele chama o método `actionPerformed()` nesse objeto `IconAction`.

### 5. Aplicação do Ícone (`IconAction`)

-   O método `actionPerformed()` na instância específica de `IconAction` é executado.
-   Ele itera sobre todos os nós selecionados no mapa.
-   Para cada nó, ele cria e executa uma transação (para suportar undo/redo) que adiciona o ícone específico (`AddIconAction`) ao nó.
-   A visualização do mapa é atualizada para exibir o novo ícone no nó.
