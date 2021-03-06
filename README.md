# LedgerMPC Toolkit

The MPC toolkit prototype for ledger interaction has been developed by the University of Salerno (UNISA) crypto group. 

The toolkit has been successfully tested with [MPyC](https://github.com/lschoe/) and [EMP-ag2PC libraries](https://github.com/emp-toolkit/emp-ag2pc). It can be currently run using Ehtereum, Hyperledger Fabric, and a local dummy ledger simulator useful for quick tests. In addition to the above two libraries,
we have successfully tested the toolkit using an implmentation of a coin-tossing protocol secure against dishonest majority. 
When using Ethereum one can exploit the possibility of quick protocol execution (without any block confirmation) as discussed in [https://eprint.iacr.org/2019/891.pdf].

## Description

LedgerMPC provides a generic mechanism for parties in a two-party or multi-party computation to use a ledger as a communications channel instead of point-to-point connections. This can be useful for providing an audit trace of the protocol execution and to enable multi-party computations among parties who are not guaranteed to be on-line simultaneously. It is also beneficial in terms of privacy since players do not need to establish direct communication channels among them. 

LedgerMPC is logically divided in two following two main components.

### Generic Proxy Component

This component provides a proxy between an MPC protocol library and a generic (abstract) ledger. Messages from the MPC library are automatically redirected to the generic ledger by the proxy, and when a new message is posted on the generic ledger from another party, the proxy forwards it to the MPC library.
The MPC library remains untouched, and thus the feature of redirecting the communication through
the ledger is transparent. The main requirement to make this possible consists of
properly setting the IP addresses and port numbers typically used when configuring the library with
classical TCP connections so that instead of connecting the parties directly, each party is
connected to the proxy and the proxy can capture the messages sent through such TCP channels.  The proxy encapsulates messages sent to each of these ports as ledger transactions and broadcasts them to the network by contacting the corresponding peers.

### The Ledger Component

The ledger component implements bridges between the generic ledger and real ledgers. Currently it includes a bridge to Hyperldeger Fabric, a bridge to Ethereum and a bridge to a dummy blockchain that can be used to speed up tests. The modular design used for the ledger component allows to easily add new bridges
to support other real ledgers.



## Installation

1. Clone this repository.

1. Use the pre-built file `mpctoolkit-1.0-SNAPSHOT.jar` located in `target/` or

1. Install [Maven](https://maven.apache.org/install.html#:~:text=The%20installation%20of%20Apache%20Maven,distribution%20archive%20in%20any%20directory)

1. Navigate to the project directory and install the project with the following command

```shell
mvn install
```

## Usage

To start using our proxy for a communication channel, go to `src/main/java` per the `proxy.java` file is located. To run the daemon, use the following command

```shell
java -cp <JAR BUILD FILE PATH> proxy.java <C/S> <SID> <LOCAL PARTY PID> <REMOTE PARTY PID> <PORT> <CONFIG INFO> <BLOCKCHAIN> <QUICKNESS>
```

This command must be run by the user for each remote party involved in the MPC execution (n-1 communication channels). 
In particular each command invocation opens a communication channel with a remote party. Note that our proxy works for any protocol (not only Secure MPC) involving multiple parties.
We remark the fact that in this version the user has to write the **absolute** path of the blockchain's configuration file. We describe the meaning of the described fields in the following:

* **Jar build file path**: the path of the `.jar` file created during the installation phase
* **C/S**: Indicates whether the machine is acting as a client or as a server. This configuration depends on the MPC library used and it's usually indicated in the configuration file or the command used for running the MPC library.
* **SID**: The sid used for the protocol instantiation
* **Local Party ID**: the party ID that the instance running on the user machine is using.
* **Remote Party ID**: the party ID of the remote party. The couple (local party, remote party) defines a communication channel between them.
* **Port**: The port of the localhost that the MPC library will use to deliver message to the remote party.
* **Config info**: Initial blockchain configuration. In our blockchain instantiations it is the path of a configuration file (more info in the blockchains section)
* **Blockchain**: The blockchain used. It can be instantiated with `ETH`, `HLF` or `DOF` (dummy blockchain).
* **Quickness**: 1 if the procotol should be run in quick mode, 0 otherwise. Parameter ignored in HLF and DOF.

## Config files

### Ethereum 

To facilitate Ethereum testing we included files `testnetconfig[2/3].json` containing wallet secret keys for three different wallets created in the ethereum ropsten testnet. To create a new config file include the following field in a json file:
* **walletSK**: The secret key of the local party's ethereum wallet
* **hostURL**: the URL of the Blockchain host. In our example, we used a pre-registered Infura host.
* **maxTxSize**: Maximum size available for a single transaction (not used in this version).
* **confirmationTime**: Blockchain confirmation time (e.g. 12 in Ethereum).
* **contractAddr**: The pre-deployed smart contract needed to collect MPC messages. We provided a [pre-deployed](https://ropsten.etherscan.io/address/0x4C50a188d772F1Fade9b2892A3070c9818037528) contract.
* **participants**: An array containing wallet addresses of the participants in ascending order (i.e. the wallet of player with ID 1 shall be in the first position, etc).

### Hyperledger Fabric 

As in Ethereum, we included files `configUser[2/3].txt` as an example. Since HLF is permissioned, wallet SK must be generated by the organizations running the HLF instance.
To create a new config file include the following field in a json file:

* **walletPath**: The path of the user's wallet in the HLF project.
* **userId**: The party ID.
* **channelName**: name of the channel used.
* **chaincodeName**: name of the chaincode used.
* **networkConfigPath**: path to the HLF configuration `.yaml` file.
* **parties**: for user 1, a JSON array containing the MSP ID and the user ID of each involved party; the parties have to
be listed in ascending order of user ID.

### Dummy Blockchain

We included the file `pgn.cfg` as an example. In this file is sufficient to write two rows with the following data
* Host IP
* Port used to communicate with the server

Coupled with the dummy blockchain, we provided a DemoServer that can be runned either by using the `DemoServer.jar`file provided in the repository, or by generating it by running

```shell
ant -f DemoServer.xml
```

that will generate a `DemoServer-${version}.${build.number}.jar` inside the directory `dist/lib/`. The server can be run with the following command

```shell
java -jar DemoServer.jar
```

## Usage example with MPyC

To test our proxy, we show how to run one of the MPyC demos with our toolkit for 3 parties

1. Clone [MPyC](https://github.com/lschoe/mpyc).
2. Navigate to the `demos/` directory and choose a demo file between the listed ones (we suggest `helloworld.py`or `onewayhashchains.py` to finish the test in a reasonable time).
3. Party 1 opens the terminal file and runs the following command:

```shell
python3 <DEMO FILE> -Plocalhost:1 -Plocalhost:12347 -Plocalhost:12349 -I0
```

4. Party 2 opens the terminal and runs the following command

```shell
python3 <DEMO FILE> -Plocalhost:12347 -Plocalhost:12345 -Plocalhost:12348 -I1
```

5. Party 3 opens the terminal and runs the following command

```shell
python3 <DEMO FILE> -Plocalhost:12349 -Plocalhost:12348 -Plocalhost:12346 -I2
```

Now that each party has started MPyC and configured the port to communicate with the proxy via TCP, each user can run our toolkit to forward the messages to the selected blockchain.

* Party 1,  runs the following commands two new shells

```shell
java -cp <BUILD JAR FILE> proxy.java C <SID> 1 2 12347 <testnetconfig.json ABOSLUTE PATH> <BLOCKCHAIN> <QUICKNESS>
java -cp <BUILD JAR FILE>  proxy.java C <SID> 1 3 12349 <testnetconfig.json ABOSLUTE PATH>  <BLOCKCHAIN> <QUICKNESS>
```
* Party 2,  runs the following commands two new shells


```shell
java -cp <BUILD JAR FILE> proxy.java S <SID> 2 1 12345 <testnetconfig2.json ABOSLUTE PATH>  <BLOCKCHAIN> <QUICKNESS>
java -cp <BUILD JAR FILE> proxy.java C <SID> 2 3 12348 <testnetconfig2.json ABOSLUTE PATH>  <BLOCKCHAIN> <QUICKNESS>
```
* Party 3,  runs the following commands two new shells

```shell
java -cp <BUILD JAR FILE> proxy.java S <SID> 3 1 12346 <testnetconfig3.json ABOSLUTE PATH>  <BLOCKCHAIN> <QUICKNESS>
java -cp <BUILD JAR FILE> proxy.java S <SID> 3 2 12346 <testnetconfig3.json ABOSLUTE PATH>  <BLOCKCHAIN> <QUICKNESS>
```


### Coin Tossing Protocol usage example

Navigate into the repository folder, and

* Player 1 runs the following command
```shell
java CoinTossing 1 1 12347 12349
```
* Players 2 runs the following command

```shell
java CoinTossing 2 12347 12345 12348
```

* Player 3 runs the following command

```shell
java CoinTossing 3 12349 12348 12346
```
Now that each party has started the coin tossing protocol, each user opens two new shells and 

* Party 1,  runs the following commands two new shells

```shell
java  proxy.java C <SID> 1 2 12347 ./pgn.cfg <BLOCKCHAIN> <QUICKNESS>
java  proxy.java C <SID> 1 3 12349 ./pgn.cfg <BLOCKCHAIN> <QUICKNESS>
```
* Party 2,  runs the following commands two new shells


```shell
java proxy.java S <SID> 2 1 12345 ./pgn.cfg <BLOCKCHAIN> <QUICKNESS>
java proxy.java C <SID> 2 3 12348 ./pgn.cfg <BLOCKCHAIN> <QUICKNESS>
```
* Party 3,  runs the following commands two new shells

```shell
java proxy.java S <SID> 3 1 12346 ./pgn.cfg <BLOCKCHAIN> <QUICKNESS>
java proxy.java S <SID> 3 2 12346 ./pgn.cfg <BLOCKCHAIN> <QUICKNESS>
```

Note that in the case of our Coin Tossing protocol, the classpath is not needed. Moreover, relative paths are also allowed.


Copyright &copy; 2021 Gennaro Avitabile, Vincenzo Botta, Daniele Friolo, Vincenzo Iovino, Ivan Visconti

