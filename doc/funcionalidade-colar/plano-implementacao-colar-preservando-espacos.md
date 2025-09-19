# Plano de Implementação: Colagem com Preservação de Espaços

Este documento detalha o plano de ação técnico para modificar a funcionalidade de "Colar", garantindo que espaços em branco no conteúdo de nós específicos sejam preservados.

## 1. Objetivo

O objetivo é alterar o comportamento da colagem de texto para que, em linhas que utilizam os atalhos de ícone `[v]` (tag_green) e `[y]` (tag_yellow), todos os espaços em branco (leading e trailing) que fazem parte do *conteúdo* do nó sejam mantidos.

Para todos os outros nós (com outros ícones ou sem ícones), o comportamento padrão de remover os espaços em branco (`trim()`) deve ser mantido.

**Exemplo do Requisito:**
Ao colar o texto abaixo, a indentação de cada linha define a hierarquia, mas os espaços dentro do conteúdo de `[v]` e `[y]` devem ser preservados.

```
[e] element
    [v] sem ident
    [e] element
        [v] sem ident
        [v]    um ident
        [v]        dois ident
        [y]            três ident
        [y] sem ident
```

O nó "um ident" deve ser criado com o texto `"   um ident"`.

## 2. Análise do Código-Fonte Atual

-   **Arquivo:** `freemind/freemind/modes/mindmapmode/actions/PasteAction.java`
-   **Método:** `pasteStringWithoutRedisplay`

A análise do método revela duas falhas principais que impedem o comportamento desejado:

1.  **Remoção Prematura de Espaços:** A linha `String visibleText = text.trim();` é executada antes de qualquer outra lógica. Isso remove todos os espaços do início e do fim da linha, tornando impossível preservar os espaços do conteúdo, pois eles já foram descartados.
2.  **Expressão Regular "Guloso" (Greedy):** A expressão regular `Pattern.compile("\\[([\\w\\.]+)\\]\\s*(.*)");` usa `\s*` para capturar os espaços *após* o atalho `]`. Isso "come" os espaços que deveriam fazer parte do conteúdo, tratando-os como separadores.

## 3. Plano de Ação Detalhado

A implementação corrigirá as duas falhas, separando o cálculo da indentação da hierarquia do tratamento do conteúdo do nó.

### Passo 1: Corrigir a Expressão Regular

A `mapperIdeaPattern` será alterada para não consumir os espaços que seguem o atalho.

-   **De:**
    ```java
    private static final Pattern mapperIdeaPattern = Pattern.compile("\\[([\\w\\.]+)\\]\\s*(.*)");
    ```
-   **Para:**
    ```java
    private static final Pattern mapperIdeaPattern = Pattern.compile("\\[([\\w\\.]+)\\](.*)");
    ```
**Justificativa:** Ao remover o `\s*`, o segundo grupo de captura `(.*)` irá capturar *todos* os caracteres que seguem imediatamente o colchete `]`, incluindo os espaços em branco que desejamos preservar.

### Passo 2: Reestruturar a Lógica no Método `pasteStringWithoutRedisplay`

A lógica dentro do loop `for` que processa cada linha (`text`) será completamente reestruturada.

**Lógica Proposta:**

1.  Para cada linha (`text`), primeiro calcular a `depth` (profundidade) para a hierarquia, contando os espaços no início.
    ```java
    int depth = 0;
    while (depth < text.length() && text.charAt(depth) == ' ') {
        ++depth;
    }
    ```
2.  Extrair o conteúdo da linha, sem a indentação da hierarquia. **Nenhum `trim()` deve ser aplicado aqui.**
    ```java
    String lineContent = text.substring(depth);
    ```
3.  Aplicar a **nova** `mapperIdeaPattern` a `lineContent`.
    ```java
    Matcher matcher = mapperIdeaPattern.matcher(lineContent);
    MindMapNode node;
    String nodeText;
    ```
4.  Implementar a lógica condicional para tratar o texto do nó:
    ```java
    if (matcher.find()) {
        String shortcut = matcher.group(1);
        String rawContent = matcher.group(2); // Conteúdo bruto com espaços

        if (shortcut.equals("v") || shortcut.equals("y")) {
            // Caso A: Preservar espaços para [v] e [y]
            nodeText = rawContent;
        } else {
            // Caso B: Comportamento padrão para outros ícones
            nodeText = rawContent.trim();
        }

        // Criar o nó e adicionar o ícone
        node = mMindMapController.newNode(nodeText, parent.getMap());
        String iconName = mapperIdeaIconMap.get(shortcut);
        if (iconName != null) {
            MindIcon icon = MindIcon.factory(iconName);
            if (icon != null) {
                node.addIcon(icon, 0);
            }
        }
    } else {
        // Caso C: Comportamento padrão para linhas sem ícone
        nodeText = lineContent.trim();
        node = mMindMapController.newNode(nodeText, parent.getMap());
    }
    ```
5.  O restante da lógica do loop (tratamento de links, determinação do nó pai, etc.) continua após a criação do `node`.

## 4. Conclusão

Este plano corrige as falhas da abordagem anterior de forma precisa. Ele isola o cálculo da indentação da hierarquia e garante que o `trim()` seja aplicado seletivamente, apenas nos casos onde ele é necessário. Isso cumpre o requisito de preservar a formatação do conteúdo para os nós `[v]` e `[y]`, melhorando significativamente a usabilidade da funcionalidade de colar para a metodologia Mapper Idea.
