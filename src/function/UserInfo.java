// src/function/UserInfo.java
package function;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserInfo {

    /**
     * 显示指定用户的详细信息，包括基本账户信息、余额及交易记录。
     *
     * @param userId 用户ID
     */
    public static void viewUserInfo(int userId) {
        try (Connection conn = DBConnection.getConnection()) {
            // 1. 获取用户的基本信息（账户名称、性别、开户时间）
            String userInfoSql = "SELECT account_name, gender, registration_date FROM users WHERE user_id = ?";
            PreparedStatement userInfoPs = conn.prepareStatement(userInfoSql);
            userInfoPs.setInt(1, userId);
            ResultSet userInfoRs = userInfoPs.executeQuery();

            if (userInfoRs.next()) {
                System.out.println("\n==== 用户基本信息 ====");
                System.out.println("账户名称: " + userInfoRs.getString("account_name"));
                System.out.println("性别: " + userInfoRs.getString("gender"));
                System.out.println("开户时间: " + userInfoRs.getTimestamp("registration_date"));

                // 2. 获取用户账户的余额信息
                String accountBalanceSql = "SELECT account_balance FROM accounts WHERE user_id = ?";
                PreparedStatement accountBalancePs = conn.prepareStatement(accountBalanceSql);
                accountBalancePs.setInt(1, userId);
                ResultSet accountBalanceRs = accountBalancePs.executeQuery();

                if (accountBalanceRs.next()) {
                    System.out.println("账户余额: " + accountBalanceRs.getDouble("account_balance"));
                } else {
                    System.out.println("未找到该用户的账户信息！");
                }
            } else {
                // 如果用户信息不存在
                System.out.println("未找到用户信息！");
            }

            // 3. 获取用户的交易记录
            System.out.println("\n==== 交易记录 ====");
            String transactionSql = "SELECT transaction_type, amount, transaction_time FROM transactions WHERE user_id = ?";
            PreparedStatement transactionPs = conn.prepareStatement(transactionSql);
            transactionPs.setInt(1, userId);
            ResultSet transactionRs = transactionPs.executeQuery();

            // 检查是否有交易记录
            if (!transactionRs.isBeforeFirst()) {
                System.out.println("没有交易记录");
            } else {
                // 输出交易记录详情
                while (transactionRs.next()) {
                    System.out.println("类型: " + transactionRs.getString("transaction_type") +
                            ", 金额: " + transactionRs.getDouble("amount") +
                            ", 时间: " + transactionRs.getTimestamp("transaction_time"));
                }
            }
        } catch (SQLException e) {
            // 捕获SQL异常并打印堆栈信息
            e.printStackTrace();
        }
    }
}