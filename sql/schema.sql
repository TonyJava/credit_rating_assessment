
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
  UserId	int comment '用户的SID',
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
  OptTime	datetime comment '操作日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='操作日志表';


-- 4. US_SignInLog 用户签到表记录表
create table US_SignInLog (
  UserId	bigint,
  SignInTime	Datetime comment '签到日期',
  CreateTime	Datetime
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户签到表记录表';


-- 5. US_Experience 用户经验值表
create table US_Experience (
  UserId	Int not null comment '用户ID',
  TotalExperience	Int comment '累积经验值',
  UsedExperience	Int comment '已使用经验值',
  CreateTime	Datetime,
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户经验值表';


-- 6. US_Score 用户积分表
create table US_Score (
  UserId	Int comment '用户ID',
  OnlineScore	Decimal(9,1) comment '登录积分累积',
  BuddyScore	Decimal(9,1) comment '好友积分累积',
  BussinessScore	Decimal(9,1) comment '业务增减积分累积',
  ConsumeScore	Decimal(9,1) comment '积分消费累计（负值）',
  VipScore	Decimal(9,1) comment 'Vip积分累计',
  MonthlyOnlineScore	Decimal(9, 1) comment '当日累计积分',
  MonthlyOnlineVipScore	Decimal(9, 1) comment '当月VIP累计积分',
  CreateTime	Datetime comment '',
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户积分表';



-- 7. US_Consume 积分消费历史表
create table US_Consume (
  UserId	Int comment '用户ID',
  OptTime	Datetime comment '消费时间',
  Score	Decimal(9,1) comment '消费分数',
  PrdCode	Varchar(20) comment '消费编号',
  `Desc`	varchar(128) comment '消费积分描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='积分消费历史表';


-- 8. US_DailyScoreLog 日积分历史表
create table US_DailyScoreLog (
  UserId	Int comment '用户内部ID',
  Day	Date comment '日期',
  OnlineScore	Decimal(9,1) comment '在线积分',
  BuddyScore	Decimal(9,1) comment '好友积分',
  VipScore	Decimal(9,1) comment 'Vip积分',
  primary key (UserId, Day)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='日积分历史表';


-- 9. US_MonthlyScoreLog 月积分历史表
create table US_MonthlyScoreLog (
  UserId	Int comment '用户内部ID',
  Time	Datetime comment '月份',
  OnlineScore	Decimal(9,1) comment '在线积分',
  BuddyScore	Decimal(9,1) comment '好友积分',
  VipScore	Decimal(9,1) comment 'Vip积分',
  primary key (UserId, Time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='月积分历史表';


-- 10. US_YearlyScoreLog 年积分历史表
create table US_YearlyScoreLog (
  UserId	Int comment '用户内部ID',
  Time	Int comment '年份',
  AvailableScore	Decimal(9,1) comment '当前年份可消费积分',
  OnlineScore	Decimal(9,1) comment '在线积分',
  BuddyScore	Decimal(9,1) comment '好友积分',
  VipScore	Decimal(9,1) comment 'Vip积分',
  primary key (UserId, Time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='年积分历史表';


-- 11. US_ExperienceLog 用户经验等级日志表
create table US_ExperienceLog (
  UserId	int comment '',
  ReachTime	DateTime comment '到达此等级的时间',
  Level	SmallInt comment '用户等级'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户经验等级日志表';


-- 12. US_MonthScore 月积分表
create table US_MonthScore (
  UserId	Int comment '',
  Time	datetime comment '月份',
  OnlineScore	Int comment '在线积分',
  Status	Tinyint comment '0-	未处理状态 1-已被提取 等待前台计算好友积分返回',
  primary key (UserId, Time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='月积分表';


-- 13. US_RankLevel 等级积分对应表
create table US_RankLevel (
  Rank	Smallint comment '等级',
  Score	int comment '等级对应的积分值',
  primary key (Rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='等级积分对应表';

insert into US_RankLevel(Rank, Score) values(1, 0);
insert into US_RankLevel(Rank, Score) values(2, 10);
insert into US_RankLevel(Rank, Score) values(3, 20);
insert into US_RankLevel(Rank, Score) values(4, 30);
insert into US_RankLevel(Rank, Score) values(5, 40);
insert into US_RankLevel(Rank, Score) values(6, 50);
insert into US_RankLevel(Rank, Score) values(7, 60);
insert into US_RankLevel(Rank, Score) values(8, 70);
insert into US_RankLevel(Rank, Score) values(9, 80);
insert into US_RankLevel(Rank, Score) values(10, 90);
insert into US_RankLevel(Rank, Score) values(11, 100);

-- 等级I级 	0级-1级 	(0-10]分
-- 等级H级 	2级-3级 	(11-20]分
-- 等级G级 	4级-5级 	(21-30]分
-- 等级F级 	6级-7级	(31-40]分
-- 等级E级 	8级-9级	(41-50]分
-- 等级D级 	10级-12级	(51-60]分
-- 等级C级 	13级-17级 	(61-80]分
-- 等级B级 	18级-22级 	(81-100]分
-- 等级A级 	23级及以上	100分



-- 14. US_RankLog 用户等级日志表
create table US_RankLog (
  UserId	int comment '手机号',
  Rank	SmallInt comment '用户等级',
  ReachTime	DateTime comment '到达此等级的时间',
  primary key (UserId, Rank)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户等级日志表';


-- 15. US_UserOnline 用户在线时间表(只记录当天的)
create table US_UserOnline (
  UserId	Int comment '',
  PCOnlineSecond	Int default 0 comment 'PC客户端在线时长(秒)',
  MobileOnlineSecond	Int default 0  comment '手机客户端在线时长(秒)',
  primary key (UserId)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户在线时间表(只记录当天的)';


-- 16. US_Process 安全策略处理汇总表
create table US_Process (
  UserId	Int,
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
  UserId	Int,
  Time	datetime,
  CreditScore	Decimal(18,2),
  primary key (UserId, Time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户信用分值表';


-- 19. US_Credit_Log 用户信用分值历史Log
create table US_Credit_Log (
  UserId	Int comment '',
  Time	datetime comment '',
  CreditScore	Decimal(18,2) comment '',
  primary key (UserId, Time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT COMMENT='用户信用分值历史Log';






































