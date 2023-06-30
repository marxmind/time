package com.marxmind.enm;

public enum TimeType
{
    REGULAR("REGULAR", 0, 0, "REGULAR"), 
    OFFICIAL_BUSINESS("OFFICIAL_BUSINESS", 1, 1, "OFFICIAL BUSINESS"), 
    FLEXIBLE("FLEXIBLE", 2, 2, "FLEXIBLE");
    
    private int id;
    private String name;
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    private TimeType(final String name2, final int ordinal, final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public static String nameId(final int id) {
        TimeType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final TimeType type = values[i];
            if (id == type.getId()) {
                return type.getName();
            }
        }
        return TimeType.REGULAR.getName();
    }
    
    public static int idName(final String name) {
        TimeType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final TimeType type = values[i];
            if (name.equalsIgnoreCase(type.getName())) {
                return type.getId();
            }
        }
        return TimeType.REGULAR.getId();
    }
}
