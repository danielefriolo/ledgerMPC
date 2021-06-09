package fabcar;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;

public class AdminEnroller {

    private Configurator conf;

    public AdminEnroller(Configurator conf) {
        this.conf = conf;
    }

    public void enrollAdmin(String secret, String mspId) throws Exception {
        if (conf.getWallet().get("admin") != null) {
            System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
            return;
        }
        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost("localhost");
        enrollmentRequestTLS.setProfile("tls");
        Enrollment enrollment = conf.getCaClient().enroll("admin", secret, enrollmentRequestTLS);
        Identity user = Identities.newX509Identity(mspId, enrollment);
        conf.getWallet().put("admin", user);
        System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
    }
}