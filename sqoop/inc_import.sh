#!/bin/bash

# 增量导入用户信息
expect -c 'spawn sqoop job --exec credit_import_user
expect "*password*" {send "123456\r"}
interact'

# 增量导入登录记录
expect -c 'spawn sqoop job --exec credit_import_login
expect "*password*" {send "123456\r"}
interact'


# 增量导入操作记录
expect -c 'spawn sqoop job --exec credit_import_operation_log
expect "*password*" {send "123456\r"}
interact'





