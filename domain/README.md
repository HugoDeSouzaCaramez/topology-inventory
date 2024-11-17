# Project dev.hugodesouzacaramez/domain

Build the project and run all tests with `./mvnw package` or `mvnw.cmd package` for Windows.


Seguindo um exemplo real de um sistema que gerencia o inventário de topologia e rede de uma empresa
de telecomunicações, nesta parte você aprenderá como implementar os blocos de construção para criar
esse sistema usando ideias de arquitetura hexagonal.

Esta é uma parte prática em que teremos a oportunidade de colocar a mão na massa enquanto aplicamos
os princípios da arquitetura hexagonal. Começamos implementando o hexágono Domain, que contém o
modelo de domínio da topologia e do sistema de inventário. Em seguida, implementamos o hexágono
Application usando casos de uso e portas para expressar comportamentos do sistema. Para habilitar e
expor os recursos fornecidos pelo sistema hexagonal, usamos adaptadores para implementar o hexágono
Framework. Fechando esta parte, aprendemos como usar módulos Java para aplicar inversão de dependência em nosso sistema.

Esta parte tem os seguintes capítulos:
• Capítulo 6, Construindo o Hexágono de Domínio
• Capítulo 7, Construindo o Hexágono da Aplicação
• Capítulo 8, Construindo o Hexágono da Estrutura
• Capítulo 9, Aplicando Inversão de Dependência com Módulos Java


====================================================================================
====================================================================================
Hexagono de domínio

O projeto de aplicação hexagonal que começaremos neste capítulo é, na verdade, uma
continuação do sistema de topologia e inventário que desenvolvemos nos últimos capítulos. No
entanto, a diferença aqui é que aumentaremos algumas das capacidades do sistema e usaremos
o Java Platform Module System (JPMS) para encapsular o hexágono de domínio em um módulo Java.

Em capítulos anteriores, tivemos a oportunidade de empregar técnicas de Domain-Driven Design
(DDD), como entidades e objetos de valor, para criar um modelo de domínio. No entanto, até agora,
não tocamos na organização de pacotes, classes e módulos para se adequar ao propósito da arquitetura hexagonal.

O hexágono de Domínio é o lugar para começar a desenvolver uma aplicação hexagonal. Com base no domínio,
derivamos todos os outros hexágonos. Podemos dizer que o hexágono de Domínio é o cérebro dos sistemas
hexagonais porque a lógica de negócios fundamental reside em tal hexágono.

Então, neste capítulo, começaremos a explorar como estruturar um projeto de aplicativo hexagonal a partir do
zero usando uma abordagem de módulo Java. Isso nos ajudará a garantir melhor encapsulamento e teste de
unidade para validar nosso código conforme desenvolvemos os componentes hexagonais do Domain.

Abordaremos os seguintes tópicos neste capítulo:
• Bootstrapping do hexágono de domínio
• Compreendendo o domínio do problema
• Definindo objetos de valor
• Definição de entidades e especificações
• Definição de serviços de domínio
• Testando o hexágono de domínio

Ao final deste capítulo, você terá adquirido uma perspectiva prática sobre o desenvolvimento de todos os
componentes do Domain hexagon. Esse conhecimento permitirá que você cuide de todos os detalhes sobre a
estrutura e o arranjo de classes e pacotes no Domain hexagon.


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
Roteadores core lidam apenas com outros roteadores. É por isso que não há referência a switches na classe de
entidade CoreRouter.
Observe que os dois métodos, addRouter e removeRouter, operam diretamente em um parâmetro do
tipo Router , usando especificações de domínio para verificar se não há violações de restrição antes
de fazer qualquer alteração. Vamos examinar de perto as especificações usadas pela entidade
CoreRouter , começando com a especificação SameCountrySpec . Essa especificação garante que os
roteadores de borda sejam sempre do mesmo país que seus roteadores principais.
A especificação do pacote é onde colocaremos todas as especificações, então esse é o pacote no
qual colocaremos a especificação SameCountrySpec.
O construtor SameCountrySpec recebe um objeto Equipment , que usamos para inicializar o
campo privado do equipamento.
Continuando com a implementação SameCountrySpec , substituímos o método isSatisfiedBy.
A implementação SameCountrySpec não se aplica a roteadores core. É por isso que sempre retornamos
true quando o objeto é uma entidade CoreRouter . Caso contrário, prosseguimos com a validação
para verificar se o equipamento não está em um país diferente.
Usamos o método check para executar a especificação. Outras classes podem chamar esse método
para verificar se a especificação foi atendida ou não.
É possível conectar dois roteadores core de diferentes países. O que não é possível, como dito anteriormente, é conectar roteadores
edge e core que não estejam presentes no mesmo país. Note que esta especificação é baseada no tipo de equipamento , permitindonos reutilizar esta especificação não apenas com roteadores, mas também em switches.
A seguinte especificação SameIpSpec garante que nenhum equipamento tenha o mesmo endereço IP.
As especificações SameCountrySpec e SameIpSpec são usadas pelo método addRouter para garantir
que nenhuma restrição seja violada antes de adicionar qualquer roteador a um roteador principal.
As especificações SameCountrySpec e SameIpSpec são usadas pelo método addRouter para garantir
que nenhuma restrição seja violada antes de adicionar qualquer roteador a um roteador principal.
Seguindo em frente, temos as especificações EmptyRouterSpec e EmptySwitchSpec . Antes de um
roteador ser removido, precisamos ter certeza de que nenhum outro roteador ou switch esteja conectado a tal roteador.
Essas são especificações muito simples. Vamos começar olhando a especificação EmptyRouterSpec.
Esta especificação é baseada no tipo CoreRouter porque somente roteadores de núcleo podem ser conectados
a outros roteadores de núcleo e de borda.
A classe EmptySwitchSpec é muito similar à classe EmptyRouterSpec . A diferença, porém, é que somente
roteadores de borda podem ter switches. É por isso que essa especificação é baseada no tipo EdgeRouter.
O propósito do método addSwitch é conectar switches a roteadores de borda. Além disso, no EdgeRouter
No entanto, o contexto é diferente porque estamos adicionando um switch a um roteador. A única
especificação nova usada na classe EdgeRouter é a especificação EmptyNetworkSpec , que é usada para
garantir que todas as redes sejam removidas de um switch antes que ele possa ser removido de um roteador de borda.
classe, reutilizamos as mesmas especificações SameCountrySpec e SameIpSpec usadas
ao implementar a classe CoreRouter.
Para o método removeSwitch , temos a especificação EmptyNetworkSpec para garantir que um switch não tenha
redes conectadas a ele.
Assim como fizemos na classe CoreRouter , usamos as especificações SameCountrySpec e SameIpSpec.
No entanto, o contexto é diferente porque estamos adicionando um switch a um roteador. A única
especificação nova usada na classe EdgeRouter é a especificação EmptyNetworkSpec , que é usada para
garantir que todas as redes sejam removidas de um switch antes que ele possa ser removido de um roteador de borda.
O que resta agora é a implementação da classe de entidade Switch e suas especificações relacionadas. As ideias
que usamos aqui são semelhantes às que aplicamos em entidades de roteador de núcleo e borda. Vamos começar
criando uma classe de entidade Switch.
Começamos a implementação da classe Switch criando um predicado do método
getSwitchTypePredicate , que usamos para filtrar coleções de switches pelo tipo de switch.
Em seguida, criamos um método addNetworkToSwitch.
O método addNetworkToSwitch recebe um parâmetro do tipo Network , que usamos para
adicionar uma rede a um switch. No entanto, antes de adicionar a rede, precisamos
verificar algumas restrições expressas pelas especificações. A primeira é a especificação
NetworkAvailabilitySpec , que verifica se a rede já existe no switch. Em seguida, usamos a especificação CIDR
especificação para verificar se o CIDR da rede é válido. Finalmente, usamos o NetworkAmountSpec
especificação para validar se ultrapassamos o máximo de redes permitidas no switch.
Em seguida, temos o método removeNetworkFromSwitch.
Como não há restrições para remover redes de um switch, esse método não usa nenhuma especificação.
Para resumir, logo no início da classe Switch , declaramos um predicado para nos
permitir filtrar coleções de switches com base nos tipos de switch (LAYER2 e LAYER3).
O método addNetworktoSwitch usa NetworkAvailabilitySpec, NetworkAmountSpec e CIDRSpecification
especificações que já definimos no Capítulo 2, Envolvendo Regras de Negócios dentro do Hexágono de Domínio.
Se nenhuma das restrições dessas especificações for violada, um objeto de rede será adicionado ao switch.
Por fim, temos o método removeNetworkFromSwitch , que não analisa nenhuma especificação para remover
redes de um switch.
Com a implementação da entidade Switch , concluímos a modelagem das entidades e especificações
necessárias para atender à finalidade da topologia e do sistema de inventário.
===================================================


