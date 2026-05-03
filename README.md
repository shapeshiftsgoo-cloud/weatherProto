# Sistema de Previsão Meteorológica Distribuído (gRPC + Spring Boot)

Este projeto implementa um sistema distribuído cliente-servidor para fornecimento de dados meteorológicos. O cliente expõe uma API RESTful e atua como uma ponte (gateway), comunicando-se via gRPC com um servidor backend que processa as regras de negócio em memória.

##  Instruções de como rodar o projeto

1. **Pré-requisitos:** Java 21, Maven 3.8+ e Postman (ou cURL).
2. **Compilar os Stubs gRPC:** Na raiz do projeto (ou no módulo compartilhado, caso use multi-módulo), execute:
   ```bash
   mvn clean compile

Isso fará o plugin do protobuf gerar as classes Java a partir do arquivo .proto.
3. Iniciar o Servidor gRPC:
Acesse a pasta do servidor e rode a aplicação Spring Boot:
Bash

mvn spring-boot:run

(O servidor iniciará na porta gRPC 9999)
4. Iniciar o Cliente REST:
Abra um novo terminal, acesse a pasta do cliente e rode a aplicação Spring Boot:
Bash

mvn spring-boot:run

(O cliente iniciará na porta web 8080)

Explicação detalhada do arquivo .proto

O arquivo .proto atua como um "contrato" rigoroso entre o Cliente e o Servidor, independente da linguagem de programação.

    Definição do serviço (service): O WeatherService mapeia 5 chamadas de procedimento remoto (RPCs). Cada RPC define exatamente o que entra e o que sai da função.

    Definição das mensagens (message): São os objetos de transferência de dados (DTOs). Tipos como string, double e bool são definidos juntamente com tags numéricas (ex: = 1), que o gRPC usa internamente para serializar os dados em formato binário (Protocol Buffers) de forma extremamente rápida. A tag repeated é utilizada para representar listas (arrays).

    RPCs implementados:

        ObterTemperaturaAtual: Recebe o nome da cidade e devolve a temperatura atual no momento.

        PrevisaoCincoDias: Recebe o nome e retorna um array de 5 doubles simulando os próximos dias.

        ListarCidades: Recebe uma mensagem vazia (Empty) e retorna uma lista de todas as chaves cadastradas no Map.

        CadastrarCidade: Recebe nome e temperatura inicial, inserindo os dados no mapa em memória e retornando um booleano de sucesso.

        EstatisticasClimaticas: Recebe o nome da cidade e retorna três valores (média, mínima, máxima) processados no servidor.

    Como o .proto gera código (stubs): Através do plugin protobuf-maven-plugin, durante a fase de build do Maven, o compilador protoc lê este arquivo e gera automaticamente as classes Java (Builders, Getters, Setters) e as classes abstratas (Stubs). O servidor herda do ImplBase e o cliente injeta o BlockingStub.

O Fluxo Completo da Requisição

Como uma requisição HTTP vira uma chamada gRPC?

    O usuário abre o Postman e faz um GET http://localhost:8080/temperatura?cidade=Urutai.

    O Tomcat (servidor web embutido no Spring Boot Cliente) intercepta a requisição HTTP.

    O @RestController aciona o método obterTemperatura().

    Dentro do método, pegamos a String "Urutai" e usamos o Builder gerado pelo .proto para criar o objeto CityRequest.

    O Cliente aciona o weatherStub.obterTemperaturaAtual(request).

    Por debaixo dos panos, o framework gRPC converte (serializa) esse objeto em formato binário compacto usando Protocol Buffers e envia via rede (usando o protocolo HTTP/2) para a porta 9090.

    O Servidor Spring Boot gRPC recebe os dados, desserializa para um objeto Java e chama o método correspondente no WeatherServiceImpl.

    O Servidor busca a cidade no mapa em memória, cria o TemperatureResponse e devolve no StreamObserver.

    O Cliente recebe o binário, converte de volta para Java, e o Spring MVC pega o valor retornado transformando em resposta HTTP (JSON/Text) enviada de volta para o Postman.

 Print da tela do sistema funcionando

