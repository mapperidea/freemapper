# Plano de Ação: Remoção da Detecção Automática de Links na Colagem

Este documento detalha a análise e o plano de ação para remover a funcionalidade que detecta e cria hiperlinks automaticamente ao colar texto.

## 1. Problema Identificado

Ao colar um texto que contém uma URL (ex: `http://localhost`), a funcionalidade de colar (`PasteAction.java`) automaticamente adiciona um hiperlink ao nó criado.

Este comportamento "inteligente" causa um efeito colateral indesejado:
1.  O nó é criado com um hiperlink que não era parte do texto original.
2.  Ao copiar este nó novamente, a funcionalidade de exportação para texto gera uma sintaxe especial (`#`) para representar o hiperlink, resultando em um texto diferente do original.
3.  Isso quebra o ciclo de "copiar -> colar -> copiar" exato, gerando conteúdo inesperado para o usuário.

## 2. Análise da Causa Raiz

A análise do código-fonte em `freemind/freemind/modes/mindmapmode/actions/PasteAction.java`, no método `pasteStringWithoutRedisplay`, revelou o bloco de código responsável por este comportamento.

```java
// Heuristically determine, if there is a link.
String[] linkPrefixes = { "http://", "ftp://", "https://" };

for (int j = 0; j < linkPrefixes.length; j++) {
    int linkStart = text.indexOf(linkPrefixes[j]);
    if (linkStart != -1) {
        // ... lógica para extrair a URL ...
        node.setLink(text.substring(linkStart, linkEnd));
    }
}
```
Este código itera sobre uma lista de prefixos de URL. Se um prefixo é encontrado no texto do nó, ele extrai a URL e a define como um hiperlink no objeto do nó (`node.setLink(...)`).

## 3. Plano de Ação

A solução é remover completamente essa lógica heurística para garantir que a colagem de texto seja sempre uma operação literal, sem efeitos colaterais automáticos.

-   **Arquivo a ser Modificado:** `freemind/freemind/modes/mindmapmode/actions/PasteAction.java`
-   **Método a ser Modificado:** `pasteStringWithoutRedisplay`

### Passo de Implementação:

1.  **Remover a Lógica de Detecção:** O bloco `for` que itera sobre `linkPrefixes` e chama `node.setLink()` será completamente removido ou comentado.
2.  **Remover Definições Associadas:** As variáveis que suportam essa lógica (`linkPrefixes` e `nonLinkCharacter`) também serão removidas ou comentadas para manter o código limpo.

### Exemplo da Alteração (Comentando o código):

```java
// Em pasteStringWithoutRedisplay:
// String[] linkPrefixes = { "http://", "ftp://", "https://" };

// ... dentro do loop principal ...

/*
// Heuristically determine, if there is a link.
for (int j = 0; j < linkPrefixes.length; j++) {
    int linkStart = text.indexOf(linkPrefixes[j]);
    if (linkStart != -1) {
        int linkEnd = linkStart;
        while (linkEnd < text.length()
                && !nonLinkCharacter.matcher(
                        text.substring(linkEnd, linkEnd + 1))
                        .matches()) {
            linkEnd++;
        }
        node.setLink(text.substring(linkStart, linkEnd));
    }
}
*/
```

## 4. Conclusão

A remoção desta funcionalidade de detecção automática de links é uma alteração de baixo risco que resolve o problema de inconsistência na cópia/colagem. Ela garante que a funcionalidade de colar seja previsível e literal, tratando o texto do usuário como a única fonte da verdade, o que está mais alinhado com as expectativas para uma ferramenta de produtividade.
