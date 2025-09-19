# Análise e Plano de Implementação: Atalho de Exportação "As MI" (Versão 3)

Este documento detalha a análise da funcionalidade de exportação e propõe um plano de implementação para adicionar um novo item de menu "As MI", que servirá como um atalho para a exportação usando um arquivo XSLT específico.

## 1. Objetivo

O objetivo é adicionar uma nova opção de exportação, "As MI", ao menu `File -> Export`. Esta opção funcionará como um atalho para a funcionalidade "Using XSLT...", pré-configurada para usar a transformação `freemind/accessories/exportMI.xsl`. O fluxo para o usuário deve ser o mais simples possível: clicar na opção, escolher onde salvar o arquivo e a exportação ser concluída.

## 2. Análise das Funcionalidades Envolvidas

### 2.1. Fluxo de Trabalho "Using XSLT" Existente

A funcionalidade existente é uma colaboração entre um plugin de ação e uma classe de diálogo de interface gráfica.

-   **Ponto de Entrada da Ação**: `freemind.accessories.plugins.ExportWithXSLT.java`
-   **Interface do Usuário (Diálogo)**: `freemind.accessories.plugins.util.xslt.ExportDialog.java`
-   **Lógica de Transformação**: O método `transformMapWithXslt(...)` dentro de `ExportWithXSLT.java`.

### 2.2. Análise do XSLT de Destino (`freemind/accessories/exportMI.xsl`)

O arquivo `exportMI.xsl` é a peça central da nova funcionalidade. Ele foi projetado para ser o "inverso" da funcionalidade de colar texto do Mapper Idea.

-   **Funcionalidade**: O XSLT percorre a árvore de nós do arquivo de mapa mental (`.mm`) e converte cada nó em uma linha de texto, mapeando os ícones de volta para seus atalhos textuais (ex: `[b]`, `[p]`).
-   **Estrutura e Formatação**: A hierarquia do mapa é convertida em indentação e o conteúdo de múltiplas linhas é preservado usando o prefixo `| `.

Em suma, o `exportMI.xsl` transforma um mapa mental visual de volta para o formato de texto puro do Mapper Idea.

## 3. Estratégia de Implementação para "As MI"

A abordagem será criar uma nova classe de ação, `ExportAsMI.java`, que reutilizará a lógica de transformação existente, mas com o fluxo de usuário drasticamente simplificado.

### Passo 1: Criar a Nova Classe de Ação `ExportAsMI.java`

-   **Localização**: `freemind/accessories/plugins/`
-   **Herança**: A nova classe `ExportAsMI` irá herdar de `ExportWithXSLT`.
-   **Lógica Principal**:
    1.  O método principal da ação será sobrescrito para implementar o fluxo simplificado.
    2.  O caminho para o arquivo de transformação será **fixado** para `accessories/exportMI.xsl` (o caminho é relativo aos recursos da aplicação).
    3.  Um diálogo `JFileChooser` será exibido, configurado apenas para **seleção de arquivo para salvar**. O título do diálogo será "Exportar como MI".
    4.  Após a seleção do arquivo de destino, o método `transformMapWithXslt` será chamado internamente, executando a exportação sem passos intermediários.

### Passo 2: Registrar a Nova Ação no Sistema de Plugins

1.  **Criar**: `freemind/accessories/plugins/ExportAsMI.properties`.
2.  **Conteúdo**: Definir a classe (`accessories.plugins.ExportAsMI`) e a chave do texto do menu.

### Passo 3: Adicionar o Texto da Ação aos Arquivos de Recurso

-   **Arquivo a ser modificado**: `freemind/Resources_en.properties`.
-   **Linha a ser adicionada**: `export_as_mi.text = As MI`.

## 4. Resumo dos Arquivos Relevantes

-   **A Criar**: `freemind/accessories/exportMI.xsl` (O recurso de transformação XSLT).
-   **A Criar**: `freemind/accessories/plugins/ExportAsMI.java` (A nova classe de ação).
-   **A Criar**: `freemind/accessories/plugins/ExportAsMI.properties` (O arquivo de registro do plugin).
-   **A Modificar**: `freemind/Resources_en.properties` (Para adicionar o texto do menu).
