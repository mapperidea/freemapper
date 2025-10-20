# Análise de Implementação: Ferramenta de Teste XPath Interna

Este documento detalha a análise e o plano de ação para implementar uma ferramenta de teste de expressões XPath integrada ao FreeMind, servindo como uma alternativa interna a serviços web como o `xpather.com`.

## 1. Objetivo

O objetivo é desenvolver uma ferramenta de propósito geral que permita aos usuários testar expressões XPath contra qualquer documento XML. A ferramenta será integrada ao FreeMind, mas não limitada a ele, funcionando de forma similar a serviços web como o `xpather.com`. Isso oferece duas vantagens principais:
1.  **Independência:** Reduz a dependência de ferramentas online que podem ser descontinuadas.
2.  **Conveniência:** Fornece uma ferramenta de teste de XPath rapidamente acessível a partir do menu do FreeMind.

## 2. Análise da Funcionalidade Alvo

Uma ferramenta de teste de XPath, como o `xpather.com`, geralmente consiste em três componentes principais de interface:
-   Uma área de texto para o usuário colar qualquer documento XML.
-   Um campo de texto para inserir a expressão XPath.
-   Uma área de texto para exibir os resultados da avaliação da expressão.

A lógica central envolve o uso de um motor XPath para executar a consulta no documento XML fornecido pelo usuário e formatar o resultado para exibição.

## 3. Análise da Arquitetura do FreeMind e Viabilidade

A implementação é totalmente viável dentro do ecossistema do FreeMind.

-   **Motor XPath:** O Java possui um motor XPath robusto e integrado no pacote `javax.xml.xpath`. Não será necessário implementar a lógica de avaliação do zero.
-   **Interface Gráfica (UI):** O FreeMind é construído em Swing. Uma nova janela de diálogo (`JDialog`) pode ser criada para abrigar a ferramenta, utilizando componentes padrão como `JTextArea`, `JTextField`, `JButton` e `JSplitPane` para criar um layout funcional.
-   **Ferramenta Autocontida:** A ferramenta será autocontida e não precisará interagir com o estado do mapa mental atual, o que simplifica a implementação e reduz dependências.
-   **Sistema de Plugins:** A maneira mais idiomática de adicionar essa ferramenta é através do sistema de plugins do FreeMind, o que garante um baixo acoplamento com o núcleo da aplicação.

## 4. Plano de Ação Detalhado

A implementação seguirá o padrão de desenvolvimento de plugins e componentes de UI já estabelecido no projeto.

### Passo 1: Criar a Classe da Interface Gráfica (UI)

1.  **Criar Arquivo:** `freemind/freemind/accessories/plugins/xpath/XPathTesterDialog.java`. (Será criado um novo subdiretório `xpath` para organização).
2.  **Estrutura da Classe:**
    -   A classe herdará de `javax.swing.JDialog`.
    -   O layout será construído com `JSplitPane` para dividir a janela em áreas para o XML, a expressão XPath e os resultados.
    -   Um XML de exemplo será definido como uma constante de string para ser carregado na inicialização.
3.  **Componentes:**
    -   `JTextArea` (dentro de um `JScrollPane`) para o código XML, inicializada com um texto XML padrão e editável.
    -   `JTextField` para a expressão XPath.
    -   `JButton` com o texto "Executar".
    -   `JLabel` para o contador de resultados (ex: `matchCountLabel`), posicionado próximo ao botão "Executar".
    -   `JTextArea` (dentro de um `JScrollPane`) para os resultados.
4.  **Lógica Interna:**
    -   Um `ActionListener` no botão "Executar" irá disparar a avaliação do XPath.
    -   Este listener utilizará a API `javax.xml.xpath.XPathFactory` para compilar e executar a expressão contra o texto na área de XML.
    -   O método deverá tratar os diferentes tipos de retorno do XPath. Se o resultado for um `NodeSet`, seu tamanho (`NodeList.getLength()`) será usado para atualizar o texto do `matchCountLabel` (ex: "Resultados: 5").
    -   O resultado da consulta será formatado e exibido na área de resultados.
    -   Blocos `try-catch` robustos serão implementados para capturar e exibir erros de sintaxe no XML ou na expressão XPath, atualizando o contador para "Erro".

### Passo 2: Criar a Classe de Ação do Plugin

1.  **Criar Arquivo:** `freemind/freemind/accessories/plugins/xpath/XPathTesterPlugin.java`.
2.  **Estrutura da Classe:**
    -   A classe implementará a interface `freemind.extensions.Hook`.
    -   O método `execute` será o ponto de entrada.
3.  **Lógica da Ação:**
    -   Instanciar o `XPathTesterDialog` (seu construtor não receberá argumentos).
    -   Exibir o diálogo para o usuário. A lógica é simplificada, pois não há necessidade de interagir com o `MindMapController`.

### Passo 3: Registrar o Plugin no Sistema

Seguindo o padrão do FreeMind, três arquivos de configuração serão criados/modificados.

1.  **Criar Arquivo de Propriedades:** `freemind/accessories/plugins/xpath/XPathTesterPlugin.properties`.
    -   Conteúdo: `accessories.plugins.xpath.XPathTesterPlugin.text=XPath Tester`

2.  **Criar Arquivo Descritor XML:** `freemind/accessories/plugins/xpath/XPathTesterPlugin.xml`.
    -   Este arquivo registrará o plugin na `HookFactory` e definirá sua localização no menu principal.
    -   Exemplo de conteúdo, baseado em outros plugins:
        ```xml
        <plugin
            class_name="accessories.plugins.xpath.XPathTesterPlugin"
            name="xpath_tester"
            documentation="docs/plugins/xpath_tester.html"
            properties_file="accessories/plugins/xpath/XPathTesterPlugin.properties">
            <mode name="MindMap"
                base="menu_bar/tools/xpath_tester"
                factory="freemind.modes.mindmapmode.hooks.MindMapHookFactory"
            />
        </plugin>
        ```

3.  **Modificar Arquivo de Recursos:** Adicionar a chave de texto ao arquivo de internacionalização.
    -   **Arquivo:** `freemind/Resources_en.properties`
    -   **Linha a ser adicionada:** `xpath_tester.text=XPath Tester`

## 5. Conclusão

A implementação de uma ferramenta de teste de XPath é uma adição valiosa e de baixo risco ao FreeMind. O plano proposto é robusto, segue as convenções de código existentes e reutiliza componentes padrão do Java, garantindo uma integração limpa e uma funcionalidade estável.
