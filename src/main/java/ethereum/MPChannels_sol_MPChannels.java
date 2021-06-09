package ethereum;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.7.0.
 */
@SuppressWarnings("rawtypes")
public class MPChannels_sol_MPChannels extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_GETPARTICIPANTSBLOCK = "getParticipantsBlock";

    public static final String FUNC_READMESSAGE = "readMessage";

    public static final String FUNC_SENDMESSAGE = "sendMessage";

    public static final String FUNC_SETPARTICIPANTS = "setParticipants";

    @Deprecated
    protected MPChannels_sol_MPChannels(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected MPChannels_sol_MPChannels(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected MPChannels_sol_MPChannels(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected MPChannels_sol_MPChannels(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<BigInteger> getParticipantsBlock(String sid) {
        final Function function = new Function(FUNC_GETPARTICIPANTSBLOCK, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(sid)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Tuple2<byte[], BigInteger>> readMessage(String sid, BigInteger _receivingPid, BigInteger _sendingPid, BigInteger _msgNum) {
        final Function function = new Function(FUNC_READMESSAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(sid), 
                new Uint256(_receivingPid),
                new Uint256(_sendingPid),
                new Uint256(_msgNum)),
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<byte[], BigInteger>>(function,
                new Callable<Tuple2<byte[], BigInteger>>() {
                    @Override
                    public Tuple2<byte[], BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<byte[], BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> sendMessage(String sid, byte[] _message, BigInteger _receivingPid, BigInteger _sendingPid, BigInteger _msgNum) {
        final Function function = new Function(
                FUNC_SENDMESSAGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(sid), 
                new DynamicBytes(_message),
                new Uint256(_receivingPid),
                new Uint256(_sendingPid),
                new Uint256(_msgNum)),
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setParticipants(String sid, List<String> _participants) {
        final Function function = new Function(
                FUNC_SETPARTICIPANTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(sid), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(_participants, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static MPChannels_sol_MPChannels load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new MPChannels_sol_MPChannels(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static MPChannels_sol_MPChannels load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new MPChannels_sol_MPChannels(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static MPChannels_sol_MPChannels load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new MPChannels_sol_MPChannels(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static MPChannels_sol_MPChannels load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new MPChannels_sol_MPChannels(contractAddress, web3j, transactionManager, contractGasProvider);
    }
}
