/* 
 * Copyright (c) 2016, Cover Solutions Ltd and/or its affiliates. All rights reserved.
 */
package online.touch.easy.desktop.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import online.touch.easy.common.model.BaseEntity;
import online.touch.easy.common.model.Identifiable;
import org.controlsfx.tools.ValueExtractor;
import org.controlsfx.validation.ValidationSupport;

/**
 * A simple new/edit form for helper entities. Form should be application modal,
 * intention is not to spend time on this screen but merely intended for quick
 * edits.
 *
 * @author George-2014
 * @param <T>
 */
public abstract class SimpleEditableController<T extends Identifiable> {

    // load the requested record.
    public abstract void load(Long id);

    // get the form title.
    public abstract StringProperty titleProperty();

    protected ValidationSupport validationSupport;

    protected final Class<T> entityClass;

    public SimpleEditableController(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.validationSupport = new ValidationSupport();
//        ValueExtractor.addObservableValueExtractor(c -> c instanceof LocalDateTimeTextField, c -> ((LocalDateTimeTextField) c).localDateTimeProperty());
    }

    public static String fmtFloat(Float fl) {
        if (fl == null) {
            return null;
        }
        float f = fl;
        if (f == (long) f) {
            return String.format("%d", (long) f);
        } else {
            return String.format("%.2f", f);
        }
    }

    public static String fmtBigDecimal(BigDecimal bd) {
        if (bd == null) {
            return null;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        df.setGroupingUsed(false);

        return df.format(bd);
    }

    public static String fmtInteger(Integer fl) {
        if (fl == null) {
            return null;
        }
        float f = fl;
        if (f == (long) f) {
            return String.format("%d", (long) f);
        } else {
            return String.format("%s", f);
        }
    }

    public static String fmtLong(Long fl) {
        if (fl == null) {
            return null;
        }
        float f = fl;
        if (f == (long) f) {
            return String.format("%d", (long) f);
        } else {
            return String.format("%s", f);
        }
    }

    public static String fmtClockFromLong(Long fl) {
        if (fl == null) {
            return null;
        }
        long hours = fl / 3600;
        long minutes = (fl % 3600) / 60;

        return String.format("%02d:%02d", hours, minutes);
    }

//    public static String formatPrintValue(Object o) {
//        if (o == null) {
//            return "";
//        } else if (o instanceof String) {
//            return (String) o;
//        } else if (o instanceof Date) {
//            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy hh:MM");
//            return format.format((Date) o);
//        } else if (o instanceof Float) {
//            return fmtFloat((Float) o);
//        } else if (o instanceof BigDecimal) {
//            return fmtBigDecimal((BigDecimal) o);
//        } else if (o instanceof Account) {
//            return ((Account) o).getName();
//        } else if (o instanceof SupportingTable) {
//            return ((SupportingTable) o).getName();
//        } else if (o instanceof Port) {
//            return ((Port) o).getName();
//        } else if (o instanceof Document) {
//            return ((Document) o).getDocumentDisplayNumber();
//        } else if (o instanceof Collection) {
//            String temp = "";
//            for (Object item : (Collection) o) {
//                if (item instanceof PortOperationType) {
//                    temp += " " + ((PortOperationType) item).getName();
//                } else {
//                    temp += " " + item;
//                }
//            }
//            return temp;
//        }
//        return o.toString();
//    }

    protected static String splitCamelCase(String s) {
        if (s.startsWith("is")) {
            return s.substring(2).replaceAll(
                    String.format("%s|%s|%s",
                            "(?<=[A-Z])(?=[A-Z][a-z])",
                            "(?<=[^A-Z])(?=[A-Z])",
                            "(?<=[A-Za-z])(?=[^A-Za-z])"
                    ),
                    " "
            );
        }
        return s.substring(3).replaceAll(
                String.format("%s|%s|%s",
                        "(?<=[A-Z])(?=[A-Z][a-z])",
                        "(?<=[^A-Z])(?=[A-Z])",
                        "(?<=[A-Za-z])(?=[^A-Za-z])"
                ),
                " "
        );
    }

    protected LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return res;
    }

