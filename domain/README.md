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

==============================================
Implementando os adaptadores de entrada

Ao construir o hexágono Application, precisamos criar casos de uso e portas de entrada para expressar capacidades do sistema.
Para tornar essas capacidades disponíveis para usuários e outros sistemas, precisamos construir adaptadores de entrada e
conectá-los a portas de entrada.

Para o sistema de topologia e inventário, implementaremos um conjunto de adaptadores de entrada genéricos como Java POJOs.
Esses adaptadores de entrada genéricos são a base para a implementação tecnologicamente específica que ocorre no Capítulo
12, Using RESTEasy Reactive to Implement Input Adapters. Nesse capítulo, reimplementaremos os adaptadores de entrada
genéricos como adaptadores de entrada baseados em RESTEasy usando o framework Quarkus.

A função central do adaptador de entrada é receber solicitações de fora do sistema hexagonal e atendê -las usando uma porta de
entrada.

Continuando a desenvolver a topologia e o sistema de inventário, vamos implementar os adaptadores de entrada que recebem
solicitações relacionadas ao gerenciamento do roteador.

==================================
Adaptador de entrada de gerenciamento do roteador

Começamos criando a classe RouterManagementGenericAdapter.
Começamos a implementação do RouterManagementGenericAdapter declarando um atributo de classe para RouterManagementUseCase.
Em vez de usar uma referência de classe de porta de entrada, utilizamos a referência de interface de caso de uso,
RouterManagementUseCase, para conectar à porta de entrada.
No construtor de RouterManagementGenericAdapter, chamamos o método setPorts , que
instancia RouterManagementInputPort com um parâmetro RouterManagementH2Adapter
como uma porta de saída para conectar ao banco de dados H2 na memória que a porta de entrada usa.
O método setPorts armazena um objeto RouterManagementInputPort no atributo
RouterManagementUseCase que definimos anteriormente.
Após a inicialização da classe, precisamos criar os métodos que expõem as operações suportadas pelo
sistema hexagonal. A intenção aqui é receber a solicitação no adaptador de entrada e encaminhá-la para
uma porta de entrada usando sua referência de interface de caso de uso:
1. Aqui estão as operações para recuperar e remover roteadores do sistema:
   Os comentários são para nos lembrar que essas operações serão transformadas em endpoints REST
   ao integrar o Quarkus no sistema hexagonal. Tanto retrieveRouter quanto removeRouter recebem Id
   como parâmetro. Então, a solicitação é encaminhada para uma porta de entrada usando uma referência de caso de uso.
2. Então, temos a operação para criar um novo roteador:
   Na referência RouterManagementUseCase , primeiro chamamos o método createRouter
   para criar um novo roteador e, em seguida, o persistimos usando o método persistRouter.
3. Lembre-se de que no sistema de topologia e inventário, apenas roteadores core podem receber
   conexões de roteadores core e edge. Para permitir a adição e remoção de roteadores de ou
   para um roteador core, primeiro definimos a seguinte operação para adicionar roteadores:
   Para o método addRouterToCoreRouter , passamos as instâncias de Id dos roteadores como parâmetros
   que pretendemos adicionar junto com o Id do roteador principal de destino. Com esses IDs, chamamos
   o método retrieveRouter para obter os objetos do roteador da nossa fonte de dados. Assim que
   tivermos os objetos Router e CoreRouter , manipulamos a solicitação para a porta de entrada usando
   uma referência de caso de uso, chamando addRouterToCoreRouter para adicionar um roteador ao outro.
4. Depois disso, definimos a operação para remover roteadores de um roteador principal:
   Para o método removeRouterFromCoreRouter , seguimos os mesmos passos que aqueles para o método
   addRouterToCoreRouter . A única diferença, porém, é que no final, chamamos removeRouterFromCoreRouter
   do caso de uso para remover um roteador do outro.

Vamos agora criar o adaptador que manipula as operações relacionadas ao switch.

=======================================================
O adaptador de entrada de gerenciamento de switch

Antes de definir os métodos que expõem as operações relacionadas ao switch, precisamos configurar a inicialização
adequada da classe SwitchManagementGenericAdapter.
SwitchManagementGenericAdapter é conectado a duas portas de entrada – a primeira porta de entrada
é SwitchManagementInputPort de SwitchManagementUseCase, e a segunda porta de entrada é
RouterManagementInputPort de RouterManagementUseCase. É por isso que iniciamos a implementação
da classe declarando os atributos para SwitchManagementUseCase e RouterManagementUseCase.
Estamos conectando o adaptador de switch à porta de entrada do roteador porque queremos impor
que qualquer atividade de persistência aconteça somente por meio de um roteador. A entidade
Router , como um agregado, controla os ciclos de vida dos objetos que estão relacionados a ele.

Em seguida, implementamos o método setPorts.
Com o método setPorts , inicializamos ambas as portas de entrada com os adaptadores
SwitchManagementH2Adapter e RouterManagementH2Adapter para permitir acesso ao banco de dados H2 na memória.

Vamos ver como implementar os métodos que expõem as operações relacionadas ao switch:
1. Começamos com uma operação simples que apenas recupera um switch:
   O método retrieveSwitch recebe Id como parâmetro. Então, ele utiliza uma referência de caso de uso
   para encaminhar a solicitação para a porta de entrada.
2. Em seguida, temos um método que nos permite criar e adicionar um switch a um roteador de borda:
   Chamamos o método de porta de entrada do switch, createSwitch, passando os parâmetros recebidos pelo
   método createAndAddSwitchToEdgeRouter para criar um switch. Com routerId, recuperamos o roteador de
   borda chamando o método retrieveRouter da porta de entrada do roteador . Assim que tivermos os objetos
   Switch e EdgeRouter , podemos chamar o método addSwitchToEdgeRouter para adicionar o switch ao roteador
   de borda. Como última etapa, chamamos o método persistRouter para persistir a operação na fonte de dados.
3. Por fim, temos o método removeSwitchFromEdgeRouter , que nos permite remover
   um switch de um roteador de borda:
   removeSwitchFromEdgeRouter recebe Id como parâmetro para o switch e outro Id
   para o roteador de borda. Então, ele recupera o roteador chamando o método retrieveRouter.
   Com o ID do switch, ele recupera o objeto switch do objeto edge router. Depois
   de obter os objetos Switch e EdgeRouter , ele chama o método
   removeSwitchFromEdgeRouter para remover o switch do edge router.

O que resta agora é implementar o adaptador que lida com as redes de topologia e inventário.

==========================================
O adaptador de entrada de gerenciamento de rede

Assim como fizemos com os adaptadores de roteador e switch, vamos implementar a classe
NetworkManagementGenericAdapter definindo primeiro as portas de que ela precisa.
Além de NetworkManagementUseCase, também usamos SwitchManagementUseCase. Precisamos
chamar o método setPorts do construtor de NetworkManagementGenericAdapter para inicializar
corretamente os objetos de porta de entrada e atribuí-los às suas respectivas referências de caso de uso.
Veja a seguir como implementamos o método setPorts:
Como fizemos em implementações anteriores do adaptador de entrada, configuramos o método setPorts para
inicializar os objetos de porta de entrada e atribuí-los às referências de caso de uso.

Vamos implementar os métodos relacionados à rede:
1. Primeiro, implementamos o método addNetworkToSwitch para adicionar uma rede a um switch:
   O método addNetworkToSwitch recebe os objetos Network e Id como parâmetros.
   Para prosseguir, precisamos recuperar o objeto Switch chamando o método retrieveSwitch.
   Então, podemos chamar o método addNetworkToSwitch para adicionar a rede ao switch.
2. Em seguida, implementamos o método para remover uma rede de um switch:
   Primeiro, obtemos um objeto Switch chamando o método retrieveSwitch com o parâmetro Id.
   Para remover uma rede de um switch, usamos o nome da rede para encontrá-la em uma lista de
   redes anexadas ao switch. Fazemos isso chamando o método removeNetworkFromSwitch.

O adaptador para gerenciar redes é o último adaptador de entrada que temos que implementar. Com esses
três adaptadores, agora podemos gerenciar roteadores, switches e redes do hexágono do Framework. Para
garantir que esses adaptadores estejam funcionando bem, vamos criar alguns testes para eles.

================================================================
Testando o hexágono do Framework

Ao testar o hexágono do Framework, não só temos a oportunidade de verificar se os adaptadores de entrada
e saída estão funcionando bem, mas também podemos testar se os outros hexágonos, Domínio e Aplicação,
estão fazendo sua parte em resposta às solicitações vindas do hexágono do Framework.

Para testá-lo, chamamos os adaptadores de entrada para disparar a execução de tudo o que for necessário nos
hexágonos downstream para atender à solicitação. Começamos implementando testes para os adaptadores de gerenciamento do roteador.
Os testes para switches e redes seguem o mesmo padrão.

Para os roteadores, colocaremos nossos testes na classe RouterTest.
No construtor RouterTest , instanciamos a classe de adaptador de entrada
RouterManagementGenericAdapter que usamos para executar os testes. O método loadData
carrega alguns dados de teste da classe pai FrameworkTestData.
Uma vez configurados corretamente os requisitos dos testes, podemos prosseguir com os testes:
1. Primeiro, testamos a recuperação do roteador:
   Chamamos o adaptador de entrada, informando-o do id do roteador que queremos recuperar. Com
   assertEquals, comparamos o ID esperado com o ID real para ver se eles correspondem.
2. Para testar a criação do roteador, temos que implementar o método de teste createRouter:
   Do adaptador de entrada do roteador, chamamos o método createRouter para criar e persistir um novo
   roteador. Então, chamamos o método retrieveRouter com o ID gerado anteriormente pelo roteador que
   acabamos de criar. Finalmente, executamos assertEquals para confirmar se o roteador recuperado da
   fonte de dados é de fato o roteador que criamos.
3. Para testar a adição de um roteador a um roteador principal, temos o método de teste addRouterToCoreRouter:
   Passamos as variáveis, routerId e coreRouterId, como parâmetros para o método addRouterToCoreRouter
   do adaptador de entrada , que retorna um roteador principal. assertEquals verifica se o roteador
   principal tem o roteador que adicionamos.
4. Para testar a remoção de um roteador de um roteador principal:
   Este teste é muito semelhante ao anterior. Usamos novamente o routerId e o coreRouterId
   variáveis, mas agora também usamos o método removeRouterFromCoreRouter , que retorna o roteador
   removido. assertEquals verifica se o ID do roteador removido corresponde ao ID da variável routerId .

Para executar esses testes, execute o seguinte comando no diretório raiz do projeto Maven:
mvn test

Ao implementar os testes hexagonais do Framework, concluímos o desenvolvimento do hexagono do
Framework e de todo o backend do sistema de topologia e inventário. Pegando o que aprendemos neste
capítulo e nos capítulos anteriores, podemos aplicar todas as técnicas abordadas para criar um sistema
seguindo os princípios da arquitetura hexagonal.




====================================================================================
====================================================================================
Aplicando Inversão de Dependência com módulos Java

Nos capítulos anteriores, aprendemos como desenvolver cada hexágono como um módulo Java. Ao
fazer isso, começamos a impor o escopo e as responsabilidades de cada hexágono na arquitetura.
No entanto, não fomos muito longe na exploração dos recursos do módulo Java, como
encapsulamento e inversão de dependência, e como esses recursos podem aprimorar a estrutura
geral de um sistema hexagonal, tornando -o mais robusto e frouxamente acoplado.

Para entender o papel desempenhado pelo Java Platform Module System (JPMS) no desenvolvimento
de um sistema hexagonal, precisamos entender quais problemas o JPMS visa resolver. Uma vez que
saibamos o que podemos fazer com o JPMS em termos de encapsulamento e inversão de dependência,
podemos aplicar essas técnicas em conjunto com a arquitetura hexagonal.

Então, neste capítulo, aprenderemos como combinar o JPMS com a arquitetura hexagonal para criar
um sistema bem encapsulado com limites claramente definidos que são reforçados pela estrutura
modular do sistema e técnicas de inversão de dependência. Abordaremos os seguintes tópicos:
• Apresentando o JPMS
• Invertendo dependências em uma aplicação hexagonal
• Usando a classe ServiceLoader da plataforma Java para recuperar implementações do provedor JPMS

Ao final deste capítulo, você terá aprendido como usar serviços, consumidores e provedores do JPMS
para aplicar princípios de inversão de dependência e encapsulamento para um sistema hexagonal.


===========================================
Apresentando o JPMS

Antes do Java SE 9, o único mecanismo que tínhamos para lidar com dependências em Java era o classpath
parâmetro. O parâmetro classpath é onde colocamos dependências na forma de arquivos JAR.
No entanto, o problema é que não há como determinar de qual arquivo JAR uma dependência específica veio .
Se você tiver duas classes com o mesmo nome, no mesmo pacote e presentes em dois arquivos JAR diferentes,
um dos arquivos JAR seria carregado primeiro, fazendo com que um arquivo JAR fosse ofuscado pelo outro.

Shadowing é o termo que usamos para nos referir a uma situação em que dois ou mais arquivos JAR que
contêm a mesma dependência são colocados no parâmetro classpath , mas apenas um dos arquivos JAR
é carregado, sombreando o resto. Esse problema de emaranhamento de dependência JAR também é
conhecido como JAR hell. Um sintoma que indica que as coisas não estão tão boas com dependências que foram carregadas
no parametro classpath é quando vemos exceções inesperadas de ClassNotFoundException no tempo de execução do sistema.

O JPMS não pode evitar completamente problemas de JAR hell relacionados a incompatibilidades de versão
de dependência e shadowing. Ainda assim, a abordagem modular nos ajuda a ter uma visão melhor das
dependências que são necessárias para um sistema. Essa perspectiva de dependência mais ampla é útil para
prevenir e diagnosticar tais problemas de dependência.
Antes do JPMS, não havia como controlar o acesso a tipos públicos de diferentes arquivos JAR. O
comportamento padrão de uma Java Virtual Machine (JVM) é sempre tornar esses tipos públicos disponíveis
entre outros arquivos JAR, o que frequentemente leva a colisões envolvendo classes com o mesmo nome e pacote.

O JPMS introduziu o caminho do módulo e uma política de encapsulamento estrita que restringe,
por padrão, o acesso a todos os tipos públicos entre diferentes módulos. Não é mais possível
acessar todos os tipos públicos de outras dependências. Com o JPMS, o módulo precisa declarar
quais pacotes que contêm tipos públicos estão disponíveis para outros módulos. Fizemos isso usando a diretiva export
no modulo de hexagono de domínio.
module domain {
exports dev.hugodesouzacaramez.topologyinventory.domain.entity;
exports dev.hugodesouzacaramez.topologyinventory.domain.entity .factory;
exports dev.hugodesouzacaramez.topologyinventory.domain .service;
exports dev.hugodesouzacaramez.topologyinventory.domain .specification;
exports dev.hugodesouzacaramez.topologyinventory.domain.vo;
requires static lombok;
}

Então, para acessar o módulo hexágono de domínio , usamos a diretiva require no modulo de aplicação hexagonal.
module application {
requires domain;
}


Esse mecanismo de modularização monta o código em uma nova construção Java chamada módulo. Como
vimos anteriormente, o módulo pode ter que determinar qual pacote ele pretende exportar e quais outros
módulos ele requer. Nesse arranjo, temos mais controle sobre as coisas que nosso aplicativo expõe e
consome.

Se você está direcionando o desenvolvimento para ambientes baseados em nuvem e se importa com
desempenho e custo, a natureza do sistema de módulos permite que você construa um Java runtime
personalizado (conhecido no passado como JRE) contendo apenas os módulos necessários para executar o
aplicativo. Com um Java runtime menor, tanto o tempo de inicialização do aplicativo quanto o uso de memória
diminuirão. Digamos que estamos falando de centenas – até milhares – de pods Kubernetes executando
Java na nuvem. Com um Java runtime menor, podemos obter uma economia considerável em relação ao consumo de recursos computacionais.

Agora que estamos mais familiarizados com as motivações e benefícios do JPMS, vamos voltar a desenvolver
nossa topologia e sistema de inventário. Aprenderemos como usar recursos mais avançados do JPMS para
aprimorar o encapsulamento e a aderência aos princípios de inversão de dependência.


====================================================
Invertendo dependências em um aplicativo hexagonal

O princípio de inversão de dependência (DIP), conforme introduzido por Robert C. Martin, afirma que
componentes de alto nível não devem depender de componentes de baixo nível. Em vez disso,
ambos devem depender de abstrações. À primeira vista, para alguns, pode não ser tão óbvio
entender tal conceito. Afinal, o que os componentes de alto e baixo nível significam? E de que tipo de abstrações estamos falando?

Um componente de alto nível tem um conjunto de operações orquestradas para habilitar um comportamento de sistema
principal. Um componente de alto nível pode depender de componentes de baixo nível para fornecer um comportamento de sistema principal. Um
componente de baixo nível, por sua vez, utiliza um comportamento especializado que suporta os objetivos
de um componente de alto nível. Chamamos um pedaço de código cliente que atua como o componente de
alto nível porque ele depende e consome as funcionalidades fornecidas pelo componente de baixo nível.

O componente de alto nível pode ser um elemento concreto ou abstrato, enquanto o componente de baixo
nível deve ser concreto porque sempre fornece detalhes de implementação.

Vamos considerar algum código de cliente como um componente de alto nível que chama métodos em um
código de serviço. O código de serviço, por sua vez, pode ser considerado um componente de baixo nível. Este
componente de baixo nível contém os detalhes de implementação. Em designs de programação procedural, é
comum ver componentes de alto nível dependendo diretamente dos detalhes de implementação fornecidos por componentes de baixo nível.
Martin diz que essa dependência direta em detalhes de implementação é ruim porque torna o sistema
rígido. Por exemplo, se mudarmos esses detalhes de implementação nos componentes de baixo
nível, tais mudanças podem causar problemas imediatos para os componentes de alto nível que dependem diretamente deles.
É daí que vem essa rigidez: não podemos alterar uma parte do código sem causar efeitos colaterais em outras
partes.

Para inverter a dependência, precisamos fazer o componente de alto nível depender da mesma abstração da qual
o componente de baixo nível é derivado. Em designs orientados a objetos, podemos conseguir esse feito usando
classes ou interfaces abstratas. O componente de baixo nível implementa uma abstração, enquanto o componente
de alto nível se refere a essa abstração em vez da implementação de baixo nível. Então, é isso que temos que
fazer para inverter as dependências corretamente.

O JPMS introduziu um mecanismo para nos ajudar a evitar essa dependência em detalhes de
implementação. Esse mecanismo é baseado em consumidores, serviços e provedores. Além
desses três elementos do JPMS, há mais um, já conhecido em versões anteriores do Java,
chamado ServiceLoader, que permite ao sistema encontrar e recuperar implementações de uma dada abstração.

Chamamos um consumer com um módulo que declara a necessidade de consumir um serviço fornecido por um
módulo provider através da diretiva uses . Esta diretiva uses declara o nome de uma interface ou classe abstrata
que representa o serviço que pretendemos utilizar. O service, por sua vez, é o objeto que implementa a interface
ou estende a classe abstrata que é informada na diretiva uses . O provider é um módulo que declara a interface
do serviço e suas implementações com os providers e diretivas, respectivamente.

Vamos ver como podemos usar o JPMS para aplicar esse DIP ao nosso sistema hexagonal, topologia e inventário.
Também veremos uma representação para inverter dependências usando adaptadores de entrada, casos de uso e portas de entrada.


=============================================================
Fornecendo serviços com casos de uso e portas de entrada

Ao desenvolver a topologia e o sistema de inventário, projetamos casos de uso como interfaces e portas de
entrada como implementações para essas interfaces. Podemos considerar casos de uso e portas de entrada
como componentes de arquitetura hexagonal que correspondem à definição JPMS para um serviço. O módulo
hexágono Application pode ser considerado o módulo que fornece o serviço. E o consumidor? O módulo
hexágono Framework é o consumidor direto do módulo hexágono Application.

Com base nesse raciocínio, reimplementaremos os módulos hexagonais Application e Framework para que os
adaptadores de entrada do hexagono Framework não precisem mais depender das implementações de porta de
entrada do hexagono Application. Em vez disso, os adaptadores de entrada dependerão apenas dos tipos de
interface de caso de uso, em vez dos tipos concretos das portas de entrada. Em tal contexto, podemos considerar
os adaptadores de entrada como componentes de alto nível e as portas de entrada como componentes de baixo
nível. Os adaptadores de entrada referem-se às interfaces de caso de uso. As portas de entrada implementam esses casos de uso.

O diagrama anterior ilustra como podemos abordar a inversão de dependência na arquitetura
hexagonal. Este exemplo considera a inversão de dependência entre os hexágonos Framework
e Application , mas podemos fazer a mesma coisa com o hexágono Domain também.

Vamos considerar como o RouterManagementGenericAdapter está acessando atualmente os detalhes da
implementação em vez da abstração:
Ao chamar new RouterManagementInputPort(RouterManagementH2Adapter.
getInstance()), estamos fazendo com que o adaptador de entrada dependa dos
detalhes de implementação da porta de entrada RouterManagementInputPort e do
adaptador de saída expresso por RouterManagementH2Adapter.

Para tornar uma classe de porta de entrada elegível para ser usada como uma classe de provedor no JPMS, precisamos fazer o seguinte:
1. Primeiro, precisamos adicionar um construtor sem argumentos.
2. Então, devemos declarar o método setOutputPort na interface do caso de uso.
3. Por fim, devemos implementar o método setOutputPort na porta de entrada.

Agora, podemos atualizar o descritor do módulo do hexágono do aplicativo para definir os serviços que
forneceremos usando as interfaces de caso de uso e as implementações de suas portas de entrada.
Começamos declarando a dependência que o módulo de aplicação tem nos módulos Domain
hexagon e lombok . Em seguida, usamos exports para habilitar o acesso às portas de entrada, portas de saída e
casos de uso.
Em seguida, precisamos declarar os serviços que queremos fornecer. Podemos realizar essa declaração de serviço
fornecendo uma interface de caso de uso e a porta de entrada que a implementa. Vamos declarar o provedor de serviços
para gerenciamento de roteador.
No código anterior, RouterManagementUseCase está sendo fornecido por
RouterManagementInputPort.
Em seguida, precisamos definir o provedor de serviços para gerenciamento de switches.
No código anterior, SwitchManagementUseCase está sendo fornecido por
SwitchManagementInputPort.
Por fim, devemos declarar o provedor de serviços para gerenciamento de rede.
Aqui, temos o NetworkManagementUseCase sendo fornecido pelo NetworkManagementInputPort.

Antes de aprendermos como acessar essas portas de entrada por meio de serviços JPMS em adaptadores de
entrada, vamos aprender como podemos inverter dependências ao trabalhar com portas de saída e adaptadores de saída.


==================================================
Fornecendo serviços com portas de saída e adaptadores de saída

No hexágono do Framework, temos portas de saída como interfaces e adaptadores de saída como suas implementações.
As portas de entrada dependem das portas de saída. Nesse sentido, as portas de entrada podem ser consideradas componentes de alto nível
porque eles dependem das abstrações fornecidas pelas portas de saída. Os adaptadores de saída agem
como componentes de baixo nível que fornecem implementações para abstrações de porta de saída. O
diagrama a seguir mostra uma ilustração desse arranjo de inversão de dependência.

Observe que tanto a porta de entrada quanto o adaptador de saída apontam para a mesma abstração de porta de saída.
Isso significa que podemos usar o JPMS para aplicar o princípio de inversão de dependência com esses
componentes de arquitetura.

Entretanto, há um requisito que precisamos atender para usar adaptadores de saída como provedores de implementação.
Esse requisito exige que cada classe de provedor tenha um construtor público sem parâmetros, o que não
é o caso dos adaptadores de saída que implementamos nos capítulos anteriores.

Implementamos o construtor RouterManagementH2Adapter como privado para impor um padrão
singleton. Para mostrar como usar esse adaptador de saída como um provedor de serviço JPMS,
precisamos desabilitar o padrão singleton alterando o modificador de acesso do construtor de privado para público.

Agora, podemos atualizar o módulo do framework hexagon (o arquivo info.java ) para definir os serviços.
Começamos usando a diretiva require para declarar as dependências do módulo nos módulos
hexagon Domain e Application . Em seguida, usamos a diretiva exports para habilitar o acesso a
todos os tipos públicos no pacote dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.h2.data .
Usamos a diretiva opens para permitir acesso reflexivo em tempo de execução aos adaptadores
de saída. Precisamos desse acesso reflexivo por causa das dependências da biblioteca de banco
de dados que esses adaptadores de saída têm.
Por fim, usamos as diretivas provides e with para informar as interfaces de porta de
saída, RouterManagementOutputPort e SwitchManagementOutputPort, juntamente com
suas respectivas implementações de adaptador de saída, RouterManagementH2Adapter
e SwitchManagementH2Adapter.
Agora que concluímos a configuração necessária para habilitar a inversão de dependência entre as
portas de saída e os adaptadores, vamos aprender como configurar os adaptadores de entrada para
acessar dependências por meio de suas abstrações.


===========================================================
Tornando os adaptadores de entrada dependentes de abstrações

O primeiro passo para consumir os serviços que expusemos com as diretivas provides e with é atualizar
o descritor de módulo do módulo hexagon do framework do consumidor utilizando a diretiva uses .
Executaremos os seguintes passos para fazer isso:
1. Vamos começar atualizando o descritor do módulo:
   module framework {
   /** Code omitted **/
   uses dev.hugodesouzacaramez.topologyinventory.application
   .usecases
   .RouterManagementUseCase;
   uses dev.hugodesouzacaramez.topologyinventory.application
   .usecases
   .SwitchManagementUseCase;
   uses dev.hugodesouzacaramez.topologyinventory.application
   .usecases
   .NetworkManagementUseCase;
   uses dev.hugodesouzacaramez.topologyinventory.application
   .ports.output
   .RouterManagementOutputPort;
   uses dev.hugodesouzacaramez.topologyinventory.application
   .ports.output
   .SwitchManagementOutputPort;
   }
   As três primeiras diretivas uses apontam para os serviços fornecidos pelo módulo
   Application hexagon. As duas últimas diretivas uses referem-se aos serviços que
   expusemos no módulo Framework hexagon.
   Agora que temos os descritores de módulo adequadamente configurados para permitir que o
   sistema dependa de interfaces em vez de implementações, precisamos refatorar os adaptadores
   de entrada para que eles dependam apenas de interfaces de caso de uso do módulo hexágono
   do aplicativo e gerem as interfaces de porta do módulo hexágono do Framework.
2.  Primeiro, precisamos configurar o adaptador RouterManagementGenericAdapter :
    Note que RouterManagementGenericAdapter não depende mais de
    RouterManagementInputPort e RouterManagementH2Adapter, como acontecia
    anteriormente. Há apenas uma dependência na interface RouterManagementUseCase.
3. Para o adaptador de entrada SwitchManagementGenericAdapter , é assim que devemos
   configurar a dependência:
   O adaptador de entrada SwitchManagementGenericAdapter depende das interfaces
   de caso de uso RouterManagementUseCase e SwitchManagementUseCase para
   executar suas atividades.
