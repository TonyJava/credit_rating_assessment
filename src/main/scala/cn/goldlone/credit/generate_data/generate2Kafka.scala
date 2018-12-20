package cn.goldlone.credit.generate_data

import java.text.SimpleDateFormat
import java.util.Date

import cn.goldlone.credit.dao.OperatorLogDao
import cn.goldlone.credit.utils.{DBUtil, PropertiesUtil}
import com.alibaba.fastjson.JSONObject
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

/**
  *
  * @author Created by CN on 2018/12/20/0020 20:28 .
  */
object generate2Kafka {
  
  def main(args: Array[String]): Unit = {
    val props = new PropertiesUtil("config/kafkaProducer.properties").getProperties
    val producer = new KafkaProducer[String, String](props)
    
    val dBUtil = new DBUtil
    var sql = "select max(UserId), min(UserId) from US_User"
    var rs = dBUtil.executeQuery(sql, null)
  
    val operatorLogDao = new OperatorLogDao()
    
    if(rs.next()) {
      val maxUserId = rs.getLong(1)
      val minUserId = rs.getLong(2)
      
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      while(true) {
        for (i <- 0 to 100) {
          // 随机选择一个用户
          val userId = minUserId + (math.random * (maxUserId - minUserId + 1)).toInt
          
          val optType = (math.random * 8 + 1).toInt.toString
          val optTime = sdf.format(new Date())
          
          val json = new JSONObject()
          json.put("userId", userId)
          json.put("optType", optType)
          json.put("optTime", optTime)
          operatorLogDao.addOne(json)
          
          println(json.toString)
          producer.send(new ProducerRecord("credit_rating_operator_log", userId.toString, json.toString))
        }
        try {
          Thread.sleep(500)
        } catch {
          case _: InterruptedException => println("休眠被打断")
        }
      }
      
    }
    
    
    producer.close()
  }
  
}
