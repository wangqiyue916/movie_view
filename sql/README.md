# 数据库脚本说明

## 文件说明

- `schema.sql`：数据库建表脚本，不包含删表语句，适合首次初始化云数据库。
- `init-data.sql`：角色、权限和演示数据初始化脚本。
- `update/`：后续数据库变更脚本目录。

## 执行顺序

1. 确认数据库 `movie` 已存在。
2. 执行 `schema.sql`。
3. 执行 `init-data.sql`。

## 命令示例

```bash
mysql -h dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com -P 3306 -u nhH6IpERCnuCcjhV6h0okozZ -p movie < sql/schema.sql
mysql -h dpshmy-ugp2kxl2hqqylbtj-pub.proxy.dms.aliyuncs.com -P 3306 -u nhH6IpERCnuCcjhV6h0okozZ -p movie < sql/init-data.sql
```

数据库密码请在命令提示后输入，不要写入脚本或仓库。

