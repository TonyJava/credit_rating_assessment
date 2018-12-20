
-- ===========================
--              函数
-- ===========================

-- 创建函数，生成随机昵称
DELIMITER //
DROP FUNCTION IF  EXISTS rand_nick_name //
CREATE  FUNCTION rand_nick_name(n INT) RETURNS VARCHAR(255)
BEGIN
  DECLARE chars VARCHAR(62) DEFAULT 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  DECLARE result VARCHAR(255) DEFAULT '';
  DECLARE i INT DEFAULT 0;
  WHILE i < n DO
    SET result = CONCAT(result,SUBSTRING(chars , FLOOR(1 + RAND()*62 ), 1));
    SET i = i +1;
  END WHILE;
  RETURN result;
END //

-- 创建函数，生成随机电话手机号
DELIMITER //
DROP FUNCTION IF  EXISTS rand_mobile //
CREATE FUNCTION rand_mobile() RETURNS BIGINT
BEGIN
  DECLARE result BIGINT;
  SET  result = FLOOR(13000000000 + RAND() * (20000000000 - 13000000000 + 1));
  RETURN result;
END //


-- 创建函数，生成随机的身份证号码
DELIMITER //
DROP FUNCTION IF  EXISTS rand_identity //
CREATE  FUNCTION rand_identity() RETURNS BIGINT
BEGIN
  DECLARE result BIGINT;
  DECLARE i_year VARCHAR(4);
  DECLARE i_month VARCHAR(2);
  DECLARE i_day VARCHAR(2);
  DECLARE pre VARCHAR(6);
  DECLARE suffix VARCHAR(4);
  SET i_year =CONCAT('',FLOOR(1900 + RAND() * (2018 - 1900 + 1)));
  SET i_month=RIGHT(CONCAT('00',FLOOR(1 + RAND() * 13)),2);
  SET i_day=RIGHT(CONCAT('00',FLOOR(1 + RAND() * 32)),2);
  SET pre =FLOOR(110000 + RAND() * (830000 - 110000 + 1));
  SET suffix=RIGHT(CONCAT('0000',FLOOR(RAND() * 100000)),4);
  -- SET  result =FLOOR(110000000000000000 + RAND() * (1100000000000000000 - 110000000000000000 + 1));
  SET result = CONCAT(pre,i_year,i_month,i_day,suffix);
  RETURN result;
END //


-- 创建函数，随机生成IP
DELIMITER //
DROP FUNCTION IF  EXISTS rand_ip //
CREATE  FUNCTION rand_ip() RETURNS VARCHAR(32)
BEGIN
  DECLARE result VARCHAR(15);
  DECLARE i_1 VARCHAR(3);
  DECLARE i_2 VARCHAR(3);
  DECLARE i_3 VARCHAR(3);
  DECLARE i_4 VARCHAR(3);

  SET i_1 = FLOOR(36 + RAND() * (222 - 36 + 1));
  SET i_2 = FLOOR(0 + RAND() * (255 - 0 + 1));
  SET i_3 = FLOOR(0 + RAND() * (255 - 0 + 1));
  SET i_4 = FLOOR(0 + RAND() * (255 - 0 + 1));
  SET result = CONCAT('',i_1,'.',i_2,'.',i_3,'.',i_4);
  RETURN result;
END //


-- 创建函数，随机生成注册时间
DELIMITER //
DROP FUNCTION IF  EXISTS rand_register_date //
CREATE  FUNCTION rand_register_date() RETURNS DATETIME
BEGIN
  DECLARE result VARCHAR(32);
  DECLARE i_year VARCHAR(4);
  DECLARE i_month VARCHAR(2);
  DECLARE i_day VARCHAR(2);
  SET @dt = '2008-1-1';
  SET @diff = FLOOR( RAND() * DATEDIFF(NOW(),@dt));
  RETURN DATE_ADD(@dt,INTERVAL @diff DAY);
  /*
  SET i_year =CONCAT('',FLOOR(2008 + RAND() * (2018 - 2008 + 1)));
  SET i_month=RIGHT(CONCAT('00',FLOOR(1 + RAND() * 13)),2);
  SET i_day=RIGHT(CONCAT('00',FLOOR(1 + RAND() * 32)),2);
  SET result = CONCAT(i_year,'-',i_month,'-',i_day);
   RETURN str_to_date(result,'%Y-%m-%d');
   */
END //



-- ===========================
--          存储过程
-- ===========================

