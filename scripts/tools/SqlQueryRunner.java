import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class SqlQueryRunner {
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("Usage: java SqlQueryRunner <jdbcUrl> <username> <password> <query>");
            System.exit(2);
        }

        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2]);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(args[3])) {
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            for (int i = 1; i <= columns; i++) {
                if (i > 1) {
                    System.out.print("\t");
                }
                System.out.print(meta.getColumnLabel(i));
            }
            System.out.println();

            while (rs.next()) {
                for (int i = 1; i <= columns; i++) {
                    if (i > 1) {
                        System.out.print("\t");
                    }
                    String value = rs.getString(i);
                    if (value != null && value.length() > 160) {
                        value = value.substring(0, 160) + "...";
                    }
                    System.out.print(value);
                }
                System.out.println();
            }
        }
    }
}
