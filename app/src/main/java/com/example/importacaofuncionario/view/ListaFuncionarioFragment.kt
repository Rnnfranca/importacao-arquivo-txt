package com.example.importacaofuncionario.view

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.importacaofuncionario.R
import com.example.importacaofuncionario.databinding.FragmentListaFuncionarioBinding
import com.example.importacaofuncionario.model.Funcionario
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {


        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        configuracaoView()

        mListaFuncionarioViewModel.lerTodosOsDadosDoBanco()

        clickListeners()

        liveDataObservers()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        mListaFuncionarioViewModel.status.observe(viewLifecycleOwner, { leitorStatus ->
            if(leitorStatus == LeitorStatus.DONE) {
                menu.clear()
                inflater.inflate(R.menu.lista_menu, menu)
            } else {
                menu.clear()
            }
        })

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete_todos -> {
                Log.d("onOptionsItemSelected", "Deletar todos clicado!")

                mostraDialog()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun mostraDialog() {
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
                mListaFuncionarioViewModel.criaArquivoLocal(listaFuncionario, binding.root.context)

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

/*    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun configuracaoView() {
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )
        mListaFuncionarioViewModel =
            ViewModelProvider(this).get(ListaFuncionarioViewModel::class.java)
        mListaFuncionarioAdapter = ListaFuncionarioAdapter(listOf()) { funcionario: Funcionario ->
            val action =
                ListaFuncionarioFragmentDirections.actionListaFuncionarioFragmentToCrudFragment(
                    funcionario
                )
            findNavController().navigate(action)
        }
        recyclerView.adapter = mListaFuncionarioAdapter
    }

    private fun pegaArquivoDoDispositivo() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "text/plain"
        startActivityForResult(intent, FILE_PICK_CODE)
    }


}