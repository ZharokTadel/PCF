package riquelme.ernesto.myapplicationtabbedactivity.general_activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import riquelme.ernesto.myapplicationtabbedactivity.R;
import riquelme.ernesto.myapplicationtabbedactivity.communication.SharedStore;
import riquelme.ernesto.myapplicationtabbedactivity.objects.Course;
import riquelme.ernesto.myapplicationtabbedactivity.tools.Conversions;

public class RegisterCourseActivity extends AppCompatActivity {
    SharedStore sharedStore;

    Conversions conversions;

    public RegisterCourseActivity() {
        this.sharedStore = SharedStore.getInstance();
        this.conversions = new Conversions();
    }

    CheckBox hiddenCheckBox;

    TextView titleCourseTextView;
    EditText titleCourseEditText;
    TextView tagsRTextView;
    EditText tagsREditText;

    TextView openDateTextView;
    EditText openTextDate;
    TextView closeDateTextView;
    EditText closeTextDate;

    TextView presentationCourseTextView;
    EditText presentationCourseTextMultiLine;

    Button switchToCoursesListButton;
    Button deleteCourseButton;
    Button registerCourseButton;

    public void switchToCoursesList() {
        startActivity(new Intent(this, CoursesListActivity.class));
    }

    public void deleteCourse() {
        sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(20) +
                "#" + sharedStore.getSelectedCourse().getIdCourse());
        sharedStore.waitUntilResponse(true);
        if(sharedStore.getResponseResults().equals("Ok")){
            switchToCoursesList();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void registerCourse() { // TODO <- aqui me he quedado
        String title = String.valueOf(titleCourseEditText.getText());
        String shortPresentation = String.valueOf(presentationCourseTextMultiLine.getText());
        String longPresentation =  "";
        int idTeacher = sharedStore.getUser().getIdUser();
        String tags = String.valueOf(tagsREditText.getText());;

        String startDate = String.valueOf(openTextDate.getText());
        String endDate = String.valueOf(closeTextDate.getText());

        if (sharedStore.getSelectedCourse() == null) { //  INSERT
            boolean hidden = true;

            sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(16) // C16#...
                    + "#" + title
                    + "#" + shortPresentation
                    + "#" + longPresentation
                    + "#" + startDate
                    + "#" + endDate
                    + "#" + idTeacher
                    + "#" + tags);
            sharedStore.waitUntilResponse(true);
            if(sharedStore.getResponseResults().equals("Ok")){
                startActivity(new Intent(this, CoursesListActivity.class));
            }

        } else { // UPDATE
            boolean hidden = hiddenCheckBox.isChecked();

            sharedStore.setClientMessage(sharedStore.getProtocolMessages().getClientArgument(17) // C17 # ...
                    + "#" + sharedStore.getSelectedCourse().getIdCourse() // 1. Id Curso
                    + "#" + title // 2. Titulo
                    + "#" + shortPresentation // 3. Presentación Corta
                    + "#" + longPresentation // 4. Presentación Larga
                    + "#" + startDate // 5. Fecha Inicio
                    + "#" + endDate // 6. Fecha Final
                    + "#" + hidden // 7. Oculto
                    + "#" + idTeacher // 8. Id Profesor
                    + "#" + tags); // 9. Tags?
            sharedStore.waitUntilResponse(true);
            if(sharedStore.getResponseResults().equals("Ok")){
                startActivity(new Intent(this, CoursesListActivity.class));
            }
        }
    }

    public void configComponents() {
        hiddenCheckBox = (CheckBox) findViewById(R.id.hiddenCheckBox);

        hiddenCheckBox.setActivated(true);
        hiddenCheckBox.setEnabled(false);

        titleCourseTextView = (TextView) findViewById(R.id.titleCourseTextView);
        titleCourseEditText = (EditText) findViewById(R.id.titleCourseEditText);
        tagsRTextView = (TextView) findViewById(R.id.tagsRTextView);
        tagsREditText = (EditText) findViewById(R.id.tagsREditText);

        openDateTextView = (TextView) findViewById(R.id.openDateTextView);
        openTextDate = (EditText) findViewById(R.id.openTextDate);
        closeDateTextView = (TextView) findViewById(R.id.closeDateTextView);
        closeTextDate = (EditText) findViewById(R.id.closeTextDate);

        presentationCourseTextView = (TextView) findViewById(R.id.presentationCourseTextView);
        presentationCourseTextMultiLine = (EditText) findViewById(R.id.presentationCourseTextMultiLine);

        switchToCoursesListButton = (Button) findViewById(R.id.switchToCoursesListButton);
        deleteCourseButton = (Button) findViewById(R.id.deleteCourseButton);
        registerCourseButton = (Button) findViewById(R.id.registerCourseButton);

        switchToCoursesListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToCoursesList();
            }
        });

        if (sharedStore.getSelectedCourse() == null) {
            deleteCourseButton.setVisibility(View.INVISIBLE);
            deleteCourseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteCourse();
                }
            });
        }

        registerCourseButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                registerCourse();
            }
        });
    }

    public void loadData() {
        Course course = sharedStore.getSelectedCourse();

        hiddenCheckBox.setEnabled(true); // Solo se puede publicar una vez haya sido creado con anterioridad
        hiddenCheckBox.setActivated(course.isHidden());

        titleCourseEditText.setText(course.getName());
        openTextDate.setText(String.valueOf(course.getStartDate()));
        closeTextDate.setText(String.valueOf(course.getEndDate()));
        presentationCourseTextMultiLine.setText(course.getShortPresentation());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_course);

        configComponents();
        if (sharedStore.getSelectedCourse() != null) {
            loadData();
        }
    }
}
