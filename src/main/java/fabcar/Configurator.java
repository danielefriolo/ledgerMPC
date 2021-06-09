package fabcar;

import java.nio.file.Paths;
import java.util.Properties;

import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.HFCAClient;

public class Configurator {

    private Properties props;
    private HFCAClient caClient;
    private CryptoSuite cryptoSuite;
    private Wallet wallet;


    public Configurator(String pemPath, String caUrl, String walletPath) throws Exception{
        this.props = new Properties();
        props.put("pemFile", pemPath);
        props.put("allowAllHostNames", "true");
        this.caClient = HFCAClient.createNewInstance(caUrl, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        caClient.setCryptoSuite(cryptoSuite);
        // Create a wallet for managing identities
        this.wallet = Wallets.newFileSystemWallet(Paths.get(walletPath));
    }

    public Properties getProps() {
        return props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    public HFCAClient getCaClient() {
        return caClient;
    }

    public void setCaClient(HFCAClient caClient) {
        this.caClient = caClient;
    }

    public CryptoSuite getCryptoSuite() {
        return cryptoSuite;
    }

    public void setCryptoSuite(CryptoSuite cryptoSuite) {
        this.cryptoSuite = cryptoSuite;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }
}

