package com.example.importacaofuncionario.view

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.importacaofuncionario.R
import com.example.importacaofuncionario.databinding.FragmentCrudBinding
import com.example.importacaofuncionario.model.Funcionario

class CrudFragment : Fragment() {

    private val args: CrudFragmentArgs by navArgs()
    private var funcionario: Funcionario? = null
    private var maiorCodigo: Long = 0

    lateinit var contentResolver: ContentResolver

    private var _binding: FragmentCrudBinding? = null
    private val binding get() = _binding!!

    private lateinit var mCrudFragmentViewModel: CrudViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        funcionario = args.funcionario
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCrudBinding.inflate(inflater)
        setHasOptionsMenu(true)

        mCrudFragmentViewModel = ViewModelProvider(this).get(CrudViewModel::class.java)

        contentResolver = binding.root.context.contentResolver

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.crudCodFuncionario.visibility = View.GONE

        funcionario?.also { funcionario ->
            binding.tituloView.text = getString(R.string.tituloCrudView2)
            binding.crudCodFuncionario.visibility = View.VISIBLE
            binding.crudCodFuncionarioEditText.isEnabled = false
            binding.crudCodFuncionarioEditText.setText(funcionario.codFuncionario.toString())
            binding.crudDescFuncionarioEditText.setText(funcionario.descFuncionario)
            binding.crudComplementoEditText.setText(funcionario.complemento)
            binding.crudReservado1EditText.setText(funcionario.reservado1)
            binding.crudReservado2EditText.setText(funcionario.reservado2)
        }

        observers()
        clickListeners(binding.root.context)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        funcionario?.also {
            inflater.inflate(R.menu.crud_menu, menu)
            super.onCreateOptionsMenu(menu, inflater)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                Log.d("Menu", "Deletar selecionado")

                mostraDialog()

                return true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun mostraDialog() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(context)
        alert.setTitle("Aviso!")
        alert.setMessage("Tem certa que deseja excluir o funcionário?")
        alert.setPositiveButton("Sim", DialogInterface.OnClickListener { dialog, _ ->

            funcionario?.also {

                mCrudFragmentViewModel.deletaFuncionario(
                    it.codFuncionario,
                )

                mostraToast(
                    binding.root.context,
                    it.codFuncionario,
                    it.descFuncionario,
                    "Excluído"
                )
            }

            dialog.dismiss()
            binding.root.findNavController().navigateUp()
        })
        alert.setNegativeButton("Não", DialogInterface.OnClickListener { dialog, _ ->
            dialog.dismiss()
        })
        alert.create().show()
    }

    private fun observers() {
        mCrudFragmentViewModel.retornaMaiorCodigo()
        mCrudFragmentViewModel.maiorCodigo.observe(viewLifecycleOwner, { maior ->
            this.maiorCodigo = maior
        })
    }

    private fun clickListeners(context: Context) {


        binding.botaoSalvar.setOnClickListener {

            if (this.funcionario == null) {


                val funcionario = Funcionario(
                    this.maiorCodigo,
                    binding.crudDescFuncionarioEditText.text.toString(),
                    binding.crudComplementoEditText.text.toString(),
                    binding.crudReservado1EditText.text.toString(),
                    binding.crudReservado2EditText.text.toString()
                )

                if (funcionario.descFuncionario.isEmpty() || funcionario.complemento.isEmpty() ||
                    funcionario.reservado1.isEmpty() || funcionario.reservado2.isEmpty()
                ) {
                    binding.validacaoAviso.visibility = View.VISIBLE
                } else {
                    // atualiza no arquivo
                    mCrudFragmentViewModel.adicionaFuncionarioNoBanco(funcionario)
                    mCrudFragmentViewModel.incluirLinhaArquivo(context, funcionario)

                    // Toast de sucesso
                    mostraToast(
                        context,
                        this.maiorCodigo + 1,
                        funcionario.descFuncionario,
                        "incluído!"
                    )

                    // esconde o teclado
                    val inputMethodManager =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
                    // volta pra tela inicial
                    binding.root.findNavController().navigateUp()
                }


            } else {

                this.funcionario = Funcionario(
                    binding.crudCodFuncionarioEditText.text.toString().toLong(),
                    binding.crudDescFuncionarioEditText.text.toString(),
                    binding.crudComplementoEditText.text.toString(),
                    binding.crudReservado1EditText.text.toString(),
                    binding.crudReservado2EditText.text.toString()
                )

                this.funcionario?.also { funcionario ->
                    mCrudFragmentViewModel.atualizaFuncionarioNoBanco(
                        funcionario.codFuncionario,
                        funcionario.descFuncionario,
                        funcionario.complemento,
                        funcionario.reservado1,
                        funcionario.reservado2
                    )

                    // Toast de sucesso
                    mostraToast(
                        context,
                        funcionario.codFuncionario,
                        funcionario.descFuncionario,
                        "atualizado!"
                    )
                }


                // volta pra tela inicial
                binding.root.findNavController().navigateUp()

            }

        }

    }

    private fun mostraToast(
        context: Context,
        codFuncionario: Long,
        descFuncionario: String,
        mensagem: String
    ) {
        Toast.makeText(
            context,
            "Funcionário: $codFuncionario - $descFuncionario $mensagem!",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}