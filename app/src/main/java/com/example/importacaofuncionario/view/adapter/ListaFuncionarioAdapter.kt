package com.example.importacaofuncionario.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.importacaofuncionario.databinding.FuncionarioItemBinding
import com.example.importacaofuncionario.model.Funcionario
import com.example.importacaofuncionario.view.ListaFuncionarioFragmentDirections

class ListaFuncionarioAdapter(private var funcionarios: List<Funcionario>) :
    RecyclerView.Adapter<ListaFuncionarioAdapter.ListaViewHolder>() {


    class ListaViewHolder(private val binding: FuncionarioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(funcionario: Funcionario, holder: ListaViewHolder) {

            binding.codigoFuncionario.text = funcionario.codFuncionario.toString()
            binding.descricaoFuncionario.text = funcionario.descFuncionario
            binding.complemento.text = funcionario.complemento

            holder.itemView.setOnClickListener {
                val action =
                    ListaFuncionarioFragmentDirections.actionListaFuncionarioFragmentToCrudFragment(
                        funcionario
                    )
                holder.itemView.findNavController().navigate(action)
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val funcionarioItemBinding =
            FuncionarioItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListaViewHolder(funcionarioItemBinding)
    }

    override fun getItemCount(): Int {
        return funcionarios.size
    }

    override fun onBindViewHolder(holder: ListaViewHolder, position: Int) {
        holder.bind(funcionarios[position], holder)
    }


    fun atualizaViewuncionarios(funcionarios: List<Funcionario>) {
        this.funcionarios = funcionarios
        notifyDataSetChanged()
    }

}

