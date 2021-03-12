# 陆羽协议

陆羽协议0.1版本如下。

## 一、协议综述

### 1.1 协议栈

陆羽协议分为四层

* 应用层：业务定制化逻辑
* 事务层：多条链操作的事务性
* 网络层：区块链数据的统一抽象，操作请求寻址
* 链路层：直接操作链，具体链的定制化逻辑，插件实现

![](img/stack.svg)

### 1.2 统一抽象

#### 账户统一抽象

将各种类型的链账户进行统一抽象，分为一级账户与二级账户

**定义**

* 一级账户（Luyu Account）：陆羽协议的统一账户，是对各种类型链账户的统一抽象
* 二级账户（Chain Account）：不同类型链的账户，用于向特定链上发交易
* 实现
  * 一二级账户关联方式：一级账户与二级账户相互签名
  * 签名算法
    * 一级账户：ECDSA
    * 二级账户：各链实现
  * 密钥管理
    * 一级账户：用户保管
    * 二级账户：托管在机构的Account Manager中

#### 资源统一抽象

将链上的智能合约、链码等可操作对象统一抽象为”资源“

**定义**

* 描述：将链上的智能合约、链码等可操作对象统一抽象为”**资源**“
* 格式：`lyp://{zone}.{chain}.{resource}/{operation}`   
* 其中
  * `{zone}` 业务名
  * `{chain}` 链名
  * `{resource}` 资源名
  * `{operation}` 操作，包括 
    * sendTransaction
    * call
    * getTransactionReceipt
    * getBlockByHash
    * getBlockByNumber
    * listResources

####  抽象定义详细描述

陆羽协议对区块链上的各种抽象定义包括

* Block
* Resource
* Transaction、Receipt
* CallRequest、CallResponse
* LuyuAccount、ChainAccount

**陆羽协议与语言无关**，此处用`java`语言进行举例

##### Block 定义

``` java
public class Block {
    private String chainPath; // Path of the blockchain. eg: payment.chain0
    private long number; // Block number
    private String hash; // Block Hash
    private String parentHash; // Block parent hash
    private String[] roots; // Block roots array. eg: transaction root, state root or receipt root
    private byte[] bytes; // Original block bytes of a certain blockchain
    
    // --snip--
}
```

##### Resource

``` java
public class Resource {
    private String path; // Path of the resource. eg: payment.chain0.hello
    private String type; // Blockchain type that the resource belongs to
    private String[] methods; // Method list of resource function name. eg: ["transfer(2)", "balanceOf(1)"]
    private Map<String, Object> properties; // Other property if needed
    
    // --snip--
}
```

##### Transaction

``` java
public class Transaction {
    private String path; // Path of the calling resource. eg: payment.chain0.hello
    private String method; // Method of resource function name. eg: "transfer"
    private String[] args; // Arguments of function. eg: ["Tom", "100"]
    private long nonce; // Nonce for unique

    // Either key or LuyuSign
    private byte[] key; // Secret key of a certain blockchain

    // Either key or LuyuSign, if key not set, use this sign to query AccountManager
    private byte[] LuyuSign; // Signature by luyu account
    
    // --snip--
}
```

##### Receipt

``` java
public class Receipt {
    private String result; // Resource function's return output
    private long code; // Error code
    private String message; // Error message
    private String path; // Transaction path of the calling resource. eg: payment.chain0.hello
    private String method; // Transaction method of resource function name. eg: "transfer"
    private String[] args; // Transaction arguments of function. eg: ["Tom", "100"]
    private String transactionHash; // Transaction hash
    private byte[] transactionBytes; // The original transaction bytes of a certain blockchain
    private long blockNumber; // Block number of this transaction belongs to
    
    // --snip--
}
```

##### CallRequest

``` java
public class CallRequest {
    private String path; // Path of the calling resource. eg: payment.chain0.hello
    private String method; // Method of resource function name. eg: "transfer"
    private String[] args; // Arguments of function. eg: ["Tom", "100"]
    
    // --snip--
}
```

##### CallResponse

``` java
public class CallResponse {
    private String result; // Resource function's return output
    private long code; // Error code
    private String message; // Error message
    private String path; // Transaction path of the calling resource. eg: payment.chain0.hello
    private String method; // Transaction method of resource function name. eg: "transfer"
    private String[] args; // Transaction arguments of function. eg: ["Tom", "100"]
    
    // --snip--
}
```

##### LuyuAccount

``` java
public class LuyuAccount {
    private Map<String, Object> properties = new HashMap<>();

    private String name;
    private byte[] identity;
    private byte[] secKey;
    private byte[] pubKey;
    
    // --snip--
}
```

##### ChainAccount

``` java
public class ChainAccount {
    private Map<String, Object> properties = new HashMap<>();

    private String name;
    private String type;
    private byte[] identity;
    private byte[] secKey;
    private byte[] pubKey;
    
    // --snip--
}
```

### 1.3 架构

**总体架构**

陆羽协议的架构如下，各组件为：

* **SDK**：发送交易，操作跨链网络
* **Account Manager**：管理统一账户信息，托管二级账户私钥
* **Router**：跨链路由，管理插件，转发请求
  * **Router Manager**：路由总逻辑，调用Account Manager验签，调用插件发交易
  * **Plugin**：各种链插件的实现，与链对接
  * **其它模块**（图中未画出）：Config、P2P、PeerManager、PluginManager、ZoneManager等





![](img/frame.svg)

### 1.4 流程

**单Router调用举例**

