package ethereum;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class UnconfirmedBucket {
    private BigInteger actualBlock;
    private HashSet<BucketMessage> messages;
    private long maxConfirmation;
    public UnconfirmedBucket(long maxConfirmation) {
        actualBlock = BigInteger.valueOf(1);
        messages = new HashSet<>();
        this.maxConfirmation = maxConfirmation;
    }
    public void setActualBlock(BigInteger block) {
        actualBlock = block;
    }
    public void add(BucketMessage msg) {
        messages.add(msg);
        flush();
    }
    public void flush() {
        HashSet<BucketMessage> toBeRemoved = new HashSet<>();
        for (BucketMessage m : messages) {
            if (actualBlock.subtract(m.getBlock()).intValue() >= maxConfirmation)
                toBeRemoved.add(m);
        }
        messages.removeAll(toBeRemoved);
    }
    public Set<BucketMessage> filter(int pid) {
        flush();
        HashSet<BucketMessage> filter = new HashSet<>();
        for (BucketMessage m : messages) {
            if (m.getPid() == pid)
                filter.add(m);
        }
        return filter;
    }
}
