# 数据库脚本说明

## 文件说明

- `schema.sql`：数据库建表脚本，不包含删表语句，适合首次初始化云数据库。
- `init-data.sql`：角色、权限和演示数据初始化脚本。
- `update/`：后续数据库变更脚本目录。

## 执行顺序

1. 确认数据库 `movie` 已存在。
2. 执行 `schema.sql`。
3. 执行 `init-data.sql`。

## Navicat 执行方式

如果在 Navicat 中执行 SQL 文件时报错：

```text
No database selected
```

说明当前是在“连接”层执行 SQL，没有选中具体数据库。处理方式：

1. 在左侧连接中展开 `movie` 连接。
2. 找到并选中数据库 `movie`。
3. 右键数据库 `movie`，选择“运行 SQL 文件”。
4. 先运行 `schema.sql`，再运行 `init-data.sql`。

脚本开头已经包含：

```sql
USE movie;
```

如果实际数据库名不是 `movie`，请把 `USE movie;` 改成真实数据库名。

## 命令示例

```bash
mysql -h dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com -P 3306 -u nhH6IpERCnuCcjhV6h0okozZ -p movie < sql/schema.sql
mysql -h dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com -P 3306 -u nhH6IpERCnuCcjhV6h0okozZ -p movie < sql/init-data.sql
```

数据库密码请在命令提示后输入，不要写入脚本或仓库。
