package cn.goldlone.credit.realtime

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import cn.goldlone.credit.common.ScoreType
import cn.goldlone.credit.utils.UserOperatorUtil
import com.alibaba.fastjson.JSON
import kafka.serializer.StringDecoder
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka.KafkaUtils

/**
  * 实时从Kafka中读取用户的操作记录
  *
  * @author Created by CN on 2018/12/20/0020 21:11 .
  */
object AnalysisUserOperator {
  
  
  def main(args: Array[String]): Unit = {
  
    val conf = new SparkConf().setAppName("realTime-AnalysisUserOperator").setMaster("local[*]")
    val ssc = new StreamingContext(conf, Seconds(1))

    val kafkaMap = Map[String, String](("bootstrap.servers", "hh:9092"))
    val topicSet = Set[String]("credit_rating_operator_log")
  
    val kafkaStream = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaMap, topicSet)

    kafkaStream.foreachRDD(rdd => {
      rdd.foreach(item => {
        println(s"用户id: ${item._1}, 操作记录: ${item._2}")
        
        val info = JSON.parseObject(item._2)
        val userId: Long = info.getLongValue("userId")
        val optType: String = info.getString("optType")
        val optTime: Date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(info.getString("optTime"))
        try {
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
              UserOperatorUtil.signIn(userId, new Timestamp(optTime.getTime))
          }
        } catch {
          case e: Exception => println(s"【异常】${e.getMessage}")
        }
      })
    })

    ssc.start()
    ssc.awaitTermination()
  
  }
  
  
}
