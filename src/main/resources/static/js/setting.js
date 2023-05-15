$(function () {
    $("#uploadForm").submit(upload);
});

function upload() {
    $.ajax({
        url: "http://upload-cn-east-2.qiniup.com",
        method: "post",
        processData: false, // 不要把数据转化为字符串
        contentType: false,  // 不让JQuery自动设定类型，让浏览器自动生成
        data: new FormData($("#uploadForm")[0]),
        success: function (data) {
            if (data && data.code == 0) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function (data) {
                        data = $.parseJSON(data);
                        if (data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                )
            } else {
                alert("上传失败！")
            }
        }
    });
    return false; // 事件到此为止，之前已经提交完成了，如果为true就仍然会尝试提交
}