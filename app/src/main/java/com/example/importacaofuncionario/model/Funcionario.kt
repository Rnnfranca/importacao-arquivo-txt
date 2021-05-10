package com.example.importacaofuncionario.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "funcionario_table")
data class Funcionario (
    @PrimaryKey
    var codFuncionario: Long = 0,
    var descFuncionario: String = "",
    var complemento: String = "",
    var reservado1: String = "",
    var reservado2: String = ""
): Parcelable