Relato:

Esse é o meu mapa mental inteiro:

New Mindmap
    [p] domain
        //
            [g] domain-mi
                #
                    domain.mi
        [p] pizzaria
            [b] Endereco
                [g] atributos
                    [d] cep: Digitos(8)
                        @
                            [e] description
                                [v] Código de Endereçamento Postal.
                    [d] logradouro: Texto(100)
                    [d] numero: Texto(10)
                    [d] complemento: Texto(50)
                    [d] bairro: Texto(50)
                    [d] cidade: Texto(50)
                    [d] estado: Texto(2)
            [b] Cliente
                [g] atributos
                    [d] codigo: Inteiro()
                    [d] nome: Texto(100)
                    [d] telefone: Digitos(11)
                    [d] dataCadastro: Data()
                    [d] endereco: Endereco
            [b] Pizza
                [g] atributos
                    [d] codigo: Inteiro()
                    [d] nome: Texto(80)
                        @
                            [e] description
                                [v] Sabor da pizza, ex: Calabresa, Marguerita.
                    [d] precoUnitario: Decimal(10, 2)
                    [d] tamanho: Texto(20)
                        @
                            [e] validation
                                [v] Valores permitidos: 'Grande', 'Média', 'Broto'.
                    [d] isDisponivel: Logico()
                [g] metodos
                    [x] verificaDisponibilidade: Logico()
                        body
                            return
                                self.isDisponivel
            [b] ItemPedido
                [g] atributos
                    [d] pizza: Pizza
                        @
                            [e] description
                                [v] Referência ao produto Pizza.
                    [d] quantidade: Inteiro(3)
                    [d] precoUnitario: Decimal(10, 2)
                    [d] valorSubTotal: Decimal(10, 2)
                    [d] observacoes: Texto(100)
                [g] metodos
                    [x] calculaValorSubTotal: Decimal(10, 2)
                        body
                            // Regra de negócio: Subtotal = Quantidade * Preço Unitário
                            return
                                FuncoesMatematicas.multiplicar()
                                    self.quantidade
                                    self.precoUnitario
            [b] Pedido
                [g] atributos
                    [d] numero: Inteiro()
                    [d] dataHora: DataHora()
                    [d] cliente: Cliente
                    [d] status: Texto(20)
                        @
                            [e] validation
                                [v] Valores permitidos: 'Pendente', 'Em Preparo', 'Em Entrega', 'Entregue', 'Cancelado'.
                    [d] valorTotalProdutos: Decimal(10, 2)
                        @
                            [e] description
                                [v] Soma dos subtotais de todos os itens do pedido.
                    [d] valorDesconto: Decimal(10, 2)
                    [d] valorFrete: Decimal(10, 2)
                    [d] valorFinal: Decimal(10, 2)
                    [d] itens: Lista<ItemPedido>
                [g] metodos
                    [x] calculaValorTotalProdutos: Decimal(10, 2)
                        body
                            // Regra: Agrega a soma do atributo 'valorSubTotal' da coleção 'itens'
                            return
                                FuncoesColecao.somaAtributo(self.itens, "valorSubTotal")
                    [x] calculaValorFinal: Decimal(10, 2)
                        body
                            // Regra: Valor Final = (Total Produtos + Frete) - Desconto
                            return
                                FuncoesMatematicas.subtrair()
                                    FuncoesMatematicas.somar()
                                        self.valorTotalProdutos
                                        self.valorFrete
                                    self.valorDesconto
                    [x] mudaStatus(novoStatus: Texto(20)): Logico()
                        body
                            // Lógica de validação de negócio: impede alteração se o pedido já foi cancelado
                            if
                                condition
                                    self.status == "Cancelado"
                                then
                                    throw
                                        [v] Erro: Não é possível alterar o status de um pedido cancelado.
                                else
                                    set
                                        self.status = novoStatus
                                    return
                                        true
    O teste de software é um teste essencial. Neste texto, a palavra "teste" aparece várias vezes para que o teste da funcionalidade de busca seja completo.
      | 
      |   Testando com "Teste" em maiúscula.
      |   O objetivo é testar todas as variações. TESTE em caixa alta.
      | 
      |   Além disso, é importante verificar se a substituição funciona, mesmo para uma palavra de teste. Vamos testar isso agora.
    [g] teste
        [e] teste
        teste
        teste
        [v] teste
    [g] teste
        [e] teste
        teste
        teste
        [v] teste
    
        [p] domain
            //
                [g] domain-mi
                    #
                        domain.mi
            [p] pizzaria
                [b] Endereco
                    [g] atributos
                        [d] cep: Digitos(8)
                            @
                                [e] description
                                    [v] Código de Endereçamento Postal.
                        [d] logradouro: Texto(100)
                        [d] numero: Texto(10)
                        [d] complemento: Texto(50)
                        [d] bairro: Texto(50)
                        [d] cidade: Texto(50)
                        [d] estado: Texto(2)
                [b] Cliente
                    [g] atributos
                        [d] codigo: Inteiro()
                        [d] nome: Texto(100)
                        [d] telefone: Digitos(11)
                        [d] dataCadastro: Data()
                        [d] endereco: Endereco
                [b] Pizza
                    [g] atributos
                        [d] codigo: Inteiro()
                        [d] nome: Texto(80)
                            @
                                [e] description
                                    [v] Sabor da pizza, ex: Calabresa, Marguerita.
                        [d] precoUnitario: Decimal(10, 2)
                        [d] tamanho: Texto(20)
                            @
                                [e] validation
                                    [v] Valores permitidos: 'Grande', 'Média', 'Broto'.
                        [d] isDisponivel: Logico()
                    [g] metodos
                        [x] verificaDisponibilidade: Logico()
                            body
                                return
                                    self.isDisponivel
                [b] ItemPedido
                    [g] atributos
                        [d] pizza: Pizza
                            @
                                [e] description
                                    [v] Referência ao produto Pizza.
                        [d] quantidade: Inteiro(3)
                        [d] precoUnitario: Decimal(10, 2)
                        [d] valorSubTotal: Decimal(10, 2)
                        [d] observacoes: Texto(100)
                    [g] metodos
                        [x] calculaValorSubTotal: Decimal(10, 2)
                            body
                                // Regra de negócio: Subtotal = Quantidade * Preço Unitário
                                return
                                    FuncoesMatematicas.multiplicar()
                                        self.quantidade
                                        self.precoUnitario
                [b] Pedido
                    [g] atributos
                        [d] numero: Inteiro()
                        [d] dataHora: DataHora()
                        [d] cliente: Cliente
                        [d] status: Texto(20)
                            @
                                [e] validation
                                    [v] Valores permitidos: 'Pendente', 'Em Preparo', 'Em Entrega', 'Entregue', 'Cancelado'.
                        [d] valorTotalProdutos: Decimal(10, 2)
                            @
                                [e] description
                                    [v] Soma dos subtotais de todos os itens do pedido.
                        [d] valorDesconto: Decimal(10, 2)
                        [d] valorFrete: Decimal(10, 2)
                        [d] valorFinal: Decimal(10, 2)
                        [d] itens: Lista<ItemPedido>
                    [g] metodos
                        [x] calculaValorTotalProdutos: Decimal(10, 2)
                            body
                                // Regra: Agrega a soma do atributo 'valorSubTotal' da coleção 'itens'
                                return
                                    FuncoesColecao.somaAtributo(self.itens, "valorSubTotal")
                        [x] calculaValorFinal: Decimal(10, 2)
                            body
                                // Regra: Valor Final = (Total Produtos + Frete) - Desconto
                                return
                                    FuncoesMatematicas.subtrair()
                                        FuncoesMatematicas.somar()
                                            self.valorTotalProdutos
                                            self.valorFrete
                                        self.valorDesconto
                        [x] mudaStatus(novoStatus: Texto(20)): Logico()
                            body
                                // Lógica de validação de negócio: impede alteração se o pedido já foi cancelado
                                if
                                    condition
                                        self.status == "Cancelado"
                                    then
                                        throw
                                            [v] Erro: Não é possível alterar o status de um pedido cancelado.
                                    else
                                        set
                                            self.status = novoStatus
                                        return
                                            true
                                            
                                            
Vou posicionar o meu cursor na // dessa seção, é o que eu espero que seja copiado e colado

//
            [g] domain-mi
                #
                    domain.mi

Resultado da minha copia, vou copiar esse texto aqui antes "Não funcionou", se a colagem do Alt + Shift + C e ai o Ctrl + V colar esse texto embaixo, saberemos que não funcionou.   


Não funcionou

E ao pressionar o Alt + Shift + C, nenhuma caixa de dialogo ou qualquer coisa foi acionada.      

                                   
