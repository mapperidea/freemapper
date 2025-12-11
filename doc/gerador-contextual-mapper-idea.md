# Documentação: Gerador Contextual para Mapper Idea

Este documento descreve a funcionalidade do "Gerador de Filhos Contextual", um script Groovy customizado para acelerar a criação de mapas de arquitetura do Mapper Idea dentro do FreeMind.

## O que é o Gerador Contextual?

É uma ferramenta de automação que, ao ser executada em um nó selecionado, analisa o texto deste nó e cria automaticamente os nós filhos mais prováveis, seguindo a gramática e a estrutura padrão do Mapper Idea.

Isso evita a digitação repetitiva de termos comuns da arquitetura, como `generators`, `parameters`, `start`, `body`, etc., e aplica os ícones corretos para "elementos" e "valores".

## Como Usar

Existem duas maneiras de ativar o gerador no nó que você selecionou:

1.  **Atalho de Teclado (Recomendado):**
    *   Selecione o nó desejado no seu mapa mental.
    *   Pressione `Control + Shift + G`.

2.  **Menu de Ferramentas:**
    *   Selecione o nó desejado.
    *   Navegue até o menu **Ferramentas > Scripting > Mapper Idea: Gerador Contextual** e clique.

O script então criará os filhos apropriados para o nó selecionado, caso ainda não existam.

## Palavras-Chave e Autocompletar

A seguir está a lista de todas as palavras-chave (texto do nó pai) que acionam a geração automática de nós filhos.

**Legenda de Ícones:**
*   `[e]` - Ícone de **Elemento** (`element`): Representa uma tag ou um nó estrutural.
*   `[v]` - Ícone de **Valor** (`tag_green`): Representa o valor ou conteúdo de uma tag.

---

### Nível Raiz e Estrutura Principal

*   `config`
    *   Gera filho: `mapperidea` [e]

*   `mapperidea`
    *   Gera filho: `generators` [e]
    *   Gera filho: `injectables` [e]
    *   Gera filho: `maps` [e]
    *   Gera filho: `fragments` [e]

---

### Estrutura de Geradores (Generators)

*   `generators`
    *   Gera filho: `meuGerador` [e] (um nome genérico para você renomear)

*   Qualquer nó filho de `generators` (ex: `meuGerador`, `sql`, `java`)
    *   Gera filho: `parameters` [e]
    *   Gera filho: `vars` [e]
    *   Gera filho: `start` [e]
    *   Gera filho: `templates` [e]
    *   Gera filho: `patterns` [e]

*   `parameters`
    *   Gera filho: `className` [e]
        *   Este, por sua vez, gera um filho: `NOT_DEFINED` [v]

*   `patterns`
    *   Gera filho: `nomeDoPadrao` [e]

---

### Fluxo de Execução (Templates, `if`, etc.)

*   `start` ou `template`
    *   Gera filho: `match` [e]
    *   Gera filho: `body` [e]

*   `match`
    *   Gera filho: `classes/class` [v] (sugestão de XPath)

*   `body` ou `then` ou `else`
    *   Gera filho: `write-pattern` [e]
    *   Gera filho: `apply-templates` [e]
    *   Gera filho: `if` [e]
    *   Gera filho: `vars` [e]

---

### Comandos Específicos

*   `apply-templates`
    *   Gera filho: `select` [e]
    *   Gera filho: `mode` [e]
    *   Gera filho: `parameters` [e]

*   `select`
    *   Gera filho: `attributes/attribute` [v] (sugestão de XPath)

*   `write-pattern`
    *   Gera filho: `nomeDoPadrao` [v] (referência a um padrão existente)

---

Se você executar o script em um nó cujo texto não corresponde a nenhuma das palavras-chave acima, uma mensagem aparecerá na área de log do FreeMind informando que não há sugestões automáticas configuradas para aquele nó.
