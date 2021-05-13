package com.example.importacaofuncionario.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.importacaofuncionario.model.Funcionario
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface FuncionarioDAO {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun addFuncionario(funcionarios: List<Funcionario>): Completable

    @Query("SELECT * FROM funcionario_table ORDER BY codFuncionario ASC")
    fun lerTodosOsDados(): Flowable<MutableList<Funcionario>>

    @Query("SELECT MAX(codFuncionario) FROM funcionario_table")
    fun retornaMaiorCodigo(): Single<Long>

    @Query("UPDATE funcionario_table SET descFuncionario = :descFuncionario, complemento = :complemento, reservado1 = :reservado1, reservado2 = :reservado2 WHERE codFuncionario = :codFuncionario")
    fun atualizaFuncionarioNoBanco(codFuncionario: Long, descFuncionario: String, complemento: String, reservado1: String, reservado2: String) : Completable

    @Query("DELETE FROM funcionario_table WHERE codFuncionario = :codFuncionario")
    fun deletaFuncionario(codFuncionario: Long): Completable

    @Query("DELETE FROM funcionario_table")
    fun deletaTodos(): Completable
}