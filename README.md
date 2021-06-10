# ledgerMPC 
MPC toolkit for ledger interaction, successfully tested with MPyC and EMP-ag2PC libraries. It can be run with Ehtereum, Hyperledge Fabric, and with a local artificially created ledger aimed for testing.


## Installation

1. Clone this repository.

1. Go to the `target/` folder where the file mpctoolkit-1.0-SNAPSHOT.jar is located. Or

1. Install [Maven](https://maven.apache.org/install.html#:~:text=The%20installation%20of%20Apache%20Maven,distribution%20archive%20in%20any%20directory)

1. Go to the main dictory and install the project with

```shell
mvn install
```

## Usage

To start using our proxy, go to `src\main\java` per the `proxy.java` file is located. To run the daemon, use the following command

```shell
#java -cp <JAR BUILD FILE PATH> proxy.java <C/S> <SID> <LOCAL PARTY PID> <REMOTE PARTY PID> <PORT> <CONFIG INFO> <BLOCKCHAIN>
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
