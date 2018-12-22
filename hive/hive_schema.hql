
-- drop table if exists credit.ods_user;
-- drop table if exists credit.ods_login;
-- drop table if exists credit.operation_log;



-- 142018-09-24 00:00:002018-09-24 09:00:001387927808851.54.207.104002018-09-23 00:00:002018-09-23 09:00:00
-- 28402018-10-12 00:00:002018-10-12 21:00:0016705786824176.10.247.181002018-10-11 00:00:002018-10-11 21:00:00
-- 用户信息表
create table if not exists credit.ods_user (
  user_id bigint,
  mobile_no bigint,
  email string,
  create_time string,
  update_time string,
  is_del int
)
row format delimited fields terminated by '\001'
stored as textfile;

load data inpath '/data/credit/ods/ods_US_User' into table credit.ods_user;

create table if not exists credit.user (
  user_id bigint,
  mobile_no bigint,
  email string,
  create_time string,
  update_time string,
  is_del int
)
row format delimited fields terminated by '\001'
stored as textfile;



-- 118725154947NULL2014-05-11 00:00:002014-05-11 00:00:000
-- 215067842997NULL2011-11-01 00:00:002011-11-01 00:00:000
-- 用户登录记录表
create table credit.ods_login (
  SN bigint comment '自增列',
  UserId int comment '用户的SID',
  SignInTime string comment '登录时间',
  ExpireTime string comment '失效时间',
  MobileNo bigint comment '用户手机号',
  UserIP string comment '用户的IP，大小写不敏感',
  UserType int comment '用户类型：0->NONE 1->WEB用户 2->app用户',
  Flag int comment '登录是否成功，0成功；1用户名密码错；2服务器内部错误',
  PreSignInTime string comment '上次登录时间',
  PreExpireTime string comment '上次失效时间'
)
row format delimited fields terminated by '\001'
stored as textfile
location '/data/credit/ods/ods_US_Login';





-- 482018-09-23 04:48:00

-- 用户操作纪律表
drop table if exists credit.ods_operation_log;
create table if not exists credit.ods_operation_log (
  user_id bigint,
  opt_type string comment '1.登录 2.互动 3.邀请好友 4.分享链接 5.加好友 6.开通会员 7.保持会员 8.签到',
  opt_time string comment '操作日期'
)
row format delimited fields terminated by '\001'
stored as textfile;

load data inpath '/data/credit/ods/ods_operation_log' into table credit.ods_operation_log;
-- load data inpath '/data/credit/ods/ods_US_OperationLog' into table credit.ods_operation_log;


create table if not exists credit.operation_log (
  user_id bigint,
  opt_type string comment '1.登录 2.互动 3.邀请好友 4.分享链接 5.加好友 6.开通会员 7.保持会员 8.签到'
)
partitioned by (year int, month int, day  int)
row format delimited fields terminated by '\001'
stored as textfile;

set hive.exec.mode.local.auto=true;
set hive.exec.dynamic.partition=true; -- (可通过这个语句查看: set hive.exec.dynamic.partition;)
set hive.exec.dynamic.partition.mode=nonstrict;
SET hive.exec.max.dynamic.partitions=100000; -- (如果自动分区数大于这个参数, 将会报错)
SET hive.exec.max.dynamic.partitions.pernode=100000;

insert
into table credit.operation_log
partition(year, month, day)
select user_id,
  opt_type,
  year(opt_time) as year,
  month(opt_time) as month,
  day(opt_time) as day
from credit.ods_operation_log
where year(opt_time)=2008;


drop table if exists credit.ods_operation_log;





-- 动态分区
-- op.sh
for it_year in 2008 2009 2010 2011 2012 2013 2014 2015 2016 2017 2018 2019
do
hive -hiveconf it_year=${it_year} -f op.hql
done

-- hive.sh
set hive.exec.mode.local.auto=true;
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;
SET hive.exec.max.dynamic.partitions=100000;
SET hive.exec.max.dynamic.partitions.pernode=100000;

insert
into table credit.operation_log
partition(year, month, day)
select user_id,
  opt_type,
  year(opt_time) as year,
  month(opt_time) as month,
  day(opt_time) as day
from credit.ods_operation_log
where year(opt_time)=${hiveconf:it_year};

