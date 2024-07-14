# clojure-caju-process

Resolução do desafio de programação da Caju ([enunciado aqui](./code-challenge.md))

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2024 FIXME


---

Pensanso sobre a resposta do L4

1. Lock no banco
2. Lock externo
3. Usar um id para transação que tenha timestamp
4. Solução async com partition key no Kafka