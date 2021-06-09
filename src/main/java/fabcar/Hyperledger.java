package fabcar;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.util.Base64;
import Blockchain.Blockchain;

public class Hyperledger extends Blockchain<String> {

    private NetworkConnectionConfigurator nc;


    public void setUp(String configFilePath, byte[] sid, Integer sender, Integer receiver) throws Exception {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(configFilePath);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        this.nc = new NetworkConnectionConfigurator(jsonObject);
        this.sid = sid;
        this.sender = sender;
        this.receiver = receiver;
        if(this.sender==1 && this.receiver==2){
            //The user is designated for setting up the session with involved parties
            JSONArray parties = (JSONArray) jsonObject.get("parties");
            String sessionJSON = "{\"parties\":"+parties.toJSONString()+"}";
            createSession(sid,sessionJSON);
        }
    }

    @Override
    public void shutDown() {
        return;
    }

    @Override
    public void writeData(byte[] data, int msgNum) throws Exception {

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(nc.getWallet(), nc.getUserId()).networkConfig(nc.getNcpath()).discovery(true);
        Base64.Encoder encoder = Base64.getEncoder();
        String encMsg = encoder.encodeToString(data);
        String encSid = new String(sid);
        System.out.println(encSid +" "+ sender.toString()+" "+  receiver.toString()+" "+  String.valueOf(msgNum)+" "+  encMsg);
        try (Gateway gateway = builder.connect() ) {
            String resultAsStr = "NO_SESSION";
            Network network = gateway.getNetwork(nc.getChannelName());
            Contract contract = network.getContract(nc.getChaincodeName());
            byte[] result={};
            while(resultAsStr.equals("NO_SESSION")){
                result=contract.submitTransaction("createMessage", encSid, sender.toString(), receiver.toString(), String.valueOf(msgNum), encMsg);
                resultAsStr = new String(result);
                if(resultAsStr.equals("NO_SESSION")) {
                    Thread.sleep(1000);
                }
            }
        }


    }

    @Override
    public byte[] readData(int msgNum, Boolean quickness) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(nc.getWallet(), nc.getUserId()).networkConfig(nc.getNcpath()).discovery(true);

        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork(nc.getChannelName());
            Contract contract = network.getContract(nc.getChaincodeName());

            byte[] result={};
            String encSid = new String(sid);
            String resultAsStr = "NOT_FOUND";
            while(resultAsStr.equals("NOT_FOUND")){
                result = contract.evaluateTransaction("queryMessage", encSid, receiver.toString(), sender.toString(), String.valueOf(msgNum));
                System.out.println("reading : "+encSid+" "+ receiver.toString() +" "+ sender.toString() +" "+ String.valueOf(msgNum));
                resultAsStr = new String(result);
                System.out.println("result: "+resultAsStr);
                if(resultAsStr.equals("NOT_FOUND")) {
                    Thread.sleep(1000); //6000
                }
            }
            return decoder.decode(result);
            
        }
    }

    public void createSession(byte[] sid, String sessionJSON) throws Exception {

        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(nc.getWallet(), nc.getUserId()).networkConfig(nc.getNcpath()).discovery(true);

        try (Gateway gateway = builder.connect()) {

            // get the network and contract
            Network network = gateway.getNetwork(nc.getChannelName());
            Contract contract = network.getContract(nc.getChaincodeName());

            contract.submitTransaction("createSession", new String(sid), sessionJSON);

        }
    }
}
