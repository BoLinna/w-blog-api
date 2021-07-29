package xyz.firstmeet.lblog.config;

import xyz.firstmeet.lblog.interceptor.CodeInterceptor;
import xyz.firstmeet.lblog.interceptor.CrosInterceptor;
import xyz.firstmeet.lblog.interceptor.MappingInterceptor;
import xyz.firstmeet.lblog.interceptor.PassTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置拦截器
 *
 * @author windSnowLi
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private CrosInterceptor cros;

    @Autowired
    public void setCros(CrosInterceptor cros) {
        this.cros = cros;
    }

    private MappingInterceptor mappingInterceptor;

    @Autowired
    public void setMappingInterceptor(MappingInterceptor mappingInterceptor) {
        this.mappingInterceptor = mappingInterceptor;
    }

    private PassTokenInterceptor passTokenInterceptor;

    @Autowired
    public void setPassTokenInterceptor(PassTokenInterceptor passTokenInterceptor) {
        this.passTokenInterceptor = passTokenInterceptor;
    }


    private CodeInterceptor codeInterceptor;

    @Autowired
    public void setCodeInterceptor(CodeInterceptor codeInterceptor) {
        this.codeInterceptor = codeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(codeInterceptor).addPathPatterns("/**");
        registry.addInterceptor(cros).addPathPatterns("/**");
        registry.addInterceptor(mappingInterceptor).addPathPatterns("/**");
        registry.addInterceptor(passTokenInterceptor)
                // 拦截所有请求，通过判断是否有 @LoginRequired 注解 决定是否需要登录
                .addPathPatterns("/**");
    }
}
