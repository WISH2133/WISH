<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String basePath=request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
%>
<html>
<head>
    <link href="jquery/bootstrap_3.3.0/css/bootstrap.min.css" type="text/css" rel="stylesheet" />

    <script type="text/javascript" src="jquery/jquery-1.11.1-min.js"></script>
    <script type="text/javascript" src="jquery/bootstrap_3.3.0/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="jquery/bs_typeahead/bootstrap3-typeahead.min.js"></script>
    <base href="<%=basePath%>">
    <title>演示bs_typeahead插件</title>
    <script type="text/javascript">
        $(function () {
            //在来个数组存customerId
            var name2id={};
            $("#customerName").typeahead({
                    //source:['动力节点','字节跳动']
                //query用来接收查询的关键字
                    source:function(query,process) {
                        $.ajax({
                            url:'workbench/transaction/typeahead.do',
                            data:{
                                customerName:query
                            },
                            type:'post',
                            dataType:'json',
                            success:function(data){ //userList
                                 //alert(data);
                               //定义一个字符串数组
                                var customerNameArr=[];
                                $.each(data,function(index,obj) { //obj就是一个customer对象
                                   // alert(obj.name);
                                    customerNameArr.push(obj.name);
                                    name2id[obj.name]=obj.id; //name2id[动力节点]=001
                                });
                                //process()会将customerNameArr交给source去展现
                                process(customerNameArr);
                            }
                        });
                    },
                    afterSelect:function (item) { //动力节点
                        alert(name2id[item]); //001
                    }

                })
        });
    </script>
</head>
<body>
<input type="text" id="customerName">
</body>
</html>
