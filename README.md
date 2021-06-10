# Ledger MPC Toolkit
MPC toolkit prototype for ledger interaction, developer by UNISA crypto group.
It is successfully tested with MPyC and EMP-ag2PC libraries. It can be run with Ehtereum, Hyperledger Fabric, and with a local artificially created ledger aimed for testing.


## Installation

1. Clone this repository.

1. Use the pre-built file `mpctoolkit-1.0-SNAPSHOT.jar` located in `target/` or

1. Install [Maven](https://maven.apache.org/install.html#:~:text=The%20installation%20of%20Apache%20Maven,distribution%20archive%20in%20any%20directory)

1. Go to the main dictory and install the project with the following command

```shell
mvn install
```

## Usage

To start using our proxy for a communication channel, go to `src/main/java` per the `proxy.java` file is located. To run the daemon, use the following command

```shell
java -cp <JAR BUILD FILE PATH> proxy.java <C/S> <SID> <LOCAL PARTY PID> <REMOTE PARTY PID> <PORT> <CONFIG INFO> <BLOCKCHAIN> <QUICKNESS>
```

This command must be run by the user for each remote party involved in the MPC execution (n-1 communication channels). 
In particular each command invokation opens a communication channel with a remote party. Note that our proxy works for any protocol (not only Secure MPC) involving multiple parties.
We remark the fact that in this version the user has to write the **absolute** path of the blockchain's configuration file. We describe the meaning of the described fields in the following:

* **Jar build file path**: the path of the `.jar` file created during the installation phase
* **C/S**: Indicates whether the machine is acting as a client or as a server. This configuration depends on the MPC library used and it's usually indicated in the configuration file or the command used for running the MPC library.
* **SID**: The sid used for the protocol instantiation
* **Local Party ID**: the party ID that the instance running on the user machine is using.
* **Remote Party ID**: the party ID of the remote party. The couple (local party, remote party) defines a communication channel between them.
* **Port**: The port of the localhost that the MPC library will use to deliver message to the remote party.
* **Config info**: Initial blockchain configuration. In our blockchain instantiations it is the path of a configuration file (more info in the blockchains section)
* **Blockchain**: The blockchain used. It can be instantiated with `ETH`, `HLF` or `DOF` (local blockchain).
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
java -cp <BUILD JAR FILE> proxy.java C <PID> 1 2 12347 /Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig.json <BLOCKCHAIN>
java -cp <BUILD JAR FILE>  proxy.java C <PID> 1 3 12349 /Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig.json <BLOCKCHAIN>
```
* Party 2,  runs the following commands two new shells


```shell
java -cp <BUILD JAR FILE> proxy.java S <PID> 2 1 12345 /Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig2.json <BLOCKCHAIN>
java -cp <BUILD JAR FILE> proxy.java C <PID> 2 3 12348 /Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig2.json <BLOCKCHAIN>
```
* Party 3,  runs the following commands two new shells

```shell
java -cp <BUILD JAR FILE> proxy.java S <PID> 3 1 12346 /Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig3.json <BLOCKCHAIN>
java -cp <BUILD JAR FILE> proxy.java S <PID> 3 2 12346 /Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig3.json <BLOCKCHAIN>
```


