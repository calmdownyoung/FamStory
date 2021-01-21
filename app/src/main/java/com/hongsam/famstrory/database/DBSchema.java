package com.hongsam.famstrory.database;

/**
 * 테이블의 구조 등을 명시 CREATE, DROP문 등이 있다.
 * 작성자 : 한재훈
 */

public interface DBSchema {

    String TB_EMOTION = "TB_EMOTION";
    String CREATE_TB_EMOTION = "create table if not exists TB_EMOTION (" +
            "SEQ_NO INTEGER primary key autoincrement -- 시퀀스넘버\n" +
            ",EMOTION_SENDER VARCHAR(20) -- 보내는사람\n" +
            ",EMOTION_MESSAGE VARCHAR(100) -- 메세지내용\n" +
            ",EMOTION_SEND_DATE VARCHAR(20) -- 보낸시간\n" +
            ")";
    String DROP_TB_EMOTION = "drop table if exists TB_EMOTION";

    String TB_LETTER = "TB_LETTER";
}