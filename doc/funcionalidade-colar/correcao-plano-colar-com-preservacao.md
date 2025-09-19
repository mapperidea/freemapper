# Plano de Ação Corrigido: Preservação de Espaços na Colagem

Este documento substitui o `plano-ajustado-colar-com-preservacao-de-espacos.md` e detalha a estratégia correta para implementar a funcionalidade de colar texto com preservação de espaços.

## 1. Resumo do Problema

O objetivo é modificar a funcionalidade de "Colar" para que o conteúdo de texto de nós com os ícones `[v]` (tag_green) e `[y]` (tag_yellow) preserve todos os espaços em branco (leading e trailing) que fazem parte do *conteúdo* do nó. O conteúdo é definido como todo o texto que aparece após o marcador de atalho (ex: `[v] `).

Para todos os outros nós (com outros ícones ou sem ícones), o comportamento padrão de remover os espaços em branco do início e do fim do texto (`trim()`) deve ser mantido.

## 2. Análise da Falha na Abordagem Anterior

A implementação anterior e o plano original falharam por dois motivos interligados:

1.  **Aplicação Prematura do `trim()`**: O plano original sugeria executar `line.trim()` no início do processamento de cada linha. Isso removia indiscriminadamente todos os espaços em branco do início e do fim da linha, tornando impossível preservar os espaços que pertenciam ao conteúdo do nó (ex: `[v]   meu texto  ` se tornava `[v]   meu texto`).
2.  **Expressão Regular "Guloso" (Greedy)**: A expressão regular `\[([\w\.]+)\]\s*(.*)` usava `\s*` que consumia (comia) todos os espaços imediatamente após o atalho `]`. Isso removia os espaços iniciais do conteúdo antes mesmo que pudessem ser capturados.

A combinação desses dois fatores resultava na perda dos espaços que deveriam ser preservados.

## 3. Novo Plano de Implementação (Corrigido)

A nova abordagem separa claramente o tratamento da indentação da linha do tratamento do conteúdo do nó.

-   **Arquivo Alvo:** `freemind/freemind/modes/mindmapmode/actions/PasteAction.java`

### Passo 1: Modificar a Expressão Regular

A `mapperIdeaPattern` será alterada para não consumir mais os espaços após o atalho.

-   **De:** `private static final Pattern mapperIdeaPattern = Pattern.compile("[[\w\.]+]\]\s*(.*)");`
-   **Para:** `private static final Pattern mapperIdeaPattern = Pattern.compile("[[\w\.]+]\](.*)");`

**Justificativa:** Ao remover o `\s*`, o segundo grupo de captura `(.*)` irá capturar *todos* os caracteres que seguem imediatamente o colchete de fechamento `]`, incluindo os espaços em branco que fazem parte do conteúdo.

### Passo 2: Modificar o Método `pasteStringWithoutRedisplay`

A lógica dentro do loop de processamento de linhas será reestruturada da seguinte forma:

1.  Para cada linha de texto (`text`), calcular a `depth` (profundidade) contando os espaços no início. Isso é usado para a hierarquia.
2.  Obter o restante da linha, que contém o atalho e o conteúdo: `String lineContent = text.substring(depth);`. **Nenhum `trim()` é aplicado aqui.**
3.  Aplicar a **nova** `mapperIdeaPattern` a `lineContent`.
4.  **Se um atalho for encontrado (`matcher.find()`):**
    -   Extrair o `shortcut` do grupo 1.
    -   Extrair o conteúdo bruto do grupo 2: `String rawContent = matcher.group(2);`
    -   **Se o atalho for `v` ou `y`:**
        -   O texto final do nó será `rawContent` **exatamente como foi capturado**, preservando todos os espaços.
    -   **Se for qualquer outro atalho:**
        -   O texto final do nó será `rawContent.trim()`, mantendo o comportamento padrão.
5.  **Se nenhum atalho for encontrado:**
    -   O texto final do nó será `lineContent.trim()`, mantendo o comportamento padrão para linhas de texto simples.

## 4. Tabela de Exemplo (Fluxo Corrigido)

| Linha de Texto Original (`text`) | `depth` | `lineContent` | `matcher.group(2)` (`rawContent`) | Atalho | Texto Final do Nó |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `    [v]   Conteúdo.  ` | 4 | `[v]   Conteúdo.  ` | `   Conteúdo.  ` | `v` | `"   Conteúdo.  "` |
| `  [b]   Classe   ` | 2 | `[b]   Classe   ` | `   Classe   ` | `b` | `"Classe"` |
| `        Texto simples   ` | 8 | `Texto simples   ` | (sem match) | N/A | `"Texto simples"` |
| `[y]sem espaço` | 0 | `[y]sem espaço` | `sem espaço` | `y` | `"sem espaço"` |

## 5. Conclusão

Este novo plano corrige a falha da abordagem anterior ao isolar o `trim()` apenas para os casos onde ele é realmente necessário. Ele garante que a indentação da hierarquia seja calculada corretamente, enquanto a preservação de espaços no conteúdo dos nós `[v]` e `[y]` é respeitada, conforme o requisito original.
