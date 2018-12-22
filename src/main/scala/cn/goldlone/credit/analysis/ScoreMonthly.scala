package cn.goldlone.credit.analysis

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date}

import cn.goldlone.credit.utils.{DBUtil, TimeUtils}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.matching.Regex

/**
  * 月积分统计
  *
  * 使用命令如下：
  * java -jar xxx.jar cn.goldlone.credit.analysis.ScoreMonthly -d 2018-12
  *
  * @author Created by CN on 2018/12/21/0021 16:39 .
  */
object ScoreMonthly {
  
  def main(args: Array[String]): Unit = {
    // 传入参数： 日期（yyyy-MM）
    var date: Date = null
    val calendar = Calendar.getInstance()
    val sdf = new SimpleDateFormat("yyyy-MM")
    
    for(i <- args.indices) {
      if(args(i).equals("-d")) {
        if(i+1 < args.length) {
          try{
            date = sdf.parse(args(i+1))
            calendar.setTime(date)
          } catch {
            case _: ParseException =>
              println("日期格式错误，例如(yyyy-MM): -d 2018-12")
          }
        }
      }
    }
    // 无时间参数传入则默认为上个月
    if(date == null) {
      calendar.add(Calendar.MONTH, -1)
  
      println(s"无时间参数 -d 传入，默认(上个月)：${sdf.format(calendar.getTime)}")
    }
    
    val dbUtil = new DBUtil
    val sql = "replace " +
        "into US_MonthlyScoreLog(UserId, year, " +
        "  month, OnlineScore, BuddyScore, " +
        "  BusinessScore, VipScore) " +
        "select UserId, year(Day), month(Day), " +
        "  sum(OnlineScore), sum(BuddyScore), " +
        "  sum(BusinessScore), sum(VipScore) " +
        "from US_DailyScoreLog " +
        "where year(Day) = ? and " +
        "      month(Day) = ? " +
        "group by UserId"
  
    dbUtil.executeInsertOrUpdate(sql,
      Array(calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH)+1))
    
  }

  
}