4. Para concluir, temos que ajustar a classe do adaptador NetworkManagementGenericAdapter:
   O adaptador de entrada NetworkManagementGenericAdapter segue o mesmo padrão que
   usamos nos adaptadores de entrada anteriores e requer referências de caso de uso na entrada
   construtor do adaptador. Aqui, estamos usando as interfaces de caso de uso
   SwitchManagementUseCase e NetworkManagementUseCase.

Nesta seção, abordamos um recurso crucial do JPMS: provedores de serviço. Ao usá-los, podemos vincular
implementações de porta de entrada às interfaces de caso de uso. É assim que organizamos o código. Então, os
adaptadores de entrada podem contar com abstrações de caso de uso para disparar operações no hexágono Application.
Agora, vamos aprender como usar o ServiceLoader para recuperar implementações de serviços com base
nos provedores JPMS que definimos.


===================================================================================================
Usando a classe ServiceLoader da plataforma Java para recuperar Implementações do provedor JPMS

Até agora, configuramos o descritor de módulo dos módulos hexagonais Application e Framework.
Refatoramos os adaptadores de entrada para que eles dependam somente das abstrações fornecidas pelas
interfaces de caso de uso. Mas como podemos recuperar as instâncias concretas que implementam essas interfaces de caso de uso?
É exatamente isso que a classe ServiceLoader faz.

ServiceLoader não é uma nova classe feita exclusivamente para suportar recursos JPMS. Em vez disso, ServiceLoader
está presente no Java desde a versão 1.6. Do Java 9 em diante, essa classe foi aprimorada para trabalhar com os
serviços do módulo Java. Ela depende da configuração fornecida pelo descritor do módulo para encontrar
implementações para uma determinada interface do provedor de serviços.

Para ilustrar como podemos usar o ServiceLoader, vamos atualizar a classe de teste FrameworkTestData
criando um método chamado loadPortsAndUseCases. Este método usa o ServiceLoader para recuperar
os objetos que precisamos para instanciar os adaptadores de entrada. Precisamos criar o método loadPortsAndUseCases
porque o chamaremos para inicializar os adaptadores de entrada por meio do ServiceLoader.
Antes de criar o método loadPortsAndUseCases , precisamos declarar as variáveis do adaptador de entrada que usaremos
Vamos começar inicializando o RouterManagementGenericAdapter.
findFirst().get() para obter essa implementação.
As variáveis que declaramos aqui são usadas para armazenar referências para os adaptadores de entrada
que criaremos usando os objetos de portas de entrada e adaptadores de saída que obtivemos da classe ServiceLoader .
para atribuir os objetos que são instanciados com o auxílio do ServiceLoader.
As variáveis que declaramos aqui são usadas para armazenar referências para os adaptadores de entrada
que criaremos usando os objetos de portas de entrada e adaptadores de saída que obtivemos da classe ServiceLoader.

Vamos começar inicializando o RouterManagementGenericAdapter.


====================================================
Inicializando RouterManagementGenericAdapter

Começaremos a implementação do método loadPortsAndUseCases usando uma
instância ServiceLoader para recuperar os objetos que são necessários para
instanciar RouterManagementGenericAdapter. Executaremos as seguintes etapas para fazer isso:
1. O código a seguir mostra a implementação inicial do método loadPortsAndUseCases:
   protected void loadPortsAndUseCases() {
   // Load router implementations
   ServiceLoader<RouterManagementUseCase>
   loaderUseCaseRouter =
   ServiceLoader.load(RouterManagementUseCase.class);
   RouterManagementUseCase =
   loaderUseCaseRouter.findFirst().get();
   // Code omitted //
   }
   O método load do ServiceLoader recebe um RouterManagementUseCase.class
   como um parâmetro. Este método pode encontrar todas as implementações
   para a interface RouterManagementUseCase. Como RouterManagementInputPort é a
   única implementação disponível para a interface de caso de uso, podemos chamar loaderUseCaseRouter.findFirst().get() para obter essa implementação.
   Além de uma implementação adequada para a interface RouterManagementUseCase , também precisamos
   fornecer uma implementação para a interface RouterManagementOutputPort.
2. O código a seguir mostra como recuperar um objeto RouterManagementOutputPort:
   ServiceLoader<RouterManagementOutputPort> loaderOutputRouter = ServiceLoader.load(RouterManagementOutputPort.class);
   RouterManagementOutputPort = loaderOutputRouter.findFirst().get();
   A chamada em loaderOutputRouter.findFirst().get() recupera um objeto RouterManagementH2Adapter , que é a
   única implementação disponível para a interface RouterManagementOutputPort.
   Com os objetos RouterManagementInputPort e RouterManagementH2Adapter carregados do
   ServiceLoader, temos os objetos necessários para criar um adaptador de entrada. Mas
   primeiro, precisamos configurar a porta de saída para o caso de uso.
3. É assim que podemos definir um objeto RouterManagementOutputPort em RouterManagementUseCase:
   routerManagementUseCase.setOutputPort(routerManagementOutputPort);
   Ao chamar routerManagementUseCase.setOutputPort(routerManagementOutputPort), estamos definindo RouterManagementOutputPort em RouterManagementUseCase.
4. Agora, podemos criar um novo adaptador RouterManagementGenericAdapter passando RouterManagementUseCase,
   que acabamos de criar, para seu construtor:
   this.routerManagementGenericAdapter = new RouterManagementGenericAdapter(routerManagementUseCase);

Agora, vamos prosseguir e aprender como inicializar o SwitchManagementGenericAdapter.


=========================================================
Inicializando SwitchManagementGenericAdapter

Ainda dentro do método loadPortsAndUseCases , precisamos usar o ServiceLoader para encontrar uma implementação
disponível para SwitchManagementUseCase. Executaremos os seguintes passos pelo mesmo motivo:
1. No código a seguir, estamos recuperando uma implementação SwitchManagementUseCase:
   ServiceLoader<SwitchManagementUseCase> loaderUseCaseSwitch = ServiceLoader.load(SwitchManagementUseCase.class);
   SwitchManagementUseCase switchManagementUseCase = loaderUseCaseSwitch.findFirst().get();
   Ao chamar ServiceLoader.load(SwitchManagementUseCase.class), estamos
   recuperando um objeto ServiceLoader contendo todas as implementações
   disponíveis para SwitchManagementUseCase. No nosso caso, a única implementação
   disponível é a porta de entrada SwitchManagementInputPort. Para carregar tal
   implementação, devemos chamar loaderUseCaseSwitch.findFirst().get().
   Também precisamos de uma implementação para a porta de saída SwitchManagementOutputPort.
2. O código a seguir mostra como podemos obter uma implementação de SwitchManagementOutputPort:
   ServiceLoader<SwitchManagementOutputPort> loaderOutputSwitch = ServiceLoader.load(SwitchManagementOutputPort.class);
   SwitchManagementOutputPort = loaderOutputSwitch.findFirst().get();
   Adaptadores de saída implementam portas de saída. Então, para obter uma implementação
   de porta de saída, devemos chamar ServiceLoader.load(SwitchManagementOutputPort.class)
   para carregar a implementação SwitchManagementH2Adapter e então chamar loaderOutputSwitch.findFirst().get() para recuperar esse objeto de implementação.
3. Agora, podemos usar o objeto de porta de saída para defini-lo no caso de uso:
   switchManagementUseCase.setOutputPort(switchManagementOutputPort);
4. Finalmente, podemos iniciar o adaptador de entrada:
   this.switchManagementGenericAdapter = new SwitchManagementGenericAdapter(routerManagementUseCase, switchManagementUseCase);
   Para instanciar SwitchManagementGenericAdapter, precisamos passar referências
   para os casos de uso RouterManagementUseCase e SwitchManagementUseCase.

Agora, vamos prosseguir e aprender como inicializar o NetworkManagementGenericAdapter


======================================================================
Inicializando NetworkManagementGenericAdapter

Para NetworkManagementGenericAdapter, precisamos apenas carregar uma implementação
para NetworkManagementUseCase. Siga estas etapas para fazer isso:
1. O código a seguir mostra como devemos usar o ServiceLoader para obter um objeto NetworkManagementUseCase:
   ServiceLoader<NetworkManagementUseCase> loaderUseCaseNetwork = ServiceLoader.load(NetworkManagementUseCase.class);
   NetworkManagementUseCase networkManagementUseCase = loaderUseCaseNetwork.findFirst().get()
2. Então, devemos reutilizar RouterManagementOutputPort, que carregamos anteriormente, para definir
   NetworkManagementUseCase:
   networkManagementUseCase.setOutputPort(routerManagementOutputPort);
3. Finalmente, podemos iniciar o NetworkManagementGenericAdapter:
   this.networkManagementGenericAdapter = new NetworkManagementGenericAdapter(switchManagementUseCase, networkManagementUseCase);
   Para iniciar um novo adaptador NetworkManagementGenericAdapter , precisamos passar
   referências para os casos de uso SwitchManagementUseCase e NetworkManagementUseCase.


Esta seção nos ensinou como recuperar implementações de interface usando ServiceLoader em
conjunto com provedores de serviço JPMS. Com essa técnica, podemos estruturar código que
depende apenas de abstrações em vez de implementações.

===================================================================================================================================
===================================================================================================================================
===================================================================================================================================
Adicionando Quarkus a um Aplicativo Hexagonal modularizado

Este capítulo expandirá nossos horizontes explorando os conceitos e tecnologias para transformar
nosso aplicativo hexagonal em um nativo da nuvem. Para nos apoiar em nossa jornada para a nuvem, temos o Quarkus como
nossa técnologia chave, que é uma proeminente estrutura nativa em nuvem Java. Para entender o Quarkus e
aprender como alavancar seus recursos para aprimorar um sistema hexagonal, precisamos revisitar alguns
conhecimentos fundamentais relacionados ao funcionamento interno da Java Virtual Machine (JVM). Ao
entender as principais características da JVM e como elas funcionam, podemos entender melhor os problemas
que o Quarkus pretende resolver.
Neste capítulo, também faremos um breve tour pelos principais recursos do Quarkus para termos uma
ideia do que podemos fazer com um software tão bom. Depois que estivermos familiarizados com o
Quarkus, daremos o primeiro passo para transformar nosso sistema hexagonal em um nativo da nuvem.
Para fazer isso, criaremos um módulo Java totalmente novo e configuraremos as dependências do Quarkus.
Estes são os tópicos que abordaremos neste capítulo:
• Revisitando a JVM
• Apresentando o Quarkus
• Adicionando Quarkus a uma aplicação hexagonal modularizada

Ao final deste capítulo, você saberá como configurar o Quarkus para funcionar com um aplicativo hexagonal.
Esse é o primeiro passo na preparação de um sistema para receber todos os recursos nativos da nuvem que o Quarkus tem a oferecer.

=======================================
Revisitando a JVM

O conceito de Máquina Virtual (VM) não era algo novo quando Java chegou em 1995. Antes disso, muitas
outras linguagens usavam VMs, embora elas não fossem tão populares entre os desenvolvedores. Os
arquitetos Java decidiram usar VMs porque queriam um mecanismo para criar independência de
plataforma para melhorar a produtividade do desenvolvedor.

Antes de elaborar o conceito de VM, vamos primeiro verificar o que podemos executar dentro de uma
VM para Java. Em linguagens como C ou C++, compilamos o código-fonte em código nativo adaptado
para um sistema operacional específico e arquitetura de CPU. Ao programar em Java, compilamos o código-fonte em bytecode.

A ideia da VM vem do conceito de executar programas em um ambiente intermediário ou virtual sobre
uma máquina real. Em tal arranjo, o programa não precisa se comunicar diretamente com o sistema
operacional subjacente – o programa lida apenas com uma VM. A VM então converte instruções de
bytecode em instruções de código nativo.

Podemos expressar uma das vantagens da JVM com um lema Java bem conhecido: escreva uma vez, execute em qualquer lugar.
Antigamente, e acho que ainda hoje, era muito atraente usar uma linguagem que permitisse desenvolver
software que, sem recompilação, pudesse rodar em diferentes sistemas operacionais e arquiteturas de
CPU. Para outras linguagens como C++, você precisaria ajustar seu código para cada sistema operacional
e arquitetura de CPU alvo, solicitando mais esforço para tornar seu programa compatível com diferentes
plataformas.

No mundo atual da computação em nuvem, temos serviços como Docker e Kubernetes que tornam as
unidades de software mais portáteis do que nunca. Para atingir a portabilidade em Java, temos a
prerrogativa de executar o mesmo bytecode compilado em diferentes JVMs em execução em diferentes
sistemas operacionais e arquiteturas de CPU. A portabilidade é possível porque cada implementação de
JVM deve estar em conformidade com a especificação de JVM, não importa onde ou como ela seja implementada.

Por outro lado, podemos usar a virtualização de contêiner para atingir a portabilidade ao empacotar o software
compilado com seu ambiente de tempo de execução e dependências em uma imagem de contêiner. Um mecanismo
de contêiner em execução em diferentes sistemas operacionais e arquiteturas de CPU pode criar contêineres com base em imagens de coneiner.

O apelo da JVM em fazer software portátil às custas da conversão de bytecode em código nativo não é
mais atraente quando você tem alternativas mais rápidas e baratas. Hoje, você pode empacotar seu
aplicativo – sem a necessidade de uma JVM e também recompilação – em uma imagem Docker e
distribuí-la entre diferentes sistemas operacionais e arquiteturas de CPU. No entanto, não devemos
esquecer o quão robusto e testado pelo tempo é um pedaço de software como a JVM. Retornaremos à
nossa discussão sobre Docker e Kubernetes em breve, mas por enquanto, vamos examinar algumas características mais interessante.

Outro aspecto importante da JVM está relacionado ao gerenciamento de memória. Com Java, um desenvolvedor
não precisa se preocupar sobre como o programa lida com liberação e alocação de memória. Tal responsabilidade
é transferida para a JVM, então o desenvolvedor pode se concentrar mais nos detalhes funcionais do programa
do que nos técnicos. Pergunte a qualquer desenvolvedor C++ o quanto é divertido depurar vazamentos de memória em sistemas grandes.

O recurso responsável por gerenciar a memória dentro da JVM é chamado de coletor de lixo. Sua finalidade é verificar
automaticamente quando um objeto não é mais usado ou referenciado para que o programa possa liberar a memória
não utilizada. Uma JVM pode usar algoritmos que rastreiam referências de objetos e marcam para liberar aqueles que
não referenciam mais nenhum objeto. Existem diferentes algoritmos de coletor de lixo, como o Concurrent Mark and
Sweep (CMS) e o Garbage First Garbage Collector (G1 GC). Desde o JDK7 Update 4, o G1 GC substituiu o CMS devido
à sua ênfase em primeiro identificar e liberar as regiões de heap de objetos Java quase vazias, disponibilizando mais
memória e fazendo isso mais rápido do que a abordagem do CMS.

Não é necessário que coletores de lixo existam em todas as implementações de JVM, mas enquanto os recursos de
memória continuarem sendo uma restrição na computação, frequentemente veremos implementações de JVM com coletores de lixo.

A JVM também é responsável por todo o ciclo de vida de um aplicativo. Tudo começa com o carregamento de
um arquivo de classe Java na VM. Quando compilamos um arquivo de origem Java, o compilador gera um
arquivo de classe Java contendo bytecode. Bytecode é um formato reconhecível pela JVM. O objetivo principal
de uma VM é carregar e processar esse bytecode por meio de algoritmos e estruturas de dados que implementam e respeitam uma especificação JVM.

Tudo começa com o arquivo de código-fonte Java que é compilado em um arquivo de classe Java (bytecode)
pelo compilador Java. Esse bytecode é lido pela JVM e traduzido em instruções que são entendidas pelo SO
nativo.
Essa questão do bytecode tem sido objeto de trabalho incansável por parte de pessoas que tentam encontrar maneiras mais
rápidas de lidar com isso.

Com o passar do tempo, a JVM recebeu boas melhorias e técnicas aprimoradas que melhoraram
consideravelmente o desempenho do carregamento de bytecode. Entre essas técnicas, podemos citar Just-in-Time
(JIT) e compilações Ahead-of-Time (AOT). Vamos examinar ambas.

================
Acelerando o desempenho do tempo de execução com compilação JIT

Os compiladores JIT surgiram da ideia de que certas instruções de programa podem ser otimizadas
para melhor desempenho enquanto um programa está em execução. Então, para realizar tal
otimização, o compilador JIT busca instruções de programa com potencial para serem otimizadas.
Em geral, essas instruções são as mais executadas pelo programa.

Como essas instruções são executadas com tanta frequência, elas consomem uma quantidade significativa
de tempo e recursos do computador. Lembre-se de que essas instruções estão no formato bytecode. Um
compilador tradicional compilaria todo o bytecode em código nativo antes de executar o programa. Com
um compilador JIT, as coisas são diferentes.

Um compilador JIT seleciona, usando seus algoritmos de otimização dinâmica, algumas partes do bytecode.
Então, ele compila e aplica otimizações a essas partes do bytecode. O resultado é um código nativo
otimizado que é ajustado para fornecer melhor desempenho para o sistema. O termo JIT é usado porque
as otimizações são feitas logo antes do código ser executado.

No entanto, não existe almoço grátis ao usar compiladores JIT. Uma das desvantagens mais conhecidas
dos compiladores JIT é o aumento do tempo de inicialização de um aplicativo devido às otimizações
iniciais que um compilador JIT faz antes de executar o programa. Para superar esse problema de
inicialização, há outra técnica chamada compilação AOT. Vários frameworks nativos da nuvem, incluindo
Quarkus, usaram essa técnica. Vamos ver como a compilação AOT funciona.

=====================
Melhorando o tempo de inicialização com compilação AOT

AOT é tão atraente no cenário Java porque os sistemas Java tradicionais – principalmente aqueles baseados
em servidores de aplicativos corporativos como JBoss e WebLogic – levam muito tempo para iniciar. Além
dos tempos de inicialização mais lentos, temos que considerar a quantidade de poder de computação que esses servidores de aplicação
consomem. Essas características são um obstáculo para qualquer um que queira migrar cargas de
trabalho Java para a nuvem, onde instâncias e Pods do Kubernetes são trazidos para cima e para baixo
freneticamente. Então, ao empregar AOT em Java, desistimos da capacidade multiplataforma fornecida
pela JVM e seu bytecode para um melhor desempenho fornecido pela AOT e seu código nativo. O problema multiplataforma é
mitigado até certo ponto com o uso de tecnologias de contêiner, como Docker e Kubernetes.

Nem tudo é uma vantagem com AOT em Java. Um compilador AOT gasta mais tempo gerando um binário nativo
do que um compilador Java precisa para criar classes de bytecode. Portanto, a compilação AOT pode ter um
impacto considerável nos pipelines de Integração Contínua (CI). Além disso, o desenvolvedor precisa fazer algumas
até certo ponto com o uso de tecnologias de contêiner, como Docker e Kubernetes.
trabalho adicional para fazer as coisas funcionarem corretamente para usar reflexão. GraalVM é o compilador
AOT usado para fornecer um binário nativo para Java e outras linguagens baseadas em JVM.

Com o Quarkus, temos a prerrogativa de criar aplicativos usando métodos de compilação JIT ou AOT. Cabe a nós
decidir qual técnica atende melhor às nossas necessidades.

Nesta seção, ganhamos algum conhecimento de fundo sobre o funcionamento interno da JVM e
como ela tenta melhorar o carregamento de bytecode com compilação JIT e AOT. Esse conhecimento
é importante para entender como o Quarkus funciona por baixo dos panos e obtém melhorias consideráveis de desempenho.

Agora que estamos familiarizados com alguns fundamentos da JVM e técnicas essenciais de compilação, vamos
nos aprofundar e aprender mais sobre os principais recursos do Quarkus.

===============================
Apresentando Quarkus

Se você desenvolve aplicações Java corporativas, você já trabalhou com Spring Boot. Testado
pelo tempo e amplamente usado na indústria, Spring Boot é um software robusto com uma comunidade vibrante.
Suas bibliotecas aumentam a produtividade desenvolvida ao fornecer soluções prontas para uso para
segurança, persistência, APIs e muitas outras coisas que um aplicativo empresarial típico requer. Você pode
se perguntar por que este livro não discute o Spring Boot, mas o Quarkus. Há dois motivos. Primeiro, há mais
material disponível cobrindo o Spring Boot do que o Quarkus, o que é compreensível, pois o Spring Boot
existe há mais tempo e tem uma comunidade maior. O segundo motivo é que o Quarkus foi construído com
desenvolvimento nativo da nuvem em seu núcleo, enquanto o Spring Boot foi adaptado a ele. E como este
livro se concentra no desenvolvimento nativo da nuvem com arquitetura hexagonal, o Quarkus foi escolhido
porque é um framework que prioriza a nuvem.

Focado em desempenho, o Quarkus vem com suporte integrado para executáveis nativos baseados no
GraalVM, possibilitando tempos de inicialização rápidos.

Para atrair desenvolvedores, ele oferece coisas valiosas, como desenvolvimento ao vivo, um recurso
que aumenta a produtividade ao evitar a necessidade de reiniciar um aplicativo sempre que algo muda no seu código.

Visando ambientes nativos da nuvem, o Quarkus vem equipado com as ferramentas adequadas, permitindo
que você lide com restrições e aproveite os benefícios que vêm ao desenvolver software para execução em
ambientes baseados em contêineres, como o Kubernetes.

Tomando emprestadas boas ideias do desenvolvimento empresarial, o Quarkus é construído
sobre padrões bem estabelecidos, como a estrutura Contexts and Dependency Injection (CDI),
a especificação Jakarta Persistence API (JPA) com implementação Hibernate ORM e os Jakarta RESTful Web Service
(JAX-RS) especificação implementada pelo RESTEasy. Para aqueles imersos no Java Enterprise Edition
(EE) mundo, isso significa que a curva de aprendizado para dominar o Quarkus é superficial porque muito do
conhecimento de desenvolvimento empresarial já adquirido pode ser reutilizado para desenvolver aplicativos Quarkus.

Criado pela Red Hat, o Quarkus se diferencia de seus concorrentes por ser um framework de desenvolvimento
de software projetado do zero para lidar com tecnologias de nuvem. Ao contrário de outros frameworks mais
antigos que trazem código boilerplate e recursos de uma era mais antiga, o Quarkus se apresenta como um
software novo e moderno.

Construído sobre outros projetos de código aberto bem estabelecidos, Quarkus é a estrutura nativa da nuvem
que usaremos para preparar nosso sistema hexagonal para a nuvem. Antes disso, porém, exploraremos alguns dos principais
recursos que este framework fornece. Vamos começar olhando primeiro como criar endpoints REST com
Quarkus.

======================
Criando endpoints REST com JAX-RS

mvn archetype:generate -D groupId=dev.hugodesouzacaramez -D artifactId=bootstrap -D interactiveMode=false

É muito simples criar endpoints REST usando Quarkus. Para fazer isso, o framework depende de
uma implementação JAX-RS chamada RESTEasy. Essa implementação está disponível na seguinte
dependência Maven:
<dependency>
<groupId>io.quarkus</groupId>
<artifactId>quarkus-resteasy</artifactId>
</dependency>

Veja o exemplo a seguir, que mostra como usar RESTEasy para criar serviços REST:
package dev.hugodesouzacaramez.bootstrap.samples;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
@Path("/app")
public class RestExample {
@GET
@Path("/simple-rest")
@Produces(MediaType.TEXT_PLAIN)
public String simpleRest() {
return "This REST endpoint is provided by Quarkus";
}
}

Definimos o endereço do endpoint com a anotação @Path . Com @GET, definimos o método
HTTP suportado por esse endpoint. Com @Produces, definimos o tipo de retorno para a solicitação.
Nesta mesma classe RestExample , podemos injetar dependências para serem usadas junto
com os endpoints REST. Vamos ver como fazer isso.

=================================
Empregando injeção de dependência com Quarkus DI

O Quarkus tem seu próprio mecanismo de injeção de dependência baseado no Quarkus ArC, que, por
sua vez, vem da especificação CDI, que tem suas raízes no Java EE 6. Com o CDI, não precisamos
mais controlar a criação e o ciclo de vida dos objetos de dependência que fornecemos a um sistema.
Sem uma estrutura de injeção de dependência, você tem que criar objetos desta forma:
BeanExample beanExample = new BeanExample();

Ao usar CDI, você só precisa anotar o atributo class com a anotação @Inject , assim:
@Inject
BeanExample beanExample

Para que a anotação @Inject funcione, primeiro precisamos declarar a dependência como um bean gerenciado.
Veja o exemplo aqui:
package dev.hugodesouzacaramez.bootstrap.samples;
import javax.enterprise.context.ApplicationScoped;
import javax.validation.Valid;
@ApplicationScoped
public class BeanExample {
public String simpleBean() {
return "This is a simple bean";
}
}

A anotação @ApplicationScoped afirma que esse bean estará disponível enquanto o aplicativo não
for encerrado. Além disso, esse bean é acessível a partir de diferentes solicitações e chamadas
em todo o sistema. Vamos atualizar nosso RestExample para injetar esse bean, como segue:
package dev.hugodesouzacaramez.bootstrap.samples;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
@Path("/app")
public class RestExample {
@Inject
BeanExample beanExample;
/** Code omitted **/
@GET
@Path("/simple-bean")
@Produces(MediaType.TEXT_PLAIN)
public String simpleBean() {
return beanExample.simpleBean();
}
}

Logo no topo, injetamos a dependência BeanExample com a anotação @Inject . Então,
chamamos o método simpleBean da dependência BeanExample injetada .

A seguir, vamos ver como validar objetos que são criados quando o sistema recebe uma solicitação HTTP.

===========================
Validando objetos

Aprendemos como criar endpoints REST e também como injetar dependências no aplicativo.
Mas e quanto à validação de objetos? Como podemos garantir que os dados fornecidos por uma determinada solicitação são válidos?
O Quarkus pode nos ajudar nessa questão. O mecanismo de validação do Quarkus está disponível na seguinte
dependência do Maven:
<dependency>
<groupId>io.quarkus</groupId>
<artifactId>quarkus-hibernate-validator</artifactId>
</dependency>

O mecanismo de validação do Quarkus é baseado no Hibernate Validator.
Para ver como isso funciona, vamos primeiro criar um objeto de exemplo contendo os campos que esperamos em uma
solicitação, como segue:

package dev.hugodesouzacaramez.bootstrap.samples;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
public class SampleObject {
@NotBlank(message = "The field cannot be empty")
public String field;
@Min(message = "The minimum value is 10", value = 10)
public int value;
}

Com a anotação @NotBlank , afirmamos que a variável field nunca deve estar vazia. Então, usando a anotação @Min ,
garantimos que a variável value deve sempre conter um número igual ou maior que 10. Vamos retornar à classe RestExample
e criar um novo endpoint REST para validar a requisição, como segue:
@POST
@Path("/request-validation")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public Result validation(@Valid SampleObject sampleObject) {
try {
return new Result("The request data is valid!");
} catch (ConstraintViolationException e) {
return new Result(e.getConstraintViolations());
}
}

Quando ConstraintViolationException é detectado, o sistema retorna uma resposta de falha HTTP 400 Bad Request.

