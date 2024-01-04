package com.haiziwang.gen.code.infra.utils.rest;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

public class MyMappingJackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {
    public MyMappingJackson2HttpMessageConverter(){
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED);  //加入text/html类型的支持
        setSupportedMediaTypes(mediaTypes);
    }
}
