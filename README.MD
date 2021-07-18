#1. 简介

本项目是一个jar文件扫描器，可以支持插件化订制不同的扫描逻辑

和别的项目扫描器不同，很多插件扫描是`java` 文件，比如在编译期执行的`checkstyle` 插件等，而
`gamma`扫描的是加载进jvm中的`class`文件

## 1.1 为什么不去扫描java文件

对于语法检查等`checkstyle`插件确实直接扫描`java`文件是更好的选择，但是扫描`java`文件意味着解析
难度变大，需要去解析对应字符串到底是属于什么包

如：我想去收集某个项目中有多少个方法标记了 `springMVC`中的注解`@RequestMapping`， 同时把这个注解中的入参给统计一下

上述需求如果直接去解析`java`文件就会很麻烦，因为虽然匹配上了字符串`@RequestMapping`但还需要去判断他的包名是否是属于
`spring`, 还需要去解析对应注解中的参数，如果注解中使用了`常量`,还需要解析对应的常量类，然后才能获取真正的值

## 1.2 为什么不去直接扫描class文件

通过一些字节码工具，如`asm`可以直接去解析`class文件`，这样确实可以解决上面提到扫描`java`文件中常量和类全称的问题，
但是有一定的门槛，需要去学习`字节码`的相关知识和对应的操作`框架`



## 1.3 gamma的优势

`gamma`出现就是解决上面2个问题

如果我可以把这个`jar`文件完全给装载进入`jvm`中，从`jvm`中获取到我所有想扫描的`class`对象， 然后通过反射去获取是否存在`@RequestMapping`注解，不就
能减少很多的工作量。 

对应开发插件的人来说，门槛就仅仅只需要会使用反射即可

所以 gamma的优势 如下：

- 极低的门槛让你自定义扫描逻辑
- 插件式的方式自定义处理逻辑

## 1.4 实际案例




#2. 架构设计

上面简介中提到过，`gamma`的工作原理是将会 把`要扫描的jar`完全加载进入到`jvm`中，然后`处理器`将会依次获得对应的`class对象`
，处理器将可以使用反射的方式去处理获得的`class对象`（如：使用反射来获取对应`类`/`方法`上面是否存在`@RequestMapping`注解）

这里的`处理器`，也就是需要根据实际业务去编写的`gamma插件`了。

## 2.1 类加载器

上面提到过，`gamma`将会把要扫描的`jar`完全加载进入`jvm`中，那么类加载器的设计就需要满足以下几点

- 每个插件之间类加载器相互隔离
- 每个插件都能够获取到被加载进`jvm`的`扫描Jar`中的class对象
 
 ![p1](/img/readme1.png)
 
## 2.2 jar包的解析

这里`jar`包的解析分成2种情况

1. `spring boot` 的jar
    - `springBoot`打出的jar格式和普通的jar又一定的区别，所以需要先去获得spring提供的一个`类加载器`，然后才能通过这个`类加载器`去获得对应业务`class`

2. 普通的java jar
    - 普通的jar如果引入了其他第三方依赖，那么打出来的jar中将会依赖和业务class都混合在一起，可以使用参数 `scan.package`来指定你想扫描的class路径，而不需要去扫描框架引入的class
    - 对应`springBoot`的jar来说，因为特殊的jar结构，扫描的将全部都是业务class
 
 
 
__注意__: 
 
# 3.快速使用

在本项目的`/dist`文件夹中下载作者编译好的文件 `gamma-bootstrap.jar`， 在`gamma-bootstrap.jar`文件同级目录创建文件夹`plugins`
在`plugins`文件夹中创建`子目录`作为插件的名称, 然后把对应的`插件jar`放入子目录中即可(插件的编写请看`4.1`章节)

最简结构 如下图所示：

![p1](/img/readme2.png)
 
然后输入命令
```shell
  java -jar gamma-bootstrap.jar source=你要扫描的jar路径
``` 
  
# 4. 插件

`gamma`仅仅只是一个扫描引擎，当扫描到对应的`class`要如何处理， 需要编写`插件`来实现

## 4.1 自定义插件

- clone本仓库，进入`gamma-common`目录执行maven命令来按照对应的依赖
```shell script
mvn clean install
```


