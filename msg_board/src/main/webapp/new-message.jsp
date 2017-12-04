<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <title>New message</title>

    <!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">

    <!-- Custom fonts for this template -->
    <link href="vendor/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css">
    <link href='https://fonts.googleapis.com/css?family=Lora:400,700,400italic,700italic' rel='stylesheet'
          type='text/css'>
    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800'
          rel='stylesheet' type='text/css'>

    <!-- Custom styles for this template -->
    <link rel="stylesheet" href="css/clean-login.css">
</head>
<body>
<!--<header>New message</header>
<form action="/board" id="form" method="post">
    <input id="name" name="title" type="text" placeholder="TITLE">
    <textarea id="message" name="text" placeholder="MESSAGE"></textarea>
    <input id="submit" type="submit" value="Submit">
</form>-->

<div class="container">
    <div class="card card-container">
        <form action="/board" method="POST" class="form-signin">
            <input type="text" name="title" id="inputTitle"  class="form-control" placeholder="Title" required
                   autofocus>
            <textarea type="text" name="text" id="inputText"  class="form-control" placeholder="Type message here"
                   required></textarea>
            <button type="submit" class="btn btn-lg btn-primary btn-block btn-signin">POST</button>
        </form>
    </div>
</div>

</body>
</html>
