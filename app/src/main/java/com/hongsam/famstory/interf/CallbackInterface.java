package com.hongsam.famstory.interf;

import com.hongsam.famstory.firebase.CalendarDB;

public interface CallbackInterface {
    public void view_more_text(CalendarDB data);

    public void isDateNull(String date);
}