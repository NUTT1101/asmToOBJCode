package com.github.nutt1101;

public class AddressedCode {
    private final String address;
    private final String lineIntroduction;
    private String objectCode;

    public AddressedCode(String address, String lineIntroduction) {
        this.address = address;
        this.lineIntroduction = lineIntroduction;
    }

    public String getAddress() {
        return address;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public String getLineIntroduction() {
        return lineIntroduction;
    }

    @Override
    public String toString() {
        return "AddressedCode{" +
                "address='" + address + '\'' +
                ", lineIntroduction='" + lineIntroduction + '\'' +
                ", objectCode='" + objectCode + '\'' +
                '}';
    }
}

