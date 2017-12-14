package org.mhabitx.uhabits.core.commands;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mhabitx.uhabits.core.models.Checkmark;
import org.mhabitx.uhabits.core.models.Habit;
import org.mhabitx.uhabits.core.models.HabitList;
import org.mhabitx.uhabits.core.models.HabitLog;
import org.mhabitx.uhabits.core.models.Repetition;
import org.mhabitx.uhabits.core.models.RepetitionList;
import org.mhabitx.uhabits.core.models.Timestamp;
import org.mhabitx.uhabits.core.utils.DateUtils;

/**
 * Created by Prateek on 11-12-2017.
 */
/**
 * Command to create a habit log.
 */
public class CreateLogCommand extends Command {

     Repetition repetition;
    @NonNull
    final Habit habit;
    final Timestamp timestamp;
    int value;
    boolean isAdd=false;
    @Nullable
    HabitLog previousLog;
    @Nullable
    HabitLog newLog;

    public CreateLogCommand(Habit habit, Timestamp timestamp,boolean isAdd) {
        this.habit =habit ;
        this.timestamp = timestamp;
        this.isAdd=isAdd;
    }

    @Override
    public void execute() {
        RepetitionList repetitionList=habit.getRepetitions();
        repetition=repetitionList.getByTimestamp(timestamp);
        if (isAdd){// tap   --> add Habit log corresponding to repetition
            if (repetition==null){//its first tap ->first create repetition and then
                repetition = new Repetition(timestamp, Checkmark.UNCHECKED);
                repetitionList.add(repetition);
            }
            newLog=new HabitLog(DateUtils.getCorrectLogTime(timestamp),Checkmark.CHECKED_EXPLICITLY);
            repetition.getHabitLogs().add(newLog);
            previousLog=newLog;
            repetition.setValue(repetition.getHabitLogs().size());
        }else{//Long tap   --> remove last Habit Log from corresponding to repetition
            if (repetition!=null&&repetition.getHabitLogs()!=null) {
                previousLog=repetition.getHabitLogs().getLast();
                repetition.getHabitLogs().remove(previousLog);
                repetition.setValue(repetition.getHabitLogs().size());
                if (repetition.getHabitLogs().size()==0){//if its last log entry then also remove Repetition
                    repetitionList.remove(repetition);
                }
            }
        }
        habit.invalidateNewerThan(timestamp);
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
        habit.invalidateNewerThan(timestamp);
    }
    public static class Record
    {
        @NonNull
        public String id;

        @NonNull
        public String event = "CreateLogCommand";

        public long repetitionId;

        public long logTimestamp;
        public Habit habit;

        public int value;

        public Record(CreateLogCommand command)
        {
            id = command.getId();
            Long repId = command.repetition.getId();
            if(repId == null) throw new RuntimeException("Repetition not saved");
            this.repetitionId = repId;
            this.habit=command.habit;
            this.logTimestamp = command.timestamp.getUnixTime();
            this.value = command.value;
        }

        public CreateLogCommand toCommand(@NonNull HabitList hList)
        {
            Repetition r = habit.getRepetitions().getById(repetitionId);
            if(r == null) throw new RuntimeException("Repetition not found");

            CreateLogCommand command;
            command = new CreateLogCommand(
                    habit, new Timestamp(logTimestamp),true);//
            command.setId(id);
            return command;
        }
    }
}
