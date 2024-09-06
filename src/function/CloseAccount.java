package function;

import DB.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CloseAccount {

    /**
     * 关闭用户账户的方法。
     * 该方法会检查用户账户余额是否为零，只有余额为零时才能销户。
     * 如果账户销户成功，账户状态将被更新为 'Closed'。
     *
     * @param userId 用户的ID
     * @return 如果账户成功销户则返回true，否则返回false
     */
    public static boolean closeAccount(int userId) {
        // 使用 try-with-resources 语句自动关闭数据库连接
        try (Connection conn = DBConnection.getConnection()) {

            // 1. 检查账户余额是否为零
            String checkBalanceSql = "SELECT account_balance FROM accounts WHERE user_id = ?";
            PreparedStatement checkBalancePs = conn.prepareStatement(checkBalanceSql);
            checkBalancePs.setInt(1, userId);
            ResultSet rs = checkBalancePs.executeQuery();

            if (rs.next()) {
                // 获取账户余额
                double balance = rs.getDouble("account_balance");

                // 2. 如果账户余额为零，则允许销户
                if (balance == 0) {
                    // 更新账户状态为 'Closed'，表示账户已销户
                    String updateStatusSql = "UPDATE accounts SET status = 'Closed' WHERE user_id = ?";
                    PreparedStatement updateStatusPs = conn.prepareStatement(updateStatusSql);
                    updateStatusPs.setInt(1, userId);
                    updateStatusPs.executeUpdate();

                    // 输出销户成功信息
                    System.out.println("账户已成功销户！");
                    return true;  // 返回销户成功状态
                } else {
                    // 如果余额不为零，则不允许销户，提示用户先取款
                    System.out.println("账户余额不为零，请先取款！");
                    return false;  // 返回销户失败状态
                }
            } else {
                // 如果查询结果为空，表示账户ID不存在
                System.out.println("账户ID不存在！");
                return false;  // 返回销户失败状态
            }
        } catch (SQLException e) {
            // 捕获并输出SQL异常信息
            e.printStackTrace();
            return false;  // 在发生异常时，返回销户失败状态
        }
    }
}
