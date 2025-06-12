CREATE DATABASE user_db;

USE user_db;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);
PHP Code (login_register.php)
<?php
$servername = "localhost";
$username_db = "root";
$password_db = "";  // your DB password
$dbname = "user_db";

// Create connection
$conn = new mysqli($servername, $username_db, $password_db, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

function register($conn) {
    $username = readline("Enter username: ");
    $password = readline("Enter password: ");
    $hashed_pw = password_hash($password, PASSWORD_DEFAULT);
    
    $stmt = $conn->prepare("INSERT INTO users (username, password) VALUES (?, ?)");
    $stmt->bind_param("ss", $username, $hashed_pw);
    
    if ($stmt->execute()) {
        echo "Registration successful!\n";
    } else {
        echo "Error: " . $stmt->error . "\n";
    }
}

function login($conn) {
    $username = readline("Enter username: ");
    $password = readline("Enter password: ");
    
    $stmt = $conn->prepare("SELECT password FROM users WHERE username=?");
    $stmt->bind_param("s", $username);
    $stmt->execute();
    $stmt->bind_result($hashed_pw);
    
    if ($stmt->fetch() && password_verify($password, $hashed_pw)) {
        echo "Login successful!\n";
    } else {
        echo "Invalid username or password.\n";
    }
}

while (true) {
    echo "\n1. Register\n2. Login\n3. Exit\nSelect option: ";
    $option = trim(fgets(STDIN));
    if ($option == '1') {
        register($conn);
    } elseif ($option == '2') {
        login($conn);
    } elseif ($option == '3') {
        break;
    } else {
        echo "Invalid option.\n";
    }
}

$conn->close();
?>