Com base nas entidades que acabamos de criar, podemos agora pensar em tarefas que não estão diretamente
relacionadas a tais entidades. Esse é o caso de serviços que funcionam como uma alternativa para fornecer
capacidades fora das entidades de domínio. Vamos agora ver como implementar serviços que nos permitem
encontrar, filtrar e recuperar dados do sistema.

========================================
Definindo serviços de domínio

O sistema de topologia e inventário é sobre a visualização e o gerenciamento de ativos de rede, então precisamos
habilitar um usuário para lidar com coleções de tais ativos de rede. Uma maneira de fazer isso é por meio de
serviços. Com serviços, podemos definir comportamentos para lidar com entidades do sistema e objetos de valor.
Todos os serviços que criaremos nesta seção residem no pacote de serviços.
Vamos começar criando um serviço para lidar com coleções de roteadores.
Serviço de roteador
Na seção anterior, ao implementar as entidades Router, CoreRouter e EdgeRouter , também criamos
alguns métodos para retornar predicados para nos ajudar a filtrar coleções de roteadores. Com
um serviço de domínio, podemos usar esses predicados para filtrar tais coleções.
Para o método filterAndRetrieveRouter , passamos uma lista de roteadores e um predicado, para
filtrar a lista, como parâmetros. Então, definimos um método findById para recuperar um roteador,
usando um parâmetro do tipo Id.
Serviço de switch
Este serviço segue a mesma ideia que aplicamos ao serviço de roteador. Ele é baseado principalmente no
predicado fornecido pelo método getSwitchTypePredicate para filtrar coleções de switches com base em seu tipo.
Conforme novos predicados surgem, podemos usá-los como novos critérios para filtrar coleções de switches.
Além disso, observe que o método findById é usado novamente para permitir a recuperação de switches com base no parâmetro do tipo.
Embora não modelemos a rede como entidades no modelo de domínio, não há problema em criar classes de
serviço para manipular coleções de objetos de valor de rede.
Vamos criar uma última classe de serviço para o sistema de topologia e inventário.
Serviço de rede
Este serviço é baseado principalmente na necessidade de filtrar coleções de rede com base no protocolo
IP. Podemos ter coleções de redes IPv4 e IPv6. Este serviço fornece a capacidade de filtrar tais coleções
com base no protocolo IP da rede.
O método filterAndRetrieveNetworks recebe uma lista de redes e um predicado, para filtrar a
lista, como parâmetros. Ele retorna uma lista filtrada de redes.
Com o NetworkService, concluímos a criação de serviços de domínio.
=========================================================================

Para conduzir o desenvolvimento de objetos de valor, entidades, especificações e serviços, você pode
adotar uma abordagem de Desenvolvimento Orientado a Testes (TDD), onde você pode começar a criar
testes quebrados e então implementar as classes e métodos corretos para fazer esses testes passarem.
Fizemos o contrário aqui para fornecer uma visão geral dos componentes que precisávamos criar para
construir o hexágono de Domínio para o sistema de topologia e inventário.


Nesta seção, criamos serviços que operam sob o nível hexagonal Domain. Em vez de colocar mais
comportamentos diretamente em entidades, criamos classes de serviço separadas para habilitar
comportamentos que não consideramos inerentemente parte das entidades. Esses serviços nos permitem
manipular coleções de roteadores, switches e redes.

=======================================================
Testando o hexágono de domínio

