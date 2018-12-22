package cn.goldlone.credit.analysis

import cn.goldlone.credit.utils.DBUtil

import scala.collection.mutable.ListBuffer

/**
  * 信用等级评定
  *
  * 计算规则：
  *   信用评分 = 经验值 * [1 - ( a * 安全策略处理次数权重系数 )]
  *   权重系数(a): 0.6
  *   安全策略处理次数权重系数(a): 1=>0.0  2=>0.6  >=3=>1.0
  *
  * 用法：
  *   java -jar xxx.jar cn.goldlone.credit.analysis.RankRating
  *
  * @author Created by CN on 2018/12/22/0022 10:22 .
  */
object RankRating {
  
  def main(args: Array[String]): Unit = {
  
    val dbUtil = new DBUtil()
    
    // 获取每个用户的经验值 及 对应的安全策略处理次数
    val sql = "select t1.UserId, TotalExperience, " +
        "  if(ProcessNum is null, 0, ProcessNum) " +
        "from US_Experience t1 " +
        "left join US_Process t2 " +
        "  on t1.UserId = t2.UserId"
    
    val list = new ListBuffer[(Long, Double)]()
    
    val rs = dbUtil.executeQuery(sql, null)
    while(rs.next()) {
      val userId = rs.getLong(1)
      val totalExperience = rs.getInt(2)
      val processNum = rs.getInt(3)
      
      var alpha: Double = 0
      processNum match {
        case 1 => alpha = 0
        case 2 => alpha = 0.6
        case x if x>=3 => alpha = 1.0
        case _ => alpha = 1.0
      }
  
      val rateScore: Double = totalExperience * (1 - 0.6 * alpha)
      
      list.append((userId, rateScore))
    }
    
    println(list)
    
    val sql1 = "insert " +
        "into US_Credit(UserId, CreditScore) " +
        "values(?, ?) " +
        "on duplicate key update CreditScore = ?"
    val sql2 = "insert " +
        "into US_Credit_Log(UserId, Time, CreditScore) " +
        "values(?, now(), ?) "
    
    val conn = DBUtil.getConnection
    conn.setAutoCommit(false)
    val psmt1 = conn.prepareStatement(sql1)
    val psmt2 = conn.prepareStatement(sql2)
    
    val batchSize = 100
    var count = 0
    for(item <- list) {
      psmt1.setLong(1, item._1)
      psmt1.setDouble(2, item._2)
      psmt1.setDouble(3, item._2)
      
      psmt2.setLong(1, item._1)
      psmt2.setDouble(2, item._2)
      
      
      psmt1.addBatch()
      psmt2.addBatch()
      count += 1
      if(count >= batchSize) {
        psmt1.executeBatch()
        psmt2.executeBatch()
        conn.commit()
        count = 0
      }
    }
    
    if(count != 0) {
      psmt1.executeBatch()
      psmt2.executeBatch()
      conn.commit()
    }
    
    
    
    DBUtil.returnConnection(conn)
  }
  
  
}
