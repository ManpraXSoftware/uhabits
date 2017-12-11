/*
 * Copyright (C) 2016 Alinson Santos Xavier <mhabitx@gmail.com>
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
 */

package org.mhabitx.uhabits.core.models;

import android.support.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

import javax.inject.Inject;

import static org.mhabitx.uhabits.core.utils.StringUtils.defaultToStringStyle;

/**
 * Represents a record that the user has performed a certain habit at a certain
 * date.
 */
public final class Repetition {

    private final Timestamp timestamp;

    /**
     * The value of the repetition.
     * <p>
     * For boolean habits, this always equals Checkmark.CHECKED_EXPLICITLY.
     * For numerical habits, this number is stored in thousandths. That
     * is, if the user enters value 1.50 on the app, it is here stored as 1500.
     * For multiple habits, it should be number of logs each day perform
     */
    private final int value;
    /**
     * For multiple habits, this is what we want to achieve.
     */
    private int target;
    /**
     * For multiple habits, this is use to bound number of logs each day
     */
    private int limit ;
    /**
     * list of logs that dumps each day
     */
    private HabitLogList habitLogs;

    private Long id;
    /**
     * Creates a new repetition with given parameters.
     * <p>
     * The timestamp corresponds to the days this repetition occurred. Time of
     * day must be midnight (UTC).
     *
     * @param timestamp the time this repetition occurred.
     */
    public Repetition(Timestamp timestamp, int value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repetition that = (Repetition) o;

        return new EqualsBuilder()
                .append(timestamp, that.timestamp)
                .append(value, that.value)
                .isEquals();
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getValue() {
        return value;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public HabitLogList getHabitLogs() {
        return habitLogs;
    }

    public void setHabitLogs(HabitLogList habitLogs) {
        this.habitLogs = habitLogs;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(timestamp)
                .append(value)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, defaultToStringStyle())
                .append("timestamp", timestamp)
                .append("value", value)
                .append("target", target)
                .append("limit", target)
                .append("id", id)
                .toString();
    }
}
