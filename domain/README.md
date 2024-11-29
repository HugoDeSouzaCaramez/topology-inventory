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