    protected String formatDateTime(long date) {
        final SimpleDateFormat format = new SimpleDateFormat();
//        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        format.applyPattern("dd/MM/yyyy HH:mm");
        return format.format(new Date(date));
    }

    protected LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = Instant.ofEpochMilli(date.getTime());
        LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        return res.toLocalDate();
    }

    protected Date toDate(LocalDateTime localDate) {
        if (localDate == null) {
            return null;
        }
        Instant instant = localDate.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    protected static String nonNull(String s) {
        return s == null ? "" : s;
    }

    protected static String nonNull(Integer i) {
        return i == null ? "" : i.toString();
    }

    protected boolean isVoid(Object o) {
        if (o instanceof String) {
            return isEmpty((String) o);
        } else {
            return o == null;
        }
    }

    protected boolean isEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    protected boolean equal(Object o1, Object o2) {
        if (isVoid(o1)) {
            return isVoid(o2);
        }
        // use big decimal compare method
        if (o1 instanceof BigDecimal && o2 instanceof BigDecimal) {
            return ((BigDecimal) o1).compareTo(((BigDecimal) o2)) == 0;
        }
        return o1.equals(o2);
    }

    protected static enum SaveState {

        INVALID, UNSAVED, UNCHANGED

    }

    protected String stringValue(TextField textFieldl) {
        if (textFieldl == null || isEmpty(textFieldl.getText())) {
            return null;
        }
        return textFieldl.getText();
    }

    protected String stringValueAndTrim(TextField textFieldl) {
        if (textFieldl == null || isEmpty(textFieldl.getText())) {
            return null;
        }
        return textFieldl.getText().trim();
    }

    protected String stringValue(TextArea textArea) {
        if (textArea == null || isEmpty(textArea.getText())) {
            return null;
        }
        return textArea.getText();
    }

    protected Float floatValue(TextField textField) {
        if (textField == null || isEmpty(textField.getText())) {
            return null;
        }
        return new Float(textField.getText());
    }

    protected BigDecimal decimalValue(TextField textField) {
        if (textField == null || isEmpty(textField.getText())) {
            return null;
        }
        return new BigDecimal(textField.getText().replace(",", "."));
    }

    protected BigDecimal decimalValue(Spinner<Double> spinner) {
        if (spinner == null) {
            return null;
        }
        return new BigDecimal(spinner.getValue());
    }

    protected Integer integerValue(TextField textField) {
        if (textField == null || isEmpty(textField.getText())) {
            return null;
        }
        return new Integer(textField.getText());
    }

    protected Long longValue(TextField textField) {
        if (textField == null || isEmpty(textField.getText())) {
            return null;
        }
        return new Long(textField.getText());
    }

    protected Long longValueFromClock(TextField textField) {
        if (textField == null || isEmpty(textField.getText())) {
            return null;
        }
        String time = textField.getText(); //mm:ss
        String[] units = time.split(":"); //will break the string up into an array
        if (units.length < 2) {
            return null;
        }
        long hours = Long.parseLong(units[0]); //first element
        long minutes = Long.parseLong(units[1]); //second element
        long duration = hours * 3600 + minutes * 60; //add up our values
        return duration;
    }

//    protected Date dateValue(LocalDateTimeTextField dateTimeTextField) {
//        if (dateTimeTextField == null || dateTimeTextField.getLocalDateTime() == null) {
//            return null;
//        }
//        Instant instant = dateTimeTextField.getLocalDateTime().atZone(ZoneId.systemDefault()).toInstant();
//        return Date.from(instant);
//    }

    protected Date datePickerValue(DatePicker datePicker) {
        if (datePicker == null || datePicker.getValue() == null) {
            return null;
        }
        Instant instant = datePicker.getValue().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    protected BaseEntity entityValue(ComboBox<?> box) {
        if (box == null) {
            return null;
        }
        return (BaseEntity) box.getValue();
    }

    protected Boolean booleanValue(ChoiceBox<Boolean> bool) {
        if (bool == null || bool.getValue() == null) {
            return null;
        }
        return bool.getValue();
    }

    protected String formatDate(long date) {
        final SimpleDateFormat format = new SimpleDateFormat();
        format.applyPattern("dd/MM/yyyy");
        return format.format(new Date(date));
    }

}
