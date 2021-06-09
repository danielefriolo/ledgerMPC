package fabcar;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Protocol {
    final public Integer headerLen = 28;
    final public Integer msgLen = 4;
    final public Integer sidLen = 8;
    final public Integer pidLen = 4;
    final public Integer msgLenLen = 4;
    final public Integer cmdLen = 4;
    final public Integer read = 0;
    final public Integer write = 1;
    final public Integer impossibleVal = -1;

    /**
     * Given an input stream, reads from the input stream the length of a message and the data
     * of the message and store these data in a Message object. It remains in reading
     * until enough data are found
     *
     * @param is    the DataInputStream from which the data are read
     * @return      a Message object containing the data read from the stream.
     *
     */
    public Message readSingleMessage(DataInputStream is){
        int len;
        byte[] data;
        byte[] readLen = new byte[msgLen];
        Message msg = null;

        try {
            readLen = readAllBytes(is, msgLen);
            len = bytesToInt(Arrays.copyOfRange(readLen, 0, msgLen));
            data = new byte [len - msgLen];
            data = readAllBytes(is, len - msgLen);
            msg = fromDataToMessage(data, len - msgLen);
        } catch (IOException e) {
            msg = new Message(null, null, null, impossibleVal, null);
        }

        return msg;
    }


    /**
     * Given an input stream, reads from the input stream the length of a message and the data
     * of the message and store these data in a Message object, Does not wait that data exists,
     * when the first error is returned, the procedure returns
     *
     * @param is    the DataInputStream from which the data are read
     * @return      a Message object containing the data read from the stream.
     * 				If there are no enough data in the stream, a default ;Message object is generated
     */
    public Message readSingleMessage2(DataInputStream is){
        int len;
        byte[] data;
        byte[] readLen = new byte[msgLen];
        Message msg = null;
        int ret;

        try {
            ret = is.read(readLen, 0, msgLen);
            if(ret < 0) {
                throw new IOException();
            }
            len = bytesToInt(Arrays.copyOfRange(readLen, 0, msgLen));
            data = new byte [len - msgLen];
            ret = is.read(data, 0, len - msgLen);
            if(ret < 0) {
                throw new IOException();
            }
            msg = fromDataToMessage(data, len - msgLen);
        } catch (IOException e) {
            msg = new Message(null, null, null, impossibleVal, null);
        }

        return msg;
    }


    /**
     * Given the data of a message, the session identifier, the sender and receiver identifiers,
     * the message index and the message content, prepare a byte array with the following information:
     * 4 bytes for the number of bytes of the entire message;
     * 8 bytes for the session identifier;
     * 4 bytes for the sender identifier;
     * 4 bytes for the receiver identifier;
     * 4 bytes for the message index;
     * 4 bytes for the command identifier;
     * and the remaining bytes for the content of the message
     *
     * The first 6 fields are the header of the packet
     *
     * @param sessionID					session identifier
     * @param sendingPartyID            sender identifier
     * @param receivingPartyID          receiver identifier
     * @param msgNum                    message index
     * @param cmd                       command identifier
     * @param data                      content of the message
     * @return                          a byte array formatted as previously specified
     */
    public byte[] prepareMessage(byte[] sessionID, Integer sendingPartyID,
                                 Integer receivingPartyID, Integer msgNum, Integer cmd, byte[] data){
        Integer dataLen = data.length;
        byte[] header = new byte[headerLen];
        byte[] packet = new byte[headerLen + dataLen];

        padding(msgLen, 0, intToBytes(headerLen + dataLen), header);
        padding(sidLen, msgLen, sessionID, header);
        padding(pidLen, sidLen + msgLen, intToBytes(sendingPartyID), header);
        padding(pidLen, pidLen + sidLen + msgLen, intToBytes(receivingPartyID), header);
        padding(msgLenLen, 2 * pidLen + sidLen + msgLen, intToBytes(msgNum), header);
        padding(cmdLen, msgLenLen + 2 * pidLen + sidLen + msgLen, intToBytes(cmd), header);


        for(int i = 0; i < headerLen; i++){
            packet[i] = header[i];
        }
        for(int i = 0; i < dataLen; i++){
            packet[headerLen + i] = data[i];
        }

        return packet;
    }

    /**
     * Given a Message object, prepare a byte array with the following information:
     * 4 bytes for the number of bytes of the entire message;
     * 8 bytes for the session identifier;
     * 4 bytes for the sender identifier;
     * 4 bytes for the receiver identifier;
     * 4 bytes for the message index;
     * 4 bytes for the command identifier;
     * and the remaining bytes for the content of the message
     *
     * @param message    the Message object to translate in a byte array
     * @return
     */
    public byte[] prepareMessage(Message message){
        if(message.getUtility() == null ||
                (message.getUtility() != this.write && message.getUtility() != this.read)) {
            throw new IllegalArgumentException("Wrong command");
        }
        return prepareMessage(message.getSid(), message.getSendingPid(),
                message.getReceivingPid(), message.getMsgNum(), message.getUtility(), message.getMsg());
    }



    /**
     * Given an array of bytes composed by the header and content of the packet, extracts the sender identifier
     *
     * @param data				the packet
     * @return                  the sender identifier
     */
    public Integer extractSender(byte[] data) {
        Integer sendingPartyID = bytesToInt(Arrays.copyOfRange(data, msgLen + sidLen, msgLen + sidLen + pidLen));
        return sendingPartyID;
    }

    /**
     * Given an array of bytes composed by the header and content of the packet, extracts the receiver identifier
     *
     * @param data				the packet
     * @return                  the receiver identifier
     */
    public Integer extractReceiver(byte[] data) {
        Integer receivingPartyID = bytesToInt(Arrays.copyOfRange(data, msgLen + sidLen + pidLen, msgLen + sidLen + 2 * pidLen));
        return receivingPartyID;
    }

    /**
     * Given an array of bytes composed by the header and content of the packet, extracts the command identifier
     *
     * @param data				the packet
     * @return                  the command identifier
     */
    public Integer extractCommand(byte[] data) {
        Integer cmdID = bytesToInt(Arrays.copyOfRange(data, msgLen + sidLen + 2 * pidLen + msgLenLen, msgLen + sidLen + 2 * pidLen + msgLenLen + cmdLen));
        return cmdID;
    }

    private byte[] readAllBytes(DataInputStream is, Integer len) throws IOException {
        byte[] read = new byte[len];
        int last_read = 0;
        do {
            byte[] r = new byte[len - last_read];
            int realReadData = is.read(r);
            for(int i = 0; i < realReadData; i++) {
                read[last_read + i] = r[i];
            }
            last_read = last_read + realReadData;

        }while(last_read < len);

        return read;
    }

    private Message fromDataToMessage(byte[] data, Integer totalLen){
        byte[] sessionID = Arrays.copyOfRange(data, 0, sidLen);
        Integer sendingPartyID = bytesToInt(Arrays.copyOfRange(data, sidLen, sidLen + pidLen));
        Integer receivingPartyID = bytesToInt(Arrays.copyOfRange(data, sidLen + pidLen, sidLen + 2 * pidLen));
        Integer msgNum = bytesToInt(Arrays.copyOfRange(data, sidLen + 2 * pidLen, sidLen + 2 * pidLen + msgLenLen));
        Integer cmd = bytesToInt(Arrays.copyOfRange(data, sidLen + 2 * pidLen + msgLenLen, sidLen + 2 * pidLen + msgLenLen + cmdLen));
        byte[] msg = Arrays.copyOfRange(data, sidLen + 2 * pidLen + msgLenLen + cmdLen, totalLen);

        Message message = new Message(sessionID, sendingPartyID, receivingPartyID, msgNum, msg);
        message.setUtility(cmd);

        return message;
    }

    private void padding(Integer byteNum, Integer startingPoint, byte[] data, byte[] container){
        int i = startingPoint;
        int j = 0;
        int dataLen = data.length;

        if(dataLen < byteNum){
            while(i < (startingPoint + byteNum - dataLen)){
                container[i] = 0;
                i++;
            }
        }

        while (i < startingPoint + byteNum){
            container[i] = data[j];
            i++;
            j++;
        }
    }

    private byte[] intToBytes (final int i){
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.putInt(i);
        return bb.array();
    }

    private int bytesToInt(byte[] intBytes){
        ByteBuffer byteBuffer = ByteBuffer.wrap(intBytes);
        return byteBuffer.getInt();
    }
}
