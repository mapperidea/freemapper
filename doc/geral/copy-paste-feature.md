# Análise da Funcionalidade de Copiar e Colar

Este documento detalha o funcionamento da funcionalidade de colar texto simples no Freemapper, especificamente como um bloco de texto com múltiplas linhas e diferentes níveis de indentação é convertido em uma estrutura de nós hierárquicos.

## Visão Geral

Quando um usuário cola um texto na interface, o sistema detecta se o conteúdo da área de transferência é texto simples. Se for, uma rotina específica é acionada para processar esse texto linha por linha, analisando a indentação de cada uma para construir uma árvore de nós. A lógica principal para esse comportamento reside no método `pasteStringWithoutRedisplay` da classe `PasteAction`.

## Arquivos e Classes Chave

-   **`freemind/modes/mindmapmode/actions/PasteAction.java`**: Contém a lógica principal da ação de colar. É a classe mais importante para esta análise.
-   **`freemind/modes/mindmapmode/MindMapController.java`**: O controlador que gerencia as interações com o mapa, incluindo a criação de novos nós e o acesso à área de transferência.
-   **`freemind/main/Tools.java`**: Fornece métodos utilitários, como o acesso à área de transferência do sistema.

## Fluxo de Execução Detalhado

O processo, desde o atalho `Ctrl+V` até a criação dos nós, segue os seguintes passos:

1.  **Disparo da Ação**: O usuário pressiona `Ctrl+V` ou seleciona "Colar" no menu. O Swing dispara a `actionPerformed` na instância de `PasteAction`.

2.  **Obtenção do Conteúdo**: O método `actionPerformed` chama `mMindMapController.getClipboardContents()` para obter o conteúdo da área de transferência do sistema como um objeto `Transferable`.

3.  **Seleção do "Handler" Apropriado**: A lógica de `_paste` dentro de `PasteAction` itera sobre uma lista de "handlers" de conteúdo (`DataFlavorHandler`), cada um responsável por um tipo de dado (arquivos, nós internos do FreeMind, HTML, imagem e, finalmente, texto simples).

4.  **Execução do `StringFlavorHandler`**: Para texto simples, o `StringFlavorHandler` é selecionado. Ele simplesmente chama o método principal de processamento: `pasteStringWithoutRedisplay`.

5.  **Processamento no `pasteStringWithoutRedisplay`**: Este é o coração da funcionalidade.
    a.  O texto da área de transferência é obtido com `t.getTransferData(DataFlavor.stringFlavor)`.
    b.  O texto é dividido em um array de strings (`textLines`), usando a quebra de linha (`"\n"`) como delimitador.
    c.  As tabulações (`"\t"`) são convertidas para 8 espaços para padronizar o cálculo da indentação: `text.replaceAll("\t", "        ")`.
    d.  Duas listas, `parentNodes` e `parentNodesDepths`, são inicializadas. Elas mantêm o controle da hierarquia de nós sendo criada. `parentNodes` armazena os nós candidatos a serem pais, e `parentNodesDepths` armazena a profundidade (nível de indentação) de cada um.
    e.  O sistema itera sobre cada linha do texto colado.

6.  **Cálculo da Hierarquia**: Para cada linha:
    a.  A profundidade (`depth`) é calculada contando o número de espaços no início da linha.
    b.  O texto visível (`visibleText`) é extraído removendo-se os espaços do início e do fim (`text.trim()`).
    c.  Um novo nó é criado com o `visibleText`: `MindMapNode node = mMindMapController.newNode(visibleText, ...)`. 
    d.  O sistema então procura na lista `parentNodes` (de trás para frente) o **primeiro nó cuja profundidade seja menor que a profundidade da linha atual**.
    e.  Ao encontrar esse nó, ele é definido como o pai do nó recém-criado (`insertNodeInto(node, target)`). A lista de `parentNodes` é "limpa", removendo-se os nós que estavam em um nível de hierarquia mais profundo, e o novo nó é adicionado à lista para se tornar um pai em potencial para as próximas linhas.

7.  **Finalização**: Após o loop por todas as linhas, a estrutura de nós está montada na memória e o `MindMapController` atualiza a interface para exibir a nova árvore de nós.

## Conclusão

A criação de múltiplos nós a partir de um texto colado é um recurso intencional, implementado no método `pasteStringWithoutRedisplay`. A lógica depende inteiramente da contagem de espaços no início de cada linha para determinar a relação pai-filho, permitindo a rápida importação de listas ou esboços textuais para o formato de mapa mental.
