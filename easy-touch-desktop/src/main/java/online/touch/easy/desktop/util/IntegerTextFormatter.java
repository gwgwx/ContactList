/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.util;

import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;

/**
 *
 * @author George
 */
public class IntegerTextFormatter extends TextFormatter {

    public IntegerTextFormatter() {
        super(new UnaryOperator<TextFormatter.Change>() {
            @Override
            public TextFormatter.Change apply(TextFormatter.Change c) {
                if (c.getControlNewText().isEmpty()) {
                    return c;
                }
                Pattern isInteger = Pattern.compile("\\d+");

                if (!isInteger.matcher(c.getControlNewText()).matches()) {
                    return null;
                } else {
                    return c;
                }
            }
        });
    }

}
