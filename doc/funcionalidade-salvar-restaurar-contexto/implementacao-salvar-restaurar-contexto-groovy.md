# Implementação: Funcionalidade Salvar e Restaurar Contexto via Script Groovy

Este documento detalha o código e os locais de inserção para a implementação da funcionalidade de "Salvar e Restaurar Contexto" utilizando um script Groovy, conforme o plano de análise.

## 1. Código do Script Groovy

**Caminho:** `freemind/accessories/save_restore_context.groovy`

```groovy
import freemind.main.Resources
import freemind.modes.MindMapNode
import freemind.modes.ModeController // Para o getNodeFromID
import javax.swing.JOptionPane
import java.util.ArrayList // Para o displayNode, se necessário

// A variável 'c' (MindMapController) é injetada automaticamente.
// Ela estende ModeController, então pode ser usada para getNodeFromID.

def props = Resources.getInstance().getProperties()
def savedNodeId = props.getProperty("mapperidea.temp.savedNodeId")
def savedNodeMapPath = props.getProperty("mapperidea.temp.savedNodeMapPath") // Caminho do mapa onde o nó foi salvo
def currentMapPath = c.getMap().getFile() != null ? c.getMap().getFile().getAbsolutePath() : null

// Verifica se o contexto foi salvo em outro mapa
if (savedNodeId && savedNodeMapPath && currentMapPath && !savedNodeMapPath.equals(currentMapPath)) {
    c.getFrame().out("Contexto salvo em outro mapa: " + savedNodeMapPath + ". Limpando contexto.")
    props.remove("mapperidea.temp.savedNodeId")
    props.remove("mapperidea.temp.savedNodeMapPath")
    savedNodeId = null
}

if (savedNodeId) {
    // --- ESTADO 2: CONTEXTO SALVO, RESTAURAR ---
    
    // Tenta obter o nó usando o ID
    MindMapNode nodeToRestore = c.getNodeFromID(savedNodeId)
    
    if (nodeToRestore != null) {
        // Assegura que o nó esteja visível (desdobrando o caminho até ele)
        // c.displayNode(node) já faz isso e centraliza
        c.displayNode(nodeToRestore)
        c.centerNode(nodeToRestore) // Centraliza o nó na tela
        c.getFrame().out("Contexto restaurado para: " + nodeToRestore.getPlainTextContent())
    } else {
        c.getFrame().out("Nó salvo não encontrado no mapa atual. O contexto foi limpo.")
    }
    
    // Limpa o estado para a próxima execução
    props.remove("mapperidea.temp.savedNodeId")
    props.remove("mapperidea.temp.savedNodeMapPath")

} else {
    // --- ESTADO 1: NENHUM CONTEXTO, SALVAR ---
    
    MindMapNode selectedNode = c.getSelected()
    if (selectedNode != null) {
        String nodeId = selectedNode.getObjectId(c)
        String nodeText = selectedNode.getPlainTextContent()
        
        if (nodeId != null && !nodeId.isEmpty()) {
            // Salva o ID do nó e o caminho do mapa nas propriedades do usuário
            props.setProperty("mapperidea.temp.savedNodeId", nodeId)
            props.setProperty("mapperidea.temp.savedNodeMapPath", currentMapPath != null ? currentMapPath : "unnamed_map")
            
            // Exibe feedback na barra de status
            String statusMessage = "Contexto salvo: " + (nodeText.length() > 50 ? nodeText.substring(0, 50) + "..." : nodeText)
            c.getFrame().out(statusMessage)
        } else {
             c.getFrame().out("Não foi possível obter um ID único para o nó selecionado. Contexto não salvo.")
        }
    } else {
        c.getFrame().out("Nenhum nó selecionado para salvar o contexto.")
    }
}
```

## 2. Registro do Script no FreeMind

Para integrar o script ao FreeMind, são necessárias alterações em dois arquivos XML e um arquivo de propriedades.

### 2.1. Registro no `ScriptingEngine.xml`

**Caminho:** `freemind/plugins/ScriptingEngine.xml`

**Ação:** Adicione o seguinte bloco `<plugin_action>` **dentro da tag principal `<plugin>`** (geralmente antes da tag de fechamento `</plugin>`)

```xml
    <!-- Ação para o script 'Salvar/Restaurar Contexto' -->
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

### 2.2. Definição do Atalho de Teclado

**Caminho:** `freemind/freemind.properties`

**Ação:** Adicione a seguinte linha ao final do arquivo:

```properties
keystroke_scripting_save_restore_context=ctrl J
```

### 2.3. Associação do Atalho ao Menu (para registro global)

**Caminho:** `freemind/mindmap_menus.xml`

**Ação:** Para garantir que o atalho seja registrado globalmente, adicione a `menu_action` correspondente ao local desejado no menu. Sugere-se adicionar sob o menu "Ferramentas" (`extras`).

Localize o bloco referente ao menu `extras` e adicione a linha da ação. Por exemplo, você pode adicioná-lo perto de outras ações de script ou de navegação.

Exemplo de onde adicionar (a localização exata pode variar, mas o importante é estar dentro de um `<menu>` existente):

```xml
<menu name="extras">
    <!-- Outros itens do menu extras -->
    <menu name="first">
        <menu name="scripting">
            <!-- Outros itens do menu scripting -->
            <menu_action field="accessories.plugins.script.ScriptingEngine.SaveRestoreContextScript" name="save_restore_context" />
        </menu>
    </menu>
</menu>
```

**Observação:** O `field` na `menu_action` deve apontar para a classe do ScriptingEngine e o `name` da sua `plugin_action` definida em `ScriptingEngine.xml`. Neste caso, `accessories.plugins.script.ScriptingEngine.SaveRestoreContextScript` é o caminho completo que o FreeMind espera. O `name` é `save_restore_context` que se alinha com a `location` do `plugin_menu`.

## 3. Próximos Passos

1.  **Crie o arquivo Groovy** com o conteúdo fornecido.
2.  **Modifique o `ScriptingEngine.xml`** adicionando o bloco XML.
3.  **Adicione a propriedade** em `freemind.properties`.
4.  **Adicione a entrada em `mindmap_menus.xml`**.
5.  **Compile o projeto:** Execute `ant dist` no diretório `freemind/`.
6.  **Execute o FreeMind:** Execute `ant run` ou use o script `freemind.sh`/`freemind.bat` da pasta `dist`.
7.  **Teste a funcionalidade:**
    *   Selecione um nó.
    *   Pressione `Ctrl+J`. Verifique a mensagem na barra de status.
    *   Navegue para outro nó.
    *   Pressione `Ctrl+J` novamente. Verifique se ele retorna ao nó salvo.
    *   Tente salvar em um mapa, fechar e abrir outro mapa, e pressionar `Ctrl+J` para ver a mensagem de contexto salvo em outro mapa.
```