Para testar o hexágono de Domínio apropriadamente, devemos confiar apenas em seus componentes,
ignorando qualquer coisa vinda de outros hexágonos. Afinal, esses hexágonos devem depender do hexágono de Domínio
e não o contrário. Como já vimos, o hexágono de Domínio concentra-se na lógica do sistema central. É dessa
lógica que derivamos a estrutura e o comportamento dos hexágonos de Aplicação e Framework. Ao construir
um hexágono de Domínio robusto e bem testado, construímos uma base sólida para todo o sistema.
Entre as operações realizadas pelo sistema de topologia e inventário, podemos considerar adicionar, remover
e pesquisar ativos de rede como os mais importantes.
Vamos começar vendo como podemos testar a adição de equipamentos de rede...
O método addNetworkToSwitch verifica o caminho bem-sucedido quando o sistema pode
adicionar uma rede a um switch.
O método addNetworkToSwitch_failBecauseSameNetworkAddress verifica o
caminho sem sucesso quando tentamos adicionar uma rede que já existe no switch.
Em seguida, temos cenários de teste em que queremos adicionar um switch a um roteador de borda
Adicionamos um switch a um roteador de borda sem switches conectados; tal roteador de borda deve ter
exatamente um switch conectado a ele.
Quando tentamos adicionar um switch para um país diferente do roteador de borda, o método
addSwitchToEdgeRouter verifica o caminho bem-sucedido, enquanto o método
addSwitchToEdgeRouter_failBecauseEquipmentOfDifferentCountries verifica o caminho malsucedido.
Em seguida, temos cenários de teste onde queremos adicionar um roteador de borda a um roteador central.
O método addEdgeToCoreRouter verifica o caminho bem-sucedido quando tentamos adicionar um roteador de borda
que é para um país diferente do roteador principal.
O método addEdgeToCoreRouter_failBecauseRoutersOfDifferentCountries verifica o
caminho sem sucesso quando os roteadores de borda e de núcleo estão em países diferentes.
Em seguida, temos cenários de teste em que queremos adicionar um roteador principal a outro roteador principal.
O método addCoreToCoreRouter verifica o caminho bem-sucedido quando podemos adicionar um roteador de
núcleo a outro.
O método addCoreToCoreRouter_failBecauseRoutersOfSameIp verifica o caminho sem
sucesso quando tentamos adicionar roteadores principais com o mesmo endereço IP.
Com esses testes, também podemos verificar se as especificações funcionam conforme o esperado.
. Depois, há outros cenários em que é necessário remover qualquer roteador de um roteador principal, um switch
de um roteador de borda e uma rede de um switch.
O método de teste removeRouter verifica se podemos remover um roteador de borda de um
roteador de núcleo.
O método de teste removeSwitch verifica se podemos remover um switch de um roteador de borda.
O método de teste removeNetwork verifica se podemos remover uma rede de um switch.
Após as operações de adição e remoção, temos que testar as operações de filtro e recuperação.
Para filtrar roteadores por tipo, implementamos o seguinte teste.
O método filterRouterByType testa as operações disponíveis na classe RouterService .
No caso anterior, verificamos se o método filterAndRetrieveRouter pode realmente
filtrar e recuperar roteadores CORE ou EDGE de uma lista contendo diferentes tipos de roteadores.
Para filtrar roteadores por fornecedo... Usando um predicado fornecido pelo método getVendorPredicate , chamamos
filterAndRetrieveRouter da classe RouterService . Então, verificamos se o modelo de
roteador recuperado é o que estamos procurando.
Em seguida, testamos o mesmo método filterRouterByLocation , mas com um predicado diferente.
Ao chamar o método getCountryPredicate , recebemos o predicado para filtrar roteadores por país. O
resultado desse método é armazenado na variável actualCountry , que usamos na asserção de teste.
Em seguida, testamos o método filterRouterByModel.
O objetivo aqui é confirmar se o método filterAndRetrieveRouter funciona conforme o esperado
quando precisamos filtrar listas de roteadores com base no modelo do roteador.
Aqui, temos um teste para o método filterAndRetrieveSwitch da classe SwitchService...
O objetivo aqui é verificar se é possível filtrar listas de switch usando o predicado
fornecido pelo método getSwitchTypePredicate . Este é o predicado que usamos para
filtrar listas de switch por tipo. Finalmente, o método assertEquals verifica se o tipo de
switch esperado corresponde ao que esperamos.
Em seguida, testamos as operações para recuperar roteadores e switches usando seus IDs.
Com findRouterById, testamos o método findById do RouterService.
Por fim, implementamos o método findSwitchById.
Com findSwitchById, testamos o método findById do SwitchService.
A execução bem-sucedida desses testes nos garante que as operações mais fundamentais do hexágono de domínio
funcionam conforme o esperado.
Essa é a luz verde que precisamos para seguir em frente e começar o desenvolvimento do Application Hexagon.
TODOS OS TESTES VERDES
=========================================================================

Começamos bootstrapping
do hexágono Domain como um projeto Maven modularizado e usando o JPMS.
Analisamos e entendemos brevemente o domínio do problema, pois ele se relaciona ao gerenciamento de
ativos de rede. Então, traduzimos o domínio do problema em um modelo de domínio baseado em objetos de
valor, entidades, especificações e serviços. Finalmente, testamos tudo o que fizemos para garantir que as coisas não quebrem quando
começamos a desenvolver o hexágono da Aplicação em cima do hexágono do Domínio.
Ao aprender como desenvolver um hexágono de Domínio robusto, estabelecemos uma base sólida
na qual os hexágonos de Aplicação e Framework podem confiar.


=================================================
=================================================
Hexagono de aplicação

Uma vez que temos uma fundação fornecida pelo hexágono Domain, podemos construir a parte
restante do sistema em cima disso. É hora de pensar sobre como o sistema coordenará o manuseio
de diferentes dados e comportamentos para atender às necessidades de diferentes atores, e
exploraremos isso por meio de uma discussão de exemplos de casos de uso. Para fazer isso,
precisamos criar o hexágono Application em cima da fundação definida pelo hexágono Domain.

Para continuar construindo a estrutura modular iniciada no capítulo anterior, onde configuramos o hexágono de Domínio
como um módulo Java, continuaremos a usar a abordagem modular definindo o hexágono de Aplicação como o segundo
módulo Java do nosso sistema hexagonal.

Para fornecer uma melhor visão das capacidades do sistema, uma abordagem recomendada é usar o Cucumber, que é
uma tecnologia de desenvolvimento orientada a comportamento bem conhecida que usa conceitos como recursos e
cenários para descrever o comportamento do sistema. Então, para o hexágono Application, usaremos o Cucumber para
nos ajudar a moldar os casos de uso do sistema hexagonal.

O Cucumber nos permite testar o hexágono do aplicativo e explicar a estrutura dos casos de uso de uma maneira não
técnica.

