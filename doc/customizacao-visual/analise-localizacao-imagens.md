# Análise da Localização de Imagens para Personalização

Este documento detalha a localização dos principais arquivos de imagem utilizados na identidade visual do FreeMind, como a tela de abertura (splash screen) e os ícones da aplicação. O objetivo é facilitar a personalização para criar uma versão visualmente distinta.

## 1. Tela de Abertura (Splash Screen)

A imagem principal exibida durante a inicialização do programa foi encontrada no seguinte local:

-   **Arquivo de Imagem:** `freemind/images/Freemind_Splash_Butterfly_Modern.png`
-   **Referência no Código:** A imagem é carregada pela classe `freemind.main.FreeMindSplashModern.java`.

**Observação:** Existe outro arquivo chamado `splash.JPG` no mesmo diretório, mas não foram encontradas referências a ele no código-fonte, indicando que pode ser um recurso legado ou não utilizado.

## 2. Ícones da Aplicação

Os ícones que representam a aplicação em diferentes contextos (janela, executável) estão em locais distintos.

### Ícone da Janela Principal

O ícone exibido na barra de título da janela principal do FreeMind é:

-   **Arquivo de Imagem:** `freemind/images/FreeMindWindowIcon.png`
-   **Referência no Código:** É carregado na classe principal `freemind.main.FreeMind.java` para ser usado como o ícone da `JFrame`.

### Ícone para Executável do Windows

O ícone utilizado pelo lançador (`launcher`) no sistema operacional Windows é um arquivo `.ico` específico.

-   **Arquivo de Ícone:** `freemind/windows-launcher/FreeMind.ico`

**Observação:** Para alterar o ícone do executável no Windows, a simples substituição deste arquivo pode não ser suficiente, podendo ser necessário reconstruir o lançador.

### Outros Ícones (Borboleta)

Foi encontrado um ícone de borboleta adicional:

-   **Arquivo de Imagem:** `freemind/images/icons/freemind_butterfly.png`

Não foram encontradas referências diretas a este arquivo no código-fonte Java. É provável que ele seja utilizado em contextos específicos da interface (como em diálogos de "Sobre") ou que seja um recurso legado.

## 3. Conclusão

Para uma personalização visual básica que diferencie a versão modificada da padrão, os seguintes arquivos são os alvos principais para substituição:

1.  `freemind/images/Freemind_Splash_Butterfly_Modern.png` (Tela de Abertura)
2.  `freemind/images/FreeMindWindowIcon.png` (Ícone da Janela)
3.  `freemind/windows-launcher/FreeMind.ico` (Ícone do Executável do Windows)
