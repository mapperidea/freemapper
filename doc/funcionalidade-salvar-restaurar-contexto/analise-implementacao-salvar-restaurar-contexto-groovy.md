# Análise de Implementação via Script Groovy: Funcionalidade de Salvar e Restaurar Contexto

Este documento detalha uma abordagem alternativa para a implementação da funcionalidade de salvar e restaurar contexto, utilizando um script Groovy em vez de modificar o código-fonte Java. Esta análise é baseada na arquitetura de plugins do FreeMind, exemplificada pela funcionalidade existente `Alt + Shift + G` (`mapper_idea_map_complete.groovy`).

## 1. Viabilidade da Abordagem com Groovy

A análise dos documentos de arquitetura (`plugins-groovy.md`, `xml-menu-modifications.md`) e do script `mapper_idea_map_complete.groovy` confirma que a implementação via script é **altamente viável**.

-   **Acesso ao Controller**: Scripts executados pelo `ScriptingEngine` recebem uma instância do `MindMapController` (variável `c`). Isso concede ao script acesso a métodos cruciais como `c.getSelected()`, `c.centerNode(node)`, `c.getNodeFromID(id)`, `c.getFrame().out(message)`, etc.
-   **Modificação do Mapa**: Os scripts podem criar nós, alterar seus textos e adicionar ícones, demonstrando capacidade de escrita.
-   **Baixo Acoplamento**: A criação de um script externo evita a necessidade de recompilar o núcleo da aplicação para cada alteração, tornando o desenvolvimento e a manutenção mais ágeis e seguros.

O principal desafio técnico a ser resolvido é a **gestão de estado**, uma vez que um script é, por natureza, executado e finalizado, perdendo seu estado interno. A solução proposta é utilizar o sistema de propriedades do FreeMind para persistir o contexto entre as execuções.

## 2. Plano de Implementação Proposto

### Passo 1: Criar o Script Groovy

1.  **Arquivo**: `freemind/accessories/save_restore_context.groovy`
2.  **Lógica Principal**: O script implementará uma máquina de estados simples, lendo e escrevendo em propriedades para determinar a ação a ser tomada.

    **Pseudo-código do Script:**
    ```groovy
    import freemind.main.Resources
    import freemind.modes.MindMapNode

    // O controller 'c' é injetado automaticamente
    def props = Resources.getInstance().getProperties()
    def savedNodeId = props.getProperty("mapperidea.temp.savedNodeId")

    if (savedNodeId) {
        // --- ESTADO 2: CONTEXTO SALVO, RESTAURAR ---
        
        // Limpa o estado para a próxima execução
        props.remove("mapperidea.temp.savedNodeId")
        
        MindMapNode nodeToRestore = c.getNodeFromID(savedNodeId)
        
        if (nodeToRestore != null) {
            // Lógica para garantir que o nó esteja visível
            ArrayList<MindMapNode> nodesToUnfold = new ArrayList<MindMapNode>()
            def findAction = c.find // Reutiliza a instância da FindAction
            findAction.displayNode(nodeToRestore, nodesToUnfold)
            
            // Centraliza o nó na tela
            c.centerNode(nodeToRestore)
            c.getFrame().out("Contexto restaurado.")
        } else {
            c.getFrame().out("Nó salvo não encontrado. O contexto foi limpo.")
        }

    } else {
        // --- ESTADO 1: NENHUM CONTEXTO, SALVAR ---
        
        MindMapNode selectedNode = c.getSelected()
        if (selectedNode != null) {
            String nodeId = selectedNode.getObjectId(c)
            String nodeText = selectedNode.getPlainTextContent()
            
            // Salva o ID do nó nas propriedades do usuário
            props.setProperty("mapperidea.temp.savedNodeId", nodeId)
            
            // Exibe feedback na barra de status
            String statusMessage = "Contexto salvo: " + (nodeText.length() > 50 ? nodeText.substring(0, 50) + "..." : nodeText)
            c.getFrame().out(statusMessage)
        } else {
            c.getFrame().out("Nenhum nó selecionado para salvar o contexto.")
        }
    }
    ```

### Passo 2: Registrar o Script e Criar o Atalho

O registro seguirá o padrão do FreeMind para plugins de script.

1.  **Registrar a Ação (XML):**
    -   **Arquivo**: `freemind/plugins/ScriptingEngine.xml`
    -   **Ação**: Adicionar um novo bloco `<plugin_action>` que define a ação e aponta para o script.

    ```xml
    <plugin_action
        name="SaveRestoreContextScript"
        documentation="Salva a posição de um nó e permite retornar a ele posteriormente."
        label="Mapper Idea: Salvar/Restaurar Contexto"
        base="freemind.extensions.ModeControllerHookAdapter"
        class_name="plugins.script.ScriptingEngine">

        <plugin_mode class_name="freemind.modes.mindmapmode"/>
        <plugin_menu location="menu_bar/extras/first/scripting/save_restore_context"/>
        <plugin_property name="ScriptLocation" value="accessories/save_restore_context.groovy" />
    </plugin_action>
    ```

2.  **Definir o Atalho de Teclado:**
    -   **Arquivo**: `freemind/freemind.properties`
    -   **Ação**: Adicionar uma nova propriedade de atalho. A combinação `Alt+Shift+J` é uma boa candidata, pois parece não estar em uso.

    ```properties
    keystroke_scripting_save_restore_context = alt shift J
    ```

3.  **Associar o Atalho à Ação (XML):**
    -   **Arquivo**: `freemind/mindmap_menus.xml`
    -   **Ação**: Para registrar um atalho global de forma confiável (sem depender apenas da estrutura de plugins), é mais seguro associá-lo diretamente na definição do menu principal. O `ScriptingEngine.xml` é lido, mas a definição do atalho aqui garante o carregamento global.
    -   Adicionar a ação a um menu apropriado (ex: "Ferramentas" ou "Navegar"), referenciando a ação definida no `ScriptingEngine.xml` e o atalho.
        *Nota: Uma análise mais aprofundada pode ser necessária para determinar o melhor local para registrar a ação para um atalho global. Se a abordagem via `ScriptingEngine.xml` se mostrar insuficiente, o registro deve ser movido para `mindmap_menus.xml`, invocando a classe `plugins.script.ScriptingEngine` com a propriedade `ScriptLocation`.*

## 3. Comparativo de Abordagens

| Critério | Implementação Java | Implementação Groovy |
| :--- | :--- | :--- |
| **Complexidade** | Alta. Exige modificar o `MindMapController.java`, uma classe grande e central. | Média. A lógica é autocontida no script. O desafio é a gestão de estado. |
| **Risco de Regressão** | Médio. Qualquer erro na classe `MindMapController` pode impactar toda a aplicação. | Baixo. O script é isolado. Um erro no script não afeta o núcleo da aplicação. |
| **Manutenção** | Mais difícil. Requer compilação completa do projeto para cada alteração. | Fácil. Basta editar o arquivo `.groovy`. As alterações são aplicadas na próxima execução. |
| **Desempenho** | Ligeiramente superior (código nativo compilado). | Ligeiramente inferior (interpretação do script), mas desprezível para esta tarefa. |

## 4. Conclusão

A abordagem via script Groovy é superior para esta funcionalidade. Ela oferece um equilíbrio ideal entre poder e segurança, permitindo implementar a lógica complexa de salvar/restaurar contexto sem os riscos associados à modificação de classes centrais do sistema. A facilidade de manutenção e a agilidade no desenvolvimento são vantagens decisivas.
