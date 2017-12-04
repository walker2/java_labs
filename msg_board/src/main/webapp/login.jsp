<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>

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
<div class="container">
    <div class="card card-container">
        <img id="profile-img" class="profile-img-card" src="//ssl.gstatic.com/accounts/ui/avatar_2x.png"/>
        <p id="profile-name" class="profile-name-card"></p>
        <form action="/login" method="POST" class="form-signin">
            <input type="text" name="name" id="inputName" class="form-control" placeholder="Username" required
                   autofocus>
            <input type="password" name="password" id="inputPassword" class="form-control" placeholder="Password"
                   required>
            <button type="submit" class="btn btn-lg btn-primary btn-block btn-signin">Login</button>
            <span>${error}</span>
        </form>
    </div>
</div>
</body>
</html>
