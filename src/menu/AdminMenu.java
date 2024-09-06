package menu;

import DB.DBConnection;
import java.sql.*;
import java.util.Scanner;

public class AdminMenu {

    // 显示管理员主菜单
    public static void showAdminMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean adminRunning = true;

        while (adminRunning) {
            System.out.println("\n=========== 管理员管理系统 ===========");
            System.out.println("1. 查询工作人员信息");
            System.out.println("2. 添加工作人员");
            System.out.println("3. 修改工作人员信息");
            System.out.println("4. 删除工作人员");
            System.out.println("5. 退出管理界面");
            System.out.println("=====================================");
            System.out.print("请选择操作: ");

            int choice = scanner.nextInt();  // 获取用户选择
            switch (choice) {
                case 1:
                    viewStaffInfo();  // 查询工作人员信息
                    break;
                case 2:
                    addStaff();  // 添加工作人员
                    break;
                case 3:
                    updateStaff();  // 修改工作人员信息
                    break;
                case 4:
                    deleteStaff();  // 删除工作人员
                    break;
                case 5:
                    adminRunning = false;  // 退出管理界面
                    System.out.println("已退出管理界面");
                    break;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    // 1. 查询工作人员信息
    public static void viewStaffInfo() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT staff_id, username, hire_date FROM staff";  // 查询所有工作人员信息
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println("\n=========== 工作人员信息 ===========");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("staff_id") +
                        ", 用户名: " + rs.getString("username") +
                        ", 入职时间: " + rs.getTimestamp("hire_date"));
            }
            System.out.println("===================================");
        } catch (SQLException e) {
            System.out.println("查询工作人员信息失败！");
            e.printStackTrace();
        }
    }

    // 2. 添加工作人员
    public static void addStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入工作人员用户名: ");
        String username = scanner.nextLine();
        System.out.print("请输入工作人员密码: ");
        String password = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO staff (username, password_hash) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);  // 直接保存密码
            ps.executeUpdate();

            System.out.println("工作人员添加成功！");
        } catch (SQLException e) {
            System.out.println("添加工作人员失败！");
            e.printStackTrace();
        }
    }

    // 3. 修改工作人员信息
    public static void updateStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要修改的工作人员ID: ");
        int staffId = scanner.nextInt();
        scanner.nextLine(); // 清除换行符
        System.out.print("请输入新的用户名: ");
        String newUsername = scanner.nextLine();
        System.out.print("请输入新的密码: ");
        String newPassword = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE staff SET username = ?, password_hash = ? WHERE staff_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newUsername);
            ps.setString(2, newPassword);  // 直接保存新密码
            ps.setInt(3, staffId);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("工作人员信息修改成功！");
            } else {
                System.out.println("未找到该ID的工作人员！");
            }
        } catch (SQLException e) {
            System.out.println("修改工作人员信息失败！");
            e.printStackTrace();
        }
    }

    // 4. 删除工作人员
    public static void deleteStaff() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入要删除的工作人员ID: ");
        int staffId = scanner.nextInt();

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM staff WHERE staff_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, staffId);
            int rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                System.out.println("工作人员删除成功！");
            } else {
                System.out.println("未找到该ID的工作人员！");
            }
        } catch (SQLException e) {
            System.out.println("删除工作人员失败！");
            e.printStackTrace();
        }
    }
}
