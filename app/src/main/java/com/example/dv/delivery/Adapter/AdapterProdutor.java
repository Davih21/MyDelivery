package com.example.dv.delivery.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.dv.delivery.Model.Produto;
import com.example.dv.delivery.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterProdutor extends RecyclerView.Adapter<AdapterProdutor.ProdutoViewHolder> {

    private Context context;
    private List<Produto> produtoList;


    public AdapterProdutor(Context context, List<Produto> produtoList) {
        this.context = context;
        this.produtoList = produtoList;
    }


    @NonNull
    @Override
    public ProdutoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //cria a visualizacao  da lista

        View itemLista;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        itemLista = layoutInflater.inflate(R.layout.produto_item, parent, false);
        return new ProdutoViewHolder(itemLista);

    }

    @Override
    public void onBindViewHolder(@NonNull ProdutoViewHolder holder, int position) { // exibe a lista

        Glide.with(context).load(produtoList.get(position).getFoto()).into(holder.foto); //Baixa e reenderiza imagem

        holder.nome.setText(produtoList.get(position).getNome());
        holder.preco.setText(produtoList.get(position).getPreco());
//        holder.descricao.setText(produtoList.get(position).getDescricao());

    }

    @Override
    public int getItemCount() {
        return produtoList.size();
    }



    public class ProdutoViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView foto;
        private TextView nome;
        private TextView preco;
        private TextView descricao;

        public ProdutoViewHolder(@NonNull View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.fotoProduto);
            nome = itemView.findViewById(R.id.nomeProduto);
            preco = itemView.findViewById(R.id.precoProduto);
            descricao = itemView.findViewById(R.id.dt_descricaoProduto);//Coloca para bsucar a informacao na classe detalhes
        }
    }
}
