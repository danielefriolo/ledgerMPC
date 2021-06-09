package fabcar;

import java.nio.file.Path;
import java.nio.file.Paths;


import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.json.simple.JSONObject;

public class NetworkConnectionConfigurator{

    private Wallet wallet;
    private String userId;
    private String chaincodeName;
    private Path ncPath;
    private String channelName;

    public NetworkConnectionConfigurator(JSONObject jsonObject) throws Exception {
        String walletPath= (String) jsonObject.get("walletPath");
        this.wallet = Wallets.newFileSystemWallet(Paths.get(walletPath));
        this.userId = (String) jsonObject.get("userId");
        this.chaincodeName = (String) jsonObject.get("chaincodeName");
        this.channelName = (String) jsonObject.get("channelName");
        String networkConfigPath = (String) jsonObject.get("networkConfigPath");
        this.ncPath = Paths.get(networkConfigPath);
    }



    public NetworkConnectionConfigurator(String walletPath, String networkConfigPath, String channelName, String chaincodeName, String userId) throws Exception{
        this.chaincodeName=chaincodeName;
        this.channelName=channelName;
        this.userId=userId;
        Path walletP = Paths.get(walletPath);
        this.wallet = Wallets.newFileSystemWallet(walletP);
        this.ncPath = Paths.get(networkConfigPath);
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChaincodeName() {
        return chaincodeName;
    }

    public void setChaincodeName(String chaincodeName) {
        this.chaincodeName = chaincodeName;
    }

    public Path getNcpath() {
        return ncPath;
    }

    public void setNcpath(Path ncpath) {
        this.ncPath = ncpath;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
