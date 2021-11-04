package com.intech.yayabureau.Models;

import java.util.Date;

public class Candidates {
    private String Candidate_name,Gender,ID_no, DOB, Mobile_no, device_token, Profile_image,
            County, Ward, Village, Next_of_kin, Kin_phone_no, Experience, Salary,Status,CandidateID,Age,Residence, Employer_name,
            Employer_no,
            Employer_county,
            Employer_city,Employer_ID,Working_status;
    private Date timestamp;
    public Candidates() {
        //empty...
    }

    public Candidates(String candidate_name, String gender, String ID_no, String DOB, String mobile_no, String device_token, String profile_image, String county, String ward, String village, String next_of_kin, String kin_phone_no, String experience, String salary, String status, String candidateID, String age, String residence, String employer_name,
                      String employer_no, String employer_county, String employer_city, String employer_ID, String working_status, Date timestamp) {
        Candidate_name = candidate_name;
        Gender = gender;
        this.ID_no = ID_no;
        this.DOB = DOB;
        Mobile_no = mobile_no;
        this.device_token = device_token;
        Profile_image = profile_image;
        County = county;
        Ward = ward;
        Village = village;
        Next_of_kin = next_of_kin;
        Kin_phone_no = kin_phone_no;
        Experience = experience;
        Salary = salary;
        Status = status;
        CandidateID = candidateID;
        Age = age;
        Residence = residence;
        Employer_name = employer_name;
        Employer_no = employer_no;
        Employer_county = employer_county;
        Employer_city = employer_city;
        Employer_ID = employer_ID;
        Working_status = working_status;
        this.timestamp = timestamp;
    }


    public String getWorking_status() {
        return Working_status;
    }

    public void setWorking_status(String working_status) {
        Working_status = working_status;
    }

    public String getEmployer_ID() {
        return Employer_ID;
    }

    public void setEmployer_ID(String employer_ID) {
        Employer_ID = employer_ID;
    }

    public String getEmployer_name() {
        return Employer_name;
    }

    public void setEmployer_name(String employer_name) {
        Employer_name = employer_name;
    }

    public String getEmployer_no() {
        return Employer_no;
    }

    public void setEmployer_no(String employer_no) {
        Employer_no = employer_no;
    }

    public String getEmployer_county() {
        return Employer_county;
    }

    public void setEmployer_county(String employer_county) {
        Employer_county = employer_county;
    }

    public String getEmployer_city() {
        return Employer_city;
    }

    public void setEmployer_city(String employer_city) {
        Employer_city = employer_city;
    }

    public String getCandidate_name() {
        return Candidate_name;
    }

    public void setCandidate_name(String candidate_name) {
        Candidate_name = candidate_name;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getID_no() {
        return ID_no;
    }

    public void setID_no(String ID_no) {
        this.ID_no = ID_no;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getMobile_no() {
        return Mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        Mobile_no = mobile_no;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getProfile_image() {
        return Profile_image;
    }

    public void setProfile_image(String profile_image) {
        Profile_image = profile_image;
    }

    public String getCounty() {
        return County;
    }

    public void setCounty(String county) {
        County = county;
    }

    public String getWard() {
        return Ward;
    }

    public void setWard(String ward) {
        Ward = ward;
    }

    public String getVillage() {
        return Village;
    }

    public void setVillage(String village) {
        Village = village;
    }

    public String getNext_of_kin() {
        return Next_of_kin;
    }

    public void setNext_of_kin(String next_of_kin) {
        Next_of_kin = next_of_kin;
    }

    public String getKin_phone_no() {
        return Kin_phone_no;
    }

    public void setKin_phone_no(String kin_phone_no) {
        Kin_phone_no = kin_phone_no;
    }

    public String getExperience() {
        return Experience;
    }

    public void setExperience(String experience) {
        Experience = experience;
    }

    public String getSalary() {
        return Salary;
    }

    public void setSalary(String salary) {
        Salary = salary;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getCandidateID() {
        return CandidateID;
    }

    public void setCandidateID(String candidateID) {
        CandidateID = candidateID;
    }

    public String getAge() {
        return Age;
    }

    public void setAge(String age) {
        Age = age;
    }

    public String getResidence() {
        return Residence;
    }

    public void setResidence(String residence) {
        Residence = residence;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
