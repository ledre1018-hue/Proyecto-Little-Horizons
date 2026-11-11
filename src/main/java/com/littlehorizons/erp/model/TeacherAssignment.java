package com.littlehorizons.erp.model;

/**
 * A subject assigned to a teacher for one year and section (table asignaciones_profesor).
 */
public class TeacherAssignment {

    private int id;
    private int teacherId;
    private String teacherName;
    private int subjectId;
    private String subjectName;
    private int academicYear;
    private String section;

    public TeacherAssignment() {
    }

    public TeacherAssignment(int teacherId, int subjectId, int academicYear, String section) {
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.academicYear = academicYear;
        setSection(section);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(int academicYear) {
        this.academicYear = academicYear;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        if (section == null || section.isBlank() || section.trim().length() > 10) {
            throw new IllegalArgumentException("La sección es obligatoria (máximo 10 caracteres).");
        }
        this.section = section.trim().toUpperCase();
    }
}
