package fabcar;

import Blockchain.Blockchain;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

public class DataOnFile extends Blockchain<String> {
    byte[] sid;
    Integer sender;
    Integer receiver;
    private Integer inputCounter, outputCounter;
    private Integer remotePort;
    private InetAddress remoteAddress;
    private Socket clientSocket;
    private Protocol protocol;
    private String configFile;
    private boolean called;


    private Map<Integer, Message> receivedMessages;

    public DataOnFile () throws UnknownHostException {
        this.inputCounter = 0;
        this.outputCounter = 0;
        this.receivedMessages = new HashMap<Integer, Message>();
        this.called = false;
        this.protocol = new Protocol();
    }


    /**
     * SetUp method is used to setup the environment. It takes a path to a file to configure two sockets.
     * The file has the following structure. It is composed by 2 lines:
     * IP address of the remote server;
     * port number of the remote server.
     *
     * The setUp takes the session identifier for the current run, the sender and the receiver of the current instance.
     *
     * It creates a socket that waits data from remote server and socket that sends data to remote server.
     * @throws Exception
     */
    @Override
    public void setUp(String configFile, byte[] sid, Integer sender, Integer receiver) throws Exception {
        if(!called) {
            this.sid = sid;
            this.sender = sender;
            this.receiver = receiver;

            updateCounters();
            this.configFile = configFile;

            Connection setupInfo = readaAddresses();
            this.remotePort = setupInfo.getRemotePort();
            this.remoteAddress = setupInfo.getRemoteAddress();

            try {
                clientSocket = new Socket(remoteAddress, remotePort);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Wrong connection information");
            }

            try {
                clientSocket.setReuseAddress(true);
            } catch (SocketException e) {
                e.printStackTrace();
            }

            called = true;
        }
    }

    /**
     * Clean the local environment
     */
    @Override
    public void shutDown(){
        if(called) {
            try {
                called = false;
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write data on the remote server. The method uses the session identifier, the identifier of
     * the sending and receiving party. The method takes the message number and the content that the sender want to send to the receiver.
     * The messages to write must be ordered.
     *
     * @param message              the data to write to the remote server
     * @param msgNum               the index of the message
     */
    @Override
    public void writeData(byte[] message, int msgNum) throws IllegalArgumentException {
        if(called) {
            DataOutputStream os = null;
            byte[] sessionID = this.sid;
            int sendingPartyID = this.sender;
            int receivingPartyID = this.receiver;

            if(msgNum != (outputCounter + 1)){
                throw new IllegalArgumentException("Wrong message index");
            }
            try {
                os = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            message = protocol.prepareMessage(sessionID, sendingPartyID, receivingPartyID, msgNum, protocol.write, message);

            try {
                os.write(message);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputCounter = outputCounter + 1;
        }else {
            throw new IllegalArgumentException("Write called without setup");
        }
    }

    /**
     * Read data from the remote server for the current communication section identifier, the identifier of the receiver and the sender and the message index to read.
     * The read command is a blocking method that returns only when the message to read is ready. The read requests must be ordered
     *
     * @param msgNum              the index of the message to read
     * @param quickness           True if the execution is quick, false otherwise
     * @return                    a byte[] object containing the message
     */
    @Override
    public byte[] readData(int msgNum, Boolean quickness){
        if(called) {
            byte[] message;
            DataInputStream is = null;
            DataOutputStream os = null;
            byte[] dummyData = {0,0,0,0};

            Message msg = null;
            Integer readMsgNum = protocol.impossibleVal;

            if(msgNum != (inputCounter + 1)){
                return null;
            }

            try {
                os = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            if(receivedMessages.containsKey(msgNum)){
                msg = receivedMessages.get(msgNum);
                receivedMessages.remove(msgNum);
                return msg.getMsg();
            }

            message = protocol.prepareMessage(this.sid, sender, receiver, inputCounter, protocol.read, dummyData);
            try {
                os.write(message);
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                is = new DataInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(msgNum != readMsgNum){
                msg = protocol.readSingleMessage(is);
                readMsgNum = msg.getMsgNum();

                if(readMsgNum != protocol.impossibleVal) {
                    if(!receivedMessages.containsKey(readMsgNum)) {
                        receivedMessages.put(readMsgNum, msg);
                    }
                }else{
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            inputCounter = inputCounter + 1;
            receivedMessages.remove(msg.getMsgNum());
            return msg.getMsg();
        }
        return null;
    }

    public Integer getInputCounter() {
        return inputCounter;
    }

    public Integer getOutputCounter() {
        return outputCounter;
    }

    public Integer getRemotePort() {
        return remotePort;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    private Connection readaAddresses() throws FileNotFoundException, IOException {
        String remoteAddress;
        String remotePort;
        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            remoteAddress = br.readLine();
            remotePort = br.readLine();
        }

        Connection conn = new Connection(Integer.parseInt(remotePort), InetAddress.getByName(remoteAddress));
        return conn;
    }

    private void updateCounters() {
        // TODO Auto-generated method stub
    }


}
