package com.godlontonconsulting.entranze.utils;


    import com.google.gson.internal.bind.util.ISO8601Utils;

    import java.text.DateFormat;
    import java.text.DecimalFormat;
    import java.text.FieldPosition;
    import java.text.NumberFormat;
    import java.text.ParseException;
    import java.text.ParsePosition;
    import java.util.Calendar;
    import java.util.Date;
    import java.util.GregorianCalendar;


public class ISO8601Date extends DateFormat
{
    private static final long serialVersionUID = 1L;

    // those classes are to try to allow a consistent behavior for hascode/equals and other methods
    private static Calendar CALENDAR = new GregorianCalendar();
    private static NumberFormat NUMBER_FORMAT = new DecimalFormat();

    public ISO8601Date() {
        this.numberFormat = NUMBER_FORMAT;
        this.calendar = CALENDAR;
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        String value = ISO8601Utils.format(date);
        toAppendTo.append(value);
        return toAppendTo;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {
        try {
            return ISO8601Utils.parse(source, pos);
        }
        catch (ParseException e) {
            return null;
        }
    }

    //supply our own parse(String) since pos isn't updated during parsing,
    //but the exception should have the right error offset.
    @Override
    public Date parse(String source) throws ParseException {
        return ISO8601Utils.parse(source, new ParsePosition(0));
    }

    @Override
    public Object clone() {
        /* Jackson calls clone for every call. Since this instance is
         * immutable (and hence thread-safe)
         * we can just return this instance
         */
        return this;
    }

    @Override
    public String toString() { return getClass().getName(); }
}