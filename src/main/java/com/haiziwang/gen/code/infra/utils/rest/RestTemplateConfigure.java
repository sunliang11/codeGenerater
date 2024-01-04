package com.haiziwang.gen.code.infra.utils.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Configuration
public class RestTemplateConfigure {

    @Value("${restTemplate.connection-request-timeout}")
    private String connectionRequestTimeout;

    @Value("${restTemplate.connect-timeout}")
    private String connectTimeout;

    @Value("${restTemplate.read-timeout}")
    private String readTimeout;

    @Bean
    public RestTemplate getRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

//        MappingJackson2HttpMessageConverter convert = new MappingJackson2HttpMessageConverter();
//        convert.setSupportedMediaTypes(Arrays.asList(MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON,MediaType.APPLICATION_FORM_URLENCODED));
//        restTemplate.getMessageConverters().add(0, convert);

//        restTemplate.getMessageConverters().add(new MyMappingJackson2HttpMessageConverter());
//        List<HttpMessageConverter<?>> messageConverters= restTemplate.getMessageConverters();
//        messageConverters.

        return restTemplate;
    }

}
