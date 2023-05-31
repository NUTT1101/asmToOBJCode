package com.github.nutt1101.introduction;

import com.github.nutt1101.ObjectCodeEncoder;

import java.util.List;
import java.util.Locale;

public class VariableIntroduction extends AddressedIntroduction {
    private VariableType variableType;

    public VariableType getVariableType() {
        return variableType;
    }

    public void setVariableType(VariableType variableType) {
        this.variableType = variableType;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<String> numbers) {
        this.numbers = numbers;
    }

    private String variableName;
    private List<String> numbers;


    public VariableIntroduction(String address, String lineIntroduction, String programmingCounter) {
        super(address, lineIntroduction, programmingCounter);
        initNumbers();
    }

    private void initNumbers() {
        var list = ObjectCodeEncoder.getPureIntroduction(this.lineIntroduction);
        variableName = list.get(0).replace(":", "");
        variableType = VariableType.valueOf(
                list.get(1).toUpperCase()
        );
        numbers = list.subList(2, list.size());
    }
}
