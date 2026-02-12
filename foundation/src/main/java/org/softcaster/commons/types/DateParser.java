package org.softcaster.commons.types;

interface STATUS {

    int S_INVALID = -1, S1 = 1, S2 = 2, S3 = 3, STOP = 4;
}

public class DateParser {

    /**
     *
     */
    public DateParser() {
        super();
    }

    //
    // ----------------------------------------------------
    //
    public DateParser(String in) {
        super();
        // Removes white space from both ends of this string
        input = in.trim();

        // Parsing della stringa
        if (parse() == STATUS.S_INVALID) {
            System.out.println("Invalid date format!");
        }

    }

    public int day() {
        return Integer.parseInt(output_d);
    }

    public int month() {
        return Integer.parseInt(output_m);
    }

    public int year() {
        int tmp = Integer.parseInt(output_y);
        if (tmp < 30) {
            return 2000 + tmp;
        }
        if (tmp > 30 && tmp < 99) {
            return 1900 + tmp;
        }
        if (tmp > 1900) {
            return tmp;
        }
        return -1;
    }

    //
    // ----------------------------------------------------
    //
    private static char _token_dlm[] = {'-', '/', ';', ':'};

    private static int _TOKEN_DLM = 4;

    private int current = STATUS.S1;

    private int counter = 0;

    private boolean adjust = false;

    private int pos = 0;

    private String output_d = new String();

    private String output_m = new String();

    private String output_y = new String();

    private String input;

    private int get_next_status() {
        switch (current) {
            case STATUS.S1 -> {
                if (_isdigit()) {
                    output_d = output_d + input.charAt(pos);
                    pos++;
                    counter++;
                    current = STATUS.S1;
                } // incontrato separatore
                else if (_istoken()) {
                    pos++;
                    counter = 0;
                    current = STATUS.S2;
                } // data in formato 010201 non c'e` separatore e non incremento
                // la posizione
                else if (counter == 2) {
                    adjust = true;
                    counter = 0;
                    current = STATUS.S2;
                } else {
                    current = STATUS.S_INVALID;
                }
            }
            case STATUS.S2 -> {
                if (_isdigit()) {
                    if (adjust) {
                        output_m = output_m + input.charAt(pos);
                    } else {
                        output_m = output_m + input.charAt(pos);
                    }
                    pos++;
                    counter++;
                    current = STATUS.S2;
                } else if (_istoken()) {
                    pos++;
                    counter = 0;
                    current = STATUS.S3;
                } // data in formato 010201 non c'e` separatore e non incremento
                // la posizione
                else if (counter == 2) {
                    counter = 0;
                    current = STATUS.S3;
                } else {
                    current = STATUS.S_INVALID;
                }
            }
            case STATUS.S3 -> {
                if (Character.isDigit(input.charAt(pos))) {
                    output_y = input.substring(pos, input.length());
                    // strncpy(output_y, input + pos, 5);
                    current = STATUS.STOP;
                } else {
                    current = STATUS.S_INVALID;
                }
            }
        }
        //
        // giorno
        //
        //
        // mese
        //
        //
        // anno
        //

        return current;
    }

    private boolean token(char c) {
        char d;
        for (int i = 0; i < _TOKEN_DLM; i++) {
            d = _token_dlm[i];
            if (c == d) {
                return true;
            }
        }

        return false;
    }

    private boolean _istoken() {
        if (token(input.charAt(pos))) {
            if (counter > 0 && counter <= 2) {
                return true;
            }
        }
        return false;
    }

    private boolean _isdigit() {
        return (Character.isDigit(input.charAt(pos)) && counter < 2);
    }

    private int parse() {
        while (true) {
            switch (get_next_status()) {
                case STATUS.S_INVALID -> {
                    return STATUS.S_INVALID;
                }
                case STATUS.STOP -> {
                    return STATUS.STOP;
                }
            }
        }

    }

    public static void main(String[] args) {
    }
}
