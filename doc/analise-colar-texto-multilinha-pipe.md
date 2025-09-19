# Análise da Implementação: Colar Texto Multilinha com Pipe

Este documento detalha a análise e a estratégia de implementação para um novo requisito na funcionalidade de colar texto: o suporte a conteúdo de nós que se estende por múltiplas linhas usando um caractere pipe (`|`) como prefixo de continuação.

## 1. Requisito

O objetivo é estender a sintaxe do Mapper Idea para permitir que o texto de um nó, especialmente um nó de valor (`[v]`), seja definido em várias linhas no texto de origem.

A sintaxe proposta é a seguinte:
- A primeira linha contém o marcador de atalho (ex: `[v]`) e, opcionalmente, a primeira parte do texto.
- As linhas subsequentes que pertencem ao mesmo nó devem começar com o prefixo `| ` (um caractere pipe seguido de um espaço).
- Ao colar, o prefixo `| ` deve ser removido, e o texto restante de cada linha de continuação deve ser anexado ao texto do nó original, separado por quebras de linha (`
`).

**Exemplo Fornecido:**
```
[v]     /**
  |      * Atualiza parcialmente um registro.
  |      */
  |     @Override
  |     public void patchById(long value, Body body) {
  |         // ...
  |     }
```

**Comportamento Esperado:**
O sistema deve criar um único nó. O texto desse nó deve ser a concatenação de todas as linhas, resultando em:
```java
    /**
     * Atualiza parcialmente um registro.
     */
    @Override
    public void patchById(long value, Body body) {
        // ...
    }
```

## 2. Análise de Impacto no Código-Fonte

-   **Arquivo Principal:** `freemind/freemind/modes/mindmapmode/actions/PasteAction.java`
-   **Método Chave:** `pasteStringWithoutRedisplay`

A lógica atual do método `pasteStringWithoutRedisplay` processa o texto colado linha por linha, de forma independente. Para implementar o novo requisito, será necessário introduzir um estado que permita a uma linha influenciar o processamento da próxima.

A nova lógica deve ser capaz de "lembrar" o último nó criado e verificar se a linha atual é uma continuação dele.

## 3. Estratégia de Implementação Proposta

A abordagem mais robusta e com menor impacto é a introdução de uma máquina de estados simples dentro do loop de processamento de linhas.

A modificação será feita no método `pasteStringWithoutRedisplay`:

1.  **Manter Estado do Último Nó:** Serão necessárias duas variáveis de estado no escopo do método, antes do loop que itera sobre as linhas:
    -   `MindMapNode lastCreatedNode = null;`
    -   `String lastUsedShortcut = null;`

2.  **Modificar o Loop de Processamento:** O loop que itera sobre cada linha de texto (`textLines`) será modificado para incluir a nova lógica de continuação.

**Pseudo-código da Nova Lógica no Início do Loop:**
```java
// Para cada linha de texto...
for (String line : textLines) {
    // Calcula a indentação da hierarquia e obtém o conteúdo real da linha.
    int depth = calculateDepth(line);
    String lineContent = line.substring(depth);

    // VERIFICAÇÃO DE CONTINUAÇÃO (NOVA LÓGICA)
    if (lineContent.startsWith("| ") && "v".equals(lastUsedShortcut) && lastCreatedNode != null) {
        // 1. Esta é uma linha de continuação.
        String continuationText = lineContent.substring(2); // Remove o prefixo "| "
        
        // 2. Anexa o texto ao nó anterior.
        lastCreatedNode.setText(lastCreatedNode.getText() + "
" + continuationText);
        
        // 3. Pula para a próxima iteração, pois esta linha já foi processada.
        continue; 
    }

    // Se não for uma linha de continuação, reseta o estado e processa normalmente.
    lastCreatedNode = null;
    lastUsedShortcut = null;

    // ... (lógica existente para criar um novo nó) ...
    // ...
    
    // Ao final do processamento da linha, se um novo nó foi criado:
    MindMapNode newNode = ... // O nó que foi criado nesta iteração.
    lastCreatedNode = newNode;
    lastUsedShortcut = foundShortcut; // O atalho que foi usado (ex: "v", "b", ou null).
}
```

### 4. Análise do Fluxo com a Nova Lógica

Vamos traçar o exemplo do requisito com a lógica proposta:

1.  **Linha 1: `[v]     /**`**
    -   A condição `lineContent.startsWith("| ")` é falsa.
    -   A lógica normal é executada. Um novo nó é criado com o texto `    /**`.
    -   Ao final, `lastCreatedNode` aponta para este novo nó e `lastUsedShortcut` é definido como `"v"`.

2.  **Linha 2: `  |      * Atualiza...`**
    -   A condição `lineContent.startsWith("| ")` é verdadeira.
    -   `lastUsedShortcut` é `"v"` e `lastCreatedNode` não é nulo.
    -   O texto `     * Atualiza...` é extraído.
    -   O texto do `lastCreatedNode` é atualizado para: `    /**
     * Atualiza...`
    -   O loop continua para a próxima linha (`continue`).

3.  **Linhas Subsequentes com `|`:**
    -   O processo se repete, anexando cada nova linha de continuação ao `lastCreatedNode`.

4.  **Linha sem `|` (ex: `[g] delete`):**
    -   A condição `lineContent.startsWith("| ")` é falsa.
    -   `lastCreatedNode` e `lastUsedShortcut` são resetados para `null`.
    -   A lógica normal prossegue, criando um novo nó para `[g] delete`. O estado de continuação é quebrado, como esperado.

## 5. Conclusão

Esta estratégia é robusta, de baixo impacto e totalmente compatível com as funcionalidades de colagem existentes. Ela estende o comportamento para o atalho `[v]` de forma limpa, sem afetar o processamento de outros atalhos ou de texto simples. A utilização de um estado (`lastCreatedNode`, `lastUsedShortcut`) é a maneira mais eficiente de lidar com a dependência entre linhas sequenciais.