sendTransaction操作经过层层处理，最后调用至区块链上。



![](img/module1.svg)

流程见下图。需注意的是，区块头同步是异步的，在多个交易并发发送并被打包到一个区块中后，只需同步一次区块头即可。

![](img/flow1.svg)

**跨Router调用举例**

sendTransaction操作经过层层处理，最后调用至另一个Router连接的区块链上。其中发起方Router的链路层的Connection是Remote的，该模块是Connection的抽象映射，负责转发至相应Router的Connection，接口与一般的Connection完全相同。



![](img/module2.svg)

流程基本与单Router调用相同，区别在于多了远程的Connection模块，该模块是远端Connection的抽象映射，直接透传参数，不做任何处理。



![](img/flow2.svg)

**链发起调用举例**

由区块链的SDK发起，调用至区块链A上。区块链A上部署了接收跨链调用请求的合约，合约通过事件机制回调至插件，插件将调用参数和调用者的链身份以事件的形式通知至Router Manager。Router Manager从Account Manager处查询到对应链的私钥后，调用相应的插件将跨链交易发送出去。



![](img/module3.svg)

流程如下

![](img/flow3.svg)



## 二、详细描述

此部分描述陆羽协议的详细定义，**陆羽协议与语言无关**，此处用`java`语言描述。

### 2.1 应用层

SDK 向应用开放的接口协议

``` java
public interface SDK {
    Receipt sendTransaction(Transaction tx);

    CallResponse call(CallRequest request);

    Receipt getTransactionReceipt(String txHash);

    Block getBlockByHash(String blockHash);

    Block getBlockByNumber(long blockNumber);

    Resource[] listResources(String chainPath);
}
```

### 2.2 事务层

对于SDK发来的请求处理方式

* 一般请求：直接透传

* 事务请求：（待设计）

### 2.3 网络层

#### 实现描述

Router向应用层暴露的接口实现

* 网络协议：**http/https**

* 接口类型：**Restful**

* 编码：**json**

#### 抽象定义描述

网络层对区块链进行了**统一抽象**，包括，详细定义见上节

* Block
* Resource
* Transaction、Receipt
* CallRequest、CallResponse
* LuyuAccount、ChainAccount

### 2.4 链路层

#### 链驱动 Driver

Driver 向网络层提供的接口协议

``` java
public interface Driver {

    interface ReceiptCallback {
        void onResponse(Receipt receipt);
    }

    interface CallResponseCallback {
        void onResponse(CallResponse callResponse);
    }

    interface BlockCallback {
        void onResponse(Block block);
    }

    interface ResourcesCallback {
        void onResponse(Resource[] resources);
    }

    /**
     * Query a contract api of blockchain with verifying on-chain proof (generate block)
     *
     * @param request
     * @param callback
     */
    void sendTransaction(Transaction request, ReceiptCallback callback);

    /**
     * Query a contract api of blockchain without verifying on-chain proof (no block generated)
     *
     * @param request
     * @param callback
     */
    void call(CallRequest request, CallResponseCallback callback);

    /**
     * Get a transaction receipt by transaction hash with verifying on-chain proof
     *
     * @param txHash
     * @param callback
     */
    void getTransactionReceipt(String txHash, ReceiptCallback callback);

    /**
     * Get block by hash
     *
     * @param blockHash
     * @param callback
     */
    void getBlockByHash(String blockHash, BlockCallback callback);

    /**
     * Get block by block number
     *
     * @param blockNumber
     * @param callback
     */
    void getBlockByNumber(long blockNumber, BlockCallback callback);

    /**
     * Sign message with account secret key
     *
     * @param key The secret key of an account
     * @param message The message for signing
     * @return signBytes
     */
    byte[] accountSign(byte[] key, byte[] message);

    /**
     * Verify signature of an account
     *
     * @param identity Account's identity, eg: address or public key
     * @param signBytes Signature with binary encoded
     * @param message The message for signing
     * @return
     */
    boolean accountVerify(byte[] identity, byte[] signBytes, byte[] message);

    /**
     * Get block chain driver type
     *
     * @return
     */
    String getType();

    /**
     * Get resource list belongs to a chain
     *
     * @param chainPath Eg: payment.chain0
     * @param callback Return the array of resources
     */
    void listResources(String chainPath, ResourcesCallback callback);

    /**
     * Call function in events to call router logic
     *
     * @param events
     */
    void onChainEvent(Events events);
}
```

#### 链连接 Connection

Connection向Driver层提供的接口协议

``` java
public interface Connection {
    /** Callback of asyncSend() function */
    interface Callback {
        /**
         * On response
         *
         * @param errorCode The errorCode according with diffrent implementation
         * @param message The description of errorCode
         * @param responseData Response binary package data, should decode to use it
         */
        void onResponse(long errorCode, String message, byte[] responseData);
    }

    /**
     * Send binary package data to certain block chain connection. Define type in your
     * implementation to separate different kinds of data
     *
     * @param path The luyu path to original blockchain
     * @param type The type defined by implementation to separate different kinds of data
     * @param data The binary package data, encode according with different implementation
     * @param callback
     */
    void asyncSend(String path, long type, byte[] data, Callback callback);

    /**
     * Subscribe callback by sending binary package data to certain block chain connection. Define
     * type in your implementation to separate different kinds of data
     *
     * @param type The type defined by implementation to separate different kinds of data
     * @param data The binary package data, encode according with different implementation
     * @param callback
     */
    void subscribe(long type, byte[] data, Callback callback);
}
```