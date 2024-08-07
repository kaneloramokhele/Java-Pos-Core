/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pharmacy_pos;

/**
 *
 * @author DELL
 */
class ClassUser {
    private final String name,surname,phone,idPassport,nationality,district,resident,gender,nextOfKin,userRole,username;//,pwrd;

    public ClassUser(String name,String surname,String phone, String idPassport, String nationality, String district, String resident, String gender, String nextOfKin, String userRole, String username) {
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.idPassport = idPassport;
        //this.pwrd = pwrd;
        this.nationality = nationality;
        this.district = district;
        this.resident = resident;
        this.gender = gender;
        //this.pwrd = pwrd;
        this.nextOfKin = nextOfKin;
        this.userRole = userRole;
        this.username = username;

    }//end of Constructor

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPhone() {
        return phone;
    }

    public String getIdPassport() {
        return idPassport;
    }

    public String getNationality() {
        return nationality;
    }

    public String getDistrict() {
        return district;
    }

    public String getResident() {
        return resident;
    }

    public String getGender() {
        return gender;
    }

    public String getNextOfKin() {
        return nextOfKin;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getUsername() {
        return username;
    }
    
    
    
    
}//end of class User
