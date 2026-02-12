package org.softcaster.commons.types;

import org.softcaster.commons.utils.Converter;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

final class Weekday {

    public static final int Monday = 1;
    public static final int Tuesday = 2;
    public static final int Wednesday = 3;
    public static final int Thursday = 4;
    public static final int Friday = 5;
    public static final int Saturday = 6;
    public static final int Sunday = 7;
}

public class Date implements Serializable, Cloneable, Comparable {

    /**
     *
     */
    public Date() {
        super();
        LocalDate currentDate = LocalDate.now();
        
        year_ = currentDate.getYear();
        month_ = currentDate.getMonthValue();
        day_ = currentDate.getDayOfMonth();
        
        calcJulianDaysNumber();
    }

    public Date(Date dt) {
        super();
        year_ = dt.year_;
        month_ = dt.month_;
        day_ = dt.day_;

        calcJulianDaysNumber();
    }

    public Date(java.sql.Date sqlDate) {
        super();
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTimeInMillis(sqlDate.getTime());
        year_ = gc.get(GregorianCalendar.YEAR);
        month_ = gc.get(GregorianCalendar.MONTH) + 1;
        day_ = gc.get(GregorianCalendar.DATE);
        calcJulianDaysNumber();
    }

    public Date(String str) {
        super();
        StringTokenizer st = new StringTokenizer(str, "/");

        // Formato dd/mm/yy
        String sDay = st.nextToken();
        String sMonth = st.nextToken();
        String sYear = st.nextToken();

        Integer i1 = Integer.valueOf(sDay);
        day_ = i1;

        Integer i2 = Integer.valueOf(sMonth);
        month_ = i2;

        Integer i3 = Integer.valueOf(sYear);
        year_ = i3;
        // data in formato breve 10/01/07
        if (year_ < 100) {
            if (year_ >= 60) {
                year_ += 1900;
            } else {
                year_ += 2000;
            }
            calcJulianDaysNumber();
        } else if (year_ > 1900) {
            calcJulianDaysNumber();
        } else {
            day_ = -1;
            month_ = -1;
            year_ = -1;
            julianDaysNumber_ = -1;
        }
    }

    @Override
    public int hashCode() {
        int result = 27;
        result = 37 * result + Long.valueOf(julianDaysNumber_).hashCode();

        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (!(other instanceof Date)) {
            return false;
        }
        Date that = (Date) other;

        return this.julianDaysNumber_ == that.julianDaysNumber_;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Object o) {
        Date that = (Date) o;
        if (julianDaysNumber_ < that.julianDaysNumber_) {
            return -1;
        } else if (julianDaysNumber_ == that.julianDaysNumber_) {
            return 0;
        } else {
            return 1;
        }
    }

