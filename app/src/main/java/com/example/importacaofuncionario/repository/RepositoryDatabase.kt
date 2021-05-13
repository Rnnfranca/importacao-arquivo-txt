package com.example.importacaofuncionario.repository

import com.example.importacaofuncionario.database.FuncionarioDAO
import com.example.importacaofuncionario.model.Funcionario
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class RepositoryDatabase(private val funcionarioDAO: FuncionarioDAO) {


    fun adicionaFuncionarioNoBanco(funcionarios: List<Funcionario>): Completable {
        return funcionarioDAO.addFuncionario(funcionarios)
    }

    fun atualizaFuncionarioNoBanco(
        codFuncionario: Long,
        descFuncionario: String,
        complemento: String,
        reservado1: String,
        reservado2: String
    ): Completable {
        return funcionarioDAO.atualizaFuncionarioNoBanco(
            codFuncionario,
            descFuncionario,
            complemento,
            reservado1,
            reservado2
        )
    }

    fun lerTodosOsDadosDoBanco(): Flowable<MutableList<Funcionario>> {
        return funcionarioDAO.lerTodosOsDados()
    }

    fun retornaMaiorCodigo(): Single<Long> {
        return funcionarioDAO.retornaMaiorCodigo()
    }

    fun deletaFuncionario(codFuncionario: Long): Completable {
        return funcionarioDAO.deletaFuncionario(codFuncionario)
    }

    fun deletaTodos(): Completable {
        return funcionarioDAO.deletaTodos()
    }
}