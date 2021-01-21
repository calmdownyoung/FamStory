package com.hongsam.famstrory.activitie;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.amitshekhar.DebugDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hongsam.famstrory.R;
import com.hongsam.famstrory.adapter.ViewPagerAdapter;
import com.hongsam.famstrory.data.Calendar;
import com.hongsam.famstrory.data.Family;
import com.hongsam.famstrory.data.Member;
import com.hongsam.famstrory.database.DBFamstory;
import com.hongsam.famstrory.databinding.ActivityMainBinding;
import com.hongsam.famstrory.define.Define;
import com.hongsam.famstrory.firebase.ReadDB;
import com.hongsam.famstrory.firebase.UpdateDB;
import com.hongsam.famstrory.fragment.CalendarFragment;
import com.hongsam.famstrory.fragment.ChattingFragment;
import com.hongsam.famstrory.fragment.EmotionFragment;
import com.hongsam.famstrory.fragment.FamCreateFragment;
import com.hongsam.famstrory.fragment.LetterListFragment;
import com.hongsam.famstrory.fragment.LetterReadFragment;
import com.hongsam.famstrory.fragment.LetterWriteFragment;
import com.hongsam.famstrory.fragment.MenuFragment;
import com.hongsam.famstrory.fragment.MonthCalendar;
import com.hongsam.famstrory.fragment.MonthCalendarFragment;
import com.hongsam.famstrory.fragment.ProfileFragment;
import com.hongsam.famstrory.fragment.SettingFragment;
import com.hongsam.famstrory.fragment.SpinnerMangerFragment;
import com.hongsam.famstrory.fragment.TimeLineFragment;
import com.hongsam.famstrory.interf.CallbackInterface;
import com.hongsam.famstrory.interf.CustomDialogInterface;
import com.hongsam.famstrory.util.FirebaseManager;
import com.hongsam.famstrory.util.SharedManager;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements CalendarFragment.DataSender,TimeLineFragment.sendTimeLineFR {
    private final String TAG = "MainActivity";

    BottomNavigationView navigationView;

    CallbackInterface ci;
    CustomDialogInterface cdi;
    ReadDB readDB;
    UpdateDB updateDB;
    InputMethodManager imm;
    Spinner spinner;
    ArrayAdapter<String> adapter;
    String name;
    String nickName;
    private ActivityMainBinding mb;
    private View root;
    public DBFamstory db;
    private SQLiteDatabase sqLiteDatabase;

    // 가족객체. db에서 받아와서 넣어줄 예정
    public Family myFamily;
    String famName = "테스트가족";
    ArrayList<Member> memberList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mb = ActivityMainBinding.inflate(getLayoutInflater());
        root = mb.getRoot();
        setContentView(root);
        SharedManager.getInstance(this);
        db = DBFamstory.getInstance(this);

        // Firebase로부터 Token값을 받아 firebase database와 sharedPreference에 저장해준다.
        getFirebaseToken();

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);


        changeFragment(Define.FRAGMENT_ID_PROFILE);

        navigationView = (BottomNavigationView) findViewById(R.id.navi_view);


        readDB = new ReadDB(this);
        updateDB = new UpdateDB(this);

        getFamilyMembers();

        changeFragment(Define.FRAGMENT_ID_LETTER_LIST);
        DebugDB.getAddressLog();
    }

    public void writeMember(String famName, String token, Member member) {
        FirebaseManager.dbFamRef.child(famName).child("members").child(token).setValue(member);
    }

    public void updateMember(String famName, Member member) {
        String token = SharedManager.readString(Define.KEY_FIREBASE_TOKEN, "");
        Map<String, Object> memberMap = new HashMap<>();
        memberMap.put(token, member);
        FirebaseManager.dbFamRef.child(famName).child("members").updateChildren(memberMap);
    }

    public void getFirebaseToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.e(TAG, "파이어베이스 토큰 등록 실패", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "파이어베이스 토큰 : " + token);
                    checkToken(token);
                }
            });
    }

    // 파이어베이스로부터 가족 객체를 얻어오는 함수
    public void getFamilyMembers() {
        Query query = FirebaseManager.dbFamRef.child(famName).child("members").orderByChild("relation");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    //Log.d(TAG, "멤버 relation : " + singleSnapshot.getValue(HashMap.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void checkToken(final String token) {
        FirebaseManager.dbFamRef.child(famName).child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean flag = false;
                for (DataSnapshot singleSnapshot : snapshot.getChildren()) {
                    if (singleSnapshot.getKey().equals(SharedManager.readString(Define.KEY_FIREBASE_TOKEN, ""))) {
                        flag = true;
                    }
                }

                if (!flag) {
                    Log.d(TAG, "db에 토큰 없음! 새로 추가!");
                    saveToken(token);
                } else
                    Log.d(TAG, "db에 토큰 있음!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void saveToken(final String token) {
        SharedManager.writeString(Define.KEY_FIREBASE_TOKEN, token);
        writeMember("테스트가족", token, new Member("아들", "김아들"));
    }

    public void setCallbackInterface(CallbackInterface ci) {
        this.ci = ci;
    }

    public void setCustomInterface(CustomDialogInterface cdi) {
        this.cdi = cdi;
    }

    public void databaseRead(int year, int month, int day, String date) {
        readDB.databaseRead(year,month,day);
    }

    public void calendarUpdateGetDialogText(Calendar data) {
        cdi.calendarUpdateGetDialogText(data);
    }
    public void visibleView(int dataIsNull){
        ci.visibleView(dataIsNull);

    }

    public void view_more_text(Calendar data) {
        ci.view_more_text(data);
    }

    Fragment fragment = null;
    @Override
    protected void onResume() {
        super.onResume();

        ViewPagerAdapter viewPagerAdapter =  new ViewPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdapter.addItem(new CalendarFragment());
        viewPagerAdapter.addItem(new TimeLineFragment());

        mb.viewPager.setAdapter(viewPagerAdapter);
        mb.tabLayout.setupWithViewPager(mb.viewPager);
        checkSelfPermission();

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.calendar_menu:
                        mb.basic.setVisibility(View.VISIBLE);
                        mb.viewPager.setVisibility(View.GONE);
                        mb.tabLayout.setVisibility(View.GONE);
                        changeFragment(Define.FRAGMENT_ID_PROFILE);
                        break;
                    case R.id.main_menu:
                        mb.basic.setVisibility(View.GONE);
                        mb.viewPager.setVisibility(View.VISIBLE);
                        mb.tabLayout.setVisibility(View.VISIBLE);
                        //changeFragment(Define.FRAGMENT_ID_CALENDAR);
                        break;
                    case R.id.message_menu:
                        mb.basic.setVisibility(View.VISIBLE);
                        mb.viewPager.setVisibility(View.GONE);
                        mb.tabLayout.setVisibility(View.GONE);
                        changeFragment(Define.FRAGMENT_ID_LETTER_LIST);
                        break;
                    case R.id.emotion_menu:
                        mb.basic.setVisibility(View.VISIBLE);
                        mb.viewPager.setVisibility(View.GONE);
                        mb.tabLayout.setVisibility(View.GONE);
                        changeFragment(Define.FRAGMENT_ID_EMOTION);
                        break;
                    case R.id.setting_menu:
                        mb.basic.setVisibility(View.VISIBLE);
                        mb.viewPager.setVisibility(View.GONE);
                        mb.tabLayout.setVisibility(View.GONE);
                        changeFragment(Define.FRAGMENT_ID_SETTING);
                        break;
                }
                return true;
            }
        });
    }


    @Override
    public void sendName(String name, String nickName) {
        this.name = name;
        this.nickName = nickName;
    }
    public void changeFragment(int fragmentId) {

        switch (fragmentId) {
            case Define.FRAGMENT_ID_MENU:
                fragment = new MenuFragment();
                break;

            case Define.FRAGMENT_ID_FAM_CREATE:
                fragment = new FamCreateFragment();
                break;

            case Define.FRAGMENT_ID_TIME_LINE:
                fragment = new TimeLineFragment();
                break;

            case Define.FRAGMENT_ID_CALENDAR:
                fragment = new CalendarFragment();
                break;

            case Define.FRAGMENT_ID_LETTER_LIST:
                fragment = new LetterListFragment();
                break;

            case Define.FRAGMENT_ID_LETTER_WRITE:
                fragment = new LetterWriteFragment();
                break;

            case Define.FRAGMENT_ID_EMOTION:
                fragment = new EmotionFragment();
                break;

            case Define.FRAGMENT_ID_PROFILE:
                fragment = new ProfileFragment();
                break;

            case Define.FRAGMENT_ID_SETTING:
                fragment = new SettingFragment();
                break;

            case Define.FRAGMENT_ID_LETTER_READ:
                fragment = new LetterReadFragment();
                break;
            case Define.FRAGMENT_ID_MONTH_LIST:
                fragment = new MonthCalendarFragment();
                break;
            case Define.FRAGMENT_ID_SPINNER_MANGER:
                fragment = new SpinnerMangerFragment();
                break;
            case Define.FRAGMENT_ID_CHATTING:
                Log.e(TAG,name+nickName);
                fragment = new ChattingFragment(name,nickName);
            default:
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
                    try {
                        getSupportFragmentManager().popBackStack();
                    } catch (IllegalStateException e) {
                        if (getSupportFragmentManager() != null && !getSupportFragmentManager().isStateSaved()) {
                            getSupportFragmentManager().popBackStack();
                        }
                    }
                }

                ft.replace(R.id.basic, fragment);

                if (fragment.isStateSaved()) {
                    ft.commitAllowingStateLoss();
                } else {
                    ft.commit();
                }
            }
        });
    }

    public void showKeyboard(final EditText et, final boolean flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    et.requestFocus();
                    imm.showSoftInput(et, 0);
                } else {
                    imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
                }
            }
        });
    }

    public void checkSelfPermission() {
        String temp = "";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.READ_EXTERNAL_STORAGE + " ";
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            temp += Manifest.permission.WRITE_EXTERNAL_STORAGE + " ";
        }

        if (!TextUtils.isEmpty(temp)) {
            ActivityCompat.requestPermissions(this, temp.trim().split(" "), 1);
        } else {
            Toast.makeText(this, "권한을 모두 허용", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void spinnerFragment(Spinner spinner, ArrayAdapter<String> adapter) {
        this.spinner = spinner;
        this.adapter = adapter;
    }

}