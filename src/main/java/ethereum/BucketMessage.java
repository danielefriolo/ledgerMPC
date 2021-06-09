package ethereum;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;

public class BucketMessage {
    private byte[] msg;
    private int msgNum;
    private BigInteger block;
    private int pid;
     public BucketMessage(byte[] msg, int msgNum,int pid, BigInteger block) {
         this.msg = msg;
         this.msgNum = msgNum;
         this.block = block;
         this.pid = pid;
        }
        @Override
        public boolean equals(Object obj) {
            BucketMessage m = (BucketMessage) obj;
            return Arrays.equals(m.getMsg(), msg) && m.getMsgNum() == msgNum && m.getPid() == pid;
        }
        @Override
        public int hashCode() {
          return Objects.hash(msg,msgNum,pid);
        }
        public byte[] getMsg() { return msg;}
        public int getPid() { return pid; }
        public BigInteger getBlock() { return block;}
    public int getMsgNum() { return msgNum; }
}
