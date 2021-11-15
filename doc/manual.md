# 使用指引

架构如下，各组件为：

* **应用（[SDK](https://gitee.com/luyu-community/luyu-java-sdk)）**：应用通过对SDK发送交易，操作跨链网络。
* **账户服务（[Account Manager](https://gitee.com/luyu-community/account-manager)）**：管理统一账户信息，管理二级账户，用二级账户对交易进行签名。
* **跨链路由（[Router](https://gitee.com/luyu-community/router)）**：管理插件，转发请求。
  * **路由管理（Router Manager）**：路由总逻辑，调用账户服务，管理插件，调用插件 。
  * **插件（Plugin）**：各种可信源插件的实现，与可信源对接。
    * [Brochain Plugin](https://gitee.com/luyu-community/brochain-plugin)
    * [FISCO BCOS Plugin](https://gitee.com/luyu-community/fisco-bcos-plugin)
    * [CITA Plugin](https://gitee.com/luyu-community/cita-plugin)
    * [Hyperledger Fabric Plugin](https://gitee.com/luyu-community/fabric-plugin)
    * 。。。

![](D:/code/LuyuProtocol/doc/img/frame.svg)

## 操作步骤

1. 部署跨链路由（参考：[Router](https://gitee.com/luyu-community/router)）
2. 部署账户服务，并与路由相连（参考：[Account Manager](https://gitee.com/luyu-community/account-manager)）
3. 在跨链路由中配置链插件，配置插件与区块链相连（根据需求选择相应的链插件）
   * [Brochain](https://gitee.com/luyu-community/brochain-plugin)
   * [FISCO BCOS](https://gitee.com/luyu-community/fisco-bcos-plugin/blob/feature-luyu/docs/luyu.md)
   * [CITA](https://gitee.com/luyu-community/cita-plugin)
   * [Hyperledger Fabric](https://gitee.com/luyu-community/fabric-plugin/blob/feature-luyu/docs/luyu.md)
4. 启动跨链路由
5. 开发跨链应用，集成SDK，调用跨链网络（参考：[luyu-java-sdk](https://gitee.com/luyu-community/luyu-java-sdk)）

