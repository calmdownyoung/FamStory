package com.hongsam.famstrory.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.hongsam.famstrory.R;
import com.hongsam.famstrory.activitie.MainActivity;
import com.hongsam.famstrory.define.Define;

import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class LetterWriteFragment extends Fragment {

    private final int GET_GALLERY_IMAGE = 200;

    MainActivity mainActivity;
    View mContentView;
    ImageButton mBackBtn;
    ImageButton mPhoto;
    ImageView mPhotoView;
    EditText mContants;
    ConstraintLayout mConstraintLayout;
    ScrollView mScrollView;
    ImageView mBackgound;
    InputMethodManager imm;
    Button mSendBtn;

    CardView mCardView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);

    }


    /**
     * View 객체를 얻는 시점
     * */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (container == null)
            return null;

        if (inflater == null)
            return null;

        mainActivity = (MainActivity) getActivity();

        mContentView = inflater.inflate(R.layout.fragment_letter_write, container, false);

        init(mContentView);

        return mContentView;
    }




    /*
     * 액티비티와 사용자의 상호작용 함수
     * */
    public void onResume() {
        super.onResume();

        //toolbar의 뒤로가기 버튼
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.changeFragment(Define.FRAGMENT_ID_LETTER_LIST);
            }
        });

        //갤러리에서 사진 가져오기 버튼
        mPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        //화면 터치시 키보드 내리기
        mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mContants.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(mScrollView.getWindowToken(), 0);
            }
        });


    }



    /**
     * 컨트롤 초기화 해주는 함수
     * */
    public void init(View v) {
        if (v != null) {
            mBackBtn = mContentView.findViewById(R.id.letter_write_back_btn);
            mContants = mContentView.findViewById(R.id.contants_tv);
            mPhotoView = mContentView.findViewById(R.id.photo_iv);
            mPhoto = mContentView.findViewById(R.id.gallery_img_btn);
            mConstraintLayout = mContentView.findViewById(R.id.fragment_letter_write);
            mScrollView = mContentView.findViewById(R.id.letter_write_scroll);
            mBackgound = mContentView.findViewById(R.id.letter_write_img_view);

            mCardView = mContentView.findViewById(R.id.letter_cardView);
        }
    }


    /**
     * 이미지 리소스 세팅해주는 함수
     * */
    public void setImageResource() {
        // 예시) button1.setBackgroundResource(R.drawable.image1);
    }


    //갤러리에서 선택한 이미지 뿌려주기
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            mPhotoView.setImageURI(selectedImageUri);
        }
    }



    /**
     * 각종 리소스 null 처리
     * */
    @Override
    public void onDestroy() {
        super.onDestroy();
        // 예시) button1 = null;
    }
}