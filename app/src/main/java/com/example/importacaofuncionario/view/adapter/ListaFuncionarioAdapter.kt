package com.example.importacaofuncionario.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.importacaofuncionario.R
import com.example.importacaofuncionario.model.Funcionario
import com.example.importacaofuncionario.view.ListaFuncionarioFragmentDirections

class ListaFuncionarioAdapter(private var funcionarios: List<Funcionario>) :
    RecyclerView.Adapter<ListaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.funcionario_item, parent, false)

        return ListaViewHolder(view)
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

class ListaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bind(funcionario: Funcionario, holder: ListaViewHolder) {

        itemView.findViewById<TextView>(R.id.codigo_funcionario).text =
            funcionario.codFuncionario.toString()
        itemView.findViewById<TextView>(R.id.descricao_funcionario).text =
            funcionario.descFuncionario
        itemView.findViewById<TextView>(R.id.complemento).text = funcionario.complemento

        holder.itemView.setOnClickListener {
            val action =
                ListaFuncionarioFragmentDirections.actionListaFuncionarioFragmentToCrudFragment(
                    funcionario
                )
            holder.itemView.findNavController().navigate(action)
        }

    }

}