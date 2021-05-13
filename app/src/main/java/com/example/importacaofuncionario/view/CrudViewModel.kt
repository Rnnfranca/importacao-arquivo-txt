package com.example.importacaofuncionario.view

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.importacaofuncionario.database.FuncionarioDatabase
import com.example.importacaofuncionario.model.Funcionario
import com.example.importacaofuncionario.repository.Repository
import com.example.importacaofuncionario.repository.RepositoryDatabase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class CrudViewModel(application: Application) : AndroidViewModel(application) {

    // database repository
    private val repositoryDatabase: RepositoryDatabase
    val repository = Repository()

    private val _maiorCodigo = MutableLiveData<Long>()
    val maiorCodigo: LiveData<Long> get() = _maiorCodigo

    val compositeDisposable = CompositeDisposable()

    init {
        val funcionarioDAO = FuncionarioDatabase.getDatabase(application).funcionarioDAO()
        repositoryDatabase = RepositoryDatabase(funcionarioDAO)
    }


    fun incluirLinhaArquivo(context: Context, funcionario: Funcionario) {
        val func = Funcionario(
            funcionario.codFuncionario + 1,
            funcionario.descFuncionario,
            funcionario.complemento,
            funcionario.reservado1,
            funcionario.reservado2
        )

        compositeDisposable.add(
            repository.incluirLinhaArquivo(context, func)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { Log.d("CrudViewModel", "Completed") },
                    { t -> Throwable(t) }
                )
        )

    }

    fun adicionaFuncionarioNoBanco(funcionario: Funcionario) {
        val func = listOf(
            Funcionario(
                funcionario.codFuncionario + 1,
                funcionario.descFuncionario,
                funcionario.complemento,
                funcionario.reservado1,
                funcionario.reservado2
            )
        )



        compositeDisposable.add(
            repositoryDatabase.adicionaFuncionarioNoBanco(func)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { Log.d("addFuncionarioNoBanco()", "Completed") },
                    { t -> Throwable(t) }
                )
        )
    }

    fun atualizaFuncionarioNoBanco(
        codFuncionario: Long,
        descFuncionario: String,
        complemento: String,
        reservado1: String,
        reservado2: String,
    ) {


        compositeDisposable.add(
            repositoryDatabase.atualizaFuncionarioNoBanco(
                codFuncionario,
                descFuncionario,
                complemento,
                reservado1,
                reservado2
            )
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.d("atualizaFuncionarioDB()", "Completed")
                    },
                    { t -> t.printStackTrace() }
                )
        )
    }

    fun retornaMaiorCodigo() {
        compositeDisposable.add(
            repositoryDatabase.retornaMaiorCodigo()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { maiorCod ->
                        _maiorCodigo.postValue(maiorCod)
                    },
                    { t ->
                        Throwable(t)
                    }
                )
        )
    }

    fun deletaFuncionario(codFuncionario: Long) {
        compositeDisposable.add(
            repositoryDatabase.deletaFuncionario(codFuncionario)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        Log.d("deletaFuncionario()", "Completed")
                    },
                    { t ->
                        t.printStackTrace()
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }


}