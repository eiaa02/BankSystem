package function;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Deposit {

    /**
     * 用户存款方法。
     * 根据传入的账户ID和存款金额，更新账户余额，并记录该存款操作到 deposits 和 transactions 表中。
     *
     * @param accountId 账户ID
     * @param amount    存款金额
     */
    public static void makeDeposit(int accountId, double amount) {
        // 使用 try-with-resources 语句自动关闭数据库连接
        try (Connection conn = DBConnection.getConnection()) {

            // 1. 获取银行卡号和当前账户余额
            String getCardNumberSql = "SELECT bank_card_number, account_balance FROM accounts WHERE account_id = ?";
            PreparedStatement getCardNumberPs = conn.prepareStatement(getCardNumberSql);
            getCardNumberPs.setInt(1, accountId);
            ResultSet rs = getCardNumberPs.executeQuery();

            if (rs.next()) {
                // 获取银行卡号和账户当前余额
                String cardNumber = rs.getString("bank_card_number");
                double currentBalance = rs.getDouble("account_balance");
                double newBalance = currentBalance + amount;  // 计算新的余额

                // 2. 更新账户余额
                String updateBalanceSql = "UPDATE accounts SET account_balance = ? WHERE account_id = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateBalanceSql);
                updatePs.setDouble(1, newBalance);  // 将新的余额设置到账户
                updatePs.setInt(2, accountId);
                updatePs.executeUpdate();

                // 3. 插入存款记录到 deposits 表
                String insertDepositSql = "INSERT INTO deposits (account_id, bank_card_number, deposit_amount, total_balance) VALUES (?, ?, ?, ?)";
                PreparedStatement insertPs = conn.prepareStatement(insertDepositSql);
                insertPs.setInt(1, accountId);
                insertPs.setString(2, cardNumber);  // 存入银行卡号
                insertPs.setDouble(3, amount);  // 存入的金额
                insertPs.setDouble(4, newBalance);  // 存款后的余额
                insertPs.executeUpdate();

                // 4. 插入交易记录到 transactions 表
                String insertTransactionSql = "INSERT INTO transactions (user_id, transaction_type, amount) VALUES (?, '存款', ?)";
                PreparedStatement insertTransactionPs = conn.prepareStatement(insertTransactionSql);
                insertTransactionPs.setInt(1, accountId);  // 将 accountId 作为 userId 存入交易记录
                insertTransactionPs.setDouble(2, amount);  // 存款的金额
                insertTransactionPs.executeUpdate();

                // 输出存款成功信息和当前余额
                System.out.println("存款成功！当前余额为：" + newBalance);
            } else {
                // 如果账户ID不存在，输出错误信息
                System.out.println("账户ID不存在！");
            }
        } catch (SQLException e) {
            // 捕获并输出SQL异常信息
            e.printStackTrace();
        }
    }
}
