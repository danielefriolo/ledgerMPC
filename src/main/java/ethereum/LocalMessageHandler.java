package ethereum;

import java.math.BigInteger;
import java.util.Set;

public interface LocalMessageHandler {
    //Path needed for restart
    public void setUp(String path, long confirmationTime);
    public byte[] retrieveMessage(int msgNum, int pid);
    public void storeMessage(byte[] msg, int msgNum, int pid,BigInteger block);
    public Set<BucketMessage> getUnconfirmedMessages(int pid);
}
