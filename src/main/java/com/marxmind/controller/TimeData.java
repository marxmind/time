package com.marxmind.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@ToString
public class TimeData
{
    private String date;
    private String employeeName;
    private String time1;
    private String time2;
    private String time3;
    private String time4;
    private String time5;
    private String time6;
    private String remarks;
    private String late;
    private String lateCharge;
    private String morningTotalTime;
    private String afternoonTotalTime;
    private String totalRenderedTime;
    private String totalOt;
    private TimeRecord data1;
    private TimeRecord data2;
    private TimeRecord data3;
    private TimeRecord data4;
    private TimeRecord data5;
    private TimeRecord data6;

}
