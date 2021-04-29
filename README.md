# Distributed Banking

## Descrição

Uma aplicação bancária para sistemas distribuídos feita com a biblioteca [BFT-SMART](https://github.com/bft-smart/library). Trabalho final da disciplina de Tópicos avançados em computadores - Tolerância a falhas em sistemas distribuídos 2020/2 da Universidade de Brasília.

## Integrantes

Nome                            | Matrícula
------------------------------- | ----------
André Filipe Caldas Laranjeira  | 16/0023777
Hugo Nascimento Fonseca         | 16/0008166
José Luiz Gomes Nogueira        | 16/0032458
Victor André Gris Costa         | 16/0019311

## Aviso de uso de material licenciado

Este projeto utiliza arquivos obtidos da biblioteca [BFT-Smart](https://github.com/bft-smart/library), distribuídos sob a licença _Apache License 2.0_. Esta seção tem o intuito de avisar o leitor sobre o uso desses arquivos e atender às obrigações legais decorrentes do uso desses arquivos em nosso projeto de software.

Conforme requisitado na seção 4, item 'a', da licença _Apache License 2.0_, uma cópia completa da licença _Apache License 2.0_, referente **aos arquivos obtidos da biblioteca BFT-Smart**, pode ser encontrada em [`/docs/apache_license_2-0.txt`](/docs/apache_license_2-0.txt).

Conforme requisitado na seção 4, item 'b', da licença _Apache License 2.0_, todos os arquivos obtidos da biblioteca BFT-Smart **que foram modificados** possuem um aviso claro afirmando que seu conteúdo foi modificado.

Conforme requisitado na seção 4, item 'c', da licença _Apache License 2.0_, todas as notificações de direitos autorais, patentes, marcas registradas e atribuições foram mantidas nos arquivos obtidos da biblioteca BFT-Smart.

A requisição feita na seção 4, item 'd', da licença _Apache License 2.0_, não se aplica a nosso projeto, pois a biblioteca BFT-Smart não possuía um arquivo de `NOTICE` na data em que seus arquivos foram incluídos nesse projeto.

Além disso, listamos abaixo todos os arquivos e pastas de arquivos obtidos da biblioteca BFT-Smart.

### Arquivos e pastas de arquivos utilizados sob licença

#### Arquivos e pastas de arquivos não modificados

* config/keysECDSA
* config/keysRSA
* config/keysSSL_TLS
* config/keysSunEC
* config/workloads
* config/java.security
* config/logback.xml
* lib/bcpkix-jdk15on-160.jar
* lib/bcprov-jdk15on-160.jar
* lib/bft-smart.jar
* lib/commons-codec-1.11.jar
* lib/core-0.1.4.jar
* lib/logback-classic-1.2.3.jar
* lib/logback-core-1.2.3.jar
* lib/netty-all-4.1.34.Final.jar
* lib/slf4j-api-1.7.25.jar

#### Arquivos e pastas de arquivos modificados

* config/hosts.config
* config/system.config

#### Arquivos da biblioteca consultados para a configuração do projeto

Os arquivos abaixo **não foram copiados para o projeto**, sendo encontrados apenas na biblioteca BFT-Smart. Entretanto, como seu conteúdo foi utilizado para configurar este projeto, decidimos listá-los abaixo por precaução.

* runscripts/smartrun.sh

## Outras bibliotecas externas
- `gson`: para converter data classes para json e vice versa
    - Licença Apache 2: https://github.com/google/gson/blob/master/LICENSE
    - https://www.javadoc.io/doc/com.google.code.gson/gson/2.6.2/index.html
    - https://github.com/google/gson
- `java-jwt`: Para codificar e decodificar jwt
    - Licença MIT: https://github.com/auth0/java-jwt/blob/master/LICENSE
    - https://javadoc.io/doc/com.auth0/java-jwt/latest/index.html
    - https://github.com/auth0/java-jwt

## Configuração do projeto

### IntelliJ

Para rodar o projeto no IntelliJ, será necessário realizar alguns passos de configuração do projeto.

1. Inclua todos os arquivos `.jar` da pasta `lib` nas bibliotecas utilizadas pelo projeto.

   Isso pode ser feito clicando com o botão direito no nome do projeto, escolhendo a opção "Open module settings (f4)" e navengando ao menu "Project settings -> Libraries".
   Lembre-se de colocar os arquivos `.jar` como uma biblioteca do tipo "Java".

2. Crie as configurações de construção e execução do projeto.

   Para isso, clique canto direito superior da tela, clique em "Add Configuration". As configurações devem ser do tipo "Application". Crie 3 configurações para a classe `BankingServer` e 1 para a classe `BankingClient`.
   Os servidores devem receber um argumento de id, o qual deve ser os números 0, 1 e 2.

3. Modifique as configurações de construção e execução do projeto para incluir as opções necessárias da máquina virtual.

   Talvez seja necessário habilitar o uso destas opções na tela de edição de configuração de construção e execução pelo link "Modify options" com a marcação da opção "Java -> Add VM options".
   O valor das opções de máquina virtual deve ser exatamente `-Djava.security.properties="./config/java.security"; -Dlogback.configurationFile="./config/logback.xml"`.