-- 创建存储过程，创建用户
DELIMITER //
DROP PROCEDURE IF EXISTS usp_create_user //
CREATE PROCEDURE usp_create_user(row_cnt INT)
BEGIN
  SET @rn = 0;
  SET @register_date :=rand_register_date();
  WHILE(@rn < row_cnt) DO
    # SET @nick_name := rand_nick_name(8);
    SET @e_mail := CONCAT(@nick_name,'@126.com');
    SET @register_date :=rand_register_date();
    SET @mobile := rand_mobile();
    /*
    while exists(select 1 from us_user where mobile_number=@mobile) do
      SET @mobile=rand_mobile();
    end while;
    */
    INSERT
    INTO US_User(MobileNo, Email, CreateTime, UpdateTime, IsDel)
    VALUES (@mobile, @e_mail, @register_date, @register_date, 0);
    SET @rn = @rn + 1;
  END WHILE;
END //



-- 创建存储过程，执行登录操作
DELIMITER //
DROP PROCEDURE IF EXISTS usp_create_login //
CREATE PROCEDURE usp_create_login(row_cnt INT)
BEGIN
  SET @rn=0;
  WHILE(@rn < row_cnt)
  DO
    -- 取最小和最大的用户id
    SELECT MAX(UserId),MIN(UserId) INTO @max_id, @min_id
    FROM US_User;

    SET @user_id :=  FLOOR(@min_id + RAND() * (@max_id - @min_id + 1));
    SET @dt = '2008-1-1';
    SET @diff = FLOOR(RAND() * 24);

    SELECT
      MobileNo,
      DATE_ADD(CreateTime, INTERVAL @diff + 1 DAY),
      DATE_ADD(DATE_ADD(CreateTime, INTERVAL @diff + 1 DAY), INTERVAL @diff HOUR),
      rand_ip(),
      DATE_ADD(CreateTime, INTERVAL @diff DAY),
      DATE_ADD(DATE_ADD(CreateTime, INTERVAL @diff DAY), INTERVAL @diff HOUR)
    INTO @mobile, @login_time, @logout_time, @ip, @pre_login, @pre_logout
    FROM US_User
    WHERE UserId = @user_id;

    INSERT
    INTO US_Login (
      UserId,
      SignInTime,
      ExpireTime,
      MobileNo,
      UserIP,
      UserType,
      Flag,
      PreSignInTime,
      PreExpireTime)
    SELECT @user_id,
      @login_time,
      @logout_time,
      @mobile,
      @ip,
      0,
      0,
      @pre_login,
      @pre_logout;

    SET @rn = @rn+1;

    -- 插入用户操作
    SET @rn_opt = 0;
    SET @opt_cnt = FLOOR(RAND() * 9);
    WHILE(@rn_opt < @opt_cnt) DO

      SET @opt_type = FLOOR(1 + RAND() * 8);
      SET @diff = TIMESTAMPDIFF(MINUTE,@pre_login,@pre_logout);
      SET @diff_minute = FLOOR(1 + RAND() * @diff);
      SET @opt_time = DATE_ADD(@pre_login,INTERVAL @diff_minute MINUTE);

      INSERT
      INTO US_OperationLog (
        UserId,
        OptType,
        OptTime)
      SELECT @user_id,
        @opt_type,
        @opt_time;


      SET @rn_opt = @rn_opt +1;
    END WHILE;

  END WHILE;
END //





-- 创建定时任务来执行用户注册,登录，用户操作
DELIMITER //
DROP EVENT IF EXISTS e_user_credit_add//
CREATE EVENT e_user_credit_add
  ON SCHEDULE EVERY 1 SECOND STARTS TIMESTAMP '2018-12-19 12:00:00' -- 设置每秒，根据自己需要进行设定
  ON COMPLETION PRESERVE
DO
BEGIN
#   CALL usp_create_user(1000);
  CALL usp_create_login(10000);
END //



-- 其他操作

select count(1) from US_User;
select count(1) from US_Login;
select count(1) from US_OperationLog;

select distinct OptType from US_OperationLog;
delete from US_OperationLog where OptType = 0;


-- 数据生成一定量后，需要对一些表数据进行排序
ALTER TABLE US_Login RENAME TO US_Login_2;
ALTER TABLE US_OperationLog RENAME TO US_OperationLog_2;

create table US_Login
as
select *
from US_Login_2
order by SignInTime asc;
drop table US_Login_2;

create table US_OperationLog
as
select *
from US_OperationLog_2
order by OptTime asc;
drop table US_OperationLog_2;


-- 4028
select count(distinct date_format(SignInTime, '%Y-%m-%d')) from US_Login;
select count(distinct date_format(OptTime, '%Y-%m-%d')) from US_OperationLog;
-- 13
select count(distinct date_format(SignInTime, '%Y-%m')) from US_Login;
select count(distinct date_format(OptTime, '%Y-%m')) from US_OperationLog;
-- 12
select count(distinct date_format(SignInTime, '%Y')) from US_Login;
select count(distinct date_format(OptTime, '%Y')) from US_OperationLog;

delete from US_OperationLog where OptType = '9';
