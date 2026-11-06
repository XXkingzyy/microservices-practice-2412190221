package com.zjgsu.jzy.model;

import jakarta.persistence.Embeddable;
import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * 上课时段（值对象，嵌入课程表）
 * 例如 周二 14:00-16:00
 */
@Embeddable
public class ScheduleSlot {

    private DayOfWeek dayOfWeek;  // 星期几
    private LocalTime startTime;  // 开始时间
    private LocalTime endTime;    // 结束时间

    protected ScheduleSlot() {} // JPA 无参构造器

    public ScheduleSlot(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
}
