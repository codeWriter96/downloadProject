
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basepath = request.getScheme() + "://"
            + request.getServerName() +
            ":" + request.getServerPort() + request.getContextPath() + "/";
%>
<html>

<head>
    <meta charset="UTF-8">
    <base href="<%=basepath%>">

    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />
    <script type="text/javascript" src="jquery/jquery-3.4.1.min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <title>多线程下载器</title>

    <script type="text/javascript">
        $(function () {
            $("#create-webPath").focus();
            loadValidateCode();
            submit();
            reset();
            getPath();
            $("textarea").val("").blur(function () {
                trimSpace(this)
            });
            $(":text").val("").blur(function () {
                trimSpace(this)
            });
            $("#validateCodeImage").click(function () {
                loadValidateCode();
            });

        });

        function getPath() {
            $("#create-locPath").click(function () {
                $.ajax({
                    url:"download/getPath.do",
                    dataType:"json",
                    type:"get",
                    success:function (data) {
                        $("#create-locPath").val(data.path)
                    }
                })
            })
        }

        function submit() {
            $("#submitBtn").click(function () {
                $.ajax({
                    url:"download/getURL.do",
                    data:{
                        "webPath":$("#create-webPath").val(),
                        "locPath":$("#create-locPath").val(),
                        "validateCode":$("#validateCode").val()
                    },
                    dataType:"json",
                    type:"post",
                    success:function (data) {
                        if(data.success){
                            startDownload();
                            $("#downloadModal").show();
                            alert("网站解析成功，开始下载，请勿关闭页面");
                        }else {
                            alert(data.msg);
                        }
                    }
                })
            })
        }
        function startDownload() {
            $.ajax({
                url:"download/startDownload.do",
                dataType:"json",
                type:"get",
                success:function (data) {
                    if(data.success){
                        $("#downloadModal").hide();
                        alert(data.msg);
                    }else {
                        alert(data.msg);
                    }
                }
            });
        }
        function reset() {
            $("#resetBtn").click(function () {
                $("#create-webPath").val("");
                $("#create-locPath").val("");
                $("#validateCode").val("")
            })
        }

        function trimSpace(obj) {
            $(obj).val($.trim($(obj).val()))
        }


        // 加载验证码
        function loadValidateCode() {
            var time = new Date().getTime();
            //id的作用是防止浏览器缓存
            $("#validateCodeImage").prop('src','download/getValidateCode.do?id='+time+'')
        }


    </script>
</head>
<body>



<div style="position: fixed;">
    <img src="png/login-bj.png" style="position: fixed;bottom: 0;z-index: 0">
    <div style="position: relative; top: 120px;left :80%;width:600px;height:400px;border:1px solid #D5D5D5;z-index: 1;background-color: white">
        <div style="position: absolute; top: -10px; left: 60px;">
            <div class="page-header">
                <h3 title="下载网页地址">下载网页地址：</h3>
            </div>
            <div style="position: relative; top: -20px; left: 0px;">
                <textarea id="create-webPath" style="width: 480px;height: 50px;resize: none;" placeholder="请粘贴网页地址在此"></textarea>
            </div>
            <div class="page-header" style="position: relative; top: -40px; left: 0px;">
                <h3 title="文件下载位置">文件下载位置：</h3>
            </div>
            <div style="position: relative; top: -60px; left: 0px;">
                <input type="text" id="create-locPath" style="width: 480px;height: 50px;resize: none;" placeholder="单击选择文件保存地址" readonly>
            </div >
            <div style="position: relative; top: -35px; left: 30px;">
                <input id="validateCode" type="text" placeholder="验证码" value="" style="width: 200px;height: 50px;" maxlength="4">
                <div style="position: relative; top: -53px; left: 230px;">
                    <img id="validateCodeImage" src="" style="width: 150px;height: 50px;" title="验证码看不清？换一张！">
                </div>
            </div >

            <div style="position: relative; top: -70px; left: 120px;">
                <button id ="submitBtn" type="button"  class="btn btn-primary btn-lg btn-block" style="width: 100px;height: 50px; position: relative;top: 1px;" title="提交">提交</button>
                <div style="position: relative; top: -50px; left: 140px;">
                    <button id = "resetBtn" type="button" class="btn btn-primary btn-lg btn-block" style="width: 100px;height: 50px; position: relative;top: 1px;" title="重置">重置</button>
                </div>

            </div>

        </div>


    </div>
    <div id="downloadModal" hidden  style="position: relative;left:1090px;bottom:280px;width:375px;height:400px;border:1px solid #D5D5D5;background-color: white;z-index: 1" >
        <img src="png/download.gif">
        <h4 title="正在下载中，请稍后.." style="position: relative;left: 30px">正在下载中，请勿刷新或关闭页面，请稍后......</h4>
    </div>

</div>



</body>
</html>