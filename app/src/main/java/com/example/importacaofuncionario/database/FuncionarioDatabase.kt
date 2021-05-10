package com.example.importacaofuncionario.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.importacaofuncionario.model.Funcionario

@Database(entities = [Funcionario::class], version = 1, exportSchema = false)
abstract class FuncionarioDatabase : RoomDatabase() {

    abstract fun funcionarioDAO(): FuncionarioDAO

    companion object{
        @Volatile // this means that writes to this field are immediately made visible to other threads
        private var INSTANCE: FuncionarioDatabase? = null

        // function to get or create the database
        fun getDatabase(context: Context): FuncionarioDatabase {
            val tempInstance = INSTANCE

            // check if database already exists
            if (tempInstance != null) {
                return tempInstance
            }


            // if not exist an instance for our database, so is created a new one

            // synchronized means that everything within that block will be protected from a
            // current execution by multiple threads and within this block we are creating
            // and instance of our room database
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FuncionarioDatabase::class.java,
                    "funcionario_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}