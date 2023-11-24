package com.example.androidmidterm_firebase;

public class StudentModel {
    String studentId,name, className, address;
    Long age;
    Double score;
    public StudentModel(){

    }
    public StudentModel(String studentId, String name, String className, String address, Long age, Double score) {
        this.studentId = studentId;
        this.name = name;
        this.className = className;
        this.address = address;
        this.age = age;
        this.score = score;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getAge() {
        return age;
    }

    public void setAge(Long age) {
        this.age = age;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
