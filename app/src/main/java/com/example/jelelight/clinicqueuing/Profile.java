package com.example.jelelight.clinicqueuing;

public class Profile {

    private String name,bdate,bmonth,byear,blood,caution,gender,height,weight,phone;

    public Profile(){

    }

    public Profile(String name,String gender,String bdate,
                   String bmonth,String byear,String blood,
                   String weight,String height,String phone,
                   String caution){
        this.name = name;
        this.gender = gender;
        this.bdate = bdate;
        this.bmonth = bmonth;
        this.byear = byear;
        this.blood = blood;
        this.caution = caution;
        this.height = height;
        this.weight = weight;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public String getBmonth() {
        return bmonth;
    }

    public void setBmonth(String bmonth) {
        this.bmonth = bmonth;
    }

    public String getByear() {
        return byear;
    }

    public void setByear(String byear) {
        this.byear = byear;
    }

    public String getBlood() {
        return blood;
    }

    public void setBlood(String blood) {
        this.blood = blood;
    }

    public String getCaution() {
        return caution;
    }

    public void setCaution(String caution) {
        this.caution = caution;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
