package com.example.ezinvoice.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.ezinvoice.models.AppUser

//@Dao
//interface AppUserDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertUser(user: AppUser): Long // Returns user ID
//
//    @Query("SELECT * FROM user WHERE userId = :id LIMIT 1")
//    suspend fun getUserById(id: Int): AppUser?
//}