    /**
     *
     * @return @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();
        return new Date(this.getYear(), this.getMonth(), this.getDay());
    }

    public Date(int year, int month, int day) {
        year_ = year;
        month_ = month;
        day_ = day;

        calcJulianDaysNumber();
    }

    public int getYear() {
        return year_;
    }

    public int getMonth() {
        return month_;
    }

    public int getDay() {
        return day_;
    }

    public boolean isLessThan(Date rValue) {
        return julianDaysNumber_ < rValue.julianDaysNumber_;
    }

    public boolean isLessOrEqualThan(Date rValue) {
        return julianDaysNumber_ <= rValue.julianDaysNumber_;
    }

    public boolean isGreaterThan(Date rValue) {
        return julianDaysNumber_ > rValue.julianDaysNumber_;
    }

    public int dayOfWeek() {
        return (int) (julianDaysNumber_ % 7) + 1;
    }

    public boolean isWeekEnd() {
        int day_of_week = dayOfWeek();
        return (day_of_week == Weekday.Saturday || day_of_week == Weekday.Sunday);
    }

    public boolean isValid() {
        return julianDaysNumber_ > INVALID;
    }

    public void addDays(int days) {
        julianDaysNumber_ += days;
        calcGregorianDate();
    }

    public int months(Date rValue) {
        int monthsDiff = getMonth() - rValue.getMonth();
        int yearsDiff = getYear() - rValue.getYear();
        return yearsDiff * 12 + monthsDiff;
    }

    public long days(Date rValue) {
        return julianDaysNumber_ - rValue.julianDaysNumber_;
    }

    public void addYears(int years) {
        year_ += years;
        calcJulianDaysNumber();
    }

    public void addMonths(int months) {

        int lastMonth = month_;

        if (months > 0) {
            // aggiungo ...
            month_ = month_ + months % 12;
            year_ = year_ + months / 12;
            if (month_ > 12) {
                month_ -= 12;
                year_++;
            }

        } else {
            // tolgo ...
            month_ = month_ - (-months % 12);
            year_ = year_ + months / 12;
            if (month_ < 1) {
                month_ += 12;
                year_--;
            }
        }

        // Check giorno partenza - arrivo
        if ((monthDays_[lastMonth - 1] == day_) || (day_ > monthDays_[month_ - 1])) {
            day_ = monthDays_[month_ - 1];
        }

        calcJulianDaysNumber();

    }

    @Override
    public String toString() {
        String sDay = Converter.fromInt(day_, "00");
        String sMonth = Converter.fromInt(month_, "00");
        String dateStr = sDay + "/" + sMonth + "/" + getYear();
        return dateStr;
    }

    private void calcJulianDaysNumber() {
        if (year_ == 1 && month_ == 1 && day_ == 1) {
            julianDaysNumber_ = INVALID;
        } else {
            julianDaysNumber_
                    = (1461 * (year_ + 4800 + (month_ - 14) / 12)) / 4 + (367 * (month_ - 2 - 12 * ((month_ - 14) / 12))) / 12 - (3 * ((year_ + 4900 + (month_ - 14) / 12) / 100)) / 4 + day_ - 32075;
        }

    }

    private void calcGregorianDate() {
        long l, n, i, j, d, m, y;
        l = julianDaysNumber_ + 68569;
        n = (4 * l) / 146097;
        l = l - (146097 * n + 3) / 4;
        i = (4000 * (l + 1)) / 1461001;
        l = l - (1461 * i) / 4 + 31;
        j = (80 * l) / 2447;
        d = l - (2447 * j) / 80;
        l = j / 11;
        m = j + 2 - (12 * l);
        y = 100 * (n - 49) + i + l;

        day_ = (int) d;
        month_ = (int) m;
        year_ = (int) y;
    }

    public boolean isLastDayInMonth() {
        return day_ == monthDays_[month_ - 1];
    }

    public static Date today() {
        java.util.Locale locale = java.util.Locale.FRENCH;
        java.util.Date now = new java.util.Date();
        java.text.DateFormat df = java.text.DateFormat.getDateInstance(java.text.DateFormat.SHORT, locale);
        return new Date(df.format(now));
    }

    public void endOfMonth() {
        day_ = monthDays_[month_ - 1];
        calcJulianDaysNumber();
    }
    private static long INVALID = -1;
    // Rappresentazione in formato data gregoriana
    private int year_;
    private int month_;
    private int day_;
    //Rappresentazione della data in formato long
    private long julianDaysNumber_ = INVALID;
    // Numero di giorni dei vari mesi
    static int monthDays_[] = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    public boolean isGreaterOrEqualThan(Date rValue) {
        return julianDaysNumber_ >= rValue.julianDaysNumber_;
    }

    public java.sql.Date sqlDate() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(year_, month_ - 1, day_);
        return new java.sql.Date(gc.getTimeInMillis());
    }

    public java.util.Date utilDate() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(year_, month_ -1, day_);
        return new java.util.Date(gc.getTimeInMillis());
    }

    public java.sql.Timestamp sqlTimeStamp() {
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(year_, month_ - 1, day_);
        return new java.sql.Timestamp(gc.getTimeInMillis());
    }

    public boolean isLeapYear() {
        boolean year_mod_4 = (year_ % 4 == 0);
        boolean year_mod_100 = (year_ % 100 == 0);
        boolean year_mod_400 = (year_ % 400 == 0);
        return ((year_mod_4 && !year_mod_100) || year_mod_400);
    }

    public static void main(String[] args) {
        DateParser parser = new DateParser("291124");
        System.out.println(parser.day());
        System.out.println(parser.month());
        System.out.println(parser.year());
        Date dt = new Date("29/11/24");
        System.out.println(dt);

        dt.endOfMonth();
        System.out.println(dt);

        System.out.println(dt.isWeekEnd());

        System.out.println(dt.sqlDate());

        /*
        java.sql.Date sqlDate = dt.sqlDate();
        dt = new Date(sqlDate);
        System.out.println(dt);

        dt.addDays(30);
        System.out.println(dt);
         */
    }
}
