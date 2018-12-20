package cn.goldlone.credit.prepare

import java.sql.{Date, ResultSet}
import java.text.SimpleDateFormat

import cn.goldlone.credit.utils.{DBUtil, JedisUtil, RedisUtil}
import com.alibaba.fastjson.JSONObject

/**
  * 废弃无用
  * @author Created by CN on 2018/12/19/0019 20:31 .
  */
@Deprecated
object LoadRedis {
  
  def main(args: Array[String]): Unit = {
    
    val dbUtil = new DBUtil
    
    val sql = "select UserId from US_User"
    var rs = dbUtil.executeQuery(sql, null)
    
    while(rs.next()) {
      val userId = rs.getInt(1)
      loadRedis(userId)
    }
    
  }
  
  
  /**
    * 加载到Redis
    * @param userId 用户id
    * @return
    */
  def loadRedis(userId: Int): Unit = {
    val dbUtil = new DBUtil
    var sql: String = ""
    var rs: ResultSet = null
  
    // 1. 连续签到次数
    // 2. 最后一次签到日期
    var continuousSignIn: Int = 0
    var lastSignDate: String = ""
    var lastSignDateTmp: Date = null
    
    sql = "select SignInTime " +
        "from US_SignInLog " +
        "where UserId = ? " +
        "order by SignInTime desc"
    rs = dbUtil.executeQuery(sql, Array(userId))
    if(rs.next()) {
      val sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      lastSignDate = sdf2.format(rs.getDate(1))
      lastSignDateTmp = rs.getDate(1)
      continuousSignIn += 1
    }
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    var isLoop = true
    while(rs.next() && isLoop) {
      val currentSign = rs.getDate(1)
      val diff = ((sdf.parse(sdf.format(lastSignDateTmp)).getTime -
          sdf.parse(sdf.format(currentSign)).getTime) / (24 * 60 * 60 * 1000)).toInt
      if(diff > 1) {
        isLoop = false
      }
      
      diff match {
        case 1 =>
          continuousSignIn += 1
          lastSignDateTmp = currentSign
        case 0 =>
          println("同一天签到，不记录连续签到")
        case _ => isLoop = false
      }
    }
  
    // 3. 经验值
    var totalExperience = 0
    sql = "select TotalExperience from US_Experience where UserId = ?"
    rs = dbUtil.executeQuery(sql, Array(userId))
    if(rs.next()) {
      totalExperience = rs.getInt(1)
    }
    
    // 4. 积分
    var totalScore: Double = 0.0
    sql = "select OnlineScore, BuddyScore, BussinessScore, VipScore " +
        "from US_Score " +
        "where UserId = ?"
    rs = dbUtil.executeQuery(sql, Array(userId))
    if(rs.next()) {
      val OnlineScore = rs.getDouble(1)
      val BuddyScore = rs.getDouble(2)
      val BusinessScore = rs.getDouble(3)
      val ConsumeScore = rs.getDouble(4)
      
      totalScore = OnlineScore + BuddyScore + BusinessScore + ConsumeScore
    }
  
    // 5. 当前等级
    var currentLevel = 0
    sql = "select Level from US_ExperienceLog where UserId = ?"
    rs = dbUtil.executeQuery(sql, Array(userId))
    if(rs.next()) {
      currentLevel = rs.getInt(1)
    }
    
    
    val json = new JSONObject()
    json.put(RedisUtil.getUserSignInField, continuousSignIn)
    json.put(RedisUtil.getUserLastSignDateField, lastSignDate)
    json.put(RedisUtil.getUserTotalExperienceField, totalExperience)
    json.put(RedisUtil.getUserTotalScoreField, totalScore)
    json.put(RedisUtil.getUserCurrentLevelField, currentLevel)
    
    JedisUtil.hset(RedisUtil.USER, RedisUtil.getUserField(userId), json.toString)
  }
  
  
}
