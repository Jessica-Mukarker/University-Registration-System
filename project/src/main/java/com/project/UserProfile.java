package com.project;


public class UserProfile {
    protected String name;
    private String id;
    private String contactDetails;
    private Role role;
    private String password;

    public UserProfile(String name, String id,String contactDetails, Role role, String password) {
        this.name = name;
        this.id = id;
        this.contactDetails = contactDetails;
        this.role = role;
        this.password = password;
    }

      public UserProfile(String name, int id,String contactDetails, Role role, String password) {
        this.name = name;
        this.id = id+"";
        this.contactDetails = contactDetails;
        this.role = role;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public Role getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

}


