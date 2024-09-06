// src/menu/UserMenu.java
package menu;

import DB.DBConnection;
import function.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class UserMenu {

    private Scanner scanner = new Scanner(System.in);

    public void showMenu(int userId) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        while (!exit) {
            // 菜单标题与分隔线
            System.out.println("===================================");
            System.out.println("         用户操作菜单");
            System.out.println("===================================");
            System.out.println("1. 查看账户信息");
            System.out.println("2. 存款");
            System.out.println("3. 取款");
            System.out.println("4. 转账");
            System.out.println("5. 修改密码");
            System.out.println("6. 销户");
            System.out.println("0. 退出");
            System.out.println("===================================");
            System.out.print("请选择操作: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    UserInfo.viewUserInfo(userId);
                    break;
                case 2:
                    System.out.print("请输入存款金额: ");
                    double depositAmount = scanner.nextDouble();
                    Deposit.makeDeposit(userId, depositAmount);
                    break;
                case 3:
                    System.out.print("请输入取款金额: ");
                    double withdrawAmount = scanner.nextDouble();
                    // 验证交易密码
                    if (verifyTransactionPassword(userId)) {
                        Withdrawal.makeWithdrawal(userId, withdrawAmount);
                    } else {
                        System.out.println("交易密码不正确，取款失败！");
                    }
                    break;
                case 4:
                    System.out.print("请输入接收方银行卡号: ");
                    String receiverCardNumber = scanner.next();
                    System.out.print("请输入转账金额: ");
                    double transferAmount = scanner.nextDouble();
                    // 验证交易密码
                    if (verifyTransactionPassword(userId)) {
                        Transfer.transferMoney(userId, receiverCardNumber, transferAmount);
                    } else {
                        System.out.println("交易密码不正确，转账失败！");
                    }
                    break;
                case 5:
                    modifyPassword(userId);
                    break;
                case 6:
                    boolean accountClosed = CloseAccount.closeAccount(userId);
                    if (accountClosed) {
                        System.out.println("账户已销户，正在退出用户操作界面...");
                        exit = true;  // 退出当前菜单，回到主界面
                    }
                    break;
                case 0:
                    System.out.println("退出用户菜单...");
                    return;  // 退出菜单，返回主界面
                default:
                    System.out.println("无效选项，请重新选择！");
                    break;
            }
        }
    }

    // 验证用户交易密码
    private boolean verifyTransactionPassword(int userId) {
        System.out.print("请输入交易密码: ");
        String transactionPassword = scanner.next();

        try (Connection conn = DBConnection.getConnection()) {
            // 查询数据库以验证交易密码
            String verifyTransactionPasswordSql = "SELECT transaction_password FROM users WHERE user_id = ? AND transaction_password = ?";
            PreparedStatement ps = conn.prepareStatement(verifyTransactionPasswordSql);
            ps.setInt(1, userId);
            ps.setString(2, transactionPassword);
            ResultSet rs = ps.executeQuery();

            return rs.next();  // 如果查询有结果，表示密码正确
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 修改用户密码
    private void modifyPassword(int userId) {
        System.out.print("请输入当前密码: ");
        String currentPassword = scanner.next();

        System.out.print("请输入新密码: ");
        String newPassword = scanner.next();

        try (Connection conn = DBConnection.getConnection()) {
            // 验证当前密码是否正确
            String verifyPasswordSql = "SELECT password_hash FROM users WHERE user_id = ? AND password_hash = ?";
            PreparedStatement ps = conn.prepareStatement(verifyPasswordSql);
            ps.setInt(1, userId);
            ps.setString(2, currentPassword);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // 更新密码
                String updatePasswordSql = "UPDATE users SET password_hash = ? WHERE user_id = ?";
                PreparedStatement updatePs = conn.prepareStatement(updatePasswordSql);
                updatePs.setString(1, newPassword);
                updatePs.setInt(2, userId);
                int rowsAffected = updatePs.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("密码修改成功！");
                } else {
                    System.out.println("密码修改失败！");
                }
            } else {
                System.out.println("当前密码不正确！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
