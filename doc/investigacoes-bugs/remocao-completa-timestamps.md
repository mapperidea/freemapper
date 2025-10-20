# Plano de Ação: Remoção Completa dos Atributos de Timestamp

Este documento detalha a análise e o plano de implementação para remover completamente os atributos de timestamp `CREATED` e `MODIFIED` dos arquivos de mapa mental (`.mm`).

## 1. Objetivo

O objetivo é impedir que os atributos `CREATED` e `MODIFIED` sejam escritos nos arquivos `.mm` quando um mapa é salvo. Isso resulta em arquivos mais "limpos" e elimina completamente qualquer ruído relacionado a datas em sistemas de controle de versão, indo além de apenas ignorar as ações de expandir/recolher nós.

## 2. Análise da Causa Raiz

A investigação do código-fonte identificou a classe e o método exatos responsáveis por persistir esses atributos:

-   **Arquivo Chave**: `freemind/freemind/modes/NodeAdapter.java`
-   **Método Chave**: `save(Writer writer, MindMapLinkRegistry registry, boolean saveInvisible, boolean saveChildren)`

Dentro deste método, o seguinte bloco de código é responsável por ler os dados de `HistoryInformation` do nó e escrevê-los como atributos no XML:

```java
// history information, fc, 11.4.2005
if (historyInformation != null) {
    node.setAttribute(XMLElementAdapter.XML_NODE_HISTORY_CREATED_AT,
            Tools.dateToString(getHistoryInformation().getCreatedAt()));
    node.setAttribute(
            XMLElementAdapter.XML_NODE_HISTORY_LAST_MODIFIED_AT, Tools
                    .dateToString(getHistoryInformation()
                            .getLastModifiedAt()));
}
```

**Conclusão:** Para impedir que os atributos sejam salvos, este bloco de código deve ser desativado.

## 3. Plano de Implementação

A solução consiste em modificar o método `save` em `NodeAdapter.java` para comentar o bloco de código responsável por escrever os atributos de histórico.

### Passo de Implementação (Para Execução Futura)

1.  **Modificar o `NodeAdapter.java`**:
    -   Utilizar a ferramenta `replace` para comentar todo o bloco `if (historyInformation != null) { ... }`.
    -   **Comando Planejado**:
        ```
        replace(
            file_path: "/mnt/disk2/workspace/mapperidea/freemapper2/freemapper/freemind/freemind/modes/NodeAdapter.java",
            old_string: "// history information, fc, 11.4.2005\n\t\tif (historyInformation != null) {\n\t\t\tnode.setAttribute(XMLElementAdapter.XML_NODE_HISTORY_CREATED_AT,\n\t\t\t\t\tTools.dateToString(getHistoryInformation().getCreatedAt()));\n\t\t\tnode.setAttribute(\n\t\t\t\t\tXMLElementAdapter.XML_NODE_HISTORY_LAST_MODIFIED_AT, Tools\n\t\t\t\t\t\t\t.dateToString(getHistoryInformation()\n\t\t\t\t\t\t\t\t\t.getLastModifiedAt()));\n\t\t}",
            new_string: "// history information, fc, 11.4.2005 -- DISABLED TO PREVENT VERSIONING NOISE\n\t\t/*\n\t\tif (historyInformation != null) {\n\t\t\tnode.setAttribute(XMLElementAdapter.XML_NODE_HISTORY_CREATED_AT,\n\t\t\t\t\tTools.dateToString(getHistoryInformation().getCreatedAt()));\n\t\t\tnode.setAttribute(\n\t\t\t\t\tXMLElementAdapter.XML_NODE_HISTORY_LAST_MODIFIED_AT, Tools\n\t\t\t\t\t\t\t.dateToString(getHistoryInformation()\n\t\t\t\t\t\t\t\t\t.getLastModifiedAt()));\n\t\t}\n\t\t*/",
            instruction: "In NodeAdapter.java, comment out the block that writes the CREATED and MODIFIED history attributes to the XML file to prevent them from being saved."
        )
        ```

2.  **Validação**:
    -   Compilar o projeto com `ant -f freemind/build.xml dist`.
    -   Executar a aplicação com `ant -f freemind/build.xml run`.
    -   Criar um novo mapa, adicionar alguns nós, salvá-lo.
    -   Abrir o arquivo `.mm` resultante em um editor de texto e verificar que os atributos `CREATED` e `MODIFIED` **não estão presentes** nas tags `<node>`.
    -   Abrir um mapa antigo que continha os atributos, salvá-lo novamente e verificar que os atributos foram removidos.

## 4. Implicações e Riscos (Reiterando)

-   **Permanência:** Esta alteração é definitiva. Uma vez que um mapa é salvo com esta versão modificada do programa, seus dados de timestamp serão perdidos.
-   **Funcionalidades Afetadas:** Recursos que exibem ou dependem das datas de criação/modificação deixarão de funcionar como antes.

Esta abordagem atende ao requisito de ter arquivos de mapa mental mínimos, ideais para controle de versão, ao custo da perda de metadados de histórico.
