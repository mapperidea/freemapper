# Análise e Plano de Ação: Remover Atualização de Versionamento ao Expandir/Recolher Nós

Este documento detalha a análise e o plano de implementação para impedir que os arquivos de mapa mental (`.mm`) sejam marcados como modificados simplesmente por um nó ser expandido ou recolhido.

## 1. Problema Reportado

Ao trabalhar com mapas mentais em um sistema de controle de versão (como Git), qualquer ação de expandir ou recolher um nó (folding/unfolding) altera o atributo `MODIFIED` (um timestamp) do nó no arquivo `.mm`. Isso gera "ruído" nos commits, pois o arquivo é marcado como modificado mesmo que nenhum conteúdo real tenha sido alterado, levando a conflitos de merge desnecessários e um histórico de versões poluído.

O objetivo é desabilitar este comportamento para que a ação de expandir/recolher não seja considerada uma modificação que precise ser salva.

## 2. Análise da Causa Raiz

A investigação do código-fonte revelou um mecanismo de controle explícito para este comportamento.

1.  **Ponto de Execução**: A lógica que atualiza o timestamp é acionada dentro da classe `freemind.modes.mindmapmode.actions.ToggleFoldedAction.java`.
2.  **Condição de Controle**: O método `act` nesta classe contém o seguinte bloco de código:
    ```java
    if (Resources.getInstance().getBoolProperty(
            FreeMind.RESOURCES_SAVE_FOLDING_STATE)) {
        modeController.nodeChanged(node);
    }
    ```
3.  **Gatilho da Modificação**: O método `modeController.nodeChanged(node)` é o responsável por atualizar o atributo `MODIFIED` do nó e marcar o mapa como "sujo" (necessitando ser salvo).
4.  **Propriedade de Configuração**: A execução desse bloco é controlada pela propriedade booleana `FreeMind.RESOURCES_SAVE_FOLDING_STATE`, cuja chave de texto é `resources_save_folding_state`.
5.  **Valor Padrão**: Uma verificação no arquivo `freemind/freemind.properties` confirma que o valor padrão para esta propriedade é `true`:
    ```properties
    resources_save_folding_state=true
    ```

**Conclusão:** O comportamento não é um bug, mas uma funcionalidade configurável que está habilitada por padrão. A solução é simplesmente alterar o valor dessa propriedade.

## 3. Plano de Implementação

A solução consiste em uma única alteração no arquivo de configuração principal da aplicação, sem a necessidade de modificar o código-fonte Java.

-   **Arquivo a ser Modificado**: `freemind/freemind.properties`
-   **Ação a ser Realizada**: Alterar o valor da propriedade `resources_save_folding_state` de `true` para `false`.

### Passo de Implementação (Para Execução Futura)

1.  **Modificar o Arquivo de Propriedades**:
    -   Utilizar a ferramenta `replace` para alterar a linha específica no arquivo `freemind.properties`.
    -   **Comando Planejado**:
        ```
        replace(
            file_path: "/mnt/disk2/workspace/mapperidea/freemapper2/freemapper/freemind/freemind.properties",
            old_string: "resources_save_folding_state=true",
            new_string: "resources_save_folding_state=false",
            instruction: "In freemind.properties, change the value of resources_save_folding_state from true to false to prevent folding actions from marking the map as modified."
        )
        ```

2.  **Validação**:
    -   Após a alteração, compilar o projeto usando `ant -f freemind/build.xml dist`.
    -   Executar a aplicação com `ant -f freemind/build.xml run`.
    -   Abrir um mapa mental, expandir ou recolher um nó, salvar o mapa, fechá-lo e reabri-lo. O timestamp do nó não deve ter sido alterado pela ação de expandir/recolher.

## 4. Conclusão da Análise

A alteração proposta é segura, de baixo risco e atende precisamente ao requisito, desativando a atualização de versionamento para ações de folding sem afetar outras modificações legítimas no mapa.
