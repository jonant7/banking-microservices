package com.banking.account.infrastructure.util;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
public class MessageUtils {

    private static MessageSource messageSource;

    public MessageUtils(MessageSource messageSource) {
        MessageUtils.messageSource = messageSource;
    }

    public static String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }

    public static String getMessage(String key, Object... args) {
        if (Objects.isNull(messageSource)) {
            return key;
        }
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, key, locale);
    }

}