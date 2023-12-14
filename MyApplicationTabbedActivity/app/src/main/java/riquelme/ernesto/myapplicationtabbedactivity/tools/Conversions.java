
package riquelme.ernesto.myapplicationtabbedactivity.tools;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author william
 */
public class Conversions {

    public Date convertStringToDate(String str) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(str);

            return date;
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate convertStringToLocalDateSQLVersion(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // 2023-12-12
        LocalDate localDate = LocalDate.parse(str, formatter);
        return localDate;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalDate convertStringToLocalDate(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = LocalDate.parse(str, formatter);
        return localDate;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String convertLocalDateToString(LocalDate localDate) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String date = localDate.format(dateFormatter);
        return date;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public LocalTime convertStringToLocalTime(String str) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTime localTime = LocalTime.parse(str, formatter);
        return localTime;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String convertLocalTimeToString(LocalTime localTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = localTime.format(timeFormatter);
        return time;
    }

    public byte[] concatByteArrays(byte[] a, byte[] b) throws IOException { // Para los mensajes que se envían
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(a);
        outputStream.write(b);

        byte c[] = outputStream.toByteArray();
        return c;
    }

    public int bytesToInt(byte[] bytes) { // Para los mensajes recibidos
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        int test = buffer.getInt();
        return test;
    }

    public byte[] intToBytes(int num) { // Para los mensajes que se envían
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(num);
        byte[] test = buffer.array();
        return test;
    }
}
