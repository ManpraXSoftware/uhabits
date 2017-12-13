package org.mhabitx.uhabits.core.commands;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mhabitx.uhabits.core.models.HabitLog;
import org.mhabitx.uhabits.core.models.Repetition;
import org.mhabitx.uhabits.core.models.RepetitionList;
import org.mhabitx.uhabits.core.models.Timestamp;

/**
 * Created by Prateek on 11-12-2017.
 */
/**
 * Command to create a habit log.
 */
public class CreateLogCommand extends Command {
    @NonNull
    final Repetition repetition;
    final Timestamp timestamp;
    final int value;
    @Nullable
    HabitLog previousLog;
    @Nullable
    HabitLog newLog;

    public CreateLogCommand(Repetition repetition, Timestamp timestamp, int value) {
        this.repetition = repetition;
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public void execute() {
        //create new log
        newLog=new HabitLog(timestamp,value);
        //add into list
        repetition.getHabitLogs().add(newLog);
    }

    @NonNull
    @Override
    public Object toRecord() {
        return new Record(this);
    }

    @Override
    public void undo() {
        if(newLog == null) throw new IllegalStateException();
        repetition.getHabitLogs().remove(newLog);
        if (previousLog != null) repetition.getHabitLogs().add(previousLog);
       // repetition.invalidateNewerThan(timestamp);
    }
    public static class Record
    {
        @NonNull
        public String id;

        @NonNull
        public String event = "CreateLogCommand";

        public long repetitionId;

        public long logTimestamp;

        public int value;

        public Record(CreateLogCommand command)
        {
            id = command.getId();
            Long repId = command.repetition.getId();
            if(repId == null) throw new RuntimeException("Repetition not saved");
            this.repetitionId = repId;
            this.logTimestamp = command.timestamp.getUnixTime();
            this.value = command.value;
        }

        public CreateLogCommand toCommand(@NonNull RepetitionList repetitionList)
        {
            Repetition r = repetitionList.getById(repetitionId);
            if(r == null) throw new RuntimeException("Repetition not found");

            CreateLogCommand command;
            command = new CreateLogCommand(
                    r, new Timestamp(logTimestamp), value);
            command.setId(id);
            return command;
        }
    }
}
