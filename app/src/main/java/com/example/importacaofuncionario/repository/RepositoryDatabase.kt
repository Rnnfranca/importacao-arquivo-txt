package com.example.importacaofuncionario.repository

import com.example.importacaofuncionario.database.FuncionarioDAO
import com.example.importacaofuncionario.model.Funcionario
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class RepositoryDatabase(private val funcionarioDAO: FuncionarioDAO) {


    fun adicionaTodosFuncionariosNoBanco(funcionarios: List<Funcionario>): Completable {
        return Completable.create { emitter ->

            funcionarios.forEach { funcionario ->
                funcionarioDAO.addFuncionario(funcionario)
            }
            emitter.onComplete()
        }
    }

    fun adicionaFuncionarioNoBanco(funcionario: Funcionario): Completable {
        return Completable.create { emitter ->
            try {
                funcionarioDAO.addFuncionario(funcionario)
                emitter.onComplete()
            } catch (t: Throwable) {
                emitter.onError(t)
            }
        }
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

    fun lerTodosOsDadosDoBanco(): Observable<MutableList<Funcionario>> {
        return Observable.create<MutableList<Funcionario>> { emitter ->
            try {
                emitter.onNext(funcionarioDAO.lerTodosOsDados())
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }
    }

    fun retornaMaiorCodigo() =
        Single.create<Long> { emitter ->
            try {
                emitter.onSuccess(funcionarioDAO.retornaMaiorCodigo())
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }

    fun deletaFuncionario(codFuncionario: Long) =
        Completable.create { emitter ->
            try {
                funcionarioDAO.deletaFuncionario(codFuncionario)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(Throwable(e))
            }
        }
}