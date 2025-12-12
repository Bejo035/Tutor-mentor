package ge.batumi.tutormentor.config;

import ge.batumi.tutormentor.utils.NullIgnoreBeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Bean
    public BeanUtilsBean beanUtils(){
        return new NullIgnoreBeanUtils();
    }
}
