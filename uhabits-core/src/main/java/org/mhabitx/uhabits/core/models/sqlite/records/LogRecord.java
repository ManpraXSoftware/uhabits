package org.mhabitx.uhabits.core.models.sqlite.records;

/**
 * Created by Prateek on 07-12-2017.
 */

import org.mhabitx.uhabits.core.database.Column;
import org.mhabitx.uhabits.core.database.Table;
import org.mhabitx.uhabits.core.models.HabitLog;
import org.mhabitx.uhabits.core.models.Timestamp;

/**
 * The SQLite database record corresponding to a {@link org.mhabitx.uhabits.core.models.HabitLog}.
 */
@Table(name = "Logs")
public class LogRecord {
//    public RepetitionRecord repetitionRecord;

    @Column(name = "repetition_id")
    public Long repetitionId;

    @Column
    public Long timestamp;

    @Column
    public Integer value;

    @Column
    public Long id;

    public void copyFrom(HabitLog log)
    {
        timestamp = log.getTimestamp().getUnixTime();
        value = log.getValue();
    }

    public HabitLog toHabitLog()
    {
        return new HabitLog(new Timestamp(timestamp), value);
    }
}
