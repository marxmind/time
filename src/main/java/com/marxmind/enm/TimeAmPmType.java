package com.marxmind.enm;

public enum TimeAmPmType
{
    AM_IN("AM_IN", 0, 0, "AM IN"), 
    AM_OUT("AM_OUT", 1, 1, "AM OUT"), 
    PM_IN("PM_IN", 2, 2, "PM IN"), 
    PM_OUT("PM_OUT", 3, 3, "PM OUT"), 
    OT_IN("OT_IN", 4, 4, "OT START"), 
    OT_OUT("OT_OUT", 5, 5, "OT END");
    
    private int id;
    private String name;
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    private TimeAmPmType(final String name2, final int ordinal, final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public static String nameId(final int id) {
        TimeAmPmType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final TimeAmPmType type = values[i];
            if (id == type.getId()) {
                return type.getName();
            }
        }
        return TimeAmPmType.AM_IN.getName();
    }
    
    public static int idName(final String name) {
        TimeAmPmType[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final TimeAmPmType type = values[i];
            if (name.equalsIgnoreCase(type.getName())) {
                return type.getId();
            }
        }
        return TimeAmPmType.AM_IN.getId();
    }
}
