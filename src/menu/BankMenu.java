// src/com/bank/BankMenu.java
package menu;

import function.Deposit1;
import login.StaffLogin;
import login.UserLogin;
import register.UserRegistration;

import java.util.Scanner;

public class BankMenu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true; // 控制主菜单的循环运行

        // 主菜单循环
        while (running) {
            System.out.println("\n========== 银行管理系统 ==========");
            System.out.println("  1. 用户开户");
            System.out.println("  2. 用户登录");
            System.out.println("  3. 工作人员登录");
            System.out.println("  4. 无卡存款");
            System.out.println("  5. 管理员登录");
            System.out.println("  6. 退出系统");
            System.out.println("=================================");
            System.out.print("请选择操作 (输入数字 1-6): ");

            int choice = scanner.nextInt(); // 获取用户的选择
            System.out.println(); // 换行美化

            // 根据用户输入进行相应的操作
            switch (choice) {
                case 1:
                    // 用户开户
                    System.out.println("===== 用户开户 =====");
                    UserRegistration.openAccount();
                    break;
                case 2:
                    // 用户登录
                    System.out.println("===== 用户登录 =====");
                    int userId = UserLogin.login(); // 用户登录方法，返回用户ID
                    if (userId != -1) {
                        // 如果登录成功，进入用户菜单
                        UserMenu userMenu = new UserMenu();
                        userMenu.showMenu(userId); // 显示用户菜单
                    }
                    break;
                case 3:
                    // 工作人员登录
                    System.out.println("===== 工作人员登录 =====");
                    int staffId = StaffLogin.login(); // 工作人员登录方法，返回工作人员ID
                    if (staffId != -1) {
                        // 如果登录成功，进入工作人员菜单
                        StaffMenu.showStaffMenu(staffId);
                    }
                    break;
                case 4:
                    // 无卡存款
                    System.out.println("===== 无卡存款 =====");
                    Deposit1.depositWithoutCard(); // 调用无卡存款功能
                    break;
                case 5:
                    // 管理员登录
                    System.out.println("===== 管理员登录 =====");
                    boolean adminLoggedIn = login.AdminLogin.login(); // 管理员登录
                    if (adminLoggedIn) {
                        // 如果管理员登录成功，进入管理员菜单
                        AdminMenu.showAdminMenu();
                    }
                    break;
                case 6:
                    // 退出系统
                    System.out.println("系统退出，感谢您的使用！");
                    running = false; // 停止循环，退出系统
                    break;
                default:
                    // 无效输入处理
                    System.out.println("无效的选择，请输入1到6之间的数字！");
            }
        }
    }
}
