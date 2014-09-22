@XmlJavaTypeAdapters({
        @XmlJavaTypeAdapter(type = LocalDateTime.class,
                value = LocalDateTimeAdapter.class)
})

package com.diosoft.calendar.server.adapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.LocalDateTime;