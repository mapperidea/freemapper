# Plano de Melhoria para a Seleção de Ícones

Este documento descreve a análise e o plano de implementação para reintroduzir a funcionalidade de remoção de ícones usando as teclas `Backspace` e `Delete` dentro do diálogo de seleção de ícones, garantindo a coexistência com a funcionalidade de busca.

## 1. Objetivo

O objetivo é restaurar a capacidade do usuário de remover ícones do nó selecionado diretamente do pop-up de seleção de ícones, sem quebrar a funcionalidade de busca por nome, implementada recentemente.

As funcionalidades a serem restauradas são:
-   **`Delete`**: Remover **todos** os ícones do(s) nó(s) selecionado(s).
-   **`Backspace`**: Remover o **último** ícone adicionado do(s) nó(s) selecionado(s).

## 2. Análise do Código Atual

Conforme a análise no documento `doc-relativa-aos-icones.md`, a classe `freemind.modes.common.dialogs.IconSelectionPopupDialog.java` é a responsável por gerenciar toda a interação do usuário no pop-up.

O método chave é `keyPressed(KeyEvent keyEvent)`. Atualmente, sua lógica é a seguinte:

1.  **Modo de Busca (ativado com `/`)**:
    -   Se a flag `isCapturingSearchText` for `true`, o método prioriza a manipulação da string de busca (`searchText`).
    -   Neste modo, a tecla `Backspace` é usada exclusivamente para apagar o último caractere da `searchText`.
    -   `Enter` finaliza a busca e `Escape` cancela.

2.  **Modo de Navegação**:
    -   Se a busca não estiver ativa, as teclas de seta navegam pela grade de ícones.
    -   `Enter` ou `Espaço` confirmam a seleção do ícone destacado.
    -   `Escape` fecha o diálogo.

O conflito principal é que a tecla `Backspace` foi completamente capturada pela lógica de busca, impedindo seu uso para a remoção de ícones.

## 3. Estratégia de Implementação

A implementação será focada em modificar o método `keyPressed` em `IconSelectionPopupDialog.java` para acomodar as novas ações, tratando a tecla `Backspace` de forma condicional.

### Passo 1: Identificar as Ações de Remoção

A classe `IconSelectionPlugin` já passa as ações necessárias para o diálogo. Elas estão contidas no `Vector actions` que é passado ao construtor do `IconSelectionPopupDialog`. As ações relevantes são:

-   `controller.removeLastIconAction` (instância de `freemind.modes.mindmapmode.actions.RemoveIconAction`)
-   `controller.removeAllIconsAction` (instância de `freemind.modes.mindmapmode.actions.RemoveAllIconsAction`)

Precisaremos iterar sobre o vetor `icons` (que armazena as ações) para encontrar as instâncias dessas duas classes e acioná-las quando as teclas correspondentes forem pressionadas.

### Passo 2: Modificar o Método `keyPressed`

A lógica dentro do `keyPressed` será reestruturada para o seguinte fluxo:

```java
public void keyPressed(KeyEvent keyEvent) {
    // 1. Lógica de busca (quando isCapturingSearchText == true)
    if (isCapturingSearchText) {
        // ... (código de busca existente)
        // A única mudança aqui é que o Backspace só deve funcionar se houver texto na busca.
        // Se o texto estiver vazio, o Backspace deve executar a remoção do último ícone.
        if (keyEvent.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (!searchText.isEmpty()) {
                // Comportamento atual: edita o texto da busca.
                searchText = searchText.substring(0, searchText.length() - 1);
                filterIcons(searchText);
            } else {
                // Novo comportamento: Se a busca está vazia, aciona a remoção do último ícone.
                isCapturingSearchText = false; // Sai do modo de busca
                searchLabel.setText("Use / to search");
                triggerRemoveLastIconAction(keyEvent.getModifiers());
            }
            keyEvent.consume();
            return;
        }
        // ... (resto da lógica de busca)
        return; // Garante que o resto do método não seja executado
    }

    // 2. Lógica de navegação e outras ações (quando a busca não está ativa)
    switch (keyEvent.getKeyCode()) {
        case KeyEvent.VK_DELETE:
            // NOVO: Aciona a remoção de todos os ícones.
            triggerRemoveAllIconsAction(keyEvent.getModifiers());
            keyEvent.consume();
            break;

        case KeyEvent.VK_BACK_SPACE:
            // NOVO: Aciona a remoção do último ícone (só será atingido se a busca não estiver ativa).
            triggerRemoveLastIconAction(keyEvent.getModifiers());
            keyEvent.consume();
            break;

        // ... (cases existentes para setas, Enter, Espaço, Escape)
    }

    // ... (lógica existente para atalhos de ícones individuais)
}
```

### Passo 3: Criar Métodos para Acionar as Ações

Dois novos métodos auxiliares serão criados dentro de `IconSelectionPopupDialog.java` para encontrar e executar as ações de remoção.

```java
private void triggerRemoveLastIconAction(int modifiers) {
    for (int i = 0; i < icons.size(); i++) {
        Action action = (Action) icons.get(i);
        if (action instanceof freemind.modes.mindmapmode.actions.RemoveIconAction) {
            result = i;
            mModifiers = modifiers;
            this.dispose(); // Fecha o diálogo e sinaliza que a ação foi selecionada
            return;
        }
    }
}

private void triggerRemoveAllIconsAction(int modifiers) {
    for (int i = 0; i < icons.size(); i++) {
        Action action = (Action) icons.get(i);
        if (action instanceof freemind.modes.mindmapmode.actions.RemoveAllIconsAction) {
            result = i;
            mModifiers = modifiers;
            this.dispose();
            return;
        }
    }
}
```

## 4. Validação

Após a implementação, os seguintes cenários de teste deverão ser executados manualmente:

1.  **Remoção com `Delete`**: Abrir o pop-up (`Alt+I`) e pressionar `Delete`. Verificar se todos os ícones do nó selecionado são removidos.
2.  **Remoção com `Backspace` (sem busca)**: Abrir o pop-up e pressionar `Backspace`. Verificar se o último ícone do nó é removido.
3.  **Busca e `Backspace`**: Abrir o pop-up, pressionar `/`, digitar um termo de busca (ex: "go"). Pressionar `Backspace` e verificar se o termo de busca é editado (ex: "g").
4.  **Busca Vazia e `Backspace`**: Abrir o pop-up, pressionar `/`, digitar algo e apagar com `Backspace` até o campo de busca ficar vazio. Pressionar `Backspace` novamente e verificar se o último ícone do nó é removido.
5.  **Funcionalidades Existentes**: Garantir que a navegação com setas, a seleção com `Enter`/`Espaço` e os atalhos diretos de ícones continuam funcionando como esperado.