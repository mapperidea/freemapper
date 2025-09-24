# Plano de Implementação Final: Exportar Ramo como MI

Este documento detalha o plano de ação final e corrigido para implementar a funcionalidade "Export Branch as MI", com base na análise do padrão de desenvolvimento usado na funcionalidade `ExportAsMI` existente.

## 1. Causa Raiz Final da Falha Anterior

A análise dos múltiplos erros de compilação revelou que os métodos e campos necessários para serializar um único nó para XML (como `createTransferable`, `mindMapNodesFlavor`, etc.) não são parte da API pública do FreeMind para plugins. Eles são métodos com visibilidade `protected` ou `default` (pacote), projetados para serem usados internamente pelas ações do `mindmapmode` e não são acessíveis a partir do pacote `accessories.plugins`.

## 2. A Solução: Seguir o Padrão de "Cópia de Lógica"

O documento `relatorio-implementacao-export-as-mi.md` mostra que a funcionalidade `ExportAsMI` contornou um problema semelhante de acesso a métodos privados copiando a lógica de transformação necessária para dentro de sua própria classe. 

A solução correta é aplicar este mesmo padrão: em vez de tentar acessar a API de serialização de nós, vamos **recriar uma versão simplificada dessa lógica** dentro da nossa nova classe, `ExportBranchAsMI.java`.

A análise da funcionalidade de Copiar/Colar mostra que a serialização de um nó envolve criar um documento XML `<map>` e inserir a representação do nó dentro dele. Podemos fazer isso manualmente.

## 3. Plano de Implementação Final e Detalhado

### Passo 1: Criar a Classe de Ação `ExportBranchAsMI.java`

1.  **Arquivo**: `freemind/accessories/plugins/ExportBranchAsMI.java`.
2.  **Herança**: A classe irá herdar de `freemind.extensions.ExportHook`, que fornece todos os métodos auxiliares necessários (`getController`, `getResource`, etc.).
3.  **Lógica Principal**: A classe terá a mesma estrutura de `ExportAsMI.java`, com a lógica de `JFileChooser` e de transformação XSLT.

### Passo 2: Implementar a Serialização Manual do Ramo (o Ponto Chave)

O método `getBranchXml(MindMapNode node)` será implementado com a seguinte lógica:

1.  Criar um `StringWriter` para receber o output.
2.  Chamar o método **`node.write(writer)`**. A análise aprofundada mostra que este método existe e é público. Ele serializa o nó e todos os seus filhos para o `writer`.
3.  Obter a string XML do `writer`.
4.  **Envolver manualmente a string resultante** com a tag `<map>` para compatibilidade com o XSLT, como no plano original.

**Exemplo do Código Corrigido:**
```java
private StringWriter getBranchXml(MindMapNode node) throws IOException {
    StringWriter nodeWriter = new StringWriter();
    node.write(nodeWriter); // Este método serializa o nó e seus filhos
    
    String nodeXml = nodeWriter.toString();
    // Envolve o XML do nó com a tag <map> para o XSLT
    String mapXml = "<map version=\"" + freemind.main.FreeMind.XML_VERSION + "\">";
    mapXml += nodeXml;
    mapXml += "</map>";

    StringWriter finalWriter = new StringWriter();
    finalWriter.write(mapXml);
    return finalWriter;
}
```
Esta abordagem é robusta porque `node.write()` é um método público e estável, e a manipulação da string é simples e direta.

### Passo 3: Registrar a Ação no Menu

Este passo permanece inalterado:

1.  **Criar**: `freemind/accessories/plugins/ExportBranchAsMI.properties`.
2.  **Modificar**: Adicionar a chave `export_branch_as_mi.text` em `freemind/Resources_en.properties`.
3.  **Modificar**: Adicionar o `<menu_item>` em `freemind/mindmap_menus.xml`.

## 4. Conclusão

Este plano é superior porque não depende de APIs internas e inacessíveis. Ele segue um padrão de design já presente no código (copiar/adaptar lógica quando necessário) e utiliza um método de serialização (`node.write()`) que é público e destinado a esse tipo de uso. A probabilidade de sucesso agora é máxima.
