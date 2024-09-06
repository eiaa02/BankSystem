package function;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Withdrawal {
    public static void makeWithdrawal(int accountId, double amount) {
        try (Connection conn = DBConnection.getConnection()) {
            // 获取银行卡号和账户余额
            String getCardNumberSql = "SELECT bank_card_number, account_balance FROM accounts WHERE account_id = ?";
            PreparedStatement getCardNumberPs = conn.prepareStatement(getCardNumberSql);
            getCardNumberPs.setInt(1, accountId);
            ResultSet rs = getCardNumberPs.executeQuery();

            if (rs.next()) {
                String cardNumber = rs.getString("bank_card_number");
                double currentBalance = rs.getDouble("account_balance");

                if (currentBalance >= amount) {
                    double newBalance = currentBalance - amount;

                    // 更新账户余额
                    String updateBalanceSql = "UPDATE accounts SET account_balance = ? WHERE account_id = ?";
                    PreparedStatement updatePs = conn.prepareStatement(updateBalanceSql);
                    updatePs.setDouble(1, newBalance);
                    updatePs.setInt(2, accountId);
                    updatePs.executeUpdate();

                    // 插入取款记录到 withdrawals 表
                    String insertWithdrawalSql = "INSERT INTO withdrawals (account_id, bank_card_number, withdrawal_amount, total_balance) VALUES (?, ?, ?, ?)";
                    PreparedStatement insertPs = conn.prepareStatement(insertWithdrawalSql);
                    insertPs.setInt(1, accountId);
                    insertPs.setString(2, cardNumber);
                    insertPs.setDouble(3, amount);
                    insertPs.setDouble(4, newBalance);
                    insertPs.executeUpdate();

                    // 插入交易记录到 transactions 表
                    String insertTransactionSql = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, '取款', ?)";
                    PreparedStatement insertTransactionPs = conn.prepareStatement(insertTransactionSql);
                    insertTransactionPs.setInt(1, accountId);  // 将 accountId 作为 userId 存入
                    insertTransactionPs.setDouble(2, amount);
                    insertTransactionPs.executeUpdate();

                    System.out.println("取款成功！当前余额为：" + newBalance);
                } else {
                    System.out.println("余额不足！");
                }
            } else {
                System.out.println("账户ID不存在！");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
