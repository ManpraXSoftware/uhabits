package org.mhabitx.uhabits.core.models.sqlite.records;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.Contract;
import org.mhabitx.uhabits.core.database.Repository;
import org.mhabitx.uhabits.core.models.HabitLog;
import org.mhabitx.uhabits.core.models.HabitLogList;
import org.mhabitx.uhabits.core.models.ModelFactory;
import org.mhabitx.uhabits.core.models.Repetition;
import org.mhabitx.uhabits.core.models.Timestamp;
import org.mhabitx.uhabits.core.models.memory.MemoryHabitLogList;

import java.util.List;

/**
 * Created by Prateek on 08-12-2017.
 */

public class SQLiteHabitLogList extends HabitLogList {
    @NonNull
    private final Repository<LogRecord> repository;

    @NonNull
    private final ModelFactory modelFactory;

    @NonNull
    private final MemoryHabitLogList list;

    private boolean loaded = false;
    public SQLiteHabitLogList(@NonNull Repetition repetition,ModelFactory modelFactory) {
        super(repetition);
        this.modelFactory=modelFactory;
        this.list= new MemoryHabitLogList(repetition);
        this.repository= modelFactory.buildLogListRepository();
    }
    private void loadRecords()
    {
        if (loaded) return;
        loaded = true;

        check(repetition.getId());
        List<LogRecord> records =
                repository.findAll("where repetition_id = ? order by timestamp",
                        Long.toString(repetition.getId()));

        for (LogRecord rec : records)
            list.add(rec.toHabitLog());
    }
    @Override
    public void add(HabitLog log) {
        loadRecords();
        list.add(log);
        check(repetition.getId());
        LogRecord record= new LogRecord();
        record.repetitionId=repetition.getId();
        record.copyFrom(log);
        repository.save(record);
        observable.notifyListeners();
    }

    @Override
    public List<HabitLog> getByInterval(Timestamp fromTimestamp, Timestamp toTimestamp) {
        loadRecords();
        return list.getByInterval(fromTimestamp, toTimestamp);
    }

    @Nullable
    @Override
    public HabitLog getByTimestamp(Timestamp timestamp) {
        loadRecords();
        return list.getByTimestamp(timestamp);
    }

    @Nullable
    @Override
    public HabitLog getOldest() {
        loadRecords();
        return list.getOldest();
    }

    @Nullable
    @Override
    public HabitLog getNewest() {
        loadRecords();
        return list.getNewest();
    }

    @Override
    public void remove(@NonNull HabitLog habitLog) {
        loadRecords();
        list.remove(habitLog);
        check(repetition.getId());
        repository.execSQL(
                "delete from Logs where repetition_id = ? and timestamp = ?",
                repetition.getId(), repetition.getTimestamp().getUnixTime());
        observable.notifyListeners();
    }

    @NonNull
    @Override
    public long getTotalCount() {
        loadRecords();
        return list.getTotalCount();
    }

    @Override
    public void removeAll() {
        loadRecords();
        list.removeAll();
        check(repetition.getId());
        repository.execSQL("delete from Logs where repetition_id = ?",
                repetition.getId());
    }
    @Contract("null -> fail")
    private void check(Long value)
    {
        if (value == null) throw new RuntimeException("null check failed");
    }
}
