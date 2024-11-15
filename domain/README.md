# Project dev.hugodesouzacaramez/domain

Build the project and run all tests with `./mvnw package` or `mvnw.cmd package` for Windows.


mvn archetype:generate -DarchetypeGroupId=org.codehaus.mojo.archetypes -DarchetypeArtifactId=pom-root -DarchetypeVersion=RELEASE -DgroupId=dev.hugodesouzacaramez -DartifactId=topology-inventory -Dversion=1.0-SNAPSHOT -DinteractiveMode=false

Usamos o objetivo archetype:generate Maven para gerar um projeto raiz Maven para o
sistema. Ele cria um arquivo pom.xml com as coordenadas que passamos nos parâmetros
do comando, como groupId e artifactId.


cd topology-inventory
mvn archetype:generate -DarchetypeGroupId=de.rieckpil.archetypes -DarchetypeArtifactId=testing-toolkit -DarchetypeVersion=1.0.0 -DgroupId=dev.hugodesouzacaramez -DartifactId=domain -Dversion=1.0-SNAPSHOT -Dpackage=dev.hugodesouzacaramez.topologyinventory.domain -DinteractiveMode=false

Então, criamos um módulo para o hexágono de Domínio



inserimos o diretório raiz do projeto Maven de
inventário de topologia gerado na primeira etapa e, novamente, executamos o comando archetype:generate
Objetivo do Maven. O resultado é um módulo Maven chamado domínio que faz parte do
projeto topologia -inventário Maven.


Desde o lançamento do Java 9, é possível criar módulos colocando o module-info.
arquivo descritor de módulo java em um diretório raiz do projeto Java. Quando você cria um módulo Java
usando esse arquivo, você fecha o acesso a todos os pacotes públicos naquele módulo. Para tornar os
pacotes públicos acessíveis a outros módulos, você precisa exportar os pacotes desejados no arquivo
descritor de módulo.
Para transformar o hexágono de domínio em um módulo Java, você precisa criar um arquivo
descritor de módulo em topology-inventory/domain/src/java/module-info.java

Como ainda não estamos permitindo acesso a nenhum pacote público, nem dependendo de
outros módulos, deixaremos o arquivo module-info.java sem entradas.

Para criar não apenas o Domínio, mas também todos os outros hexágonos com classes menos detalhadas,
adicionaremos a biblioteca lombok à raiz do projeto pom.xml

Também é importante configurar os caminhos de processamento de anotação para lombok; caso
contrário, haverá falhas de compilação.

É dentro do bloco de plugin maven-compile-plugin que adicionamos a configuração para annotationProcessorPaths.

Como adicionamos a dependência lombok, precisamos atualizar o arquivo module-info.java do
domínio.

======================================
Compreendendo o domínio do cenário em questão:
Começaremos a modelar o domínio do problema considerando o fato de que um roteador core pode se
conectar a roteadores core e edge. Os roteadores edge, por sua vez, se conectam a switches e suas redes.
Os roteadores de núcleo são mais rápidos e lidam com altas cargas de tráfego, e não lidam diretamente com
o tráfego gerado por um switch e suas redes. Por outro lado, os roteadores de borda lidam diretamente com
o tráfego gerado por um switch e suas redes. Em nosso cenário, um roteador de borda não tem permissão
para se conectar a outros roteadores de borda; ele só pode se conectar a roteadores de núcleo e switches.
Um switch pode ter várias redes.

O objetivo do sistema de topologia e inventário é permitir que os usuários visualizem e gerenciem ativos de rede.
Por ativos de rede, queremos dizer roteadores, switches e redes – roteadores e switches sendo físicos
ativos, e redes sendo ativos lógicos fornecidos por switches. Esses ativos são espalhados por diferentes
locais, e o sistema deve mostrar a interconectividade entre ativos e seus sites. Um local é composto do
endereço completo, juntamente com sua latitude e longitude.
A parte de gerenciamento é baseada em nada mais do que operações do tipo Criar, Ler, Atualizar, Excluir
(CRUD), permitindo que os usuários exerçam controle sobre os dados dos sistemas de topologia e inventário.

Nossa abordagem para construir tal sistema é primeiro criar um hexágono de Domínio, usando um modelo
de domínio contendo as operações e regras necessárias para cumprir o propósito do sistema em seu nível mais alto.
Nossa intenção no nível mais alto é validar ideias de negócios diretamente no hexágono de Domínio sem a
ajuda de coisas presentes nos hexágonos de Aplicação e Framework. Conforme as coisas se movem para
esses hexágonos, elas tendem a se tornar mais específicas da tecnologia, operando em um nível mais
baixo, porque as coisas específicas da tecnologia estão muito longe do hexágono de Domínio. O grau em
que mantemos as funcionalidades do sistema central dentro do hexágono de Domínio influencia
fortemente o quão frouxamente acoplado o sistema hexagonal será.

Para validar os métodos e classes do hexágono de domínio, criaremos testes unitários para garantir que as
operações de domínio funcionem conforme o esperado. Isso nos dará um grau de garantia para seguir em frente e usar essas
operações no hexágono da aplicação.
==============================================


Entidades são os elementos
que usamos para classificar componentes do sistema que têm uma identidade. Por outro lado, os objetos de
valor não têm uma identidade. Usamos objetos de valor para descrever aquelas partes do sistema onde não
há necessidade de definir uma identidade. Então, temos agregados que servem para encapsular as entidades e os valores relacionados a elas.


Recomendo começar criando objetos de valor primeiro porque eles são como os blocos de construção, a matériaprima que usaremos para construir objetos de valor mais elaborados e, mais importante, as entidades. Agora,
adicionaremos todas as classes de objetos de volume (diretório vo para VOLUME) no módulo hexágono Domain, que foram criadas na seção
anterior quando inicializamos o hexágono Domain.

