package function;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Transfer {

    /**
     * 进行账户间的转账操作。
     * 该方法从发起方账户向接收方账户转账，更新双方的余额，并记录转账和交易信息。
     *
     * @param senderAccountId    发起方的账户ID
     * @param receiverCardNumber 接收方的银行卡号
     * @param amount             转账金额
     */
    public static void transferMoney(int senderAccountId, String receiverCardNumber, double amount) {
        // 使用 try-with-resources 自动关闭数据库连接
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);  // 开启事务以确保所有操作都成功

            // 1. 获取发起方的账户余额和银行卡号
            String senderSql = "SELECT account_balance, bank_card_number FROM accounts WHERE account_id = ?";
            PreparedStatement senderPs = conn.prepareStatement(senderSql);
            senderPs.setInt(1, senderAccountId);
            ResultSet senderRs = senderPs.executeQuery();

            if (senderRs.next()) {
                double senderBalance = senderRs.getDouble("account_balance");  // 发起方当前余额
                String senderCardNumber = senderRs.getString("bank_card_number");  // 发起方银行卡号

                // 2. 检查发起方余额是否足够
                if (senderBalance >= amount) {
                    // 3. 获取接收方账户的账户ID和当前余额
                    String receiverSql = "SELECT account_id, account_balance FROM accounts WHERE bank_card_number = ?";
                    PreparedStatement receiverPs = conn.prepareStatement(receiverSql);
                    receiverPs.setString(1, receiverCardNumber);
                    ResultSet receiverRs = receiverPs.executeQuery();

                    if (receiverRs.next()) {
                        int receiverAccountId = receiverRs.getInt("account_id");  // 接收方账户ID
                        double receiverBalance = receiverRs.getDouble("account_balance");  // 接收方当前余额

                        // 4. 更新发起方的账户余额
                        String updateSenderSql = "UPDATE accounts SET account_balance = ? WHERE account_id = ?";
                        PreparedStatement updateSenderPs = conn.prepareStatement(updateSenderSql);
                        updateSenderPs.setDouble(1, senderBalance - amount);  // 更新后的余额
                        updateSenderPs.setInt(2, senderAccountId);  // 发起方账户ID
                        updateSenderPs.executeUpdate();

                        // 5. 更新接收方的账户余额
                        String updateReceiverSql = "UPDATE accounts SET account_balance = ? WHERE account_id = ?";
                        PreparedStatement updateReceiverPs = conn.prepareStatement(updateReceiverSql);
                        updateReceiverPs.setDouble(1, receiverBalance + amount);  // 更新后的余额
                        updateReceiverPs.setInt(2, receiverAccountId);  // 接收方账户ID
                        updateReceiverPs.executeUpdate();

                        // 6. 将转账记录插入 transfers 表
                        String insertTransferSql = "INSERT INTO transfers (sender_account_id, sender_bank_card_number, " +
                                "receiver_account_id, receiver_bank_card_number, transfer_amount, sender_total_balance, receiver_total_balance) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement insertTransferPs = conn.prepareStatement(insertTransferSql);
                        insertTransferPs.setInt(1, senderAccountId);  // 发起方账户ID
                        insertTransferPs.setString(2, senderCardNumber);  // 发起方银行卡号
                        insertTransferPs.setInt(3, receiverAccountId);  // 接收方账户ID
                        insertTransferPs.setString(4, receiverCardNumber);  // 接收方银行卡号
                        insertTransferPs.setDouble(5, amount);  // 转账金额
                        insertTransferPs.setDouble(6, senderBalance - amount);  // 发起方新余额
                        insertTransferPs.setDouble(7, receiverBalance + amount);  // 接收方新余额
                        insertTransferPs.executeUpdate();

                        // 7. 插入发起方的交易记录到 transactions 表（转出）
                        String insertSenderTransactionSql = "INSERT INTO transactions (user_id, transaction_type, amount) " +
                                "VALUES (?, '转出', ?)";
                        PreparedStatement insertSenderTransactionPs = conn.prepareStatement(insertSenderTransactionSql);
                        insertSenderTransactionPs.setInt(1, senderAccountId);  // 发起方账户ID
                        insertSenderTransactionPs.setDouble(2, amount);  // 转出金额
                        insertSenderTransactionPs.executeUpdate();

                        // 8. 插入接收方的交易记录到 transactions 表（转入）
                        String insertReceiverTransactionSql = "INSERT INTO transactions (user_id, transaction_type, amount) " +
                                "VALUES (?, '转入', ?)";
                        PreparedStatement insertReceiverTransactionPs = conn.prepareStatement(insertReceiverTransactionSql);
                        insertReceiverTransactionPs.setInt(1, receiverAccountId);  // 接收方账户ID
                        insertReceiverTransactionPs.setDouble(2, amount);  // 转入金额
                        insertReceiverTransactionPs.executeUpdate();

                        // 9. 提交事务
                        conn.commit();
                        System.out.println("转账成功！发起方新余额为：" + (senderBalance - amount));
                    } else {
                        // 如果接收方账户不存在
                        System.out.println("接收方账户不存在！");
                        conn.rollback();  // 回滚事务
                    }
                } else {
                    // 如果发起方余额不足
                    System.out.println("发起方余额不足！");
                    conn.rollback();  // 回滚事务
                }
            } else {
                // 如果发起方账户不存在
                System.out.println("发起方账户不存在！");
                conn.rollback();  // 回滚事务
            }
        } catch (SQLException e) {
            // 捕获并处理 SQL 异常
            e.printStackTrace();
        }
    }
}
