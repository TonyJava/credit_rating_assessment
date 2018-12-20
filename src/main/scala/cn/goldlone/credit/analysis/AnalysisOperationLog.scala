package cn.goldlone.credit.analysis

import cn.goldlone.credit.common.ScoreType
import cn.goldlone.credit.utils.UserOperatorUtil
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkConf, SparkContext}

/**
  * 把历史数据跑个遍
  * @author Created by CN on 2018/12/19/0019 23:04 .
  */
object AnalysisOperationLog {
  
  def main(args: Array[String]): Unit = {
  
    val conf = new SparkConf().setMaster("local[*]").setAppName("run-all-logs")
    val sc = new SparkContext(conf)
    val sqlContext = new SQLContext(sc)
    
    
    val reader = sqlContext.read.format("jdbc")
        .options(Map(
          ("driver", "com.mysql.jdbc.Driver"),
          ("url", "jdbc:mysql://hh:3306/credit_rating?useUnicode=true&characterEncoding=utf8"),
          ("user", "hive"),
          ("password", "123456"),
          ("dbtable", "US_OperationLog")))
    val logs = reader.load()
  
    logs.foreachPartition(it => {
      
      while(it.hasNext) {
        val row = it.next()
        val userId = row.getLong(0)
        val optType = row.getString(1)
        val optTime = row.getTimestamp(2)
        
        println(s"用户:$userId, 操作类型：$optType, 操作时间：$optTime ")

        // 根据操作类型匹配
        optType match {
          case "1" =>   // 登录，0.5
            UserOperatorUtil.updateScore(userId, ScoreType.LOGIN, 0.5)

          case "2" =>   // 互动行为（点赞，下载，评论等），每个行为0.1
            UserOperatorUtil.updateScore(userId, ScoreType.BUSINESS, 0.1)

          case "3" =>   // 邀请好友注册，+1
            UserOperatorUtil.updateScore(userId, ScoreType.BUDDY, 1)

          case "4" =>   // 分享链接，+1
            UserOperatorUtil.updateScore(userId, ScoreType.BUSINESS, 1)

          case "5" =>   // 好友数量+1，+1
            UserOperatorUtil.updateScore(userId, ScoreType.BUDDY, 1)

          case "6" =>   // 首次开通会员，50
            UserOperatorUtil.updateScore(userId, ScoreType.VIP_FIRST, 50)

          case "7" =>   // 保持会员资源，每月25（续费一次）
            UserOperatorUtil.updateScore(userId, ScoreType.VIP_NEXT, 25)

          case "8" =>   // 签到，+1，当天重复不计算
            UserOperatorUtil.signIn(userId, optTime)
        }
      }
    })
    
  }
  
  
}
