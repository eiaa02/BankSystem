package login;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StaffLogin {
    public static int login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入工作人员用户名: ");
        String inputUsername = scanner.nextLine();

        System.out.print("请输入工作人员密码: ");
        String inputPassword = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT staff_id, password_hash FROM staff WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, inputUsername);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String dbPassword = rs.getString("password_hash");
                if (dbPassword.equals(inputPassword)) {  // 假设未加密，直接比较
                    System.out.println("工作人员登录成功！");
                    return rs.getInt("staff_id");
                } else {
                    System.out.println("密码错误！");
                }
            } else {
                System.out.println("用户名不存在！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
}
