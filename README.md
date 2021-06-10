# ledgerMPC 
MPC toolkit prototype for ledger interaction, developer by UNISA crypto group,
It is successfully tested with MPyC and EMP-ag2PC libraries. It can be run with Ehtereum, Hyperledger Fabric, and with a local artificially created ledger aimed for testing.


## Installation

1. Clone this repository.

1. Use the pre-built file `mpctoolkit-1.0-SNAPSHOT.jar` located in `target/` or.

1. Install [Maven](https://maven.apache.org/install.html#:~:text=The%20installation%20of%20Apache%20Maven,distribution%20archive%20in%20any%20directory)

1. Go to the main dictory and install the project with

```shell
mvn install
```

## Usage

To start using our proxy, go to `src/main/java` per the `proxy.java` file is located. To run the daemon, use the following command

```shell
#java -cp <JAR BUILD FILE PATH> proxy.java <C/S> <SID> <LOCAL PARTY PID> <REMOTE PARTY PID> <PORT> <CONFIG INFO> <BLOCKCHAIN> <QUICKNESS>
```

This command must be run the user for each recipient in the MPC. In particular each command invokation opens a ccommunication channel with a remote party.
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
* **participants**: An array containing wallet addresses of the participants in order (i.e. the wallet of player with ID 1 shall be in the first position, etc).


