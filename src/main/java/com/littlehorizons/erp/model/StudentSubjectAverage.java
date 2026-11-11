package com.littlehorizons.erp.model;

/**
 * Average of one student in one subject. Result of a calculation, not a table.
 */
public class StudentSubjectAverage {

    private final int studentId;
    private final String studentName;
    private final String subjectName;
    private final int gradeId;
    private final String gradeName;
    private final double average;

    public StudentSubjectAverage(int studentId, String studentName, String subjectName, int gradeId,
                                 String gradeName, double average) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.gradeId = gradeId;
        this.gradeName = gradeName;
        this.average = average;
    }

    public String getCourseAndSubject() {
        return gradeName + " - " + subjectName;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public int getGradeId() {
        return gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public double getAverage() {
        return average;
    }
}
