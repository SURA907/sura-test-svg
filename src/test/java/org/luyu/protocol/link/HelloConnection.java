package org.luyu.protocol.link;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.luyu.protocol.blockchain.MockBlockchain;
import org.luyu.protocol.utils.Utils;

public class HelloConnection implements Connection {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;

    public static final int SEND_TRANSACTION = 101;
    public static final int CALL_TRANSACTION = 102;
    public static final int GET_TRANSACTION_RECEIPT = 103;

    public static final int EVENT_NEW_BLOCK = 201;
    public static final int EVENT_RESOURCES_CHANGED = 202;

    private MockBlockchain blockchain;

    public HelloConnection(Map<String, Object> properties) {
        String chainPath = (String) properties.get("chainPath");

        this.blockchain = new MockBlockchain(chainPath);
    }

    @Override
    public void asyncSend(String path, int type, byte[] data, Callback callback) {
        String resourceName = Utils.getResourceName(path);

        switch (type) {
            case SEND_TRANSACTION:
                {
                    String contractAddress = getContractAddress(resourceName);
                    blockchain.sendTransaction(
                            contractAddress,
                            data,
                            new MockBlockchain.TxCallback() {
                                @Override
                                public void onResponse(byte[] receipt, byte[] onChainProof) {
                                    // Encode receipt bytes and onChainProof bytes together
                                    byte[] responseData = Utils.bytesConcat(receipt, onChainProof);

                                    callback.onResponse(
                                            SUCCESS, "Send transaction success", responseData);
                                }
                            });
                    break;
                }

            case CALL_TRANSACTION:
                {
                    String contractAddress = getContractAddress(resourceName);
                    byte[] result = blockchain.call(contractAddress, data);
                    callback.onResponse(SUCCESS, "Call success", result);
                    break;
                }

            case GET_TRANSACTION_RECEIPT:
                {
                    byte[] receipt = blockchain.getTransactionReceipt(data);
                    byte[] onChainProof = "block-header".getBytes(StandardCharsets.UTF_8);

                    // Encode receipt bytes and onChainProof bytes together
                    byte[] responseData = Utils.bytesConcat(receipt, onChainProof);

                    callback.onResponse(SUCCESS, "Get receipt success", responseData);
                    break;
                }

            default:
                {
                    callback.onResponse(ERROR, "Unrecognized type of " + type, null);
                    break;
                }
        }
    }

    @Override
    public void subscribe(int type, byte[] data, Callback callback) {

        switch (type) {
            case EVENT_NEW_BLOCK:
                {
                    blockchain.registerBlockEvent(
                            new MockBlockchain.BlockEvent() {
                                @Override
                                public void onNewBlock(byte[] blockBytes) {
                                    callback.onResponse(SUCCESS, "New block", blockBytes);
                                }
                            });
                }

            case EVENT_RESOURCES_CHANGED:
            default:
        }
    }

    private String getContractAddress(String contractName) {
        // Use CNS for something else to get real address of contract
        return "address-" + contractName;
    }
}
