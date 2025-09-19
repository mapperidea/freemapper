# Guia de Referência do Mapper Idea: Do Mapa de Negócios à Estrutura de Dados

## 1. Introdução: Mapper Idea como um Sistema Especialista

Um questionamento comum sobre o Mapper Idea é em qual categoria de Inteligência Artificial ele se enquadra. Conforme os documentos de fundamentos e a patente, o Mapper Idea pode ser classificado como um *Sistema Especialista*.

Sistemas Especialistas são um ramo da IA focados em capturar o conhecimento de especialistas humanos em um domínio específico e representá-lo em um formato que um computador possa utilizar para resolver problemas. Eles são compostos por duas partes principais:

* **Base de Conhecimento:** Onde o conhecimento do especialista é armazenado. No Mapper Idea, a Base de Conhecimento é o *Mapa de Negócios* (ou Mapa de Arquitetura). Ele captura as regras, estruturas e lógica do negócio de uma forma estruturada e visual.
* **Máquina de Inferência:** Um motor que utiliza a base de conhecimento para deduzir novas informações e chegar a conclusões. A Máquina de Inferência do Mapper Idea interpreta o DOM (Document Object Model) gerado a partir do Mapa de Negócios e, através de regras e padrões (definidos no Mapa de Arquitetura), realiza a geração de código e outras automações.

Portanto, ao criar um Mapa de Negócios, você está, na prática, alimentando uma base de conhecimento para que um sistema especialista (o Mapper Idea) possa raciocinar sobre ela e automatizar tarefas complexas de engenharia de software.

## 2. Construindo o Mapa de Negócios: A Sintaxe Textual

O Mapa de Negócios é a representação visual das suas ideias e regras. Para que a máquina de inferência possa interpretá-lo, utilizamos uma sintaxe textual simples e poderosa, baseada em nós, indentação e ícones. A indentação define a hierarquia (nós filhos).

### 2.1. Ícones e Atalhos

Os ícones são metadados visuais que definem o "tipo" de um nó, sendo cruciais para a máquina de inferência. Cada ícone possui um nome completo e um atalho para agilizar a escrita.

| Ícone | Atalho | Descrição |
| :--- | :--- | :--- |
| `Descriptor.bean` | b | Define uma classe de dados simples (POJO/Bean). |
| `Descriptor.class` | c | Define uma classe com lógica de negócio. |
| `Descriptor.grouping` | g | "Agrupador visual, não afeta a estrutura do DOM." |
| `Package` | p | Agrupa elementos em um domínio (pacote/namespace). |
| `Mapping.directToField` | d | Define um atributo/campo de uma classe. |
| `Mapping.oneToOne` | r | Define um relacionamento de um para um. |
| `Mapping.oneToMany` | o | Define um relacionamento de um para muitos. |
| `Mapping.manyToOne` | m | Define um relacionamento de muitos para um. |
| `Method.public` | x | Define um método público. |
| `element` | e | Define uma propriedade de metadados. |
| `tag_green` | v | Define o valor de uma propriedade de metadados. |
| `textNode` | t | Nó de texto simples. |
| `bullet_key` | k | Ícone de chave. |
| `tag_yellow` | y | Tag de anotação amarela. |
| `elementOutput` | x | Saída de um elemento. |
| `Mapping.directMap` | h | Mapeamento direto. |

### 2.2. Estruturas Fundamentais

#### Pacotes (Packages)

Usados para organizar domínios, similar aos pacotes Java ou namespaces .NET. Geralmente em letras minúsculas.

**Sintaxe:**
```

[p] vendas

```
ou
```

[Package] vendas

```

#### Classes (Classes e Beans)

Representam as entidades do seu sistema.

**Sintaxe:**
```

[c] Pedido

```
ou
```

[b] PessoaFisica

```

#### Atributos (Fields)

Definem as propriedades de uma classe. A sintaxe é `nome: Tipo(tamanho)`.

**Sintaxe:**
```

[d] codigo: Numero(6)

```
ou
```

[Mapping.directToField] codigo: Numero(6)

```

