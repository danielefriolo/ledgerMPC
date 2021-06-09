package ethereum;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Set;

public class InternalMessageHandler implements LocalMessageHandler {
    HashMap<Integer, HashMap<Integer, byte[]>> messages;
    UnconfirmedBucket unconfirmedBucket;

    @Override
    public void setUp(String path, long confirmationTime) {
        messages = new HashMap<>();
        unconfirmedBucket = new UnconfirmedBucket(confirmationTime);
    }

    @Override
    public byte[] retrieveMessage(int msgNum, int pid) {
        if (messages.containsKey(pid) && messages.get(pid).containsKey(msgNum))
                return messages.get(pid).get(msgNum);
        else return new byte[0];
    }

    @Override
    public void storeMessage(byte[] msg, int msgNum, int pid, BigInteger block) {
        if (!messages.containsKey(pid))
            messages.put(pid,new HashMap<>());
        if (!messages.get(pid).containsKey(msgNum)) {
            messages.get(pid).put(msgNum,msg);
        }
        unconfirmedBucket.flush();
        unconfirmedBucket.add(new BucketMessage(msg,msgNum,pid,block));
    }
    public Set<BucketMessage> getUnconfirmedMessages(int pid) {
        return unconfirmedBucket.filter(pid);
    }
    public void setActualBlock(BigInteger block) { unconfirmedBucket.setActualBlock(block);}
}
