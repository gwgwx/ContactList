/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package online.touch.easy.desktop.util;

import java.util.function.Predicate;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author George
 */
public class EmailPredicate implements Predicate<String> {

    @Override
    public boolean test(String t) {
        if (t == null || t.isEmpty()) {
            return true;
        }
//            return t.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
        return EmailValidator.getInstance().isValid(t);
    }

}
