# Histórico de Evolução da Funcionalidade "Colar"

Este documento consolida a sequência de implementações e refinamentos aplicados à funcionalidade de colar texto (`Ctrl+V`) no FreeMind, focando na sintaxe da metodologia Mapper Idea.

## Fase 1: Funcionalidade Base - Colar com Hierarquia

-   **Descrição:** O comportamento inicial da funcionalidade. O sistema processava um bloco de texto com múltiplas linhas, utilizando a quantidade de espaços em branco no início de cada linha (indentação) para construir uma árvore de nós hierárquicos.
-   **Análise Detalhada:** `doc/copy-paste-feature.md`

---

## Fase 2: Reconhecimento de Ícones via Atalhos

-   **Descrição:** A funcionalidade foi estendida para reconhecer uma sintaxe especial no formato `[atalho] texto do nó`. Ao colar um texto assim, o sistema passou a extrair o atalho (ex: `b`, `c`, `p`), atribuindo o ícone correspondente (`Descriptor.bean`, `Descriptor.class`, `Package`, etc.) ao nó recém-criado. O texto do nó era processado com `.trim()` para remover todos os espaços em branco ao redor.
-   **Análise Detalhada:** `doc/estrategia-paste-pattern.md`

---

## Fase 3: Preservação de Espaços em Branco para Nós Específicos

-   **Descrição:** Surgiu a necessidade de preservar a formatação de espaços em branco para o conteúdo de nós específicos, notavelmente aqueles com os atalhos `[v]` (tag_green) e `[y]` (tag_yellow). A implementação foi complexa e passou por várias etapas de planejamento e correção:
    1.  A lógica de `trim()` foi removida do início do processamento da linha.
    2.  A expressão regular foi ajustada para capturar todo o conteúdo após o atalho, incluindo os espaços.
    3.  Foi introduzida uma lógica condicional: para os atalhos `v` e `y`, o conteúdo bruto era usado como texto do nó; para todos os outros, o `.trim()` era aplicado ao conteúdo.
-   **Análise Detalhada:** A evolução do plano pode ser vista em `doc/plano-implementacao-colar-preservando-espacos.md` e `doc/correcao-plano-colar-com-preservacao.md`.

---

## Fase 4: Ajuste Fino da Preservação de Espaços (Correção de Efeito Colateral)

-   **Descrição:** A implementação da Fase 3 introduziu um efeito colateral: ao colar `[v] meu texto`, o nó era criado como `" meu texto"`, com um espaço extra no início. Isso ocorria porque a expressão regular capturava o espaço separador entre o atalho e o conteúdo.
-   **Solução:** Foi realizado um ajuste final na lógica. Para os nós `[v]` e `[y]`, o código agora verifica se o conteúdo capturado começa com um espaço. Se começar, ele remove apenas esse primeiro espaço (`substring(1)`) antes de definir o texto do nó, preservando todos os demais espaços e corrigindo o problema.
-   **Análise Detalhada:** `doc/analise-ajuste-espaco-colar-mapper-idea.md`