Observe a anotação @Valid logo antes de SampleObject. Ao usar essa anotação, acionamos uma
verificação de validação sempre que uma solicitação atinge o endpoint /app/request-validation .
Confira os seguintes resultados:
$ curl -H "Content-Type: application/json" -d '{"field": "", "value":
10}' localhost:8080/app/request-validation | jq
{
"exception": null,
"propertyViolations": [],
"classViolations": [],
"parameterViolations": [
{
"constraintType": "PARAMETER",
"path": "validation.arg0.field",
"message": "The field cannot be empty",
"value": ""
}
],
"returnValueViolations": []
}

limpar cache: mvn dependency:purge-local-repository


Compilar e empacotar o projeto:
mvn clean package

Este comando usa o Maven para compilar e empacotar o projeto.

mvn clean: Remove os arquivos gerados anteriormente (build anterior).
mvn package: Compila o código, executa testes (se configurados) e cria um pacote executável (JAR, WAR, etc.).
O resultado será um arquivo .jar gerado no diretório target/, pronto para ser executado.

Executar o aplicativo:
java -jar bootstrap/target/bootstrap-1.0-SNAPSHOT-runner.jar

Este comando executa o aplicativo gerado no passo anterior.

java -jar: Executa um arquivo JAR.
bootstrap/target/bootstrap-1.0-SNAPSHOT-runner.jar: Caminho para o JAR gerado no processo de empacotamento.
Quando este comando é executado, o servidor Quarkus é iniciado e estará disponível (geralmente em http://localhost:8080).

Testar a aplicação (pode ser via postman também):
Exemplo 1: Criar uma entidade
Comando:

bash
Copiar código
curl -vv -H "Content-Type: application/json" -d '{"field": "item-a", "value": 20}' localhost:8080/app/create-entity
Este comando faz uma requisição HTTP POST para o endpoint /app/create-entity e envia os dados JSON:

json
Copiar código
{
"field": "item-a",
"value": 20
}
-vv: Exibe informações detalhadas da requisição.
-H "Content-Type: application/json": Define o cabeçalho da requisição como JSON.
-d: Especifica o corpo da requisição (payload).
Exemplo 2: Obter todas as entidades
Comando:

bash
Copiar código
curl -s localhost:8080/app/get-all-entities | jq
Este comando faz uma requisição HTTP GET para o endpoint /app/get-all-entities para listar todas as entidades criadas.

curl -s: Faz a requisição sem exibir informações adicionais.
| jq: Formata e exibe a saída JSON no terminal (é necessário ter o utilitário jq instalado).

Resumo do processo
Compile e empacote o projeto com mvn clean package.
Execute o servidor com java -jar bootstrap/target/bootstrap-1.0-SNAPSHOT-runner.jar.
Envie requisições para o servidor usando os comandos curl.
Essas etapas ajudam a validar se o projeto está funcionando corretamente. Se houver dúvidas ou erros ao seguir o processo, compartilhe os detalhes para que eu possa ajudar.


Na solicitação POST anterior , o campo está vazio, o que resulta em uma resposta de falha com
um código HTTP 400 Bad Request .
Na próxima solicitação, definimos o valor como um número menor que 10, da seguinte maneira:
$ curl -s -H "Content-Type: application/json" -d '{"field": "test",
"value": 9}' localhost:8080/app/request-validation | jq
{
"exception": null,
"propertyViolations": [],
"classViolations": [],
"parameterViolations": []
{
"constraintType": "PARAMETER",
"path": "validation.arg0.value",
"message": "The minimum value is 10",
"value": "9"
}
],
"returnValueViolations": []
}

Novamente, a restrição foi violada, e o resultado mostrou que a validação falhou. A falha foi causada porque
enviamos o número 9, e o valor mínimo aceito é 10.

Aqui está uma solicitação adequada com dados válidos:
$ curl -s -H "Tipo de conteúdo: aplicativo/json" -d '{"campo": "teste", "valor": 10}' localhost:8080/app/
validação-de-solicitação | jq {
"message": "Os dados da solicitação são válidos!",

"sucesso": verdadeiro
}

O parâmetro de campo não é nulo, nem o valor é menor que 10. Portanto, a solicitação retorna uma resposta válida.

==============================================================
Configurando uma fonte de dados e usando o Hibernate ORM

O Quarkus permite que você se conecte a uma fonte de dados de duas maneiras. A primeira e tradicional
maneira é baseada em uma conexão JDBC. Para conectar usando esse método, você precisa da biblioteca agroal
e do driver JDBC do tipo de banco de dados específico que você deseja conectar. A segunda maneira – e
reativa – permite que você trate a conexão do banco de dados como um fluxo de dados. Para esse modo, você precisa dos drivers reativos Vert.x

Nas etapas a seguir, configuraremos uma conexão de fonte de dados usando o método JDBC tradicional:

1. Para começar, precisamos das seguintes dependências:
   <dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-agroal</artifactId>
   </dependency>
   <dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-jdbc-h2</artifactId>
   </dependency>
   <dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-hibernate-orm</artifactId>
   </dependency>

quarkus-hibernate-orm se refere à implementação Hibernate ORM do JPA. É essa dependência
que fornece a capacidade de mapear objetos Java para entidades de banco de dados.

2. Em seguida, precisamos configurar as configurações da fonte de dados no arquivo application.properties,
   como segue:
   quarkus.datasource.db-kind=h2
   quarkus.datasource.jdbc.url=jdbc:h2:mem:de
   fault;DB_CLOSE_DELAY=-1
   quarkus.hibernate-orm.dialect=org.hibernate.dia
   lect.H2Dialect
   quarkus.hibernate-orm.database.generation=drop-and-
   create

quarkus.datasource.db-kind é opcional, mas usamos isso para enfatizar que o
aplicativo usa um banco de dados H2 na memória. Usamos quarkus.datasource.jdbc.url
para informar a string de conexão. A opção quarkus.hibernate-orm.dialect define o
dialeto usado para a comunicação da fonte de dados e quarkus.hibernate-orm.database.generation=drop-and-create força a criação de uma estrutura de banco de dados na inicialização.

Se houver um arquivo import.sql no classpath, esta opção drop-and-create habilita o uso desse
arquivo para carregar dados no banco de dados. Algo muito interessante sobre esta opção
drop - and-create é que cada alteração em entidades do aplicativo ou no arquivo import.sql é
selecionada automaticamente e aplicada ao banco de dados sem reiniciar o sistema. Para que
isso funcione , um sistema precisa ser executado no modo de desenvolvimento ao vivo.

Vamos criar uma classe SampleEntity para persistir no banco de dados, como segue:
@Entity
@NamedQuery(name = "SampleEntity.findAll",
query = "SELECT f FROM SampleEntity f ORDER BY
f.field",
hints = @QueryHint(name =
"org.hibernate.cacheable",
value = "true") )
public class SampleEntity {
@Id
@GeneratedValue(strategy = GenerationType.AUTO)
private Long id;
@Getter
@Setter
private String field;
@Getter
@Setter
private int value;
}

A classe SampleEntity corresponde à classe SampleObject que criamos anteriormente. O requisito
para usar a classe SampleEntity como uma entidade de banco de dados é anotá-la com a anotação
@Entity . Seguindo essa anotação, temos @NamedQuery, que usaremos mais tarde para
recuperar todas as entidades do banco de dados. Para gerar automaticamente valores de ID, usaremos GenerationType.AUTO.
As variáveis de campo e valor de SampleEntity são mapeadas para as mesmas
variáveis que existem na classe SampleObject.

Vamos agora criar um novo bean chamado PersistenceExample para nos ajudar a criar e recuperar entidades
de banco de dados. Veja como fazer isso:
package dev.hugodesouzacaramez.bootstrap.samples;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
@ApplicationScoped
public class PersistenceExample {
@Inject
EntityManager em;
/** Code omitted **/
}

Para interagir com o banco de dados, a primeira coisa que temos que fazer é injetar o EntityManager.
O Quarkus cuidará de recuperar um objeto EntityManager com todas as configurações de conexão do
banco de dados que fornecemos no arquivo application.properties . Continuando a implementação do
PersistenceExample , vamos criar um método para persistir entidades, como segue:
@Transactional
public String createEntity(SampleObject sampleObject) {
SampleEntity sampleEntity = new SampleEntity();
sampleEntity.setField(sampleObject.field);
sampleEntity.setValue(sampleObject.value);
em.persist(sampleEntity);
return "Entity with field "+sampleObject.field+"
created!";
}

O método createEntity persiste uma entidade no banco de dados.

A anotação @Transactional acima da declaração do método fará com que o objeto EntityManager
limpe a transação assim que a operação do banco de dados for confirmada. Isso é ilustrado
no seguinte trecho de código:
@Transactional
public List<SampleEntity> getAllEntities(){
return em.createNamedQuery(
"SampleEntity.findAll", SampleEntity.class)
.getResultList();
}

O método getAllEntities recupera todas as entidades do banco de dados.

Agora, vamos retornar ao RestExample para criar endpoints REST para disparar a criação e
recuperação de entidades do banco de dados. Começaremos injetando PersistenceExample para que
possamos usar esse bean para iniciar operações no banco de dados. O código é ilustrado no seguinte snippet:
@Inject
PersistenceExample persistenceExample;

Em seguida, criamos um endpoint /create-entity, como segue:
@POST
@Path("/create-entity")
@Produces(MediaType.TEXT_PLAIN)
@Consumes(MediaType.APPLICATION_JSON)
public String persistData(@Valid SampleObject sampleObject) {
return persistenceExample.createEntity(sampleObject);
}

Passamos SampleObject como parâmetro. Este objeto representa o corpo da requisição POST .

Por fim, criamos um endpoint /get-all-entities para recuperar todas as entidades do banco de dados, da seguinte
maneira:
@GET
@Path("/get-all-entities")
public List<SampleEntity> retrieveAllEntities() {
return persistenceExample.getAllEntities();
}

O método retrieveAllEntities chama getAllEntities do PersistenceExample bean. O resultado é uma lista de objetos SampleEntity.

Vamos ver o que obtemos quando clicamos em /create-entity para criar uma nova entidade. Você pode ver a saída aqui:
$ curl -s -H "Content-Type: application/json" -d '{"field": "item-a",
"value": 10}' localhost:8080/app/create-entity
Entity with field item-a created!
$ curl -s -H "Content-Type: application/json" -d '{"field": "item-b",
"value": 20}' localhost:8080/app/create-entity
Entity with field item-b created!

Para ver as entidades que criamos, enviamos uma solicitação para /get-all-entities, da seguinte maneira:
$ curl -s localhost:8080/app/get-all-entities | jq
[
{
"field": "item-a",
"value": 10
},
{
"field": "item-b",
"value": 20
}
]

Como esperado, recebemos todas as entidades que persistimos anteriormente no banco de dados em um formato JSON

Quarkus é uma estrutura vasta e em constante crescimento que está absorvendo cada vez mais recursos.
Os recursos que vimos abrangem algumas das coisas básicas necessárias para desenvolver aplicativos modernos.

Poderemos usar RESTEasy ao reimplementar adaptadores de entrada para dar suporte a REST em nosso aplicativo
hexagonal. O Quarkus DI nos permitirá gerenciar melhor o ciclo de vida de objetos dos hexágonos Framework e
Application. Os mecanismos de validação do Quarkus contribuirão para validar os dados que entram no sistema
hexagonal. A configuração da fonte de dados e o Hibernate ORM darão suporte à reestruturação dos adaptadores
de saída.

Nesta seção, aprendemos como ajustar o arquivo application.properties para configurar uma conexão de
banco de dados no Quarkus, e exploramos brevemente os recursos ORM do Hibernate que ajudam a
mapear classes Java para entidades de banco de dados. Exploraremos esse assunto mais a fundo no
Capítulo 13, Persistindo Dados com Adaptadores de Saída e Hibernate Reactive.

Vamos agora ver como integrar o Quarkus ao sistema hexagonal.

===========================================
Adicionando Quarkus a uma aplicação hexagonal modularizada

Para recapitular, estruturamos o sistema de topologia e inventário em três hexágonos
modularizados: Domínio, Aplicação e Framework. Uma questão que pode surgir é: qual módulo
deve ser responsável por iniciar o mecanismo Quarkus? Bem, para evitar confundir as
responsabilidades de cada módulo no sistema de topologia e inventário, criaremos um módulo
dedicado cujo único propósito é agregar os outros módulos do sistema hexagonal e inicializar
o mecanismo Quarkus. Chamaremos esse novo módulo de Bootstrap.

O módulo bootstrap é um módulo agregador que fornece, de um lado, as dependências
necessárias para inicializar o Quarkus e, do outro lado, as dependências do módulo
hexagonal para uso em conjunto com o Quarkus.

Vamos criar este novo módulo bootstrap no sistema de topologia e inventário, como segue:
1. No projeto raiz Maven do sistema de topologia e inventário, você pode executar o seguinte Comando Maven para criar este módulo bootstrap :
   mvn archetype:generate \
   -DarchetypeGroupId=de.rieckpil.archetypes \
   -DarchetypeArtifactId=testing-toolkit \
   -DarchetypeVersion=1.0.0 \
   -DgroupId=dev.hugodesouzacaramez \
   -DartifactId=bootstrap \
   -Dversion=1.0-SNAPSHOT \
   -Dpackage=dev.hugodesouzacaramez.topologyinventory.bootstrap \
   -DinteractiveMode=false

Este comando Maven cria uma estrutura de diretório básica para o módulo bootstrap .
Definimos artifactId para bootstrap e groupId para dev.hugodesouzacaramez, pois este módulo é
parte do mesmo projeto Maven que contém os módulos para outros hexágonos de
topologia e sistema de inventário.

2. Em seguida, precisamos configurar as dependências do Quarkus no arquivo pom.xml raiz do projeto , da seguinte maneira:
   <dependencyManagement>
   <dependencies>
   <dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-universe-bom</artifactId>
   <version>${quarkus.platform.version}</version>
   <type>pom</type>
   <scope>import</scope>
   </dependency>
   </dependencyManagement>

A dependência quarkus-universe-bom torna todas as extensões do Quarkus disponíveis.
Como estamos trabalhando com um aplicativo multimódulo, precisamos configurar o Quarkus para descobrir
beans CDI em módulos diferentes.

3. Então, precisamos configurar o jandex-maven-plugin no pom.xml raiz do projeto no arquivo Maven:
   <plugin>
   <groupId>org.jboss.jandex</groupId>
   <artifactId>jandex-maven-plugin</artifactId>
   <version>${jandex.version}</version>
   <executions>
   <execution>
   <id>make-index</id>
   <goals>
   <goal>jandex</goal>
   </goals>
   </execution>
   </executions>
   </plugin>

Sem o plugin anterior, teríamos um problema ao configurar e usar beans
CDI nos hexágonos Framework e Application.

4. Agora vem a parte mais crucial – a configuração do quarkus-maven-plugin. Para fazer o módulo
   bootstrap ser aquele que iniciará o mecanismo Quarkus, precisamos configurar o quarkusmaven-plugin naquele módulo corretamente.
   Veja como devemos configurar o quarkus-maven-plugin em bootstrap/pom.xml:

   <dependency>
   <groupId>dev.hugodesouzacaramez</groupId>
   <artifactId>domain</artifactId>
   </dependency>
   <dependency>
   <groupId>dev.hugodesouzacaramez</groupId>
   <artifactId>application</artifactId>
   </dependency>
   <dependency>
   <groupId>dev.hugodesouzacaramez</groupId>
     <artifactId>framework</artifactId>
    </dependency>

A parte importante aqui é a linha que contém <goal>build</goal>. Ao definir essa
meta de build para o módulo bootstrap , tornamos esse módulo responsável por
iniciar o mecanismo Quarkus.

5. Em seguida, precisamos adicionar as dependências do Maven dos hexágonos do sistema de
   topologia e inventário. Fazemos isso no arquivo bootstrap/pom.xml , como segue:
   <dependency>
   <groupId>dev.hugodesouzacaramez</groupId>
   <artifactId>domain</artifactId>
   </dependency>
   <dependency>
   <groupId>dev.hugodesouzacaramez</groupId>
   <artifactId>application</artifactId>
   </dependency>
   <dependency>
   <groupId>dev.hugodesouzacaramez</groupId>
   <artifactId>framework</artifactId>
   </dependency>

6. E finalmente, criamos um descritor de módulo Java module-info.java com os requisitos
   diretivas para Quarkus e os módulos hexagonais de topologia e inventário, como segue:
   module dev.hugodesouzacaramez.bootstrap {
   requires quarkus.core;
   requires domain;
   requires application;
   requires framework;
   }


Para agregar os três módulos hexagon em uma unidade de implantação, configuraremos o Quarkus
para gerar um arquivo uber .jar . Esse tipo de JAR agrupa todas as dependências necessárias para
executar um aplicativo em um único JAR. Para fazer isso, precisamos definir a seguinte configuração no
arquivo pom.xml raiz do projeto :
<quarkus.package.type>uber-jar</quarkus.package.type>

Então, estamos prontos para compilar o aplicativo executando o seguinte comando Maven:
mvn clean package

Este comando Maven compilará o aplicativo inteiro e criará um arquivo .jar que
podemos usar para iniciar o aplicativo executando o seguinte comando:
java -jar bootstrap/target/bootstrap-1.0-SNAPSHOT-runner.jar

Note que o artefato que usamos é gerado a partir do módulo bootstrap , que agrega todos os
outros módulos.

O aplicativo visto na captura de tela anterior está sendo executado no perfil prod . Nesse perfil, alguns
recursos são desativados por motivos de segurança. Também podemos ver os recursos instalados
em execução no aplicativo. Esses recursos são ativados quando adicionamos dependências de extensão Quarkus em pom.xml

O módulo bootstrap atua como uma ponte, permitindo-nos conectar o framework de desenvolvimento
externo aos módulos hexagon que compõem o sistema hexagonal. Para a aplicação de topologia e
inventário, usamos o Quarkus, mas também poderíamos usar outros frameworks de desenvolvimento.
Não podemos dizer que estamos desacoplando totalmente a lógica do sistema do framework de
desenvolvimento; afinal, haverá alguma lógica do sistema que se beneficia dos recursos do framework.
No entanto, a abordagem apresentada neste capítulo mostra que parte desse sistema pode ser desenvolvida primeiro e a framework de desenvolvimento introduzido depois.

=================================
Resumo

Neste capítulo, revisitamos os fundamentos da JVM, avaliando alguns de seus recursos relacionados à
compilação JIT e à compilação AOT. Aprendemos que o JIT melhora o desempenho do tempo de execução,
enquanto o AOT ajuda a impulsionar o tempo de inicialização do aplicativo, o que prova ser um recurso
essencial para frameworks que visam ambientes de nuvem, como neste caso com o Quarkus.

Depois de nos familiarizarmos com alguns conceitos de JVM, passamos a aprender sobre o Quarkus e
alguns recursos importantes que ele oferece. Finalmente, integramos o Quarkus em nossa topologia e
inventário de sistema hexagonal já desenvolvidos. Para realizar tal integração, criamos um novo modulo bootstrap
para atuar como uma ponte entre os módulos do sistema hexagonal e o framework de desenvolvimento.
Agora sabemos o que é preciso para integrar o Quarkus em um aplicativo hexagonal modularizado.

No próximo capítulo, nos aprofundaremos na integração entre o Quarkus e a arquitetura hexagonal.
Aprenderemos como refatorar casos de uso e portas do hexágono de aplicativos para aproveitar os recursos
do Quarkus DI.

================================
================================
Aproveitando os CDI Beans para Gerenciar portas e casos de uso

A Quarkus fornece sua própria solução de injeção de dependência chamada Quarkus DI. Ela deriva
da especificação Contexts and Dependency Injection (CDI) para Java 2.0. Empregamos CDI para
delegar a responsabilidade de fornecer instâncias de objeto para uma dependência externa e
gerenciar seu ciclo de vida em um aplicativo. Várias soluções de injeção de dependência no mercado assumem essa responsabilidade.
Quarkus DI é um deles.

O valor de usar um mecanismo de injeção de dependência é que não precisamos mais nos preocupar sobre
como e quando fornecer uma instância de objeto. Uma solução de injeção de dependência nos permite criar e
fornecer objetos automaticamente como dependências em classes que dependem desses objetos, geralmente
usando atributos de anotação.

No contexto da arquitetura hexagonal, os hexágonos Framework e Application são bons candidatos para
alavancar os benefícios que uma solução CDI pode fornecer. Em vez de usar construtores que injetam
dependências usando classes concretas, podemos usar os mecanismos de descoberta CDI para procurar
automaticamente implementações de interface e fornecê-las ao aplicativo.

Neste capítulo, aprenderemos como aprimorar o provisionamento de portas e casos de uso transformandoos em beans. Exploraremos os escopos de bean e seus ciclos de vida e entenderemos como e quando
usar os escopos de bean disponíveis. Depois que soubermos sobre os fundamentos do CDI,
aprenderemos como aplicá-los a um sistema hexagonal.

Os seguintes tópicos serão abordados neste capítulo:
• Aprendendo sobre Quarkus DI
• Transformando portas, casos de uso e adaptadores em beans CDI
• Testando casos de uso com Quarkus e Cucumber

Ao final deste capítulo, você saberá como integrar o Quarkus DI em um aplicativo hexagonal, transformando
casos de uso e portas em beans gerenciados que podem ser injetados no sistema hexagonal.
Você também saberá como testar casos de uso usando o Quarkus em conjunto com o Cucumber.

==================================================================
Aprendendo sobre Quarkus DI

Quarkus DI é a solução de injeção de dependência fornecida pelo framework Quarkus. Esta solução,
também chamada ArC, é baseada no CDI para a especificação Java 2.0. O Quarkus DI não implementa
completamente tal especificação. Em vez disso, ele fornece algumas implementações personalizadas e
alteradas que são mais inclinadas para os objetivos do projeto Quarkus. No entanto, essas mudanças são
mais visíveis quando você se aprofunda no que o Quarkus DI fornece. Para aqueles que trabalham apenas
com os recursos básicos e mais recorrentes descritos no CDI para a especificação Java 2.0, a experiência
do Quarkus DI é semelhante a outras implementações de CDI.

A vantagem que obtemos ao usar o Quarkus DI ou qualquer solução de injeção de dependência é que
podemos focar mais nos aspectos de negócios do software que estamos desenvolvendo, em vez das
atividades de encanamento relacionadas ao provisionamento e controle do ciclo de vida dos objetos que
o aplicativo precisa para fornecer seus recursos. Para habilitar tal vantagem, o Quarkus DI lida com os chamados beans.

=============================
Trabalhando com feijões (beans)

Beans são tipos especiais de objetos que podemos usar para injetar dependências ou que agem como
dependências para serem injetadas em outros beans. Essa atividade de injeção ocorre em um ambiente gerenciado por contêiner.
Este ambiente nada mais é do que o ambiente de execução no qual o aplicativo é executado.

Beans têm um contexto que influencia quando e como seus objetos de instância são criados. A seguir estão
os principais contextos suportados pelo Quarkus DI:
• ApplicationScoped: Um bean marcado com tal contexto está disponível para todo o aplicativo. Apenas
uma instância de bean é criada e compartilhada entre todas as áreas do sistema que injetam esse
bean. Outro aspecto importante é que os beans ApplicationScoped são carregados preguiçosamente.
Isso significa que a instância do bean é criada somente quando o método de um bean é chamado
pela primeira vez. Dê uma olhada neste exemplo:
@ApplicationScoped
class MyBean {
public String name = "Test Bean";
public String getName(){
return name;
}
}
class Consumer {
@Inject
MyBean myBean;
public String getName() {
return myBean.getName();
}
}

A classe MyBean está disponível não apenas para a classe Consumer , mas também para outras classes
que injetam o bean. A instância do bean será criada apenas uma vez quando myBean.getName() for
chamado pela primeira vez.

• Singleton: Semelhante aos beans ApplicationScoped , para os beans Singleton , apenas um
objeto bean é criado e compartilhado no sistema. A única diferença, no entanto, é que o Singleton
beans são carregados avidamente. Isso significa que, uma vez que o sistema é iniciado, a instância
do bean Singleton também é iniciada. Aqui está o código que exemplifica isso:
@Singleton
class EagerBean { ... }

class Consumer {
@Inject
EagerBean eagerBean;
}

O objeto EagerBean será criado durante a inicialização do sistema.

• RequestScoped: Normalmente marcamos um bean como RequestScope quando queremos
tornar esse bean disponível apenas enquanto a solicitação associada a esse bean estiver
ativa. A seguir, um exemplo de como podemos usar RequestScope:

@RequestScoped
class RequestData {
public String getResponse(){
return "string response";
}
}

@Path("/")
class Consumer {
@Inject
RequestData requestData;
@GET
@Path("/request")
public String loadRequest(){
return requestData.getResponse();
}
}

Toda vez que uma solicitação chega em /request, um novo objeto bean RequestData será
criado e destruído assim que a solicitação for concluída.

• Dependent: Beans marcados como Dependent têm seu escopo restrito aos lugares onde
são usados. Então, beans Dependent não são compartilhados entre outros beans no
sistema. Além disso, seu ciclo de vida é o mesmo que o definido no bean que os injeta.
Por exemplo, se você injetar um bean com anotação Dependent em um bean
RequestScoped , o bean anterior usa o escopo do último:
@Dependent
class DependentBean { ... }

@ApplicationScoped
class ConsumerApplication {
@Inject
DependentBean dependentBean;
}

@RequestScoped
class ConsumerRequest {
@Inject
DependentBean dependentBean;
}

A classe DependentBean se tornará ApplicationScoped quando injetada em
ConsumerApplication e RequestScoped em ConsumerRequest.

• SessionScoped: Usamos esse escopo para compartilhar o contexto do bean entre todas as
requisições da mesma sessão HTTP. Precisamos da extensão quarkus-undertow para habilitar o SessionScoped
sobre Quarkus:
@SessionScoped
class SessionBean implements Serializable {
public String getSessionData(){
return "sessionData";
}
}

@Path("/")
class Consumer {
@Inject
SessionBean sessionBean;
@GET
@Path("/sessionData")
public String test(){
return sessionBean.getSessionData();
}
}

No exemplo anterior, uma instância SessionBean será criada após a primeira solicitação ser enviada
para /sessionData. Essa mesma instância estará disponível para outras solicitações vindas da mesma
sessão.

Para resumir, o Quarkus oferece os seguintes escopos de bean: ApplicationScoped, RequestScoped, Singleton,
Dependent e SessionScoped. Para aplicativos sem estado, na maioria das vezes, você pode precisar apenas de
ApplicationScoped e RequestScoped. Ao entender como esses escopos funcionam, podemos selecioná-los de
acordo com as necessidades do nosso sistema.

Agora que conhecemos as vantagens do Quarkus DI e os princípios básicos de como ele funciona, vamos aprender como
empregar técnicas de injeção de dependência com as portas e casos de uso da arquitetura hexagonal.

=======================
Transformando portas, casos de uso e adaptadores em beans CDI

Ao projetar o hexágono Application para o sistema de topologia e inventário, definimos os casos
de uso como interfaces e portas de entrada como suas implementações. Também definimos
portas de saída como interfaces e adaptadores de saída como suas implementações no hexágono
Framework. Nesta seção, refatoraremos componentes dos hexágonos Application e Framework para habilitar o uso
de injeção de dependência com Quarkus DI.

