# Relatório de Correção: Atalho Global para "Exportar Ramo para Área de Transferência"

Este documento detalha a investigação e a solução definitiva para o bug que impedia o funcionamento do atalho `Alt + Shift + C`.

## 1. Problema Reportado

A funcionalidade "Exportar Ramo para Área de Transferência" funcionava corretamente quando acionada pelo menu de contexto (clique com o botão direito no nó). No entanto, o atalho global `Alt + Shift + C`, que deveria executar a mesma ação, não produzia nenhum efeito.

## 2. Análise da Causa Raiz

A investigação passou por várias etapas:

1.  **Hipótese Inicial (Incorreta):** A suspeita inicial era de que a lógica *dentro* da ação estava falhando ao tentar obter o nó selecionado no contexto de um atalho global.

2.  **Depuração:** Foram feitas várias tentativas de corrigir a lógica de obtenção do nó, que não funcionaram. Uma tentativa de adicionar uma caixa de diálogo para depuração revelou a verdadeira causa.

3.  **Causa Definitiva:** O teste de depuração demonstrou que, ao usar o atalho, a ação `actionPerformed` não estava sendo chamada. O problema não era a lógica interna, mas sim que o **atalho não estava sendo registrado globalmente pela aplicação**.

4.  **Análise Comparativa:** Ao comparar a definição da ação defeituosa com outras ações que possuíam atalhos globais funcionais (como "Recortar"), foi identificado que as ações funcionais estavam declaradas na estrutura da barra de menus principal no arquivo `mindmap_menus.xml`. A ação `exportBranchAsMIToClipboard` estava definida apenas na seção do menu de popup (`mindmapmode_popup`), o que se mostrou insuficiente para o registro de um atalho global.

## 3. Solução Implementada

A correção foi realizada em duas etapas para resolver o problema de registro e limpar o código:

1.  **Modificação do `mindmap_menus.xml`:**
    *   Uma nova tag `<menu_action>` para o `field="exportBranchAsMIToClipboard"` foi adicionada à estrutura da barra de menus principal, especificamente em `menu_bar/file/export`.
    *   Essa adição forçou o sistema de menus a registrar o `key_ref` associado como um atalho global na inicialização da aplicação, espelhando o comportamento de outras ações globais.

2.  **Limpeza do `MindMapController.java`:**
    *   Todo o código de depuração temporário (caixas de diálogo) foi removido da classe interna `ExportBranchAsMIToClipboardAction`.
    *   A lógica final da ação foi estabelecida para usar `getView().getSelectedNodesSortedByY()`, que é a forma mais robusta de obter a seleção de nós, e verificar se exatamente um nó foi selecionado, garantindo o comportamento esperado.

## 4. Resultado

Após as correções, o atalho `Alt + Shift + C` passou a invocar a ação corretamente. A lógica interna, por sua vez, consegue identificar o nó selecionado e copiar o ramo correspondente para a área de transferência, resolvendo o bug de forma completa e definitiva.
