package ethereum;

import org.web3j.crypto.Credentials;

import java.util.ArrayList;

public class EthereumConfig {
    private final String walletSK;
    private final String hostURL; //usually a driver element, e.g. "https://mainnet.infura.io/v3/<project key>"
    private final Credentials credentials;
    private final long maxTxSize;
    private final long confirmationTime; //expressed in number of blocks
    private final String smartContractAddr;
    //private ArrayList<String> addresses;
    private int sendingPid;
    private int receivingPid;
    private ArrayList<String> participants;
    public EthereumConfig(String walletSK, String hostURL, long maxTxSize, long confirmationTime, String smartContractAddr,
                          int sendingPid, int receivingPid, ArrayList<String> participants) {
        this.walletSK = walletSK;
        this.sendingPid = sendingPid;
        this.receivingPid = receivingPid;
        //this.addresses = addresses;
        this.credentials = Credentials.create(walletSK);
        this.hostURL = hostURL;
        this.maxTxSize = maxTxSize;
        this.confirmationTime = confirmationTime;
        this.smartContractAddr = smartContractAddr;
        this.participants = participants;
    }
    public String getWalletSK() {
        return walletSK;
    }
    public Credentials getCredentials() {
        return credentials;
    }
    public String getHostURL() {
        return hostURL;
    }
    public ArrayList<String> getParticipants () { return participants;}
    public int getSendingPid() {
        return sendingPid;
    }
    public int getReceivingPid() {
        return receivingPid;
    }

    //public ArrayList<String> getAddresses() {
       // return addresses;
    //}
    public long getMaxTxSize() {
        return maxTxSize;
    }
    public long getConfirmationTime() {
        return confirmationTime;
    }
    public String getSmartContractAddr() {
        return smartContractAddr;
    }
}
