# Criando Plugins com Groovy no FreeMind

Este documento explica como criar plugins simples para o FreeMind utilizando scripts Groovy. Estes scripts podem ser adicionados como opções no menu principal ou no menu de contexto.

O processo consiste em duas etapas principais:
1.  Escrever o script Groovy.
2.  Registrar o script como uma ação de menu através de um arquivo XML.

---

## Passo 1: Escrevendo o Script Groovy

1.  **Localização do Arquivo:** Crie seu arquivo de script com a extensão `.groovy` e coloque-o no diretório `freemind/accessories/`. Esta é a pasta padrão para acessórios e scripts externos.

2.  **Variáveis Disponíveis:** Ao ser executado a partir de um menu, o FreeMind injeta automaticamente as seguintes variáveis no escopo do seu script, que você pode usar diretamente:
    *   `node`: Uma instância de `freemind.modes.MindMapNode`, que representa o nó atualmente selecionado no mapa mental. Você pode usar isso para ler texto, atributos, ou navegar pela hierarquia (nós pais, filhos, etc.).
    *   `c`: Uma instância de `freemind.modes.mindmapmode.MindMapController`. Este é um objeto poderoso que lhe dá acesso a quase todas as funcionalidades do FreeMind, como modificar o mapa, abrir diálogos, alterar a seleção e muito mais.

### Exemplo de Script: "Olá Mundo"

Vamos criar um script simples que exibe uma caixa de diálogo com o texto do nó selecionado.

Crie o arquivo `freemind/accessories/hello_world.groovy` com o seguinte conteúdo:

```groovy
import javax.swing.JOptionPane

// A variável 'node' é injetada automaticamente pelo FreeMind.
// Ela representa o nó selecionado no momento em que o script é executado.
if (node != null) {
    def nodeText = node.getText()
    JOptionPane.showMessageDialog(null, "O nó selecionado é: " + nodeText)
} else {
    JOptionPane.showMessageDialog(null, "Nenhum nó selecionado.")
}
```

---

## Passo 2: Integrando o Script ao Menu

Para que o script apareça como uma opção de menu, você precisa registrá-lo como uma `<plugin_action>`. A maneira mais simples de fazer isso é adicionando a definição ao arquivo de configuração do plugin de script existente.

1.  **Abra o arquivo:** `freemind/plugins/ScriptingEngine.xml`.

2.  **Adicione a Ação:** Localize o final do arquivo, antes da tag de fechamento `</plugin>`. Você verá uma seção comentada que serve como um modelo. Use-a para adicionar sua própria ação.

    Adicione o seguinte bloco XML dentro da tag `<plugin>`:

    ```xml
    <!-- Ação para o script 'Hello World' -->
    <plugin_action
        name="HelloWorldScript"
        documentation="Executa um script de exemplo que mostra o texto do nó."
        label="Exemplo: Olá Mundo Script"
        base="freemind.extensions.ModeControllerHookAdapter"
        class_name="plugins.script.ScriptingEngine">

        <plugin_mode class_name="freemind.modes.mindmapmode"/>
        <!-- Localização no menu: Ferramentas -> Scripting -> (Seu Script) -->
        <plugin_menu location="menu_bar/extras/first/scripting/hello_world"/>
        <!-- Caminho para o seu script, relativo à pasta 'freemind/' -->
        <plugin_property name="ScriptLocation" value="accessories/hello_world.groovy" />

    </plugin_action>
    ```

### Detalhes da Configuração:
*   `label`: O texto que aparecerá no menu.
*   `location`: Define a hierarquia do menu. `menu_bar/extras/first/scripting/hello_world` coloca o item no menu "Ferramentas" (Extras), em uma subcategoria "Scripting".
*   `name="ScriptLocation"`: Esta propriedade é fundamental. Seu `value` deve ser o caminho para o seu arquivo `.groovy` a partir do diretório `freemind/`.

---

## Passo 3: Testando o Plugin

1.  **Compile e Execute o FreeMind:** Abra um terminal no diretório `freemind/` e execute o comando:
    ```bash
    ant run
    ```
2.  **Verifique o Menu:** Após o FreeMind iniciar, abra um mapa mental qualquer. Vá para o menu **Ferramentas > Scripting**. Você deverá ver uma nova opção chamada **"Exemplo: Olá Mundo Script"**.
3.  **Execute a Ação:** Selecione um nó no seu mapa mental e clique nesta opção de menu. Uma caixa de diálogo deve aparecer com o texto do nó que você selecionou.

## Exemplo Avançado: Listar Filhos do Nó

Este script itera sobre todos os filhos diretos do nó selecionado e imprime seus textos no console.

**Arquivo: `freemind/accessories/list_children.groovy`**
```groovy
// As variáveis 'c' (Controller) e 'node' (MindMapNode) são injetadas.
if (node != null) {
    println "Executando script 'Listar Filhos' no nó: '${node.getText()}'"
    def children = node.getChildren()
    if (children.isEmpty()) {
        println "Este nó não tem filhos."
    } else {
        println "Filhos do nó:"
        children.each { child ->
            println "- ${child.getText()}"
        }
    }
}
```

Para adicionar este script ao menu, repita o **Passo 2**, criando outra entrada `<plugin_action>` em `ScriptingEngine.xml` com o `label` e `ScriptLocation` apropriados.
