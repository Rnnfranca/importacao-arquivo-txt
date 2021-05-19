package com.example.importacaofuncionario.repository

import android.content.Context
import android.net.Uri
import com.example.importacaofuncionario.model.Funcionario
import io.reactivex.Completable
import io.reactivex.Observable
import java.io.*


class Repository {


    fun criaArquivoLocal(listaFuncionarios: MutableList<Funcionario>, context: Context) =
        Completable.create { emitter ->

            val fileOutPutStream: FileOutputStream

            try {

                fileOutPutStream = context.openFileOutput(
                    "C_FUNC.txt",
                    Context.MODE_PRIVATE
                )

                listaFuncionarios.forEach { funcionario ->
                    fileOutPutStream.write(
                        ("${funcionario.codFuncionario};" +
                                "${funcionario.descFuncionario};" +
                                "${funcionario.complemento};" +
                                "${funcionario.reservado1};" +
                                "${funcionario.reservado2}\n").toByteArray()
                    )
                }

                fileOutPutStream.close()

                emitter.onComplete()

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                emitter.onError(Throwable(e))
            } catch (e: IOException) {
                e.printStackTrace()
                emitter.onError(Throwable(e))
            }

        }


    fun lerConteudoArquivo(context: Context, uri: Uri) =
        Observable.create<MutableList<Funcionario>> { emitter ->

            try {
                val listaFuncionarios: MutableList<Funcionario> = mutableListOf()

                context.contentResolver?.openInputStream(uri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream, "ISO-8859-1")).use { leitor ->

                        var linha: String? = leitor.readLine()

                        while (linha != null) {
                            val dados = linha.split(";")
                            listaFuncionarios.add(
                                Funcionario(
                                    dados[0].toLong(),
                                    dados[1],
                                    dados[2],
                                    dados[3],
                                    dados[4]
                                )
                            )

                            linha = leitor.readLine()
                        }
                    }
                }

                emitter.onNext(listaFuncionarios)
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }


    fun incluirLinhaArquivo(context: Context, funcionario: Funcionario) =
        Completable.create { emitter ->

            try {
                FileOutputStream(File(context.filesDir.path, "C_FUNC.txt"), true)
                    .bufferedWriter().use { writer ->
                        writer.write(
                            "${funcionario.codFuncionario};" +
                                    "${funcionario.descFuncionario};" +
                                    "${funcionario.complemento};" +
                                    "${funcionario.reservado1};" +
                                    "${funcionario.reservado2}\n"
                        )
                    }
                emitter.onComplete()
            } catch (e: Exception) {
                emitter.onError(Throwable())
            }
        }
}