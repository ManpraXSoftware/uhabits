package org.mhabitx.uhabits.core.models.memory;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mhabitx.uhabits.core.models.HabitLog;
import org.mhabitx.uhabits.core.models.HabitLogList;
import org.mhabitx.uhabits.core.models.Repetition;
import org.mhabitx.uhabits.core.models.Timestamp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Prateek on 08-12-2017.
 */

public class MemoryHabitLogList extends HabitLogList {
    ArrayList<HabitLog> list;

    public MemoryHabitLogList(@NonNull Long repetitionId) {
        super(repetitionId);
        list= new ArrayList<>();
    }

    @Override
    public void add(HabitLog habitLog) {
        list.add(habitLog);
        observable.notifyListeners();
    }

    @Override
    public List<HabitLog> getByInterval(Timestamp fromTimestamp, Timestamp toTimestamp) {
        ArrayList<HabitLog> filtered = new ArrayList<>();

        for (HabitLog r : list)
        {
            Timestamp t = r.getTimestamp();
            if (t.isOlderThan(fromTimestamp) || t.isNewerThan(toTimestamp)) continue;
            filtered.add(r);
        }

        Collections.sort(filtered,
                (r1, r2) -> r1.getTimestamp().compare(r2.getTimestamp()));

        return filtered;
    }

    @Nullable
    @Override
    public HabitLog getByTimestamp(Timestamp timestamp) {
        for (HabitLog l : list)
            if (l.getTimestamp().equals(timestamp)) return l;

        return null;
    }

    @Nullable
    @Override
    public HabitLog getOldest() {
        Timestamp oldestTimestamp = Timestamp.ZERO.plus(1000000);
        HabitLog oldestLog = null;

        for (HabitLog l: list)
        {
            if (l.getTimestamp().isOlderThan(oldestTimestamp))
            {
                oldestLog = l;
                oldestTimestamp = l.getTimestamp();
            }
        }

        return oldestLog;
    }

    @Nullable
    @Override
    public HabitLog getNewest() {
        Timestamp newestTimestamp = Timestamp.ZERO;
        HabitLog newestLog = null;

        for (HabitLog log : list)
        {
            if (log.getTimestamp().isNewerThan(newestTimestamp))
            {
                newestLog = log;
                newestTimestamp = log.getTimestamp();
            }
        }

        return newestLog;
    }

    @Override
    public void remove(@NonNull HabitLog habitLog) {
        list.remove(habitLog);
        observable.notifyListeners();
    }

    @NonNull
    @Override
    public long getTotalCount() {
        return list==null?0:list.size();
    }

    @Override
    public void removeAll() {
        list.clear();
        getObservable().notifyListeners();
    }
}
