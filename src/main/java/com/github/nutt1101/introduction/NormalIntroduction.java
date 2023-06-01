package com.github.nutt1101.introduction;

import com.github.nutt1101.ObjectCodeEncoder;

import java.util.List;

public class NormalIntroduction extends AddressedIntroduction{
    String first;
    List<String> args;

    public NormalIntroduction(String address, String lineIntroduction, String programmingCounter) {
        super(address, lineIntroduction, programmingCounter);
        initArgs();
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getFirst() {
        return first;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    private void initArgs() {
        args = ObjectCodeEncoder.getPureIntroduction(this.lineIntroduction);
        first = args.get(0);
        args = args.subList(1, args.size());
    }
}
