package menu;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StaffMenu {

    // 展示工作人员操作菜单
    public static void showStaffMenu(int staffId) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        // 主循环用于显示菜单并处理选择
        while (running) {
            System.out.println("\n=========== 工作人员操作界面 ===========");
            System.out.println("1. 查询银行所有交易记录");
            System.out.println("2. 查询银行所有账户");
            System.out.println("3. 修改用户信息");
            System.out.println("0. 退出工作界面");
            System.out.println("=====================================");
            System.out.print("请选择操作: ");

            int choice = scanner.nextInt();

            // 根据输入的选项执行相应操作
            switch (choice) {
                case 1:
                    viewAllTransactionRecords(); // 查看所有交易记录
                    break;
                case 2:
                    viewAccountStatus(); // 查看所有账户信息
                    break;
                case 3:
                    modifyUserInfo(); // 修改用户信息
                    break;
                case 0:
                    running = false; // 退出循环，结束程序
                    System.out.println("退出工作人员界面！");
                    break;
                default:
                    System.out.println("无效选择，请重新输入！");
            }
        }
    }

    // 1. 查询银行所有用户的交易记录（存款、转账、取款）
    private static void viewAllTransactionRecords() {
        try (Connection conn = DBConnection.getConnection()) {
            // SQL查询，获取所有交易记录并关联用户表，获取用户名
            String sql = "SELECT t.transaction_id, u.account_name, t.transaction_type, t.amount, t.transaction_time " +
                    "FROM transactions t " +
                    "JOIN users u ON t.user_id = u.user_id";  // 关联用户表获取账户名称
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // 遍历查询结果，逐行打印交易记录
            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                String accountName = rs.getString("account_name");  // 获取用户名
                String type = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                String time = rs.getString("transaction_time");
                System.out.println("交易ID: " + transactionId + ", 用户名: " + accountName +
                        ", 类型: " + type + ", 金额: " + amount + ", 时间: " + time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. 查询银行账户状态（包括销户）
    private static void viewAccountStatus() {
        try (Connection conn = DBConnection.getConnection()) {
            // SQL查询，获取所有账户的详细信息
            String sql = "SELECT a.account_id, u.account_name, a.bank_card_number, a.account_balance, a.status " +
                    "FROM accounts a " +
                    "JOIN users u ON a.user_id = u.user_id";  // 联结accounts表和users表
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            // 遍历查询结果，逐行打印账户信息
            while (rs.next()) {
                int accountId = rs.getInt("account_id");
                String accountName = rs.getString("account_name");
                String bankCardNumber = rs.getString("bank_card_number");
                double balance = rs.getDouble("account_balance");
                String status = rs.getString("status");
                System.out.println("账户ID: " + accountId + ", 账户名称: " + accountName +
                        ", 银行卡号: " + bankCardNumber + ", 余额: " + balance + ", 状态: " + status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. 修改用户信息
    private static void modifyUserInfo() {
        Scanner scanner = new Scanner(System.in);

        // 获取需要修改的用户ID
        System.out.print("请输入要修改的用户ID: ");
        int userId = scanner.nextInt();
        scanner.nextLine();  // 清除输入缓冲区

        // 获取新的用户信息
        System.out.print("请输入新的账户名称: ");
        String newAccountName = scanner.nextLine();

        System.out.print("请输入新的性别 (男/女): ");
        String newGender = scanner.nextLine();

        System.out.print("请输入新的年龄: ");
        int newAge = scanner.nextInt();
        scanner.nextLine();  // 清除输入缓冲区

        System.out.print("请输入新的电话号码: ");
        String newPhone = scanner.nextLine();

        System.out.print("请输入新的地址: ");
        String newAddress = scanner.nextLine();

        try (Connection conn = DBConnection.getConnection()) {
            // 更新用户信息的SQL语句
            String sql = "UPDATE users SET account_name = ?, gender = ?, age = ?, phone = ?, address = ? WHERE user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newAccountName);
            ps.setString(2, newGender);
            ps.setInt(3, newAge);
            ps.setString(4, newPhone);
            ps.setString(5, newAddress);
            ps.setInt(6, userId);

            // 执行更新操作并打印结果
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("用户信息修改成功！");
            } else {
                System.out.println("用户ID未找到！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
