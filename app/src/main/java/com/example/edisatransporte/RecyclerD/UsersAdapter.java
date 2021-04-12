package com.example.edisatransporte.RecyclerD;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.edisatransporte.R;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersAdapterVh> implements Filterable {
    private List<UserModel> userModels;
    private Context context;
    private SelectedUser selectedUser;
    private List<UserModel> getUserModelsFiltered;

    public UsersAdapter(List<UserModel> userModels,SelectedUser selectedUser) {
        this.userModels = userModels;
        this.getUserModelsFiltered = userModels;
        this.selectedUser = selectedUser;
    }

    @NonNull
    @Override
    public UsersAdapter.UsersAdapterVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new UsersAdapterVh(LayoutInflater.from(context).inflate(R.layout.item_list,null));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UsersAdapterVh holder, int position) {

        UserModel userModel= userModels.get(position);
        String username = userModel.getUsername();
        String desc1 = userModel.getDesc1();
        String desc2 = userModel.getDesc2();
        String prefix = userModel.getUsername().substring(0,1);
        holder.tvUsername.setText(username);
        holder.desc2.setText(desc2);
        holder.desc1.setText(desc1);

        holder.tvPrefix.setText("");
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint == null | constraint.length()==0){
                    filterResults.count = getUserModelsFiltered.size();
                    filterResults.values = getUserModelsFiltered;
                }
                else {
                    String searchChr= constraint.toString().toLowerCase();
                    List<UserModel> resultData = new ArrayList<>();
                    for (UserModel userModel: getUserModelsFiltered){
                        if (userModel.getUsername().toLowerCase().contains(searchChr)){
                            resultData.add(userModel);
                        }
                    }
                    filterResults.count = resultData.size();
                    filterResults.values = resultData;
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                userModels = (List<UserModel>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    public interface SelectedUser{
        void selectedUser(UserModel userModel);
    }

    public class UsersAdapterVh extends RecyclerView.ViewHolder {
        TextView tvPrefix;
        TextView tvUsername;
        TextView desc1;
        TextView desc2;

        ImageView imIcon;
        public UsersAdapterVh(@NonNull View itemView) {
            super(itemView);
            tvPrefix = itemView.findViewById(R.id.calificacion);
            tvUsername = itemView.findViewById(R.id.destino);
            desc1 = itemView.findViewById(R.id.oeprador);
            desc2 = itemView.findViewById(R.id.horasalida);

            imIcon = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedUser.selectedUser(userModels.get(getAdapterPosition()));
                }
            });
        }
    }
}
