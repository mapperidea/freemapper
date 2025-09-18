# Análise da Melhoria na Colagem de Texto com Indentação

Este documento detalha a análise e a estratégia de implementação para melhorar a funcionalidade de colar texto, especificamente para preservar a estrutura de indentação relativa de um bloco de texto copiado.

## 1. Problema Identificado

Atualmente, ao colar um bloco de texto com múltiplas linhas e níveis de indentação (como um trecho de código ou uma lista aninhada), a lógica do FreeMind calcula a hierarquia dos novos nós com base na **indentação absoluta** de cada linha (o número total de espaços desde o início da linha).

Isso causa um problema de usabilidade: se o bloco de texto copiado já possui uma "indentação base", essa indentação é levada em conta no cálculo, muitas vezes resultando em uma estrutura hierárquica "achatada" ou incorreta ao ser colada no mapa. O usuário é forçado a corrigir manualmente a posição dos nós recém-criados.

**Exemplo do Problema:**
Um usuário copia o seguinte trecho de código, que tem uma indentação base de 4 espaços:

```
    if (user != null) {
        processUser(user);
    }
```

Ao colar isso sob um nó no mapa, a lógica atual pode interpretar a primeira linha como tendo 4 níveis de profundidade, em vez de tratá-la como o "nó raiz" do trecho colado.

## 2. Análise do Código-Fonte

A funcionalidade reside no seguinte local:
-   **Classe:** `freemind.modes.mindmapmode.actions.PasteAction.java`
-   **Método:** `pasteStringWithoutRedisplay`

A análise do método confirma que ele itera por cada linha do texto colado e executa os seguintes passos:
1.  Calcula a `depth` (profundidade) contando o número de espaços no início da linha.
2.  Usa essa `depth` absoluta para encontrar um nó pai na pilha de nós que estão sendo criados.

O código não possui um passo de **normalização** para tratar a indentação relativa do bloco de texto como um todo.

## 3. Estratégia de Melhoria Proposta

Para resolver o problema, propõe-se a modificação do método `pasteStringWithoutRedisplay` para que ele normalize a indentação antes de criar os nós.

O novo fluxo seria:

1.  **Passo 0 (Pré-processamento): Encontrar a Indentação Mínima**
    -   Antes de iniciar o loop principal que cria os nós, o método deve fazer uma varredura em todas as linhas do texto colado.
    -   Nessa varredura, ele identificará o menor número de espaços iniciais encontrado em qualquer linha que não esteja vazia. Este valor será a `baseIndentation`.

2.  **Passo 1 (Loop Principal): Normalizar a Indentação de Cada Linha**
    -   Dentro do loop `for` existente, para cada linha, a `depth` ainda será calculada contando os espaços iniciais.
    -   No entanto, um novo passo será adicionado: `int relativeDepth = depth - baseIndentation;`. Se o resultado for negativo (o que pode acontecer em linhas mal formatadas), ele deve ser tratado como 0.

3.  **Passo 2: Construir a Hierarquia com a Profundidade Relativa**
    -   A variável `relativeDepth` (em vez da `depth` original) será usada para determinar a relação pai-filho entre os nós.

## 4. Exemplo Prático do Novo Comportamento

**Texto Copiado (com `baseIndentation` de 4 espaços):**
```
    if (user != null) {
        processUser(user);
    }
```

**Execução da Nova Lógica:**
1.  **Pré-processamento:** O código analisa o texto e determina que `baseIndentation = 4`.
2.  **Processamento da Linha 1:**
    -   `depth` = 4 espaços.
    -   `relativeDepth` = 4 - 4 = 0.
    -   Um nó é criado com o texto "if (user != null) {" no nível 0 (relativo ao ponto de colagem).
3.  **Processamento da Linha 2:**
    -   `depth` = 8 espaços.
    -   `relativeDepth` = 8 - 4 = 4.
    -   Um nó é criado com o texto "processUser(user);" no nível 4, tornando-se corretamente um filho do nó anterior.
4.  **Processamento da Linha 3:**
    -   `depth` = 4 espaços.
    -   `relativeDepth` = 4 - 4 = 0.
    -   Um nó é criado com o texto "}" no nível 0, tornando-se um irmão do primeiro nó.

## 5. Conclusão

A implementação desta estratégia de normalização tornará a funcionalidade de colar texto significativamente mais inteligente e intuitiva. Ela preservará a estrutura interna de qualquer bloco de texto copiado, eliminando a necessidade de ajustes manuais e melhorando drasticamente a experiência do usuário ao importar conteúdo de fontes externas.
