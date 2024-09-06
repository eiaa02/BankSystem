package function;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Deposit1 {

    /**
     * 无卡存款方法。
     * 允许用户通过银行卡号进行存款操作，更新账户余额，并将操作记录插入 deposits 和 transactions 表中。
     */
    public static void depositWithoutCard() {
        // 使用 try-with-resources 自动关闭数据库连接
        try (Connection conn = DBConnection.getConnection()) {
            // 创建 Scanner 对象以从控制台读取用户输入
            Scanner scanner = new Scanner(System.in);

            // 1. 获取用户输入的银行卡号
            System.out.print("请输入银行卡号: ");
            String cardNumber = scanner.nextLine();  // 读取银行卡号

            // 2. 获取用户输入的存款金额
            System.out.print("请输入存款金额: ");
            double amount = scanner.nextDouble();  // 读取存款金额

            // 3. 检查银行卡号是否存在于 accounts 表中
            String checkAccountSql = "SELECT * FROM accounts WHERE bank_card_number = ?";
            PreparedStatement checkAccountPs = conn.prepareStatement(checkAccountSql);
            checkAccountPs.setString(1, cardNumber);
            ResultSet accountRs = checkAccountPs.executeQuery();

            if (accountRs.next()) {
                // 4. 如果银行卡号存在，获取当前账户余额并更新余额
                double currentBalance = accountRs.getDouble("account_balance");
                double newBalance = currentBalance + amount;  // 计算新的余额

                // 5. 更新 accounts 表中的账户余额
                String updateBalanceSql = "UPDATE accounts SET account_balance = ? WHERE bank_card_number = ?";
                PreparedStatement updateBalancePs = conn.prepareStatement(updateBalanceSql);
                updateBalancePs.setDouble(1, newBalance);  // 更新为新的余额
                updateBalancePs.setString(2, cardNumber);
                updateBalancePs.executeUpdate();  // 执行更新操作

                // 6. 插入存款记录到 deposits 表
                String insertDepositSql = "INSERT INTO deposits (account_id, bank_card_number, deposit_amount, total_balance) VALUES (?, ?, ?, ?)";
                PreparedStatement insertDepositPs = conn.prepareStatement(insertDepositSql);
                insertDepositPs.setInt(1, accountRs.getInt("account_id"));  // 获取 account_id
                insertDepositPs.setString(2, cardNumber);  // 使用银行卡号
                insertDepositPs.setDouble(3, amount);  // 存款金额
                insertDepositPs.setDouble(4, newBalance);  // 更新后的余额
                insertDepositPs.executeUpdate();  // 执行插入操作

                // 7. 插入交易记录到 transactions 表
                String insertTransactionSql = "INSERT INTO transactions (user_id, transaction_type, amount) " +
                        "SELECT u.user_id, '存款', ? " +
                        "FROM accounts a " +
                        "JOIN users u ON a.user_id = u.user_id " +
                        "WHERE a.bank_card_number = ?";
                PreparedStatement insertTransactionPs = conn.prepareStatement(insertTransactionSql);
                insertTransactionPs.setDouble(1, amount);  // 存款金额
                insertTransactionPs.setString(2, cardNumber);  // 使用银行卡号作为条件
                insertTransactionPs.executeUpdate();  // 执行插入操作

                // 8. 输出存款成功消息和当前余额
                System.out.println("存款成功！当前余额为：" + newBalance);
            } else {
                // 如果银行卡号不存在，输出错误信息
                System.out.println("银行卡号不存在！");
            }
        } catch (SQLException e) {
            // 捕获并输出 SQL 异常信息
            e.printStackTrace();
        }
    }
}
