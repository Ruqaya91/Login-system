import java.sql.*;
import java.util.Scanner;
import java.security.MessageDigest;

public class LoginSystem {

    public static void main(String[] args) {
        String url = "jdbc:sqlite:users.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                createTable(conn);
                Scanner sc = new Scanner(System.in);
                while (true) {
                    System.out.println("\n1. Register\n2. Login\n3. Exit");
                    System.out.print("Select option: ");
                    String choice = sc.nextLine();
                    if (choice.equals("1")) {
                        register(conn, sc);
                    } else if (choice.equals("2")) {
                        login(conn, sc);
                    } else if (choice.equals("3")) {
                        break;
                    } else {
                        System.out.println("Invalid option.");
                    }
                }
                sc.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "username TEXT UNIQUE,"
                   + "password TEXT"
                   + ");";
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }

    private static void register(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        String hashed_pw = hashPassword(password);

        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashed_pw);
            pstmt.executeUpdate();
            System.out.println("Registration successful!");
        } catch (SQLException e) {
            System.out.println("Username already exists.");
        }
    }

    private static void login(Connection conn, Scanner sc) throws SQLException {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        String hashed_pw = hashPassword(password);

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashed_pw);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful!");
            } else {
                System.out.println("Invalid username or password.");
            }
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
