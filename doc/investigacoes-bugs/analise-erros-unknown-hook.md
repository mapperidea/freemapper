# Análise dos Erros "Unknown hook name"

Este documento detalha a investigação sobre os erros `java.lang.IllegalArgumentException: Unknown hook name` que ocorrem durante a inicialização da aplicação.

## 1. Problema

Durante a inicialização, o console exibe múltiplos erros com as seguintes mensagens:

- `Unknown hook name MapStyle`
- `Unknown hook name AutomaticEdgeColor`

Esses erros são lançados pela classe `freemind.modes.mindmapmode.hooks.MindMapHookFactory`, mas não impedem a aplicação de iniciar.

## 2. Análise da Causa Raiz

A investigação aponta para a seguinte causa:

1.  **Origem do Erro:** O erro é lançado pelo método `getHookDescriptor` na `MindMapHookFactory`. Este método é responsável por encontrar a definição de um "hook" (um ponto de extensão ou plugin) com base em seu nome.
2.  **Gatilho:** A análise do rastro da pilha (`stack trace`) mostra que o erro ocorre durante o carregamento de um mapa mental (`.mm`). A aplicação tenta restaurar os mapas abertos na sessão anterior, e esses arquivos de mapa contêm referências aos hooks "MapStyle" e "AutomaticEdgeColor".
3.  **Hooks Inexistentes:** Uma busca completa no código-fonte atual não encontrou nenhuma definição ou referência para "MapStyle" ou "AutomaticEdgeColor".

**Conclusão:** A hipótese mais provável é que esses hooks eram parte de uma versão mais antiga do FreeMind ou de um plugin que foi removido. Os arquivos de mapa mental do usuário, especialmente os que são carregados automaticamente na inicialização, ainda contêm referências a esses hooks obsoletos. Como a fábrica de hooks não os reconhece mais, ela lança a exceção.

## 3. Próximos Passos

Para resolver o problema, seria necessário identificar e remover as referências a esses hooks inválidos dos arquivos `.mm` que estão causando o erro. A investigação foi interrompida antes que os arquivos específicos pudessem ser identificados.
