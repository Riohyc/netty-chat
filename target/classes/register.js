$(document).ready(function () {
    //建立与服务器连接
    var ipAddress = "127.0.0.1";
    var webSocket = new WebSocket("ws://" + ipAddress + ":8080/nettychat");

    webSocket.onmessage = function (event) {
        var re = JSON.parse(event.data);
        if (re.result == "success") {
            webSocket.close();
            alert("注册成功");
            window.location.href = "index.html"
        }
        if (re.result == "false") {
            alert("注册失败，用户名已存在")
        }
    };

    $("#register").click(function () {
        sendRegister();
    });

    // 发送注册信息
    function sendRegister() {
        //获得注册信息
        var username = document.getElementById("username").value;
        var password = document.getElementById("password").value;
        var email = document.getElementById("email").value;
        var gender = document.getElementById("gender").value;
        console.log(username);
        if (isEmail(email) && isNumAndAlpha(username) && isNumAndAlpha(password)) {
            var msg = JSON.stringify({
                op: "register",
                username: username,
                password: password,
                email: email,
                gender: gender
            });
            webSocket.send(msg);
        }
        else {
            console.log(username);
            console.log(password);
            console.log(email);
            if (!isNumAndAlpha(username)) {
                alert("用户名非法");
            }
            if (!isNumAndAlpha(password)) {
                alert("密码非法");
            }
            if (!isEmail(email)) {
                alert("电子邮箱非法");
            }
        }
    }

    function isNumAndAlpha(str) {
        var reg = /^[\w+]+$/;
        return reg.test(str);
    }

    function isEmail(str) {
        var reg = /^\w+@[a-zA-Z0-9]{2,10}(?:\.[a-z]{2,4}){1,3}$/;
        return reg.test(str);
    }

});
