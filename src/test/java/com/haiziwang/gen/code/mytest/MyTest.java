package com.haiziwang.gen.code.mytest;

import org.apache.commons.lang.WordUtils;
import org.junit.Test;

public class MyTest {
    @Test
    public void test(){
        String str = WordUtils.capitalizeFully("contractCode", new char[]{'_'});
        System.out.println(str);
        String str2 = WordUtils.capitalizeFully("contract_code", new char[]{'_'});
        System.out.println(str2);
        String str3 = WordUtils.capitalize("contractCode", new char[]{'_'});
        System.out.println(str3);



    }
}