Neste capítulo, aprenderemos sobre os seguintes tópicos:
• Inicializando o hexágono do aplicativo
• Definição de casos de uso
• Implementação de casos de uso com portas de entrada
• Testando o aplicativo hexagon


Ao final deste capítulo, você saberá como utilizar casos de uso como um modelo para conduzir o
desenvolvimento de todo o hexágono do aplicativo. Ao expressar a intenção do usuário por meio de casos de uso e derivar objetos
a partir deles para implementar portas, você será capaz de desenvolver o código para atingir objetivos de caso
de uso de forma estruturada.

=================================================
Inicializando o hexágono do aplicativo

O hexágono Application orquestra requisições internas por meio do hexágono Domain e
requisições externas por meio do hexágono Framework. Construímos os recursos do sistema com base no modelo de domínio
fornecido pelo hexágono Domain, com portas e casos de uso. No hexágono Application, não
especificamos nenhuma restrição ou regra de negócio. Em vez disso, nosso objetivo para o hexágono
Application é definir e controlar o fluxo de dados no sistema hexagonal.

cd topology-inventory
mvn archetype:generate -DarchetypeGroupId=de.rieckpil.archetypes -DarchetypeArtifactId=testing-toolkit -DarchetypeVersion=1.0.0 -DgroupId=dev.hugodesouzacaramez -DartifactId=application -Dversion=1.0-SNAPSHOT -Dpackage=dev.hugodesouzacaramez.topologyinventory.application -DinteractiveMode=false

O comando anterior cria a estrutura básica do projeto Maven para o hexágono Application.
Aqui, definimos a coordenada groupId do módulo como dev.hugodesouzacaramez e a versão como 1.0-
SNAPSHOT, as mesmas usadas para o projeto pai. Definimos artifactId como application
para identificar exclusivamente este módulo no projeto Maven.

O arquivo raiz pom.xml deve conter os módulos Maven de aplicação e domínio.

Após a criação do projeto do módulo Maven, precisamos configurar o Application hexagon
como um módulo Java criando o arquivo descritor do módulo em application/src/java/moduleinfo.java

Note que a primeira entrada requer – ela declara que o módulo application depende do
módulo domain . Precisamos adicionar a dependência Domain hexagon em application/pom.xml
As coordenadas Maven groupId, artifactId e version especificam os parâmetros corretos para
buscar o módulo Maven do hexágono de domínio.

Como utilizaremos o Cucumber para fornecer uma descrição escrita e também testar nossos casos de uso,
precisamos adicionar suas dependências ao application/pom.xml

Conforme declarado na introdução deste capítulo, usaremos o Cucumber para estruturar e testar casos de uso.
As dependências do Maven declaradas nos exemplos de código anteriores são necessárias para habilitar o
Cucumber no hexágono do aplicativo.
Depois que o módulo Maven e o módulo Java do Application Hexagon estiverem configurados corretamente para a
topologia e o sistema de inventário, podemos prosseguir e começar a definir casos de uso para o sistema.

==================================================
Definindo casos de uso

O sistema de topologia e inventário permite que os usuários gerenciem recursos de rede, como
roteadores, switches e redes. Para habilitar esse gerenciamento, criamos um modelo de domínio
no capítulo anterior que representa o relacionamento entre esses recursos. O que temos que fazer agora é construir os
recursos do sistema em termos do modelo de domínio. Esses recursos representam a intenção do usuário ao interagir com o
sistema.

Para tornar possível expressar casos de uso tanto na forma escrita quanto em código, usamos o Cucumber, uma
ferramenta valiosa para permitir que pessoas não técnicas entendam os casos de uso que existem no código.
Ao confiar em conceitos do Cucumber, como recursos e cenários, podemos criar descrições de casos de uso que
são fáceis de seguir. As descrições de casos de uso que são moldadas usando o Cucumber podem servir como
referências para desenvolver interfaces de casos de uso.

Antes de criar as interfaces de caso de uso para o sistema de topologia e inventário, primeiro precisamos estruturar os
casos de uso em arquivos de recursos consumidos pelo Cucumber. Os arquivos de recursos são onde descreveremos
uma sequência de instruções escritas que definem o caso de uso. Essa mesma descrição escrita é então usada durante
a implementação das classes para testar o caso de uso.
=======================================================

=================================================================================
Criação de descrições escritas para casos de uso de gerenciamento de roteador

Para começar, vamos criar o arquivo RouterAdd.feature , que descreve o caso de uso relacionado
à adição de roteadores ao sistema.
Este arquivo de recursos descreve dois cenários: o primeiro é quando um usuário deseja adicionar um roteador de borda
a um roteador principal; o segundo é quando o usuário deseja adicionar um roteador principal a outro roteador principal.

Depois disso, temos o arquivo RouterCreate.feature.
Aqui, temos dois cenários descrevendo a criação de roteadores de núcleo e de borda.

Por fim, temos o arquivo RouterRemove.feature.
Para cada um dos dois cenários descritos, definimos um conjunto específico de restrições para permitir a
remoção do roteador.

Uma vez que temos cenários Cucumber descrevendo os comportamentos suportados
em relação ao gerenciamento do roteador, podemos definir a interface do caso de uso que permitirá a implementação das operações.
Essas operações permitirão tais comportamentos.
===============================================


=====================================================================
Definindo a interface do caso de uso para gerenciamento de roteador

Uma boa interface de caso de uso para gerenciamento de roteador deve conter as operações que permitem
que o sistema cumpra os cenários descritos pelos arquivos RouterAdd.feature, RouterCreate.feature e
RouterRemove.feature. A interface de caso de uso RouterManagementUseCase é definida em referência aos cenários que
descrevemos nos arquivos de recursos do Cucumber.
O método createRouter é baseado no arquivo RouterCreate.feature Cucumber.
Os métodos addRouterToCoreRouter e removeRouterFromCoreRouter são para
Arquivos RouterAdd.feature e RouterRemove.feature , respectivamente. Agora, vamos prosseguir
para criar as descrições escritas para os casos de uso de gerenciamento de switch.
=======================================================

================================================================
Criação de descrições escritas para casos de uso de gerenciamento de switch

Começaremos criando o arquivo SwitchAdd.feature.
Este é um cenário de caso de uso muito direto. Dado que fornecemos um switch válido, podemos adicioná-lo
a um roteador de borda. Não há menção aos roteadores principais porque eles não devem receber conexões
de switch.

