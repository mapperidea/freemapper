# Plano de Ação Ajustado: Colar com Preservação de Espaços para Ícones Específicos

Este documento descreve a estratégia de implementação refinada para a funcionalidade de colar texto, com base nos últimos requisitos fornecidos.

## 1. Objetivo

Modificar a funcionalidade de colar texto (`Ctrl+V`) para que, ao processar linhas que contenham os atalhos de ícone `[v]` (tag_green) ou `[y]` (tag_yellow), todo o espaço em branco (whitespace) após o marcador do atalho seja preservado como parte do conteúdo do nó.

Para todas as outras linhas — seja com outros atalhos de ícone (ex: `[b]`, `[c]`) ou sem atalhos — o comportamento padrão de remover os espaços em branco do início e do fim do texto (`trim()`) deve ser mantido.

## 2. Descarte da Abordagem de Normalização Global

A ideia inicial de "normalizar" a indentação de todo o bloco de texto colado foi **descartada**. Essa abordagem entraria em conflito direto com o requisito de preservar espaços em branco para os nós `[v]` e `[y]`, que podem conter texto pré-formatado onde os espaços iniciais são significativos.

## 3. Nova Estratégia de Implementação

A implementação será focada e cirúrgica, alterando apenas a lógica de extração de texto dentro do método `pasteStringWithoutRedisplay`.

-   **Arquivo a ser Modificado:** `freemind/modes/mindmapmode/actions/PasteAction.java`
-   **Método a ser Modificado:** `pasteStringWithoutRedisplay`

### Lógica Detalhada

Dentro do loop que itera sobre cada linha do texto colado, a lógica será a seguinte:

1.  A linha de texto será limpa de espaços no início e no fim (`line.trim()`) para se obter uma `trimmedLine`.
2.  O padrão de expressão regular (`mapperIdeaPattern`) será aplicado à `trimmedLine` para detectar a presença de um atalho de ícone.
3.  Se um atalho for encontrado, o código executará uma nova verificação condicional:

    -   **CASO A: O atalho é `v` ou `y`**
        -   `if (shortcut.equals("v") || shortcut.equals("y"))`
        -   O texto do nó será o resultado do `matcher.group(2)`, que captura todo o conteúdo após o marcador `[atalho] `.
        -   Crucialmente, o método `.trim()` **não será** chamado para este grupo, preservando assim todos os espaços em branco contidos nele.

    -   **CASO B: O atalho é qualquer outro**
        -   O texto do nó será o resultado de `matcher.group(2).trim()`, mantendo o comportamento atual de remover espaços em branco para todos os outros nós com ícones.

4.  Se nenhum atalho de ícone for encontrado na linha, o comportamento existente de usar a `trimmedLine` como texto do nó será mantido.

## 4. Exemplo do Fluxo de Execução

| Linha de Texto Original | Atalho Detectado | Lógica Aplicada | Texto Final do Nó |
| :--- | :--- | :--- | :--- |
| `    [v]   Conteúdo com espaços. ` | `v` | CASO A (Preservar) | `"   Conteúdo com espaços. "` |
| `  [y]  Outro conteúdo.   ` | `y` | CASO A (Preservar) | `"  Outro conteúdo.   "` |
| `    [b]   Nome da Classe   ` | `b` | CASO B (Trim) | `"Nome da Classe"` |
| `    Texto simples   ` | Nenhum | Padrão | `"Texto simples"` |

## 5. Conclusão

Esta abordagem implementa o requisito de forma precisa e segura, adicionando a exceção para os ícones `[v]` e `[y]` sem causar regressões no comportamento esperado para os demais nós. A lógica permanece clara e a manutenção é simples.
