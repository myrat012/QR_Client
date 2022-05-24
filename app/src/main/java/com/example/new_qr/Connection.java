package com.example.new_qr;

public class Connection {
    private final String IP = "http://192.168.43.19/";

    public String getIP() {
        return IP;
    }

    public String sendUserData(){
        return IP + "qr/userJson.php";
    }

    public String getUserData(){
        return IP + "qr/getUserJson.php";
    }

    public String sendGuwaHatData(){
        return IP + "qr/setGuwaJson.php";
    }

    public String getGuwaHatData(){
        return IP + "qr/getGuwaJson.php";
    }

    public String sendGuratHatData(){
        return IP + "qr/setGuratJson.php";
    }

    public String getGuratHatData(){
        return IP + "qr/getGuratJson.php";
    }
}