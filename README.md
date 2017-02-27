使用说明：


此组件目的为了解决C++与java通信过程中结构体在java解析困难。
使得顺序读取结构体、顺序往结构体写入数据、修改结构协议代码的变得可配置、容易。

读取C结构体为json例子：
```java 
@HandleMessage(Type=HandleType.RECEIVE)
public class HandleConfirmMessage implements HandleMessageService {
@Override 
// 接收协议id，为xml具体协议的ID属性
@HandleMessageProtocol(id="1")
public void handle(JSONObject message) {
    System.out.println("ACK->" + message);
}
}   
```
写入json到C结构体例子：
```java
 SendMessageService sendService = new SendMessageServiceImpl();
 JSONObject message = new JSONObject(); 
 message.put("Gateway_Id", 1234);  
 message.put("Package_Number", 25);
 message.put("command_properties", 0x01);
 message.put("BCC", 0x01);
 //发送协议id，为xml具体协议的ID属性
 sendService.send("1", message); 
 ```

接收数据协议的FC.xml配置： 
```java
<!-- 实现解析类的基本路径 --> 
<base_package src="com.sgck.dtu.analysis.read.handle"/>
<!-- 以下每个协议都有包头和包尾在解析的时候，会自动加上 -->
<!-- 包头，通过包头指定的 primaryKey解析每次通信所属协议,也称作每次通信的公共部分-->
<head primaryKey="Package_Type"> 
<Constant_Up type="ushort"/><!--  2字节，前导字符，固定为0XAAAA，表示为上行数据包-->
<Gateway_Id type="unsigendint"/><!-- 4字节，网关id号 --> 
<Package_Type type="char"/><!-- 具体根据每个协议ID决定 -->
</head>
<!-- 包尾可以没有 --> 
<foot>
<BCC type="char"/><!-- 1个字节，前面所有数据含前导，异或运算 -->
<Constant_Up_Stop type="ushort"/><!-- 2字节，固定为0XAA55，表示为上行数据包结束 -->
</foot>

<!-- 通讯确认 协议号=0x01-->
<Gateway_Server name="ACK" id="1" check="length">
<Version type="char"/><!-- 1字节,网关软件版本 -->
<Package_length type="ushort"/><!--2字节数据包长度，单位字节，从Package_length（包含）至BCC（包含） -->
<Package_Number type="ushort"/><!-- 2字节，包序号，copy接收到的上行数据包包序号 -->
<command_properties type="char"/><!-- 1字节，
=0x01,表示传感节点测试通信
=0x02，数据接收正确
=0x03，数据接收错误，要求重发
=0x04，数据接收错误，不要求重发
=0X05，终止传输（多包传输时使用）。
=0X06，接收正确，命令无法执行。
 -->
</Gateway_Server>
 ```
 发送数据给C++协议的TC.xml配置：
 ```java
 <!-- 包头，通过包头指定的 primaryKey解析每次通信所属协议,也称作每次通信的公共部分-->
<head primaryKey="Package_Type">
<Constant_Up type="ushort" default="0x5555"/><!--  2字节，前导字符，固定为0X5555，表示为上行数据包-->
<Gateway_Id type="unsigendint"/><!-- 4字节，网关id号 -->
</head>

<!-- 包尾可以没有 -->
<foot>
<BCC type="char"/><!-- 1个字节，前面所有数据含前导，异或运算 -->
<Constant_Up_Stop type="ushort" default="0x55AA"/><!-- 2字节，固定为0X55AA，表示为上行数据包结束 -->
</foot>

<!-- 通讯确认-->
<Server_Gateway name="ACK" id="1">
<Package_Type type="char" default="0x01"/><!-- 1字节，协议号=0x01 -->
<Package_length type="ushort" default="0x06"/><!--2字节数据包长度，单位字节，从Package_length（包含）至BCC（包含） -->
<Package_Number type="ushort"/><!-- 2字节，包序号，copy接收到的上行数据包包序号 -->
<command_properties type="char"/><!-- 1字节，
=0x01,表示传感节点测试通信
=0x02，数据接收正确
=0x03，数据接收错误，要求重发
=0x04，数据接收错误，不要求重发
=0X05，终止传输（多包传输时使用）。
=0X06，接收正确，命令无法执行。
 -->
</Server_Gateway>
 ```
