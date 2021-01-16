package com.hongsam.famstory.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.hongsam.famstory.activitie.MainActivity;
import com.hongsam.famstory.databinding.CalendarDialogBinding;
import com.hongsam.famstory.define.Define;

import com.hongsam.famstory.firebase.CalendarDB;
import com.hongsam.famstory.firebase.CreateDB;
import com.hongsam.famstory.firebase.UpdateDB;
import com.hongsam.famstory.interf.CustomDialogInterface;

import java.util.Calendar;


/**
 * CalendarFragment 에서 일정 추가 또는 수정시 나오는 대화창
 * devaspirant0510
 *
 */
public class CalendarCustomDialog extends Dialog implements CustomDialogInterface {
    private Dialog dialog;
    private Button dialogOkBtn, dialogCancelBtn, startTimeBtn, endTimeBtn;
    private TextView startTimeTv, endTimeTv;
    private Spinner spinner;
    private EditText viewMoreTitleEt, viewMoreDescriptionEt;
    private TextView description_set_text;
    String date;
    private int pickerState;
    View mBindingRoot;



    int type;

    // 일정추가시 파이어베이스 db 연동
    CreateDB createDB = new CreateDB();
    int pickerHour, pickerMinute;
    // 현재 시간을 가져오기위해 객체 생성
    Calendar cal = Calendar.getInstance();
    int hour = cal.get(Calendar.HOUR_OF_DAY);
    int min = cal.get(Calendar.MINUTE);
    private CalendarDialogBinding mBinding;
    UpdateDB updateDB;
    String state = "오전";
    int getYear,getMonth, day;
    public CalendarCustomDialog(int getYear, int getMonth, int getDay, @NonNull MainActivity context, String date, int type) {
        super(context);
        this.date = date;
        this.type = type;
        this.getYear = getYear;
        this.getMonth = getMonth;
        this.day = getDay;

        dialog = new Dialog(context);
        if (type == Define.UPDATE_DIALOG) {
            updateDB = new UpdateDB(context);
            updateDB.updateDB(date);
        }
        context.setCdi(this);


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = CalendarDialogBinding.inflate(getLayoutInflater());
        mBindingRoot = mBinding.getRoot();
        setContentView(mBindingRoot);

        //setContentView(R.layout.calendar_dialog);

        // xml id 연결
        init();

        viewMoreDescriptionEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = viewMoreDescriptionEt.getText().toString().length();

                description_set_text.setText(len + "/100");
                if (len < 70) {

                    description_set_text.setTextColor(Color.parseColor("#000000"));
                } else if (len < 90) {

                    description_set_text.setTextColor(Color.parseColor("#FB8C00"));
                } else {

                    description_set_text.setTextColor(Color.parseColor("#CD0202"));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        dialogOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText 에서 일정이름,일정내용을 가져옴
                String str_title = viewMoreTitleEt.getText().toString();
                String str_text = viewMoreDescriptionEt.getText().toString();
                String str_start_time = startTimeTv.getText().toString();
                String str_end_time = endTimeTv.getText().toString();

                // 확인 버튼을 눌렀을때 채우지 않은 부분이 있는지 확인
                if (str_text.length() == 0 || str_text.length() == 0 ||
                        startTimeTv.getText().toString().equals("시작시간") ||
                        endTimeTv.getText().toString().equals("종료시간")) {
                    Toast.makeText(getContext(), "빈칸을 채워주세요", Toast.LENGTH_SHORT).show();
                } else {
                    // 번들객체에 데이터를 담아서 Firebase 로 데이터 처리
                    Bundle bundle = new Bundle();
                    bundle.putString("date", date);
                    bundle.putString("title", str_title);
                    bundle.putString("text", str_text);
                    bundle.putString("startT", str_start_time);
                    bundle.putString("endT", str_end_time);
                    bundle.putInt("year",getYear);
                    bundle.putInt("month",getMonth);
                    bundle.putInt("day", day);
                    createDB.pushFireBaseDatabase(bundle);
                    dismiss();
                }

            }
        });
        // 취소버튼을 눌렀을때 다이얼로그 종료
        dialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "취소하였습니다.", Toast.LENGTH_SHORT).show();
                dismiss();
            }
        });
        // 시작시간을 설정하면 timePiker 와 시간 적용 버튼의 Visibility 의 상태를 gone 에서 Visible 로 바꿔줌
        startTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerState = Define.TIME_PICKER_START;


                CalendarTimePicker calendarTimePicker = new CalendarTimePicker(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        pickerHour = hour;
                        pickerMinute = minute;

                        if (pickerHour >= 12) {
                            state = "오후";
                            pickerHour -= 12;
                        }
                        startTimeTv.setText(state + " " + pickerHour + ":" + pickerMinute);
                    }

                }, hour, min, false);

                calendarTimePicker.setMessage("시간을 설정해주세요");
                calendarTimePicker.show();

            }
        });


        endTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickerState = Define.TIME_PICKER_END;
                CalendarTimePicker calendarTimePicker = new CalendarTimePicker(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {

                        pickerHour = hour;
                        pickerMinute = minute;

                        if (pickerHour >= 12) {
                            state = "오후";
                            pickerHour -= 12;
                        }
                        endTimeTv.setText(state + " " + pickerHour + ":" + pickerMinute);

                    }

                }, hour, min, false);

                calendarTimePicker.setMessage("시간을 설정해주세요");
                calendarTimePicker.show();

            }
        });

    }


    public void init() {
        viewMoreTitleEt = mBinding.viewMoreTitleEt;
        viewMoreDescriptionEt = mBinding.viewMoreDescriptionEt;
        dialogOkBtn = mBinding.dialogOkBtn;
        dialogCancelBtn = mBinding.dialogCancelBtn;
        startTimeBtn = mBinding.startTimeBtn;
        startTimeTv = mBinding.startTimeTv;
        endTimeBtn = mBinding.endTimeBtn;
        endTimeTv = mBinding.endTimeTv;
        spinner = mBinding.spinner;
        description_set_text = mBinding.descriptionSetText;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void calendarUpdateGetDialogText(CalendarDB data) {

        if (type == Define.UPDATE_DIALOG) {
            String dialogGetTitle = data.getTitle();
            String dialogGetDescription = data.getDescription();
            String dialogGetStartTime = data.getStartTime();
            String dialogGetEndTime = data.getEndTime();
            viewMoreTitleEt.setText(dialogGetTitle);
            viewMoreDescriptionEt.setText(dialogGetDescription);
            startTimeTv.setText(dialogGetStartTime);
            endTimeTv.setText(dialogGetEndTime);
        }
    }
}