O primeiro passo para trabalhar com o Quarkus DI é adicionar a seguinte dependência Maven ao pom.xml raiz do projeto:
<dependency>
<groupId>io.quarkus</groupId>
<artifactId>quarkus-resteasy</artifactId>
</dependency>

Além das bibliotecas RESTEasy, esta biblioteca quarkus-resteasy também fornece as bibliotecas
necessárias para trabalhar com o Quarkus DI.

Vamos começar nossos esforços de refatoração com as classes e interfaces relacionadas ao gerenciamento de roteadores.

==============================
Implementando CDI para objetos de gerenciamento de roteador

Ao desenvolver a topologia e o sistema de inventário, definimos um conjunto de portas, casos de uso e adaptadores para
gerenciar operações relacionadas ao roteador. Vamos percorrer as alterações necessárias para habilitar a injeção de
dependência em tais operações:

1. Começamos transformando o adaptador de saída RouterManagementH2Adapter em um bean gerenciado:
   import jakarta.enterprise.context.ApplicationScoped;
   @ApplicationScoped
   public class RouterManagementH2Adapter implements
   RouterManagementOutputPort {
   @PersistenceContext
   private EntityManager em;
   /** Code omitted **/
   private void setUpH2Database() {
   EntityManagerFactory entityManagerFactory =
   Persistence.createEntityManagerFactory(
   "inventory");
   EntityManager em =
   entityManagerFactory.createEntityManager();
   this.em = em;
   }
   }

Transformamos essa classe em um bean gerenciado colocando a anotação @ApplicationScoped
no topo da classe RouterManagementH2Adapter . Observe o atributo EntityManager – podemos
usar injeção de dependência nesse atributo também. Faremos isso no Capítulo 13,
Persistindo Dados com Adaptadores de Saída e Hibernate Reactive, mas não tocaremos nisso por enquanto.

2. Antes de alterar a interface RouterManagementUseCase e sua implementação,
   RouterManagementInputPort, vamos analisar alguns aspectos da implementação atual:
   public interface RouterManagementUseCase {
   void setOutputPort(
   RouterManagementOutputPort
   routerManagementOutputPort);
   /** Code omitted **/
   }

Definimos o método setOutputPort para receber e definir um tipo de instância de
RouterManagementOutputPort, que é preenchido por um RouterManagementH2Adapter
adaptador de saída. Como não precisaremos mais fornecer explicitamente esse
objeto adaptador de saída (porque o Quarkus DI o injetará), podemos remover o
método setOutputPort da interface RouterManagementUseCase.

O código a seguir demonstra como RouterManagementInputPort é implementado sem
Quarkus DI:
@NoArgsConstructor
public class RouterManagementInputPort implements
RouterManagementUseCase {
private RouterManagementOutputPort
routerManagementOutputPort;
@Override
public void setOutputPort(
RouterManagementOutputPort
routerManagementOutputPort) {
this.routerManagementOutputPort =
routerManagementOutputPort;
}
/** Code omitted **/
}

Para fornecer um objeto do tipo RouterManagementOutputPort , precisamos usar o
método setOutputPort mencionado anteriormente . Após implementar o Quarkus DI,
isso não será mais necessário, como veremos na próxima etapa.

3. É assim que o RouterManagementOutputPort deve ficar após a implementação
   do Quarkus DI:
   import jakarta.enterprise.context.ApplicationScoped;
   import jakarta.inject.Inject;
   @ApplicationScoped
   public class RouterManagementInputPort implements
   RouterManagementUseCase {
   @Inject
   RouterManagementOutputPort
   routerManagementOutputPort;
   /** Code omitted **/
   }

Primeiro, adicionamos ApplicationScoped em cima de RouterManagementInputPort para
permitir que ele seja injetado em outras partes do sistema. Então, usando a anotação
@Inject , injetamos RouterManagementOutputPort. Não precisamos nos referir à implementação
do adaptador de saída. O Quarkus DI encontrará uma implementação adequada para essa
interface de porta de saída, que por acaso é o adaptador de saída RouterManagementH2Adapter
que transformamos em um bean gerenciado anteriormente.

4. Por fim, precisamos atualizar o adaptador de entrada RouterManagementGenericAdapter :
   @ApplicationScoped
   public class RouterManagementGenericAdapter {
   @Inject
   private RouterManagementUseCase
   routerManagementUseCase;
   /** Code omitted **/
   }

Em vez de inicializar RouterManagementUseCase usando um construtor, precisamos fornecer a
dependência por meio da anotação @Inject . No tempo de execução, o Quarkus DI criará e atribuirá um
objeto RouterManagementInputPort a essa referência de caso de uso.

É isso para as mudanças que devemos fazer nas classes e interfaces relacionadas ao gerenciamento do roteador.
Agora, vamos aprender o que precisamos mudar em relação às classes e interfaces para gerenciamento de switches.

====================================
Implementando CDI para objetos de gerenciamento de switch

Nesta seção, seguiremos um caminho semelhante ao que seguimos quando refatoramos as portas, casos
de uso e adaptadores relacionados ao gerenciamento do roteador:

1. Começamos transformando o adaptador de saída SwitchManagementH2Adapter em um
   bean gerenciado:
   import jakarta.enterprise.context.ApplicationScoped;
   @ApplicationScoped
   public class SwitchManagementH2Adapter implements
   SwitchManagementOutputPort {
   @PersistenceContext
   private EntityManager em;
   /** Code omitted **/
   }

O adaptador SwitchManagementH2Adapter também faz uso do EntityManager. Não modificaremos como
o objeto EntityManager é fornecido, mas no Capítulo 13, Persistindo Dados com Adaptadores de Saída
e Hibernate Reactive, o alteraremos para usar injeção de dependência.

2. Alteramos a definição da interface SwitchManagementUseCase no Capítulo 9,
   Aplicando Inversão de Dependência com Módulos Java, e definimos o método setOutputPort:
   public interface SwitchManagementUseCase {
   void setOutputPort(
   SwitchManagementOutputPort
   switchManagementOutputPort)
   /** Code omitted **/
   }

Como o Quarkus DI fornecerá uma instância SwitchManagementOutputPort adequada , não precisaremos
mais desse método setOutputPort , então podemos removê-lo.

3. O código a seguir mostra como SwitchManagementInputPort é implementado sem
   injeção de dependência:

@NoArgsConstructor
public class SwitchManagementInputPort implements
SwitchManagementUseCase {
private SwitchManagementOutputPort
switchManagementOutputPort;
@Override
public void setOutputPort(
SwitchManagementOutputPort
switchManagementOutputPort) {
this.switchManagementOutputPort =
switchManagementOutputPort;
}
/** Code omitted **/
}

Chamamos o método setOutputPort para inicializar um objeto SwitchManagementOutputPort .
Ao usar técnicas de injeção de dependência, não há necessidade de instanciar ou
inicializar objetos explicitamente.

4. A seguir está a aparência que SwitchManagementInputPort deve ter após a implementação
   da injeção de dependência:

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
@ApplicationScoped
public class SwitchManagementInputPort implements
SwitchManagementUseCase {
@Inject
private SwitchManagementOutputPort
switchManagementOutputPort;
/** Code omitted **/
}

Usamos a anotação @ApplicationScoped para converter SwitchManagementInputPort em um bean
gerenciado e a anotação @Inject para fazer o Quarkus DI descobrir um objeto de bean
gerenciado que implementa a interface SwitchManagementOutputPort , que por acaso é o
adaptador de saída SwitchManagementH2Adapter .

5. Ainda precisamos ajustar o adaptador de entrada SwitchManagementGenericAdapter:

public class SwitchManagementGenericAdapter {
@Inject
private SwitchManagementUseCase
switchManagementUseCase;
@Inject
private RouterManagementUseCase
routerManagementUseCase;
/** Code omitted **/
}

Aqui, estamos injetando dependências para ambos os objetos SwitchManagementUseCase
e RouterManagementUseCase . Antes de usar anotações, essas dependências eram
fornecidas desta forma:

public SwitchManagementGenericAdapter (
RouterManagementUseCase routerManagementUseCase,
SwitchManagementUseCase switchManagementUseCase){
this.routerManagementUseCase =
routerManagementUseCase;
this.switchManagementUseCase =
switchManagementUseCase;
}

A melhoria que obtemos é que não precisamos mais depender do construtor para
inicializar as dependências do SwitchManagementGenericAdapter . O Quarkus DI fornecerá
automaticamente as instâncias necessárias para nós.

A próxima seção é sobre as operações relacionadas ao gerenciamento de rede. Vamos aprender como
devemos alterá-las.

============================
Implementando CDI para classes e interfaces de gerenciamento de rede

Temos menos coisas para mudar na parte de rede porque não criamos uma porta de saída específica
e adaptador para as operações relacionadas à rede. Então, as mudanças de implementação só ocorrerão
nos casos de uso, portas de entrada e adaptadores de entrada:

1. Vamos começar analisando a interface do caso de uso NetworkManagementUseCase:
   public interface NetworkManagementUseCase {
   void setOutputPort(
   RouterManagementOutputPort
   routerNetworkOutputPort);
   /** Code omitted **/
   }

Como fizemos nos outros casos de uso, também definimos o método setOutputPort para permitir a
inicialização do RouterManagementOutputPort. Após implementar o Quarkus DI, esse método não
será mais necessário.

2. É assim que o NetworkManagementInputPort é implementado sem o Quarkus DI:
   import jakarta.enterprise.context.ApplicationScoped;
   import jakarta.inject.Inject;
   public class NetworkManagementInputPort implements
   NetworkManagementUseCase {
   private RouterManagementOutputPort
   routerManagementOutputPort;
   @Override
   public void setOutputPort(
   RouterManagementOutputPort
   routerManagementOutputPort) {
   this.routerManagementOutputPort =
   routerManagementOutputPort;
   }
   /** Code omitted **/
   }

A porta de entrada NetworkManagementInputPort depende apenas de
RouterManagementOutputPort , que, sem injeção de dependência, é inicializada pelo
método setOutputPort.

3. É assim que o NetworkManagementInputPort se parece após a implementação do Quarkus DI:
   @ApplicationScoped
   public class NetworkManagementInputPort implements
   NetworkManagementUseCase {
   @Inject
   private RouterManagementOutputPort
   routerManagementOutputPort;
   /** Code omitted **/
   }

Como você pode ver, o método setOutputPort foi removido. O Quarkus DI agora está
fornecendo uma implementação para RouterManagementOutputPort por meio da anotação @Inject.
A anotação @ApplicationScoped converte NetworkManagementInputPort em um bean
gerenciado.

4. Por fim, temos que alterar o adaptador de entrada NetworkManagementGenericAdapter:

   import jakarta.enterprise.context.ApplicationScoped;
   import jakarta.inject.Inject;
   @ApplicationScoped
   public class NetworkManagementGenericAdapter {
   @Inject
   private SwitchManagementUseCase
   switchManagementUseCase;
   @Inject
   private NetworkManagementUseCase
   networkManagementUseCase;
   /** Code omitted **/
   }

O adaptador de entrada NetworkManagementGenericAdapter depende dos casos de uso
SwitchManagementUseCase e NetworkManagementUseCase para disparar operações
relacionadas à rede no sistema. Como fizemos nas implementações anteriores, aqui,
estamos usando @Inject para fornecer as dependências em tempo de execução.

O código a seguir mostra como essas dependências eram fornecidas antes do Quarkus DI:

public NetworkManagementGenericAdapter(
SwitchManagementUseCase switchManagementUseCase, Net
workManagementUseCase networkManagementUseCase) {
this.switchManagementUseCase =
switchManagementUseCase;
this.networkManagementUseCase =
networkManagementUseCase;
}

Após implementar o mecanismo de injeção , podemos remover com segurança este construtor
NetworkManagementGenericAdapter .

Concluímos todas as alterações necessárias para converter as portas de entrada, casos de uso e
adaptadores em componentes que podem ser usados para injeção de dependência. Essas alterações nos mostraram como integrar
os mecanismos Quarkus CDI em nossa aplicação hexagonal.

Agora, vamos aprender como adaptar o sistema hexagonal para simular e usar beans gerenciados durante os testes.

=========================================
Testando casos de uso com Quarkus e Cucumber

Ao implementar o Application hexagon no Capítulo 7, Construindo o Application Hexagon, usamos o Cucumber
para nos ajudar a moldar e testar nossos casos de uso. Ao alavancar as técnicas de design orientadas a
comportamento fornecidas pelo Cucumber, pudemos expressar casos de uso de forma declarativa. Agora,
precisamos integrar o Cucumber para que ele funcione com o Quarkus:

1. O primeiro passo é adicionar as dependências de teste do Quarkus ao arquivo pom.xml
   do hexágono Application:
   <dependency>
   <groupId>io.quarkiverse.cucumber</groupId>
   <artifactId>quarkus-cucumber</artifactId>
   <version>1.0 .0</version>
   <scope>test</scope>
   </dependency>
   <dependency>
   <groupId>io.quarkus</groupId>
   <artifactId>quarkus-junit5</artifactId>
   <scope>test</scope>
   </dependency>

A dependência quarkus-cucumber fornece a integração que precisamos para executar testes com o
Quarkus. Também precisamos da dependência quarkus-junit5 , que nos permite usar a anotação
@QuarkusTest.

2. Em seguida, devemos adicionar as dependências necessárias do Cucumber:

<dependency>
 <groupId>io.cucumber</groupId>
 <artifactId>cucumber-java</artifactId>
 <version>${cucumber.version}</version>
 <scope>test</scope>
</dependency>
<dependency>
 <groupId>io.cucumber</groupId>
 <artifactId>cucumber-junit</artifactId>
 <version>${cucumber.version}</version>
 <scope>test</scope>
</dependency>
<dependency>
 <groupId>io.cucumber</groupId>
 <artifactId>cucumber-picocontainer</artifactId>
 <version>${cucumber.version}</version>
 <scope>test</scope>
</dependency>

Com as dependências cucumber-java, cucumber-junit e cucumber-picocontainer ,
podemos habilitar o mecanismo Cucumber no sistema.

Vamos ver como o Cucumber é configurado sem o Quarkus:

package dev.hugodesouzacaramez.topologyinventory.application;
import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
@RunWith(Cucumber.class)
@CucumberOptions(
plugin = {"pretty", "html:target/cucumber-result"}
)
public class ApplicationTest {
}

A anotação @RunWith(Cucumber.class) é usada para ativar o mecanismo Cucumber.
Ao usar Quarkus, é assim que ApplicationTest deve ser implementado:

package dev.hugodesouzacaramez.topologyinventory.application;
import io.quarkiverse.cucumber.CucumberQuarkusTest;
import io.quarkus.test.junit.QuarkusTest;
@QuarkusTest
public class ApplicationTest extends CucumberQuarkusTest {
}

As anotações @QuarkusTest ativam o mecanismo de teste Quarkus. Ao estender a classe
CucumberQuarkusTest , também habilitamos o mecanismo de teste Cucumber.

Não há testes na classe ApplicationTest porque esta é apenas uma classe bootstrap. Lembre-se de
que os testes do Cucumber foram implementados em classes separadas. Antes de alterar essas
classes, precisamos simular os managed beans que são necessários para fornecer instâncias
para RouterManagementOutputPort e SwitchManagementOutputPort.

Vamos criar um objeto bean simulado para RouterManagementOutputPort:

package dev.hugodesouzacaramez.topologyinventory.application.mocks;
import dev.hugodesouzacaramez.topologyinventory.applica
tion.ports.output.RouterManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.domain.en
tity.Router;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import io.quarkus.test.Mock;
@Mock
public class RouterManagementOutputPortMock implements
RouterManagementOutputPort {
@Override
public Router retrieveRouter(Id id) {
return null;
}
@Override
public Router removeRouter(Id id) {
return null;
}
@Override
public Router persistRouter(Router router) {
return null;
}
}

Este é um mocked bean fictício que criamos para evitar que o Quarkus lance
Unsatis - fiedResolutionException. Ao usar a anotação @Mock , o Quarkus instanciará
a classe RouterManagementOutputPortMock e a servirá como um bean a ser injetado durante os testes.

Da mesma forma, faremos mock de SwitchManagementOutputPort:

package dev.hugodesouzacaramez.topologyinventory.application.mocks;

import dev.hugodesouzacaramez.topologyinventory.application.ports.output.SwitchManagementOutputPort;
import dev.hugodesouzacaramez.topologyinventory.domain.entity.Switch;
import dev.hugodesouzacaramez.topologyinventory.domain.vo.Id;
import io.quarkus.test.Mock;

@Mock
public class SwitchManagementOutputPortMock implements SwitchManagementOutputPort {
@Override
public Switch retrieveSwitch(Id id) {
return null;
}
}

Para SwitchManagementOutputPort, criamos SwitchManagementOutputPortMock para
fornecer um bean gerenciado fictício para que o Quarkus possa usá-lo para injeção durante os testes.
Sem simulações, precisaríamos de instâncias reais dos adaptadores de saída
RouterManagementH2Adapter e SwitchManagementH2Adapter.

Embora não façamos referência direta a interfaces de saída e adaptadores de porta de saída durante os
testes, o Quarkus ainda tenta executar a descoberta de bean neles. É por isso que precisamos fornecer os mocks.

Agora, podemos refatorar os testes para usar a injeção de dependência fornecida pelo Quarkus DI. Vamos
aprender como fazer isso no teste RouterAdd:

public class RouterAdd extends ApplicationTestData {
@Inject
RouterManagementUseCase routerManagementUseCase;
/** Code omitted **/
}

Antes de usar o Quarkus DI, foi assim que obtivemos a implementação para RouterManagementUseCase:

this.routerManagementUseCase = new RouterManagementInputPort();

O código anterior pode ser removido depois que a anotação @Inject for implementada.

Podemos seguir a mesma abordagem de adicionar a anotação @Inject e remover a chamada do
construtor para instanciar objetos de porta de entrada ao refatorar outras classes de teste.

========================================
========================================
========================================
========================================
Usando RESTEasy Reactive para Implementar adaptadores de entrada

Um adaptador de entrada é como uma porta da frente que expõe todos os recursos fornecidos por um sistema hexagonal.
Sempre que um usuário ou outro aplicativo deseja se comunicar com um sistema hexagonal, eles alcançam um dos
adaptadores de entrada disponíveis. Com esses adaptadores, podemos fornecer diferentes maneiras de acessar a
mesma funcionalidade dentro do sistema hexagonal. Se um cliente não suportar comunicação HTTP, podemos
implementar um adaptador usando um protocolo diferente. A vantagem significativa aqui é que remover ou adicionar
novos adaptadores não influencia a lógica do domínio.

Devido à natureza de desacoplamento e encapsulamento adequado da arquitetura hexagonal, podemos mudar
tecnologias sem que ocorram grandes mudanças na lógica do domínio do sistema.

Neste capítulo, continuaremos nossa jornada explorando os recursos interessantes do Quarkus. Um recurso que se
encaixa muito bem com a implementação de adaptadores de entrada é a implementação RESTEasy Reactive JAX-RS,
que é parte da estrutura Quarkus. O RESTEasy Reactive propõe uma maneira assíncrona e orientada a eventos para
expor endpoints HTTP. Então, aprenderemos como integrar esses recursos do Reactive com adaptadores de entrada
de um sistema hexagonal.

Abordaremos os seguintes tópicos neste capítulo:

• Explorar as abordagens para lidar com solicitações do servidor
• Implementação de adaptadores de entrada com RESTEasy Reactive
• Adicionando OpenAPI e Swagger UI
• Testando adaptadores de entrada reativos

Ao final deste capítulo, você saberá como implementar e testar adaptadores de entrada com comportamento reativo.
Você também saberá como publicar a API para esses adaptadores de entrada usando OpenAPI e Swagger UI.

================================
Explorando as abordagens para lidar com solicitações do servidor

Na comunicação cliente-servidor, temos um fluxo de processo em que um cliente envia uma solicitação, o
servidor a recebe e começa a fazer algum trabalho. Assim que o servidor termina seu trabalho, ele responde
ao cliente com um resultado. Da perspectiva do cliente, esse fluxo não muda. É sempre sobre enviar uma
solicitação e receber uma resposta. O que pode mudar, no entanto, é como o servidor pode lidar internamente
com o processamento de uma solicitação.

Há duas abordagens para lidar com o processamento de requisições do servidor: reativa e imperativa. Então,
vamos ver como um servidor pode lidar com requisições imperativamente.

===========================
Imperativo

Em um aplicativo web tradicional em execução no Tomcat, cada solicitação recebida pelo servidor aciona a
criação de um thread de trabalho em algo chamado de pool de threads. No Tomcat, um pool de threads é um
mecanismo que controla o ciclo de vida e a disponibilidade de threads de trabalho que atendem a solicitações
de aplicativos. Então, quando você faz uma solicitação de servidor, o Tomcat puxa um thread dedicado do pool
de threads para atender à sua solicitação. Esse thread de trabalho depende do bloqueio de E/S para acessar bancos de dados e outros sistemas.

Conforme mostrado no diagrama anterior, o servidor precisa criar um novo thread de trabalho de bloqueio de E/
S para cada solicitação.

Depois que um thread de trabalho é criado e alocado para atender a uma solicitação, ele é bloqueado até
que a solicitação seja atendida. O servidor tem um número limitado de threads. Se você tiver muitas
solicitações de execução longa e continuar enviando essas solicitações antes que o servidor possa
finalizá-las, o servidor ficará sem threads, o que levará a falhas no sistema.

A criação e o gerenciamento de threads também são caros. O servidor gasta recursos valiosos na criação
e troca entre threads para atender às solicitações do cliente.

Então, o ponto principal da abordagem imperativa é que um thread de trabalho é bloqueado para atender
a uma – e somente uma – solicitação por vez. Para atender a mais solicitações simultaneamente, você
precisa fornecer mais threads de trabalho. Além disso, a abordagem imperativa influencia como o código
é escrito. O código imperativo é um pouco mais direto de entender porque as coisas são tratadas sequencialmente.

Agora, vamos ver como a abordagem reativa contrasta com a imperativa.

=================================
Reativo

Como você pode imaginar, a ideia por trás da abordagem reativa é que você não precisa bloquear um thread para
atender a uma solicitação. Em vez disso, o sistema pode usar o mesmo thread para processar diferentes solicitações simultaneamente.
Na abordagem imperativa, temos threads de trabalho que lidam com apenas uma solicitação por vez, enquanto
na abordagem reativa, temos threads não bloqueantes de E/S que lidam com várias solicitações simultaneamente.

Conforme mostrado no diagrama anterior, um único thread não bloqueante pode manipular diversas solicitações.

Na abordagem reativa, temos uma sensação de continuação. Em vez da natureza sequencial da abordagem
imperativa, com Reactive, podemos ver que as coisas têm continuidade. Por continuação, queremos dizer
que sempre que um servidor pronto para Reactive recebe uma solicitação, tal solicitação é despachada como
uma operação de E/S com uma continuação anexada. Essa continuação funciona como um retorno de
chamada que é acionado e continua a executar a solicitação assim que o servidor retorna com uma resposta.
Se essa solicitação precisar buscar um banco de dados ou qualquer sistema remoto, o servidor não bloqueará o thread de E/S enquanto espera pela resposta.
Em vez disso, o thread de E/S acionará uma operação de E/S com uma continuação anexada e liberará o
thread de E/S para aceitar outras solicitações.

Como podemos ver, um thread de E/S chama uma tarefa não bloqueante que dispara uma operação de E/S e
retorna imediatamente. Isso acontece porque o thread de E/S não precisa esperar a primeira operação de E/S
terminar para chamar uma segunda. Enquanto a primeira operação de E/S ainda está em execução, o mesmo
thread de E/S chama outra tarefa não bloqueante. Uma vez que a operação de E/S tenha sido concluída, o
thread de E/S retoma a execução ao finalizar as tarefas não bloqueantes.

Ao evitar o desperdício de tempo e recursos existentes na abordagem imperativa, a abordagem reativa otimiza
o uso de threads, já que eles não o fazem enquanto esperam a conclusão de uma operação de E/S.

A seguir, aprenderemos como implementar adaptadores de entrada reativos usando a implementação
RESTEasy Reactive JAX-RS fornecida pela Quarkus.

==================
Implementando adaptadores de entrada com RESTEasy Reactive

RESTEasy Reactive é uma implementação JAX-RS que suporta endpoints HTTP imperativos e reativos . Tal
implementação integra-se com Vert.x, que é um kit de ferramentas que podemos usar para construir sistemas
Reactive distribuídos. RESTEasy Reactive e Vert.x trabalham juntos no Quarkus para fornecer capacidades
Reactive.

Para entender a aparência de um endpoint reativo, integraremos o RESTEasy Reactive com os adaptadores
de entrada do sistema de topologia e inventário.

Vamos começar configurando as dependências necessárias do Maven:

<dependencies>
 <dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-resteasy-reactive</artifactId>
 </dependency>
 <dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-resteasy-reactive-
 jackson</artifactId>
 </dependency>
</dependencies>

Com quarkus-resteasy-reactive, trazemos as bibliotecas Reactive, incluindo Reactive
RESTEasy e a biblioteca Mutiny , que usaremos para criar código de forma reativa.
Usaremos quarkus - resteasy-reactive-jackson para tarefas de desserialização envolvendo as respostas Reactive.

Depois de configurar as dependências, podemos começar a implementar o adaptador de entrada reativo para
gerenciamento de roteadores no sistema de topologia e inventário.

=========================
Implementando o adaptador de entrada reativa para gerenciamento de roteador

Trabalharemos em cima dos adaptadores de entrada existentes que criamos no Capítulo 8, Construindo o
Framework Hexagon. Alteraremos esses adaptadores de entrada para habilitar os recursos JAX-RS e Reactive. Executaremos
os seguintes passos para fazer isso:

1. Vamos começar definindo o caminho de nível superior para solicitações relacionadas ao gerenciamento do roteador
   na classe RouterManagementAdapter:

@ApplicationScoped
@Path("/router")
public class RouterManagementAdapter {
@Inject
RouterManagementUseCase routerManagementUseCase;
/** Code omitted **/
}

Usamos a anotação @Path para mapear um caminho de URL para um recurso no sistema. Podemos
usar essa anotação em cima de uma classe ou método.

O único campo dessa classe é RouterManagementUseCase, que é injetado usando a anotação
@Inject . Ao utilizar essa referência de caso de uso, obtemos acesso a recursos do sistema
relacionados ao gerenciamento de roteador.

2. Em seguida, vamos definir um endpoint reativo para recuperar um roteador:

