# netty-chat
采用websocket协议的实现了点对点，广播推送的聊天室项目。

服务端基于netty,客户端基于js+html。

## 服务端
服务端打包后的可执行jar为```out\artifacts\netty_chat_jar```下的```netty-chat.jar```。

运行命令为```java -jar netty-chat.jar```。

服务器启动后默认监听8080端口，也可以在命令中加入端口号例如```java -jar netty-chat.jar 8888```。

默认创建两个账号:

用户名 | 密码 | 类型 | 功能
--- | --- | ---- | ---
root | root | 管理员 | 可以向任意用户点对点发送消息
hyc | hyc | 普通用户 | 可以向所有用户群发消息


## 客户端
客户端直接打开```src\main\resources```下的```html```文件即可
- ```chat.html```聊天页
- ```index.html```登录页
- ```register.html```注册页

