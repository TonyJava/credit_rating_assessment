package cn.goldlone.credit.utils

import cn.goldlone.credit.common.IdentityType
import com.alibaba.fastjson.{JSON, JSONObject}

/**
  *
  * @author Created by CN on 2018/12/19/0019 20:13 .
  */
object RedisUtil {
  
  /** 分割符 */
  private val SPLIT: String = ":"

  /** 用户信息 */
  val USER = "user"
  
  /** 连续签到次数 */
  private val USER_SIGN_IN = "sign_in"
  /** 最后一次签到日期 */
  private val USER_LAST_SIGN_DATE = "last_sign_date"
  /** 经验值 */
  private val USER_TOTAL_EXPERIENCE = "total_experience"
  /** 积分 */
  private val USER_TOTAL_SCORE = "total_score"
  /** 当前等级 */
  private val USER_CURRENT_LEVEL = "current_level"
  /** 身份 => 1:普通用户 2:月费会员 3:年费会员 */
  private val USER_IDENTITY = "identity"
  /** VIP失效时间 */
  private val USER_VIP_EXPIRED_TIME = "vip_expired_time"
  
  
  
  
  /**
    * 获取的存入用户信息的key
    * @param userId 用户id
    * @return
    */
  def getUserField(userId: Long): String = String.valueOf(userId)
  
  def getUserSignInField: String = USER_SIGN_IN
  
  def getUserLastSignDateField: String = USER_LAST_SIGN_DATE
  
  def getUserTotalExperienceField: String = USER_TOTAL_EXPERIENCE
  
  def getUserTotalScoreField: String = USER_TOTAL_SCORE
  
  def getUserCurrentLevelField: String = USER_CURRENT_LEVEL
  
  def getUserIdentityField: String = USER_IDENTITY
  
  def getUserVipExpiredTimeField: String = USER_VIP_EXPIRED_TIME
  
  /**
    * 从Redis中读取用户数据
    * @param userId 用户id
    * @return
    */
  def getUserData(userId: Long): JSONObject = {
    val info = JedisUtil.hget(RedisUtil.USER, RedisUtil.getUserField(userId))
    var infoJson: JSONObject = null
    if(info != null) {
      infoJson = JSON.parseObject(info)
    } else {
      infoJson = new JSONObject()
      infoJson.put(RedisUtil.getUserSignInField, 1)
      infoJson.put(RedisUtil.getUserLastSignDateField, "")
      infoJson.put(RedisUtil.getUserTotalExperienceField, 0)
      infoJson.put(RedisUtil.getUserTotalScoreField, 0.0)
      infoJson.put(RedisUtil.getUserCurrentLevelField, 0)
      infoJson.put(RedisUtil.getUserIdentityField, IdentityType.GENERAL_USER)
      infoJson.put(RedisUtil.getUserVipExpiredTimeField, "")
      JedisUtil.hset(RedisUtil.USER, RedisUtil.getUserField(userId), infoJson.toString)
    }
    
    infoJson
  }
  
  
}
