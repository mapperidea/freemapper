# Plano de Implementação Corrigido: Exportar Ramo como MI

Este documento detalha o novo plano de ação para implementar a funcionalidade "Export Branch as MI", com base em uma análise estática do código-fonte que revelou o mecanismo de serialização correto.

## 1. Análise da Falha Anterior

A tentativa anterior de implementação falhou devido a repetidos erros de compilação. A causa raiz foi a incapacidade de encontrar um método público e direto para serializar um único ramo de nó (`MindMapNode`) para XML. As abordagens baseadas em adivinhação de assinaturas de métodos (`node.write(...)`) ou no uso incorreto de classes internas (`MindMapActions`) se provaram ineficazes.

## 2. Descoberta da Solução Através da Análise de Código

A análise da classe `freemind.modes.mindmapmode.actions.CopyAction` e do `freemind.modes.mindmapmode.MindMapController` revelou o mecanismo correto, que é o mesmo utilizado pela funcionalidade de Copiar/Colar:

1.  **Obter o Controller e as Ações**: O `MindMapController` (que precisa de um "cast" a partir do `ModeController`) possui um método `getActions()` que retorna uma instância de `MindMapActions`.
2.  **Criar um `Transferable`**: A classe `MindMapActions` possui um método `createTransferable(Vector nodes)` que pega um vetor de nós e o encapsula em um objeto `Transferable` padrão do Java.
3.  **Extrair o XML**: O objeto `Transferable` pode fornecer os dados do nó em diferentes "sabores" (`DataFlavor`). Um desses sabores, `MindMapActions.mindMapNodesFlavor`, retorna os dados dos nós como uma **String XML**, já formatada e envolvida por uma tag `<map>`, exatamente o que é necessário para a transformação XSLT.

Esta abordagem é robusta porque reutiliza o mesmo mecanismo interno que o FreeMind usa para a área de transferência, garantindo a serialização correta.

## 3. Novo Plano de Implementação Detalhado

### Passo 1: Criar a Nova Classe de Ação

1.  **Criar Arquivo**: `freemind/accessories/plugins/ExportBranchAsMI.java`.
2.  **Conteúdo Inicial**: Usar o código de `freemind.accessories.plugins.ExportAsMI.java` como base, pois ele já contém a lógica de `JFileChooser` e o pós-processamento do arquivo.

### Passo 2: Modificar a Lógica de Obtenção de XML (Método Corrigido)

Dentro da nova classe `ExportBranchAsMI.java`, o método `getBranchXml(MindMapNode node)` será implementado da seguinte forma:

```java
private StringWriter getBranchXml(MindMapNode node) throws Exception {
    // 1. Fazer o cast para o controller correto e obter as ações
    MindMapController mmController = (MindMapController) getController();
    MindMapActions actions = (MindMapActions) mmController.getActions();

    // 2. Preparar o vetor de nós para a serialização
    Vector<MindMapNode> nodesToExport = new Vector<MindMapNode>();
    nodesToExport.add(node);

    // 3. Criar o Transferable e extrair a string XML
    java.awt.datatransfer.Transferable transferable = actions.createTransferable(nodesToExport);
    String xmlData = (String) transferable.getTransferData(MindMapActions.mindMapNodesFlavor);

    // 4. Retornar a string XML em um StringWriter, como esperado pelo resto do código
    StringWriter finalWriter = new StringWriter();
    finalWriter.write(xmlData);
    return finalWriter;
}
```

**Importante**: Esta abordagem elimina a necessidade de envolver manualmente o XML com a tag `<map>`, pois o mecanismo de `Transferable` já faz isso.

### Passo 3: Registrar a Nova Ação no Menu de Contexto

Este passo permanece o mesmo do plano anterior:

1.  **Adicionar Texto**: Adicionar a chave `export_branch_as_mi.text = Branch as MI` ao arquivo `freemind/Resources_en.properties`.
2.  **Adicionar Item de Menu**: Adicionar a seguinte linha dentro do submenu `<menu name="export" ...>` no arquivo `freemind/mindmap_menus.xml`:
    ```xml
    <menu_item name="export_branch_as_mi" class_name="freemind.accessories.plugins.ExportBranchAsMI"/>
    ```
3.  **Criar Arquivo de Propriedades**: Criar `freemind/accessories/plugins/ExportBranchAsMI.properties` com o conteúdo `accessories.plugins.ExportBranchAsMI.text=Branch as MI`.

## 4. Conclusão

Este novo plano é baseado em uma análise concreta do código-fonte e utiliza um mecanismo de serialização validado e existente no FreeMind. A probabilidade de sucesso na compilação e no funcionamento da funcionalidade é agora muito alta.
