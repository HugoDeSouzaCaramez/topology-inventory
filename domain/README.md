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