Em seguida, criamos o arquivo SwitchCreate.feature.
Este cenário é semelhante ao arquivo RouterCreate.feature , no sentido de que se fornecermos todos os dados
necessários, um novo objeto Switch será criado.

Por fim, criamos o arquivo SwitchRemove.feature.
Então, para remover um switch de um roteador de borda, temos que garantir que o switch não tenha redes conectadas
a ele. É isso que o cenário anterior afirma.

Agora, vamos definir a interface do caso de uso para gerenciamento de switches, com base nos cenários do Cucumber
que acabamos de criar.
===============================================================


================================================================
Definindo a interface do caso de uso para gerenciamento de switch

Assim como fizemos com os roteadores, faremos o mesmo para os switches, criando uma interface de caso de
uso para definir as operações de gerenciamento do switch, com base nas descrições escritas que fizemos anteriormente em nosso
Arquivos de recursos do Cucumber.
Os métodos createSwitch, addSwitchToEdgeRouter e removeSwitchFromEdgeRouter
correspondem aos arquivos de recurso Cucumber SwitchCreate.feature, SwitchAdd.feature
e SwitchRemove.feature , respectivamente. O método createSwitch recebe todos os
parâmetros necessários para construir um objeto Switch . Os métodos
addSwitchToEdgeRouter e removeSwitchFromEdgeRouter recebem um switch e um
roteador de borda como parâmetros, e ambos os métodos retornam EdgeRouter.

Para finalizar a definição dos casos de uso, ainda precisamos criar os arquivos de recursos e interfaces do
Cucumber para redes.
==============================================

========================================================
Criação de descrições escritas para casos de uso de gerenciamento de rede

Para redes, continuaremos a seguir o mesmo padrão das operações add, create e remove usadas anteriormente
em roteadores e switches. 

Vamos começar com o arquivo NetworkAdd.feature.
Este é um cenário simples para garantir que podemos adicionar redes a um switch.

Após a adição das redes, temos o arquivo NetworkCreate.feature.
Para a criação de redes, assim como fizemos com roteadores e switches, garantimos que todos os dados
necessários sejam fornecidos corretamente para que uma nova rede seja criada.

Por fim, temos o arquivo NetworkRemove.feature.
Ele segue a mesma estrutura do cenário de adição, mas verifica a capacidade do sistema de remover redes
de um switch.

Agora que temos cenários do Cucumber para gerenciamento de rede, vamos definir uma interface de caso de uso
para executar tais cenários.
=====================================================


====================================================
Criação de descrições escritas para casos de uso de gerenciamento de rede

A interface NetworkManagementUseCase segue a mesma estrutura das interfaces definidas
anteriormente, onde declaramos métodos para operações de criação, adição e remoção.

Aqui, novamente, declaramos os métodos createNetwork, addNetworkToSwitch e
removeNetworkFromSwitch com base nas descrições escritas dos arquivos de recursos
do Cucumber. Essas três declarações de método na interface NetworkManagementUseCase
representam o primeiro passo na implementação dos recursos que nos permitirão gerenciar redes, como
descrito nos cenários que criamos usando o Cucumber.
=======================================================

Nesta seção, aprendemos sobre uma abordagem para iniciar o desenvolvimento de casos de uso descrevendo
primeiro os comportamentos e cenários esperados do sistema. Depois que os cenários foram completamente
explorados, nós os utilizamos como referência para definir as interfaces de casos de uso que permitirão que
o sistema execute os comportamentos descritos nos cenários.
portas de saída. Uma vez que a lógica de negócios do hexágono de Domínio é aplicada aos dados, o hexágono de
Aplicativo move esses dados para jusante até que eles alcancem um dos adaptadores de saída no hexágono de Estrutura.
Agora que temos todas as interfaces de caso de uso para gerenciar roteadores, switches e redes, podemos fornecer
uma implementação de porta de entrada para cada uma dessas interfaces de caso de uso.

===============================================
Implementando casos de uso com portas de entrada

Portas de entrada são um elemento central do hexágono Application. Elas desempenham um
papel crucial de integração porque é por meio delas que fazemos a ponte entre os hexágonos Domain e Framework.
Podemos obter dados externos de uma porta de saída e encaminhar esses dados para o hexágono de domínio usando
portas de saída. Uma vez que a lógica de negócios do hexágono de Domínio é aplicada aos dados, o hexágono de
Aplicativo move esses dados para jusante até que eles alcancem um dos adaptadores de saída no hexágono de Estrutura.

Ao criar o hexágono do aplicativo, você pode definir interfaces de porta de saída, mas como ainda não há um
hexágono do Framework para fornecer um adaptador de saída como implementação, você não pode usar essas
portas de saída.

Você verá declarações de porta de saída no código a seguir, mas elas ainda não estão sendo usadas.
Estamos apenas preparando o hexágono Application para funcionar quando tivermos o hexágono Framework para fornecer
as implementações.

As etapas a seguir nos ajudarão a implementar casos de uso com portas de entrada.

Começamos criando um campo RouterManagementOutputPort na classe
RouterManagementInputPort.
Criamos este campo de interface RouterManagementOutputPort porque não queremos depender
diretamente de sua implementação. Lembre-se, adaptadores de saída implementam portas de saída.

Em seguida, implementamos o método createRouter.
Com o método createRouter , receberemos todos os parâmetros necessários para
construir um objeto Router . A criação do objeto é delegada ao método getRouter da classe RouterFactory.

Em seguida, implementamos o método retrieveRoute.
É um método muito simples que usa Id para obter os objetos Router , usando o método
retrieveRouter da porta de saída RouterManagementOutputPort.

Em seguida, implementamos o método persistRouter.
Para persistir um roteador, precisamos passar o objeto Router que queremos persistir. Esse método
é geralmente usado após qualquer operação que crie novos objetos Router ou cause alterações nos existentes.

Em seguida, implementamos o método addRouterToCoreRouter.
Para adicionar o roteador ao CoreRouter, chamamos o método addRouter do CoreRouter.
Não estamos persistindo Router porque não temos um adaptador que nos permita fazer isso.
Então, apenas retornamos o objeto Router adicionado.

Por fim, implementamos removeRouterFromCoreRouter.
Novamente, usamos um dos métodos presentes na classe CoreRoute . Aqui, chamamos
o método removeRouter para remover Router do CoreRouter. Então, retornamos
removedRouter, em vez de realmente removê-lo de uma fonte de dados externa.

