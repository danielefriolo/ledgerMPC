package Blockchain;

public abstract class Blockchain<T> {
	protected byte[] sid;
	protected Integer sender;
	protected Integer receiver;
	
	/**
	 * SetUp method is used to setup the environment. 
	 * It takes a T type that depends on the implementation of the interface, 
	 * the identifier of the specific sender and specific receiver of the 
	 * current instance
	 * 
	 * @param setupData          the object to configure the blockchain
	 * @param sid                the session identifier of the current execution
	 * @param sender             the identifier of the sender of the current instantiation
	 * @param receiver           the identifier of the receiver of the current instantiation
	 * @throws Exception

	 */
	abstract public void setUp(T setupData, byte[] sid, Integer sender, Integer receiver) throws Exception;
	
	/**
	 * Clean the local environment
	 */
	abstract public void shutDown();
	
	/**
	 * Write data on the blockchain. The method uses the session identifier, 
	 * the identifier of the sending and receiving party, the message number and 
	 * the content that the sender want to send to the receiver. 
	 * 
	 * @param data               the data to write to the blockchain
	 * @param msgNum             the index of the message
	 */
	abstract public void writeData(byte[] data, int msgNum) throws Exception;
	
	
	/**
	 * Read data from the blockchain given a communication section identifier, the identifier of the receiver and the sender and the message index to read.
	 * The read command is a blocking method that returns only when the message to read is ready.
	 * 
	 * @param msgNum              the index of the message to read
	 * @param quickness           True if the execution is quick, false otherwise
	 * @return                    a byte[] object containing the message 
	 */
	abstract public byte[] readData(int msgNum, Boolean quickness) throws Exception;
	
	/**
	 * Read data from the blockchain given a communication section identifier, the identifier of the receiver and the message index 
	 * to read and the number of messages to read with this message identifier.
	 * In this case the messages are broadcast.
	 * The read command is a blocking method that returns only when the messages to read are ready.
	 * 
	 * @param msgNum              the index of the message to read
	 * @param partyNum            the total number of parties that participates to the protocol
	 * @param quickness           True if the execution is quick, false otherwise
	 * @return                    a list of byte[] object containing the messages, message in position i 
	 * 							  is the message from party i+1. The message associated to the current player is null  
	 */
	//abstract public byte[][] readBroadcastData(int msgNum, int partyNum, Boolean quickness) throws Exception;

}
