package riquelme.ernesto.myapplicationtabbedactivity.recyclers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.objects.User;
import riquelme.ernesto.myapplicationtabbedactivity.tools.Conversions;

public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {
    Conversions conversions;
    Context context;
    List<User> courses;

    public UsersAdapter(Context applicationContext, List<User> courses) {
        this.conversions = new Conversions();
        this.context = applicationContext;
        this.courses = courses;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersViewHolder(LayoutInflater.from(context).inflate(R.layout.search_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        // nameView NO PUEDE SER private
        holder.nameView.setText(courses.get(position).getName());
        holder.initDateView.setText(courses.get(position).getProvince());
        holder.endDateView.setText(courses.get(position).getTownship());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