O primeiro método que implementamos, createRouter, pode produzir roteadores core ou
edge. Para fazer isso, precisamos fornecer um método factory diretamente no hexágono
Domain, em uma classe chamada RouterFactory. A seguir está como implementamos esse método factory getRouter.
O parâmetro RouterType , que passamos para o método getRouter , tem apenas dois valores possíveis – CORE
e EDGE. O switch analisa um desses dois valores para determinar qual método builder usar. Se RouterType for
CORE, então o método builder do CoreRouter é chamado.
Caso contrário, o método builder do EdgeRouter é usado.
Se nem CORE nem EDGE forem informados, o comportamento padrão é lançar uma exceção dizendo que
nenhum tipo de roteador válido foi informado.


Vamos implementar a interface SwitchManagementUseCase com SwitchManagementInputPort

Começaremos implementando o método createSwitch.
Para o método createSwitch , não precisamos de um método factory para criar objetos porque
não há variações de objeto Switch em comparação com roteadores. Em vez disso, geramos
objetos Switch , usando o método builder diretamente da classe Switch.

Em seguida, implementamos o método addSwitchToEdgeRouter.
Então, temos addSwitchToEdgeRouter, que recebe Switch e EdgeRouter como parâmetros, para
adicionar switches a um roteador de borda. Não há como persistir switches sem persistir
roteadores também. É por isso que não colocamos um método de persistência aqui. Ao fazer isso,
impomos que todas as operações de persistência de switch ocorram somente quando persistimos roteadores.
Lembre-se de que o roteador é um agregado (um cluster de objetos de domínio) que controla o
ciclo de vida de outras entidades e objetos de valor, incluindo objetos do tipo Switch.

Por fim, implementamos o método removeSwitchFromEdgeRoute.
O último método, removeSwitchFromEdgeRouter, recebe os mesmos parâmetros,
Switch e EdgeRouter, e remove switches de roteadores de borda usando o método
removeSwitch presente em uma instância do EdgeRouter.


Agora, vamos ver como podemos implementar a interface NetworkManagementUseCase
com NetworkManagementInputPort.

Começamos implementando o método createNetwork.
Para criar uma nova rede, usamos todos os parâmetros do método recebidos em conjunto com o
método builder da classe Network.

Em seguida, implementamos addNetworkToSwitch.
Aqui, recebemos os objetos Network e Switch . Então, chamamos o método
addNetworkToSwitch no Switch passando o objeto Network como parâmetro. Então,
retornamos um objeto Switch com o objeto Network adicionado.

Por fim, implementamos o método removeNetworkFromSwitch.
Recebemos os objetos Network e Switch como parâmetros, como no método
addNetworkToSwitch . No entanto, para remover a rede de um switch, chamamos
removeNetworkFromSwitch do objeto Switch.
=====================================================

Isso conclui a implementação de portas de entrada para roteador, switch e gerenciamento de rede. Para garantir que
tudo funcione conforme o esperado, vamos criar testes Cucumber com base nas descrições de casos de uso escritas
e nas portas de entrada que acabamos de criar.

=======================================================
Implementando casos de uso com portas de entrada

Uma coisa interessante e útil sobre o Cucumber é que podemos usar a descrição de cenário escrita fornecida no
arquivo de recursos para personalizar testes de unidade. Além disso, esses cenários escritos fornecem uma maneira
fácil de entender e implementar os casos de uso do sistema hexagonal. Também estamos preparando o terreno para o
desenvolvimento de testes de unidade no hexágono do aplicativo.

=============================================
TESTES
Então, os testes que estamos prestes a construir nesta seção são uma continuação das descrições de cenário escritas
que criamos para as operações de roteador, switch e gerenciamento de rede. Nosso objetivo aqui é testar
implementações de porta de entrada para garantir que essas portas funcionem conforme o esperado quando os adaptadores de entrada as chamam.

Para começar, precisamos criar a classe de teste ApplicationTest para habilitar o Cucumber.
A parte importante é a anotação @RunWith , que aciona a inicialização do mecanismo Cucumber.

Vamos começar criando testes para verificar se o sistema é capaz de adicionar roteadores.
Da mesma forma que criamos um arquivo RouterAdd.feature , criaremos sua contraparte como uma
classe de teste RouterAdd.java.
As etapas a seguir mostram como adicionar um roteador de borda a um roteador principal:
1. O primeiro passo é obter um roteador de borda:
   Aqui, usamos o método createRouter de RouterManagementUseCase para criar objetos
   de roteador de borda. Precisamos converter o objeto retornado para um tipo
   EdgeRouter porque o método createRouter retorna Router. Então, para garantir que
   recebemos um objeto de roteador adequado, chamamos assertNotNull em edgeRouter.
2. Agora que temos o EdgeRouter, precisamos criar o CoreRouter usando o método createRouter.
   Este código segue exatamente o mesmo padrão do primeiro passo. A única diferença é que
   passamos CORE como RouterType para o método createRouter de RouterManagementUseCase
3. Com esses dois objetos, EdgeRouter e CoreRouter, agora podemos testar a adição do primeiro para o último.
   O método addRouterToCoreRouter recebe EdgeRouter e CoreRouter como parâmetros.
   No final do método, comparamos os IDs de roteador de borda reais e esperados para
   confirmar se o roteador de borda foi adicionado corretamente ao roteador de núcleo.


Para testar a execução das etapas do cenário Cucumber do RouterAdd.feature, precisamos
executar o seguinte comando Maven:
mvn test

O teste Cucumber passa pelos métodos de teste no arquivo RouterAdd.java na mesma
ordem em que foram declarados no arquivo RouterAdd.feature .

Agora, vamos ver como podemos implementar a classe de teste RouterCreate.java para o arquivo de recurso
RouterCreate.

Agora, vamos ver como podemos implementar a classe de teste RouterCreate.java para o arquivo de recurso
RouterCreate.

As etapas do cenário a seguir explicam como criar um novo roteador principal no sistema:
1. O primeiro passo é criar um novo roteador principal:
   Fornecemos todos os dados necessários para o método createRouter do
   RouterManagementUseCase para criar o novo roteador principal.
2. Em seguida, procedemos para confirmar se o roteador criado era de fato um roteador core:
   A primeira asserção verifica se recebemos um ponteiro nulo. A segunda asserção analisa o tipo do
   roteador para confirmar que é um roteador core.

