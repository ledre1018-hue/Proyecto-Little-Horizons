package com.littlehorizons.erp.model;

import java.time.LocalTime;

/**
 * Day and time of a class for a teacher assignment (table horarios_clase).
 */
public class ClassSchedule {

    private int id;
    private int assignmentId;
    private int teacherId;
    private String subjectName;
    private WeekDay day;
    private LocalTime startTime;
    private LocalTime endTime;

    public ClassSchedule() {
    }

    public ClassSchedule(int assignmentId, WeekDay day, LocalTime startTime, LocalTime endTime) {
        this.assignmentId = assignmentId;
        this.day = day;
        this.startTime = startTime;
        setEndTime(endTime);
    }

    /** Two classes clash when they are on the same day and their hours overlap. */
    public boolean overlapsWith(ClassSchedule other) {
        return day == other.day && startTime.isBefore(other.endTime) && other.startTime.isBefore(endTime);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(int assignmentId) {
        this.assignmentId = assignmentId;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public WeekDay getDay() {
        return day;
    }

    public void setDay(WeekDay day) {
        this.day = day;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        if (endTime == null || startTime == null || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        this.endTime = endTime;
    }
}
