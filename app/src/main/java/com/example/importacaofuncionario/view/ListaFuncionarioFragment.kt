package com.example.importacaofuncionario.view

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.importacaofuncionario.R
import com.example.importacaofuncionario.databinding.FragmentListaFuncionarioBinding
import com.example.importacaofuncionario.view.adapter.ListaFuncionarioAdapter


class ListaFuncionarioFragment : Fragment() {

    private val FILE_PICK_CODE = 1000

    private var _binding: FragmentListaFuncionarioBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var mListaFuncionarioViewModel: ListaFuncionarioViewModel
    private lateinit var mListaFuncionarioAdapter: ListaFuncionarioAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListaFuncionarioBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configuracaoView()

        mListaFuncionarioViewModel.lerTodosOsDadosDoBanco()

        clickListeners()

        liveDataObservers()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.lista_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete_todos -> {
                Log.d("onOptionsItemSelected", "Deletar todos clicado!")

                val alert: AlertDialog.Builder = AlertDialog.Builder(context)
                alert.setTitle("Aviso!")
                alert.setMessage("Tem certe que deseja excluir TODOS os funcionários?")
                alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, _ ->
                    mListaFuncionarioViewModel.deletaTodos()
                    dialog.dismiss()
                })
                alert.setNegativeButton("Não", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
                alert.create().show()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun clickListeners() {
        binding.botaoImportar.setOnClickListener {
            pegaArquivoDoDispositivo()
        }

        binding.fabAddFuncionario.setOnClickListener {
            val action =
                ListaFuncionarioFragmentDirections.actionListaFuncionarioFragmentToCrudFragment(null)
            binding.fabAddFuncionario.findNavController().navigate(action)
        }
    }

    private fun liveDataObservers() {
        mListaFuncionarioViewModel.listaFuncionario.observe(
            viewLifecycleOwner,
            { listaFuncionario ->
                // faz o bind na view
                mListaFuncionarioAdapter.atualizaViewuncionarios(listaFuncionario)

            })


        mListaFuncionarioViewModel.status.observe(viewLifecycleOwner, { status ->

            status?.also { leitorStatus ->
                when (leitorStatus) {
                    LeitorStatus.EMPTY -> {
                        binding.botaoImportar.visibility = View.VISIBLE
                        binding.textNenhumArquivo.visibility = View.VISIBLE
                        binding.fabAddFuncionario.visibility = View.INVISIBLE
                    }
                    LeitorStatus.DONE -> {
                        binding.botaoImportar.visibility = View.INVISIBLE
                        binding.textNenhumArquivo.visibility = View.INVISIBLE
                        binding.fabAddFuncionario.visibility = View.VISIBLE
                    }
                    LeitorStatus.ERROR -> {
                        Log.d("ListaFuncionarioFragme", "Erro ao ler conteudo do arquivo")
                    }
                }
            }
        })

        mListaFuncionarioViewModel.statusInsert.observe(viewLifecycleOwner, { status ->
            Log.d("statusInsert", "status insert: $status")
        })
    }

    // após usuario selecionar o arquivo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000 && resultCode == Activity.RESULT_OK) {

            data.also { intent ->
                intent?.data?.also { uri ->
                    context?.let { context ->
                        mListaFuncionarioViewModel.lerConteudoArquivo(context, uri)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }


    fun configuracaoView() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        mListaFuncionarioViewModel =
            ViewModelProvider(this).get(ListaFuncionarioViewModel::class.java)
        mListaFuncionarioAdapter = ListaFuncionarioAdapter(listOf())
        recyclerView.adapter = mListaFuncionarioAdapter
    }

    fun pegaArquivoDoDispositivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain"
        intent.addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        startActivityForResult(intent, FILE_PICK_CODE)
    }


}