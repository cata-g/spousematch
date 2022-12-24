package com.example.spousematch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ProfileModel {

    private String name;
    private String imgLink;
    private String aboutme, diet, dosh, education, gender, height, partnerPrefs, profession, religion, status, birthdate, phoneNumber;
    private String profileId;
    private Double distance;

    public ProfileModel(String name, String imgLink, String aboutme, String diet, String dosh, String education, String gender, String height, String partnerPrefs, String profession, String religion, String status, String birthdate,String phoneNumber,String profileId, Double distance) {
        this.name = name;
        this.imgLink = imgLink;
        this.aboutme = aboutme;
        this.diet = diet;
        this.dosh = dosh;
        this.education = education;
        this.gender = gender;
        this.height = height;
        this.partnerPrefs = partnerPrefs;
        this.profession = profession;
        this.religion = religion;
        this.status = status;
        this.birthdate = birthdate;
        this.profileId = profileId;
        this.distance = distance;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Double getDistance() {
        return distance;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getAge() throws ParseException {
        Date birthD = new SimpleDateFormat("dd/mm/yyyy").parse((String)this.birthdate);
        Date now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = new SimpleDateFormat("dd/mm/yyyy").parse(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        long dif = now.getTime() - birthD.getTime();
        return String.valueOf( dif / (86400000L * 365));
    }

    public Integer getAgeInt() throws ParseException {
        Date birthD = new SimpleDateFormat("dd/mm/yyyy").parse((String)this.birthdate);
        Date now = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            now = new SimpleDateFormat("dd/mm/yyyy").parse(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        }
        long dif = now.getTime() - birthD.getTime();
        return  (int)( dif / (86400000L * 365));
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getProfileId() {
        return profileId;
    }
    public String getAboutme() {
        return aboutme;
    }

    public String getDiet() {
        return diet;
    }

    public String getDosh() {
        return dosh;
    }

    public String getEducation() {
        return education;
    }

    public String getGender() {
        return gender;
    }

    public String getHeight() {
        return height;
    }

    public String getPartnerPrefs() {
        return partnerPrefs;
    }

    public String getProfession() {
        return profession;
    }

    public String getReligion() {
        return religion;
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgLink() {
        return imgLink;
    }

    public void setImgLink(String imgLink) {
        this.imgLink = imgLink;
    }
}
