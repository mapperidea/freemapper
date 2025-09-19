# Análise: Remoção do Link Automático de E-mail na Colagem

Este documento detalha a análise da funcionalidade que adiciona automaticamente um link de e-mail a nós cujo texto contém o caractere "@".

## 1. Problema Identificado

Ao colar um texto que contém o caractere "@", mesmo que não seja um endereço de e-mail, o FreeMind adiciona um link `mailto:` ao nó, o que, por consequência, exibe um ícone de e-mail. 

Este comportamento é indesejado e atrapalha o uso da sintaxe da metodologia Mapper Idea, que utiliza o "@" para outros fins.

**Exemplo do Problema:**
Ao colar a linha abaixo, o nó é criado com o ícone `tag_green` e também com um ícone de e-mail/link.
```
[v] classes/class[ @name=$className]
```

## 2. Análise do Código-Fonte

-   **Arquivo:** `freemind/freemind/modes/mindmapmode/actions/PasteAction.java`
-   **Método:** `pasteStringWithoutRedisplay`

A causa do problema é um bloco de código intencional, projetado para ser uma "feature" de conveniência:

```java
// Heuristically determine, if there is a mail.

Matcher mailMatcher = mailPattern.matcher(nodeText);
if (mailMatcher.find()) {
    node.setLink("mailto:" + mailMatcher.group());
}
```

Este código utiliza a seguinte expressão regular para encontrar o que considera ser um e-mail:

```java
Pattern mailPattern = Pattern.compile("([^@ <>*\\'\`]+@[^@ <>*\\'\`]+)");
```

## 3. Causa do Comportamento Indesejado

1.  **Lógica Heurística Simplista:** A funcionalidade não valida se o texto é um endereço de e-mail válido. A expressão regular apenas procura por um caractere `@` cercado por qualquer sequência de caracteres que não sejam espaços ou alguns outros símbolos, como `<` e `>`. 
2.  **Falso Positivo:** No caso do texto `classes/class[ @name=$className]`, a expressão regular encontra uma correspondência incorreta em `class[ @name`, pois essa substring satisfaz a regra simplista, e o método `find()` localiza essa correspondência dentro da string maior.
3.  **Funcionalidade Legada:** A detecção automática de e-mails para criar links `mailto:` é uma funcionalidade que perdeu relevância com a diminuição do uso de clientes de e-mail locais, tornando-se mais um obstáculo do que uma ajuda nos casos de uso atuais do Mapper Idea.

## 4. Conclusão e Recomendação

A funcionalidade de detecção automática de e-mail é baseada em uma heurística frágil que entra em conflito direto com a sintaxe utilizada no projeto. Ela causa mais problemas do que benefícios.

**Recomendação:** Remover completamente o bloco de código responsável pela detecção de e-mail do método `pasteStringWithoutRedisplay`. Isso eliminará os falsos positivos e o comportamento indesejado, sem nenhum impacto negativo nas funcionalidades principais do Mapper Idea.

```