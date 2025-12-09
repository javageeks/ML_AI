package com.example.demo_server.model;

import lombok.Data;

@Data
public class HolidayWindow {
        private String type;   // "school" or "office"
        private String start;  // "YYYY-MM-DD"
        private String end;    // "YYYY-MM-DD"

}
