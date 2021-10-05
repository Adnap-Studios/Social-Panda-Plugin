package com.adnapstudios.socialpanda;

import java.sql.Timestamp;
import java.util.Date;

public class SocialPlayer {
    private String uuid;
    private String name;
    private Timestamp lastOnline;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public void setLastOnline(Timestamp lastOnline) {
        this.lastOnline = lastOnline;
    }
}
