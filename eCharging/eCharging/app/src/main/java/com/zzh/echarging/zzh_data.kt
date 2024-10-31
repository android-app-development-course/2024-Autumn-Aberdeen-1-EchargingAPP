package com.zzh.echarging

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

class Charging : ArrayList<Plie>()

@Entity(tableName = "charging_data")
data class Plie(
    @PrimaryKey
    var id: String,
    var isOccupied: Boolean,
    var location: String,
    var powerOutput: String,
    var pricePerKw: Double,
    var progress: Int = 0,
    var chepaihao: String = "",
    var image: String = "data:image/jpeg;base64,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"
)

@Entity(tableName = "user_data")
data class User(
    @PrimaryKey
    var chepaihao: String,
    val password: String,
    var isCharging: Boolean = false,
    var chargingProgress: Int = 0,
)

@Dao
interface ChargingDao {
    @androidx.room.Insert
    fun insert(zhuang: Plie)

    @androidx.room.Query("SELECT * FROM charging_data")
    fun getAllPilesAsFlow(): kotlinx.coroutines.flow.Flow<List<Plie>>

    @Query("SELECT * FROM charging_data")
    fun getAllPlies(): List<Plie>

    @androidx.room.Query("SELECT * FROM charging_data WHERE id = :id")
    fun getPlie(id: String): Plie

    @androidx.room.Update
    fun updatePlie(zhuang: Plie)

    @androidx.room.Delete
    fun deletePlie(zhuang: Plie)

    @androidx.room.Insert
    fun insertUser(user: User)

    @androidx.room.Query("SELECT * FROM user_data WHERE chepaihao = :id LIMIT 1")
    fun getUserByChepaiHao(id: String): User?

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM charging_data WHERE chepaihao = :chepaihao LIMIT 1")
    fun findPlieByChePai(chepaihao: String): Plie?

    @Query("SELECT * FROM user_data")
    fun getUserAsFlow(): kotlinx.coroutines.flow.Flow<List<User>>
}

@Database(entities = [Plie::class, User::class], version = 1, exportSchema = false)
abstract class ChargingDatabase : androidx.room.RoomDatabase() {
    abstract fun chargingDao(): ChargingDao

    companion object {
        private var instance: ChargingDatabase? = null

        fun getInstance(context: Context): ChargingDatabase {
            if (instance == null) {
                instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    ChargingDatabase::class.java,
                    "charging_database"
                ).build()
            }
            return instance!!
        }
    }
}