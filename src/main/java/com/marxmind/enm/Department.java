package com.marxmind.enm;

public enum Department
{
    MAYOR("MAYOR", 0, 1, "MAYOR"), 
    VICE_MAYOR("VICE_MAYOR", 1, 2, "VICE_MAYOR"), 
    SB("SB", 2, 3, "SB"), 
    SK("SK", 3, 4, "SK"), 
    TREASURER("TREASURER", 4, 5, "TREASURER"), 
    BUDGET("BUDGET", 5, 6, "BUDGET"), 
    ACCOUNTING("ACCOUNTING", 6, 7, "ACCOUNTING"), 
    ADMIN("ADMIN", 7, 8, "ADMIN"), 
    PERSONNEL("PERSONNEL", 8, 9, "PERSONNEL"), 
    MSWD("MSWD", 9, 10, "MSWD"), 
    MAO("MAO", 10, 11, "MAO"), 
    ASSESSOR("ASSESSOR", 11, 12, "ASSESSOR"), 
    CIVIL_REGISTRAR("CIVIL_REGISTRAR", 12, 13, "CIVIL_REGISTRAR"), 
    LICENSING("LICENSING", 13, 14, "LICENSING"), 
    MPDC("MPDC", 14, 15, "MPDC"), 
    ENGINEERING("ENGINEERING", 15, 16, "ENGINEERING"), 
    GSO("GSO", 16, 17, "GSO"), 
    MDREAMS("MDREAMS", 17, 18, "MDREAMS"), 
    TOURISM("TOURISM", 18, 19, "TOURISM"), 
    TOURISM_LODGE("TOURISM_LODGE", 19, 20, "TOURISM_LODGE"), 
    MOTORPOOL("MOTORPOOL", 20, 21, "MOTORPOOL"), 
    HOSPITAL("HOSPITAL", 21, 22, "HOSPITAL"), 
    HEALTH("HEALTH", 22, 23, "HEALTH"), 
    MENRO("MENRO", 23, 24, "MENRO"), 
    SENIOR("SENIOR", 24, 25, "SENIOR"), 
    IPMR("IPMR", 25, 26, "IPMR"), 
    IPS("IPS", 26, 27, "IPS"), 
    SECUIRTY("SECUIRTY", 27, 28, "SECUIRTY"), 
    DILG("DILG", 28, 29, "DILG");
    
    private int id;
    private String name;
    
    public int getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
    private Department(final String name2, final int ordinal, final int id, final String name) {
        this.id = id;
        this.name = name;
    }
    
    public static Department getValue(final int id) {
        Department[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Department m = values[i];
            if (id == m.getId()) {
                return m;
            }
        }
        return Department.MAYOR;
    }
    
    public static String getDepartment(final int id) {
        Department[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Department m = values[i];
            if (id == m.getId()) {
                return m.getName();
            }
        }
        return Department.MAYOR.name;
    }
    
    public static int getDepartmentId(final String name) {
        Department[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Department m = values[i];
            if (name.equalsIgnoreCase(m.getName())) {
                return m.getId();
            }
        }
        return Department.MAYOR.getId();
    }
}