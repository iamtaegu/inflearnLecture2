package com.jojoldu.book.springboot.config.auth;

import com.jojoldu.book.springboot.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter (MethodParameter parameter) {

        boolean isLoginUserAnnotation = parameter.getMethodAnnotation(LoginUser.class) != null;
        boolean isUserClss = SessionUser.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isUserClss; //파마리터에 @LoginUser 어노테이션이 붙어 있고, 클래스 타입이 SessionUser면 true
    }

    @Override
    public Object resolveArgument (MethodParameter parameter, ModelAndViewContainer mavContainer
                                    , NativeWebRequest webRequest, WebDataBinderFactory binderFactory)
                                        throws Exception {
        return httpSession.getAttribute("user");
    }
}
