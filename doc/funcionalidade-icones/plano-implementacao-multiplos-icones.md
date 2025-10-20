# Plano de Ação: Suporte a Múltiplos Ícones por Nó

Este documento detalha o plano de implementação para estender as funcionalidades de colar texto e exportar para `.mi`, permitindo que um único nó suporte múltiplos ícones.

## 1. Objetivo

O objetivo é permitir que a sintaxe do Mapper Idea e as funcionalidades do FreeMind suportem a atribuição de múltiplos ícones a um único nó. Isso deve funcionar tanto na importação (colar texto) quanto na exportação (Exportar como MI / Exportar Ramo como MI).

**Exemplo de Sintaxe Desejada:**
```
[e][v] Este nó tem o ícone "element" e "tag_green"
```

## 2. Análise das Limitações Atuais

Conforme a análise anterior, as limitações estão em dois locais distintos:

1.  **Exportação (`exportMI.xsl`):** A transformação XSLT utiliza uma estrutura `<xsl:choose>` que processa apenas a primeira correspondência de ícone que encontra, ignorando quaisquer outros ícones que o nó possa ter.
2.  **Colar Texto (`PasteAction.java`):** A lógica de parsing utiliza uma expressão regular que foi projetada para encontrar um único padrão `[atalho]` no início da linha, tratando o resto como o texto do nó.

## 3. Plano de Implementação Detalhado

A implementação será dividida em duas fases, tratando a exportação e a importação separadamente.

### Fase 1: Modificar a Lógica de Exportação (Saída)

Esta fase garante que, ao exportar, todos os ícones de um nó sejam corretamente escritos no arquivo `.mi`.

1.  **Arquivo Alvo:** `freemind/accessories/exportMI.xsl`
2.  **Estratégia:** Substituir a estrutura `<xsl:choose>` por um loop `<xsl:for-each>` que itera sobre todos os elementos `<icon>` de um nó.
3.  **Passos de Implementação:**
    *   Localizar o template que corresponde a um `<node>`.
    *   Dentro deste template, remover o bloco `<xsl:choose>` existente que lida com os ícones.
    *   No lugar, inserir um bloco `<xsl:for-each select="icon">`.
    *   Dentro do `for-each`, usar um `<xsl:choose>` para mapear o valor do atributo `@BUILTIN` de cada ícone para seu respectivo atalho em texto (ex: `[e]`, `[v]`). Incluir um caso `otherwise` para lidar com ícones que não têm um atalho mapeado, escrevendo seu nome completo (ex: `[nome_do_icone]`).
    *   Após o bloco `for-each`, adicionar uma lógica `<xsl:if test="icon">` que insere um único espaço em branco, para separar a sequência de atalhos do texto do nó.

### Fase 2: Modificar a Lógica de Colar Texto (Entrada)

Esta fase garante que uma linha com múltiplos atalhos de ícone seja corretamente interpretada ao ser colada.

1.  **Arquivo Alvo:** `freemind/freemind/modes/mindmapmode/actions/PasteAction.java`
2.  **Método Alvo:** `pasteStringWithoutRedisplay`
3.  **Estratégia:** Modificar a lógica de parsing de linha para que ela processe múltiplos atalhos em sequência antes de definir o texto do nó.
4.  **Passos de Implementação:**
    *   Dentro do loop principal que itera sobre as linhas do texto, após extrair o `lineContent`, a lógica de `if (matcher.find())` será substituída.
    *   Será criado um novo loop (ex: `while`) que continuará enquanto a `lineContent` começar com `[` e terminar com `]`.
    *   **Regex para o Loop:** Uma nova expressão regular mais simples, como `^\[([\w\.]+)\]`, será usada para extrair apenas o primeiro atalho da string.
    *   **Lógica do Loop:**
        a. Tenta corresponder a regex com o início da `lineContent`.
        b. Se corresponder, o atalho é extraído.
        c. O atalho é usado para encontrar o nome do ícone (usando o `mapperIdeaIconMap` existente).
        d. O ícone é adicionado ao nó recém-criado (`node.addIcon(icon, 0)`).
        e. A parte processada da `lineContent` (o atalho `[...]]`) é removida do início da string.
        f. Opcionalmente, um espaço em branco inicial também é removido.
    *   **Texto do Nó:** Após o término do loop `while`, a `lineContent` restante será o texto final do nó.

## 4. Passos de Validação

Após a implementação, os seguintes testes devem ser realizados:

1.  **Teste de Exportação:**
    *   Manualmente, adicione dois ou mais ícones a um nó no FreeMind.
    *   Use a função "Exportar Ramo como MI".
    *   Verifique se o arquivo de texto gerado contém todos os atalhos dos ícones na linha correspondente (ex: `[e][v] Texto do nó`).

2.  **Teste de Colar:**
    *   Crie um arquivo de texto com uma linha como `[p][g] Nó com múltiplos ícones`.
    *   Copie e cole esta linha no FreeMind.
    *   Verifique se o nó resultante é criado com ambos os ícones (`Package` e `Descriptor.grouping`).

3.  **Teste de Regressão:**
    *   Execute os testes de colar e exportar com nós que têm apenas um ícone e com nós que não têm nenhum ícone.
    *   Verifique se o comportamento para esses casos permanece inalterado e funcional.
