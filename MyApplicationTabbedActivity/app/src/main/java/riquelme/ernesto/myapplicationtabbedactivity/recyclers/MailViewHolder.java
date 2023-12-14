package riquelme.ernesto.myapplicationtabbedactivity.recyclers;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import riquelme.ernesto.myapplicationtabbedactivity.R;

public class MailViewHolder  extends RecyclerView.ViewHolder {
    TextView nameView;
    TextView timeTextView;
    TextView dateTextView;

    public MailViewHolder(@NonNull View itemView) {
        super(itemView);
        this.nameView = itemView.findViewById(R.id.titleCourseTextView);
        this.timeTextView = itemView.findViewById(R.id.provinceDateTextView);
        this.dateTextView = itemView.findViewById(R.id.townshipDateTextView);
    }
}
