# 陆羽跨链协议

## 一、架构及组件

陆羽跨链协议的架构如下，各组件为：

* **应用（SDK）**：应用通过SDK向跨链路由发送交易，操作跨链网络。交易被路由接收后转发至相应可信源。
* **账户服务（Account Manager）**：是一个独立的服务，由部署跨链路由的各方独立部署。该服务负责管理统一账户信息，维护一级账户与二级账户的对应关系，并用二级账户对交易进行签名。
* **跨链路由（Router）**：以插件化的方式实现与可信源对接。并作为可信源之间互联的媒介，负责其消息路由。
  
  * **路由管理（Router Manager）**：路由内部的总逻辑模块，负责响应SDK消息、与账户服务交互，管理可信源插件和对跨链消息进行路由。
  
  * **插件（Plugin）**：是配置在跨链路由内的与可信源对接的模块，实现与可信源的数据交互。基于相同的协议，不同类型的可信源有不同的实现。
  
    （各模块的具体关系请参考《五、调用流程》章节）

![](img/frame.svg)

## 二、协议栈

协议分为四层

* 应用层：向业务提供的API，包括各种语言的SDK。
* 路由层：以统一的抽象数据结构进行调用和路由。
* 适配层：以插件形式适配不同可信源，将差异化的链数据抽象统一。
* 数据层：可信源（区块链，预言机等）。

![](img/stack.svg)

## 三、统一抽象协议

统一抽象协议包括四个协议，在可信源、账户服务、跨链路由和应用间定义统一的抽象协议。

* **统一账户协议**：各种可信源的账户的统一抽象，实现用统一的账户操作不同可信源。
* **统一寻址协议**：各种可信源智能合约（链码）等可操作对象的统一抽象，以"资源"的概念实现统一寻址。
* **统一调用协议**：各种可信源调用协议的统一抽象，实现用统一接口与参数调用资源。
* **统一接入协议**：各种可信源接入协议的抽象，实现不同可信源的统一接入适配。不同可信源基于此协议开发插件，即可完成适配接入。

![](img/abstract.svg)

### 3.1 统一账户协议

将各种可信源的账户进行统一抽象，以实现用统一的账户操作不同可信源。本协议分为一级账户与二级账户

**定义**

* 一级账户（LuyuAccount）：陆羽协议的统一账户，是对不同签名算法的链账户的统一抽象
  * 密钥管理：用户保管
  * 签名算法：ECDSA 或 国密
* 二级账户（Account）：不同签名算法的链账户，对交易进行签名，是链上的实际账户。本协议定义了一系列标准签名算法，可信源根据自身设计进行选择与实现
  * 签名算法
    * ECDSA
      * ECDSASecp256k1WithSHA256
      * ECDSASecp256r1WithSHA256
    * 国密
      * SM2WithSM3
    * ...（支持协议更新定义更多算法）

### 3.2 统一寻址协议

将各种可信源的智能合约（链码）等可操作对象统一抽象为”资源“，以实现统一寻址

**定义**

* 资源（Resource）：将可信源的智能合约（链码）等可操作对象统一抽象为”**资源**“
* 地址（Path）：`{zone}.{chain}.{resource}`   
  * `{zone}` 业务名
  * `{chain}` 链名
  * `{resource}` 资源名
  * `{operation}` 操作
    * sendTransaction
    * call
    * getTransactionReceipt
    * getBlockByHash、getBlockByNumber
    * listResources

### 3.3 统一调用协议

将各种可信源的调用协议进行统一抽象，以实现用统一的接口与参数对资源进行调用

**定义**

