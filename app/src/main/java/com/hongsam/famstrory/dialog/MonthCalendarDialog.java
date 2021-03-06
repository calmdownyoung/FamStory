package com.hongsam.famstrory.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hongsam.famstrory.activitie.MainActivity;
import com.hongsam.famstrory.adapter.MonthAdapter;
import com.hongsam.famstrory.data.MonthCalendar;
import com.hongsam.famstrory.data.CalendarData;
import com.hongsam.famstrory.databinding.MonthCalendarLayoutBinding;
import com.hongsam.famstrory.define.Define;

import java.util.ArrayList;

public class MonthCalendarDialog extends Dialog {
    private View root;
    private MonthCalendarLayoutBinding mb;
    private int Year,Month;
    private Dialog dialog;
    public MonthCalendarDialog(@NonNull Context context,int Year,int Month) {
        super(context);
        this.Year = Year;
        this.Month = Month;


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mb = MonthCalendarLayoutBinding.inflate(getLayoutInflater());
        root = mb.getRoot();
        setContentView(root);
        MonthAdapter adapter = new MonthAdapter(getContext());
        mb.title.setText(Year+"년 "+Month+"월 일정");
        getCalendarDB(adapter,Year,Month);
        mb.monthCalendarList.setAdapter(adapter);
    }
    public void getCalendarDB(final MonthAdapter adapter, int Year, final int Month){
        FirebaseDatabase fireDB = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = fireDB.getReference(Define.DB_REFERENCE).child(MainActivity.famName).child(Define.CALENDAR_DB).child(Year+"년").child(Month+"월");
        final ArrayList<String> monthList = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot val:snapshot.getChildren()){
                    String day = val.getKey();
                    CalendarData calendarData = val.getValue(CalendarData.class);

                    adapter.list.add(new MonthCalendar(day,"일정명 : "+ calendarData.getTitle(),"세부내용 : "+ calendarData.getDescription()));
                    adapter.notifyDataSetChanged();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
