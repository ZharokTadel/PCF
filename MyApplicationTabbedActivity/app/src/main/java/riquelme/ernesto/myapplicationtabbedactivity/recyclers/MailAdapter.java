package riquelme.ernesto.myapplicationtabbedactivity.recyclers;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Message;
import riquelme.ernesto.myapplicationtabbedactivity.tools.Conversions;

public class MailAdapter extends RecyclerView.Adapter<MailViewHolder> {
    SharedStore sharedStore;
    Conversions conversions;
    Context context;
    List<Message> messages;

    public MailAdapter(Context applicationContext, List<Message> messages) {
        this.sharedStore = SharedStore.getInstance();
        this.conversions = new Conversions();
        this.context = applicationContext;
        this.messages = messages;
    }

    @NonNull
    @Override
    public MailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MailViewHolder(LayoutInflater.from(context).inflate(R.layout.mail_course_item,parent,false));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MailViewHolder holder, int position) {
        // nameView NO PUEDE SER private
        holder.nameView.setText(messages.get(position).getSenderReceiverName());
        holder.timeTextView.setText(conversions.convertLocalTimeToString(messages.get(position).getSentTime()));
        holder.dateTextView.setText(conversions.convertLocalDateToString(messages.get(position).getSentDate()));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
}