================================
Classes de VOLUME (objetos de valor) de domínio:
O código da classe Id é muito direto, com apenas um atributo UUID que usamos para
armazenar o valor id . Usaremos o método estático withId para criar instâncias de Id com uma string dada.

Se quisermos criar algo novo, devemos usar o método estático withoutId ,
que gera IDs aleatoriamente.

A classe de objeto de valor enum Vendor , como veremos na seção Definindo entidades e
especificações, é usada em classes de entidade de roteador e switch.

A classe de objeto de valor enum Vendor , como veremos na seção Definindo entidades e
especificações, é usada em classes de entidade de roteador e switch.
Modelaremos a classe Vendor como enum para ilustrar facilmente os recursos do sistema.
Faremos a mesma coisa com o enum Model.

Para o Protocolo, criamos um objeto de valor enum para representar o Protocolo da Internet.

Para nos ajudar a definir claramente com que tipo de roteador estamos lidando, criaremos um enum RouterType
A mesma ideia também é aplicada aos tipos de switch disponíveis

Como cada roteador e switch tem uma localização, temos que criar uma classe de objeto de valor Location.
Apresentamos o objeto de valor Location com atributos que nos permitem identificar um endereço de
forma única. É por isso que também temos latitude e longitude como atributos de classe.

Os objetos de valor que acabamos de criar são os mais importantes porque são os blocos de construção
básicos para os outros objetos de valor e entidades que compõem o sistema inteiro. Em seguida, podemos
criar objetos de valor mais elaborados com base naqueles que acabamos de criar.

Em seguida, podemos
criar objetos de valor mais elaborados com base naqueles que acabamos de criar.

Com a classe de objeto de valor IP , podemos criar endereços IPv4 e IPv6. A restrição que
verifica qual protocolo usar está dentro do construtor de objeto de valor. A lógica que
usamos para validar o endereço IP é simples, apenas para fins de nosso exemplo. Para
uma validação mais abrangente, podemos usar a classe InetAddressValidator da biblioteca commons-validator

Em seguida, criamos um objeto de valor para representar as redes que serão adicionadas a um switch
Modelamos o objeto de valor Network para armazenar o endereço IP, o nome da rede e os atributos
Classless Inter-Domain Routing (CIDR). CIDR é uma notação de endereço de rede composta de dois
números. O primeiro número (por exemplo, 10.0.0.0) é o endereço IP base da rede. O segundo número
(por exemplo, 24) é usado para determinar a máscara de sub-rede da rede e quantos endereços IP
estarão disponíveis nessa rede. Na classe Network , nos referimos ao segundo número CIDR.
Dentro do construtor Network , adicionamos a restrição para validar se o valor CIDR é válido.
Agora que vimos os objetos de valor, que são os blocos de construção do nosso hexágono de domínio,
podemos prosseguir para a criação de entidades e suas especificações.
=====================================

=====================================
Definindo entidades e especificações

Depois de criarmos todos os objetos de valor, podemos começar a pensar em como representar os elementos
em entidades que têm uma identidade. Além disso, precisamos desenvolver especificações para definir regras
de negócios que governam restrições que as entidades devem obedecer.
O que caracteriza uma entidade é sua identidade e a presença de regras de negócio e dados.
No sistema de topologia e inventário, temos como entidades Equipamento, Roteador e Switch.

Dentro do módulo Java de domínio que criamos anteriormente, adicionaremos as classes de entidade dentro de
um pacote chamado entity.

Roteadores e switches são tipos diferentes de equipamentos de rede, então começaremos criando uma
classe abstrata Equipment.

A maioria dos objetos de valor criados na seção anterior estão presentes aqui na entidade Equipamento .
Usamos o predicado fornecido por getVendorTypePredicate para aplicar os filtros que recuperam apenas o
equipamento de um vendor específico.

Derivando de Equipment, criamos uma classe abstrata Router.
A classe abstrata Router define predicados comuns a roteadores core ou edge. Usamos o
predicado fornecido por getRouterTypePredicate para aplicar filtros que recuperam apenas
roteadores de um tipo específico.
Usamos os predicados getModelPredicate e getCountryPredicate para recuperar roteadores de um
modelo específico ou de um país específico.
A classe abstrata Router fornece os atributos comuns compartilhados por roteadores core e edge. É na classe Router que
introduzimos os predicados para servir como filtros ao consultar listas de roteadores.

Os roteadores de núcleo podem ser conectados a outros roteadores de núcleo e de borda. Para permitir tal comportamento no CoreRouter
class, criamos um método addRouter recebendo o tipo abstrato Router como parâmetro. Também usamos
a especificação SameCountrySpec para garantir que os roteadores de borda estejam no mesmo país que
o roteador principal. Esta regra não se aplica quando tentamos conectar um roteador principal a outro roteador principal.
Em seguida, temos a especificação SameIPSpec para confirmar que os roteadores não têm o mesmo endereço IP.
Tornamos as regras de negócios mais explícitas e o código mais fácil de ler e entender usando
especificações. Você pode escrever esse código sem nenhuma especificação e apenas lançar
condições if-else com as variáveis necessárias, mas a carga mental necessária para entender o código
para qualquer um que não esteja familiarizado com ele provavelmente seria maior.
Para o método removeRouter , temos a especificação EmptyRouterSpec , que nos impede
de remover um roteador que tenha outros roteadores conectados a ele. A especificação
EmptySwitchSpec verifica se um roteador tem algum switch conectado a ele.

