package com.marxmind.enm;

public enum PassSlipStatus
{
    REQUEST("REQUEST", 0, 0, "REQUEST"), 
    APPROVED("APPROVED", 1, 1, "APPROVED"), 
    DENIED("DENIED", 2, 3, "DENIED");
    
    private int id;
    private String name;
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    private PassSlipStatus(final String name2, final int ordinal, final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public static PassSlipStatus getValue(final int id) {
        PassSlipStatus[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final PassSlipStatus m = values[i];
            if (id == m.getId()) {
                return m;
            }
        }
        return PassSlipStatus.REQUEST;
    }
    
    public static String getSlipStatus(final int id) {
        PassSlipStatus[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final PassSlipStatus m = values[i];
            if (id == m.getId()) {
                return m.getName();
            }
        }
        return PassSlipStatus.REQUEST.name;
    }
    
    public static int getSlipStatusId(final String name) {
        PassSlipStatus[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final PassSlipStatus m = values[i];
            if (name.equalsIgnoreCase(m.getName())) {
                return m.getId();
            }
        }
        return PassSlipStatus.REQUEST.getId();
    }
}