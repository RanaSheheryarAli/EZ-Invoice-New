package com.example.ezinvoice.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ezinvoice.models.BusinessInfo

//@Dao
//interface BusinessInfoDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertBusinessInfo(businessInfo: BusinessInfo)
//
//    @Query("SELECT * FROM business_info WHERE userId = :userId")
//    suspend fun getBusinessInfoByUserId(userId: Int): List<BusinessInfo>
//}
