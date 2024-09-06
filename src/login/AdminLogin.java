package login;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AdminLogin {
    public static boolean login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入管理员用户名: ");
        String inputUsername = scanner.nextLine();

        System.out.print("请输入管理员密码: ");
        String inputPassword = scanner.nextLine();

        // 数据库查询管理员用户名和密码
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username, password FROM admin WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, inputUsername);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password");
                if (dbPassword.equals(inputPassword)) {
                    System.out.println("管理员登录成功！");
                    return true;
                } else {
                    System.out.println("密码错误，登录失败！");
                }
            } else {
                System.out.println("用户名不存在！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