* 写入资源
  * 操作：[sendTransaction](../src/main/java/link/luyu/protocol/application/SDK.java#L11)
  * 参数
    * 交易（[Transaction](../src/main/java/link/luyu/protocol/network/Transaction.java)）：修改可信源数据的请求
    * 回执（[Receipt](../src/main/java/link/luyu/protocol/network/Receipt.java)）：修改可信源数据的结果
* 读取资源
  * 操作：[call](../src/main/java/link/luyu/protocol/application/SDK.java#L13)
  * 参数
    * 查询（[CallRequest](../src/main/java/link/luyu/protocol/network/CallRequest.java)）：查询可信源数据的请求
    * 返回（[CallResponse](../src/main/java/link/luyu/protocol/network/CallResponse.java)）：查询可信源数据的结果
* 查询记录
  * 操作：[getTransactionReceipt](/src/main/java/link/luyu/protocol/application/SDK.java#L15)
  * 参数
    * 回执（[Receipt](../src/main/java/link/luyu/protocol/network/Receipt.java)）：修改可信源数据的结果记录

* 查询区块
  * 操作：[getBlockByHash](../src/main/java/link/luyu/protocol/application/SDK.java#L17)、[getBlockByNumber](../src/main/java/link/luyu/protocol/application/SDK.java#L19)
  * 参数
    * 区块（[Block](../src/main/java/link/luyu/protocol/network/Block.java)）：区块结构的统一抽象
* 列举资源
  * 操作：[listResources](../src/main/java/link/luyu/protocol/application/SDK.java#L23)
  * 参数
    * 资源（[Resource](../src/main/java/link/luyu/protocol/network/Resource.java)）：各种可信源的智能合约（链码）等可操作的统一抽象对象

### 3.4 统一接入协议

将各种可信源的接入协议进行抽象，以实现不同可信源的统一接入适配。不同可信源基于此协议开发插件，即可完成适配接入。本协议采用分层设计，以支持本地与远程的调用。

**定义**

* 驱动组件（[Driver](../src/main/java/link/luyu/protocol/link/Driver.java)）
  * 功能：定义统一的可信源操作抽象接口，各可信源基于此定制化具体实现逻辑，实现统一调用协议与实际可信源调用协议的转化（如交易组装，回执处理，区块解析，资源查询等）。调用连接组件向可信源发送数据。
  * 接口
    * sendTransaction
    * call
    * getTransactionReceipt
    * getBlockByHash、getBlockByNumber
    * listResources

* 连接组件（[Connection](../src/main/java/link/luyu/protocol/link/Connection.java)）
  * 可信源接入的抽象层，与实际可信源建立连接，将驱动组件的调用转发至可信源。
  * 借助远程接入组件 “`Connection(Remote)`” 实现远程调用。

## 四、跨链互信

跨链互信是跨链的基础，本协议将跨链的验证操作内置于链插件中，由链插件根据区块链的设计进行实现。如图所示，当对方链上的交易上链后，原始的交易存在证明会直接透传至发起方的Driver，在Driver中以链本身的原始逻辑进行验证。该设计需在发起方的Driver处根据具体链类型事先配置相关验证数据，但从某种程度上简化了插件开发难度，对开发者友好。

![](img/verify.svg)



## 五、调用流程

### 5.1 单Router调用

sendTransaction操作经过层层处理，最后调用至区块链上。



![](img/module1.svg)

流程见下图。需注意的是，区块头同步是异步的，在多个交易并发发送并被打包到一个区块中后，只需同步一次区块头即可。

![](img/flow1.svg)

### 5.2 跨Router调用

sendTransaction操作经过层层处理，最后调用至另一个Router连接的区块链上。其中发起方Router的链路层的Connection是Remote的，该模块是Connection的抽象映射，负责转发至相应Router的Connection，接口与一般的Connection完全相同。



![](img/module2.svg)

流程基本与单Router调用相同，区别在于多了远程的Connection模块，该模块是远端Connection的抽象映射，直接透传参数，不做任何处理。



![](img/flow2.svg)

### 5.3 链发起调用

由区块链的SDK发起，调用至区块链A上。区块链A上部署了接收跨链调用请求的合约，合约通过事件机制回调至插件，插件将调用参数和调用者的链身份以事件的形式通知至Router Manager。Router Manager从Account Manager处查询到对应链的私钥后，调用相应的插件将跨链交易发送出去。



![](img/module3.svg)

流程如下：

![](img/flow3.svg)