As etapas do cenário a seguir envolvem verificar se podemos simplesmente criar um roteador de borda usando
o método createRouter do RouterManagementUseCase:
1. Primeiro, criamos um roteador de borda:
   Seguimos o mesmo procedimento para criar os objetos do roteador principal, mas agora definimos o
   parâmetro EDGE como RouterType para criação do objeto.
2. Na última etapa do cenário, apenas executamos as asserções:
   A primeira asserção verifica com o método assertNotNull se a referência do roteador não é nula. Então,
   ele prossegue executando assertEquals para verificar se o roteador criado é EdgeRouter.

Para executar os testes relacionados à criação de roteadores, executaremos o seguinte comando Maven no
diretório raiz do projeto:
mvn test

Agora que terminamos o cenário para criar roteadores, vamos ver como implementar a classe de
teste java RouterRemove. para o arquivo RouterRemove.feature.
Temos que criar os métodos para testar um cenário onde queremos remover um roteador de borda de um
roteador principal:
1. Para começar, primeiro precisamos saber se o roteador principal com o qual estamos trabalhando tem pelo menos
   um roteador de borda conectado a ele.
   A partir de um roteador core, buscamos um roteador edge conectado a ele. Então,
   armazenamos o roteador edge retornado na variável edgeRouter . Depois disso, afirmamos
   o tipo de roteador para confirmar se temos um roteador edge.
2. Em seguida, temos que verificar se não há redes anexadas ao switch conectado ao roteador de
      borda. Temos que verificar isso; caso contrário, não seremos capazes de remover o switch
      do roteador de borda:
   Para afirmar que um switch não tem redes conectadas a ele, primeiro verificamos o tamanho das
   redes no switch. Ele deve retornar 1. Então, removemos a rede e verificamos o tamanho novamente.
   Precisamos garantir que o switch não tenha redes conectadas a ele para que ele possa ser removido.
3. Em seguida, podemos prosseguir para verificar se não há switches conectados ao roteador de borda:
   Aqui, removemos o switch usando o método removeSwitch , seguido por uma asserção para confirmar
   que o roteador de borda não tem mais switches conectados.
4. Agora, podemos testar a remoção do roteador de borda do roteador principal:
   Para testar a remoção de um roteador de borda do roteador principal, primeiro obtemos o ID do
   roteador de borda do roteador que pretendemos remover. Armazenamos esse ID na variável actualID .
   Então, prosseguimos para a remoção real. O método removeRouterFromCoreRouter retorna o roteador
   removido. Então, podemos usar o ID do roteador removido, armazenado na variável expectedID , para
   verificar com o método assertEquals se o roteador foi realmente removido.

Concluímos a parte de teste do gerenciamento de roteadores. Para o gerenciamento de switches
e redes, seguimos as mesmas ideias.

para executar os testes automáticos do hexagono de domínio: mvn test
para executar os testes automáticos do hexagono de aplicação: executar o arquivo ApplicationTest
==================================================================


=============================================================
=============================================================
Construindo o hexagono de framework (hexagono de estrutura)

Ao construir um aplicativo hexagonal, o último passo consiste em expor os recursos do aplicativo
conectando adaptadores de entrada a portas de entrada. Além disso, se houver necessidade de obter dados de, ou persisti-los dentro,
sistemas externos, então precisamos conectar adaptadores de saída às portas de saída. O hexágono do
Framework é o lugar onde montamos todos os adaptadores necessários para fazer o sistema hexagonal.

Primeiro criamos o modelo de domínio usando coisas incluindo entidades, objetos de valor e
especificações no hexágono Domain. Então, no hexágono Application, expressamos a intenção
do usuário usando casos de uso e portas. Agora, no hexágono Framework, temos que empregar
adaptadores para expor recursos do sistema e definir quais tecnologias serão usadas para
habilitar tais recursos. Depois de montar os hexágonos Domain, Application e Framework, teremos uma arquitetura hexagonal montada completamente.

O que é tão atraente sobre a arquitetura hexagonal é que podemos adicionar e remover
adaptadores sem nos preocupar em mudar a lógica do sistema central encapsulada no hexágono
de Domínio. Claro, há um preço a ser pago na forma de tradução de dados entre entidades de domínio e entidades externas.
No entanto, em troca, ganhamos um sistema mais desacoplado, com limites claros entre seus
âmbitos de responsabilidades.

Neste capítulo, abordaremos os seguintes tópicos:
• Inicializando o hexágono do Framework
• Implementação de adaptadores de saída
• Implementação de adaptadores de entrada
• Testando o hexágono do Framework

Ao final deste capítulo, você terá aprendido a criar adaptadores de entrada para tornar os recursos do aplicativo
hexagonal acessíveis a outros usuários e sistemas. Além disso, você aprenderá a implementar adaptadores de saída
para permitir que o sistema hexagonal se comunique com fontes de dados externas.

======================================
Inicializando o hexágono do Framework

Ao construir um sistema usando arquitetura hexagonal, você não precisa decidir antecipadamente se a API do
sistema será exposta usando REST ou gRPC, nem se a fonte de dados primária do sistema será um banco de dados
MySQL ou MongoDB. Em vez disso, o que você precisa fazer é começar a modelar seu domínio de problema no
hexágono Domain, então projetar e implementar casos de uso no hexágono Application. Então, somente após criar
os dois hexágonos anteriores você precisa começar a pensar sobre quais tecnologias habilitarão as funcionalidades
do sistema hexagonal.

Uma abordagem hexagonal centrada em Domain-Driven Design nos permite adiar as
decisões sobre as tecnologias subjacentes internas ou externas ao sistema hexagonal. Outra prerrogativa
da abordagem hexagonal é a natureza plugável dos adaptadores. Se você quiser expor um determinado
recurso do sistema para ser acessível via REST, você cria e pluga um adaptador de entrada REST em uma porta de entrada.
Mais tarde, se você quiser expor esse mesmo recurso aos clientes que usam gRPC, você pode criar e conectar um
adaptador de entrada gRPC na mesma porta de entrada.

Ao lidar com fontes de dados externas, temos as mesmas prerrogativas plugáveis usando
adaptadores de saída. Você pode plugar diferentes adaptadores de saída na mesma porta de
saída, alterando a tecnologia de fonte de dados subjacente sem ter que refatorar muito todo o sistema hexagonal.

Para explorar mais os adaptadores de entrada, teremos uma discussão mais aprofundada no Capítulo 12, Usando
RESTEasy Reactive para Implementar Adaptadores de Entrada. Também investigaremos mais possibilidades para
adaptadores de saída no Capítulo 13, Persistindo Dados com Adaptadores de Saída e Hibernate Reactive.

