package org.mhabitx.uhabits.core.models;

import org.mhabitx.uhabits.core.database.Column;

/**
 * Created by Prateek on 07-12-2017.
 */

public class HabitLog {
    private Timestamp timestamp;
    private int value;

    public HabitLog(Timestamp timestamp, int value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