@GET
@Path("/{id}")
public Uni<Response> retrieveRouter(Id id) {
return Uni.createFrom()
.item(
routerManagementUseCase.
retrieveRouter(id))
.onItem()
.transform(
router -> router != null ?
Response.ok(f) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);

A anotação @GET diz que apenas requisições HTTP GET são permitidas. O @Path("/{id}")
a anotação do nível do método é concatenada com a anotação @Path("/router") do nível
da classe. Então, para alcançar esse método retrieveRouter , temos que enviar uma
requisição para /router/{id}.

Observe também a anotação @PathParam("id") , que usamos para capturar um parâmetro da URL

O que torna esse endpoint um Reactive é seu tipo de resposta Uni<Response> . Uni
é um dos dois tipos fornecidos pela biblioteca Mutiny . Além de Uni, há também o tipo Multi.

Usamos os tipos Uni e Multi para representar com que tipo de dados estamos lidando. Por
exemplo, se sua resposta retornar apenas um item, você deve usar Uni. Caso contrário, se sua
resposta for como um fluxo de dados, como aqueles que vêm de um servidor de mensagens,
então Multi pode ser mais adequado para seu propósito.

Ao chamar Uni.createFrom().item(routerManagementUseCase.retrieveRouter(id)), estamos criando um pipeline que executa routerManagementUseCase.
retrieveRouter(id). O resultado é capturado em transform(f -> f != null ? Response.ok(f) :
Response.ok(null)). Se a solicitação for bem-sucedida, obtemos Response.ok(f);
caso contrário, obtemos Response.ok(null). Finalmente, chamamos transform
(Response.ResponseBuilder::build) para transformar o resultado em um Uni<-
Resposta> objeto.

Response.ResponseBuilder::build é uma referência de método que pode ser escrita como a
seguinte expressão lambda: (Response.ResponseBuilder responseBuilder) ->
responseBuilder.build(). responseBuilder representa o parâmetro de objeto que recebemos,
seguido pela chamada do método build para criar um novo objeto Response . Favorecemos
a abordagem de referência de método porque escrevemos menos código para realizar a mesma coisa.

Os endpoints restantes que estamos prestes a implementar seguem uma abordagem
semelhante à descrita anteriormente.

3. Depois de implementar um endpoint para recuperar um roteador, podemos implementar um endpoint
   para remover um roteador do sistema:

@DELETE
@Path("/{id}")
public Uni<Response> removeRouter(@PathParam("id") Id
id) {
return Uni.createFrom()
.item(
routerManagementUseCase.removeRouter(id))
.onItem()
.transform(
router -> router != null ?
Response.ok(router) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

A anotação @DELETE corresponde ao método HTTP DELETE . Novamente, estamos
definindo um parâmetro Path na anotação @Path("/{id}") . O corpo do método tem um
pipeline Uni que executa routerManagementUseCase.removeRouter(id) e retorna
Uni<Response>.

4. Vamos implementar o endpoint para criar um novo roteador:

@POST
@Path("/")
public Uni<Response> createRouter(CreateRouter cre
ateRouter) {
/** Code omitted **/
return Uni.createFrom()
.item(
routerManagementUseCase.
persistRouter(router))
.onItem()
.transform(
router -> router != null ?
Response.ok(f) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

Usamos a anotação @POST porque estamos criando um novo recurso. A anotação
@Path("/") no nível do método, quando concatenada com a anotação @Path("/router") no
nível da classe, gera o caminho /router/ . Temos o código Reactive no corpo do método
para manipular a solicitação e retornar Uni<Response>.

5. Em seguida, implementaremos o endpoint para que um roteador possa ser adicionado a um roteador principal:

@POST
@Path("/add")
public Uni<Response> addRouterToCoreRouter(AddRouter
addRouter) {
/** Code omitted **/
return Uni.createFrom()
.item(routerManagementUseCase.
addRouterToCoreRouter(router,
coreRouter))
.onItem()
.transform(
router -> router != null ?
Response.ok(router) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

Novamente, usamos a anotação @POST aqui. A anotação @Path("/add") no nível do
método, quando concatenada com @Path("/router") no nível da classe, gera o
caminho /router/ add . O código Reactive cria um pipeline para executar
routerManagementUseCase. addRouterToCoreRouter(router, coreRouter) e retornar Uni<Response>.

6. Por fim, precisamos implementar o endpoint para remover um roteador de um roteador principal:

@DELETE
@Path("/{routerId}/from/{coreRouterId}")
public Uni<Response> removeRouterFromCoreRouter(
/** Code omitted **/
return Uni.createFrom()
.item(routerManagementUseCase.
removeRouterFromCoreRouter(
router, coreRouter))
.onItem()
.transform(
router -> router != null ?
Response.ok(f) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

Aqui, usamos a anotação @DELETE para manipular solicitações HTTP DELETE . No @Path
anotação, temos dois parâmetros de caminho – routerId e coreRouterId. Usamos esses
dois parâmetros para obter os objetos Router e CoreRouter quando chamamos
routerManagementUseCase. removeRouterFromCoreRouter(router, coreRouter) dentro do
pipeline fornecido pela Uni.

Como podemos ver, ao usar Quarkus, não é preciso muito para mudar de uma maneira imperativa para uma
forma Reativa de implementar endpoints REST. Grande parte do trabalho é feito nos bastidores pelo
framework e suas bibliotecas.

Agora, vamos prosseguir e implementar adaptadores de entrada reativos para gerenciamento de switches.

================================================
Implementando o adaptador de entrada reativa para gerenciamento de switch

Seguindo uma abordagem semelhante à que seguimos na seção anterior, podemos implementar os
Adaptadores de entrada reativos para gerenciamento de switches executando as seguintes etapas:

1. Começaremos habilitando o JAX-RS na classe SwitchManagementAdapter:

@ApplicationScoped
@Path("/switch")
public class SwitchManagementAdapter {
@Inject
SwitchManagementUseCase switchManagementUseCase;
@Inject
RouterManagementUseCase routerManagementUseCase;
/** Code omitted **/
}

Esta classe é anotada com @Path("/switch"), então todas as requisições relacionadas ao
gerenciamento de switch serão direcionadas a ela. Depois disso, injetamos SwitchManagementUseCase
e RouterManagementUseCase para executar operações no hexágono Application.

2. Para habilitar a recuperação do switch no sistema de topologia e inventário, precisamos implementar o
   Comportamento reativo no método retrieveSwitch:

@GET
@Path("/{id}")
public Uni<Response> retrieveSwitch(@PathParam("id")
Id switchId) {
return Uni.createFrom()
.item(
switchManagementUseCase.
retrieveSwitch(switchId))
.onItem()
.transform(
aSwitch -> aSwitch != null ?
Response.ok(aSwitch) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

Ao adicionar as anotações @GET e @Path , ativamos o JAX-RS no método
retrieveSwitch . Colocamos switchManagementUseCase.retrieveSwitch(switchId) para
que ele seja executado dentro de um pipeline Mutiny que retorna Uni<Response>.
A chamada em item retorna imediatamente. Ela dispara a operação que é executada
pelo método retrieveSwitch e permite que o thread continue atendendo outras
solicitações. O resultado é obtido quando chamamos onItem, que representa a
continuação da operação que é disparada quando chamamos item.

3. Em seguida, precisamos adicionar o comportamento Reativo ao método createAndAddSwitchToEdgeRouter:

@POST
@Path("/create/{edgeRouterId}")
public Uni<Response> createAndAddSwitchToEdgeRouter(
CreateSwitch createSwitch,
@PathParam("edgeRouterId") Id
edgeRouterId){
/** Code omitted **/
return Uni.createFrom()
.item((EdgeRouter)
routerManagementUseCase.
persistRouter(router))
.onItem()
.transform(
router -> router != null ?
Response.ok(f) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

O método anterior manipula as solicitações HTTP POST para criar um objeto switch e adicionálo a um roteador de borda. Chamamos o método routerManagementUseCase.persistRouter(router)
aqui, que é encapsulado dentro de um pipeline Mutiny , para retornar Uni<Response>.

4. Por fim, precisamos definir o endpoint Reativo para remover um switch de um roteador de borda:

@DELETE
@Path("/{switchId}/from/{edgeRouterId}")
public Uni<Response> removeSwitchFromEdgeRouter(
@PathParam("switchId") Id switchId,
@PathParam("edgeRouterId") Id
edgeRouterId) {
/** Code omitted **/
return Uni.createFrom()
.item(
(EdgeRouter)routerManagementUseCase.
persistRouter(router))
.onItem()
.transform(
router -> router != null ?
Response.ok(f) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

Assim como fizemos com nossa operação de remoção anterior, onde removemos um roteador de um
roteador core, usamos a anotação @DELETE para fazer o método removeSwitchFromEdgeRouter aceitar
apenas as solicitações HTTP DELETE . Passamos os parâmetros Path , switchId e edgeRouterId, para
obter os objetos switch e edge router necessários para a operação.

Depois de definir os endpoints reativos para retrieveSwitch,
createAndAddSwitchToEdgeRouter e removeSwitchFromEdgeRouter, podemos começar a implementar
o adaptador de entrada reativo para gerenciamento de rede.

=============================
Implementando o adaptador de entrada reativa para gerenciamento de rede

Como você pode imaginar, o adaptador de entrada Reactive da rede segue o mesmo padrão usado pelos
adaptadores Reactive do roteador e do switch. Nas etapas a seguir, habilitaremos o comportamento Reactive
para endpoints relacionados ao gerenciamento de rede:

1. Vamos começar habilitando o JAX-RS no adaptador de entrada NetworkManagementAdapter:

@ApplicationScoped
@Path("/network")
public class NetworkManagementAdapter {
@Inject
SwitchManagementUseCase switchManagementUseCase;
@Inject
NetworkManagementUseCase networkManagementUseCase;
/** Code omitted **/
}

Neste ponto, você pode estar familiarizado com a anotação @Path no nível de classe. Injetamos os
casos de uso SwitchManagementUseCase e NetworkManagementUseCase para auxiliar nas operações
que são executadas por este adaptador de entrada.

2. Em seguida, devemos definir um endpoint reativo para que redes possam ser adicionadas a um switch:

@POST
@Path("/add/{switchId}")
public Uni<Response> addNetworkToSwitch(AddNetwork
addNetwork, @PathParam("switchId") Id switchId) {
/** Code omitted **/
return Uni.createFrom()
.item(
networkManagementUseCase.
addNetworkToSwitch(
network, networkSwitch))
.onItem()
.transform(
f -> f != null ?
Response.ok(f) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

A ideia que aplicamos aqui é a mesma que aplicamos às implementações anteriores.
Dentro do método addNetworkToSwitch , adicionamos algum código Reactive que usará um Mutiny
pipeline para chamar networkManagementUseCase.addNetworkToSwitch( network, networkSwitch) e retornar
Uni<Response>.

3. Por fim, devemos definir o endpoint Reativo para remover uma rede de um switch:

@DELETE
@Path("/{networkName}/from/{switchId}")
public Uni<Response> removeNetworkFromSwitch(@Path
Param("networkName") String networkName, @Path
Param("switchId") Id switchId) {
/** Code omitted **/
return Uni.createFrom()
.item(
networkManagementUseCase.
removeNetworkFromSwitch(
networkName, networkSwitch))
.onItem()
.transform(
f -> f != null ?
Response.ok(f) :
Response.ok(null))
.onItem()
.transform(Response.Response
Builder::build);
}

Aqui, usamos a anotação @DELETE e dois parâmetros de caminho, networkName e
switchId, para remover uma rede de um switch. Dentro do pipeline Mutiny , chamamos
networkManagementUseCase.removeNetworkFromSwitch(networkName, networkSwitch). O
resultado do pipeline é Uni<Response>.

Com isso, finalizamos a implementação do adaptador de entrada Reactive para gerenciamento
de rede. Agora, os adaptadores de entrada RouterManagementAdapter, SwitchManagementAdapter
e NetworkManagementAdapter estão prontos para atender requisições HTTP de forma Reactive.

Esses três adaptadores de entrada e seus endpoints formam a API do sistema hexagonal.

Nesta seção, não apenas aprendemos como criar endpoints REST comuns, mas também fomos além usando o RESTEasy
Reactive para habilitar o comportamento Reactive nos endpoints do adaptador de entrada. Essa é uma etapa fundamental
para aproveitar as vantagens que uma abordagem Reactive pode fornecer. Com a abordagem Reactive, não precisamos
mais depender de threads de bloqueio de E/S, que podem consumir mais recursos de computação do que threads de
não bloqueio de E/S. Threads de bloqueio de E/S precisam esperar que as operações de E/S terminem. Threads de não
bloqueio de E/S são mais eficientes porque a mesma thread pode manipular várias operações de E/S ao mesmo tempo.

A próxima seção abordará como usar o OpenAPI e o Swagger UI para publicar a API do sistema.

========================================================
Adicionando OpenAPI e Swagger UI

Entender e interagir com sistemas de terceiros às vezes é uma tarefa nada trivial. No melhor
cenário, podemos ter a documentação do sistema, uma base de código organizada e um
conjunto de APIs que, juntos, nos ajudam a entender o que o sistema faz. No pior cenário, não
temos nada disso . Essa situação desafiadora requer coragem, paciência e persistência para
se aventurar a tentar entender uma base de código emaranhada com complexidades intrincadas.

OpenAPI representa um esforço honroso para aumentar nossa capacidade de expressar e entender o que
um sistema faz. Originalmente baseada na especificação Swagger, a especificação OpenAPI padroniza
como as APIs são documentadas e descritas para que qualquer um possa compreender os recursos oferecidos por um sistema
sem muito esforço.

Passamos a seção anterior implementando os adaptadores de entrada Reactive que formam a API do
nosso sistema hexagonal. Para tornar esse sistema mais compreensível para outras pessoas e sistemas,
usaremos o OpenAPI para descrever as funcionalidades fornecidas pelos adaptadores de entrada e seus
endpoints. Além disso, habilitaremos o Swagger UI, um aplicativo da web que apresenta uma visão clara e organizada das APIs.

O Quarkus vem com suporte integrado para a especificação OpenAPI v3. Para habilitá-lo, precisamos da
seguinte dependência Maven:

<dependencies>
 <dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-smallrye-openapi</artifactId>
 </dependency>
</dependencies>

A dependência quarkus-smallrye-openapi fornece as bibliotecas que contêm as anotações OpenAPI que
podemos usar para descrever os métodos de endpoint Reactive nas classes do adaptador de entrada.
Essa dependência nos permite configurar o Swagger UI também.

Lembre-se de que configuramos quatro módulos Java: domínio, aplicativo,
framework e bootstrap. Para ativar e configurar o Swagger UI, precisamos criar o resource/application.properties
s dentro do módulo bootstrap . Aqui está como podemos configurar este arquivo:

quarkus.swagger-ui.always-include=true
quarkus.swagger-ui.urls-primary-name=Topology & Inventory
quarkus.swagger-ui.theme=material
quarkus.swagger-ui.title=Topology & Inventory - Network
Management System
quarkus.swagger-ui.footer=&#169; 2021 | Hugo de Souza Caramez
quarkus.swagger-ui.display-operation-id=true
mp.openapi.extensions.smallrye.info.title=Topology & Inven
tory API
mp.openapi.extensions.smallrye.info.version=1.0
mp.openapi.extensions.smallrye.info.description=Manage net
works assets

Definimos quarkus.swagger-ui.always-include como true para garantir que o Swagger UI
também estará disponível quando o aplicativo for iniciado usando o perfil prod (produção)
– um dos perfis Quarkus integrados . Com quarkus.swagger-ui.theme, podemos configurar o tema da interface
Usaremos as propriedades restantes para fornecer uma descrição de alto nível da API.

Vamos aprender como usar as anotações do OpenAPI para expor e descrever os endpoints do sistema hexagonal.
Veja o exemplo a seguir da classe RouterManagementAdapter:

@ApplicationScoped
@Path("/router")
@Tag(name = "Router Operations", description = "Router man
agement operations")
public class RouterManagementAdapter {
@GET
@Path("/retrieve/{id}")
@Operation(operationId = "retrieveRouter",
description = "Retrieve a router from the network
inventory")
public Uni<Response> retrieveRouter(@PathParam("id")
Id id) {
/** Code omitted **/
}

A anotação @Tag , que é usada no nível de classe, nos permite definir as informações de
metadados que são aplicadas para todos os endpoints definidos na classe
RouterManagementAdapter . Isso significa que os endpoints do método, como o método retrieveRouter no RouterManagementAdapter
classe, herdará a anotação @Tag de nível de classe.

Usamos a anotação @Operation para fornecer detalhes de uma operação. No código
anterior, estamos descrevendo a operação que é realizada no caminho /retrieve/{id} . Temos o operationId
parâmetro aqui, que é usado para identificar exclusivamente o endpoint, e o parâmetro de
descrição , que é usado para fornecer uma descrição significativa da operação.

Para fazer com que o Quarkus e o Swagger UI exibam uma interface de usuário sofisticada da API do nosso
sistema hexagonal, precisamos apenas adicionar essas anotações OpenAPI às classes e métodos (configurados
corretamente com JAX-RS) que queremos expor no Swagger UI.

Você pode compilar e executar o aplicativo usando:
$ mvn clean package
$ java -jar bootstrap/target/bootstrap-1.0-SNAPSHOT-runner.jar

Isso abrirá o seguinte URL no seu navegador:
http://localhost:8080/q/swagger-ui/

Na captura de tela anterior, as operações são agrupadas em Network Operations, Router Operations e
Switch Operations. Esses grupos vêm da anotação @Tag que inserimos para cada uma das classes de
adaptador de entrada. Cada endpoint herdou suas respectivas informações de metadados @Tag.

Até agora, temos nosso sistema hexagonal configurado corretamente com endpoints reativos que são bem
documentados com OpenAPI e Swagger UI. Agora, vamos aprender como testar esses endpoints para
garantir que estejam funcionando conforme o esperado.

==========================================
Testando adaptadores de entrada reativos

Nossos esforços de teste começaram no hexágono Domain testando a unidade dos componentes
principais do sistema. Então, passamos para o hexágono Application, onde pudemos testar os casos de
uso usando técnicas de design orientadas por comportamento. Agora que implementamos endpoints
REST reativos no hexágono Framework, precisamos encontrar uma maneira de testá-los.

Felizmente, o Quarkus vem bem equipado quando se trata de testes de endpoint. Para começar,
precisamos da seguinte dependência:

<dependencies>
 <dependency>
 <groupId>io.rest-assured</groupId>
 <artifactId>rest-assured</artifactId>
 <scope>test</scope>
 </dependency>
</dependencies>

A dependência rest-assured nos permite testar endpoints HTTP. Ela fornece uma biblioteca
intuitiva que é muito útil para fazer solicitações e extrair respostas de chamadas HTTP.

Para ver como funciona, vamos implementar um teste para o endpoint /router/retrieve/{routerId}:

@Test
@Order(1)
public void retrieveRouter() throws IOException {
var expectedRouterId =
"b832ef4f-f894-4194-8feb-a99c2cd4be0c";
var routerStr = given()
.contentType("application/json")
.pathParam("routerId", expectedRouterId)
.when()
.get("/router/retrieve/{routerId}")
.then()
.statusCode(200)
.extract()
.asString();
var actualRouterId =
getRouterDeserialized(routerStr).getId().getUuid()
.toString();
assertEquals(expectedRouterId, actualRouterId);
}

Para criar uma solicitação, podemos usar o método estático io.restassured.RestAssured.given.
Podemos especificar o tipo de conteúdo, parâmetros, método HTTP e corpo de uma solicitação com o dado
método. Após enviar a solicitação, podemos verificar seu status com statusCode. Para obter a
resposta, chamamos extract. No exemplo a seguir, estamos obtendo a resposta na forma de uma
string. Isso ocorre porque o tipo de retorno do endpoint Reactive é Uni<Response>. Portanto, o resultado é uma string JSON.

Precisamos desserializar a string JSON em um objeto Router antes de executar asserções. O trabalho
de desserialização é realizado pelo método getRouterDeserialized:

public static Router getRouterDeserialized(String jsonStr)
throws IOException {
var mapper = new ObjectMapper();
var module = new SimpleModule();
module.addDeserializer(Router.class, new
RouterDeserializer());
mapper.registerModule(module);
var router = mapper.readValue(jsonStr, Router.class);
return router;
}

Este método recebe uma string JSON como parâmetro. Esta string JSON é passada para um ObjectMapper
mapper quando chamamos mapper.readValue(jsonStr, Router.class). Além de fornecer um mapper,
também precisamos estender e implementar o método deserialize da classe
com.fasterxml.jackson.databind.deser.std.StdDeserializer . No exemplo anterior, essa implementação é
fornecida pelo RouterDeserializer. Esse desserializador transformará a string JSON em um objeto
Router , conforme mostrado no código a seguir:

public class RouterDeserializer extends StdDeserial
izer<Router> {
/** Code omitted **/
@Override
public Router deserialize(JsonParser jsonParser,
DeserializationContext ctxt)
throws IOException {
JsonNode node =
jsonParser.getCodec().readTree(jsonParser);
var id = node.get("id").get("uuid").asText();
var vendor = node.get("vendor").asText();
var model = node.get("model").asText();
var ip = node.get("ip").get("ipAddress").asText();
var location = node.get("location");
var routerType = RouterType.valueOf(
node.get("routerType").asText());
var routersNode = node.get("routers");
var switchesNode = node.get("switches");
/** Code omitted **/
}

O método deserialize pretende mapear cada atributo JSON relevante para um tipo de domínio.
Realizamos esse mapeamento recuperando os valores que queremos de um objeto JsonNode . Após
mapear os valores que queremos, podemos criar um objeto roteador , conforme mostrado no código a seguir:

var router = RouterFactory.getRouter(
Id.withId(id),
Vendor.valueOf(vendor),
Model.valueOf(model),
IP.fromAddress(ip),
getLocation(location),
routerType);

Depois que todos os valores foram recuperados, chamamos RouterFactory.getRouter para produzir um roteador
objeto. Como um roteador pode ter roteadores e switches filhos, chamamos fetchChildRouters e
fetchChildSwitches para que eles também tenham implementações de StdDeserializer:

fetchChildRouters(routerType, routersNode, router);
fetchChildSwitches(routerType, switchesNode, router);

Chamamos os métodos fetchChildRouters e fetchChildSwitches porque um roteador pode ter roteadores e
switches filhos que precisam ser desserializados. Esses métodos executarão a desserialização necessária.

Depois de desserializar a resposta da string JSON, podemos executar a asserção em um objeto Router :

var actualRouterId = getRouterDeserialized(router
Str).getId().getUuid().toString();
assertEquals(expectedRouterId, actualRouterId);

Para testar o endpoint /router/retrieve/{routerId} , estamos verificando se o ID do
roteador que foi recuperado pelo endpoint Reativo é igual ao que passamos na solicitação.

Você pode executar este e outros testes com:
mvn test


A saída do código anterior será semelhante à seguinte:

[INFO] -------------------------------------------------------
[INFO] T E S T S
[INFO] -------------------------------------------------------
[INFO] Running dev.hugodesouzacaramez.topologyinventory.framework.adapters.
input.rest.NetworkManagementAdapterTest
2021-09-29 00:47:36,825 INFO [io.quarkus] (main) Quarkus 2.2.1.Final
on JVM started in 2.550s. Listening on: http://localhost:8081
2021-09-29 00:47:36,827 INFO [io.quarkus] (main) Profile test
activated.
2021-09-29 00:47:36,827 INFO [io.quarkus] (main) Installed features:
[cdi, resteasy-reactive, resteasy-reactive-jackson, smallrye-contextpropagation, smallrye-openapi, swagger-ui]
[EL Info]: 2021-09-29 00:47:38.812--ServerSession(751658062)-
-EclipseLink, version: Eclipse Persistence Services -
3.0.1.v202104070723
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0, Time elapsed:
5.418 s - in dev.hugodesouzacaramez.topologyinventory.framework.adapters.
input.rest.NetworkManagementAdapterTest
[INFO] Running dev.hugodesouzacaramez.topologyinventory.framework.adapters.
input.rest.RouterManagementAdapterTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed:
0.226 s - in dev.hugodesouzacaramez.topologyinventory.framework.adapters.
input.rest.RouterManagementAdapterTest
[INFO] Running dev.hugodesouzacaramez.topologyinventory.framework.adapters.
input.rest.SwitchManagementAdapterTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed:
0.085 s - in dev.hugodesouzacaramez.topologyinventory.framework.adapters.
input.rest.SwitchManagementAdapterTest
2021-09-29 00:47:39,675 INFO [io.quarkus] (main) Quarkus stopped in
0.032s


A saída anterior descreve a execução dos testes de endpoint reativo para
os adaptadores de entrada RouterManagementAdapter, SwitchManagementAdapter e
NetworkManagementAdapter.

Um benefício de executar esses testes de endpoint é que não estamos apenas testando a funcionalidade
do endpoint no hexágono do Framework, mas também realizando testes abrangentes que verificam o
comportamento de todos os hexágonos do sistema.

==============================
Resumo

Neste capítulo, tivemos a oportunidade de nos aprofundar em mais recursos do Quarkus, especialmente o RESTEasy Reactive.
Começamos revisando o que imperativo e reativo significam no contexto da comunicação cliente-servidor.

Então, aprendemos que o Quarkus fornece RESTEasy Reactive como sua implementação JAX-RS, permitindo- nos
implementar endpoints Reactive em adaptadores de entrada. Depois disso, expusemos a API do sistema hexagonal
usando OpenAPI e Swagger UI. Para garantir que implementamos os endpoints Reactive corretamente, escrevemos
os testes de endpoint usando a biblioteca rest-assured.

No próximo capítulo, continuaremos explorando os recursos reativos oferecidos pelo Quarkus e enfatizaremos os
aspectos de persistência de dados com o Hibernate Reactive.


========================================================
========================================================
========================================================
Dados persistentes com Adaptadores de saída e Hibernar Reativo

No capítulo anterior, aprendemos sobre algumas das vantagens que podem ser trazidas a um sistema usando as
capacidades reativas do Quarkus. Nosso primeiro passo na estrada reativa foi implementar o Reactive
adaptadores de entrada usando RESTEasy Reactive. Embora os endpoints dos adaptadores de entrada estejam sendo
atendidos de forma reativa, ainda temos os adaptadores de saída trabalhando de forma síncrona e bloqueante.

Para transformar o sistema hexagonal em um mais reativo, neste capítulo, primeiro aprenderemos como configurar o
Mapeamento Objeto-Relacional (ORM) em entidades do sistema usando o Hibernate Reactive e o Panache.
Depois que as entidades do sistema estiverem configuradas corretamente, aprenderemos como usá-las para se conectar
a um banco de dados MySQL de forma reativa.

A seguir estão os tópicos que abordaremos neste capítulo:
• Apresentando Hibernate Reactive e Panache
• Habilitando comportamento reativo em adaptadores de saída
• Teste de adaptadores de saída reativa

Como já implementamos adaptadores de entrada reativos no capítulo anterior, nosso objetivo aqui é estender o
comportamento reativo em um sistema hexagonal implementando adaptadores de saída reativos. Essa implementação
ocorre no hexágono do Framework, que é o elemento de arquitetura onde nos concentramos em adaptadores.

Ao final deste capítulo, você terá aprendido como integrar o Quarkus com um sistema hexagonal para
acessar bancos de dados de forma reativa. Ao entender as etapas de configuração necessárias e os detalhes
fundamentais de implementação, você será capaz de implementar adaptadores de saída reativos. Esse
conhecimento ajudará você a lidar com situações em que solicitações de E/S não bloqueantes oferecem mais vantagens do que as E/S bloqueantes.

======================================
Apresentando Hibernate Reactive e Panache

As tecnologias e técnicas disponíveis para lidar com operações de banco de dados em Java evoluíram muito nos
últimos anos. Com base na especificação Java Persistence API (JPA), fomos apresentados a diferentes implementações
de ORM, como Spring Data JPA, EclipseLink e, claro, Hibernate. Essas tecnologias tornam nossas vidas mais fáceis
ao abstrair muito do trabalho de encanador necessário para lidar com bancos de dados.

O Quarkus é integrado com o Hibernate ORM e sua contraparte reativa, o Hibernate Reactive. Além disso, o Quarkus
vem com uma biblioteca chamada Panache, que simplifica nossa interação com bancos de dados.

A seguir, daremos uma breve olhada nos principais recursos do Hibernate Reactive e do Panache.

==============================================
Recursos do Hibernate Reactive

É raro, se não impossível, encontrar uma solução mágica que resolva todos os problemas relacionados ao acesso ao
banco de dados. Quando falamos sobre as abordagens reativa e imperativa para o manuseio do banco de dados, é
fundamental entender as vantagens e desvantagens de ambas as abordagens.

O que é tão atraente sobre a abordagem imperativa para acesso ao banco de dados é a simplicidade com a qual você
desenvolve seu código. Há menos coisas para ajustar e pensar quando você precisa ler ou persistir coisas usando
uma abordagem imperativa. No entanto, essa abordagem pode causar contratempos quando sua natureza de bloqueio
começa a impactar os casos de uso do seu sistema. Para evitar tais contratempos, temos a abordagem reativa,
permitindo-nos lidar com bancos de dados de forma não bloqueante, mas não sem complexidades adicionais em
nosso desenvolvimento e os novos problemas e desafios que surgem ao lidar com bancos de dados de forma reativa.

A implementação original do Hibernate foi concebida para resolver os problemas com os quais os desenvolvedores
tinham que lidar ao mapear objetos Java para entidades de banco de dados. A implementação original depende de
comunicação síncrona de bloqueio de E/S para interagir com bancos de dados. Foi, e ainda é, a maneira mais
convencional de acessar bancos de dados em Java. Por outro lado, o Hibernate Reactive surgiu da necessidade de
movimentos de programação reativa e da comunicação assíncrona para acesso ao banco de dados. Em vez de
bloqueio de E/S, o Hibernate Reactive depende de comunicação não bloqueante de E/S para interagir com bancos de
dados.

As propriedades de mapeamento de entidade permanecem as mesmas em uma implementação reativa. No entanto, o
que muda é como abrimos a conexão Reactive de um banco de dados e como devemos estruturar o código do software
para manipular entidades de banco de dados de forma reativa.

Ao usar o Quarkus, não há necessidade de fornecer uma configuração de persistência reativa com base no
arquivo persistence.xml porque o Quarkus já o configura para nós. Ainda assim, exploraremos brevemente para
ter uma ideia de como o Hibernate Reactive funciona sozinho.

Para configurar o Hibernate Reactive, você pode seguir a abordagem padrão para configurar o arquivo METAINF/ persistence.xml , conforme mostrado no exemplo a seguir:
<persistence-unit name="mysql">
<provider>
org.hibernate.reactive.provider
.ReactivePersistenceProvider
</provider>
<class>dev.davivieria.SomeObject</class>
<properties>
<property name=»javax.persistence.jdbc.url»
value=»jdbc:mysql://localhost/hreact"/>
</properties>
</persistence-unit>

Note que estamos usando ReactivePersistenceProvider para abrir uma conexão reativa com o banco de dados.
Depois que o arquivo persistence.xml estiver configurado corretamente, podemos começar a usar o Hibernate
Reactive em nosso código:

import static javax.persistence.Persistence.createEntityManagerFactory;
SessionFactory factory = createEntityManagerFactory (
persistenceUnitName ( args ) ).unwrap(SessionFac
tory.class);
/** Code omitted **/
public static String persistenceUnitName(String[] args) {
return args.length > 0 ?
args[0] : "postgresql-example";
}

Começamos importando o método estático javax.persistence.Persistence.createEntityMan - agerFactory fornecido pelo
Hibernate Reactive. Este método estático facilita a criação de objetos SessionFactory.

Para criar um objeto SessionFactory , o sistema usa as propriedades definidas pelo arquivo persistence.xml .
Com SessionFactory, podemos iniciar a comunicação reativa com o banco de dados:

SomeObject someObject = new SomeObject();
factory.withTransaction(
(
org.hibernate.reactive.mutiny.Mutiny.
Transaction session,
org.hibernate.reactive.mutiny.Mutiny.Transaction tx) ->
session.persistAll(someObject)).subscribe();

Para persistir dados, primeiro precisamos criar uma transação chamando o método withTransaction.
Dentro de uma transação, chamamos o método persistAll de SessionFactory para persistir um
objeto. Chamamos o método subscribe para disparar a operação de persistência de forma não bloqueante.

Ao estabelecer uma camada entre o aplicativo e o banco de dados, o Hibernate fornece todas as coisas básicas que
precisamos para manipular bancos de dados em Java.

Agora, vamos ver como o Panache pode tornar as coisas ainda mais simples.

Características do Panache

O Panache fica em cima do Hibernate e o aprimora ainda mais ao fornecer uma interface simples para lidar
com as entidades do banco de dados. O Panache foi desenvolvido principalmente para trabalhar com o
framework Quarkus, e é uma biblioteca que visa abstrair grande parte do código boilerplate necessário para
lidar com as entidades do banco de dados. Com o Panache, você pode facilmente aplicar padrões de banco
de dados como Active Record e Repository. Vamos ver brevemente como fazer isso.

Aplicando o padrão Active Record

No padrão Active Record, usamos a classe que representa a entidade do banco de dados para fazer
alterações no banco de dados. Para habilitar esse comportamento, precisamos estender o PanacheEntity. Veja o exemplo a seguir:

@Entity
@Table(name="locations")
public class Location extends PanacheEntity {
@Id @GeneratedValue
private Integer id;
@NotNull @Size(max=100)
public String country;
@NotNull @Size(max=100)
public String state;
@NotNull @Size(max=100)
public String city;
}

A classe Location anterior é uma entidade regular baseada no Hibernate que estende PanacheEntity.
Além de estender PanacheEntity, não há nada de novo nessa classe Location . Temos anotações como @NotNull
e @Size que usamos para validar os dados.

A seguir estão algumas coisas que podemos fazer com uma entidade do Active Record:

• Para listar entidades, podemos chamar o método listAll . Este método está disponível em Location
porque estamos estendendo a classe PanacheEntity:

List<Location> locations = Location.listAll();

• Para excluir todas as entidades Location , podemos chamar o método deleteAll:

Location.deleteAll();

• Para encontrar uma entidade Location específica por seu ID, podemos usar o método findByIdOptional:

Optional<Location> optional = Location.findByIdOptional(locationId);

• Para persistir uma entidade Location , temos que chamar o método persist na instância
Location que pretendemos persistir:

Location location = new Location();
location.country = "Brazil";
location.state = "Sao Paulo";
location.city = "Santo Andre";
location.persist();

Toda vez que executamos uma das operações descritas anteriormente, elas são imediatamente confirmadas
no banco de dados.

Agora, vamos ver como usar o Panache para aplicar o padrão Repositório.

================================
Aplicando o padrão Repositório

Em vez de usar uma classe de entidade para executar ações no banco de dados, usamos uma classe separada que
geralmente é dedicada a fornecer operações de banco de dados no padrão Repository. Esse tipo de classe funciona
como uma interface de repositório para o banco de dados.

Para aplicar o padrão Repositório, devemos usar entidades regulares do Hibernate:

@Entity
@Table(name="locations")
public class Location {
/** Code omitted **/
}

Observe que, neste momento, não estamos estendendo a classe PanacheEntity . No padrão Repository, não
chamamos as operações do banco de dados diretamente por meio da classe entity. Em vez disso, as
chamamos por meio da classe repository. Aqui está um exemplo de como podemos implementar uma classe repository:

@ApplicationScoped
public class LocationRepository implements PanacheRepository<Location> {
public Location findByCity(String city){
return find ("city", city).firstResult();
}
public Location findByState(String state){
return find("state", state).firstResult();
}
public void deleteSomeCountry(){
delete ("country", "SomeCountry");
}
}

Ao implementar PanacheRepository na classe LocationRepository , estamos habilitando todas as operações
padrão, como findById, delete, persist, e assim por diante, que estão presentes na classe PanacheEntity .
Além disso, podemos definir nossas próprias consultas personalizadas, como fizemos no exemplo
anterior, usando os métodos find e delete fornecidos pela classe PanacheEntity.

Note que anotamos a classe do repositório como um bean @ApplicationScoped . Isso significa que
podemos injetá-lo e usá-lo em outras classes:

@Inject
LocationRepository locationRepository;
public Location findLocationByCity(City city){
return locationRepository.findByCity(city);
}

Aqui, temos as operações mais comuns disponíveis na classe de repositório:

• Para listar todas as entidades Location , precisamos chamar o método listAll
de LocationRepository:

List<Location> locations = locationRepository.listAll();

• Ao chamar deleteAll em LocationRepository, removemos todas as entidades Location:

locationRepository.deleteAll();

• Para encontrar uma entidade Location pelo seu ID, chamamos o método findByIdOptional
em LocalizaçãoRepositório:

Optional<Location> optional = locationRepository.findByIdOptional(locationId);

• Para persistir uma entidade Location , precisamos passar uma instância Location para o método persist
de LocationRepository:

Location location = new Location();
location.country = "Brazil";
location.state = "Sao Paulo";
location.city = "Santo Andre";
locationRepository.persist(location);

Nos exemplos anteriores, estamos executando todas as operações do banco de dados usando a classe de repositório. O
Os métodos que chamamos aqui são os mesmos presentes na classe de entidade da abordagem Active Record.
A única diferença aqui é o uso da classe de repositório.

Ao aprender como usar o Panache para aplicar os padrões Active Record e Repository, aumentamos
nossa capacidade de fornecer boas abordagens para lidar com entidades de banco de dados. Não há melhor ou pior
padrão. As circunstâncias do projeto acabarão por ditar qual padrão é mais adequado.

Panache é uma biblioteca feita especialmente para Quarkus. Então, a melhor maneira de conectar
objetos Hibernate Reactive como SessionFactory e Transaction ao Panache é delegando a
configuração do banco de dados para Quarkus, que fornecerá automaticamente esses objetos para você.

Agora que estamos familiarizados com o Hibernate Reactive e o Panache, vamos ver como podemos
implementar adaptadores de saída em um sistema hexagonal.

=================================
Habilitando comportamento reativo em adaptadores de saída

Um dos benefícios mais importantes do uso da arquitetura hexagonal é a flexibilidade melhorada
para mudar tecnologias sem refatoração significativa. O sistema hexagonal é projetado para que
sua lógica de domínio e regras de negócios sejam alheias às tecnologias utilizadas para executá-las.

Não existe almoço grátis – quando decidimos usar a arquitetura hexagonal, temos que pagar o preço pelos
benefícios que essa arquitetura pode fornecer. (Por preço, quero dizer um aumento considerável no esforço e
na complexidade necessários para estruturar o código do sistema seguindo os princípios hexagonais.)

Se você está preocupado com a reutilização de código, pode achar algumas práticas estranhas para desacoplar
código de tecnologias específicas. Por exemplo, considere um cenário em que temos uma classe de entidade
de domínio e uma classe de entidade de banco de dados. Podemos argumentar, por que não ter apenas uma
classe que atenda a ambos os propósitos? Bem, no final, é tudo uma questão de prioridades. Se o acoplamento
das classes específicas de domínio e tecnologia não for um problema para você, vá em frente. Nesse caso,
você não terá o fardo de manter um modelo de domínio mais todo o código de infraestrutura que o suporta. No entanto, o mesmo código
serviria a diferentes propósitos, violando assim o Princípio da Responsabilidade Única (SRP). Caso contrário, se você vir um risco
em usar o mesmo código para servir propósitos diferentes, então os adaptadores de saída podem ajudar.

No Capítulo 2, Encapsulando Regras de Negócios Dentro do Hexágono de Domínio, introduzimos um adaptador
de saída que integrava o aplicativo com o sistema de arquivos. No Capítulo 4, Criando Adaptadores para
Interagir com o Mundo Exterior, criamos um adaptador de saída mais elaborado para se comunicar com um
banco de dados H2 na memória . Agora que temos o kit de ferramentas Quarkus à nossa disposição, podemos criar adaptadores de saída reativos.

===================================================================
Configurando fontes de dados reativas

Para continuar o esforço reativo que iniciamos no capítulo anterior implementando adaptadores de entrada
reativos, criaremos e conectaremos adaptadores de saída reativos a esses adaptadores de entrada reativos
executando as seguintes etapas:

1. Vamos começar configurando as dependências necessárias no arquivo pom.xml
   do Framework hexagon:

<dependencies>
 <dependency>
 <groupId>io.quarkus</groupId>
 artifactId>quarkus-reactive-mysql-client
 </artifactId>
 </dependency>
 <dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-hibernate-reactive-panache</artifactId>
 </dependency>
</dependencies>

A dependência quarkus-reactive-mysql-client contém as bibliotecas que precisamos para abrir
uma conexão reativa com bancos de dados MySQL e a dependência quarkus-hibernatereactive-panache contém Hibernate Reactive e Panache. É importante notar que esta
biblioteca é especialmente adequada para atividades reativas. Para atividades não reativas,
o Quarkus oferece uma biblioteca diferente.

2. Agora, precisamos configurar a conexão do banco de dados no application.properties
   arquivo do hexágono Bootstrap. Vamos começar com as propriedades da fonte de dados:

quarkus.datasource.db-kind = mysql
quarkus.datasource.reactive = true
quarkus.datasource.reactive.url = mysql://lo
calhost:3306/inventory
quarkus.datasource.username = root
quarkus.datasource.password = password

A propriedade quarkus.datasource.db-kind não é obrigatória porque o Quarkus pode inferir o
tipo de banco de dados ao olhar para o cliente de banco de dados específico que é
carregado das dependências do Maven. Com quarkus.datasource.reactive definido como true,
estamos impondo conexões reativas. Precisamos especificar a URL de conexão de banco de dados reativa no quarkus.datasource.reactive.url.

3. Por fim, temos que definir a configuração do Hibernate:

quarkus.hibernate-orm.sql-load-script=inventory.sql
quarkus.hibernate-orm.database.generation = drop-and-
create
quarkus.hibernate-orm.log.sql = true

Depois que o Quarkus tiver criado o banco de dados e suas tabelas, você pode carregar um arquivo .sql
para executar mais instruções no banco de dados. Por padrão, ele procura e carrega um arquivo chamado import.
sql. Podemos alterar esse comportamento usando a propriedade quarkus.hibernateorm.sql-load -script .

Esteja ciente de não usar quarkus.hibernate-orm.database.generation = drop- and-create em produção.
Caso contrário, ele removerá todas as suas tabelas de banco de dados. Se você não definir nenhum
valor, o padrão, none, será usado. O comportamento padrão não faz nenhuma alteração no banco de
dados.

E, finalmente, habilitamos quarkus.hibernate-orm.log.sql para ver quais consultas SQL o Hibernate está
executando nos bastidores. Recomendo que você habilite o recurso de log apenas para fins de
desenvolvimento. Ao executar o aplicativo em produção, não esqueça de desabilitar esta opção.

Vamos agora ver como configurar entidades de aplicação para trabalhar com um banco de dados MySQL.

=======================
Configurando entidades

O sistema de topologia e inventário requer quatro tabelas de banco de dados para armazenar seus dados:
roteadores, switches, redes e localização. Cada uma dessas tabelas será mapeada para uma classe de entidade
Hibernate configurada corretamente para trabalhar com uma fonte de dados MySQL.

Aplicaremos o padrão Repositório, para que não tenhamos entidades para executar operações no banco de dados.
Em vez disso, criaremos classes de repositório separadas para acionar ações no banco de dados, mas antes
de criar classes de repositório, vamos começar implementando entidades do Hibernate para o sistema de topologia e inventário.
Configuraremos essas entidades para trabalhar com bancos de dados MySQL.

===========================================================
A entidade Roteador

Para esta entidade e as outras que serão implementadas posteriormente, devemos criar classes no
dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.data
pacote de dados do Framework hexagon.

Veja como a classe de entidade Router deve se parecer:

@Entity(name = "RouterData")
@Table(name = "routers")
@EqualsAndHashCode(exclude = "routers")
public class RouterData implements Serializable {
@Id
@Column(name="router_id", columnDefinition =
«BINARY(16)")
private UUID routerId;
@Column(name="router_parent_core_id",
columnDefinition = "BINARY(16)")
private UUID routerParentCoreId;
/** Code omitted **/
}

Para os campos routerId e routerParentCoreId , precisamos definir columnDefinition, o parâmetro de anotação @Column ,
como BINARY(16). É um requisito para fazer atributos UUID funcionarem em bancos de dados MySQL.

Em seguida, criamos o mapeamento de relacionamento entre roteadores e outras tabelas:

{
/**Code omitted**/
@ManyToOne(cascade = CascadeType.ALL)
@JoinColumn(name="location_id")
private LocationData routerLocation;
@OneToMany(cascade = {CascadeType.MERGE},
fetch = FetchType.EAGER)
@JoinColumn(name="router_id")
private List<SwitchData> switches;
@OneToMany(cascade = CascadeType.ALL, fetch =
FetchType.EAGER)
@JoinColumn(name="router_parent_core_id")
private Set<RouterData> routers;
/**Code omitted**/
}

Aqui, definimos uma relação muitos-para-um entre roteadores e localização. Depois disso, temos
duas relações um-para-muitos com switches e roteadores, respectivamente. A propriedade fetch =
FetchType.EAGER é usada para evitar quaisquer erros de mapeamento que possam ocorrer durante as conexões reativas.

Vamos passar para a configuração da classe de entidade Switch.

==========================================
A entidade Switch

O código a seguir nos mostra como devemos implementar a classe de entidade Switch:

@Entity
@Table(name = "switches")
public class SwitchData {
@ManyToOne
private RouterData router;
@Id
@Column(name="switch_id", columnDefinition =
«BINARY(16)")
private UUID switchId;
@Column(name="router_id", columnDefinition =
«BINARY(16)")
private UUID routerId;
@OneToMany(cascade = CascadeType.ALL, fetch =
FetchType.EAGER)
@JoinColumn(name="switch_id")
private Set<NetworkData> networks;
@ManyToOne
@JoinColumn(name="location_id")
private LocationData switchLocation;
/**Code omitted**/
}

Omitimos outros atributos de coluna para focar apenas nos IDs e relacionamentos. Começamos definindo um
relacionamento muitos-para-um entre switches e um roteador. A chave primária é o campo switchId , que por acaso é um
atributo UUID . Temos outro atributo UUID para mapear o campo routerId .

Além disso, há uma relação de um para muitos entre um switch e redes, e uma relação de muitos para um
entre switches e um local.

Agora, vamos configurar a classe de entidade Network.

===============================================
A entidade da rede

Embora não consideremos redes como entidades no modelo de domínio, elas têm uma tabela separada no banco de
dados. Então, no nível do hexágono do Framework, nós as tratamos como entidades do banco de dados, mas quando
elas alcançam o hexágono do Domínio, nós as tratamos como objetos de valor. Este exemplo mostra que o sistema
hexagonal determina como os dados serão tratados no nível do hexágono do Domínio. Ao fazer isso, o sistema
hexagonal protege o modelo de domínio de detalhes técnicos.

Implementamos a classe de entidade Network da seguinte maneira:

@Entity
@Table(name = "networks")
public class NetworkData {
@ManyToOne
@JoinColumn(name="switch_id")
private SwitchData switchData;
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name="network_id")
private int id;
/**Code omitted**/
}

Esta é uma classe de entidade simples com um relacionamento muitos-para-um entre redes e um switch
Para redes, confiamos no banco de dados para gerar IDs de rede. Além disso, redes não são consideradas entidades no modelo de
domínio. Em vez disso, tratamos redes como objetos de valor que são controlados por um agregado. Para agregados, precisamos
manipular o UUID, mas para objetos de valor, não. É por isso que não manipulamos UUIDs para entidades de banco de dados de
rede.

Ainda precisamos implementar uma última entidade para localização. Vamos fazer isso.

==================================
A entidade Localização

Em redes, a localização não é considerada uma entidade no nível do hexágono do Domínio, mas como temos uma tabela
separada para localização, precisamos tratá-la como uma entidade de banco de dados no nível do hexágono da Estrutura.

O código a seguir é usado para implementar a classe de entidade Location:

Entity
@Table(name = "location")
public class LocationData {
@Id
@Column(name="location_id")
@GeneratedValue(strategy = GenerationType.IDENTITY)
private int locationId;
@Column(name="address")
private String address;
@Column(name="city")
private String city;
/**Code omitted**/
}

Novamente, contamos com o mecanismo de geração de ID integrado ao banco de dados para manipular IDs para dados de localização.
Depois disso, temos atributos como endereço e cidade que fazem parte de um local.

Agora que temos todas as entidades necessárias configuradas adequadamente, podemos prosseguir e usar o Panache
para criar classes de repositório reativas, que usaremos para acionar operações de banco de dados com as entidades
que configuramos.

===========================
Implementando classes de repositório reativa

Ao implementar a interface PanacheRepositoryBase , você cria uma classe de repositório reativa.
Precisaremos de uma classe de repositório para operações de roteador e outra para operações de switch.

É fundamental definir apenas um repositório para a raiz agregada. No nosso caso, a entidade
Router é a raiz agregada para operações de gerenciamento de roteador, e Switch é a raiz agregada
para operações de gerenciamento de switch. O propósito de um agregado é garantir a consistência
em todos os objetos que são controlados por tal agregado. O ponto de entrada para qualquer agregado é sempre a raiz agregada.
Para garantir a consistência agregada em uma transação de banco de dados, definimos apenas uma classe de repositório, que
é dedicado a controlar as operações do banco de dados com base na raiz agregada.

As classes que estamos prestes a implementar estão localizadas no pacote
dev.hugodesouzacaramez.topologyinventory.framework.adapters.output.mysql.repository:

• O código a seguir implementa a classe RouterManagementRepository:

@ApplicationScoped
public class RouterManagementRepository implements PanacheRepositoryBase<RouterData, UUID> {
}

Note que estamos passando RouterData como a entidade na qual estamos trabalhando e UUID como o
tipo de atributo mapeado para ser usado pelo ID. Se não precisarmos de nenhuma consulta personalizada,
podemos deixar essa classe vazia porque o Panache já fornece muitas operações de banco de dados padrão.

Observe que também estamos anotando essa classe com @ApplicationScoped, para que possamos
injetar esse componente em outros lugares, como no adaptador de saída, que implementaremos em breve.

• O código a seguir implementa a classe SwitchManagementRepository:

@ApplicationScoped
public class SwitchManagementRepository implements PanacheRepositoryBase<SwitchData, UUID> {
}

Aqui, estamos seguindo a mesma abordagem que usamos para a classe RouterManagementRepository .

Com as classes de repositório reativo implementadas corretamente, estamos prontos para criar adaptadores
de saída reativos. Vamos fazer isso!

=================================
Implementando adaptadores de saída reativos

Só para recapitular, precisamos fornecer uma implementação de adaptador para a interface de porta de saída
RouterManagementOutputPort:

public interface RouterManagementOutputPort {
Router retrieveRouter(Id id);
boolean removeRouter(Id id);
Router persistRouter(Router router);
}

Ao implementar o adaptador de saída do MySQL, forneceremos uma implementação reativa para cada uma
das declarações de método anteriores.

Também precisamos implementar a interface do adaptador de saída SwitchManagementOutputPort:

public interface SwitchManagementOutputPort {
Switch retrieveSwitch(Id id);
}

É mais simples, pois há apenas um método para o qual precisamos fornecer uma implementação reativa.

Vamos começar implementando o adaptador de saída reativa para gerenciamento do roteador.

===============================================
Gerenciamento reativo do roteador do adaptador de saída MySQL

Para habilitar o sistema hexagonal a se comunicar com um banco de dados MySQL, precisamos criar um novo
adaptador de saída para permitir tal integração (porque estamos usando Quarkus, tal implementação de
adaptador de saída é bem simples). Usaremos os seguintes passos para fazer isso:

1. Começamos injetando a classe de repositório RouterManagementRepository:

@ApplicationScoped
public class RouterManagementMySQLAdapter implements
RouterManagementOutputPort {
@Inject
RouterManagementRepository
routerManagementRepository;
/** Code omitted **/
}

Usaremos o repositório RouterManagementRepository para fazer operações de banco de dados.

2. Em seguida, implementamos o método retrieveRouter:

@Override
public Router retrieveRouter(Id id) {
var routerData =
routerManagementRepository.findById(id.getUuid())
.subscribe()
.asCompletionStage()
.join();
return RouterMapper.routerDataToDomain(router
Data);
}

Quando chamamos routerManagementRepository.findById(id.getUuid()), o sistema inicia
uma operação de E/S não bloqueante. Esta chamada subscribe tenta resolver o item
produzido pela operação findById . Então, chamamos asCompletionStage para receber
o item. Finalmente, chamamos join, que retorna o valor do resultado quando a operação é concluída.

3. Agora, precisamos implementar o método removeRouter:

@Override
public Router removeRouter(Id id) {
return routerManagementRepository
.deleteById(
id.getUuid())
.subscribe().asCompletionStage().join();
}

Aqui, chamamos routerManagementRepository.deleteById(id.getUuid())
Gerenciamento de switch reativo do adaptador de saída MySQL
Operação Panache para remover um roteador do banco de dados. Depois disso, chamamos subscribe,
asCompletionStage e join para executar as operações de forma reativa.

4. Por fim, implementamos o método persistRouter:

@Override
public Router persistRouter(Router router) {
var routerData =
RouterH2Mapper.routerDomainToData(router);
Panache.withTransaction(
()->routerManagementRepository.persist
(routerData));
return router;
}

A construção é diferente aqui. Para garantir que a transação não será perdida entre o cliente e o
servidor durante a solicitação, encapsulamos a operação de persistência dentro de Panache.
withTransaction. Este é um requisito para operações em que precisamos persistir dados.

Vamos agora implementar o adaptador de saída reativa para gerenciamento de switches.

================================================================================
Gerenciamento de switch reativo do adaptador de saída MySQL

A abordagem usada aqui é a mesma utilizada quando implementamos o adaptador de saída reativa para
gerenciamento de roteador. Executaremos as seguintes etapas para implementar o adaptador de saída reativa:

1. Vamos começar injetando a classe de repositório SwitchManagementRepository:

@ApplicationScoped
public class SwitchManagementMySQLAdapter implements
SwitchManagementOutputPort {
@Inject
SwitchManagementRepository
switchManagementRepository;
/** Code omitted **/
}

Como já vimos, a injeção de uma classe de repositório é necessária para que possamos usá-la para acionar
operações de banco de dados.

2. Depois disso, implementamos o método retrieveSwitch:

@Override
public Switch retrieveSwitch(Id id) {
var switchData =
switchManagementRepository.findById(id.getUuid())
.subscribe()
.asCompletionStage()
.join();
return RouterMapper.switchDataToDo
main(switchData);
}

Usamos esse método para recuperar um objeto Switch reativo. Não há métodos de persistência porque
todas as operações de gravação devem sempre ocorrer por meio de um adaptador de saída de gerenciamento de roteador.

Ao implementar adaptadores de saída reativos no sistema hexagonal, podemos aproveitar as vantagens
das técnicas de programação reativa. Com a arquitetura hexagonal, não é um grande problema ter
adaptadores de saída reativos e imperativos atendendo a necessidades diferentes no mesmo sistema.

Os recursos reativos do Quarkus para bancos de dados são primordiais para qualquer um que se
aventure no desenvolvimento de sistemas reativos. Podemos fornecer uma alternativa reativa para como
nosso aplicativo lida com bancos de dados ao entender como usar esses recursos. Isso não significa
que a abordagem reativa seja sempre uma escolha melhor do que a tradicional imperativa; cabe a você e às necessidades do seu projeto
decidir qual abordagem é mais adequada.

Agora que implementamos os adaptadores de saída RouterManagementMySQLAdapter
e SwitchManagementMySQLAdapter , vamos testá-los.

===================================================================
Testando os adaptadores de saída reativa

Precisamos implementar testes de unidade para garantir que os métodos dos adaptadores de saída estejam funcionando conforme o esperado.

Aqui está um exemplo de como podemos criar testes de unidade para RouterManagementMySQLAdapter:

@QuarkusTest
public class RouterManagementMySQLAdapterTest {
@InjectMock
RouterManagementMySQLAdapter
routerManagementMySQLAdapter;
@Test
public void testRetrieveRouter() {
Router router = getRouter();
Mockito.when(
routerManagementMySQLAdapter.
retrieveRouter(router.getId())).thenReturn(router);
Router retrievedRouter =
routerManagementMySQLAdapter.
retrieveRouter(router.getId());
Assertions.assertSame(router, retrievedRouter);
}
/** Code omitted **/
}

É possível usar a anotação @InjectMock para simular o RouterManagementMySQLAdapter
adaptador de saída. Ao executar o método de teste testRetrieveRouter , podemos simular
uma chamada para routerManagementMySQLAdapter.retrieveRouter(router.getId) usando
Mockito.when. O método thenReturn retorna o objeto que nosso teste simulado deve retornar. Neste caso, é um objeto Router .
Com Assertions.assertSame(router, retrievedRouter), podemos afirmar o resultado para a execução de
retrieveRouter(router.getId).

Não precisaremos implementar novas classes de teste para executar testes de integração para adaptadores de saída reativos.
Podemos confiar nos mesmos testes usados no capítulo anterior para testar os adaptadores de entrada reativos.
Esses testes chamam os adaptadores de entrada, que, por sua vez, chamam os adaptadores de saída usando as operações de caso de uso.

No entanto, o que muda é que precisaremos de um banco de dados MySQL para testar os adaptadores de saída reativos.

O Quarkus fornece contêineres baseados em Docker que podemos usar para fins de desenvolvimento ou teste. Para
habilitar tal contêiner de banco de dados, não há necessidade de fornecer uma conexão detalhada de fonte de dados
configuração no arquivo application.properties . Aqui está como devemos configurar esse arquivo para fins de teste:

quarkus.datasource.db-kind=mysql
quarkus.datasource.reactive=true
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.hibernate-orm.sql-load-script=inventory.sql
quarkus.vertx.max-event-loop-execute-time=100

Observe que não estamos especificando uma URL de conexão de banco de dados. Ao fazer isso,
o Quarkus entende que precisa fornecer um banco de dados. O arquivo application.properties
descrito anteriormente deve ser colocado no diretório tests/resource/ . Dentro desse diretório,
também devemos colocar o arquivo inventory.sql , que carrega dados no banco de dados.

Você pode substituir entradas em application.properties para usar variáveis de
ambiente. Isso pode ser útil para configurações como quarkus.hibernate-orm.database.generation
onde você pode definir o valor da propriedade com base nas variáveis de ambiente do aplicativo. Por
exemplo, para propósitos locais ou de desenvolvimento, você pode usar ${DB_GENERATION}, uma
variável de ambiente que resolve para drop-and-create. Na produção, essa variável de ambiente pode resolver para none.

Depois de configurar corretamente os arquivos application.properties e inventory.sql , podemos
testar o aplicativo executando o seguinte comando no diretório raiz do projeto:

mvn test

A saída a seguir mostra o contêiner MySQL Docker sendo criado para ser usado durante os testes:

2021-10-10 01:33:40,242 INFO [ .0.24]] (build-10) Criando contêiner para imagem: mysql:8.0.24

2021-10-10 01:33:40,876 INFO [ .0.24]] (build-10) Iniciando contêiner com
ID: 67e788aab66f2f2c6bd91c0be1a164117294ac29cc574941ad41ff5760de918c

2021-10-10 01:33:41,513 INFO [ .0.24]] (build-10) O contêiner mysql:8.0.24 está iniciando:
67e788aab66f2f2c6bd91c0be1a164117294ac29cc574941ad41ff5760de918c

2021-10-10 01:33:41,520 INFO [ .0.24]] (build-10) Aguardando a conexão com o banco de dados ficar
disponível em jdbc:mysql://localhost:49264/default usando a consulta 'SELECT 1'
2021-10-10 01:34:01,078 INFO [ .0.24]] (build-10) O contêiner foi iniciado (URL JDBC: jdbc:mysql://localhost:49264/
default)
2021-10-10 01:34:01,079 INFO [ .0.24]] (build-10) Container mysql:8.0.24 iniciado em PT20.883579S

2021-10-10 01:34:01,079 INFORMAÇÕES [io.qua.dev.mys.dep.MySQLDevServicesProcessor] (build-10) Serviços de desenvolvimento para MySQL iniciados.

O Quarkus cria um banco de dados chamado default, onde as tabelas são criadas. O inventory.sql
arquivo é executado neste banco de dados padrão.

Após o banco de dados estar pronto, o Quarkus inicia os testes do sistema, obtendo um resultado semelhante ao
seguinte:

[INFO] Testes executados: 2, Falhas: 0, Erros: 0, Ignorados: 0, Tempo decorrido: 32.672 s - em
dev.davivieira.topologyinventory.framework.adapters.entrada.rest.NetworkManagementAdapterTest
[INFO] Executando dev.davivieira.topologyinventory.framework.adapters.entrada.rest.RouterManagementAdapterTest
[INFO] Testes executados: 5, Falhas: 0, Erros: 0, Ignorados: 0, Tempo decorrido: 0,232 s - em
dev.davivieira.topologyinventory.framework.adapters.entrada.rest.RouterManagementAdapterTest
[INFO] Executando dev.davivieira.topologyinventory.framework.adapters.entrada.rest.SwitchManagementAdapterTest
[INFO] Testes executados: 3, Falhas: 0, Erros: 0, Ignorados: 0, Tempo decorrido: 0,088 s - em
dev.davivieira.topologyinventory.framework.adapters.entrada.rest.SwitchManagementAdapterTest
[INFO] Executando dev.davivieira.topologyinventory.framework.adapters.entrada.rest.saídaAdaptadores.RoteadorGerenciamentoMySQLAdapterTest
[INFO] Testes executados: 3, Falhas: 0, Erros: 0, Ignorados: 0, Tempo decorrido: 0,116 s - em
dev.davivieira.topologyinventory.framework.adapters.entrada.rest.saídaAdaptadores.RoteadorGerenciamentoMySQLAdapterTest
[INFO] Executando dev.davivieira.topologyinventory.framework.adapters.entrada.rest.saídaAdaptadores.SwitchManagementMySQLAdapterTest
[INFO] Testes executados: 1, Falhas: 0, Erros: 0, Ignorados: 0, Tempo decorrido: 0,013 s - em
dev.davivieira.topologyinventory.framework.adapters.entrada.rest.saídaAdaptadores.SwitchManagementMySQLAdapterTest

Para testar os adaptadores de saída, precisamos chamar os adaptadores de entrada. Se pudermos testar os adaptadores de
entrada com sucesso, isso significa que também estamos testando os adaptadores de saída com sucesso.

=========================================
Resumo

Hibernate Reactive e Panache tornam nossas vidas muito mais fáceis quando precisamos lidar com bancos de dados
reativo usando Quarkus. Aprendemos que o Hibernate Reactive é construído sobre a implementação
tradicional do Hibernate, mas com a adição de recursos reativos.

Ao analisar o Panache, aprendemos que ele pode nos ajudar a implementar os padrões Active
Record e Repository para implementar operações de banco de dados. Para a parte prática,
implementamos entidades de banco de dados, repositórios e adaptadores de saída reativos, que
usamos juntos para interagir com o banco de dados MySQL. Por fim, configuramos os testes de
sistema hexagonal para usar o contêiner MySQL Docker fornecido pelo Quarkus.

No próximo capítulo, aprenderemos sobre algumas técnicas para empacotar o sistema hexagonal em uma
imagem Docker. Também aprenderemos como executar o sistema hexagonal em um cluster Kubernetes. Esse conhecimento irá
nos permite deixar nosso aplicativo hexagonal pronto para ser implantado em ambientes baseados em nuvem.

=====================================================================
==========================================================================================
=================================================================================
Configurando objetos Dockerfile e Kubernetes para implantação na nuvem

Passamos os capítulos anteriores explorando alguns dos recursos incríveis que o Quarkus fornece
para nos ajudar a criar aplicativos nativos da nuvem. Indo ainda mais longe, também aprendemos como
integrar o Quarkus em um sistema hexagonal.

Agora, precisamos preparar o sistema hexagonal para que ele possa ser implantado em ambientes de nuvem.
Docker e Kubernetes são as principais tecnologias que dominam o cenário de nuvem atualmente. Se seu
aplicativo estiver preparado para rodar nessas tecnologias, você está seguro para fazê-lo rodar na maioria dos provedores de nuvem.

Então, neste capítulo, aprenderemos como encapsular o sistema hexagonal em uma imagem Docker e
executá-lo em um cluster Kubernetes. Para imagens Docker, exploraremos duas técnicas para criar
tais imagens: uma que depende de um executável .jar e outra que usa um executável nativo. Também
aprenderemos como implantar o sistema hexagonal em um cluster Kubernetes local baseado em minikube.

Os seguintes tópicos serão abordados neste capítulo:

• Preparando a imagem do Docker
• Criação de objetos Kubernetes
• Implantando no minikube

Ao final deste capítulo, você saberá como fazer o sistema hexagonal rodar em um ambiente nativo
da nuvem com base em Docker e Kubernetes. Hoje em dia, a maioria dos aplicativos modernos roda na nuvem.
Ao transformar o sistema hexagonal em um sistema nativo da nuvem, você poderá aproveitar as vantagens
que existem quando você está na nuvem.

====================================================
Preparando a imagem do Docker

A tecnologia de virtualização baseada em contêiner não é algo novo. Muito antes do Docker, havia tecnologias
como o OpenVZ, que aplicava os mesmos conceitos fundamentais que são aplicados pelo Docker também.
Ainda hoje, temos alternativas como o Linux Containers (LXC), que fornece uma solução robusta baseada em
contêiner. O que diferencia o Docker é o quão fácil e intuitivo ele torna o manuseio de aplicativos em contêiner.
O Docker leva a portabilidade a outro nível, simplificando e tornando os contêineres uma tecnologia viável
para públicos maiores.

No passado, outras plataformas de contêineres não eram tão simples de usar quanto o Docker é hoje. Os
contêineres eram um tópico mais relacionado a administradores de sistemas do que a desenvolvedores de
software. Hoje, o cenário é diferente por causa da solução simples, mas poderosa, baseada em contêineres
que temos com o Docker. Por causa de sua simplicidade, o Docker rapidamente se tornou popular entre os
desenvolvedores, que começaram a incorporá-lo em seus projetos.

Como mencionei anteriormente, a força do Docker está na sua simplicidade de uso e aprendizado. Veja, por
exemplo, como o Docker abstrai a complexidade necessária para encapsular um aplicativo dentro de um
contêiner. Você só precisa definir um Dockerfile descrevendo como o aplicativo deve ser configurado e
executado dentro do contêiner. Você pode fazer isso usando um conjunto simples de instruções. Então, o
Docker protege o usuário de complexidades de baixo nível que existiam em tecnologias de contêiner anteriores.

Uma das coisas que torna o Quarkus tão especial é que ele é um framework container-first. Ele foi projetado
para construir aplicativos baseados em contêiner. 

Com o Quarkus, podemos gerar imagens Docker usando artefatos .jar ou artefatos executáveis nativos.
Exploraremos ambas as abordagens a seguir.

========================================================================
Criando uma imagem Docker com um artefato uber .jar

Nossa abordagem aqui é encapsular o artefato uber .jar na imagem Docker para que o contêiner
possa iniciar e executar o aplicativo executando esse arquivo .jar . Para construir uma imagem
Docker, precisamos criar um Dockerfile com instruções para construir tal imagem.

O código a seguir mostra como criar um Dockerfile para o sistema de topologia e inventário que
usa o arquivo .jar do uber:

FROM eclipse-temurin:17.0.8_7-jdk-alpine
ENV APP_FILE_RUNNER bootstrap-1.0-SNAPSHOT-runner.jar
ENV APP_HOME /usr/apps
EXPOSE 8080
COPY bootstrap/target/$APP_FILE_RUNNER $APP_HOME/
WORKDIR $APP_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -jar $APP_FILE_RUNNER"]

Este Dockerfile deve ser colocado no diretório raiz do projeto.

A primeira linha é a imagem base do JDK 17 da qual construiremos nossa imagem. Então, definimos as
variáveis de ambiente APP_FILE_ RUNNER e APP_HOME para definir o nome e o caminho do artefato, respectivamente.
Como o Quarkus está configurado para rodar na porta 8080, temos que usar a propriedade
EXPOSE para expor essa porta externamente. O comando COPY copiará o artefato gerado pelo
Maven. WORKDIR define o caminho de onde os comandos serão executados dentro do
contêiner. Com ENTRYPOINT e CMD, podemos definir como o contêiner executará o arquivo uber .jar do aplicativo.

Siga estas etapas para gerar a imagem do Docker e iniciar o contêiner:

1. Primeiro, precisamos compilar e gerar um arquivo uber .jar:

$ mvn clean package

2. Então, podemos gerar a imagem Docker:

$ docker build . -t topology-inventory
Sending build context to Docker daemon 38.68MB
Step 1/8 : FROM eclipse-temurin:17.0.8_7-jdk-alpine
---> 9b2a4d2e14f6
Step 2/8 : ENV APP_FILE_RUNNER bootstrap-1.0-SNAPSHOT-runner.jar
---> Using cache
---> 753b39c99e78
Step 3/8 : ENV APP_HOME /usr/apps
---> Using cache
---> 652c7ce2bd47
Step 4/8 : EXPOSE 8080
---> Using cache
---> 37c6928bcae4
Step 5/8 : COPY bootstrap/target/$APP_FILE_RUNNER $APP_HOME/
---> Using cache
---> 389c28dc9fa7
Step 6/8 : WORKDIR $APP_HOME
---> Using cache
---> 4ac09c0fe8cc
Step 7/8 : ENTRYPOINT ["sh", "-c"]
---> Using cache
---> 737bbcf2402b
Step 8/8 : CMD ["exec java -jar $APP_FILE_RUNNER"]
---> Using cache
---> 3b17c3fa0662
Successfully built 3b17c3fa0662
Successfully tagged topology-inventory:latest

A saída anterior descreve todas as etapas que precisam ser executadas para gerar a imagem
do Docker. Aqui, podemos ver que o Docker Engine começa a construir nossa imagem em
cima da imagem eclipse- temurin:17.0.8_7-jdk-alpine . Em seguida, ele prossegue definindo as
variáveis de ambiente e manipulando o artefato do aplicativo, preparando-o para ser
executado toda vez que um novo contêiner dessa imagem for criado.

3. Agora, podemos iniciar o contêiner com o seguinte comando:

$ docker run -p 5555:8080 topology-inventory

Com o parâmetro -p , estamos mapeando a porta do host 5555 para a porta do contêiner 8080.
Então, precisaremos usar a porta 5555 para acessar o sistema.

4. Para confirmar se o aplicativo está sendo executado no contêiner Docker, podemos acessar o Swagger
   URL da interface do usuário em http://localhost:5555/q/swagger-ui.

Agora, vamos aprender como gerar uma imagem do Docker usando o executável nativo.

=======================================================
Criando uma imagem Docker com um executável nativo

No Capítulo 10, Adicionando Quarkus a um aplicativo hexagonal modularizado, aprendemos que o Quarkus
usa técnicas de compilação Ahead-Of-Time (AOT) para otimizar o bytecode e gerar código nativo que oferece
melhor desempenho, principalmente durante a inicialização do aplicativo.

Este executável nativo é um produto da compilação AOT que é realizada pelo Quarkus. Ao contrário
do arquivo uber .jar , que pode ser distribuído para rodar em diferentes sistemas operacionais e
arquiteturas de CPU, o arquivo executável nativo é dependente da plataforma. Mas podemos
superar essa limitação encapsulando o executável nativo em uma imagem Docker que pode ser
distribuída para diferentes sistemas operacionais e arquiteturas de CPU.

Existem diferentes abordagens para gerar um executável nativo. Algumas delas exigem que instalemos uma
distribuição GraalVM e outro software. No entanto, para manter as coisas simples, seguiremos uma abordagem
descomplicada e conveniente, onde o Quarkus gera o executável nativo para nós dentro de um contêiner
Docker que contém o GraalVM.

Siga estas etapas para gerar uma imagem do Docker com um executável nativo:

1. No arquivo pom.xml do diretório raiz do projeto, precisamos incluir o seguinte código
   antes da tag </project>:

<profiles>
 <profile>
 <id>native</id>
 <properties>
 <quarkus.package.type>native
 </quarkus.package.type>
 </properties>
 </profile>
</profiles>

A configuração anterior cria um perfil que define a propriedade quarkus.package.type como nativa,
fazendo com que o Quarkus crie um artefato executável nativo.

2. Então, devemos criar a classe ReflectionConfiguration no hexágono bootstrap:

@RegisterForReflection(targets = {
CoreRouter.class,
EdgeRouter.class,
Switch.class,
Id.class,
IP.class,
Location.class,
Model.class,
Network.class,
Protocol.class,
RouterType.class,
SwitchType.class,
Vendor.class,
})
public class ReflectionConfiguration {}

Uma das limitações do executável nativo é que ele oferece suporte parcial para reflexão.
Reflexão é uma técnica que nos permite inspecionar ou modificar os atributos de tempo de execução
de componentes Java, como classes e métodos. Quando estamos executando um aplicativo dentro de
uma JVM, o sistema pode detectar as classes/métodos/campos que estão indiretamente conectados. O
mesmo não é verdade quando estamos executando um executável nativo. O motivo para isso é que
apenas classes que estão diretamente conectadas são visíveis para reflexão.

Para superar essa limitação, precisamos registrar todas as classes para reflexão que não
estão diretamente conectadas. Há duas maneiras de fazer isso: podemos colocar essas
classes em um arquivo de configuração .json ou podemos criar uma classe anotada com a
anotação @RegisterForReflection contendo as classes que queremos registrar para reflexão.
No código anterior, estamos usando a última abordagem, que depende da classe anotada.

3. Para gerar um executável nativo, temos que executar o seguinte comando:

$ mvn clean package -P native -D quarkus.native.containerbuild=true -D native-image.xmx=6g

O processo de compilação de um executável nativo é muito caro em termos de consumo de
memória. Então, precisamos aumentar os limites de memória para evitar erros de falta de memória.
Se 6g não for o suficiente para você, sinta-se à vontade para aumentá-lo para evitar erros.

4. Em seguida, devemos criar um arquivo chamado Dockerfile-native que contém instruções para
   construindo uma imagem Docker com o executável nativo:

FROM registry.access.redhat.com/ubi8/ubi-minimal
ENV APP_FILE_RUNNER bootstrap-1.0-SNAPSHOT-runner
ENV APP_HOME /work
EXPOSE 8080
COPY bootstrap/target/$APP_FILE_RUNNER $APP_HOME/
WORKDIR $APP_HOME
RUN echo $APP_FILE_RUNNER
CMD ["./bootstrap-1.0-SNAPSHOT-runner", "-
Dquarkus.http.host=0.0.0.0"]

Em vez da imagem base do JDK 17, estamos usando a imagem ubi-minimal do registro oficial
do Red Hat. Esta imagem é adequada para executar executáveis nativos.

5. Então, devemos gerar a imagem Docker com o seguinte comando:

$ docker build . -t topology-inventory-native -f Dockerfile-native

Você deve executar o comando anterior no diretório raiz do projeto.

Usamos -t topology-inventory-native:latest e -f Dockerfile-native
para criar uma imagem Docker diferente com base no executável nativo em vez do uber .jar
arquivo. A saída deste comando docker build será semelhante à que geramos quando criamos a imagem
do Docker para o arquivo uber .jar . A única diferença serão as entradas relacionadas ao artefato
executável nativo.

6. Marque e carregue sua imagem no seu registro pessoal do Docker:

$ docker tag topology-inventory-native:latest hugodesouzacaramez/topology-inventory-native:latest
docker login
$ docker push hugodesouzacaramez/topology-inventory-native:latest
The push refers to repository [docker.io/hugodesouzacaramez/topology-inventory-native]
f3216c6ba268: Pushed
0b911edbb97f: Layer already exists
54e42005468d: Layer already exists
latest: digest: sha256:4037e5d9c2cef01bda9c4bb5722bccbe0d003336534c28f8245076223ce77273 size: 949

Usaremos a imagem nativa do sistema ao implantar o aplicativo em um cluster minikube.

7. Agora, podemos iniciar o contêiner:

docker run -p 5555:8080 topology-inventory-native:latest

Observe que o aplicativo está inicializando muito mais rápido!

8. Para confirmar se o aplicativo está sendo executado no contêiner Docker, podemos acessar o Swagger
   URL da interface do usuário em http://localhost:5555/q/swagger-ui.

Com isso, configuramos as imagens do Docker para os artefatos executáveis nativos e uber .jar . Essas
imagens do Docker podem ser implantadas em um cluster do Kubernetes. No entanto, para fazer isso,
precisamos criar os objetos do Kubernetes necessários para permitir a implantação. Então, na
próxima seção, aprenderemos como criar objetos do Kubernetes para o sistema hexagonal em contêiner.

========================================================
Criando objetos Kubernetes

O Docker Engine não fornece nenhum mecanismo de tolerância a falhas ou alta disponibilidade. Ele
oferece apenas tecnologia de virtualização baseada em contêiner. Então, se você planeja executar um
aplicativo de missão crítica usando o Docker, pode ser necessário elaborar sua solução para garantir que os contêineres sejam confiáveis
durante a execução ou delegar essa responsabilidade a um orquestrador de contêineres.

Os orquestradores de contêineres surgiram como uma resposta ao uso crescente de contêineres no setor de TI.
Entre esses orquestradores, podemos citar o Docker Swarm, o Rancher e aquele que domina o setor: o Kubernetes.

Inicialmente concebido no Google como um software de código fechado chamado Borg, ele foi aberto com o nome
Kubernetes. É uma tecnologia poderosa que pode rodar no seu computador para propósitos de desenvolvimento ou
controlar uma frota de centenas, até milhares, de nós de servidores, fornecendo Pods para os aplicativos em execução.

Você pode estar se perguntando, o que é um Pod? Descobriremos em breve.

Não é nossa intenção aqui nos aprofundar nos detalhes internos do Kubernetes, mas revisaremos alguns conceitos
básicos para garantir que estamos na mesma página.

=================================================
Revisando os principais objetos do Kubernetes

Como vimos anteriormente, o Kubernetes é um orquestrador de contêineres que nos ajuda a gerenciar
contêineres. Para fazer isso, a maioria – se não todas – as configurações do Kubernetes podem ser feitas por
meio de arquivos .yaml . No Kubernetes, temos a noção do estado atual e do estado desejado. Quando o
primeiro encontra o último, estamos bem. Caso contrário, temos problemas.

A espinha dorsal dessa abordagem de estado desejado atualmente é o mecanismo de configuração do Kubernetes
baseado em arquivos YAML. Com esses arquivos, podemos expressar o estado desejado das coisas dentro do
cluster. O Kubernetes fará sua mágica para garantir que o estado atual sempre corresponda ao estado desejado.
Mas, você pode estar se perguntando, o estado de quê? A resposta é o estado dos objetos do Kubernetes. Vamos dar uma olhada em alguns deles:

• Pod: Um Pod é um objeto Kubernetes que controla o ciclo de vida de contêineres em um cluster
Kubernetes. É possível anexar mais de um contêiner ao mesmo Pod, embora isso não seja uma
prática comum.

• Implantação: Se um Pod controla o ciclo de vida dos contêineres, podemos afirmar que uma Implantação
object controla o ciclo de vida dos Pods. Com um Deployment, você pode especificar quantos Pods deseja
fornecer para seu aplicativo. O Kubernetes cuidará de encontrar os recursos disponíveis no cluster para
ativar esses Pods. Se, por algum motivo, um dos Pods cair, o Kubernetes tentará ativar um Pod totalmente
novo para garantir que o estado desejado seja atendido.

• Serviço: Quando implantamos Pods no cluster Kubernetes, eles não ficam imediatamente
disponíveis internamente para outros Pods ou externamente para clientes fora do cluster.
Para tornar um Pod implantado disponível na rede, precisamos criar um objeto Service anexado a esse Pod. Este objeto de serviço
atua como um ponto de entrada DNS que fornece acesso básico de balanceamento de carga aos
Pods. Por exemplo, se você tiver um aplicativo em execução em três Pods, o objeto Service manipulará
solicitações de aplicativo para um dos três Pods localizados atrás do objeto Service . Recursos de
balanceamento de carga mais sofisticados podem ser obtidos usando tecnologias de malha de serviço, como Istio.

• ConfigMap: Se você precisar fornecer variáveis de ambiente ou montar um arquivo de configuração dentro
de um Pod, o ConfigMap é o objeto que pode ajudá-lo com isso.

• Secret: Funciona de forma semelhante ao ConfigMap, mas pode ser usado para armazenar informações
confidenciais, como credenciais ou chaves privadas. Os dados em um objeto Secret devem ser codificados com base64.

Agora que estamos mais familiarizados com alguns dos objetos mais importantes do Kubernetes, vamos ver como
podemos usá-los para preparar nosso sistema hexagonal para ser implantado em um cluster do Kubernetes.

===================================================================
Configurando objetos do Kubernetes para o sistema hexagonal

Antes de criar os objetos do Kubernetes, primeiro, vamos configurar o Quarkus para habilitar a configuração YAML e
também um mecanismo de verificação de integridade. Precisaremos de ambos quando estivermos implantando o aplicativo no Kubernetes:

<dependencies>
 <dependency>
<groupId>io.quarkus</groupId>
 <artifactId>quarkus-config-yaml</artifactId>
 </dependency>
 <dependency>
 <groupId>io.quarkus</groupId>
 <artifactId>quarkus-smallrye-health</artifactId>
 </dependency>
</dependencies>

Com quarkus-config-yaml, podemos usar o arquivo application.yaml para a maioria das
configurações do Quarkus. E para habilitar endpoints de health checks, podemos usar quarkus-smallrye-health.
Quando o aplicativo for iniciado, ele tentará resolver uma variável de ambiente chamada QUARKUS_
Antes de criar os objetos do Kubernetes, vamos configurar o arquivo application.yaml
no hexágono do bootstrap:

quarkus:
datasource:
username: ${QUARKUS_DATASOURCE_USERNAME:root}
password: ${QUARKUS_DATASOURCE_PASSWORD:password}
reactive:
url: ${QUARKUS_DATASOURCE_REACTIVE_URL:
mysql://localhost:3306/inventory}

Este arquivo .yaml nos permite usar a maioria, mas não todas, das configurações disponíveis no
Quarkus. Então, é normal usar tanto application.yaml quanto application.properties. Estamos
usando a configuração YAML porque podemos empregar uma técnica chamada interpolação
de variáveis. Veja, por exemplo, a seguinte entrada de configuração:

${QUARKUS_DATASOURCE_USERNAME:root}

Quando o aplicativo for iniciado, ele tentará resolver uma variável de ambiente chamada QUARKUS_DATASOURCE_USERNAME. Se o aplicativo não puder resolver o nome da variável, ele retornará ao valor
padrão de root. Essa técnica é muito útil para definir configurações padrão para desenvolvimento local,
onde variáveis de ambiente podem não ser definidas.

Você deve ter notado a presença do QUARKUS_DATASOURCE_USERNAME, QUARKUS_DATASOURCE_PASSWORD
e QUARKUS_DATASOURCE_REACTIVE_URL . O
Kubernetes fornecerá essas variáveis de ambiente com o Secret e o ConfigMap
objects. Então, vamos aprender como configurar esses e os outros objetos do Kubernetes que são
necessários para implantar o sistema de topologia e inventário (os arquivos que descreveremos aqui são
colocados dentro de um diretório chamado k8s no diretório raiz do projeto):

1. Começaremos configurando o arquivo configmap.yaml:

apiVersion: v1
kind: ConfigMap
metadata:
name: topology-inventory
data:
QUARKUS_DATASOURCE_REACTIVE_URL:
«mysql://topology-inventory-mysql:3306/inventory»

Este ConfigMap fornece uma variável de ambiente QUARKUS_DATASOURCE_REACTIVE_URL
com a URL do banco de dados reativo que o aplicativo precisa para se conectar ao banco de dados MySQL.

2. Então, devemos configurar o arquivo secret.yaml:

apiVersion: v1
kind: Secret
metadata:
name: topology-inventory
type: Opaque
data:
QUARKUS_DATASOURCE_USERNAME: cm9vdAo=
QUARKUS_DATASOUCE_PASSWORD: cGFzc3dvcmQK

No segredo anterior, definimos as variáveis de ambiente QUARKUS_DATASOURCE_USERNAME
e QUARKUS_DATASOUCE_PASSWORD como as credenciais para conectar ao banco de
dados MySQL do sistema.

3. Para gerar base64, você pode executar o seguinte comando em sistemas baseados em Unix:

$ echo root | base64 && echo password | base64
cm9vdAo=
cGFzc3dvcmQK

Usamos os valores root e password como credenciais para autenticação no banco de dados MySQL.

4. Vamos configurar o arquivo deployment.yaml:

apiVersion: apps/v1
kind: Deployment
metadata:
name: topology-inventory
labels:
app: topology-inventory
spec:
replicas: 1
selector:
matchLabels:
app: topology-inventory
template:
metadata:
labels:
app: topology-inventory
/** Code omitted **/

Aqui, descrevemos algumas das entradas de metadados do arquivo deployment.yaml:

O campo metadata.labels.app : Um objeto Kubernetes Service pode aplicar balanceamento
de carga usando a propriedade labels para identificar os Pods que fazem parte da
mesma Deployment. Veremos como o objeto Service faz referência a esse rótulo em breve.

O campo replicas: define que esta implantação fornecerá apenas um Pod.

5. Ainda no arquivo deployment.yaml , podemos começar a definir as entradas para a
   configuração do container:

spec:
initContainers:
- name: topology-inventory-mysql-init
  image: busybox
  command: [ ‹sh›, ‹-c›, ‹until nc -zv
  topology-inventory-mysql.default.svc.clus
  ter.local 3306; do echo waiting
  for topology-inventory-mysql.de
  fault.svc.cluster.local; sleep 5;
  done;› ]
  containers:
- name: topology-inventory
  image: s4intlaurent/topology-
  inventory:latest
  envFrom:
- configMapRef:
  name: topology-inventory
  livenessProbe:
  httpGet:
  path: /q/health/ready
  port: 8080
  initialDelaySeconds: 30
  timeoutSeconds: 5
  periodSeconds: 3
  ports:
- containerPort: 8080

Vejamos as entradas que são usadas para a configuração do contêiner:

O campo initContainers : Ele é usado quando precisamos executar algumas tarefas ou esperar por
algo antes que o contêiner principal inicie. Aqui, estamos usando um contêiner init para esperar que
um banco de dados MySQL esteja disponível. O arquivo .yaml que carrega o banco de dados está
disponível no repositório GitHub deste livro para este capítulo.

Campo Contêineres : é aqui que definimos a configuração do contêiner que o Pod executa.

O campo de imagem : É aqui que informamos a localização da imagem da nossa aplicação. Pode
ser um registro público ou privado.

O campo configMapRef : é usado para injetar dados do ConfigMap no contêiner.

O campo livenessProbe : O Kubernetes pode enviar pacotes de sonda para verificar se o aplicativo
está ativo. É aqui que usaremos o mecanismo de verificação de integridade que configuramos anteriormente.

Campo containerPort : é aqui que informaremos a porta sobre o contêiner Docker exposto.

6. Por fim, vamos configurar o arquivo service.yaml:

apiVersion: v1
kind: Service
metadata:
name: topology-inventory
labels:
app: topology-inventory
spec:
type: NodePort
ports:
- port: 8080
  targetPort: 8080
  nodePort: 30080
  protocol: TCP
  selector:
  app: topology-inventory

O Kubernetes fornece três tipos diferentes de serviço: ClusterIP para comunicação interna
e NodePort e LoadBalance para comunicação externa. Estamos usando o NodePort para
acessar o aplicativo de fora do cluster do Kubernetes. Vamos dar uma olhada nos campos mais importantes:

O campo port : Este campo declara a porta de serviço que está disponível internamente para outros
Pods no cluster Kubernetes

O campo targetPort : Este campo especifica a porta que o contêiner está expondo

O campo nodePort : Este campo especifica a porta externa, que permite que clientes
externos acessem o aplicativo

Não é uma tarefa trivial preparar um aplicativo para ser implantado em um cluster Kubernetes.
Nesta seção, aprendemos sobre os principais objetos do Kubernetes. Entender esses objetos é
essencial porque eles são os blocos de construção para qualquer aplicativo em execução em um cluster Kubernetes.

Com todos os objetos Kubernetes necessários configurados adequadamente, podemos implantar o sistema hexagonal
em um cluster do Kubernetes.

======================================================================
Implantando no minikube

minikube é um cluster Kubernetes que foi feito para propósitos de desenvolvimento. Ele nos
permite criar e destruir clusters com facilidade. Devido à sua simplicidade, usaremos o minikube
para implantar nosso sistema hexagonal seguindo estas etapas (recomendo seguir as instruções em
https://minikube.sigs.k8s.io/docs/start/ para instalar o minikube na sua máquina):

1. Depois de instalar o minikube, você pode iniciar seu cluster emitindo o seguinte comando:

$ minikube start
:) minikube v1.4.0 on Fedora 30
Creating virtualbox VM (CPUs=2, Memory=2000MB, Disk=20000MB)
...
Preparing Kubernetes v1.16.0 on Docker 18.09.9 ...
Pulling images ...
Launching Kubernetes ...
Waiting for: apiserver proxy etcd scheduler controller dns
Done! kubectl is now configured to use "minikube"

A configuração de cluster padrão consome 2 CPUs, 2 GB de RAM e 20 GB de espaço em disco.

2. Para confirmar se seu cluster está ativo, execute o seguinte comando:

$ kubectl get nodes
NAME STATUS ROLES AGE VERSION
minikube Ready master 5m v1.16.0

Legal! Agora, podemos implantar a topologia e o sistema de inventário em nosso cluster Kubernetes local.

3. O processo de implantação é bastante simples. Tudo o que precisamos fazer é aplicar os arquivos YAML do Kubernetes
   criamos na seção anterior:

$ kubectl apply -f k8s/
configmap/topology-inventory created
deployment.apps/topology-inventory-mysql created
service/topology-inventory-mysql created
deployment.apps/topology-inventory created
secret/topology-inventory created
service/topology-inventory created

4. Então, podemos executar o seguinte comando para verificar se o sistema de topologia e inventário está ativo e
   funcionando:

$ kubectl get pods
NAME READY STATUS RES
TARTS AGE
topology-inventory-76f4986846-zq5t8 1/1 Running 0 73s
topology-inventory-mysql-dc9dbfc4b-7sct6 1/1 Running 0 73s

5. Para acessar o aplicativo, precisamos usar o IP do cluster minikube. Você pode usar o seguinte código para recuperar
   esse IP em um sistema operacional baseado em Unix:

$ minikube ssh "ip addr show eth0" | grep "inet\b" | awk '{print
$2}' | cut -d/ -f1
192.168.49.2

PowerShell:
ipconfig | Select-String -Pattern "IPv4.*" | ForEach-Object { ($_ -split ':')[1].Trim() }

6. Com esse IP, podemos consultar o ponto de extremidade de verificação de integridade para ver se a topologia e o inventário
   o sistema está vivo:

$ curl -s http://192.168.49.2:30080/q/health/ready | jq
{
"status": "UP",
"checks": [
{
"name": "Reactive MySQL connections health
check",
"status": "UP",
"data": {
"<default>": "UP"
}
}
]
}


Isso mostra que tanto o aplicativo quanto sua conexão com o banco de dados estão saudáveis.

Você também pode acessar a URL da interface do usuário do Swagger em http://192.168.49.2:30080/q/swagger-ui

Observe que estamos usando a porta 30080 para acessar a URL da interface do usuário do Swagger no
minikube. 30080 é a porta do nó do Kubernetes que configuramos para habilitar o acesso externo ao aplicativo.

Com isso, concluímos os passos fundamentais para transformar o sistema hexagonal em um cloud-native .
Nossa aplicação está pronta para ser implantada em um cluster minikube local e em qualquer provedor de
nuvem que ofereça clusters Kubernetes.

========================
Resumo

Começamos este capítulo aprendendo sobre os blocos de construção que podemos usar para criar uma
imagem Docker para o sistema hexagonal. Então, criamos dois tipos de imagem Docker. O primeiro foi
baseado no arquivo uber .jar , que é usado para empacotar e executar o aplicativo, enquanto o segundo foi
baseado em executáveis nativos, onde poderíamos aproveitar os recursos que o Quarkus fornece para criar
um artefato executável nativo .

Então, criamos os objetos Kubernetes que eram necessários para implantar o sistema hexagonal em
um cluster Kubernetes. Finalmente, implantamos o sistema hexagonal em um cluster minikube local.
Mais do que um sistema hexagonal, agora temos um sistema hexagonal nativo da nuvem pronto para aproveitar as vantagens
fornecidos por ambientes de nuvem.

No próximo capítulo, aprenderemos como a arquitetura hexagonal se relaciona com a arquitetura em camadas,
um estilo arquitetônico usado por muitos aplicativos. Entender as diferenças entre essas duas arquiteturas nos
ajuda a avaliar qual arquitetura pode ser melhor para empregar ao iniciar ou refatorar um projeto de software.

===============================================
=======================
=================================================
==============================================================
Usando princípios SOLID com Arquitetura Hexagonal

A ideia de ter um conjunto de princípios para nos ajudar a desenvolver um software melhor me diverte. Ao
longo dos anos, os programadores enfrentaram muitos problemas; alguns aconteceram com tanta frequência
que padrões para resolver tais questões surgiram, dando origem aos chamados padrões de design. Esses
padrões foram empregados para resolver problemas específicos de desenvolvimento de software.
Complementando padrões de design que atuam mais em problemas de codificação recorrentes e específicos,
surgiram ideias para lidar com problemas de manutenibilidade em projetos de software. Um conjunto notável
e influente dessas ideias foi sintetizado no que é conhecido como princípios SOLID.

Este capítulo explorará os princípios SOLID e como podemos aproveitá-los ao usar a arquitetura hexagonal.
Começaremos revisando cada princípio e, em seguida, prosseguiremos para ver como eles podem ser
aplicados no contexto de um sistema hexagonal. Por fim, discutiremos como padrões de design, como
builder e abstract factory, podem ser usados com a arquitetura hexagonal.

Os seguintes tópicos serão abordados neste capítulo:

• Compreendendo os princípios SOLID
• Aplicação do SOLID em um sistema de arquitetura hexagonal
• Explorando outros padrões de design

Após concluir este capítulo, você será capaz de empregar princípios SOLID em conjunto com técnicas de
arquitetura hexagonal. Além disso, você saberá como usar padrões de design, como chain of responsibility,
decorator, builder e singleton, ao desenvolver um sistema hexagonal.

============================================
Compreendendo os princípios SOLID

Desde o advento da programação, os desenvolvedores têm discutido ideias e capturado princípios para ajudar a
desenvolver um software melhor. Esses princípios surgiram como uma resposta para ajudar a lidar com código altamente complexo.
Depois de sofrer várias vezes com os mesmos problemas recorrentes, os desenvolvedores começaram a
reconhecer os padrões desses problemas e criaram técnicas para evitar tais problemas. Um exemplo notável
é o livro Gang of Four (GoF) sobre padrões de design, que causou um tremendo impacto no mundo orientado
a objetos e continua a influenciar gerações de desenvolvedores até hoje. Outro exemplo notável e influente
são as ideias formuladas por Robert Martin que levaram aos princípios SOLID.

SOLID representa os seguintes princípios:

• Princípio da Responsabilidade Única (SRP)
• Princípio Aberto Fechado (OCP)
• Princípio da Substituição de Liskov (LSP)
• Princípio de Segregação de Interface (ISP)
• Princípio da Inversão de Dependência (DIP)

Esses princípios visam ajudar os desenvolvedores a criar software robusto e fácil de alterar por meio de
código, com base em um conjunto de regras definidas por esses princípios. Acredito que usar esses
princípios não garante totalmente que o software esteja livre de problemas de manutenibilidade. No entanto,
esses princípios podem melhorar significativamente a qualidade geral do código. Em essência, trata-se de
empregar técnicas que permitam a introdução de alterações em uma base de código de forma sustentável.
Quero dizer, o software crescerá, mas sua complexidade será mantida sob controle.

Os princípios SOLID funcionam de forma semelhante à arquitetura hexagonal porque ambos
visam fornecer técnicas para desenvolver software mais sustentável e tolerante a mudanças. Portanto, faz sentido
explorar como esses princípios podem ser aplicados no contexto de uma aplicação hexagonal. Vamos
começar nossa exploração revisando cada um dos princípios SOLID.

==========================
Princípio da Responsabilidade Única (SRP)

Não é difícil para mim lembrar de uma situação em que testemunhei ou fui o autor de uma alteração de código
que causou efeitos colaterais, descobertos somente depois que um aplicativo foi implantado em um ambiente de teste ou, pior ainda,
em um ambiente de produção. Um stakeholder relataria problemas no aplicativo que começaram a ocorrer
logo após a mudança que causou o efeito colateral ser implantada. Então, embora a mudança tenha
resolvido o problema para um stakeholder, ela criou um problema para outro. Por quê? Porque o responsável pela mudança de problema
violou o SRP. A violação ocorreu porque a mesma lógica do sistema serviu a dois stakeholders
diferentes. A mudança resolveu o problema para um stakeholder, mas criou um efeito colateral que causou
problemas para o outro.

Uma violação de SRP também pode ocorrer quando definimos abstrações muito cedo. Suponha que definimos
uma classe abstrata com certos dados e comportamentos que acreditamos que serão comuns a todas as
implementações futuras dessa classe abstrata. Então, mais tarde, descobrimos, por meio de um infeliz
relatório de incidente, que alguns dados ou comportamentos dessa classe abstrata causam resultados
inesperados em uma implementação recente fornecida por outro desenvolvedor, que assumiu que os
comportamentos e dados fornecidos por essa abstração funcionariam na implementação que causa o problema.

O SRP garante que um método ou função seja alterado com base em solicitações de apenas um tipo de
stakeholder ou ator, geralmente um departamento ou uma linha de negócios em uma organização. É
importante garantir que a lógica do departamento A, por exemplo, não bagunce a lógica do departamento B,
o que pode ser feito organizando o código de forma que a lógica para atender a diferentes stakeholders seja
adequadamente separada.

======================================
Princípio Aberto-Fechado (OCP)

A ideia por trás desse princípio está em aumentar o que o software pode fazer sem alterar as coisas existentes
nele. Para fazer isso, um componente ou módulo de software deve estar aberto para extensão, mas fechado
para modificação. Posso me lembrar de uma experiência em que estava implementando um recurso de relatório.
Em vez de ter uma classe para lidar com todos os tipos de relatórios, criei uma classe abstrata base com
atributos básicos comuns a relatórios. Toda vez que um novo tipo de relatório tivesse que ser implementado,
uma nova classe concreta seria criada implementando a classe abstrata base. Atributos e funções adicionais
seriam anexados aos atributos básicos da classe abstrata base.

Usamos o OCP para evitar aquelas situações em que queremos adicionar um novo recurso e, para isso,
também precisamos alterar alguma parte da lógica que já suporta um recurso existente. Ao fazer isso,
violamos o OCP. Em vez disso, precisamos organizar o código para que possamos adicionar novos recursos
sem modificar o código que já atende aos recursos existentes.

========================================
Princípio da Substituição de Liskov (LSP)

Com base no exemplo de relatório que dei na descrição do OCP, vamos supor que temos um Relatório
classe contendo a declaração do método print . De acordo com um determinado domínio de problema, o método print
método é um comportamento suportado por qualquer relatório. Além da classe Report , imagine
que temos as classes WorkdayReport e WeekendReport estendendo-a. O LSP prescreve que se
passarmos objetos do tipo WorkdayReport ou WeekendReport para um método que espera um tipo
Report , esse método será capaz de disparar os comportamentos inerentes a todos os tipos de relatórios – neste caso
o método print.  O ponto principal é que o tipo Report deve ser projetado de modo que seus métodos
declarados, quando substituídos por subtipos, sejam coerentes com o propósito do subtipo.

============================
Princípio de Segregação de Interface (ISP)

O ISP é útil sempre que queremos fornecer aos clientes uma interface contendo apenas as declarações
de método de que eles precisam. Esse princípio é geralmente empregado quando temos uma única
interface com muitas declarações de método, e um determinado cliente implementa apenas alguns
métodos e fornece implementações fictícias para aqueles de que não precisa. Ao empregar o ISP,
quebramos essa única interface com múltiplas interfaces adaptadas para necessidades específicas do cliente.

======================================
Princípio da Inversão de Dependência (DIP)

Componentes de software estáveis e instáveis têm conceitos distintos. Estável significa aqueles componentes que não
mudam com muita frequência, enquanto instável é o oposto. Ter um componente cliente dependendo diretamente de
um componente instável pode ser arriscado porque mudanças no código instável podem desencadear mudanças no
cliente. Na maioria das vezes, o componente instável é uma classe concreta com detalhes de implementação que não
precisam ser expostos aos seus clientes.

Para evitar expor tais detalhes de implementação e proteger o cliente de mudanças de dependência, o DIP prescreve
que os clientes devem sempre depender de abstrações em vez de concreções. O componente instável – uma classe
concreta com detalhes de implementação – deve derivar de uma abstração implementando uma interface, por exemplo.
Então, o cliente deve confiar em um componente estável, uma interface implementada pelo componente instável (uma
classe concreta). Chamamos uma interface de componente estável porque ela atua como um contrato, e contratos são
menos suscetíveis a mudanças.

amos ver na próxima seção como podemos aplicar os princípios SOLID a uma aplicação desenvolvida usando
arquitetura hexagonal.

===============================
Aplicando SOLID em um sistema de arquitetura hexagonal

Para ver como cada princípio SOLID é aplicado, voltaremos à topologia e ao sistema de inventário que desenvolvemos
ao longo do livro. Vamos começar vendo como o SRP pode ser aplicado na topologia e no sistema de inventário.

===========================================
Aplicando o SRP

Só para recapitular, o sistema de topologia e inventário gerencia recursos de rede, como roteadores e switches. Esse
sistema é adequado para empresas de telecomunicações ou Provedores de Serviços de Internet (ISP) que desejam
manter um inventário dos recursos de rede que usam para atender seus clientes.

No sistema de topologia e inventário, temos roteadores core e edge. Roteadores core lidam com tráfego de
rede de alta carga de um ou mais roteadores edge. Roteadores edge são usados para lidar com tráfego de usuários finais.
Roteadores de borda se conectam a switches de rede.

Considere um cenário em que roteadores core e edge mudam de local. Por exemplo, um roteador
core que agora está localizado na França precisa, por algum motivo, ser reprovisionado na Itália, e um roteador edge
que está em Frankfurt precisa ser reprovisionado em Berlim. Considere também que as mudanças de
rede entre países são tratadas pelo ator A, e as mudanças de rede entre cidades são tratadas pelo ator B.

Vamos mudar a topologia e o aplicativo de inventário para cumprir o requisito descrito. As mudanças
descritas a seguir são feitas no hexágono Domain:

1. Crie a classe de especificação AllowedCountrySpec :

public final class AllowedCountrySpec extends Ab
stractSpecification<Location> {
private List<String> allowedCountries =
List.of(
"Germany", "France", "Italy", "United States");
@Override
public boolean isSatisfiedBy(Location location) {
return allowedCountries
.stream()
.anyMatch(
allowedCountry -> allowedCountry
.equals(location.country()));
}
/** Code omitted **/
}

Esta especificação limita quais países podem ser escolhidos por meio do atributo allowedCountries .
Não é assim que você deve representá-lo em uma aplicação real, mas é o suficiente para ilustrar a
ideia do SRP.

2. Agora, crie a classe de especificação AllowedCitySpec :

public final class AllowedCitySpec extends Ab
stractSpecification<Location> {
private List<String> allowedCities =
List.of(
"Berlin", "Paris", "Rome", "New York");
@Override
public oolean isSatisfiedBy(Location location) {
return allowedCities
.stream()
.anyMatch(
allowedCountry -> allowedCountry
.equals(location.city()));
}
/** Code omitted **/
}

Seguindo a mesma ideia da especificação anterior, aqui limitamos quais cidades
são permitidas através do atributo allowedCities .

3. Declare o método changeLocation na classe abstrata Router :

public abstract sealed class Router extends Equipment
permits CoreRouter, EdgeRouter {
/** Code omitted **/
public abstract void changeLocation(
Location location);
/** Code omitted **/
}

Observe que Router é uma classe abstrata selada, permitindo que apenas as classes CoreRouter
e EdgeRouter a implementem.

4. Forneça uma implementação para CoreRouter:

@Override
public void changeLocation(Location location) {
var allowedCountrySpec = new AllowedCountrySpec();
allowedCountrySpec.check(location);
this.location = location;
}

Usamos o AllowedCountrySpec para verificar se o novo local do roteador é permitido.
Se um país não permitido for fornecido, uma exceção será lançada. Caso contrário,
o novo local será atribuído à variável location do objeto Router .

5. Forneça uma implementação para EdgeRouter:

@Override
public void changeLocation(Location location) {
var allowedCountrySpec = new AllowedCountrySpec();
var allowedCitySpec = new AllowedCitySpec();
allowedCountrySpec.check(location);
allowedCitySpec.check(location);
this.location = location;
}

A implementação do EdgeRouter é um pouco diferente. Além de AllowedCountrySpec,
também temos AllowedCitySpec. Um novo Location será atribuído ao objeto Router
somente após o cumprimento dessas duas especificações.

Vamos rever o que fizemos aqui. Começamos criando o AllowedCountrySpec
e especificações AllowedCitySpec ; então, declaramos o método changeLocation na classe
abstrata Router . Como tanto CoreRouter quanto EdgeRouter implementam essa classe,
tivemos que substituir o método changeLocation para atender às necessidades do ator A
e do ator B. O ator A é responsável por manipular mudanças de localização entre países
– neste caso, CoreRouter. O ator B é responsável por manipular mudanças de localização
entre cidades, o que é responsabilidade do EdgeRouter'.

Suponha que, em vez de declarar changeLocation como abstrato, fornecemos um concreto
implementação compartilhada pelas classes CoreRouter e EdgeRouter. Isso
violaria o SRP porque a lógica changeLocation atenderia a diferentes atores.

====================================
Aplicando o OCP

Ainda não declaramos, mas o arranjo entre o roteador, o CoreRouter e o EdgeRouter
classes representa uma aplicação do OCP.

O OCP garante que um módulo ou componente seja fechado para alteração, mas aberto para extensão.
Em vez de fornecer um design de classe em que uma única classe conteria a lógica para lidar com
roteadores de núcleo e de borda, aproveitamos a capacidade de herança do Java para estender as possibilidades da classe abstrata Router, sem alterar seus atributos e comportamentos. Tais extensões são possíveis
por meio das implementações de classe concreta CoreRouter e EdgeRouter.

=====================================
Aplicando o LSP

Para demonstrar a aplicação do LSP, precisamos fazer mais mudanças na topologia e
no sistema de inventário. Ao aplicar o SRP e o OCP, mudamos o hexágono Domain.
Agora, faremos mudanças no hexágono Application:

1. Declare o método changeLocation na interface RouterManagementUseCase :

public interface RouterManagementUseCase {
/** Code omitted **/
Router changeLocation(
Router router, Location location);
/** Code omitted **/
}

Alterar a localização de um roteador é um novo caso de uso que adicionamos ao sistema de topologia e
inventário, então adicionamos a declaração do método changeLocation para expressar esse caso de uso.

2. Implemente o método changeLocation em RouterManagementInputPort:

public class RouterManagementInputPort implements
RouterManagementUseCase {
/** Code omitted **/
@Override
public Router changeLocation(Router router,
Location location) {
router.changeLocation(location);
return persistRouter(router);
}
/** Code omitted **/
}

O método changeLocation de RouterManagementInputPort chama changeLocation de
Router passando um objeto Location . changeLocation de Router tem uma lógica
que verifica se o Location fornecido é permitido. Se tudo estiver bem, chamamos
persitRouter para persistir Router com seu novo Location.

A aplicação LSP pode ser observada quando implementamos o método
changeLocation em RouterManagementInputPort. Note que changeLocation espera um tipo Router:

public Router changeLocation(Router router,
Location location) {
router.changeLocation(location);
return persistRouter(router);
}

Isso significa que podemos passar aqui um objeto CoreRouter ou EdgeRouter porque ambos estendem
Router e ambos fornecem uma implementação de changeLocation, um comportamento inerente a todos os roteadores.


