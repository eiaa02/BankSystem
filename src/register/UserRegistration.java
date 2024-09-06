// src/register/UserRegistration.java
package register;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class UserRegistration {
    // 开户方法，用于注册新用户并生成账户
    public static void openAccount() {
        try (Connection conn = DBConnection.getConnection()) { // 自动关闭数据库连接
            Scanner scanner = new Scanner(System.in);

            System.out.println("===================================");
            System.out.println("          银行账户注册            ");
            System.out.println("===================================");

            // 获取用户基本信息
            System.out.print("请输入账户名称: ");
            String accountName = scanner.nextLine();

            System.out.print("请输入性别(男/女): ");
            String gender = scanner.nextLine();

            System.out.print("请输入年龄: ");
            int age = scanner.nextInt();
            scanner.nextLine();  // 处理换行符

            System.out.print("请输入电话号码: ");
            String phone = scanner.nextLine();

            System.out.print("请输入地址: ");
            String address = scanner.nextLine();

            System.out.print("请输入密码: ");
            String password = scanner.nextLine();

            System.out.print("请输入交易密码: ");
            String transactionPassword = scanner.nextLine();

            // 生成一个12位的随机银行卡号
            String bankCardNumber = String.valueOf((long) (Math.random() * 1_000_000_000_000L));

            // 插入用户信息到 users 表
            String sqlUser = "INSERT INTO users (account_name, gender, age, phone, address, password_hash, transaction_password) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement psUser = conn.prepareStatement(sqlUser);  // 创建准备语句
            psUser.setString(1, accountName);  // 设置账户名称
            psUser.setString(2, gender);       // 设置性别
            psUser.setInt(3, age);             // 设置年龄
            psUser.setString(4, phone);        // 设置电话号码
            psUser.setString(5, address);      // 设置地址
            psUser.setString(6, password);     // 设置密码
            psUser.setString(7, transactionPassword);  // 设置交易密码
            psUser.executeUpdate();  // 执行插入操作

            // 插入账户信息到 accounts 表，使用上一步生成的用户ID和随机的银行卡号
            String sqlAccount = "INSERT INTO accounts (user_id, bank_card_number) VALUES (LAST_INSERT_ID(), ?)";
            PreparedStatement psAccount = conn.prepareStatement(sqlAccount);  // 创建准备语句
            psAccount.setString(1, bankCardNumber);  // 设置银行卡号
            psAccount.executeUpdate();  // 执行插入操作

            // 提示用户开户成功，并显示银行卡号
            System.out.println("-----------------------------------");
            System.out.println("开户成功！您的银行卡号是：" + bankCardNumber);
            System.out.println("===================================");
        } catch (SQLException e) {
            e.printStackTrace();  // 打印SQL异常信息
        }
    }
}
