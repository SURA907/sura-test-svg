# 陆羽协议

详细协议描述见：[DESCRIPTION](./doc/description.md)

插件开发Demo见：[HelloPlugin](https://gitee.com/luyu-community/luyu-protocol/tree/develop/src/test/java/org/luyu/protocol/link)

## 引用方式

### maven

以`gradle`为例

``` gradle
dependencies { implementation 'xxx.xxxx:luyu-protocol:1.x.x'}
```

### 本地编译

在目录下

``` bash
bash gradlew assemble
```

生成的目录`dist/apps`下

``` 
luyu-protocol-xxxxxx.jar
```

将其拷贝至对应项目的classpath下，并引用此jar包即可，以`gradle`为例

``` gradle
dependencies { compile files('lib/luyu-protocol-xxxxxx.jar')}
```

## License

开源协议为Apache License 2.0，详情参考[LICENSE](./LICENSE)。

