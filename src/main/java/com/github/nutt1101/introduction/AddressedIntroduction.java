package com.github.nutt1101.introduction;

public class AddressedIntroduction {
    protected String address;
    protected final String lineIntroduction;
    protected String objectCode;
    protected String programmingCounter;

    public AddressedIntroduction(String address, String lineIntroduction, String programmingCounter) {
        this.address = this.fillZero(address);
        this.lineIntroduction = lineIntroduction;
        this.programmingCounter = this.fillZero(programmingCounter);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public String getProgrammingCounter() {
        return programmingCounter;
    }

    public void setProgrammingCounter(String programmingCounter) {
        this.programmingCounter = programmingCounter;
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

    private String fillZero(String operator) {
        int len = operator.length(), i = 2;

        while (len > Math.pow(2, i)) {
            i++;
        }

        StringBuilder operatorBuilder = new StringBuilder(operator);
        while (operatorBuilder.length() < Math.pow(2, i)) {
            operatorBuilder.insert(0, "0");
        }
        operator = operatorBuilder.toString();

        return operator;
    }
}

