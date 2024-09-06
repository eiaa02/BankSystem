// src/login/UserLogin.java
package login;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserLogin {
    public static int login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入账户名称: ");
        String accountName = scanner.nextLine();

        System.out.print("请输入密码: ");
        String password = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT u.user_id, u.password_hash, a.status " +
                    "FROM users u " +
                    "JOIN accounts a ON u.user_id = a.user_id " +
                    "WHERE u.account_name = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, accountName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String passwordHash = rs.getString("password_hash");
                String accountStatus = rs.getString("status");

                if (password.equals(passwordHash)) { // 比对密码（请根据实际情况调整）
                    if ("Closed".equals(accountStatus)) {
                        System.out.println("账户已销户，无法登录！");
                        return -1;
                    } else {
                        int userId = rs.getInt("user_id");
                        System.out.println("登录成功！");
                        return userId;
                    }
                } else {
                    System.out.println("密码错误！");
                    return -1;
                }
            } else {
                System.out.println("账户不存在！");
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
