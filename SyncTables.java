import java.io.*;
import java.sql.*;

/**
 * 连接 MySQL 并创建 user_subscription + payment_record 表。
 * 编译: javac -cp mysql-connector-j-8.3.0.jar SyncTables.java
 * 运行: java -cp .:mysql-connector-j-8.3.0.jar SyncTables
 */
public class SyncTables {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://192.168.1.38:3306/auto?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai";

        String[] ddl = {
            // user_subscription
            "CREATE TABLE IF NOT EXISTS `user_subscription` ("
                + "`id` BIGINT NOT NULL AUTO_INCREMENT, "
                + "`user_id` BIGINT NOT NULL, "
                + "`plan` VARCHAR(16) NOT NULL COMMENT 'MONTHLY/QUARTERLY/YEARLY', "
                + "`start_date` DATETIME NOT NULL, "
                + "`end_date` DATETIME NOT NULL, "
                + "`status` VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE/EXPIRED/CANCELLED', "
                + "`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "PRIMARY KEY (`id`), "
                + "INDEX `idx_user_id` (`user_id`), "
                + "INDEX `idx_status_end_date` (`status`, `end_date`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4",

            // payment_record
            "CREATE TABLE IF NOT EXISTS `payment_record` ("
                + "`id` BIGINT NOT NULL AUTO_INCREMENT, "
                + "`user_id` BIGINT NOT NULL, "
                + "`amount` DECIMAL(10,2) NOT NULL, "
                + "`method` VARCHAR(16) NOT NULL COMMENT 'WECHAT/ALIPAY/BANK_CARD', "
                + "`subscription_plan` VARCHAR(16) NOT NULL COMMENT 'MONTHLY/QUARTERLY/YEARLY', "
                + "`status` VARCHAR(16) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/SUCCESS/FAILED/REFUNDED', "
                + "`transaction_id` VARCHAR(128) DEFAULT NULL, "
                + "`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "PRIMARY KEY (`id`), "
                + "INDEX `idx_user_id` (`user_id`), "
                + "INDEX `idx_transaction_id` (`transaction_id`)"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4"
        };

        try (Connection conn = DriverManager.getConnection(url, "root", "root");
             Statement stmt = conn.createStatement()) {

            for (String sql : ddl) {
                try {
                    stmt.execute(sql);
                    System.out.println("OK: " + extractTable(sql));
                } catch (SQLException e) {
                    if (e.getErrorCode() == 1050) {
                        System.out.println("SKIP (exists): " + extractTable(sql));
                    } else {
                        System.err.println("ERROR " + e.getErrorCode() + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("\nAll tables:");
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next()) System.out.println("  " + rs.getString(1));
        }
    }

    static String extractTable(String sql) {
        int i = sql.indexOf('`');
        int e = sql.indexOf('`', i + 1);
        return i >= 0 && e > i ? sql.substring(i + 1, e) : "?";
    }
}
