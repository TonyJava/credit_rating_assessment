package cn.goldlone.credit.analysis

import java.text.{ParseException, SimpleDateFormat}
import java.util.{Calendar, Date}

import cn.goldlone.credit.utils.DBUtil

/**
  * 年积分统计
  *
  * 使用命令如下：
  * java -jar xxx.jar cn.goldlone.credit.analysis.ScoreAnnual -d 2018
  *
  * @author Created by CN on 2018/12/22/0022 9:36 .
  */
object ScoreAnnual {
  def main(args: Array[String]): Unit = {
    // 传入参数： 日期（yyyy）
    var year: Date = null
    val calendar = Calendar.getInstance()
    val sdf = new SimpleDateFormat("yyyy")
    
    for(i <- args.indices) {
      if(args(i).equals("-d")) {
        if(i+1 < args.length) {
          try{
            year = sdf.parse(args(i+1))
            calendar.setTime(year)
          } catch {
            case _: ParseException =>
              println("日期格式错误，例如(yyyy-MM): -d 2018")
          }
        }
      }
    }
    // 无时间参数传入则默认为上一年
    if(year == null) {
//      calendar.add(Calendar.YEAR, -1)
      
      println(s"无时间参数 -d 传入，默认(上一年)：${sdf.format(calendar.getTime)}")
    }
    
    val dbUtil = new DBUtil
    val sql = "replace " +
        "into US_YearlyScoreLog(UserId, year, " +
        "  OnlineScore, BuddyScore, " +
        "  BusinessScore, VipScore) " +
        "select UserId, year, " +
        "  sum(OnlineScore), sum(BuddyScore), " +
        "  sum(BusinessScore), sum(VipScore) " +
        "from US_MonthlyScoreLog " +
        "where year = ? " +
        "group by UserId"
    
    dbUtil.executeInsertOrUpdate(sql,
      Array(calendar.get(Calendar.YEAR)))
    
  }

}
