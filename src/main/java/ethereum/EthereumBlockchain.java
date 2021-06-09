package ethereum;
import Blockchain.Blockchain;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.ChainIdLong;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

public class EthereumBlockchain extends Blockchain<String> {
    EthereumConfig config;
    Logger logger;
    Web3j web3;
    EthGasPrice gasPrice;
    TransactionManager transactionManager;
    ArrayList<String> sids;
    InternalMessageHandler messageHandler;
    HashMap<String,Integer> lastConfirmedMessage;
    private boolean participantsChecked = false;
    private String sessionID;
    private int sendingPartyID;
    private int receivingPartyID;
    private MPChannels_sol_MPChannels contract;
    private final static BigInteger GAS_LIMIT = BigInteger.valueOf(3000000);
    private final static int BLOCK_TIME = 500; //16000; Ethereum 15 seconds
    private HashMap<Integer,String> queue = new HashMap<>();
    private int msgCounter = 0;

    @Override
    public void setUp(String configFilePath, byte[] sid, Integer sender, Integer receiver) throws Exception {
        logger = Logger.getLogger(EthereumBlockchain.class.getName());
        this.sessionID = Numeric.toHexString(sid);
        this.sendingPartyID = sender;
        this.receivingPartyID = receiver;

        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(configFilePath);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        JSONArray p =  ((JSONArray) jsonObject.get("participants"));
        ArrayList<String> participants = new ArrayList<>();
        for (Object jsonObj : p) {
            participants.add(((String) jsonObj).toLowerCase());
        }
       /* String[] participants = new String[p.length];
        for (int i = 0; i < p.length; i++)
            participants[i] = (String) p[i];*/
        config = new EthereumConfig((String) jsonObject.get("walletSK"),
                (String) jsonObject.get("hostURL"), (long) jsonObject.get("maxTxSize"),(long) jsonObject.get("confirmationTime"),
                (String) jsonObject.get("contractAddr"),sender,receiver,participants);

        //da togliere
       /* ArrayList<String> addr = new ArrayList<>();
        addr.add("0xBD7DCE37dFdBD45791f69C0887e948CFE2589566");
        addr.add("0xCfAAA683818f02aB797F17a72e08aA4356951EBf");
        addr.add("0x6f855363B60B17689893084421C7535753AEE10f");
        addr.add("0xd2A4b2d08e2aC61d8541Ac6317f88f2d53C78ee9");
        addr.add("0x284c485474D11281A2bB668F569a9Ab2f7238190");
         config = new EthereumConfig("0xBD7DCE37dFdBD45791f69C0887e948CFE2589566","0xb1deaac56078e4a5e0eb117ef03cc3c53d17997897c23bb8dc927827687342dd",
                "http://localhost:7545",1000,12,"0x213eB72C734fbE6355936d6f3c49E389758cb412",addr,1);
*/
        messageHandler = new InternalMessageHandler();
        messageHandler.setUp("",config.getConfirmationTime());
        web3 = Web3j.build(new HttpService(config.getHostURL()));
        try {
            // web3_clientVersion returns the current client version.
            Web3ClientVersion clientVersion = web3.web3ClientVersion().send();

            EthBlockNumber blockNumber = web3.ethBlockNumber().send();

            //eth_gasPrice, returns the current price per gas in wei.
            gasPrice =  web3.ethGasPrice().send();

            System.out.println("Ethereum Connection to "+config.getHostURL()+" successful");
            System.out.println("Client version: "+clientVersion.getWeb3ClientVersion());
            System.out.println("Gas price: "+gasPrice.getGasPrice());
            System.out.println("Most recent block: "+blockNumber.getBlockNumber());

            //transactionManager = new RawTransactionManager(
                  //  web3, config.getCredentials());

            TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
                    web3,
                    TransactionManager.DEFAULT_POLLING_FREQUENCY,
                    10);
            transactionManager = new RawTransactionManager(web3,config.getCredentials(), ChainIdLong.NONE,receiptProcessor);
           // System.out.println("First address: "+config.getCredentials().getAddress());
            loadContract();
            if (sendingPartyID == 1 && config.getCredentials().getAddress().equals(config.getParticipants().get(0)) && !participantsChecked) loadAccounts();
            else if (!participantsChecked && sendingPartyID != 1) checkParticipants();
            //startWriteTimer();

        } catch(IOException ex) {
            throw new RuntimeException("Error whilst sending json-rpc requests", ex);
        }
    }

    //spostare sul proxy
  /*  public void startWriteTimer() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        String bigMessage = "0x";
                        ArrayList<Integer> toBeRemoved = new ArrayList<>();

                            for (int msgNum : queue.keySet()) {
                                bigMessage = bigMessage + queue.get(msgNum);
                                toBeRemoved.add(msgNum);
                            }
                                try {

                                   if (!bigMessage.equals("0x")) {
                                       msgCounter++;
                                       writeMessage(bigMessage, msgCounter,false);
                                       System.out.println("Writing message of length "+bigMessage.length());
                                   }
                                }
                                catch (Exception ex) { ex.printStackTrace(); }

                            for (Integer msgNum : toBeRemoved)
                                queue.remove(msgNum);
                            startWriteTimer();
                    }
                },
                2000
        );
    }*/
    public void checkParticipants() throws Exception {
        System.out.println("Checking registered parties");
        BigInteger block = contract.getParticipantsBlock(sessionID).send();
        while (block.intValue() == 0) {
            Thread.sleep(BLOCK_TIME);
            System.out.println("Waiting that player with ID 1 sets the participants... actual block: "+block);
            block = contract.getParticipantsBlock(sessionID).send();
        }
        waitConfirmation(block);
    }
    public void loadAccounts() throws  Exception {
        if (!contract.getParticipantsBlock(sessionID).send().equals(BigInteger.valueOf(0))) return;
        System.out.println("LoadAccounts triggered!");
        BigInteger block = contract.setParticipants(sessionID,config.getParticipants()).send().getBlockNumber();
        System.out.println("Transaction saved at block: "+block);
    }
    public void waitConfirmation(BigInteger block) throws  Exception {
        BigInteger currentBlock = web3.ethBlockNumber().send().getBlockNumber();
        //System.out.println("Most recent block: "+currentBlock);

        System.out.println("Waiting for confirmation...");
        BigInteger waitingTime = BigInteger.valueOf(config.getConfirmationTime()).subtract(currentBlock.subtract(block));
        System.out.println(waitingTime.intValue() <= 0 ? "Confirmed" : "Waiting time: "+waitingTime+ " blocks");
        System.out.println("Waiting for confirmation");
        while (currentBlock.subtract(block).intValue() < config.getConfirmationTime()) {
            Thread.sleep(BLOCK_TIME);
            currentBlock = web3.ethBlockNumber().send().getBlockNumber();
            System.out.println("to confirm: "+block+", current "+currentBlock);
        }
    }
    @Override
    public void shutDown() {
        web3.shutdown();
    }

    @Override
    public byte[] readData(int msgNum, Boolean quickness) throws Exception {
        return quickness ? readDataQuick(msgNum,false) : readDataNonQuick(msgNum,false);
    }

    public boolean isConsistent() throws  Exception {
       // Set<Integer> pids = messageHandler.encounteredPids(receivingPartyID);
            Set<BucketMessage> unconfirmed = messageHandler.getUnconfirmedMessages(receivingPartyID);
            for (BucketMessage temp : unconfirmed) {
                Tuple2<byte[], BigInteger> result = contract.readMessage(sessionID, BigInteger.valueOf(sendingPartyID), BigInteger.valueOf(receivingPartyID),
                        BigInteger.valueOf(temp.getMsgNum())).send();
                if (!result.component1().equals(temp.getMsg())) return false;
                if (!result.component2().equals(temp.getBlock()))
                    messageHandler.storeMessage(result.component1(), temp.getMsgNum(), receivingPartyID, temp.getBlock()); //restore with the new block
            }

        return true;
    }
    /*public void checkResend() throws  Exception {
        Set<BucketMessage> unconfirmed = messageHandler.getUnconfirmedMessages(sendingPartyID);
        for (BucketMessage temp : unconfirmed) {
            Tuple2<byte[],BigInteger>  result = contract.readMessage(sessionID,BigInteger.valueOf(receivingPartyID),BigInteger.valueOf(sendingPartyID),
                    BigInteger.valueOf(temp.getMsgNum())).send();
            if (result.component1().equals("")) writeData(Numeric.hexStringToByteArray(temp.getMsg()),temp.getMsgNum());
            if (!result.component2().equals(temp.getBlock())) messageHandler.storeMessage(temp.getMsg(),temp.getMsgNum(),sendingPartyID,temp.getBlock());
        }
    }*/


    public byte[] readDataQuick(int msgNum, boolean broadcast) throws Exception {
        int rec;
        if (!isConsistent()) throw new RuntimeException("Quickness: Inconsistent transcript");
        if (broadcast) rec = 0;
        else rec = receivingPartyID;
        Tuple2<byte[],BigInteger>  result = contract.readMessage(sessionID,BigInteger.valueOf(sendingPartyID),BigInteger.valueOf(rec),
                BigInteger.valueOf(msgNum)).send();
        //Polling
        if (result.component1().length == 0) System.out.println("Waiting for messagge "+msgNum+ " to appear...");
        while (result.component1().length == 0) {
            Thread.sleep(BLOCK_TIME); //Time for a block to appear
            result = contract.readMessage(sessionID,BigInteger.valueOf(sendingPartyID),BigInteger.valueOf(rec),
                    BigInteger.valueOf(msgNum)).send();
        }
        System.out.println("Message: "+Arrays.toString(result.component1()));
        BigInteger lastBlock = web3.ethBlockNumber().send().getBlockNumber();
        messageHandler.setActualBlock(lastBlock);
        messageHandler.storeMessage(result.component1(),msgNum,rec,result.component2());
        System.out.println("Message stored: "+ messageHandler.retrieveMessage(msgNum,rec));
        return result.component1();
    }
    public byte[] readDataNonQuick(int msgNum, boolean broadcast) throws Exception {
        int rec;
        if (broadcast) rec = 0;
        else rec = receivingPartyID;
        Tuple2<byte[],BigInteger>  result = contract.readMessage(sessionID,BigInteger.valueOf(sendingPartyID),BigInteger.valueOf(rec),
                BigInteger.valueOf(msgNum)).send();
        //Polling
        if (result.component1().length == 0) System.out.println("Waiting for messagge "+msgNum+ " to appear...");
        while (result.component1().length == 0) {
            Thread.sleep(BLOCK_TIME); //Time for a block to appear
            result = contract.readMessage(sessionID,BigInteger.valueOf(sendingPartyID),BigInteger.valueOf(rec),
                    BigInteger.valueOf(msgNum)).send();
        }
        System.out.println("Message: "+result.component1());
           waitConfirmation(result.component2());
            result = contract.readMessage(sessionID, BigInteger.valueOf(sendingPartyID), BigInteger.valueOf(rec),
                    BigInteger.valueOf(msgNum)).send();
           // Message m = new Message(sessionID, sendingPartyID, receivingPartyID, msgNum, Numeric.hexStringToByteArray(result.component1()));
            messageHandler.storeMessage(result.component1(),msgNum,rec,result.component2());
            System.out.println("Message stored: "+ Arrays.toString(messageHandler.retrieveMessage(msgNum,rec)));
            return result.component1();
    }
    public void writeMessage(byte[] msg, int msgNum, boolean broadcast) throws Exception {
        int rec;
        if (broadcast) rec = 0;
        else rec = receivingPartyID;
        try {
          TransactionReceipt transactionReceipt = contract.sendMessage(
            sessionID, msg, BigInteger.valueOf(rec),
            BigInteger.valueOf(sendingPartyID), BigInteger.valueOf(msgNum)).send();
            }
            catch (RuntimeException ex) {
                System.out.println("Exception encountered: "+ex.getMessage());
             System.out.println("Re-send attempt");
              writeMessage(msg, msgNum, broadcast);
             }
             catch (TransactionException ex) {
              System.out.println("Exception encountered: "+ex.getMessage());
                System.out.println("Re-send attempt");
                writeMessage(msg, msgNum, broadcast);
         }
    }
    @Override
    public void writeData(byte[] msg, int msgNum) throws Exception {
        writeMessage(msg,msgNum,false);
        //queue.put(msgNum,Numeric.toHexString(msg).substring(2));
       //if (transactionReceipt.isStatusOK()) System.out.println("Receipt: "+transactionReceipt);
       //else throw new RuntimeException(transactionReceipt.getStatus());
    }
    public void loadContract() throws Exception {
        System.out.println("Going to load smart contract");
       // contract = MPChannels_sol_MPChannels.load(config.getSmartContractAddr(),web3,config.getCredentials(),new StaticGasProvider(gasPrice.getGasPrice(),BigInteger.valueOf(3000000)));

        contract = MPChannels_sol_MPChannels.load(config.getSmartContractAddr(),web3,transactionManager,new StaticGasProvider(gasPrice.getGasPrice(),BigInteger.valueOf(3000000)));
            //contract = MPChannels_sol_MPChannels.load(config.getSmartContractAddr(),web3,transactionManager,new DefaultGasProvider());
            System.out.println("Load smart contract done!");
    }
    public EthereumConfig getConfig() {
        return config;
    }

}
