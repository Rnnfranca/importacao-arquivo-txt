package com.example.importacaofuncionario.view

import android.app.Application
import android.content.Context
import android.net.Uri
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

enum class LeitorStatus { EMPTY, ERROR, DONE }


class ListaFuncionarioViewModel(application: Application) : AndroidViewModel(application) {

    private val _status = MutableLiveData<LeitorStatus>()
    val status: LiveData<LeitorStatus> get() = _status

    private val _listaFuncionario = MutableLiveData<MutableList<Funcionario>>()
    val listaFuncionario: LiveData<MutableList<Funcionario>> get() = _listaFuncionario
    val repository = Repository()

    // database repository
    private val repositoryDatabase: RepositoryDatabase

    private val _statusInsert = MutableLiveData<String>()
    val statusInsert: LiveData<String> get() = _statusInsert

    val compositeDisposable = CompositeDisposable()

    init {
        val funcionarioDAO = FuncionarioDatabase.getDatabase(application).funcionarioDAO()
        repositoryDatabase = RepositoryDatabase(funcionarioDAO)
    }


    fun adicionaFuncionario(listaFuncionario: List<Funcionario>) {
        compositeDisposable.add(
            repositoryDatabase.adicionaTodosFuncionariosNoBanco(listaFuncionario)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { _statusInsert.postValue("Completed") },
                    { t -> t.printStackTrace() }
                )
        )
    }


    fun lerTodosOsDadosDoBanco() {
        compositeDisposable.add(
            repositoryDatabase.lerTodosOsDadosDoBanco()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { listaFuncionario ->
                        _listaFuncionario.postValue(listaFuncionario) // onNext
                        if (listaFuncionario.isEmpty()) {
                            _status.postValue(LeitorStatus.EMPTY)
                        } else {
                            _status.postValue(LeitorStatus.DONE)
                        }
                    },
                    { t ->
                        t.printStackTrace()
                    }
                )
        )
    }


    fun lerConteudoArquivo(context: Context, uri: Uri) {
        compositeDisposable.add(
            repository.lerConteudoArquivo(context, uri)
                .doOnError {
                    _status.postValue(LeitorStatus.ERROR)
                }
                .doOnComplete {
                    _status.postValue(LeitorStatus.DONE)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { funcionario ->
                        _listaFuncionario.postValue(funcionario)
                        criaArquivoLocal(funcionario, context)
                        // adiciona a lista de funcionarios no banco de dados
                        adicionaFuncionario(funcionario)
                    },
                    { t ->
                        t.printStackTrace()
                    }
                )
        )
    }


    fun criaArquivoLocal(listaFuncionario: MutableList<Funcionario>, context: Context) {
        compositeDisposable.add(
            repository.criaArquivoLocal(listaFuncionario, context)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    { Log.d("criaArquivoLocal", "Completed") },
                    { e ->
                        e.printStackTrace()
                    }
                )
        )
    }

    fun deletaTodos() {
        compositeDisposable.add(
            repositoryDatabase.deletaTodos()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(
                    {
                        _status.postValue(LeitorStatus.EMPTY)
                        _listaFuncionario.postValue(mutableListOf())
                    },
                    { e ->
                        e.printStackTrace()
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()

        compositeDisposable.clear()
    }

}