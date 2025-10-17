# Guia de Funcionalidades: Freemapper 1.0.8

Bem-vindo ao Freemapper 1.0.8! Esta versão introduz um conjunto de ferramentas poderosas para acelerar a criação e edição de mapas mentais. Este guia apresenta as principais funcionalidades e como utilizá-las.

---

## 🚀 Colagem Inteligente de Texto (Ctrl+V)

Transforme texto simples em nós estruturados instantaneamente. A funcionalidade de colar foi aprimorada com diversas sintaxes para máxima produtividade:

- **Criação de Nós com Ícones:**
  - **Atalhos:** Cole texto como `[e] Meu Nó` para criar um nó com o ícone correspondente ao atalho "e".
  - **Nomes Extensos:** Use o nome completo do ícone para maior clareza, como em `[element] Meu Nó`.

- **Nós com Múltiplas Linhas:**
  - Para nós de valor (atalho `[v]`), você pode colar blocos de texto. Linhas subsequentes que começam com `| ` serão automaticamente anexadas ao mesmo nó, preservando a formatação.

- **Criação de Hiperlinks:**
  - Para criar um link para outro mapa, cole a seguinte estrutura:
    ```
    Nome do Nó Pai
        #
            caminho/para/o/arquivo.mm
    ```
  - O "Nome do Nó Pai" se tornará um nó com um hiperlink para o arquivo especificado.

- **Preservação da Estrutura e Formatação:**
  - **Indentação:** Ao colar código ou listas, a estrutura de indentação relativa é mantida.
  - **Espaços em Branco:** Para nós de valor (`[v]`) e anotação (`[y]`), os espaços no início e fim do texto são preservados, ideal para conteúdo pré-formatado.

- **Qualidade de Vida:**
  - Textos contendo o caractere `@` não criam mais um link de e-mail indesejado automaticamente.

---

## 📝 Editor de Nó Longo (Alt+Enter)

O editor para textos longos agora é uma ferramenta de edição completa:

- **Localizar e Substituir (`Ctrl+F` / `Ctrl+H`):**
  - Abra um painel de busca integrado para localizar e substituir texto.
  - Navegue entre os resultados com os botões "Próximo" e "Anterior".
  - Veja o número total de ocorrências com o contador dinâmico (ex: "3 de 15").
  - Use `Enter` para pular para a próxima ocorrência.

- **Desfazer e Refazer (`Ctrl+Z` / `Ctrl+Y`):**
  - Todo o histórico de digitação no editor de nó longo pode ser desfeito e refeito.

---

## 📤 Exportando seu Trabalho

Exporte seus mapas ou ramos para o formato de texto `.mi`, ideal para compartilhamento e controle de versão:

- **Exportar Mapa Inteiro:** No menu principal, vá em `Arquivo -> Exportar -> As MI` para salvar o mapa completo como um arquivo `.mi`.

- **Exportar Ramo para Arquivo:** Clique com o botão direito em um nó e selecione `Exportar -> Branch as MI` para salvar apenas aquele ramo como um arquivo de texto `.mi`.

- **Exportar Ramo para Área de Transferência:**
  - Use a opção de menu `Exportar -> Branch as MI to Clipboard`.
  - Ou utilize o atalho global **`Alt + Shift + C`** para copiar o ramo selecionado diretamente para a área de transferência, pronto para ser colado em outro lugar.

---

## ⌨️ Melhorias de Usabilidade e Atalhos

- **Seleção de Ícones (`Alt+I`):**
  - **Busca Rápida:** Pressione `/` para buscar ícones e navegue nos resultados com as setas do teclado instantaneamente.
  - **Gerenciamento de Ícones:** Use `Delete` para remover todos os ícones de um nó e `Backspace` para remover o último, tudo dentro do diálogo de seleção.

- **Arrastar e Soltar (Drag and Drop):** A funcionalidade de arrastar e soltar nós para reorganizar o mapa está mais estável e confiável.