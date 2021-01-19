
package com.hongsam.famstrory.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hongsam.famstrory.ItemTouchHelper.RecyclerItemTouchHelper;
import com.hongsam.famstrory.R;
import com.hongsam.famstrory.activitie.MainActivity;
import com.hongsam.famstrory.adapter.LetterListAdapter;
import com.hongsam.famstrory.data.LetterContants;
import com.hongsam.famstrory.data.LetterContants;
import com.hongsam.famstrory.data.LetterList;
import com.hongsam.famstrory.define.Define;

import java.util.ArrayList;
import java.util.List;

import static com.hongsam.famstrory.fragment.LetterWriteFragment.TEST_FAMILY;

/*
 * 편지 목록 화면
 * 1/4 , 오나영
 * */

public class LetterListFragment extends Fragment {

    private MainActivity mainActivity;
    private View mContentView;

    private FirebaseDatabase mDb;
    private DatabaseReference mFamRef;

    private RecyclerView recyclerView;
    private LetterListAdapter letterListAdapter;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private ArrayList<LetterList> itemList;


    public LetterListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);


    }


    /**
     * View 객체를 얻는 시점
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null)
            return null;

        if (inflater == null)
            return null;

        mainActivity = (MainActivity) getActivity();
        //View mContentView;
        mContentView = inflater.inflate(R.layout.fragment_letter_list, container, false);

        init(mContentView);

        return mContentView;

    }


    public void onResume() {
        super.onResume();
        //recycle 관련
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        itemList = new ArrayList<>();

        //DB에서 편지 리스트 뿌려주기
        mFamRef = FirebaseDatabase.getInstance().getReference("Family").child(TEST_FAMILY).child("LetterContants");
        mFamRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot npsnapshot : snapshot.getChildren()) {
                        LetterList al = npsnapshot.getValue(LetterList.class);
                        itemList.add(al);
                    }
                    letterListAdapter = new LetterListAdapter(itemList, mainActivity);
                    recyclerView.setAdapter(letterListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    /**
     * 컨트롤 초기화 해주는 함수
     */
    public void init(View v) {
        if (v != null) {
            coordinatorLayout = mContentView.findViewById(R.id.coordinatorlayout);
            fab = mContentView.findViewById(R.id.f_latter_send_fab_btn);
            recyclerView = mContentView.findViewById(R.id.f_latter_list_recycler);


            //편지보내기로 전환
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.changeFragment(Define.FRAGMENT_ID_LETTER_WRITE);
                }
            });


            //스크롤시 fab 숨기 , 스크롤시 fab 나타남
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                //스크롤이 얼마나 되었는지의 값
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        fab.show();
                    } else if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                        fab.hide();
                    }
                }
            });

        }
    }


    /**
     * 각종 리소스 null 처리
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 예시) button1 = null;
    }
}