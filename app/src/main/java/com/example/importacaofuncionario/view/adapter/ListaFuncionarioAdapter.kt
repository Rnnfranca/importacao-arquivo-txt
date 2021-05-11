package com.example.importacaofuncionario.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.importacaofuncionario.databinding.FuncionarioItemBinding
import com.example.importacaofuncionario.model.Funcionario

class ListaFuncionarioAdapter(private var funcionarios: List<Funcionario>, val clickListener: (Funcionario) -> Unit) :
    RecyclerView.Adapter<ListaFuncionarioAdapter.ListaViewHolder>() {


    class ListaViewHolder(private val binding: FuncionarioItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(funcionario: Funcionario, clickListener: (Funcionario) -> Unit) {

            binding.codigoFuncionario.text = funcionario.codFuncionario.toString()
            binding.descricaoFuncionario.text = funcionario.descFuncionario
            binding.complemento.text = funcionario.complemento

            binding.root.setOnClickListener { clickListener(funcionario) }

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
        holder.bind(funcionarios[position], clickListener)
    }


    fun atualizaViewuncionarios(funcionarios: List<Funcionario>) {
        this.funcionarios = funcionarios
        notifyDataSetChanged()
    }

}

