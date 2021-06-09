package fabcar;

public class ExampleMaterialCreator {

    public static void main(String[] args) throws Exception{
        String pemPath=args[0];
        String caUrl=args[1];
        String walletPath=args[2];
        Configurator conf = new Configurator(pemPath,caUrl,walletPath);
        AdminEnroller ae = new AdminEnroller(conf);
        ae.enrollAdmin("adminpw", "Org1MSP");
        UserEnroller ue= new UserEnroller(conf);
        System.out.println("Enrolling first user");
        ue.enrollUser("1","org1.department1","Org1MSP");
        System.out.println("Enrolling second user");
        ue.enrollUser("2","org1.department1","Org1MSP");
        //Second Organization
        pemPath=args[3];
        caUrl=args[4];
        walletPath=args[5];
        conf = new Configurator(pemPath,caUrl,walletPath);
        ae = new AdminEnroller(conf);
        ae.enrollAdmin("adminpw", "Org2MSP");
        ue= new UserEnroller(conf);
        System.out.println("Enrolling third user");
        ue.enrollUser("3","org2.department1","Org2MSP");
    }

}
