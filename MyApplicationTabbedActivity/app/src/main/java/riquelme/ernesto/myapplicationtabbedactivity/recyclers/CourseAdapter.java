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
import riquelme.ernesto.myapplicationtabbedactivity.objects.Course;
import riquelme.ernesto.myapplicationtabbedactivity.tools.Conversions;

public class CourseAdapter extends RecyclerView.Adapter<CourseViewHolder> {
    SharedStore sharedStore;
    Conversions conversions;
    Context context;
    List<Course> courses;

    public CourseAdapter(Context applicationContext, List<Course> courses) {
        this.sharedStore = SharedStore.getInstance();
        this.conversions = new Conversions();
        this.context = applicationContext;
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(sharedStore.isCourseWidthAdapted()) {
            return new CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.search_item, parent, false));
        } else {
            return new CourseViewHolder(LayoutInflater.from(context).inflate(R.layout.mail_course_item, parent, false));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        // nameView NO PUEDE SER private
        holder.nameView.setText(courses.get(position).getName());
        holder.initDateView.setText(conversions.convertLocalDateToString(courses.get(position).getStartDate()));
        holder.endDateView.setText(conversions.convertLocalDateToString(courses.get(position).getEndDate()));
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }
}
