package com.example.ezinvoice.database.database


//import android.content.Context
//import androidx.room.Database
//import androidx.room.Room
//import androidx.room.RoomDatabase
//import com.example.ezinvoice.database.dao.AppUserDao
//import com.example.ezinvoice.database.dao.BusinessInfoDao
//import com.example.ezinvoice.models.AppUser
//import com.example.ezinvoice.models.BusinessInfo
//
//
//@Database(entities = [AppUser::class, BusinessInfo::class], version = 1, exportSchema = false)
//abstract class EZInvoiceDatabase : RoomDatabase(){
//    abstract fun appUserDao(): AppUserDao
//    abstract fun businessInfoDao(): BusinessInfoDao
//
//    companion object {
//        @Volatile
//        private var INSTANCE: EZInvoiceDatabase? = null
//
//        fun getDatabase(context: Context): EZInvoiceDatabase {
//            return INSTANCE ?: synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    EZInvoiceDatabase::class.java,
//                    "ezinvoice_database"
//                ).build()
//                INSTANCE = instance
//                instance
//            }
//        }
//    }
//}