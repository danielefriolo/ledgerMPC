package ethereum;

import org.web3j.utils.Numeric;

public class Test {

    public static void main(String [] args) throws Exception {
        //commandLine(args);
        test1();
    }

public static void commandLine(String[] args) throws Exception {
    EthereumBlockchain bchain = new EthereumBlockchain();
    bchain.setUp("/Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig.json",Numeric.hexStringToByteArray("bbccdd23"),Integer.parseInt(args[0]),Integer.parseInt(args[1]));
    switch (args[2]) {
        case "read":
            System.out.println(Numeric.toHexString(bchain.readData(Integer.parseInt(args[3]),true)));
            break;

        case "write":
            bchain.writeData(Numeric.hexStringToByteArray(args[3]),Integer.parseInt(args[4]));
            break;

        default: System.out.println("no argomenti") ;
    }
}
public static void test1() throws Exception {
    EthereumBlockchain bchain = new EthereumBlockchain();
    bchain.setUp("/Users/danielefriolo/toolkitunisa/toolkitunisa/ethereum/testnetconfig.json",Numeric.hexStringToByteArray("bbccdd23"),1,2);
    //System.out.println(bchain.getConfig().getHostURL());
   // System.out.println(bchain.getConfig().getSmartContractAddr());

}
}
