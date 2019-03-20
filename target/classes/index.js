$(document).ready(function () {
    // 建立连接
    var ipAddress = "127.0.0.1";
    var webSocket = new WebSocket("ws://" + ipAddress + ":8080/nettychat");

    // 监听服务器消息
    webSocket.onmessage = function (event) {

        var re = JSON.parse(event.data);

        if (re.result == "success") {
            console.log(re.user.username);
            sessionStorage.setItem("username", re.user.username);
            sessionStorage.setItem("level", re.user.level);
            sessionStorage.setItem("email", re.user.email);
            sessionStorage.setItem("id", re.user.id);
            sessionStorage.setItem("gender", re.user.gender);
            webSocket.close();
            alert("登录成功");
            window.location.href = "chat.html";
        }
        if (re.result == "false") {
            alert("用户名或密码错误或用户已登录");
        }
    };

    $("#login").click(function () {
        login();
    });

    $("#register").click(function () {
        toRegister();
    });

    // 提交登录信息
    function login() {
        var username = $("#username").val();
        var password = $("#password").val();
        if (username == "" || password == "") {
            alert("用户名或密码不可为空");
        }
        else {
            var msg = JSON.stringify({op: "login", username: username, password: password});
            webSocket.send(msg);
        }
    }

    // 跳转注册页面
    function toRegister() {
        webSocket.close();
        window.location.href = "register.html";
    }
});
