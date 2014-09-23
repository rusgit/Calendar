package com.diosoft.calendar.server.adapter;

import com.diosoft.calendar.server.util.DateParser;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

//local code review (vtegza): convert in place instead of additional Adapter class @ 9/23/2014
public class LocalDateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    public LocalDateTime unmarshal(String dataString) throws Exception {
        return DateParser.stringToDate(dataString);
    }
    public String marshal(LocalDateTime date) throws Exception {
        return DateParser.dateToString(date);
    }
}