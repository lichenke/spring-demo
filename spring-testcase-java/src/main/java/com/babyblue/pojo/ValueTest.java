package com.babyblue.pojo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
public class ValueTest {

    @Value("lichenke")
    private String userName;

    @Value("#{systemProperties['os.name']}")
    private String systemPropertiesName;

    @Value(("#{T(java.lang.Math).random() * 100}"))
    private double randomNumber;

    @Value("test.txt")
    private Resource resourceFile;

    @Value("${driverClassName}")
    private String jdbcUrl;

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSystemPropertiesName() {
        return systemPropertiesName;
    }

    public void setSystemPropertiesName(String systemPropertiesName) {
        this.systemPropertiesName = systemPropertiesName;
    }

    public double getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(double randomNumber) {
        this.randomNumber = randomNumber;
    }

    public Resource getResourceFile() {
        return resourceFile;
    }

    public void setResourceFile(Resource resourceFile) {
        this.resourceFile = resourceFile;
    }

    @Override
    public String toString() {
        return "ValueTest{" +
                "userName='" + userName + '\'' +
                ", systemPropertiesName='" + systemPropertiesName + '\'' +
                ", randomNumber=" + randomNumber +
                ", resourceFile=" + resourceFile +
                '}';
    }
}
