# clojure-caju-process

Resolução do desafio de programação da Caju ([enunciado aqui](./code-challenge.md))

## Sobre o projeto

O projeto busca seguir conceitos de Clean Architecture.
Estruturalmente temos:

- src/clojure_caju_process/
  - domain/           -> (regras de negócio e definições de entidades)
  - infrastructure/   -> (implementações de repository e outros detalhes)
  - use_case/         -> (regras da aplicação e fluxos)
  - core.clj          -> (main)
  - system.clj        -> (definição do mapa de componentes do sistema)

### Para a resolução do item L1 e L2:

No autorizador simples, foi criada uma função em merchant-categories que controla a lógica de, dado um MCC, responder qual a categoria. Também foi criada uma função em accounts que verifica se um débito pode ou não ser realizado e em qual saldo será debitado.

### Para a resolução do item L3:

Decidi criar uma entidade no banco chamada merchant, que guarda a informação do nome de um vendedor e sua categoria real. Assim, para um novo débito/transação, verifica-se se o nome do vendedor está cadastrado no banco. Se estiver, essa informação tem precedência sobre o MCC recebido para definir a categoria. E foi criado um endpoint para cadastrar novos merchants.

## Para a resolução do L4

O desafio aqui era lidar com transações que pudessem gerar débito em uma mesma carteira ao mesmo tempo, considerando as restrições de que a operação precisa ser síncrona e rápida, embora rara.

Diante dessas informações, escolhi utilizar o sistema de lock do PostgreSQL sobre um item da tabela ([row level lock](https://www.postgresql.org/docs/current/explicit-locking.html#LOCKING-ROWS)). Ou seja, quando chega uma operação de débito, abre-se uma transação no banco, faz-se o lock sobre a conta, calcula-se o novo saldo, atualiza-se a conta no banco, cria-se uma entidade de transação no banco e então libera-se o lock. Dessa maneira, garantimos a consistência do saldo.

Além dessa solução, pensei em outras abordagens:

1. Caso o banco de dados não possuísse um sistema de lock, seria possível usar um lock otimista. Isso significa ter uma informação de versão da entidade e, caso essa versão tenha mudado entre o momento do cálculo e o do update, cancelar a operação e tentar novamente. Essa solução tem a desvantagem de ter que recomeçar o cálculo do saldo, o que pode ser problemático em casos de muitos conflitos simultâneos.
2. Caso fosse possível processar assincronamente, poderíamos usar sistemas de mensageria que direcionam mensagens para um mesmo consumidor com base em uma chave. O Kafka faz isso com sua partition key. Nesse caso, poderíamos usar o ID da conta como a partition key, garantindo que todas as operações sobre uma conta fossem consumidas por um mesmo consumidor, em sequência, eliminando o problema de operações em paralelo.

## Requisitos

Para a execução desse projeto são necessarias as seguintes tecnologias:

- [Clojure][https://clojure.org/index] >= 1.11.1.
- [Java][https://www.oracle.com/java/technologies/javase/jdk18-archive-downloads.html] >= 18.0.2.
- [Leiningen][https://github.com/technomancy/leiningen] >= 2.0.0.

Recomendo a instalação usando o gerenciador de runtimes [adsf](https://asdf-vm.com/) e o arquivo `.tool-versions`.

Também é recomendado a instalação do [Docker](https://www.docker.com/) para facilitar na execução dos testes.

Um detalhe importante é que é preciso ter um PostgreSQL rodando na porta 5432 com as configurações de banco. Para facilitar, temos ele configurado no Docker Compose. Portanto, antes de rodar a aplicação ou os testes, é necessário executar o comando `docker compose up -d`.

## Instalando Depêndencias

Esse projeto foi feito com o Leiningen e para baixar as dependências é só rodar o comando

    lein deps

## Executando o projeto

Para executar o projeto é só rodar o comando

    lein run

**depende de banco de dados rodando*

## Executando os testes

Podemos executar os testes de maneira geral com o comando

    lein test

Ou podemos executar apenas os unitários ou os de integração. Sendo os de integração aqueles que possuem depêndencia com banco.

    lein test :unit
    lein test :integration
