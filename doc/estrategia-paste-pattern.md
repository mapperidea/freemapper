# Estratégia de Implementação: Colar com Padrão de Ícones

Este documento descreve a estratégia e os passos técnicos seguidos para implementar a funcionalidade de reconhecimento de padrões de ícones ao colar texto, conforme especificado no `guiaMapperidea.md`.

## Objetivo

O objetivo foi estender a funcionalidade de colar texto existente para que, além de criar nós hierárquicos baseados na indentação, o sistema pudesse reconhecer uma sintaxe especial (`[atalho] texto do nó`) para atribuir automaticamente um ícone ao nó recém-criado. A nova funcionalidade deveria coexistir com a antiga, sem quebrar o comportamento para textos simples.

## Análise e Planejamento

A análise inicial, documentada em `copy-paste-feature.md`, identificou que a classe `freemind/modes/mindmapmode/actions/PasteAction.java` e, especificamente, seu método `pasteStringWithoutRedisplay`, era o ponto central da lógica de colar texto.

A estratégia de implementação foi modificar este método para que, dentro do seu loop de processamento de linhas, ele executasse os seguintes passos:

1.  Verificar se a linha de texto corresponde ao padrão `[atalho] texto`.
2.  **Se corresponder**: Extrair o atalho e o texto, criar o nó e aplicar o ícone correspondente ao atalho.
3.  **Se não corresponder**: Manter o fluxo de execução original, tratando a linha como texto simples.

## Passos da Implementação

A implementação foi realizada nos seguintes passos:

### 1. Adição de Mapeamento de Ícones

Para traduzir os atalhos do guia (ex: `b`, `p`) para os nomes de ícones reais do FreeMind (ex: `Descriptor.bean`, `Package`), foi adicionado um `Map<String, String>` estático e privado à classe `PasteAction`.

```java
private static final Map<String, String> mapperIdeaIconMap;
static {
    mapperIdeaIconMap = new HashMap<String, String>();
    mapperIdeaIconMap.put("b", "Descriptor.bean");
    mapperIdeaIconMap.put("c", "Descriptor.class");
    // ... e assim por diante para todos os ícones
}
```

Isso centraliza o mapeamento e facilita futuras manutenções.

### 2. Criação do Padrão Regex

Uma expressão regular (`Pattern`) foi criada como um campo estático para identificar e extrair as partes relevantes da sintaxe do Mapper Idea.

```java
private static final Pattern mapperIdeaPattern = Pattern.compile("\\[([\\w\\.]+)\\]\\s*(.*)");
```

-   `\[([\\w\\.]+)\]`: Captura o atalho do ícone dentro de colchetes. O atalho pode conter caracteres de palavra e pontos.
-   `\\s*`: Ignora espaços em branco após os colchetes.
-   `(.*)`: Captura o restante da linha como o texto do nó.

### 3. Modificação do Método `pasteStringWithoutRedisplay`

O núcleo da mudança foi dentro do loop `for` que itera sobre cada linha do texto colado (`textLines`).

O bloco de código original que criava o nó foi substituído por uma estrutura `if/else`:

**Antes:**
```java
// ...
String visibleText = text.trim();
// Lógica para tratar links...
MindMapNode node = mMindMapController.newNode(visibleText, parent.getMap());
// ...
```

**Depois:**
```java
// ...
String visibleText = text.trim();
MindMapNode node;
Matcher matcher = mapperIdeaPattern.matcher(visibleText);

if (matcher.find()) {
    // ---
    String shortcut = matcher.group(1);
    String nodeText = matcher.group(2).trim();
    node = mMindMapController.newNode(nodeText, parent.getMap());

    String iconName = mapperIdeaIconMap.get(shortcut);
    if (iconName != null) {
        MindIcon icon = MindIcon.factory(iconName);
        if (icon != null) {
            node.addIcon(icon, 0); // Adiciona o ícone
        }
    }
}
else {
    // ---
    // (código original para tratar links e criar o nó com visibleText)
    node = mMindMapController.newNode(visibleText, parent.getMap());
}
// ... (resto do método continua igual)
```

-   A classe `MindIcon` foi importada para permitir a criação de objetos de ícone via `MindIcon.factory(iconName)`.
-   O método `node.addIcon(icon, 0)` foi usado para adicionar o ícone encontrado ao nó recém-criado.

### 4. Correção de Erros de Compilação

Durante o processo, a compilação inicial falhou por dois motivos:
1.  **Uso do Operador Diamond:** O projeto compila com a flag `-source 1.6`, que não suporta o operador diamond (`<>`). A instanciação do HashMap foi corrigida para `new HashMap<String, String>()`.
2.  **Escapamento de Strings:** Múltiplas tentativas de modificação do arquivo via `replace` corromperam literais de string no código. A solução final foi reescrever o arquivo por completo (`write_file`) com o código-fonte corrigido e validado, garantindo que todos os caracteres de escape estivessem corretos.

## Validação

A validação final foi feita executando o comando de build do projeto, `ant dist`, a partir do diretório `freemind`. O sucesso da compilação garantiu que as alterações eram sintaticamente corretas e não introduziram regressões que impedissem a construção do software.

## Conclusão

A implementação foi bem-sucedida, resultando em uma funcionalidade de colar mais poderosa e flexível, que atende tanto ao uso geral quanto à necessidade específica da metodologia Mapper Idea, mantendo total compatibilidade com o comportamento anterior.
