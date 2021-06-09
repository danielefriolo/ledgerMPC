package fabcar;


import java.security.PrivateKey;
import java.util.Set;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric_ca.sdk.Attribute;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;


public class UserEnroller {

    private Configurator conf;

    public UserEnroller(Configurator conf) {
        this.conf = conf;
    }

    public void enrollUser(String userId, String affiliation, String mspId ) throws Exception{
        // Check to see if we've already enrolled the user.
        if (conf.getWallet().get(userId) != null) {
            System.out.println("An identity for the user " + userId + " already exists in the wallet");
            return;
        }

        X509Identity adminIdentity = (X509Identity) conf.getWallet().get("admin");
        if (adminIdentity == null) {
            System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
            return;
        }
        User admin = new User() {

            @Override
            public String getName() {
                return "admin";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return affiliation;
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {

                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return mspId;
            }

        };

        // Register the user, enroll the user, and import the new identity into the wallet.
        RegistrationRequest registrationRequest = new RegistrationRequest(userId);
        registrationRequest.setAffiliation(affiliation);
        registrationRequest.setEnrollmentID(userId);
        registrationRequest.addAttribute(new Attribute("userId",userId,true));
        String enrollmentSecret = conf.getCaClient().register(registrationRequest, admin);
        Enrollment enrollment = conf.getCaClient().enroll(userId, enrollmentSecret);
        Identity user = Identities.newX509Identity(mspId, enrollment);
        conf.getWallet().put(userId, user);
        System.out.println("Successfully enrolled user "+ "\"" +userId+"\"" +" and imported it into the wallet");
    }
}