Vamos nos ater ao básico e criar uma estrutura sólida para adaptadores de entrada e saída. Além dessa estrutura,
mais tarde, poderemos adicionar os recursos interessantes fornecidos pelo framework Quarkus.

Dando continuidade ao desenvolvimento da topologia e do sistema de inventário, precisamos inicializar o
Framework Hexagon como um módulo Maven e Java:
cd topology-inventory
mvn archetype:generate -DarchetypeGroupId=de.rieckpil.archetypes -DarchetypeArtifactId=testing-toolkit -DarchetypeVersion=1.0.0 -DgroupId=dev.hugodesouzacaramez -DartifactId=framework -Dversion=1.0-SNAPSHOT -Dpackage=dev.hugodesouzacaramez.topologyinventory.framework -DinteractiveMode=false

O objetivo mvn archetype:generate cria um módulo Maven chamado framework dentro
de topology-inventory. Este módulo vem com uma estrutura de diretório esqueleto
baseada no groupId e artificatId que passamos para o comando mvn . Além disso, ele inclui um pom filho.
rquivo xml dentro do diretório do framework .

Após executar o comando mvn para criar o módulo do framework , o pom.xml do projeto raiz
o arquivo será atualizado para conter o novo módulo.
O módulo framework é inserido no final como o último módulo que acabamos de adicionar.

Como o módulo do framework depende dos módulos de domínio e de aplicativo , precisamos
adicioná-los como dependências ao arquivo pom.xml do módulo do framework.

Deve haver um arquivo filho pom.xml no diretório do framework e um arquivo pai
pom.xml no diretório topology-inventory.

Depois de concluirmos a configuração do Maven, podemos criar o arquivo descriptor
que transforma o módulo Maven do framework em um módulo Java. Fazemos isso criando
o seguinte arquivo, topology- inventory/framework/src/java/module-info.java
Como adicionamos domínio e aplicativo como dependências Maven ao arquivo pom.xml do
framework , também podemos adicioná-los como dependências do módulo Java ao module-info.java
arquivo descritor.

Com os módulos Maven e Java configurados corretamente para o Framework hexagon, podemos prosseguir para a
criação dos adaptadores de saída para o sistema de topologia e inventário.

===================================
Implementando adaptadores de saída

Começaremos implementando os adaptadores de saída para configurar a integração entre nossa
topologia e sistema de inventário e a tecnologia de fonte de dados subjacente, que é um banco de dados H2 na memória
Também é importante implementar primeiro os adaptadores de saída porque nos referimos a eles ao implementar os
adaptadores de entrada.

O sistema de topologia e inventário permite a recuperação de dados externos para entidades de roteadores e
switches. Então, nesta seção, revisaremos as interfaces de porta de saída que obtêm dados externos relacionados a essas entidades.
Também forneceremos uma implementação de adaptador de saída para cada interface de porta de saída.

================================
Adaptador de saída de gerenciamento do roteador

O adaptador de saída de gerenciamento do roteador que precisamos criar deve implementar a
Interface RouterManagementOutputPort.
As assinaturas dos métodos retrieveRouter e removeRouter têm Id como parâmetro.
Usamos Id para identificar o roteador na fonte de dados subjacente. Então, temos a assinatura
do método persistRouter recebendo um parâmetro Router , que pode representar roteadores core e edge.
Usamos esse parâmetro do roteador para persistir os dados na fonte de dados.

Para o sistema de topologia e inventário, por enquanto, temos que implementar apenas um adaptador de saída
para permitir que o sistema use um banco de dados H2 na memória.

Começamos a implementação com a classe RouterManagementH2Adapter.
A conexão do banco de dados H2 é controlada pelo EntityManager. Essa conexão é configurada pelo
método setUpH2Database , que executamos quando chamamos o construtor vazio da classe. Usamos a
variável chamada instance para fornecer um singleton para que outros objetos possam disparar operações do banco de dados.

Vamos implementar cada método declarado na interface da porta de saída:
1. Começamos com o método retrieveRouter , que recebe Id como parâmetro:
   O método getReference do EntityManager é chamado com RouterData.class e o valor UUID
   é extraído do objeto Id . RouterData é uma classe de entidade de banco de dados que
   usamos para mapear dados vindos do banco de dados para a classe de entidade de
   domínio Router . Esse mapeamento é realizado pelo método routerDataToDomain da classe RouterH2Mapper.
2. Em seguida, implementamos o método removeRouter , que remove um roteador do banco de dados:
   Para remover um roteador, primeiro temos que recuperá-lo chamando o método getReference .
   Uma vez que temos um objeto RouterData representando a entidade do banco de dados, podemos
   chamar o método remove do EntityManager, que pode excluir o roteador do banco de dados.
3. Por fim, implementamos o método persistRouter:
   Ele recebe um objeto de entidade de domínio Router que precisa ser convertido em um objeto de
   entidade de banco de dados RouterData que pode ser persistido com o método persist do EntityManager.

Ao implementar os métodos retrieveRouter, removeRouter e persistRouter , fornecemos as
operações básicas de banco de dados necessárias para o sistema de topologia e inventário.

Vamos prosseguir para ver a implementação dos adaptadores de saída do switch.

======================================
Adaptador de saída de gerenciamento de switch

O adaptador de saída que implementamos para o switch é mais simples porque não precisamos persistir switches
diretamente ou removê-los. O único propósito do adaptador de saída do switch é habilitar a recuperação de switches
do banco de dados. Permitimos persistência somente por meio do adaptador de saída do roteador.

Para começar, vamos definir a interface SwitchManagementOutputPort.
Temos apenas um método chamado retrieveSwitch, que recebe Id e retorna Switch.

A implementação do adaptador de saída SwitchManagementH2Adapter é muito direta e similar
à sua contraparte de roteador. Então, vamos apenas avaliar a implementação do método retrieveSwitch.
Chamamos o método getReference do EntityManager com SwitchData.class e um valor UUID
como parâmetros para recuperar um objeto de entidade de banco de dados SwitchData . Então,
esse objeto é convertido em uma entidade de domínio Switch quando chamamos o método
switchDataToDomain da classe RouterH2Mapper.

Agora que temos RouterManagementH2Adapter e SwitchManagementH2Adapter
implementado corretamente, podemos prosseguir com a implementação dos adaptadores de entrada.








