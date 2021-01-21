package com.hongsam.famstory.fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import android.widget.Button;
import android.widget.ListView;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hongsam.famstory.R;
import com.hongsam.famstory.activitie.MainActivity;
import com.hongsam.famstory.databinding.SpinnerMangerBinding;
import com.hongsam.famstory.firebase.CalendarFirebaseDB;

import java.util.ArrayList;

/**
 * 스피너 추가,삭제 담당 프레그먼트
 * 여기서 조작한 스피너를 파이어베이스에 보냄
 * 2021-01-19 이승호
 */
public class SpinnerMangerFragment extends Fragment {

    protected MainActivity mainActivity;
    private Button addItem, deleteItem;
    private ListView spinnerList;
    private View root;
    private CalendarFirebaseDB firebaseDB = new CalendarFirebaseDB();
    CalendarFirebaseDB.SpinnerMangerDB mangerDB = firebaseDB.new SpinnerMangerDB();
    View mContentView;


    private SpinnerMangerBinding mb;
    ArrayAdapter<String> adapter;
    Spinner spinner;
    ArrayAdapter<String> adapter1;
    ArrayList<String> itemList;
    CalendarFirebaseDB.SpinnerDB spinnerDB = firebaseDB.new SpinnerDB(getContext());


    public SpinnerMangerFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
        mainActivity = (MainActivity) getActivity();
        mb = SpinnerMangerBinding.inflate(getLayoutInflater());
        root = mb.getRoot();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        itemList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, itemList);

        mb.spinnerList.setAdapter(adapter);
        mb.spinnerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String clickItem =((TextView)view).getText().toString();
                adapter.remove(clickItem);
                Toast.makeText(getContext(),"삭제되었습니다.",Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        //mb.spinnerList.setAdapter(adapter);

        mb.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem();
            }
        });
        mb.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.clear();
            }
        });
        return root;
    }
    private void addItem(){
        final EditText editText = new EditText(getContext());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        editText.setHint("아이템을 입력해주세요");

        builder.setTitle("추가하실 아이템을 입력하세요");
        builder.setView(editText);
        builder.setPositiveButton("추가", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                adapter.add(editText.getText().toString());
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //dialog.dismiss();
            }
        });
        builder.show();
    }
    private void removeAll() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("전체삭제됩니다");
        builder.setMessage("한개씩 삭제하고싶으면 아이템을 꾹누르면 됩니다.");
        builder.setPositiveButton("삭제",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        adapter.clear();
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.show();
    }

}