# e-comerce

## Objetivos
<li> Criar um e-comerce totalmente do Zero
<li> Criado totalmente por mim, apenas com pesquisas e sem cópia de códigos já prontos
<li> Utilização apenas de I.A para auxílio com erros
<li> Integração entre BackEnd, FrontEnd, Banco de Dados e Aplicação em Nuvem

## Diagrama de classes

```mermaid
classDiagram
  class Produto {
    +id: int
    +nome: string
    +descricao: string
    +preco: decimal
    +imagem_url: string
    +estoque: int
  }
  class Carrinho {
    +id: int
    +data_criacao: datetime
  }
  class ItemCarrinho {
    +id: int
    +carrinho_id: int
    +produto_id: int
    +quantidade: int
  }
  class Usuario {
    +id: int
    +nome: string
    +email: string
    +senha: string
    +data_cadastro: datetime
  }

  Carrinho "1" -- "*" ItemCarrinho : tem
  Produto "1" -- "*" ItemCarrinho : contém
  Usuario "1" -- "0..*" Carrinho : possui
```
