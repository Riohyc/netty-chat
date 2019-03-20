$(document).ready(function () {

    // 账户登录检验
    if (sessionStorage.getItem("id") == null) {
        window.location.href = "index.html";
    }
    else {
        $("#username").val(sessionStorage.getItem("username"));
        $("#email").val(sessionStorage.getItem("email"));
        $("#id").val(sessionStorage.getItem("id"));
        $("#gender").val(sessionStorage.getItem("gender"));
        $("#level").val(sessionStorage.getItem("level"));
    }

    // 建立与服务器连接
    var ipAddress = "127.0.0.1";
    var webSocket = new WebSocket("ws://" + ipAddress + ":8080/nettychat");

    // 更新通道及用户名单
    webSocket.onopen = function (ev) {
        updateChannel();
        getAllOnlineUser();
    };

    // 监听服务器消息
    webSocket.onmessage = function (event) {

        var re = JSON.parse(event.data);

        // 用户信息更新响应
        if (re.op == "update") {
            if (re.result == "success") {
                alert("更新成功");
                sessionStorage.setItem("username", re.user.username);
                sessionStorage.setItem("level", re.user.level);
                sessionStorage.setItem("email", re.user.email);
                sessionStorage.setItem("id", re.user.id);
                sessionStorage.setItem("gender", re.user.gender);
                $("#username").val(sessionStorage.getItem("username"));
                $("#email").val(sessionStorage.getItem("email"));
                $("#id").val(sessionStorage.getItem("id"));
                $("#gender").val(sessionStorage.getItem("gender"));
                $("#level").val(sessionStorage.getItem("level"));
            }
            if (re.result == "false") {
                alert("更新失败")
            }
        }

        // 用户名单响应
        if (re.op == "getAllUser") {
            $("#userList").html("");
            $("#receivers").html("");
            if (sessionStorage.getItem("level") == 0) {
                $("#receivers").html(function (i, ori) {
                    return ori + "<option value='all'>All User</option>";

                });
            }
            for (var i = 0, len = re.onlineUsers.length; i < len; i++) {
                var id = re.onlineUsers[i].id;
                var username = re.onlineUsers[i].username;
                $("#userList").html(function (i, ori) {
                    return ori + "<li class='list-group-item'>(" + id + ") " + username + "</li>";
                });
                $("#receivers").html(function (i, ori) {
                    return ori + "<option value=" + username + ">" + username + "</option>";
                });
            }
        }

        // 新消息响应
        if (re.op == "msg") {
            var date = new Date().toLocaleTimeString();
            var newMsg = date + " From:" + re.sender + "\n" + re.msg + "\n";
            $("#msgWindow").val(function (i, ori) {
                return ori + newMsg;
            })
        }

    };

    // 发送用户更新后的信息
    function sendUpdate() {
        var username = $("#username").val();
        var password = $("#password").val();
        var email = $("#email").val();
        var gender = $("#gender").val();

        if (isEmail(email) && isNumAndAlpha(username) && isNumAndAlpha(password)) {
            var msg = JSON.stringify({
                op: "update",
                username: username,
                password: password,
                email: email,
                gender: gender
            });
            webSocket.send(msg);
        }
        else {
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

    $("#exit").click(function () {
        exit();
    });

    $("#update").click(function () {
        sendUpdate();
    });

    $("#send").click(function () {
        sendMessage();
    });

    function sendMessage() {
        var message = $("#msgInput").val();
        if (message == "") {
            alert("消息不能为空");
        }
        else {
            var receiver = document.getElementById("receivers").value;
            var username = sessionStorage.getItem("username");
            var msg = JSON.stringify({op: "msg", sender: username, receiver: receiver, msg: message});
            webSocket.send(msg);
            var date = new Date().toLocaleTimeString();
            var newMsg = date + " 我" + "\n" + message + "\n";
            $("#msgWindow").val(function (i, ori) {
                return ori + newMsg;
            })

        }
    }

    function getAllOnlineUser() {
        var msg = JSON.stringify({op: "getAllUser"});
        webSocket.send(msg);
    }

    function updateChannel() {
        var msg = JSON.stringify({op: "updateChannel", username: sessionStorage.getItem("username")});
        webSocket.send(msg);
    }

    function isNumAndAlpha(str) {
        var reg = /^[\w+]+$/;
        return reg.test(str);
    }

    function isEmail(str) {
        var reg = /^\w+@[a-zA-Z0-9]{2,10}(?:\.[a-z]{2,4}){1,3}$/;
        return reg.test(str);
    }

    function exit() {
        var msg = JSON.stringify({op: "exit"});
        webSocket.send(msg);
        webSocket.close();
        sessionStorage.clear();
        window.location.href = "index.html";
    }
});
