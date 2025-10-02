# Guia de Compilação e Distribuição do FreeMind

Este documento serve como um guia completo para compilar o projeto FreeMind, entender os comandos de build e criar um pacote de distribuição portátil.

## 1. Pré-requisitos

Antes de começar, garanta que você tenha as seguintes ferramentas instaladas e configuradas no seu ambiente:

-   **Java Development Kit (JDK):** A versão **Java 8** é necessária para garantir a compatibilidade com o código-fonte. Versões mais recentes podem causar erros de compilação.
-   **Apache Ant:** A ferramenta de automação de build utilizada pelo projeto. Certifique-se de que o comando `ant` esteja acessível no seu terminal.

## 2. Estrutura de Diretórios

Todos os comandos de compilação devem ser executados a partir do diretório `freemind/`, que é onde o arquivo principal de build (`build.xml`) está localizado.

```sh
cd /caminho/para/o/projeto/freemind/
```

## 3. Comandos de Compilação (Build)

O projeto utiliza o Apache Ant para automatizar todo o processo. Abaixo estão os principais comandos (alvos do Ant).

### Limpando o Projeto (`ant clean`)

Este comando apaga todos os arquivos compilados e diretórios de distribuição (`bin/`, `dist/`, etc.). É uma boa prática executá-lo antes de uma nova compilação para garantir que não haja arquivos antigos causando conflitos.

**Comando:**
```sh
ant clean
```

### Compilando e Criando a Distribuição (`ant dist`)

Este é o comando principal para construir o projeto. Ele executa as seguintes tarefas:

1.  Compila todo o código-fonte Java.
2.  Processa os plugins e acessórios.
3.  Cria uma versão "distribuível" e pronta para ser executada dentro do diretório `freemind/bin/dist/`.

**Comando:**
```sh
ant dist
```

### Compilando e Executando para Testes (`ant run`)

Este comando é um atalho conveniente para desenvolvedores. Ele primeiro executa o `ant dist` para garantir que o projeto esteja compilado e, em seguida, inicia a aplicação FreeMind diretamente.

**Comando:**
```sh
ant run
```

## 4. Criando um Pacote de Distribuição (.zip)

Para empacotar a aplicação em um único arquivo `.zip` que pode ser facilmente compartilhado, você deve usar o alvo `post`.

### Gerando o ZIP (`ant post`)

Este comando, após compilar o projeto (ele depende do `ant dist`), cria pacotes `.zip` da aplicação.

**Comando:**
```sh
ant post
```

-   **O que ele faz:** Ele pega o conteúdo da pasta `freemind/bin/dist` e o comprime.
-   **Onde encontrar:** O arquivo gerado será salvo no diretório `post/` (localizado na raiz do projeto, ao lado da pasta `freemind/`). O nome do arquivo seguirá o padrão `freemind-bin-1.0.1.zip`.

## 5. Executando a Versão Distribuível

Após descompactar o arquivo `freemind-bin-1.0.1.zip` em qualquer lugar do seu computador, você pode iniciar a aplicação usando os scripts fornecidos:

-   **Para Linux e macOS:**
    ```sh
    ./freemind.sh
    ```
-   **Para Windows:**
    ```sh
    freemind.bat
    ```
