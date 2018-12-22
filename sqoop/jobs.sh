#!/bin/bash


# 将US_User导出
sqoop job --create credit_import_user \
-- import \
--connect jdbc:mysql://hh:3306/credit_rating \
--username hive \
--password 123456 \
--table US_User \
--append \
--direct \
--target-dir /data/credit/ods/ods_US_User \
--num-mappers 1 \
--incremental append \
--check-column UserId \
--last-value 0 \
--lines-terminated-by '\n' \
--fields-terminated-by '\001'


# 将US_Login导出
sqoop job --create credit_import_login \
-- import \
--connect jdbc:mysql://hh:3306/credit_rating \
--username hive \
--password 123456 \
--table US_Login \
--append \
--direct \
--target-dir /data/credit/ods/ods_US_Login \
--num-mappers 1 \
--incremental append \
--check-column SN \
--last-value 0 \
--lines-terminated-by '\n' \
--fields-terminated-by '\001'


# 将US_OperationLog导出
sqoop job --create credit_import_operation_log \
-- import \
--connect jdbc:mysql://hh:3306/credit_rating \
--username hive \
--password 123456 \
--table US_OperationLog \
--append \
--direct \
--target-dir /data/credit/ods/ods_US_OperationLog \
--num-mappers 1 \
--incremental append \
--check-column OptTime \
--last-value '2008-01-01' \
--lines-terminated-by '\n' \
--fields-terminated-by '\001'


sqoop import \
--connect jdbc:mysql://hh:3306/credit_rating \
--username hive \
--password 123456 \
--table US_OperationLog \
--append \
--direct \
--target-dir /data/credit/ods/ods_US_OperationLog \
--num-mappers 1 \
--incremental append \
--check-column OptTime \
--last-value '2008-01-01' \
--lines-terminated-by '\n' \
--fields-terminated-by '\001'
