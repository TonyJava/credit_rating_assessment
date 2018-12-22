
-- =====================
--       建表信息
-- =====================

drop table if exists US_User;
drop table if exists US_Login;
drop table if exists US_OperationLog;
drop table if exists US_SignInLog;
drop table if exists US_Experience;
drop table if exists US_Score;
drop table if exists US_Consume;
drop table if exists US_DailyScoreLog;
drop table if exists US_MonthlyScoreLog;
drop table if exists US_YearlyScoreLog;
drop table if exists US_ExperienceLog;
drop table if exists US_MonthScore;
drop table if exists US_RankLevel;
drop table if exists US_RankLog;
drop table if exists US_UserOnline;
drop table if exists US_Process;
drop table if exists US_ProcessLogSecurityPolicy;
drop table if exists US_Credit;
drop table if exists US_Credit_Log;


-- 1. US_User 用户全量表
create table US_User (
  UserId	bigint auto_increment,
  MobileNo	Bigint,
  Email	Varchar(150),
  CreateTime	datetime,
  UpdateTime	datetime,
  IsDel	int,
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户全量表';


-- 2. US_Login 用户登录历史
create table US_Login (
  SN	Bigint auto_increment comment '自增列',
  UserId	bigint comment '用户的SID',
  SignInTime	datetime comment '登录时间',
  ExpireTime	datetime comment '失效时间',
  MobileNo	bigint comment '用户手机号',
  UserIP	varchar(256) comment '用户的IP，大小写不敏感',
  UserType	Int comment '用户类型：0->NONE 1->WEB用户 2->app用户',
  Flag	Int comment '登录是否成功，0成功；1用户名密码错；2服务器内部错误',
  PreSignInTime	Datetime comment '上次登录时间',
  PreExpireTime	DateTime comment '上次失效时间',
  primary key (SN)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户登录历史';


-- 3. US_OperationLog 操作日志表
create table US_OperationLog (
  UserId	bigint,
  OptType	Varchar(10) comment '1.登录 2.互动 3.邀请好友 4.分享链接 5.加好友 6.开通会员 7.保持会员 8.签到',
  OptTime	datetime comment '操作日期',
  primary key (UserId, OptType, OptTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='操作日志表';


-- 4. US_SignInLog 用户签到表记录表
create table US_SignInLog (
  UserId	bigint,
  SignInTime	Datetime comment '签到日期',
  CreateTime	Datetime,
  primary key (UserId, SignInTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户签到表记录表';


-- 5. US_Experience 用户经验值表
create table US_Experience (
  UserId	bigint not null comment '用户ID',
  TotalExperience	Int default 0 comment '累积经验值',
  UsedExperience	Int default 0 comment '已使用经验值',
  CreateTime	Datetime,
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户经验值表';


-- 6. US_Score 用户积分表
create table US_Score (
  UserId	bigint comment '用户ID',
  OnlineScore	Decimal(9,1) default 0 comment '登录积分累积',
  BuddyScore	Decimal(9,1) default 0 comment '好友积分累积',
  BussinessScore	Decimal(9,1) default 0 comment '业务增减积分累积',
  ConsumeScore	Decimal(9,1) default 0 comment '积分消费累计（负值）',
  VipScore	Decimal(9,1) default 0 comment 'Vip积分累计',
  MonthlyOnlineScore	Decimal(9, 1) default 0 comment '当日累计积分',
  MonthlyOnlineVipScore	Decimal(9, 1) default 0 comment '当月VIP累计积分',
  CreateTime	Datetime comment '创建时间',
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户积分表';



-- 7. US_Consume 积分消费历史表
create table US_Consume (
  UserId	bigint comment '用户ID',
  OptTime	Datetime comment '消费时间',
  Score	Decimal(9,1) comment '消费分数',
  PrdCode	Varchar(20) comment '消费编号',
  `Desc`	varchar(128) comment '消费积分描述',
  primary key (UserId, OptTime)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='积分消费历史表';


-- 8. US_DailyScoreLog 日积分历史表
create table US_DailyScoreLog (
  UserId	bigint comment '用户内部ID',
  Day	Date comment '日期',
  OnlineScore	Decimal(9,1) default 0 comment '在线积分',
  BuddyScore	Decimal(9,1) default 0 comment '好友积分',
  BusinessScore	Decimal(9,1) default 0 comment '业务积分',
  VipScore	Decimal(9,1) default 0 comment 'Vip积分',
  primary key (UserId, Day)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='日积分历史表';


-- 9. US_MonthlyScoreLog 月积分历史表
create table US_MonthlyScoreLog (
  UserId	bigint comment '用户内部ID',
  year int comment '年份',
  month	int comment '月份',
  OnlineScore	Decimal(9,1) default 0 comment '在线积分',
  BuddyScore	Decimal(9,1) default 0 comment '好友积分',
  BusinessScore	Decimal(9,1) default 0 comment '业务积分',
  VipScore	Decimal(9,1) default 0 comment 'Vip积分',
  primary key (UserId, year, month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='月积分历史表';


-- 10. US_YearlyScoreLog 年积分历史表
create table US_YearlyScoreLog (
  UserId	bigint comment '用户内部ID',
  year	Int comment '年份',
  AvailableScore	Decimal(9,1) comment '当前年份可消费积分',
  OnlineScore	Decimal(9,1) default 0 comment '在线积分',
  BuddyScore	Decimal(9,1) default 0 comment '好友积分',
  BusinessScore	Decimal(9,1) default 0 comment '业务积分',
  VipScore	Decimal(9,1) default 0 comment 'Vip积分',
  primary key (UserId, year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='年积分历史表';


-- 11. US_ExperienceLog 用户经验等级日志表
create table US_ExperienceLog (
  UserId	bigint comment '用户id',
  Level	SmallInt comment '用户等级',
  ReachTime	DateTime comment '到达此等级的时间',
  primary key (UserId, Level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户经验等级日志表';


-- 12. US_MonthScore 月积分表
create table US_MonthScore (
  UserId	bigint comment '用户id',
  Time	datetime comment '月份',
  OnlineScore	Int comment '在线积分',
  Status	Tinyint comment '0-	未处理状态 1-已被提取 等待前台计算好友积分返回',
  primary key (UserId, Time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='月积分表';


-- 13. US_RankLevel 等级对应表
create table US_RankLevel (
  Rank	Smallint comment '等级',
  Score	int comment '等级对应的经验值',
  primary key (Rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='等级积分对应表';

-- 1级	经验值  150
-- 2级	经验值  400
-- 3级	经验值  1200
-- 4级	经验值  2400
-- 5级	经验值  4000
-- 6级	经验值  6000
-- 7级	经验值  9750
-- 8级	经验值  18000
-- 9级	经验值  28750
insert into US_RankLevel(Rank, Score) values(1, 150);
insert into US_RankLevel(Rank, Score) values(2, 400);
insert into US_RankLevel(Rank, Score) values(3, 1200);
insert into US_RankLevel(Rank, Score) values(4, 2400);
insert into US_RankLevel(Rank, Score) values(5, 4000);
insert into US_RankLevel(Rank, Score) values(6, 6000);
insert into US_RankLevel(Rank, Score) values(7, 9750);
insert into US_RankLevel(Rank, Score) values(8, 18000);
insert into US_RankLevel(Rank, Score) values(9, 28750);



-- 14. US_RankLog 用户等级日志表
create table US_RankLog (
  UserId	bigint comment '用户Id',
  Rank	SmallInt comment '用户等级',
  ReachTime	DateTime comment '到达此等级的时间',
  primary key (UserId, Rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户等级日志表';


-- 15. US_UserOnline 用户在线时间表(只记录当天的)
create table US_UserOnline (
  UserId	bigint comment '',
  PCOnlineSecond	Int default 0 comment 'PC客户端在线时长(秒)',
  MobileOnlineSecond	Int default 0 comment '手机客户端在线时长(秒)',
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户在线时间表(只记录当天的)';


-- 16. US_Process 安全策略处理汇总表
create table US_Process (
  UserId	bigint,
  ProcessNum	Int comment '累积次数',
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='安全策略处理汇总表';


-- 17. US_ProcessLogSecurityPolicy 处理日志
create table US_ProcessLogSecurityPolicy (
  LogId	bigint comment '' auto_increment,
  ObjectId	varchar(48) comment '',
  ObjectData	nvarchar(512) comment '数据内容,举报信息或投诉信息等',
  ObjectType	tinyint comment '其中收集人、收集原因也写入该字段',
  ProcessRule	varchar(64) comment '对象ID：2消息摘要100：UserId 101：mobileNo',
  ProcessReason	int comment '处理规则Code.GroupId:ProcessRule',
  ProcessType	smallint comment '处理原因Code.GroupId:ProcessReason',
  ProcessResult	smallint comment '1加黑,2踢下线,3封禁',
  ProcessDesc	nvarchar(256) comment '处理结果Code.GroupId:ProcessResult',
  ProcessDate	datetime comment '处理结果原因',
  primary key (LogId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='处理日志';


-- 18. US_Credit 用户信用分值表
create table US_Credit (
  UserId	bigint,
  CreditScore	Decimal(18,2),
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户信用分值表';


-- 19. US_Credit_Log 用户信用分值历史Log
create table US_Credit_Log (
  UserId	bigint comment '',
  Time	datetime comment '',
  CreditScore	Decimal(18,2) comment '',
  primary key (UserId, Time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户信用分值历史Log';

