# Modificações no Arquivo `freemind/plugins/ScriptingEngine.xml` para Plugins Groovy

Este documento detalha as modificações necessárias no arquivo `freemind/plugins/ScriptingEngine.xml` para registrar seus scripts Groovy como ações de menu no FreeMind.

## Contexto

O arquivo `ScriptingEngine.xml` é onde o FreeMind registra as ações relacionadas ao motor de scripts. Cada `<plugin_action>` define um item de menu ou uma ação que pode ser invocada. Para integrar seu script Groovy, você precisa adicionar um novo bloco `<plugin_action>` para cada script.

## Estrutura de um `<plugin_action>` para Scripts Groovy

Um bloco `<plugin_action>` para um script Groovy geralmente se parece com isto:

```xml
    <plugin_action
        name="NomeUnicoDoScript"
        documentation="Breve descrição da funcionalidade do script."
        label="Texto do Menu a Ser Exibido"
        base="freemind.extensions.ModeControllerHookAdapter"
        class_name="plugins.script.ScriptingEngine">

        <plugin_mode class_name="freemind.modes.mindmapmode"/>
        <!-- A localização no menu (e um identificador único para o item) -->
        <plugin_menu location="menu_bar/extras/first/scripting/identificador_unico"/>
        <!-- O caminho para o seu arquivo .groovy, relativo à pasta 'freemind/' -->
        <plugin_property name="ScriptLocation" value="accessories/seu_script.groovy" />

    </plugin_action>
```

### Pontos Importantes:
*   **`name`**: Um identificador interno único para esta ação.
*   **`documentation`**: Uma descrição mais detalhada da ação (opcional).
*   **`label`**: O texto que aparecerá para o usuário no menu. **É crucial que este `label` seja claro e diferente de outros itens no mesmo nível do menu para evitar confusão.**
*   **`class_name="plugins.script.ScriptingEngine"`**: Sempre use esta classe para executar scripts Groovy.
*   **`plugin_menu location`**: Define o caminho completo no menu onde o item aparecerá. A última parte do caminho (`identificador_unico` no exemplo) deve ser única dentro daquele sub-menu (`scripting` neste caso) para garantir que todos os seus scripts apareçam corretamente.
*   **`plugin_property name="ScriptLocation" value="..."`**: Aponta para o seu arquivo `.groovy`. O caminho deve ser relativo ao diretório `freemind/`.

## Alterações a Serem Aplicadas

Para registrar os scripts `hello_world.groovy` e `list_children.groovy` (com as correções para usar o nó selecionado), você deve adicionar os seguintes blocos `<plugin_action>` dentro da tag principal `<plugin>` do arquivo `freemind/plugins/ScriptingEngine.xml`.

```xml
    <!-- Ação para o script 'Hello World' -->
    <plugin_action
        name="HelloWorldScript"
        documentation="Executa um script de exemplo que mostra o texto do nó selecionado."
        label="Exemplo: Olá Mundo Script"
        base="freemind.extensions.ModeControllerHookAdapter"
        class_name="plugins.script.ScriptingEngine">

        <plugin_mode class_name="freemind.modes.mindmapmode"/>
        <plugin_menu location="menu_bar/extras/first/scripting/hello_world"/>
        <plugin_property name="ScriptLocation" value="accessories/hello_world.groovy" />

    </plugin_action>
    
    <!-- Ação para o script 'Listar Filhos' -->
    <plugin_action
        name="ListChildrenScript"
        documentation="Executa um script de exemplo que lista os filhos do nó selecionado."
        label="Exemplo: Listar Filhos do Nó"
        base="freemind.extensions.ModeControllerHookAdapter"
        class_name="plugins.script.ScriptingEngine">

        <plugin_mode class_name="freemind.modes.mindmapmode"/>
        <plugin_menu location="menu_bar/extras/first/scripting/list_children"/>
        <plugin_property name="ScriptLocation" value="accessories/list_children.groovy" />

    </plugin_action>
```

Após adicionar esses blocos, salve o arquivo `freemind/plugins/ScriptingEngine.xml`.

## Corrigindo o Problema do Nó Selecionado

**Atenção:** Seus scripts Groovy também precisam ser ajustados para operar no nó *selecionado* pelo usuário, e não no nó raiz. A variável `node` que é injetada diretamente no script pelo FreeMind quando a ação é disparada de um plugin via `ScriptingEngine` é, na verdade, sempre o *nó raiz do mapa*.

Para acessar o nó que o usuário realmente selecionou, você deve usar a variável `c` (que é uma instância de `MindMapController`) e chamar o método `getSelected()`.

Aqui estão os scripts Groovy corrigidos para `hello_world.groovy` e `list_children.groovy`:

### `freemind/accessories/hello_world.groovy` (Corrigido)

```groovy
import javax.swing.JOptionPane

// A variável 'c' é o MindMapController, injetado automaticamente.
// Usamos c.getSelected() para pegar o nó que o usuário selecionou.
def selectedNode = c.getSelected()

if (selectedNode != null) {
    def nodeText = selectedNode.getText()
    JOptionPane.showMessageDialog(null, "O nó selecionado é: " + nodeText)
} else {
    JOptionPane.showMessageDialog(null, "Nenhum nó selecionado.")
}
```

### `freemind/accessories/list_children.groovy` (Corrigido)

```groovy
// As variáveis 'c' (Controller) e 'node' (MindMapNode, mas é o root) são injetadas.
// Vamos ignorar a variável 'node' e usar c.getSelected() para pegar a seleção atual.
def selectedNode = c.getSelected()

if (selectedNode != null) {
    println "Executando script 'Listar Filhos' no nó: '${selectedNode.getText()}'"
    def children = selectedNode.getChildren()
    if (children.isEmpty()) {
        println "Este nó não tem filhos."
        // Opcional: Mostrar uma mensagem para o usuário
        javax.swing.JOptionPane.showMessageDialog(null, "O nó '${selectedNode.getText()}' não tem filhos.")
    } else {
        def childList = "Filhos do nó '${selectedNode.getText()}':\n"
        println "Filhos do nó:"
        children.each { child ->
            println "- ${child.getText()}"
            childList += "- ${child.getText()}\n"
        }
        // Opcional: Mostrar uma mensagem para o usuário
        javax.swing.JOptionPane.showMessageDialog(null, childList)
    }
} else {
    println "Nenhum nó selecionado."
    javax.swing.JOptionPane.showMessageDialog(null, "Por favor, selecione um nó primeiro.")
}
```

## Próximos Passos

1.  **Edite os arquivos Groovy:** Certifique-se de que seus arquivos `hello_world.groovy` e `list_children.groovy` em `freemind/accessories/` contenham o código corrigido acima.
2.  **Edite `ScriptingEngine.xml`:** Adicione os blocos `<plugin_action>` conforme descrito acima ao arquivo `freemind/plugins/ScriptingEngine.xml`.
3.  **Compile e Execute:** Execute o FreeMind com `ant run` e verifique se os dois novos itens de menu aparecem em **Ferramentas > Scripting** e funcionam corretamente no nó selecionado.
