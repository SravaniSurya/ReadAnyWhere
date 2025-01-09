package com.example.pageflow;

public class user {

    private String userId;
    private String firstName;
    private String email;
    private String role;
    private String profileImageUrl;
    private String photoUrl;


    public user() {
    }


    public user(String firstName, String email, String role, String profileImageUrl,String photoUrl) {
        this.firstName = firstName;
        this.email = email;
        this.role = role;
        this.profileImageUrl = profileImageUrl;
        this.photoUrl = photoUrl;

    }
    public user(String firstName, String email, String role) {
        this.firstName = firstName;
        this.email = email;
        this.role = role;
        this.profileImageUrl = "";
        this.photoUrl = "";
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

public String getPhotoUrl() {
        return photoUrl;
}

public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
}
}