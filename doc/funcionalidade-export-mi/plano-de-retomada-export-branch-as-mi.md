# Plano de Retomada: Implementação de "Export Branch as MI"

Este documento detalha o plano de ação para retomar e concluir a implementação da funcionalidade "Export Branch as MI", com base na solução definitiva encontrada após uma série de tentativas bloqueadas.

## 1. Resumo do Bloqueio

A implementação da funcionalidade a partir de um plugin externo (`accessories/plugins`) foi consistentemente bloqueada pela impossibilidade de acessar a lógica interna do FreeMind para serializar um único ramo de nós para XML. Os métodos necessários não são parte da API pública para plugins, resultando em erros de compilação intransponíveis. A conclusão é que a tarefa não pode ser finalizada sem uma modificação direcionada no código-fonte principal.

A análise completa do bloqueio está documentada em `relatorio-final-tentativas-export-branch.md`.

## 2. A Solução: Criar uma API Pública para Serialização de Ramo

A solução, proposta em `analise-solucao-export-branch-as-mi.md`, é criar um "portal" de acesso público e seguro à funcionalidade de serialização interna, em vez de tentar contornar as restrições de acesso. Isso será feito adicionando um novo método público ao `MindMapController`.

Esta abordagem é de baixo risco, reutiliza código existente e estável (a mesma lógica da ação de "Copiar") e cria uma API limpa para uso futuro.

## 3. Plano de Ação Detalhado

A implementação será dividida em duas fases principais:

### Fase 1: Modificar o Código-Fonte Principal

1.  **Arquivo a ser Modificado**: `freemind/freemind/modes/mindmapmode/MindMapController.java`.
2.  **Ação**: Adicionar o seguinte método público à classe:

    ```java
    /**
     * Serializes a single node and its descendants to an XML string, wrapped in a <map> tag.
     * This provides a public API for plugins to get the XML of a branch,
     * reusing the same internal logic as the "Copy" action.
     * @param node The MindMapNode to serialize.
     * @return A String containing the well-formed XML of the branch, or null on error.
     */
    public String getBranchXml(MindMapNode node) {
        try {
            // Reuses the exact same internal logic that the "Copy" action uses.
            java.util.Vector<MindMapNode> nodesToExport = new java.util.Vector<MindMapNode>();
            nodesToExport.add(node);
            
            freemind.modes.mindmapmode.actions.MindMapActions actions = (freemind.modes.mindmapmode.actions.MindMapActions) getActions();
            java.awt.datatransfer.Transferable transferable = actions.createTransferable(nodesToExport);
            
            // This flavor returns the data as a String containing the full XML.
            return (String) transferable.getTransferData(freemind.modes.mindmapmode.actions.MindMapActions.mindMapNodesFlavor);
        } catch (Exception e) {
            freemind.main.Resources.getInstance().logException(e);
            return null; // Return null on error.
        }
    }
    ```

### Fase 2: Implementar o Plugin `ExportBranchAsMI`

Com o novo método público disponível, a implementação do plugin se torna simples e livre de erros de compilação.

1.  **Criar a Classe de Ação**: `freemind/accessories/plugins/ExportBranchAsMI.java`.
    -   A classe irá herdar de `freemind.extensions.ExportHook`.
    -   Ela conterá a lógica para exibir o `JFileChooser` e chamar a transformação XSLT, similar à `ExportAsMI.java`.

2.  **Implementar a Lógica de Exportação**: O método principal da ação irá:
    a. Obter o nó selecionado (`MindMapNode selectedNode = getController().getSelected();`).
    b. Fazer o "cast" do controller: `MindMapController mmController = (MindMapController) getController();`.
    c. Chamar o novo método público: `String branchXml = mmController.getBranchXml(selectedNode);`.
    d. Verificar se o `branchXml` não é nulo.
    e. Passar a string `branchXml` para o motor de transformação XSLT.
    f. Realizar o pós-processamento para substituir os caracteres `§` por espaços, como já feito na `ExportAsMI`.

3.  **Registrar a Ação no Menu**:
    a. Adicionar a chave `export_branch_as_mi.text = Branch as MI` ao arquivo `freemind/Resources_en.properties`.
    b. Adicionar o item de menu ao `freemind/mindmap_menus.xml` dentro do submenu de exportação:
       ```xml
       <menu_item name="export_branch_as_mi" class_name="freemind.accessories.plugins.ExportBranchAsMI"/>
       ```
    c. Criar o arquivo `freemind/accessories/plugins/ExportBranchAsMI.properties` para o registro do texto.

## 4. Conclusão

Seguindo este plano, a funcionalidade será desbloqueada e poderá ser implementada de forma robusta e sustentável, alinhando-se com a arquitetura do FreeMind.
