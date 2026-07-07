# 数据库脚本说明

## 文件说明

- `schema.sql`：数据库建表脚本，不包含删表语句，适合首次初始化云数据库。
- `00_navicat_create_all_tables.sql`：Navicat 专用完整建表脚本，末尾会输出当前数据库表数量和表名。
- `init-data.sql`：角色、权限和演示数据初始化脚本，已包含演示数据依赖表的兜底建表语句。
- `init-homepage-data.sql`：首页推荐位演示数据初始化脚本，依赖 `homepage_recommendations` 等表已存在。
- `update/`：后续数据库变更脚本目录。

## 执行顺序

1. 确认数据库 `movie` 已存在。
2. 如果使用 Navicat，优先执行 `00_navicat_create_all_tables.sql`。执行成功后，结果里应能看到约 25 张表。
3. 如果不用 Navicat，也可以执行 `schema.sql`。如果使用阿里云 DMS 或 Navicat，建议再执行 `update/001_repair_missing_tables.sql`，确保初始化数据依赖的表都已存在。
4. 执行 `init-data.sql`。
5. 执行 `init-homepage-data.sql`。

如果执行 `init-data.sql` 时提示某张表不存在，例如：

```text
Table 'movie.homepage_recommendations' doesn't exist
```

说明 `schema.sql` 没有完整执行成功。可以先执行：

```text
update/001_repair_missing_tables.sql
```

然后重新执行 `init-data.sql`，最后执行 `init-homepage-data.sql`。

阿里云 DMS 有时会在执行整段 SQL 前预检查后续 `INSERT` 语句引用的表。遇到这种情况时，不要继续反复执行 `init-data.sql`，请先单独执行 `update/001_repair_missing_tables.sql`。

如果执行 `update/001_repair_missing_tables.sql` 后仍然提示 `Table 'movie.merchandise' doesn't exist`，请按下面顺序排查：

1. 执行 `check-tables.sql`，确认结果中是否包含 `merchandise`。
2. 如果没有 `merchandise`，执行 `update/002_force_create_merchandise.sql`。
3. 再次执行 `check-tables.sql`，确认 `SHOW TABLES LIKE 'merchandise'` 有返回结果。
4. 确认后再执行 `init-data.sql`。

## Navicat 执行方式

如果在 Navicat 中执行 SQL 文件时报错：

```text
No database selected
```

说明当前是在“连接”层执行 SQL，没有选中具体数据库。处理方式：

1. 在左侧连接中展开 `movie` 连接。
2. 找到并选中数据库 `movie`。
3. 右键数据库 `movie`，选择“运行 SQL 文件”。
4. 先运行 `00_navicat_create_all_tables.sql`，确认结果窗口里能看到多张表，再运行 `init-data.sql`，最后运行 `init-homepage-data.sql`。

脚本已经做了两层处理：

```sql
USE `movie`;
CREATE TABLE IF NOT EXISTS `movie`.`users` (...);
```

也就是说，表名都已经写成 `movie.users`、`movie.movies` 这种全限定形式。

如果实际数据库名不是 `movie`，请把脚本中的 `` `movie` `` 批量替换为真实数据库名。

如果仍然提示 `No database selected`，不要在连接节点上运行 SQL 文件。请先展开连接，选中具体数据库节点，再运行 SQL 文件。

## 命令示例

```bash
mysql -h dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com -P 3306 -u nhH6IpERCnuCcjhV6h0okozZ -p movie < sql/schema.sql
mysql -h dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com -P 3306 -u nhH6IpERCnuCcjhV6h0okozZ -p movie < sql/init-data.sql
mysql -h dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com -P 3306 -u nhH6IpERCnuCcjhV6h0okozZ -p movie < sql/init-homepage-data.sql
```

数据库密码请在命令提示后输入，不要写入脚本或仓库。