- 创建一个新的maven项目
- 在新的插件项目中引入依赖
```xml
<dependency>
  <groupId>com.chy</groupId>
  <artifactId>gamma-common</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
``` 
- 创建一个类，实现接口
```java
/**
 * gamma插件的执行接口
 * 泛型T 为插件需要的配置文件类，如：本插件在配置文件config.properties 中设置了属性 commitId=1234 、 ref=master
 * 那么泛型T需要定义 一个实体类来接收这2个属性
 * 如果没有任何的属性对象，这里泛型可以填入 Void
 * 
 * 
 * @param <T>
 */
public interface Processor<T> {

    /**
     *  配置对象的接收，gamma将会根据配置文件以及jvm参数来生成对应的配置对象
     *  如果泛型T 设置是Object那么这里注入的将会是一个HashMap对象
     * @param t
     */
    void setProperty(T t);

    /**
     * 处理的核心接口
     * gamma每扫描到jar中的一个class都会回调该接口
     * 
     * @param originClass 被扫描jar中的某一个class
     */
    void processor(Class originClass);

    /**
     * 当全部扫描完后回调
     *
     */
    void finishProcessor();

}
```
- 在插件的`META-INF`目录(如果没有就在`resources`目录下先创建`META-INF`目录)下创建文件`gamma.plugin`文件，里面写上对应你插件`Processor`
接口实现类的全路径
- 执行命令打包成一个插件
```shell script
mvn clean package
```

__注意__: 打出的插件`jar`需要把对应的第三方依赖也一起打包进插件`jar`中，可以使用以下配置来完成对应的操作

添加对应的maven插件
```shell script
 <plugin>
    <artifactId>maven-assembly-plugin</artifactId>
    <executions>
        <execution>
            <!-- 绑定到package生命周期 -->
            <phase>package</phase>
            <goals>
                <goal>single</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <descriptorRefs>
            <!-- 将依赖一起打包到 JAR -->
            <descriptorRef>jar-with-dependencies</descriptorRef>
        </descriptorRefs>
        <appendAssemblyId>false</appendAssemblyId>
    </configuration>
</plugin>
```

# 5 配置

在`gamma`中配置分成`2类`
- `gamma`核心配置
    - 该配置用于控制gamma的行为，有固定的参数
- `插件`配置
    - 由插件开发者自己定义的参数配置，每一个插件都可能不相同
    
同时不管`gamma核心配置`还是`插件配置`都有2种设置方式
- 通过`program arguments`方式在`gamma`启动的时候设置如：
```shell script
java -jar gamma-bootstrap.jar source=你要扫描的jar路径
```
这里的 `source`就是通过`program arguments`设置进入的参数

如果要设置插件的`专属参数`, 那么在参数前面需要带上插件的名称如：
```shell script
java -jar gamma-bootstrap.jar source=你要扫描的jar路径 mypl1:url=123
```
这里的`mypl1:url`代表的就是给插件`mypl1`设置参数`url`

- 通过`配置文件`方式设置

创建文件 `config.properties` 来设置对应的配置

`config.properties`文件位置的不同所具有的含义也不同,如下图所示

 ![p3](/img/readme3.png)


__注意__: 通过`program arguments`设置的参数优先级高于配置文件的方式

## 5.1 gamma核心配置

- `source`: 指定要扫描的jar包的位置
- `scan.package`: 指定要扫描的包名前缀，不设置将会扫描所有，多个路径可以用`逗号`分隔


## 5.2 插件配置

上面提到了插件如何去设置自己的参数，那么设置了之后，在插件中如何去读到对应的参数嗯？

从上面的`Processor<T>`接口可以看出，需要填入一个泛型`T`，这个泛型`T`就是插件自定义的配置类,
当插件被`gamma`加载的时候，将会回调对应的实现方法`void setProperty(T t)`来传递生成的配置对象

当然这个配置对象也是有部分规则的，需要在需要注入的配置字段上面打上注解`@com.chy.gamma.common.profile.Param`
如：
```java
@Data
public class ApiScanProperty {

    @Param
    private String ref;
    @Param
    private String commitId;
    @Param(nullable = true)
    private String appName;
    @Param("endpoint.topology.host")
    private String host;
}
```
那么对应配置文件中的设置为：
```properties
endpoint.topology.host=http://127.0.0.1:3222
ref=master
commitId=23123131
appName=chyapp
```

或者是：
```shell script
java -jar gamma-bootstrap.jar source=你要扫描的jar路径 插件名称:ref=master 插件名称:endpoint.topology.host=http://127.0.0.1:3222 插件名称:commitId=23123131 插件名称:appName=chyapp
```

同时注解`@Param`还有一个参数`nullable`来控制是否可以缺省某个值，默认是`false`，及如果没有传入
对应的值将会抛出异常

# 6. 日志

`gamma`使用的日志框架是 `log4j2`框架，虽然对应插件的类加载器相互独立了，但是`双亲委派`机制的原因
正常去获取到的`logger`对象 对于所有`插件`来说是同一个对象，这样所有的日志将会混杂到一起

为了能做到日志之间的相互隔离  在插件中请使用以下代码来获取`logger`对象
```java
import com.chy.gamma.common.utils.LogUtils;
public static Logger logger = LogUtils.getLogger("类路径");
```

同时每个插件都可以配置自己的`log4j2`文件来自定义输出

 ![p4](/img/readme4.png)




