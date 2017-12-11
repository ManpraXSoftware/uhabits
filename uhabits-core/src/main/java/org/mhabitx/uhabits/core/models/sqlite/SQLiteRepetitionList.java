/*
 * Copyright (C) 2017 Alinson Santos Xavier <mhabitx@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */

package org.mhabitx.uhabits.core.models.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.jetbrains.annotations.Contract;
import org.mhabitx.uhabits.core.database.Repository;
import org.mhabitx.uhabits.core.models.Habit;
import org.mhabitx.uhabits.core.models.ModelFactory;
import org.mhabitx.uhabits.core.models.Repetition;
import org.mhabitx.uhabits.core.models.RepetitionList;
import org.mhabitx.uhabits.core.models.Timestamp;
import org.mhabitx.uhabits.core.models.memory.MemoryRepetitionList;
import org.mhabitx.uhabits.core.models.sqlite.records.RepetitionRecord;

import java.util.List;

/**
 * Implementation of a {@link RepetitionList} that is backed by SQLite.
 */
public class SQLiteRepetitionList extends RepetitionList {
    private final Repository<RepetitionRecord> repository;

    private final MemoryRepetitionList list;

    private boolean loaded = false;
    private ModelFactory modelFactory;

    public SQLiteRepetitionList(@NonNull Habit habit,
                                @NonNull ModelFactory modelFactory) {
        super(habit);
        this.modelFactory = modelFactory;
        repository = modelFactory.buildRepetitionListRepository();
        list = new MemoryRepetitionList(habit);
    }

    private void loadRecords() {
        if (loaded) return;
        loaded = true;

        check(habit.getId());
        List<RepetitionRecord> records =
                repository.findAll("where habit = ? order by timestamp",
                        habit.getId().toString());

        //Habit rep
        //init mx habit logs
        for (RepetitionRecord rec : records) {
            Repetition rep = rec.toRepetition();                       //modelFactory.buildLogList() create a collection for table item & memory item.
            rep.setHabitLogs(modelFactory.buildLogList(rec.getId()));  //repetition id  which corresponds to Habit Log.
            list.add(rep);
        }
    }

    @Override
    public void add(Repetition rep) {
        loadRecords();
        check(habit.getId());
        RepetitionRecord record = new RepetitionRecord();
        record.habit_id = habit.getId();
        record.copyFrom(rep);
        repository.save(record);
        rep.setId(record.getId());
        rep.setHabitLogs(modelFactory.buildLogList(record.getId()));//repetition id  which corresponds to Habit Log.
        list.add(rep);
        observable.notifyListeners();
    }

    @Override
    public List<Repetition> getByInterval(Timestamp timeFrom, Timestamp timeTo) {
        loadRecords();
        return list.getByInterval(timeFrom, timeTo);
    }

    @Override
    @Nullable
    public Repetition getByTimestamp(Timestamp timestamp) {
        loadRecords();
        return list.getByTimestamp(timestamp);
    }

    @Override
    public Repetition getOldest() {
        loadRecords();
        return list.getOldest();
    }

    @Override
    public Repetition getNewest() {
        loadRecords();
        return list.getNewest();
    }

    @Nullable
    @Override
    public Repetition getById(long id) {
        return list.getById(id);
    }

    @Override
    public void remove(@NonNull Repetition repetition) {
        loadRecords();
        list.remove(repetition);
        check(habit.getId());
        repository.execSQL(
                "delete from repetitions where habit = ? and timestamp = ?",
                habit.getId(), repetition.getTimestamp().getUnixTime());
        observable.notifyListeners();
    }

    public void removeAll() {
        loadRecords();
        list.removeAll();
        check(habit.getId());
        repository.execSQL("delete from repetitions where habit = ?",
                habit.getId());
    }

    @Override
    public long getTotalCount() {
        loadRecords();
        return list.getTotalCount();
    }

    public void reload() {
        loaded = false;
    }

    @Contract("null -> fail")
    private void check(Long value) {
        if (value == null) throw new RuntimeException("null check failed");
    }
}
