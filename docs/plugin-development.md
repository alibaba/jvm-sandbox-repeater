# 插件开发手册

> 框架封装了基础录制回放协议，对于普通插件开发可以非常简单的完成，下面我们来一起开发一个插件

## 快速开发一个mybatis插件

### 第一步 在repeater-plugins下创建一个maven-module

repeater-plugin提供了基础依赖信息，新建的mybatis-plugin的pom.xml如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>repeater-plugins</artifactId>
        <groupId>com.alibaba.jvm.sandbox</groupId>
        <version>1.0.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>mybatis-plugin</artifactId>

    <build>
        <finalName>${project.name}-${project.version}</finalName>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-assembly-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>attached</goal>
                        </goals>
                        <phase>package</phase>
                        <configuration>
                            <descriptorRefs>
                                <descriptorRef>jar-with-dependencies</descriptorRef>
                            </descriptorRefs>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```

增加了build插件，如果插件里面有依赖，需要把依赖的jar打包进去

### 第二步 实现`InvokePlugin`的SPI

repeater-module通过SPI方式加载插件，所有插件需要实现`InvokePlugin`并按标准SPI协议配置，框架抽象了基础插件信息，可以直接继承`AbstractInvokePluginAdapter`即可，插件代码如下：

```java

@MetaInfServices(InvokePlugin.class)
public class MybatisPlugin extends AbstractInvokePluginAdapter {

    @Override
    protected List<EnhanceModel> getEnhanceModels() {
        EnhanceModel em = EnhanceModel.builder()
                .classPattern("org.apache.ibatis.binding.MapperMethod")
                .methodPatterns(EnhanceModel.MethodPattern.transform("execute"))
                .watchTypes(Type.BEFORE, Type.RETURN, Type.THROWS)
                .build();
        return Lists.newArrayList(em);
    }

    @Override
    protected InvocationProcessor getInvocationProcessor() {
        return new MybatisProcessor(getType());
    }

    @Override
    public InvokeType getType() {
        return InvokeType.MYBATIS;
    }

    @Override
    public String identity() {
        return "mybatis";
    }

    @Override
    public boolean isEntrance() {
        return false;
    }
}

```

> 这里面比较难的点在EnhanceModel的构建，需要开发者自己找到对应框架最合适的埋点，Demo中mybatis框架通过拦截
`org.apache.ibatis.binding.MapperMethod#execute`来录制和mock回放

### 第三步 实现`InvocationProcessor`接口处理调用

InvocationProcessor负责组装请求入参、调用唯一标志`Identity`等系统操作，是每个插件需要实现的核心接口，也提供了基础的默认实现`DefaultInvocationProcessor`，满足大部分的拦截场景，插件只需要继承它即可：

```java
class MybatisProcessor extends DefaultInvocationProcessor {

    MybatisProcessor(InvokeType type) {
        super(type);
    }

    @Override
    public Identity assembleIdentity(BeforeEvent event) {
        Object mapperMethod = event.target;
        // SqlCommand = MapperMethod.command
        Field field = FieldUtils.getDeclaredField(mapperMethod.getClass(), "command", true);
        if (field == null) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
        try {
            Object command = field.get(mapperMethod);
            Object name = MethodUtils.invokeMethod(command, "getName");
            Object type = MethodUtils.invokeMethod(command, "getType");
            return new Identity(InvokeType.MYBATIS.name(), type.toString(), name.toString(), new HashMap<String, String>(1));
        } catch (Exception e) {
            return new Identity(InvokeType.MYBATIS.name(), "Unknown", "Unknown", new HashMap<String, String>(1));
        }
    }

    @Override
    public Object[] assembleRequest(BeforeEvent event) {
        // MapperMethod#execute(SqlSession sqlSession, Object[] args)
        // args可能存在不可序序列化异常（例如使用tk.mybatis)
        return new Object[]{event.argumentArray[1]};
    }
}
```

> 这里插件重写了`assembleRequest`和`assembleIdentity`两个方法，因为在执行`execute`的时候，真正与业务相关的参数是在`args`里面


到这里整个插件的编码过程已经完成，非常简单，甚至很多的插件可以通过配置`java-plugin`的增强埋点即可实现(eg:`ibatis-plugin`)，适配插件成本只在**寻找最合适的插桩埋点**。

### 第四步 打包运行

- 编译打包
- 插件复制到 `~/.sandbox-module/plugins/`
- 修改`repeater.json`，允许`mybatis`插件启动
- attach sandbox 开始使用

### 第五步 实现流量回放器`Repeater`

> 可选。只有入口类型的插件需要实现，用来发起一次回放调用，流量回放可参考[http-plugin](/repeater-plugins/http-plugin)和[java-plugin](/repeater-plugins/java-plugin)的实现


## 已支持的插件列表

|    				      	插件类型     		            | 录制   |  回放  | Mock  | 支持时间 |                  贡献者                    |
| -----------------------------------------------   | ----- | :---: | :---: | :-----: |   :----------------------------------:    |
| [http-plugin](/repeater-plugins/http-plugin)       |   √   |   √   |   ×   | 201906  |[zhaoyb1990](https://github.com/zhaoyb1990)|
| [dubbo-plugin](/repeater-plugins/dubbo-plugin)     |   √   |   ×   |   √   | 201906  |[zhaoyb1990](https://github.com/zhaoyb1990)|
| [ibatis-plugin](/repeater-plugins/ibatis-plugin)   |   √   |   ×   |   √   | 201906  |[zhaoyb1990](https://github.com/zhaoyb1990)|
| [mybatis-plugin](/repeater-plugins/mybatis-plugin) |   √   |   ×   |   √   | 201906  |[ztbsuper](https://github.com/ztbsuper)    |
| [java-plugin](/repeater-plugins/java-plugin)       |   √   |   √   |   √   | 201906  |[zhaoyb1990](https://github.com/zhaoyb1990)|
| [redis-plugin](/repeater-plugins/redis-plugin)     |   ×   |   ×   |   ×   | 预期7月底|                      NA/NA                |


期待大家贡献更多的插件~