#### Agrupadores Visuais (Grouping)

O ícone `Descriptor.grouping` (g) é um recurso para organizar visualmente seu mapa sem alterar a estrutura lógica do DOM. Você pode agrupar atributos, métodos ou qualquer outro conjunto de nós para melhor clareza.

**Sintaxe:**
```

[g] Atributos Fiscais
[d] baseCalculo: Moeda()
[d] valorIcms: Moeda()

```

### 2.3. Estendendo Metadados com Propriedades

Para adicionar informações extras a um nó (como descrições, validações, etc.), utilizamos um nó filho especial contendo apenas o caractere `@`. Abaixo deste nó, definimos as propriedades.

#### Modo 1: Estrutura com Ícones (`element` e `tag_green`)

Este modo cria uma estrutura XML mais explícita.

**Sintaxe:**
```

[d] nome: Texto(128)
@
[e] description
[v] Nome completo da pessoa.
[v] Utilizar o nome que consta em documentos oficiais.
[e] required
[v] true

````

**Estrutura DOM resultante (simplificada):**
```xml
<properties>
    <description>
        <value>Nome completo da pessoa.</value>
        <value>Utilizar o nome que consta em documentos oficiais.</value>
    </description>
    <required>
        <value>true</value>
    </required>
</properties>
````

**Acesso via XPath:** `description/value` ou `required/value`.

#### Modo 2: Estrutura Chave-Valor

Este modo é mais compacto e gera uma lista de pares de propriedade/valor.

**Sintaxe:**

```
[d] nome: Texto(128)
    @
        description: Nome completo da pessoa.
        required: true
```

**Estrutura DOM resultante (simplificada):**

```xml
<properties>
    <property name="description" value="Nome completo da pessoa."/>
    <property name="required" value="true"/>
</properties>
```

**Acesso via XPath:** `property[@name='description']/@value` ou `property[@name='required']/@value`.

### 2.4. Definindo Métodos e Lógica

Métodos são definidos com o ícone `Method.public` (x). A assinatura segue o padrão `nomeMetodo(parametro: Tipo): TipoRetorno`. A lógica do método é expressa em nós de texto simples, indentados sob o nó do método.

**Sintaxe:**

```
[g] métodos
    [x] calculaIdade: Inteiro(2)
        body
            if
                condition
                    self.dataNascimento
                then
                    return 
                        FuncoesTempo.diferencaAnos()
                            self.dataNascimento
                            DataHoje()
                else     
                    return
                        -1
```

A máquina de inferência interpreta essa estrutura aninhada para entender o fluxo da lógica (condicionais, chamadas de função, retornos).

## 3\. Exemplo Consolidado: Classe PessoaFisica

Vamos unir todos os conceitos em um exemplo completo. Este texto pode ser colado diretamente em uma ferramenta de mapa mental compatível para gerar a representação visual, e o Mapper Idea o utilizará para gerar a estrutura de dados para a inferência.

```
[p] domain
    [p] pessoa
        [b] PessoaFisica
            [g] atributos
                [d] nome: Texto(128)
                    @
                        [e] description         
                            [v] Nome da pessoa
                            [v] use o mesmo usado em documentos oficiais
                [d] cpf: Digitos(11)
                    @     
                        [e] description
                            [v] Número do CPF
                [d] dataNascimento: Data()
                    @           
                        [e] description
                            [v] Data de Nascimento da Pessoa
                            [v] Usaremos essa data para contato de aniversário e para calcular a Idade da Pessoa
            [g] métodos
                [x] calculaIdade: Inteiro(2)
                    body
                        if
                            condition        
                                self.dataNascimento
                            then
                                return                
                                    FuncoesTempo.diferencaAnos()
                                        self.dataNascimento
                                        DataHoje()
                            else
                                return
                                    -1
```

Este guia fornece a base para que Mapeadores de Negócio e Arquitetura possam modelar sistemas de forma eficiente, criando uma ponte direta e automatizada entre a concepção da ideia e a sua implementação em código através da máquina de inferência do Mapper Idea.