package com.marxmind.utils;

import org.dom4j.Node;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import java.io.File;

public class ReadConfig
{
    private final String sep;
    private final String PATH;
    private final String FILE_NAME;
    private static final String APPLICATION_FILE;
    
    static {
        APPLICATION_FILE = String.valueOf(AppConf.PRIMARY_DRIVE.getValue()) + AppConf.SEPERATOR.getValue() + AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.APP_CONFIG_SETTING_FOLDER.getValue() + AppConf.SEPERATOR.getValue() + AppConf.APP_CONFIG_SETTING_FILE_NAME.getValue();
    }
    
    public ReadConfig() {
        this.sep = File.separator;
        this.PATH = String.valueOf(AppConf.PRIMARY_DRIVE.getValue()) + AppConf.SEPERATOR.getValue() + AppConf.APP_CONFIG_FOLDER_NAME.getValue() + AppConf.SEPERATOR.getValue() + AppConf.APP_CONFIG_SETTING_FOLDER.getValue() + AppConf.SEPERATOR.getValue();
        this.FILE_NAME = AppConf.APP_CONFIG_SETTING_FILE_NAME.getValue();
    }
    
    public static String value(final AppConf conf) {
        final File xmlFile = new File(ReadConfig.APPLICATION_FILE);
        if (xmlFile.exists()) {
            try {
                final SAXReader reader = new SAXReader();
                final Document document = reader.read(xmlFile);
                final Node node = document.selectSingleNode("/application/" + conf.getValue());
                return node.getText();
            }
            catch (DocumentException ex) {}
        }
        return null;
    }
}