import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SqlFileRunner {
    public static void main(String[] args) throws Exception {
        if (args.length != 4) {
            System.err.println("Usage: java SqlFileRunner <jdbcUrl> <username> <password> <sqlFile>");
            System.exit(2);
        }

        String jdbcUrl = args[0];
        String username = args[1];
        String password = args[2];
        Path sqlFile = Path.of(args[3]);

        String sql = Files.readString(sqlFile, StandardCharsets.UTF_8);
        if (!sql.isEmpty() && sql.charAt(0) == '\uFEFF') {
            sql = sql.substring(1);
        }

        List<String> statements = splitSql(sql);
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
             Statement statement = connection.createStatement()) {
            int executed = 0;
            for (String raw : statements) {
                String item = raw.trim();
                if (item.isEmpty()) {
                    continue;
                }
                try {
                    statement.execute(item);
                    executed++;
                } catch (SQLException ex) {
                    System.err.println("Failed at statement " + (executed + 1) + ":");
                    System.err.println(firstLine(item));
                    throw ex;
                }
            }
            System.out.println("Executed statements: " + executed);
        }
    }

    private static List<String> splitSql(String sql) throws IOException {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inSingle = false;
        boolean inDouble = false;
        boolean inBacktick = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            char next = i + 1 < sql.length() ? sql.charAt(i + 1) : '\0';

            if (inLineComment) {
                current.append(c);
                if (c == '\n') {
                    inLineComment = false;
                }
                continue;
            }

            if (inBlockComment) {
                current.append(c);
                if (c == '*' && next == '/') {
                    current.append(next);
                    i++;
                    inBlockComment = false;
                }
                continue;
            }

            if (!inSingle && !inDouble && !inBacktick) {
                if (c == '-' && next == '-') {
                    current.append(c).append(next);
                    i++;
                    inLineComment = true;
                    continue;
                }
                if (c == '#') {
                    current.append(c);
                    inLineComment = true;
                    continue;
                }
                if (c == '/' && next == '*') {
                    current.append(c).append(next);
                    i++;
                    inBlockComment = true;
                    continue;
                }
            }

            if (c == '\'' && !inDouble && !inBacktick) {
                current.append(c);
                if (inSingle && next == '\'') {
                    current.append(next);
                    i++;
                    continue;
                }
                if (!isEscaped(sql, i)) {
                    inSingle = !inSingle;
                }
                continue;
            }

            if (c == '"' && !inSingle && !inBacktick && !isEscaped(sql, i)) {
                inDouble = !inDouble;
                current.append(c);
                continue;
            }

            if (c == '`' && !inSingle && !inDouble) {
                inBacktick = !inBacktick;
                current.append(c);
                continue;
            }

            if (c == ';' && !inSingle && !inDouble && !inBacktick) {
                statements.add(current.toString());
                current.setLength(0);
                continue;
            }

            current.append(c);
        }

        if (current.toString().trim().length() > 0) {
            statements.add(current.toString());
        }
        return statements;
    }

    private static boolean isEscaped(String text, int index) {
        int slashCount = 0;
        for (int i = index - 1; i >= 0 && text.charAt(i) == '\\'; i--) {
            slashCount++;
        }
        return slashCount % 2 == 1;
    }

    private static String firstLine(String sql) {
        String compact = sql.strip().replace("\r", "");
        int lineBreak = compact.indexOf('\n');
        return lineBreak >= 0 ? compact.substring(0, lineBreak) : compact;
    }
}
