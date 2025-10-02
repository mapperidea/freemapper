# Análise e Plano de Implementação: Exportar Ramo como MI ("Export Branch as MI")

Este documento detalha a análise e o plano de ação para implementar a funcionalidade "Export Branch as MI", acessível através do menu de contexto de um nó.

## 1. Objetivo

O objetivo é criar uma nova opção no menu de contexto (acessado com o botão direito do mouse sobre um nó) chamada "Branch as MI", localizada sob o submenu "Export". Esta funcionalidade irá exportar o nó selecionado e todos os seus descendentes (o "ramo") para o formato de texto puro da metodologia Mapper Idea, utilizando a mesma lógica de transformação da funcionalidade "Export as MI" já existente.

## 2. Análise da Funcionalidade Existente ("Export as MI")

A análise dos documentos `doc/funcionalidade-export-mi/*.md` revela que a funcionalidade existente opera da seguinte forma:

-   **Classe de Ação**: `freemind.accessories.plugins.ExportAsMI.java` é a classe principal que orquestra a ação.
-   **Lógica de Transformação**: A classe utiliza o motor XSLT do Java para transformar o XML do mapa mental. Ela não herda de `ExportWithXSLT`, mas sim copia a lógica de transformação para ser autossuficiente, devido a restrições de acesso a métodos privados na classe pai.
-   **Arquivo XSLT**: A transformação é definida no arquivo `freemind/accessories/exportMI.xsl`. Este arquivo converte a estrutura XML do mapa para o formato de texto `.mi`, recriando a indentação e os atalhos de ícones (ex: `[b]`, `[v]`).
-   **Fluxo de Usuário**: A ação exibe um `JFileChooser` para que o usuário escolha o local e o nome do arquivo a ser salvo, sugerindo a extensão `.mi`.
-   **Pós-processamento**: Após a exportação, um passo de pós-processamento substitui os caracteres de indentação placeholder (`§`) por espaços.

## 3. Desafio Principal: Exportar um Ramo vs. o Mapa Inteiro

A principal diferença entre a funcionalidade existente e a nova é o escopo dos dados a serem exportados.

-   **`ExportAsMI`**: Serializa o **mapa mental inteiro** para XML e o passa para o transformador XSLT.
-   **`ExportBranchAsMI` (nova)**: Precisa serializar apenas o **nó selecionado e seus filhos** para XML.

O `exportMI.xsl` existente espera um documento XML que comece com a tag `<map>`. Se passarmos a ele um XML que começa com `<node ...>`, a transformação falhará.

**Solução Proposta:** Em vez de criar um novo arquivo XSLT, a abordagem mais eficiente é, na classe Java da nova ação, obter o XML do ramo do nó e **envolvê-lo programaticamente** com as tags `<map version="1.0.1"> ... </map>`. Isso torna o XML do ramo compatível com o `exportMI.xsl` existente, permitindo a reutilização total da lógica de transformação.

## 4. Plano de Implementação Detalhado

### Passo 1: Criar a Nova Classe de Ação

1.  **Criar Arquivo**: `freemind/accessories/plugins/ExportBranchAsMI.java`.
2.  **Conteúdo Inicial**: Copiar o conteúdo de `freemind.accessories.plugins.ExportAsMI.java` como ponto de partida. Isso reutilizará toda a lógica de `JFileChooser`, a chamada ao motor XSLT e o pós-processamento.
3.  **Modificar a Lógica de Obtenção de XML**:
    -   A nova classe `ExportBranchAsMI` irá herdar de `freemind.extensions.NodeHookAction` para ter acesso direto ao nó selecionado.
    -   O método principal da ação (`execute`) será modificado. Em vez de obter o XML do mapa inteiro, ele irá:
        a. Obter o nó selecionado (`MindMapNode`).
        b. Utilizar os mecanismos internos do FreeMind (provavelmente métodos no `MindMapController` ou classes de serialização) para converter o objeto do nó selecionado e seus descendentes em uma String XML.
        c. Envolver a String XML resultante com a tag `<map ...>`, conforme descrito na seção 3.
        d. Passar esta nova String XML para a lógica de transformação já existente no arquivo.

### Passo 2: Registrar a Nova Ação no Menu de Contexto

Diferente da "Export as MI", que é um plugin carregado dinamicamente via arquivos `.properties` e `.xml` no menu principal, o menu de contexto dos nós é definido de forma mais estática.

1.  **Adicionar Texto da Ação**:
    -   **Arquivo**: `freemind/Resources_en.properties`.
    -   **Adicionar Linha**: `export_branch_as_mi.text = Branch as MI`.

2.  **Adicionar Item ao Menu de Contexto**:
    -   **Arquivo**: `freemind/mindmap_menus.xml`.
    -   **Análise**: Este arquivo define a estrutura dos menus, incluindo o `node_popup_menu`.
    -   **Adicionar Item**: Localizar o submenu de exportação (`<menu name="export_branch" ...>`) e adicionar um novo item de menu que aponte para a nossa nova classe de ação.

    ```xml
    <menu_item name="export_branch_as_mi" class_name="freemind.accessories.plugins.ExportBranchAsMI"/>
    ```

    *Observação: Pode ser necessário criar um arquivo `ExportBranchAsMI.properties` em `freemind/accessories/plugins/` para associar o nome da ação (`export_branch_as_mi`) à chave de texto (`export_branch_as_mi.text`), dependendo de como o `mindmap_menus.xml` resolve os nomes.*

## 5. Resumo dos Arquivos a Serem Criados/Modificados

-   **A Criar**:
    -   `doc/funcionalidade-export-mi/analise-implementacao-export-branch-as-mi.md` (este documento).
    -   `freemind/accessories/plugins/ExportBranchAsMI.java` (coração da nova funcionalidade).
    -   `freemind/accessories/plugins/ExportBranchAsMI.properties` (para registro do nome da ação).

-   **A Modificar**:
    -   `freemind/Resources_en.properties` (para adicionar o texto "Branch as MI").
    -   `freemind/mindmap_menus.xml` (para adicionar a opção ao menu de contexto).

-   **A Reutilizar (sem modificação)**:
    -   `freemind/accessories/exportMI.xsl`.

Este plano permite implementar a nova funcionalidade de forma rápida e robusta, maximizando a reutilização de código e seguindo os padrões já estabelecidos no projeto.
