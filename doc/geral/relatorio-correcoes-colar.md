# Relatório de Correções na Funcionalidade de Colar

Este documento sumariza as recentes alterações realizadas no arquivo `freemind/modes/mindmapmode/actions/PasteAction.java` para corrigir comportamentos inesperados e melhorar a previsibilidade da funcionalidade de colar texto.

## 1. Generalização da Colagem Multilinha com Pipe (`|`)

-   **Problema:** A funcionalidade de continuar o texto de um nó em múltiplas linhas usando o prefixo `| ` estava restrita, funcionando apenas para nós que possuíam o ícone `[v]`. Isso impedia seu uso em nós de texto simples ou com outros ícones.

-   **Análise da Causa:** A condição no código que validava a continuação de linha verificava explicitamente se o último atalho de ícone usado era `"v"`.
    ```java
    // Lógica antiga
    if (lineContent.startsWith("| ") && "v".equals(lastUsedShortcut) && lastCreatedNode != null) { ... }
    ```

-   **Solução Implementada:** A verificação do atalho foi removida, generalizando a funcionalidade para qualquer nó que tenha sido criado na linha anterior.
    ```java
    // Lógica corrigida
    if (lineContent.startsWith("| ") && lastCreatedNode != null) { ... }
    ```

-   **Resultado:** A colagem de múltiplas linhas agora funciona de forma consistente para qualquer tipo de nó, alinhando-se com a expectativa do usuário.

## 2. Remoção da Detecção Automática de Hiperlinks

-   **Problema:** Ao colar um texto contendo uma URL (ex: `http://localhost`), o sistema criava automaticamente um hiperlink no nó. Isso causava uma inconsistência, pois ao copiar o mesmo nó novamente, o texto exportado incluía uma sintaxe especial (`#`) para representar o link, resultando em um conteúdo diferente do original.

-   **Análise da Causa:** O método de colagem continha uma lógica heurística que procurava por prefixos como `http://`, `https://` e `ftp://` para criar links automaticamente.

-   **Solução Implementada:** O bloco de código responsável por essa detecção e pela chamada `node.setLink()` foi completamente comentado, desativando a funcionalidade. As variáveis de suporte (`linkPrefixes`, `nonLinkCharacter`) também foram comentadas.

-   **Resultado:** A funcionalidade de colar agora é estritamente literal. O sistema não tenta mais interpretar o conteúdo do texto para criar links, garantindo que o ciclo de copiar e colar seja exato e previsível.
