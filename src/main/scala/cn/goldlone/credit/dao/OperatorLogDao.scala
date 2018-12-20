package cn.goldlone.credit.dao

import java.text.SimpleDateFormat

import cn.goldlone.credit.utils.DBUtil
import com.alibaba.fastjson.JSONObject

/**
  *
  * @author Created by CN on 2018/12/20/0020 21:03 .
  */
class OperatorLogDao {
  
  def addOne(log: JSONObject): Unit = {
  
    val dbUtil = new DBUtil
    val sql = "insert " +
        "into US_OperationLog(UserId, OptType, OptTime) " +
        "values(?, ?, ?)"
    
    val userId = log.getLongValue("userId")
    val optType = log.getString("optType")
    val optTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(log.getString("optTime"))
    
    dbUtil.executeInsertOrUpdate(sql, Array(userId, optType, optTime))
  }


}
