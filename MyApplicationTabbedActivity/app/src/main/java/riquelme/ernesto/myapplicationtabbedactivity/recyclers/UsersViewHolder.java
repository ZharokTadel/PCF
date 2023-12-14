package riquelme.ernesto.myapplicationtabbedactivity.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import riquelme.ernesto.myapplicationtabbedactivity.R;

public class UsersViewHolder extends RecyclerView.ViewHolder {
    TextView nameView;
    TextView initDateView;
    TextView endDateView;

    public UsersViewHolder(@NonNull View itemView) {
        super(itemView);
        this.nameView = itemView.findViewById(R.id.titleCourseTextView);
        this.initDateView = itemView.findViewById(R.id.provinceDateTextView);
        this.endDateView = itemView.findViewById(R.id.townshipDateTextView);
    }
}
