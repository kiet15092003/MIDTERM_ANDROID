package com.example.androidmidterm_firebase;

public class CertificateModel {
    private String certificateName, studentId;
    private double score;
    public CertificateModel(){

    }
    public CertificateModel(String certificateName, String studentId, double score) {
        this.certificateName = certificateName;
        this.studentId = studentId;
        this.score = score;
    }

    public String getCertificateName() {
        return certificateName;
    }

    public void setCertificateName(String certificateName) {
        this.certificateName = certificateName;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
