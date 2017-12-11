package org.mhabitx.uhabits.core.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.mhabitx.uhabits.core.utils.DateUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.annotation.concurrent.ThreadSafe;

/**
 * Created by Prateek on 07-12-2017.
 */
@ThreadSafe
public abstract class HabitLogList {
//    @NonNull
   // protected final Habit habit;
    protected final Long repetitionId;

    @NonNull
    protected final ModelObservable observable;

//    public HabitLogList(@NonNull Habit habit)
//    {
//        this.habit = habit;
//        this.observable = new ModelObservable();
//    }
    public HabitLogList(@NonNull Long repetitionId)
    {
        this.repetitionId = repetitionId;
        this.observable = new ModelObservable();
    }

    /**
     * Adds a repetition to the list.
     * <p>
     * Any implementation of this method must call observable.notifyListeners()
     * after the repetition has been added.
     *
     * @param habitLog the repetition to be added.
     */
    public abstract void add(HabitLog habitLog);

    /**
     * Returns true if the list contains a repetition that has the given
     * timestamp.
     *
     * @param timestamp the timestamp to find.
     * @return true if list contains repetition with given timestamp, false
     * otherwise.
     */
    public boolean containsTimestamp(Timestamp timestamp)
    {
        return (getByTimestamp(timestamp) != null);
    }

    /**
     * Returns the list of habitLogs that happened within the given time
     * interval.
     * <p>
     * The list is sorted by timestamp in increasing order. That is, the first
     * element corresponds to oldest timestamp, while the last element
     * corresponds to the newest. The endpoints of the interval are included.
     *
     * @param fromTimestamp timestamp of the beginning of the interval
     * @param toTimestamp   timestamp of the end of the interval
     * @return list of habitLog within given time interval
     */
    public abstract List<HabitLog> getByInterval(Timestamp fromTimestamp,
                                                   Timestamp toTimestamp);

    /**
     * Returns the HabitLog that has the given timestamp, or null if none
     * exists.
     *
     * @param timestamp the repetition timestamp.
     * @return the HabitLog that has the given timestamp.
     */
    @Nullable
    public abstract HabitLog getByTimestamp(Timestamp timestamp);

    @NonNull
    public ModelObservable getObservable()
    {
        return observable;
    }

    /**
     * Returns the oldest HabitLog in the list.
     * <p>
     * If the list is empty, returns null. HabitLogs in the future are
     * discarded.
     *
     * @return oldest HabitLog in the list, or null if list is empty.
     */
    @Nullable
    public abstract HabitLog getOldest();

    @Nullable
    /**
     * Returns the newest HabitLog in the list.
     * <p>
     * If the list is empty, returns null. HabitLogs in the past are
     * discarded.
     *
     * @return newest HabitLog in the list, or null if list is empty.
     */
    public abstract HabitLog getNewest();

//    /**
//     * Returns the total number of HabitLogs for each month, from the first
//     * HabitLogs until today, grouped by day of week.
//     * <p>
//     * The habitLogs are returned in a HashMap. The key is the timestamp for
//     * the first day of the month, at midnight (00:00). The value is an integer
//     * array with 7 entries. The first entry contains the total number of
//     * repetitions during the specified month that occurred on a Saturday. The
//     * second entry corresponds to Sunday, and so on. If there are no
//     * repetitions during a certain month, the value is null.
//     *
//     * @return total number of repetitions by month versus day of week
//     */
//    @NonNull
//    public HashMap<Timestamp, Integer[]> getWeekdayFrequency()
//    {
//        List<Repetition> reps =
//                getByInterval(Timestamp.ZERO, DateUtils.getToday());
//        HashMap<Timestamp, Integer[]> map = new HashMap<>();
//
//        for (Repetition r : reps)
//        {
//            Calendar date = r.getTimestamp().toCalendar();
//            int weekday = r.getTimestamp().getWeekday();
//            date.set(Calendar.DAY_OF_MONTH, 1);
//
//            Timestamp timestamp = new Timestamp(date.getTimeInMillis());
//            Integer[] list = map.get(timestamp);
//
//            if (list == null)
//            {
//                list = new Integer[7];
//                Arrays.fill(list, 0);
//                map.put(timestamp, list);
//            }
//
//            list[weekday]++;
//        }
//
//        return map;
//    }

    /**
     * Removes a given HabitLog from the list.
     * <p>
     * If the list does not contain the HabitLog, it is unchanged.
     * <p>
     * Any implementation of this method must call observable.notifyListeners()
     * after the HabitLog has been added.
     *
     * @param habitLog the habitLog to be removed
     */
    public abstract void remove(@NonNull HabitLog habitLog);

    /**
     * Adds HabitLog at a certain timestamp.
     * <p>
     * If there exists a repetition on the list with the given timestamp, the
     * method removes this repetition from the list and returns it. If there are
     * no repetitions with the given timestamp, creates and adds one to the
     * list, then returns it.
     *
     * @param timestamp the timestamp for the timestamp that should be added .
     * @return the HabitLog that has been added.
     */
    @NonNull
    public synchronized HabitLog makeEntry(Timestamp timestamp)
    {
        HabitLog log=new HabitLog(timestamp, Checkmark.CHECKED_EXPLICITLY);
        add(log);
//        if(habit.isNumerical())
//            throw new IllegalStateException("habit must NOT be numerical");
//
//        Repetition rep = getByTimestamp(timestamp);
//        if (rep != null) remove(rep);
//        else
//        {
//            rep = new Repetition(timestamp, Checkmark.CHECKED_EXPLICITLY);
//            add(rep);
//        }
//
//        habit.invalidateNewerThan(timestamp);
        return log;
    }

    /**
     * Returns the number of all repetitions
     *
     * @return number of all repetitions
     */
    @NonNull
    public abstract long getTotalCount();

//    public void toggle(Timestamp timestamp, int value)
//    {
//        Repetition rep = getByTimestamp(timestamp);
//        if(rep != null) remove(rep);
//        add(new Repetition(timestamp, value));
//        habit.invalidateNewerThan(timestamp);
//    }

    public abstract void removeAll();
}
