# Relatório de Correção: Exportação de Texto Longo (Nós de Conteúdo)

Este documento detalha a análise e o plano de correção para um bug na funcionalidade de "Copiar" (Exportar para MI) que impedia a exportação de textos longos inseridos no editor de nós (`Alt+Enter`).

## 1. Problema Reportado

Ao copiar um ramo do mapa mental, o conteúdo de nós que continham texto longo (como blocos de código XML inseridos via `Alt+Enter`) não era incluído no resultado. Os nós que deveriam conter esse texto apareciam vazios ou apenas com seu rótulo principal.

## 2. Análise da Causa Raiz

A análise de um arquivo de mapa de exemplo (`teste.mm`) fornecido pelo usuário foi crucial para identificar a causa raiz.

A estrutura de um nó com texto longo no FreeMind é a seguinte:

```xml
<node TEXT="content">
    <!-- Nó filho sem texto, que serve como contêiner -->
    <node>
        <richcontent TYPE="NODE">
            <html>
                <body>
                    <p>Linha 1 do texto longo.</p>
                    <p>Linha 2 do texto longo.</p>
                    ...
                </body>
            </html>
        </richcontent>
    </node>
</node>
```

O problema reside no script de transformação `freemind/accessories/exportMI.xsl`, que é responsável por converter a estrutura do mapa em texto. A lógica do script era muito simples e continha uma falha fundamental:

-   **Seleção Limitada:** O script lia o conteúdo de cada nó usando exclusivamente o seletor `select="@TEXT"`. Ele extraía apenas o texto do atributo `TEXT` do nó.
-   **Ignorava `<richcontent>`:** O script não tinha conhecimento da tag `<richcontent>` e, portanto, ignorava completamente qualquer texto longo armazenado nela.

No exemplo acima, o script processava o primeiro nó e exportava o texto "content". Em seguida, ao processar o nó filho, não encontrava um atributo `TEXT`, resultando em uma saída vazia para o conteúdo que deveria ser o texto longo.

## 3. Plano de Correção

A solução é tornar o script `exportMI.xsl` mais inteligente, ensinando-o a procurar texto tanto no atributo `@TEXT` quanto dentro da tag `<richcontent>`.

-   **Arquivo a ser Modificado:** `freemind/accessories/exportMI.xsl`
-   **Template a ser Modificado:** O template que corresponde a um elemento `<node>`.

### Passo de Implementação:

A seguinte linha no XSLT:
```xml
<xsl:variable name="nodeText" select="@TEXT"/>
```
será substituída por um bloco lógico que define a variável `nodeText` de forma condicional:

```xml
<xsl:variable name="nodeText">
    <xsl:choose>
        <!-- Caso 1: O nó tem um atributo TEXT (comportamento padrão) -->
        <xsl:when test="string-length(@TEXT) > 0">
            <xsl:value-of select="@TEXT"/>
        </xsl:when>
        <!-- Caso 2: O nó contém um richcontent (novo comportamento para texto longo) -->
        <xsl:when test="richcontent[@TYPE='NODE']">
            <!-- Itera sobre cada parágrafo <p> dentro do HTML -->
            <xsl:for-each select="richcontent/html/body/p">
                <!-- Extrai o texto do parágrafo, normalizando espaços -->
                <xsl:value-of select="normalize-space(.)"/>
                <!-- Adiciona uma quebra de linha entre os parágrafos -->
                <xsl:if test="position() != last()">
                    <xsl:text>&#xA;</xsl:text> <!-- Caractere de nova linha -->
                </xsl:if>
            </xsl:for-each>
        </xsl:when>
    </xsl:choose>
</xsl:variable>
```

### Justificativa da Solução:

1.  **Priorização:** A nova lógica primeiro verifica se o nó tem um texto de rótulo normal (`@TEXT`).
2.  **Fallback Inteligente:** Se não houver um rótulo, ele verifica se há um `<richcontent>`.
3.  **Extração de Conteúdo:** Se encontrar um `<richcontent>`, ele extrai o texto de cada parágrafo (`<p>`), junta tudo e recria as quebras de linha.
4.  **Compatibilidade:** O restante do script, que já sabe como lidar com texto de múltiplas linhas (usando a variável `$nodeText`), funcionará perfeitamente com o conteúdo extraído do `<richcontent>`, aplicando o prefixo `|` corretamente.

Esta alteração resolve o bug de forma completa, unificando o tratamento de textos curtos e longos durante a exportação para o formato de texto `.mi`.
