package com.example.saurabh.firebaseconnect;
public class HospitalData {
    public String HospitalName;
    public String TreatmentName;
    public String StartDate;
    public String EndDate;

    public HospitalData(){}

    public HospitalData(String hospitalName, String treatmentName, String startDate, String endDate) {
        this.HospitalName = hospitalName;
        this.TreatmentName = treatmentName;
        this.StartDate = startDate;
        this.EndDate = endDate;
    }
}