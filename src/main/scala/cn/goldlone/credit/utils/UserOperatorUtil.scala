package cn.goldlone.credit.utils

import java.sql.Timestamp
import java.util.{Calendar, Date}
import java.text.{ParseException, SimpleDateFormat}

import cn.goldlone.credit.common.{IdentityType, ScoreType}
import org.apache.commons.lang.StringUtils

import scala.collection.mutable

/**
  * 用户操作工具类
  * @author Created by CN on 2018/12/19/0019 21:08 .
  */
object UserOperatorUtil {

  private val dbUtil = new DBUtil
  
  /** 等级缓存 */
  private var rankLevelCache = new mutable.HashMap[Int, Int]()
  
  /**
    * 更新积分
    * 如果是VIP类型操作，也会更新用户身份
    * @param userId 用户id
    * @param scoreType 1:登录 2:好友 3:业务 4:Vip
    * @param incScore 增加的积分
    */
  def updateScore(userId: Long, scoreType: Int, incScore: Double): Unit = {
  
    val infoJson = RedisUtil.getUserData(userId)
    // 检测其身份
    var identity: Int = infoJson.getIntValue(RedisUtil.getUserIdentityField)
    var vipExpiredTime = infoJson.getString(RedisUtil.getUserVipExpiredTimeField)
    // 判断VIP是否过期
    if(identity == IdentityType.MONTHLY_MEMBER ||
        identity == IdentityType.ANNUAL_MEMBER) {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      try {
        val expired = sdf.parse(vipExpiredTime)
        if(expired.after(new Date())) {
          identity = IdentityType.EXPIRED_MEMBER
        }
      } catch {
        case e: ParseException =>
          identity = IdentityType.GENERAL_USER
      }
    }
  
    var totalScore = infoJson.getDoubleValue(RedisUtil.getUserTotalScoreField)
    // 计算VIP累积积分，非VIP为0
    val incVipScore = getVipScore(identity, incScore)
    var sql = ""
    // 匹配操作
    scoreType match {
      case ScoreType.LOGIN => // 登录累积积分
        // 计算总积分
        sql = "insert " +
            "into US_Score(UserId, OnlineScore, VipScore, CreateTime) " +
            "values(?, ?, ?, now()) " +
            "on duplicate key update OnlineScore = OnlineScore + ?, " +
            "   VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incVipScore, incScore, incVipScore))
        // 计算日积分
        sql = "insert " +
            "into US_DailyScoreLog(UserId, Day, OnlineScore, VipScore) " +
            "values(?, now(), ?, ?) " +
            "on duplicate key update OnlineScore = OnlineScore + ?, " +
            "   VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incVipScore, incScore, incVipScore))
        totalScore += incScore + incVipScore
        
      case ScoreType.BUDDY => // 好友积分累积
        // 计算总积分
        sql = "insert " +
            "into US_Score(UserId, BuddyScore, VipScore, CreateTime) " +
            "values(?, ?, ?, now()) " +
            "on duplicate key update BuddyScore = BuddyScore + ?, " +
            "   VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incVipScore, incScore, incVipScore))
        // 计算日积分
        sql = "insert " +
            "into US_DailyScoreLog(UserId, Day, BuddyScore, VipScore) " +
            "values(?, now(), ?, ?) " +
            "on duplicate key update BuddyScore = BuddyScore + ?, " +
            "   VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incVipScore, incScore, incVipScore))
        totalScore += incScore + incVipScore
        
      case ScoreType.BUSINESS => // 业务增减积分累积
        sql = "insert " +
            "into US_Score(UserId, BussinessScore, VipScore, CreateTime) " +
            "values(?, ?, ?, now()) " +
            "on duplicate key update BussinessScore = BussinessScore + ?, " +
            "   VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incVipScore, incScore, incVipScore))
        sql = "insert " +
            "into US_DailyScoreLog(UserId, Day, BusinessScore, VipScore) " +
            "values(?, now(), ?, ?) " +
            "on duplicate key update BusinessScore = BusinessScore + ?, " +
            "   VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incVipScore, incScore, incVipScore))
        totalScore += incScore + incVipScore
        
      case ScoreType.VIP_FIRST => // 首次VIP
        if(identity == IdentityType.GENERAL_USER) {
          // 计算总积分
          sql = "insert " +
              "into US_Score(UserId, VipScore, CreateTime) " +
              "values(?, ?, now()) " +
              "on duplicate key update VipScore = VipScore + ? "
          dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incScore))
          // 计算日积分
          sql = "insert " +
              "into US_DailyScoreLog(UserId, Day, VipScore) " +
              "values(?, now(), ?) " +
              "on duplicate key update VipScore = VipScore + ? "
          dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incScore))
          totalScore += incScore
          
          // 修改VIP失效时间
          identity = IdentityType.MONTHLY_MEMBER
          val cal = Calendar.getInstance()
          cal.setTimeInMillis(new Date().getTime + (1000*60*60*24*30))
          vipExpiredTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(cal.getTime)
          infoJson.put(RedisUtil.getUserVipExpiredTimeField, vipExpiredTime)
        }
        
      case ScoreType.VIP_NEXT => // 续费VIP
        sql = "insert " +
            "into US_Score(UserId, VipScore, CreateTime) " +
            "values(?, ?, now()) " +
            "on duplicate key update VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incScore))
        sql = "insert " +
            "into US_DailyScoreLog(UserId, Day, VipScore) " +
            "values(?, now(), ?) " +
            "on duplicate key update VipScore = VipScore + ? "
        dbUtil.executeInsertOrUpdate(sql, Array(userId, incScore, incScore))
  
        totalScore += incScore
        
        // 修改VIP失效时间
        val cal = Calendar.getInstance()
        val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        if (identity==IdentityType.MONTHLY_MEMBER || identity==IdentityType.ANNUAL_MEMBER) {
          cal.setTimeInMillis(sdf.parse(vipExpiredTime).getTime + (1000*60*60*24*30))
        }  else {
          cal.setTimeInMillis(new Date().getTime + (1000*60*60*24*30))
        }
        vipExpiredTime = sdf.format(cal.getTime)
        infoJson.put(RedisUtil.getUserVipExpiredTimeField, vipExpiredTime)
    }
  
    infoJson.put(RedisUtil.getUserTotalScoreField, totalScore)
    infoJson.put(RedisUtil.getUserIdentityField, identity)
    JedisUtil.hset(RedisUtil.USER, RedisUtil.getUserField(userId), infoJson.toString)
  }
  
  /**
    * 获取应该增加的VIP积分
    * @param identity 身份id
    * @param incScore 增加的积分
    * @return
    */
  private def getVipScore(identity:Int, incScore: Double): Double = {
    identity match {
      case IdentityType.MONTHLY_MEMBER => incScore * 0.1
      case IdentityType.ANNUAL_MEMBER => incScore * 0.2
      case _ => 0
    }
  }
  
  
  /**
    * 用户签到
    * @param userId 用户id
    * @param signInTime 签到时间
    */
  def signIn(userId: Long, signInTime: Timestamp): Unit = {
    val infoJson = RedisUtil.getUserData(userId)
  
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    // 获取Redis中存储的用户信息
    var continuousSignIn = infoJson.getIntValue(RedisUtil.getUserSignInField)
    var totalScore = infoJson.getDoubleValue(RedisUtil.getUserTotalScoreField)
    var totalExperience = infoJson.getDoubleValue(RedisUtil.getUserTotalExperienceField)
    val lastSignDateStr = infoJson.getString(RedisUtil.getUserLastSignDateField)
  
    // 检测是否存在上次的签到信息
    if(StringUtils.isNotBlank(lastSignDateStr)) {
      val lastSignDate = sdf.parse(lastSignDateStr)
      val diff = ((sdf.parse(sdf.format(signInTime)).getTime -
          lastSignDate.getTime) / (24 * 60 * 60 * 1000)).toInt
      
      diff match {
        case 0 => // 当天内连续签到
        case 1 => // 连续签到
          // 累加总积分
          totalScore += 1
          updateScore(userId, ScoreType.BUSINESS, 1)
        
          // 计算连续的经验值
          continuousSignIn += 1
          var exp = 1
          for(i <- 1 to continuousSignIn) {
            exp *= 2
          }
          totalExperience += exp
          // 增加的经验值
          updateTotalExperience(userId, exp)
        case _ => // 签到中断
          continuousSignIn = 1
          totalExperience += 1
          updateTotalExperience(userId, 1)
      }
    } else {
      totalScore += 1
      totalExperience += 1
      continuousSignIn = 1
    }
  
    // 写入JSON
    infoJson.put(RedisUtil.getUserLastSignDateField, sdf.format(signInTime))
    infoJson.put(RedisUtil.getUserSignInField, continuousSignIn)
    infoJson.put(RedisUtil.getUserTotalScoreField, totalScore)
    infoJson.put(RedisUtil.getUserTotalExperienceField, totalExperience)
    JedisUtil.hset(RedisUtil.USER, RedisUtil.getUserField(userId), infoJson.toString)
    
    val sql = "insert " +
        "into US_SignInLog(UserId, SignInTime, CreateTime) " +
        "values(?, ?, now())"
    
    dbUtil.executeInsertOrUpdate(sql, Array(userId, signInTime))
  }
  
  
  
  /**
    * 更新经验值
    * @param userId 用户id
    * @param incExperience 增加的经验值
    */
  def updateTotalExperience(userId: Long, incExperience: Int): Unit = {
    // 更新经验值
    var sql = "insert " +
        "into US_Experience(UserId, TotalExperience, CreateTime) " +
        "values(?, ?, now()) " +
        "on duplicate key update TotalExperience = TotalExperience + ?"
    dbUtil.executeInsertOrUpdate(sql, Array(userId, incExperience, incExperience))
    
    // 查询当前的经验值
    sql = "select TotalExperience " +
        "from US_Experience " +
        "where UserId = ?"
    var rs = dbUtil.executeQuery(sql, Array(userId))
    // 如果没有找到经验记录则退出（不太可能）
    if(!rs.next()) {
      return
    }
    
    val totalExperience = rs.getInt(1)
    
    // 查询等级记录表，并加载至缓存
    if(rankLevelCache.isEmpty) {
      sql = "select Rank,Score from US_RankLevel"
      rs = dbUtil.executeQuery(sql, null)
      while(rs.next()) {
        rankLevelCache += ((rs.getInt(1), rs.getInt(2)))
      }
    }
    
    var currentLevel = 0
    
    // 查询当前的等级
    sql = "select Rank " +
        "from US_RankLog " +
        "where UserId = ? " +
        "order by ReachTime desc " +
        "limit 1"
    rs = dbUtil.executeQuery(sql, Array(userId))
    // 如果存在等级记录
    if(rs.next()) {
      currentLevel = rs.getInt(1)
    }
    
    // 决定是否添加等级记录
    val newLevel = getRankLevel(currentLevel, totalExperience)
    if(newLevel != currentLevel) {
      sql = "insert " +
          "into US_RankLog(UserId, Rank, ReachTime) " +
          "values(?, ?, now())"
      dbUtil.executeInsertOrUpdate(sql, Array(userId, newLevel))
      
      // 同时更新Redis中的等级
      val info = RedisUtil.getUserData(userId)
      info.put(RedisUtil.getUserCurrentLevelField, newLevel)
      JedisUtil.hset(RedisUtil.USER, RedisUtil.getUserField(userId), info.toString)
    }
  }
  
  /**
    * 计算等级
    * @param currentLevel 当前等级
    * @param totalExperience 当前总经验值
    * @return
    */
  def getRankLevel(currentLevel: Int, totalExperience: Int): Int = {
    var newLevel = currentLevel
    
    if(totalExperience >= rankLevelCache(currentLevel + 1)) {
      newLevel = getRankLevel(currentLevel+1, totalExperience)
    }
    
    newLevel
  }
  

  
}
